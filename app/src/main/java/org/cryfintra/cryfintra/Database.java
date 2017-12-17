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
    private static final int DATABASE_VERSION = 4;
    private SQLiteDatabase db;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        open();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createQuery = "CREATE TABLE \"coin\" " +
                "( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " +
                "`name` TEXT NOT NULL, " +
                "`coinName` TEXT NOT NULL, " +
                "`fullName` TEXT, " +
                "`imgUrl` TEXT, " +
                "`sortOrder` INTEGER NOT NULL, " +
                "`BTC` REAL, " +
                "`USD` REAL, " +
                "`EUR` REAL, " +
                "`amount` REAL, " +
                "`lastUpdated` LONG NOT NULL );" +
                "CREATE TABLE \"graph\" " +
                "( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " +
                "`time` NUMERIC NOT NULL, " +
                "`close` REAL, " +
                "`high` REAL, " +
                "`low` REAL, " +
                "`open` REAL, " +
                "`volumefrom` REAL, " +
                "`volumeto` REAL, " +
                "`coin_id` INTEGER NOT NULL, " +
                "FOREIGN KEY(`coin_id`) REFERENCES `coin`(`id`) ON DELETE CASCADE )";
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
        ContentValues coinValues = new ContentValues();

        if(coin.name != null)
            coinValues.put("name", coin.name);

        if(coin.coinName != null)
            coinValues.put("coinName", coin.coinName );

        if(coin.fullName != null)
            coinValues.put("fullName", coin.fullName);

        if(coin.imgUrl != null)
            coinValues.put("imgUrl", coin.imgUrl);

        if(coin.sortOrder != null)
            coinValues.put("sortOrder", coin.sortOrder);

        if(coin.BTC != null)
            coinValues.put("BTC", coin.BTC);

        if(coin.USD != null)
            coinValues.put("USD", coin.USD);

        if(coin.EUR != null)
            coinValues.put("EUR", coin.EUR);

        if(coin.amount != null)
            coinValues.put("amount", coin.amount);

        coinValues.put("lastUpdated", getTimestamp());

        long id = db.insert("coin", null, coinValues);
        coin.id = id;


        Log.d("db", "coin inserted " + id + " " + coin.name);
    }

    /**
     * @param coin
     *
     * Update coin entry in database
     */
    public void updateCoin(Coin coin) {
        ContentValues coinValues = new ContentValues();


        if(coin.name != null)
            coinValues.put("name", coin.name);

        if(coin.coinName != null)
            coinValues.put("coinName", coin.coinName );

        if(coin.fullName != null)
            coinValues.put("fullName", coin.fullName);

        if(coin.imgUrl != null)
            coinValues.put("imgUrl", coin.imgUrl);

        if(coin.sortOrder != null)
            coinValues.put("sortOrder", coin.sortOrder);

        if(coin.BTC != null)
            coinValues.put("BTC", coin.BTC);

        if(coin.USD != null)
            coinValues.put("USD", coin.USD);

        if(coin.EUR != null)
            coinValues.put("EUR", coin.EUR);

        if(coin.amount != null)
            coinValues.put("amount", coin.amount);

        coinValues.put("lastUpdated", getTimestamp());

        db.update("coin", coinValues, "name=" + coin.name, null);
    }

    /**
     * @param coins
     *
     * Insert all coins in ArrayList
     */
    void bulkInsertCoins(ArrayList<Coin> coins) {
        for (Coin c:coins) {
            insertCoin(c);
        }
    }

    /**
     * @param coins
     *
     * Update all coins in ArrayList
     */
    void bulkUpdateCoins(ArrayList<Coin> coins) {
        for (Coin c:coins) {
            updateCoin(c);
        }
    }

    /**
     * @param coin
     *
     * Delete coin from database
     */
    public void deleteCoin(Coin coin) {
        db.delete("coin", "name=" + coin.name, null);
        coin.id = null;
    }

    /**
     * @param id
     *
     * Delete coin from database
     */
    public void deleteCoin(Long id) {
        db.delete("coin", "id=" + id, null);
    }

    /**
     * @param name
     * @return Cursor
     *
     * Get single coin with given name
     */
    public Cursor getOneCoin(String name) {
        return db.query("coin", null, "name=" + name, null, null, null, null);
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


    public long getTimestamp() {
        return System.nanoTime();
    }

}
