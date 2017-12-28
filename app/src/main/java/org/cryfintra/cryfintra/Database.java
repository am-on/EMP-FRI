package org.cryfintra.cryfintra;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "coins";
    private static final int DATABASE_VERSION = 14;
    private SQLiteDatabase db;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        open();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createQuery = "CREATE TABLE \"coin\" " +
                "( `_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " +
                "`name` TEXT NOT NULL, " +
                "`coinName` TEXT NOT NULL, " +
                "`fullName` TEXT, " +
                "`imgUrl` TEXT, " +
                "`sortOrder` INTEGER NOT NULL, " +
                "`BTC` REAL, " +
                "`USD` REAL, " +
                "`EUR` REAL, " +
                "`amount` REAL, " +
                "`lastUpdated` LONG NOT NULL );";
        db.execSQL(createQuery);


        createQuery = "CREATE TABLE \"graph\" " +
                "( `_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " +
                "`time` LONG NOT NULL, " +
                "`close` REAL, " +
                "`high` REAL, " +
                "`low` REAL, " +
                "`open` REAL, " +
                "`volumefrom` REAL, " +
                "`volumeto` REAL, " +
                "`coin_id` INTEGER NOT NULL, " +
                "`lastUpdated` LONG NOT NULL, " +
                "FOREIGN KEY(`coin_id`) REFERENCES `coin`(`_id`) ON DELETE CASCADE );";
        db.execSQL(createQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + "coin");
        db.execSQL("DROP TABLE IF EXISTS " + "graph");

        onCreate(db);
    }


    public void open() throws SQLException {
        db = this.getWritableDatabase();
    }

    public void close() {
        if (db != null)
            db.close();
    }

    /**
     * @param coin
     *
     * Insert coin to database and save given id
     */
    public void insertCoin(Coin coin) {
        ContentValues coinValues = coin.getContentValues();

        long id = db.insert("coin", null, coinValues);
        coin.id = id;

        Log.d("db", "coin inserted " + id + " " + coin.name);
    }

    /**
     * @param coin
     *
     * Insert or update coin
     */
    public void insertOrUpdateCoin(Coin coin) {
        Cursor c = getOneCoin(coin.name);
        if (c.getCount() > 0) {
            c.moveToFirst();
            coin.id = (long)c.getInt(c.getColumnIndex("_id"));
            updateCoin(coin);

            return;
        }
        insertCoin(coin);
    }



    /**
     * @param coin
     *
     * Update coin entry in database
     */
    public void updateCoin(Coin coin) {
        ContentValues coinValues = coin.getContentValues();

        db.update("coin", coinValues, "name= \'" + coin.name + "\'", null);
    }

    /**
     * @param coins
     *
     * Insert all coins in ArrayList
     */
    void bulkInsertCoins(ArrayList<Coin> coins) {
        db.beginTransaction();
        try {
            for (int i = 0; i < coins.size(); i++) {
                ContentValues coinValues = coins.get(i).getContentValues();
                db.insert("coin", null, coinValues);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * @param coins
     *
     * Update all coins in ArrayList
     */
    void bulkUpdateCoins(ArrayList<Coin> coins) {
        db.beginTransaction();
        try {
            for (Coin coin:coins) {
                ContentValues coinValues = coin.getContentValues();
                db.update("coin", coinValues, "name= \'" + coin.name + "\'", null);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * @param graph
     *
     * Insert graph to db
     */
    void insertGraph(ArrayList<Coin.GraphData> graph) {

        db.beginTransaction();
        try {
            for (Coin.GraphData g:graph) {
                if(g.coin.id == null) {
                    insertOrUpdateCoin(g.coin);
                }
                ContentValues graphValues = g.getContentValues();
                db.insert("graph", null, graphValues);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    /**
     * @param coin
     *
     * Delete coin from database
     */
    public void deleteCoin(Coin coin) {
        db.delete("coin", "name= \'" + coin.name + "\'", null);
        coin.id = null;
    }

    /**
     * @param coin
     *
     * Delete graph from database
     */
    public void deleteGraph(Coin coin) {
        db.delete("graph", "coin_id= \'" + coin.id + "\'", null);
    }

    /**
     * @param id
     *
     * Delete coin from database
     */
    public void deleteCoin(Long id) {
        db.delete("coin", "_id=" + id, null);
    }

    /**
     * @param name
     * @return Cursor
     *
     * Get single coin with given name
     */
    public Cursor getOneCoin(String name) {
        return db.query("coin", null, "name= \'" + name + "\'", null, null, null, null);
    }

    /**
     * @return Cursor
     *
     * Get all coins with amount
     */
    public Cursor getOwnedCoins() {
        return db.query("coin", null, "amount IS NOT NULL", null, null, null, null);
    }

    /**
     * @return Cursor
     *
     * Get all coins without amount
     */
    public Cursor getUnownedCoins() {
        return db.query("coin", null, "amount IS NULL", null, null, null, null);
    }


    public static long getTimestamp() {
        return System.nanoTime();
    }

    /**
     * @param coin_name
     * @return
     *
     * Check if graph data in db isn't older than half a day
     */
    public boolean isGraphRecent(String coin_name) {
        Cursor c = db.rawQuery(
                "SELECT graph.lastUpdated FROM graph INNER JOIN coin " +
                    "ON (coin._id = graph.coin_id) WHERE coin.name LIKE " +
                    " \'" + coin_name + "\'",
                    null
                );

        if (c.getCount() > 0){
            c.moveToFirst();
            Long timestamp = c.getLong(c.getColumnIndex("lastUpdated"));
            Log.d("graph", "inside");
            return isRecent(12*60*60L, timestamp);
        }

        return false;
    }

    public Cursor getGraph(String coin_name) {
        Cursor c = db.rawQuery(
                "SELECT * FROM graph INNER JOIN coin " +
                    "ON (coin._id = graph.coin_id) WHERE coin.name LIKE " +
                    " \'" + coin_name + "\' ORDER BY time ASC",
                    null
        );

        return c;
    }

    /**
     * @param maxTimeDiff max timestamp age in seconds
     * @param timestamp
     * @return
     *
     * Check if given timestamp isn't older than maxTimeDiff
     */
    public boolean isRecent(Long maxTimeDiff, Long timestamp) {
        Long diff = getTimestamp() - timestamp;
        return maxTimeDiff > diff / (1000000000L);
    }

}
