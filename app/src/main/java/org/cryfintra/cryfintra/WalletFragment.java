package org.cryfintra.cryfintra;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;



public class WalletFragment extends Fragment {

    public static WalletFragment newInstance() {

        return new WalletFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        // Testing the listView
        final String[] itemname = {
                "Safari",
                "Camera",
                "Global",
                "FireFox",
                "UC Browser",
                "Android Folder",
                "VLC Player",
                "Cold War"
        };
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, itemname);
        ListView list = (ListView) view.findViewById(R.id.listViewCurrencies);
        list.setAdapter(myAdapter);
        return view;
        //return inflater.inflate(R.layout.fragment_wallet, container, false);
    }

}
