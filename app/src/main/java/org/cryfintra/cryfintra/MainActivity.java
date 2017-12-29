package org.cryfintra.cryfintra;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;



public class MainActivity extends AppCompatActivity {

    public CoinApi api;
    public Database db;

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
        //ApiExamples apiExample = new ApiExamples(api, getApplicationContext());

        // Database Demonstration
        // new DbExamples(api, db, getApplicationContext());

    }

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

    /**
     * @param name Coin name
     *
     * Show a graph for given coin name
     */
    public void toGraph(String name){
        Intent intent = new Intent(this, GraphActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString("coin_name", name);

        intent.putExtras(bundle);
        startActivity(intent);
    }

}
