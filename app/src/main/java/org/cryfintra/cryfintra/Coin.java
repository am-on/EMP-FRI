package org.cryfintra.cryfintra;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Coin {

    public Long id, lastUpdated;
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

    public Coin(Cursor c) {
        String name = c.getString(c.getColumnIndex("name"));
        String coinName = c.getString(c.getColumnIndex("coinName"));
        String fullName = c.getString(c.getColumnIndex("fullName"));
        String imgUrl = c.getString(c.getColumnIndex("imgUrl"));
        Double amount = c.getDouble(c.getColumnIndex("amount"));
        Double BTC = c.getDouble(c.getColumnIndex("BTC"));
        Double USD = c.getDouble(c.getColumnIndex("USD"));
        Double EUR = c.getDouble(c.getColumnIndex("EUR"));
        Integer sortOrder = c.getInt(c.getColumnIndex("sortOrder"));
        Long lastUpdated = c.getLong(c.getColumnIndex("lastUpdated"));

        this.name = name;
        this.coinName = coinName;
        this.fullName = fullName;
        this.sortOrder = sortOrder;
        this.amount = amount;
        this.BTC = BTC;
        this.USD = USD;
        this.EUR = EUR;
        this.imgUrl = imgUrl;
        this.lastUpdated = lastUpdated;
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

    public boolean noExchangeRates() {
        return BTC == null || USD == null || EUR == null;
    }

    /**
     * @return ContentValues
     *
     * Export coin data to ContentValues, so it's prepared to insert into db
     */
    public ContentValues getContentValues() {
        ContentValues coinValues = new ContentValues();

        if(name != null)
            coinValues.put("name", name);

        if(coinName != null)
            coinValues.put("coinName", coinName );

        if(fullName != null)
            coinValues.put("fullName", fullName);

        if(imgUrl != null)
            coinValues.put("imgUrl", imgUrl);

        if(sortOrder != null)
            coinValues.put("sortOrder", sortOrder);

        if(BTC != null)
            coinValues.put("BTC", BTC);

        if(USD != null)
            coinValues.put("USD", USD);

        if(EUR != null)
            coinValues.put("EUR", EUR);

        if(amount != null)
            coinValues.put("amount", amount);

        lastUpdated = Database.getTimestamp();

        coinValues.put("lastUpdated", lastUpdated);

        return coinValues;
    }

    public boolean isRecent(Database db) {
        return lastUpdated != null && db.isRecent(60*5L, lastUpdated);
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
        Long time;
        Double close, high, low, open, volumefrom, volumeto;
        Coin coin;

        public GraphData() {

        }

        public GraphData(JSONObject data, Coin coin) {
            this.coin = coin;
            try {
                time = data.getLong("time") * 1000L;
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

        public GraphData(Cursor data, Coin coin) {
            this.coin = coin;
            Long time = data.getLong(data.getColumnIndex("time"));
            Double close = data.getDouble(data.getColumnIndex("close"));
            Double high = data.getDouble(data.getColumnIndex("high"));
            Double low = data.getDouble(data.getColumnIndex("close"));
            Double open = data.getDouble(data.getColumnIndex("close"));
            Double volumefrom = data.getDouble(data.getColumnIndex("close"));
            Double volumeto = data.getDouble(data.getColumnIndex("close"));

            this.time = time;
            this.close = close;
            this.high = high;
            this.low = low;
            this.open = open;
            this.volumefrom = volumefrom;
            this.volumeto = volumeto;

        }

        public Date getDate() {
            if(date == null)
                date = new Date(time);
            return date;
        }


        /**
         * @return ContentValues
         *
         * Export coin data to ContentValues, so it's prepared to insert into db
         */
        public ContentValues getContentValues() {
            ContentValues coinValues = new ContentValues();

            //time close, high, low, open, volumefrom, volumeto;

            if(time != null)
                coinValues.put("time", time);

            if(close != null)
                coinValues.put("close", close );

            if(high != null)
                coinValues.put("high", high);

            if(low != null)
                coinValues.put("low", low);

            if(open != null)
                coinValues.put("open", open);

            if(volumefrom != null)
                coinValues.put("volumefrom", volumefrom);

            if(volumeto != null)
                coinValues.put("volumeto", volumeto);

            if(coin.id != null)
                coinValues.put("coin_id", coin.id);

            coinValues.put("lastUpdated", Database.getTimestamp());

            return coinValues;
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

