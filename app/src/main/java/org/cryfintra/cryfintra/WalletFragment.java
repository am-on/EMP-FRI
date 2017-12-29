package org.cryfintra.cryfintra;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Locale;


public class WalletFragment extends Fragment {

    private MainActivity ma;
    private double sumBTC;
    private double sumEUR;

    public static WalletFragment newInstance() {
        return new WalletFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        // access to public variables from MainActivity class
        this.ma = (MainActivity) getActivity();

        ma.db.updateCoins(ma.api);
        Cursor coinsRecord = ma.db.getOwnedCoins();

        ListView coinListView = (ListView) view.findViewById(R.id.listViewCurrencies);

        ListViewCoinCursorAdapter customAdapter = new ListViewCoinCursorAdapter(this.getActivity(), coinsRecord);
        coinListView.setAdapter(customAdapter);

        coinListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor coin = (Cursor) adapterView.getItemAtPosition(i);
                coin.moveToPosition(i);

                String name = coin.getString(coin.getColumnIndex("name"));

                ma.toGraph(name);
            }
        });

        // TODO: we need to sum all owned coins and display their value in BTC and EUR

        TextView btcWorth = (TextView) view.findViewById(R.id.textViewBtcWorth);
        TextView eurWorth = (TextView) view.findViewById(R.id.textViewEuroWorth);

        coinsRecord.moveToPosition(-1);
        sumBTC = 0;
        sumEUR = 0;

        while(coinsRecord.moveToNext()) {
            Coin currentCoin = new Coin(coinsRecord);
            sumBTC = sumBTC + currentCoin.amount * currentCoin.BTC;
            sumEUR = sumEUR + currentCoin.amount * currentCoin.EUR;
        }

        btcWorth.setText(String.format(Locale.getDefault(),"%f BTC", sumBTC));
        eurWorth.setText(String.format(Locale.getDefault(), "%.2f €", sumEUR));


        return view;
    }


}
