package com.r4sh33d.currencyconverter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by r4sh33d on 10/20/17.
 *
 */

public class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.MyHolder> {
    private Context context;
    private ArrayList<Currency> currencyArrayList;




    public CurrencyListAdapter(Context context, ArrayList<Currency> currencyArrayList) {
        this.context = context;
        this.currencyArrayList = currencyArrayList;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_currency_list, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        Currency currency = currencyArrayList.get(position);
        holder.headerText.setText( currency.countryName + " [" + currency.countryShortCode + "] ");
        holder.oneBtcEquivalentTV.setText(String.valueOf(currency.oneBtcEquivalent));
        holder.oneEthEquivalentBc.setText(String.valueOf(currency.oneEthEquivalent));
        holder.countryflagIv.setImageResource(currency.countryFlagResource);
        holder.currencySymbolRow1Iv.setImageResource(currency.currencySymbolResource);
        holder.currencySymbolRow2Iv.setImageResource(currency.currencySymbolResource);
    }

    @Override
    public int getItemCount() {
        return currencyArrayList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView headerText, oneBtcEquivalentTV, oneEthEquivalentBc;
        ImageView countryflagIv, currencySymbolRow1Iv, currencySymbolRow2Iv;

        public MyHolder(View itemView) {
            super(itemView);
            headerText = (TextView) itemView.findViewById(R.id.headerText);

            oneBtcEquivalentTV = (TextView) itemView.findViewById(R.id.btcEquivalent);
            oneEthEquivalentBc = (TextView) itemView.findViewById(R.id.ethEquivalentTv);

            countryflagIv = (ImageView) itemView.findViewById(R.id.imageViewFlag);

            currencySymbolRow1Iv = (ImageView) itemView.findViewById(R.id.currencySymbol1);
            currencySymbolRow2Iv = (ImageView) itemView.findViewById(R.id.currencySymbol2);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ConversionActivity.class);
            intent.putExtra(Utils.CURRENCY_INTENT_KEY, currencyArrayList.get(getAdapterPosition()));
            context.startActivity(intent);
        }
    }


}
