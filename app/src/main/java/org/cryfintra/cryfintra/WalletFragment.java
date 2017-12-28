package org.cryfintra.cryfintra;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;



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

        Cursor coinsRecord = ma.db.getOwnedCoins();

        ListView coinListView = (ListView) view.findViewById(R.id.listViewCurrencies);

        ListViewCoinCursorAdapter customAdapter = new ListViewCoinCursorAdapter(this.getActivity(), coinsRecord);
        coinListView.setAdapter(customAdapter);

        coinListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO: Show clicked currency graph (open GraphActivity with appropriate currency parameter)
            }
        });

        return view;
    }


}
