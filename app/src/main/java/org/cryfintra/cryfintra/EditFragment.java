package org.cryfintra.cryfintra;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = this.getView();
        FloatingActionButton addBtn = (FloatingActionButton) view.findViewById(R.id.floatingActionButton_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddCurrencyActivity.class);
                Bundle bundle_add = new Bundle();
                bundle_add.putString("mode", "add");
                intent.putExtras(bundle_add);
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
                String name = coin.getString(coin.getColumnIndex("name"));
                String fullName = coin.getString(coin.getColumnIndex("fullName"));

                Intent intent = new Intent(getActivity(), AddCurrencyActivity.class);
                Bundle bundle_edit = new Bundle();
                bundle_edit.putString("mode", "edit");
                bundle_edit.putString("coin", name);
                bundle_edit.putString("fullName", fullName);
                intent.putExtras(bundle_edit);
                startActivity(intent);
            }
        });
    }
/*
    @Override
    public void onResume() {
        super.onResume();
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
    */
}
