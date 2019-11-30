package com.example.notifications;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Notification;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.notifications.db.Item;
import com.example.notifications.db.ItemHelper;

import java.util.ArrayList;
//import static com.example.notifications.MyNotification.CHANNEL_1_ID;
//import static com.example.notifications.MyNotification.CHANNEL_2_ID;


public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";

    private NotificationManagerCompat notificationManager;
    private EditText itemInput;
    private EditText amountInput;
    private GroceryItemArrayAdapter mAdapter;
    private ItemHelper mHelper;
    private ListView mItemListView;
    private Context mContext;
    private BroadcastReceiver myReceiver = new MyReceiver();
    private static final String PRODUCT_ADDED =
            BuildConfig.APPLICATION_ID + ".PRODUCT_ADDED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        itemInput = findViewById(R.id.add_product);
        amountInput = findViewById(R.id.add_amount);
        mHelper = new ItemHelper(this);
        mItemListView = findViewById(R.id.list_to_buy);
        Button btnadd = findViewById(R.id.btn1);
    }

    public void sendOnChannel1(View v) {
        String item = itemInput.getText().toString();
        String amount = amountInput.getText().toString();
        SQLiteDatabase db = mHelper.getWritableDatabase();
//                mHelper.onUpgrade(db, 6, 7);

        ContentValues values = new ContentValues();
        values.put(Item.ItemEntry.COL_ITEM_NAME, item);
        values.put(Item.ItemEntry.COL_AMOUNT, amount);
        db.insertWithOnConflict(Item.ItemEntry.TABLE,
                null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        updateUI();

        String string = "Dodano " + item + " (ilość:" + amount + ") do listy";
        registerReceiver(myReceiver,new IntentFilter("com.example.notifications.PRODUCT_ADDED"));
        Intent intent = new Intent("com.example.notifications.PRODUCT_ADDED");
        intent.putExtra("string", string);
        sendBroadcast(intent);

        itemInput.getText().clear();
        amountInput.getText().clear();

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

    public void deleteItem(View view){
        View parent = (View) view.getParent();
        TextView itemTextView = (TextView) parent.findViewById(R.id.item_title);
        String item = String.valueOf(itemTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(Item.ItemEntry.TABLE, Item.ItemEntry.COL_ITEM_NAME + " = ?", new String[] {item});
        db.close();
        updateUI();
    }

    @Override
    protected void onStart(){
        super.onStart();
        registerReceiver(myReceiver, new IntentFilter("com.example.shoppingapp.PRODUCT_ADDED"));
        Log.i("ReceiverTest", "zarejestrowano odbiorcę");
    }

    @Override
    protected void onStop(){
        super.onStop();
        unregisterReceiver(myReceiver);
        Log.i("ReceiverTest", "wyrejestrowano odbiorcę");
    }

}