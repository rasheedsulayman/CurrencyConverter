package com.r4sh33d.currencyconverter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

public class ConversionActivity extends AppCompatActivity {
    EditText editTextBtc, editTextBaseCurrency, editTextEth;
    TextView countryName, baseCurrencyShortCode;
    Currency currency;
    DecimalFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);
        editTextBtc = (EditText) findViewById(R.id.editTextBtc);
        editTextEth = (EditText) findViewById(R.id.editTextETH);
        editTextBaseCurrency = (EditText) findViewById(R.id.editTextBaseCurrency);
        countryName = (TextView) findViewById(R.id.countryName);
        formatter = new DecimalFormat("#");
        formatter.setMaximumFractionDigits(4);
        baseCurrencyShortCode = (TextView) findViewById(R.id.baseCurrencyShortCode);
        currency = getIntent().getParcelableExtra(Utils.CURRENCY_INTENT_KEY);
        countryName.setText(currency.countryName);
        baseCurrencyShortCode.setText(currency.countryShortCode);
        setEditTextChangedListeners();
    }

    TextWatcher editTextBaseCurrencyWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {


            Utils.logMessage("After test changed called in base currency watcher");
            editTextBtc.removeTextChangedListener(editTextBtcTextWatcher);
            editTextEth.removeTextChangedListener(editTextEthWatecher);
            String enteredValue = s.toString();
            if (!TextUtils.isEmpty(enteredValue)) {



                editTextBtc.setText(formatter.format(baseCurrencyToBtc(Double.parseDouble(enteredValue))));
                editTextEth.setText(formatter.format(baseCurrencyToEth(Double.parseDouble(enteredValue))));

            }


            editTextBtc.addTextChangedListener(editTextBtcTextWatcher);
            editTextEth.addTextChangedListener(editTextEthWatecher);

        }
    };
    TextWatcher editTextBtcTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            Utils.logMessage("After test changed called in base btc watcher");
            editTextEth.removeTextChangedListener(editTextEthWatecher);
            editTextBaseCurrency.removeTextChangedListener(editTextBaseCurrencyWatcher);
            String enteredValue = s.toString();
            if (!TextUtils.isEmpty(enteredValue)) {
                Utils.logMessage("Entered the if statement");

                editTextEth.setText(formatter.format(btcToEth(Double.parseDouble(enteredValue))));
                editTextBaseCurrency.setText(formatter.format(btcTobaseCurrency(Double.parseDouble(enteredValue))));
            }
            editTextEth.addTextChangedListener(editTextEthWatecher);
            editTextBaseCurrency.addTextChangedListener(editTextBaseCurrencyWatcher);

        }
    };
    TextWatcher editTextEthWatecher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            Utils.logMessage("After test changed called in eth watcher");
            editTextBaseCurrency.removeTextChangedListener(editTextBaseCurrencyWatcher);
            editTextBtc.removeTextChangedListener(editTextBtcTextWatcher);
            String enteredValue = s.toString();
            if (!TextUtils.isEmpty(enteredValue)) {
                editTextBaseCurrency.setText(formatter.format(ethToBaseCurrency(Double.parseDouble(enteredValue))));
                editTextBtc.setText(formatter.format(ethToBtc(Double.parseDouble(enteredValue))));
            }
            editTextBaseCurrency.addTextChangedListener(editTextBaseCurrencyWatcher);
            editTextBtc.addTextChangedListener(editTextBtcTextWatcher);

        }
    };

    void setEditTextChangedListeners() {

        editTextBaseCurrency.addTextChangedListener(editTextBaseCurrencyWatcher);
        editTextBtc.addTextChangedListener(editTextBtcTextWatcher);
        editTextEth.addTextChangedListener(editTextEthWatecher);
    }


    double btcTobaseCurrency(double btcValue) {
        return currency.oneBtcEquivalent * btcValue;
    }

    double ethToBaseCurrency(double ethValue) {
        return currency.oneEthEquivalent * ethValue;
    }

    double baseCurrencyToBtc(double baseCurrencyValue) {
        return baseCurrencyValue / currency.oneBtcEquivalent;
    }

    double baseCurrencyToEth(double baseCurrencyValue) {
        return baseCurrencyValue / currency.oneEthEquivalent;
    }

    double ethToBtc(double ethValue) {
        return ethValue * currency.oneBtcEquivalent / currency.oneEthEquivalent;
    }

    double btcToEth(double btcValue) {
        return btcValue * currency.oneEthEquivalent / currency.oneBtcEquivalent;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
