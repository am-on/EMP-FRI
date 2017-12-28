package org.cryfintra.cryfintra;


import android.view.View;

public class OnClickAddCurrencyListener implements View.OnClickListener {
    AddCurrencyActivity aca;
    public OnClickAddCurrencyListener(AddCurrencyActivity aca) {
        this.aca = aca;
    }

    @Override
    public void onClick(View v) {
        this.aca.onBtnAddClicked();
    }
}
