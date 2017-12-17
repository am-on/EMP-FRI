package org.cryfintra.cryfintra;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Coin {

    public Long id;
    public String name, coinName, fullName, imgUrl;
    public Integer sortOrder;
    public ArrayList<GraphData> graph;

    // exchange rates
    public Double BTC, USD, EUR, amount;

    public Coin(String name, String coinName, String fullName, String imgUrl, Integer sortOrder) {
        this.name = name;
        this.coinName = coinName;
        this.fullName = fullName;
        this.sortOrder = sortOrder;
        setImgUrl(imgUrl);
    }

    /**
     * @param imgUrl partial image url (example: "/media/19972/ripple.png")
     *
     * Create full image url and save it to imgUrl
     *
     */
    private void setImgUrl(String imgUrl) {
        this.imgUrl = "https://www.cryptocompare.com" + imgUrl;
    }

    /**
     * @param coin JSONObject with single coin data
     *
     * Extract data about coin from json and save it
     *
     * Example data:
     *
     *       "Id": "5031",
     *       "Url": "/coins/xrp/overview",
     *       "ImageUrl": "/media/19972/ripple.png",
     *       "Name": "XRP",
     *       "Symbol": "XRP",
     *       "CoinName": "Ripple",
     *       "FullName": "Ripple (XRP)",
     *       "Algorithm": "N/A",
     *       "ProofType": "N/A",
     *       "FullyPremined": "1",
     *       "TotalCoinSupply": "38305873865",
     *       "PreMinedValue": "N/A",
     *       "TotalCoinsFreeFloat": "N/A",
     *       "SortOrder": "12",
     *       "Sponsored": false
     *
     */
    public Coin(JSONObject coin) {
        try {
            this.name = coin.getString("Name");
            this.coinName = coin.getString("CoinName");
            this.fullName = coin.getString("FullName");
            this.sortOrder = coin.getInt("SortOrder");

            setImgUrl(coin.getString("ImageUrl"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateExchangeRates(CoinApi api, final CustomListener<Void> listener) {
        api.getExchangeRates(this, new CustomListener<HashMap<String, Double>>() {
            @Override
            public void getResult(HashMap<String, Double> rates) {
                setExchangeRates(rates);
                listener.getResult(null);
            }
        });
    }

    public void updateGraphData(
            CoinApi api,
            String currency,
            int limit,
            final CustomListener<Void> listener) {

        api.getGraph(
                this,
                currency,
                limit,
                new CustomListener<ArrayList<GraphData>>() {
                    @Override
                    public void getResult(ArrayList<GraphData> data) {
                        graph = data;
                        listener.getResult(null);
                    }
                });
    }


    public void setExchangeRates(HashMap<String, Double> rates) {
        BTC = rates.get("BTC");
        USD = rates.get("USD");
        EUR = rates.get("EUR");
    }

    /**
     * Class for storing chart data
     *
     * Example data:
     *
     *     "time": 1482364800,
     *     "close": 891.07,
     *     "high": 920.49,
     *     "low": 828.87,
     *     "open": 829.21,
     *     "volumefrom": 218811.74,
     *     "volumeto": 193891331.09
     */
    static class GraphData {

        Date date;
        long time;
        double close, high, low, open, volumefrom, volumeto;

        public GraphData() {

        }

        public GraphData(JSONObject data) {
            try {
                time = data.getLong("time");
                close = data.getDouble("close");
                high = data.getDouble("high");
                low = data.getDouble("low");
                open = data.getDouble("open");
                volumefrom = data.getDouble("volumefrom");
                volumeto = data.getDouble("volumeto");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public Date getDate() {
            if(date == null)
                date = new Date(time);
            return date;
        }

        @Override
        public String toString() {
            return getDate() + " open:" + open;
        }
    }


    @Override
    public String toString() {
        return name + " - " + fullName;
    }
}

