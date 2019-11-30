package com.example.notifications;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.notifications.db.Item;
import com.example.notifications.db.ItemHelper;

import java.util.ArrayList;

public class ShowProductList extends Activity {
    private GroceryItemArrayAdapter mAdapter;
    private ItemHelper mHelper;
    private ListView mItemListView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_product_list_activity);
        mContext = getApplicationContext();
        mHelper = new ItemHelper(this);
        mItemListView = findViewById(R.id.list_of_items);
        updateUI();
    }

    private void updateUI() {
        final ArrayList<GroceryItem> groceryItemList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(Item.ItemEntry.TABLE,
                new String[]{Item.ItemEntry._ID, Item.ItemEntry.COL_ITEM_NAME,
                        Item.ItemEntry.COL_AMOUNT},
                null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(Item.ItemEntry._ID);
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String amount = cursor.getString(cursor.getColumnIndex("amount"));
            groceryItemList.add(new GroceryItem(name, amount));
        }

        if (mAdapter == null) {
            if(groceryItemList.isEmpty()){

            }
            else{
                mAdapter = new GroceryItemArrayAdapter(this,groceryItemList);
                mItemListView.setAdapter(mAdapter);
            }
        }
        else {
            mAdapter.clear();
            mAdapter.addAll(groceryItemList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }


}