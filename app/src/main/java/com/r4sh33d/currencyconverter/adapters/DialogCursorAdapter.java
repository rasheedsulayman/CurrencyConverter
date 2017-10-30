package com.r4sh33d.currencyconverter.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.r4sh33d.currencyconverter.R;
import com.r4sh33d.currencyconverter.utils.Utils;
import com.r4sh33d.currencyconverter.database.CurrencyContract;

/**
 * Created by rasheed on 10/27/17.
 */

public class DialogCursorAdapter extends RecyclerView.Adapter<DialogCursorAdapter.MyHolder> {
    Cursor cursor;
    MultichoiceItemSelectedListener selectedListener;
    SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
    private Context context;


    public DialogCursorAdapter(Context context, Cursor cursor, DialogCursorAdapter.MultichoiceItemSelectedListener selectedListener) {
        this.context = context;
        this.selectedListener = selectedListener;
        this.cursor = cursor;
        prePopulateArrayFromCursor();
    }

    private void  prePopulateArrayFromCursor() {
        int columnIsEnabledIndex = cursor.getColumnIndex(CurrencyContract.COLUMN_IS_ENABLED);
         for (int i =0 ; i < cursor.getCount() ; i++ ){
             cursor.moveToPosition(i);
             sparseBooleanArray.put(i  , (cursor.getInt(columnIsEnabledIndex)==1));
         }
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dialog_list, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        Utils.logMessage("Inside onbindViewHolder :" + position);
        cursor.moveToPosition(position);
        int columnDialogLabelIndex = cursor.getColumnIndex(CurrencyContract.COLUMN_DIALOG_LABEL);
        holder.labelText.setText(cursor.getString(columnDialogLabelIndex));
        if (sparseBooleanArray.get(position , false)){
            holder.checkBox.setChecked(true);
        }else {
            holder.checkBox.setChecked(false);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return cursor.getCount();
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }


    public interface MultichoiceItemSelectedListener {
        void onItemSelected(int which, boolean isChecked, String currencyShotCode);
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckBox checkBox;
        TextView labelText;

        public MyHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkboxDialog);
            labelText = (TextView) itemView.findViewById(R.id.labelText);
            itemView.setOnClickListener(this);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Utils.logMessage("oncheckchanged lestener called ");
                    cursor.moveToPosition(getAdapterPosition());
                    final int colunmShortCodeIndex = cursor.getColumnIndex(CurrencyContract.COLUMN_COUNTRY_SHORT_CODE);
                    selectedListener.onItemSelected(getAdapterPosition(), isChecked, cursor.getString(colunmShortCodeIndex));
                    sparseBooleanArray.put(getAdapterPosition() , isChecked);
                }
            });
        }

        @Override
        public void onClick(View v) {
            checkBox.setChecked(!checkBox.isChecked());
        }
    }
}
