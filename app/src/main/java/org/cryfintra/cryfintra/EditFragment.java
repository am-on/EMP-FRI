package org.cryfintra.cryfintra;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;


public class EditFragment extends Fragment {

    private MainActivity ma;


    public static EditFragment newInstance() {

        return new EditFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        FloatingActionButton addBtn = (FloatingActionButton) view.findViewById(R.id.floatingActionButton_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddCurrencyActivity.class);
                startActivity(intent);
            }
        });
        // access to public variables from MainActivity class
        this.ma = (MainActivity) getActivity();

        Cursor coinsRecord = ma.db.getOwnedCoins();

        ListView coinListView = (ListView) view.findViewById(R.id.listViewEditCurrencies);

        ListViewCoinCursorAdapter customAdapter = new ListViewCoinCursorAdapter(this.getActivity(), coinsRecord);
        coinListView.setAdapter(customAdapter);

        coinListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor coin = (Cursor) adapterView.getItemAtPosition(i);
                coin.moveToPosition(i);

                // TODO: Open AddCurrencyActivity with currency pre selected
            }
        });

        return view;
    }


}
