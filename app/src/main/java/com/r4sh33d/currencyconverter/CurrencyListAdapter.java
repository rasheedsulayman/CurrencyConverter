package com.r4sh33d.currencyconverter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 *
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_currency_list , parent , false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        Currency currency = currencyArrayList.get(position);
        holder.countryNameTv.setText("Country Name :"+currency.countryName);
        holder.countryExchangeCodeTv.setText("Currency shortcode :"+currency.countryShortCode);
        holder.oneBtcEquivalentTV.setText("One btc equivalent :"+String.valueOf(currency.oneBtcEquivalent));
        holder.oneEthEquivalentBc.setText("One Eth equivalent :"+String.valueOf(currency.oneEthEquivalent));

    }

    @Override
    public int getItemCount() {
        return currencyArrayList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView countryNameTv, countryExchangeCodeTv, oneBtcEquivalentTV, oneEthEquivalentBc;
        public MyHolder(View itemView) {
            super(itemView);
            countryNameTv = (TextView) itemView.findViewById(R.id.textView);
            countryExchangeCodeTv = (TextView) itemView.findViewById(R.id.textView2);
            oneBtcEquivalentTV = (TextView) itemView.findViewById(R.id.textView3);
            oneEthEquivalentBc = (TextView) itemView.findViewById(R.id.textView4);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context , ConversionActivity.class);
            intent.putExtra(Utils.CURRENCY_INTENT_KEY , currencyArrayList.get(getAdapterPosition()));
            context.startActivity(intent);
        }
    }


}
