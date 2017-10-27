package com.r4sh33d.currencyconverter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.r4sh33d.currencyconverter.database.CurrencyContract;

/**
 * Created by rasheed on 10/27/17.
 */

public class MyCursorAdapter extends RecyclerView.Adapter<MyCursorAdapter.MyHolder> {
    private Context context;
    Cursor cursor;
    MultichoiceItemSelectedListener selectedListener;

    public MyCursorAdapter(Context context, Cursor cursor, MyCursorAdapter.MultichoiceItemSelectedListener selectedListener) {
        this.context = context;
        this.selectedListener = selectedListener;
        this.cursor = cursor;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dialog_list, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        Utils.logMessage("Inside onbindViewHolder :" + position);
        cursor.move(position);
        int columnDialogLabelIndex = cursor.getColumnIndex(CurrencyContract.COLUMN_DIALOG_LABEL);
        int columnIsEnabledIndex = cursor.getColumnIndex(CurrencyContract.COLUMN_IS_ENABLED);
        holder.labelText.setText(cursor.getString(columnDialogLabelIndex));
        holder.checkBox.setChecked(cursor.getInt(columnIsEnabledIndex) == 1);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                holder.checkBox.setChecked(isChecked);
                selectedListener.onItemSelected(holder.getAdapterPosition(), isChecked);
            }
        });


    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView labelText;

        public MyHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkboxDialog);
            labelText = (TextView) itemView.findViewById(R.id.labelText);

        }
    }


    interface MultichoiceItemSelectedListener {
        void onItemSelected(int which, boolean isChecked);
    }
}
