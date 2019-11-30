package com.example.notifications;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notifications.db.ItemHelper;

public class ItemEditor extends AppCompatActivity {
    private GroceryItemArrayAdapter mAdapter;
    private ItemHelper mHelper;
    private ListView mItemListView;
    private Context mContext;
    MyReceiver myReceiver;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_edit_activity);
        mContext = getApplicationContext();
        mHelper = new ItemHelper(this);
        mItemListView = findViewById(R.id.list_of_items);
        EditText update_name = (EditText)findViewById(R.id.edit_product);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        Intent i = getIntent();
        String txt1 = i.getStringExtra("message");
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

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
