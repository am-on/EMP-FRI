package org.cryfintra.cryfintra;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class AddCurrencyActivity extends AppCompatActivity  {

    private Database db;

    private List<String> arrayCurrencySpinner;
    public ArrayList<Coin> retrievedCoinList;
    private Spinner s;
    private EditText currencyAmountInput;

    private String mode, name, fullName;

    public void onBtnAddClicked() {

        final Coin selectedCoin;

        if (mode.equals("add")) {
            selectedCoin = this.retrievedCoinList.get(this.s.getSelectedItemPosition());
        }
        else {
            // mode == edit
            Cursor coinCursor = this.db.getOneCoin(name);
            selectedCoin = new Coin(coinCursor);
        }
        selectedCoin.amount = Double.valueOf(this.currencyAmountInput.getText().toString());
        CoinApi api = new CoinApi(getApplicationContext());
        selectedCoin.updateExchangeRates(api, new CustomListener<Void>() {
            @Override
            public void getResult(Void object) {
                db.insertOrUpdateCoin(selectedCoin);
                finish();
            }
        });

    }

    private void updateActivityDialog(final ArrayList<Coin> coins) {
        this.retrievedCoinList = coins;

        this.arrayCurrencySpinner = new ArrayList<String>();
        for (final Coin c : this.retrievedCoinList) {
            this.arrayCurrencySpinner.add(c.fullName);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.addAll(this.arrayCurrencySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // adapter for dropdown menu
        this.s = (Spinner) findViewById(R.id.spinnerCurrency);
        this.s.setAdapter(adapter);

        this.currencyAmountInput = (EditText) findViewById(R.id.editText_decimalAmount);

        Button btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button btnAdd = (Button) findViewById(R.id.btn_add);
        if (mode.equals("edit")) {
            btnAdd.setText("Save");
            TextView editCurrency = (TextView) findViewById(R.id.textView_editCurrency);
            editCurrency.setText(fullName);
            editCurrency.setVisibility(View.VISIBLE);
        }
        // see custom listener class
        btnAdd.setOnClickListener(new OnClickAddCurrencyListener(this));

        findViewById(R.id.progressBar_waiting).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_cancel).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_add).setVisibility(View.VISIBLE);
        findViewById(R.id.textView_currency).setVisibility(View.VISIBLE);
        findViewById(R.id.textView_Amount).setVisibility(View.VISIBLE);
        if (mode.equals("add")) {
            findViewById(R.id.spinnerCurrency).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.editText_decimalAmount).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_currency);

        db = new Database(getApplicationContext());

        Intent intent = getIntent();
        mode = intent.getExtras().getString("mode", "add");
        name = intent.getExtras().getString("coin", "BTC");
        fullName = intent.getExtras().getString("fullName", "Bitcoin (BTC)");
        if (mode.equals("edit")) {
            setTitle("Edit Crypto Currency");
        }

        // Show loading until you get list of coins from API
        findViewById(R.id.btn_cancel).setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_add).setVisibility(View.INVISIBLE);
        findViewById(R.id.textView_currency).setVisibility(View.INVISIBLE);
        findViewById(R.id.textView_editCurrency).setVisibility(View.INVISIBLE);
        findViewById(R.id.textView_Amount).setVisibility(View.INVISIBLE);
        findViewById(R.id.spinnerCurrency).setVisibility(View.INVISIBLE);
        findViewById(R.id.editText_decimalAmount).setVisibility(View.INVISIBLE);


        CoinApi api = new CoinApi(getApplicationContext());
        api.getCoinList(40, new CustomListener<ArrayList<Coin>>() {
            @Override
            public void getResult(ArrayList<Coin> coins) {
                updateActivityDialog(coins);
            }
        });
    }
}
