package org.cryfintra.cryfintra;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;



public class MainActivity extends AppCompatActivity implements Button.OnClickListener {

    private CoinApi api;
    private Database db;

    private Button graphButton;
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBottomNavigation();

        if (savedInstanceState == null) {
            loadWalletFragment();
        }


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

//    private TextView mTextMessage;
//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_wallet:
//                    mTextMessage.setText(R.string.title_wallet);
//                    return true;
//                case R.id.navigation_edit_funds:
//                    mTextMessage.setText(R.string.title_edit_funds);
//                    return true;
//                case R.id.navigation_import_export:
//
//                    return true;
//            }
//            return false;
//        }
//    };

    private void setupBottomNavigation() {
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.navigation_wallet:
                                loadWalletFragment();
                                return true;
                            case R.id.navigation_edit_funds:
                                loadEditFundsFragment();
                                return true;
                            case R.id.navigation_import_export:
                                loadImportExportFragment();
                                return true;
                        }
                        return false;
                    }
                });
    }

    private void loadWalletFragment() {
        WalletFragment fragment = WalletFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, fragment);
        ft.commit();
        setTitle("Wallet");
    }

    private void loadEditFundsFragment() {
        EditFragment fragment = EditFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, fragment);
        ft.commit();
        setTitle("Edit Funds");
    }

    private void loadImportExportFragment() {
        ImportExportFragment fragment = ImportExportFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, fragment);
        ft.commit();
        setTitle("Import/Export Data");
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
