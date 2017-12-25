package org.cryfintra.cryfintra;

import android.app.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class GraphActivity extends Activity implements Runnable{

    private CoinApi api;
    private Database db;
    private String coinName;
    private Thread thread;
    private Coin coin;
    private boolean noData = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // hide content until it's ready
        findViewById(R.id.data).setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        coinName = intent.getExtras().getString("coin_name", "BTC");

        api = new CoinApi(getApplicationContext());
        db = new Database(getApplicationContext());

        getCoin(coinName);

        if(coin != null) {
            this.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            if (!noData)
                findViewById(R.id.data).setVisibility(View.VISIBLE);
        } else {
            Log.d("graph_view", "coin is null");
            noData = true;
        }

        if(noData) {
            findViewById(R.id.data).setVisibility(View.INVISIBLE);
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            findViewById(R.id.noDataTextView).setVisibility(View.VISIBLE);
        }

    }

    /**
     * @param coinName
     *
     * Get coin from db
     */
    private void getCoin(String coinName) {
        Cursor c = db.getOneCoin(coinName);
        if (c.getCount() > 0) {
            c.moveToFirst();
            coin = new Coin(c);
        }
    }

    /**
     * @param coin
     *
     * Make sure exchange data is recent, then display it
     */
    private void exchangeData(final Coin coin) {

        if(
            coin.isRecent(db) && !coin.noExchangeRates() ||
            !coin.noExchangeRates() && !CoinApi.isApiAvailable()
            ) {
            displayExchangeData(coin);
        } else if(CoinApi.isApiAvailable()) {
            api.getExchangeRates(coin, new CustomListener<HashMap<String, Double>>() {
                @Override
                public void getResult(HashMap<String, Double> rates) {
                    coin.setExchangeRates(rates);
                    db.updateCoin(coin);
                    displayExchangeData(coin);
                }
            });
        } else {
            noData = true;
        }
    }

    /**
     * @param coin
     *
     * Display exchange data
     */
    private void displayExchangeData(Coin coin) {
        TextView btc = (TextView) findViewById(R.id.exchangesBTC);
        TextView usd = (TextView) findViewById(R.id.exchangesUSD);
        TextView eur = (TextView) findViewById(R.id.exchangesEUR);

        btc.setText(String.format("%s %.10f", btc.getText(), coin.BTC));
        usd.setText( String.format("%s %.10f", usd.getText(), coin.USD));
        eur.setText( String.format("%s %.10f", eur.getText(), coin.EUR));
    }

    /**
     * @param coin
     *
     * Make sure graph data is recent, then display graph
     */
    private void graph(final Coin coin) {

        if (db.isGraphRecent(coinName) || !CoinApi.isApiAvailable()) {
            ArrayList<Coin.GraphData> ga = new ArrayList<>();
            Cursor graph = db.getGraph(coinName);

            if(graph.getCount() > 0) {
                while(graph.moveToNext()) {
                    Coin.GraphData gd = new Coin.GraphData(graph, coin);
                    ga.add(gd);
                }
                coin.graph = ga;
                drawGraph(coin);
            } else {
                Log.d("no graph", "no data in db");
                noData = true;
                return;
            }

        } else {
            db.deleteGraph(coin);
            coin.updateGraphData(api, "USD", 60, new CustomListener<Void>() {
                @Override
                public void getResult(Void object) {
                    drawGraph(coin);
                    db.insertGraph(coin.graph);
                }
            });
        }

    }

    /**
     * @param coin
     *
     * Draw graph
     */
    private void drawGraph(Coin coin) {
        GraphView graph = (GraphView) findViewById(R.id.graph);

        ArrayList<DataPoint> points = new ArrayList<>();

        graph.setTitle(coin.fullName);

        for (int i = 0; i < coin.graph.size(); i++) {
            Coin.GraphData e = coin.graph.get(i);
            points.add(new DataPoint(e.time, e.open));
        }

        DataPoint[] dp = points.toArray(new DataPoint[0]);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dp);

        graph.addSeries(series);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    Date date = new Date((long)value);
                    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd. MMM");
                    return  DATE_FORMAT.format(date);
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + " $";
                }
            }
        });

        GridLabelRenderer glr = graph.getGridLabelRenderer();
        glr.setHorizontalLabelsAngle(90);
        glr.setLabelHorizontalHeight(200);
        glr.setNumHorizontalLabels(12);
        glr.setGridStyle( GridLabelRenderer.GridStyle.NONE);
        glr.setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);

        graph.getViewport().setXAxisBoundsManual(true);
        double range = graph.getViewport().getMaxX(true) - graph.getViewport().getMinX(true);
        graph.getViewport().setMinX(graph.getViewport().getMinX(true) + range * 0.5);
        graph.getViewport().setMaxX(graph.getViewport().getMaxX(true));
        graph.getViewport().setScalable(true);

    }

    @Override
    public void run() {
        graph(coin);
        exchangeData(coin);
    }

    public void start() {
        if (thread != null) return;
        thread = new Thread(this);
        thread.start();
    }


}
