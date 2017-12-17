package org.cryfintra.cryfintra;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Class for Api demonstration
 */
public class ApiExamples {

    private CoinApi api;
    private Context ctx;

    public ApiExamples(CoinApi api, Context ctx) {
        this.api = api;
        this.ctx = ctx;

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
                useCoinList(coins);

            }
        });
    }

    /**
     * @param coins
     *
     * Loop through coin list and get exchange rates and graph for every coin
     */
    private void useCoinList(final ArrayList<Coin> coins) {
        for (final Coin c : coins) {
            // print coin info to log
            Log.d(c.toString(), "coin info");

            exchangeRates(c);
            graph(c);
        }
    }


    /**
     * @param c
     *
     * Demonstrate getting exchange rates
     */
    private void exchangeRates(final Coin c) {
        // update exchange rates
        c.updateExchangeRates(api, new CustomListener<Void>() {
            @Override
            public void getResult(Void object) {
                Log.d("got exchange rates", "in btc: " + c.BTC);
            }
        });

        // or get them as hash map
        api.getExchangeRates(c, new CustomListener<HashMap<String, Double>>() {
            @Override
            public void getResult(HashMap<String, Double> rates) {
                String out = "";
                for (String name : rates.keySet()) {
                    Double value = rates.get(name);
                    out += name + " " + value + ", ";
                }
                Log.d(out, "exchange rates");
            }
        });
    }

    /**
     * @param c
     *
     * Demonstrate getting graph data
     */
    private void graph(final Coin c) {
        // update data for graph
        c.updateGraphData(
                api,
                "EUR",
                60,
                new CustomListener<Void>() {
                    @Override
                    public void getResult(Void object) {
                        if (!c.graph.isEmpty())
                            Log.d("updated graph", "day 1: " + c.graph.get(0).toString());
                    }
                });

        // or directly get data for graph
        api.getGraph(c, "USD", 30, new CustomListener<ArrayList<Coin.GraphData>>() {
            @Override
            public void getResult(ArrayList<Coin.GraphData> graph) {
                c.graph = graph;
                if (!c.graph.isEmpty())
                    Log.d("got graph data", "day 1: " + graph.get(0).toString());
            }
        });


    }

}
