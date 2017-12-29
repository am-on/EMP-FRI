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


public class WalletFragment extends Fragment {

    private MainActivity ma;

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


        return view;
    }


}
