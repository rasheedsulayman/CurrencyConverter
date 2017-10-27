package com.r4sh33d.currencyconverter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.r4sh33d.currencyconverter.database.CurrencyDBHelper;

import java.util.ArrayList;

/**
 * Created by rasheed on 10/27/17.
 */

public class MyDialogFragment extends DialogFragment implements MyCursorAdapter.MultichoiceItemSelectedListener {
    RecyclerView recyclerView;
    final ArrayList<Integer> selectedItems = new ArrayList<>();  // Where we track the selected items
    final ArrayList<Integer> desabledItems = new ArrayList<>();
    MyCursorAdapter myCursorAdapter;
    private CurrencyDBHelper currencyDBHelper;
    SQLiteDatabase database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static MyDialogFragment newInstance(SQLiteDatabase database) {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.alertdialogfrag_view, null);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewDialog);
        database = currencyDBHelper.getWritableDatabase();
        myCursorAdapter = new MyCursorAdapter(getActivity() , Utils.makeCreateCardDialogCursor(database) ,
               this );


        mBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        }).setTitle("Create Cards");

        mBuilder.setView(v);
        return mBuilder.create();

    }


    @Override
    public void onItemSelected(int which, boolean isChecked) {
        int indexToUse = which;
        if (isChecked) {
            if (desabledItems.contains(indexToUse)) {
                desabledItems.remove(Integer.valueOf(indexToUse));
            }
            selectedItems.add(indexToUse);
        } else {
            if (selectedItems.contains(indexToUse)) {
                selectedItems.remove(Integer.valueOf(indexToUse));
            }
            desabledItems.add(indexToUse);
        }

    }


}
