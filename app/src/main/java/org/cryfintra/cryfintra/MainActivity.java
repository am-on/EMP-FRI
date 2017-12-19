package org.cryfintra.cryfintra;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity  implements Button.OnClickListener {

    private CoinApi api;
    private Database db;

    private Button graphButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setContentView(R.layout.activity_graph);
        api = new CoinApi(getApplicationContext());
        db = new Database(getApplicationContext());

        // API Demonstration
        // new ApiExamples(api, getApplicationContext());

        // Database Demonstration
        // new DbExamples(api, db, getApplicationContext());

        graphButton = (Button)findViewById(R.id.graph_button);

        graphButton.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, GraphActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString("coin_name", "BTC");

        intent.putExtras(bundle);
        startActivity(intent);
    }



}
