package com.r4sh33d.currencyconverter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class ConversionActivity extends AppCompatActivity {
    EditText editTextBtc, editTextBaseCurrency, editTextEth;
    TextView countryName, baseCurrencyShortCode;
    Currency currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);
        editTextBtc = (EditText) findViewById(R.id.editTextBtc);
        editTextEth = (EditText) findViewById(R.id.editTextETH);
        editTextBaseCurrency = (EditText) findViewById(R.id.editTextBaseCurrency);
        countryName = (TextView) findViewById(R.id.countryName);
        baseCurrencyShortCode = (TextView) findViewById(R.id.baseCurrencyShortCode);
        currency = getIntent().getParcelableExtra(Utils.CURRENCY_INTENT_KEY);
        countryName.setText(currency.countryName);
        baseCurrencyShortCode.setText(currency.countryShortCode);
        setEditTextChangedListeners();

    }

    void setEditTextChangedListeners() {

        editTextBaseCurrency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
               // editTextBtc.setText(String.valueOf((baseCurrencyToBtc(Double.pa))));
            }
        });

        editTextBtc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextEth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    double btcTobaseCurrency(double btcValue) {
        return currency.oneBtcEquivalent * btcValue;
    }

    double ethToBaseCurrency(double ethValue) {
        return currency.oneEthEquivalent * ethValue;
    }

    double baseCurrencyToBtc(double baseCurrencyValue){
        return baseCurrencyValue/currency.oneBtcEquivalent;
    }
    double baseCurrencyToEth(double baseCurrencyValue){
        return baseCurrencyValue/currency.oneEthEquivalent;
    }

    double ethToBtc(double ethValue){
        return  currency.oneBtcEquivalent/currency.oneEthEquivalent;
    }
    double btcToEth(){
        return  currency.oneEthEquivalent/currency.oneBtcEquivalent;
    }

}
