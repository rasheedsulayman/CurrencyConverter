package com.r4sh33d.currencyconverter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by r4sh33d on 10/20/17.
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
        return null;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return currencyArrayList.size();
    }

    class  MyHolder extends  RecyclerView.ViewHolder{

        public MyHolder(View itemView) {
            super(itemView);
        }
    }
}
