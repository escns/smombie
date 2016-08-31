package com.escns.smombie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BlankActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        IntentFilter filter = new IntentFilter();

        filter.addAction("com.escns.smombie.REMOVE_BLANK_ACTIVITY");
        registerReceiver(receiver, filter);
    }

    public void finish() {
        unregisterReceiver(receiver);
        super.finish();
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();

        }
    };
}
