package org.cryfintra.cryfintra;


import android.os.Bundle;
import android.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;


public class ImportExportFragment extends Fragment {

    private MainActivity ma;
    Toast toast;


    public static ImportExportFragment newInstance() {

        return new ImportExportFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_import_export, container, false);

        // access to public variables from MainActivity class
        this.ma = (MainActivity) getActivity();

        Button btn_export = (Button) view.findViewById(R.id.btn_export);
        Button btn_import = (Button) view.findViewById(R.id.btn_import);


        // export
        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportClicked();
            }
        });

        // import
        btn_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importClicked();
            }
        });

        return view;
    }

    public void exportClicked() {
        try {
            if (this.ma.db.exportDatabase("/data/data/org.cryfintra.cryfintra/databases/exportedDatabase.sqlite")) {
                 toast = Toast.makeText(this.ma.getApplicationContext(),
                        "Database exported successfully.", Toast.LENGTH_SHORT);
                 toast.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 250);
                 toast.show();
            }
            else {
                toast = Toast.makeText(this.ma.getApplicationContext(),
                        "Failed to export database.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 250);
                toast.show();
            }

        } catch (IOException e) {
            toast = Toast.makeText(this.ma.getApplicationContext(),
                    "Failed to export database.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 250);
            toast.show();
            e.printStackTrace();
        }
    }

    public void importClicked() {
        try {
            if (this.ma.db.importDatabase("/data/data/org.cryfintra.cryfintra/databases/exportedDatabase.sqlite")) {
                toast = Toast.makeText(this.ma.getApplicationContext(),
                        "Database imported successfully.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 250);
                toast.show();
            }
        } catch (IOException e) {
            toast = Toast.makeText(this.ma.getApplicationContext(),
                    "Failed to import database.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, Gravity.CENTER_HORIZONTAL, 250);
            toast.show();
            e.printStackTrace();
        }
    }

}
