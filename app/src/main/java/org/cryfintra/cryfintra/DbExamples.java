package org.cryfintra.cryfintra;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;


/**
 * Class for usage demonstration of Database functions
 */
public class DbExamples {

    private CoinApi api;
    private Context ctx;
    private Database db;


    public DbExamples(CoinApi api, Database db, Context ctx) {
        this.api = api;
        this.ctx = ctx;
        this.db = db;


        if (db.isGraphRecent("BTC"))
            Log.d("graph", "graph = recent");
        else
            Log.d("graph", "graph != recent");

        getCoinList();


    }

    /**
     * Get coin list from api and send result to other function
     */
    private void getCoinList() {
        // Get top 40 coins
        api.getCoinList(40, new CustomListener<ArrayList<Coin>>() {
            @Override
            public void getResult(ArrayList<Coin> coins) {

                // Do stuff with array list
               insertToDb(coins);

            }
        });
    }

    /**
     * @param coins
     *
     * Insert coins to db
     */
    private void insertToDb(ArrayList<Coin> coins) {

        // Set amount to some coins - set them as owned
        for (int i = 0; i < coins.size(); i++) {
            Coin c = coins.get(i);
            switch (c.name) {
                case "BTC": c.amount = 2.1; save_graph(c); break;
                case "LTC": c.amount = 0.1; save_graph(c); break;
                case "XMR": c.amount = 54.021; save_graph(c); break;
                case "ZEC": c.amount = 4.0; save_graph(c); break;
            }

        }


        // insert coins
        db.bulkInsertCoins(coins);

        // get owned coins
        getMyCoins();

        // get unowned coins
        getUnownedCoins();
    }

    /**
     * Get coins with set amount
     */
    private void getMyCoins() {
        Database db = new Database(ctx);
        Cursor c = db.getOwnedCoins();

        if (c != null ) {
            if  (c.moveToFirst()) {
                do {
                    String name = c.getString(c.getColumnIndex("name"));
                    Double amount = c.getDouble(c.getColumnIndex("amount"));
                    Log.d("db select my", name + " " + amount);
                } while (c.moveToNext());
            }
        }
        c.close();

    }

    /**
     * Get coins with null amount
     */
    private void getUnownedCoins() {
        Database db = new Database(ctx);
        Cursor c = db.getUnownedCoins();

        if (c != null ) {
            if  (c.moveToFirst()) {
                do {
                    String name = c.getString(c.getColumnIndex("name"));
                    Log.d("db select oth", name );
                } while (c.moveToNext());
            }
        }
        c.close();

    }


    /**
     * @param c
     *
     * Save graph data to db
     */
    private void save_graph(final Coin c) {

        // or directly get data for graph
        api.getGraph(c, "USD", 30, new CustomListener<ArrayList<Coin.GraphData>>() {
            @Override
            public void getResult(ArrayList<Coin.GraphData> graph) {
                c.graph = graph;
                if (!c.graph.isEmpty())
                    db.insertGraph(graph);

                Log.d("graph to db", "inserted graph to db");
            }
        });


    }
}
