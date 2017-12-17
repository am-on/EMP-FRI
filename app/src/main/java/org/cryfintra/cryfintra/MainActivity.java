package org.cryfintra.cryfintra;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private CoinApi api;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        api = new CoinApi(getApplicationContext());
        db = new Database(getApplicationContext());


        // API Demonstration
        new ApiExamples(api, getApplicationContext());

        // Database Demonstration
        new DbExamples(api, db, getApplicationContext());
    }

}
