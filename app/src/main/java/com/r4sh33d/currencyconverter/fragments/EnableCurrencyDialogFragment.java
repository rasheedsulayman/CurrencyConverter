package com.r4sh33d.currencyconverter.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.r4sh33d.currencyconverter.R;
import com.r4sh33d.currencyconverter.utils.Utils;
import com.r4sh33d.currencyconverter.activities.HomeActivity;
import com.r4sh33d.currencyconverter.adapters.DialogCursorAdapter;
import com.r4sh33d.currencyconverter.database.CurrencyDBHelper;

import java.util.ArrayList;

/**
 * Created by rasheed on 10/27/17.
 */

public class EnableCurrencyDialogFragment extends DialogFragment implements DialogCursorAdapter.MultichoiceItemSelectedListener {
    final ArrayList<Integer> selectedItems = new ArrayList<>();  // Where we track the selected items
    final ArrayList<Integer> desabledItems = new ArrayList<>(); //Where we track disabled items
    RecyclerView recyclerView;
    DialogCursorAdapter myCursorAdapter;
    SQLiteDatabase database;
    Cursor cursor;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        desabledItems.clear();
        selectedItems.clear();

        CurrencyDBHelper currencyDBHelper = new CurrencyDBHelper(getActivity());
        database = currencyDBHelper.getWritableDatabase();
        SharedPreferences sharedPref;
        sharedPref = getActivity().getSharedPreferences(
                Utils.SHARED_PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        cursor = Utils.makeCreateCardDialogCursor(database);
        myCursorAdapter = new DialogCursorAdapter(getActivity(), cursor,
                this);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.alertdialogfrag_view, null);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewDialog);
        recyclerView.setAdapter(myCursorAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.logMessage("seleced items " + selectedItems);
                Utils.logMessage("Disabled items " + desabledItems);
                if (selectedItems.size() > 0) {
                    Utils.updateCheckedRows(selectedItems, database);
                }
                if (desabledItems.size() > 0) {
                    Utils.updateUncheckedRows(desabledItems, database);
                }
                ((HomeActivity) getActivity()).refreshRecyclerViewItems();
                editor.apply();
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


    /**
     * @param which
     * @param isChecked
     * @param currencyShortCode
     *
     * Called when an item is checked/unchecked from the Create card dialog
     */
    @Override
    public void onItemSelected(int which, boolean isChecked, String currencyShortCode) {
        which++; //adapter position start from zero , while cursor column _ID start from 1 .
        if (isChecked) {
            if (desabledItems.contains(which)) {
                desabledItems.remove(Integer.valueOf(which));
            }
            selectedItems.add(which);
        } else {
            if (selectedItems.contains(which)) {
                selectedItems.remove(Integer.valueOf(which));
            }
            desabledItems.add(which);
        }
        editor.putBoolean(currencyShortCode, isChecked);
        Utils.logMessage("Put boolean --->" + currencyShortCode + " : " + isChecked );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
        }
        database.close();
    }
}
