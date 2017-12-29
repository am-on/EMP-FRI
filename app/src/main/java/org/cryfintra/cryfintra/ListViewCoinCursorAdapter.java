package org.cryfintra.cryfintra;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;


public class ListViewCoinCursorAdapter extends CursorAdapter {
    private LayoutInflater cursorInflater;

    public ListViewCoinCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        cursorInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void bindView(View v, Context context, Cursor coinCursor) {
        if (coinCursor != null) {
            ImageView icon = (ImageView) v.findViewById(R.id.adapter_icon);
            TextView coinAmountAndCurrency = (TextView) v.findViewById(R.id.adapter_coin);
            TextView coinValueAndCurrency = (TextView) v.findViewById(R.id.adapter_value);

            if (icon != null) {
                String imgUrl = coinCursor.getString(coinCursor.getColumnIndex("imgUrl"));
                Picasso.with(context).load(imgUrl).resize(120, 120).into(icon);
            }

            if (coinAmountAndCurrency != null) {
                String amount = coinCursor.getString(coinCursor.getColumnIndex("amount"));
                String coinName = coinCursor.getString(coinCursor.getColumnIndex("coinName"));

                coinAmountAndCurrency.setText(String.format(Locale.getDefault(), "%s %s", amount, coinName));
            }

            if (coinValueAndCurrency != null && coinAmountAndCurrency != null) {
                double cryptoToEuroConversionFactor = coinCursor.getDouble(coinCursor.getColumnIndex("EUR"));
                double amountInCrypto = coinCursor.getDouble(coinCursor.getColumnIndex("amount"));
                double calculatedValue = amountInCrypto * cryptoToEuroConversionFactor;

                String valueInEUR = String.format(Locale.getDefault(), "%,.2f EUR", calculatedValue);
                coinValueAndCurrency.setText(valueInEUR);
            }
        }
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // R.layout.list_row is your xml layout for each row
        return cursorInflater.inflate(R.layout.coin_item_list_row, parent, false);
    }
}