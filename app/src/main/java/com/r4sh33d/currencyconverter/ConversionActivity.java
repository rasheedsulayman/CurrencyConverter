package com.r4sh33d.currencyconverter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class ConversionActivity extends AppCompatActivity {
    EditText editTextBtc, editTextBaseCurrency, editTextEth;
    TextView countryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);
        editTextBtc = (EditText)findViewById(R.id.editTextBtc);
        editTextEth = (EditText)findViewById(R.id.editTextETH);
        editTextBaseCurrency = (EditText)findViewById(R.id.editTextBaseCurrency);
        countryName = (TextView)findViewById(R.id.countryName);

    }
}
