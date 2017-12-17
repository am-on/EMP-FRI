package org.cryfintra.cryfintra;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class CoinApi {

    public static RequestQueueSingleton requestQueue;

    /**
     * @param ctx Application context - getApplicationContext()
     */
    public CoinApi(Context ctx) {
        requestQueue = RequestQueueSingleton.getInstance(ctx);
    }

    /**
     * @param topN get first "topN" coins sorted by popularity
     * @param listener CustomListener, create new instance and override getResult method
     *
     * Get coin list from cryptocompare as ArrayList<Coin>.
     * Results are returned to given CustomListener.getResults().
     */
    public void getCoinList(final int topN, final CustomListener<ArrayList<Coin>> listener) {

        String url = "https://min-api.cryptocompare.com/data/all/coinlist";

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Process the JSON
                try {
                    ArrayList<Coin> coins = new ArrayList<>();
                    JSONObject data = response.getJSONObject("Data");
                    Iterator<String> keys = data.keys();
                    while (keys.hasNext()) {
                        JSONObject coinData = data.getJSONObject(keys.next());
                        Coin c = new Coin(coinData);
                        if (c.sortOrder <= topN) {
                            coins.add(c);
                        }

                    }

                    listener.getResult(coins);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("REST error", error.getMessage());
            }
        };


        // Initialize a new JsonObjectRequest instance
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                responseListener,
                errorListener
        );

        requestQueue.addToRequestQueue(jsonObjectRequest);
    }

    public void getExchangeRates(Coin coin, final CustomListener<HashMap<String, Double>> listener) {
        String url = String.format(
                "https://min-api.cryptocompare.com/data/price?fsym=%s&tsyms=%s,%s,%s",
                coin.name,
                "BTC",
                "USD",
                "EUR"
        );


        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Process the JSON
                try {
                    HashMap<String, Double> rates = new HashMap<>();
                    Iterator<String> keys = response.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        Double value = response.getDouble(key);

                        if(key.equals("Response") && value.equals("Error")) {
                            rates.put("BTC", 0.0);
                            rates.put("USD", 0.0);
                            rates.put("EUR", 0.0);
                            break;
                        }

                        rates.put(key, value);
                    }

                    listener.getResult(rates);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("REST error", error.getMessage());
            }
        };


        // Initialize a new JsonObjectRequest instance
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                responseListener,
                errorListener
        );

        requestQueue.addToRequestQueue(jsonObjectRequest);
    }


    public void getGraph(
            final Coin coin,
            String currency,
            int limit,
            final CustomListener<ArrayList<Coin.GraphData>> listener) {

        String url = String.format(
                "https://min-api.cryptocompare.com/data/histoday?fsym=%s&tsym=%s&limit=%d",
                coin.name,
                currency.toUpperCase(),
                limit
        );


        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Process the JSON
                try {
                    ArrayList<Coin.GraphData> data = new ArrayList<>();

                    JSONArray jsonArray = response.getJSONArray("Data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Coin.GraphData d = new Coin.GraphData(obj);
                        data.add(d);
                    }

                    listener.getResult(data);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("REST error", error.getMessage());
            }
        };


        // Initialize a new JsonObjectRequest instance
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                responseListener,
                errorListener
        );

        requestQueue.addToRequestQueue(jsonObjectRequest);
    }

}
