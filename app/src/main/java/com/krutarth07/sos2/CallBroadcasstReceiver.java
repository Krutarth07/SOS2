package com.krutarth07.sos2;

/**
 * Created by Admin on 26-03-2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class CallBroadcasstReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int volume = (Integer)intent.getExtras().get("android.media.EXTRA_VOLUME_STREAM_VALUE");

        Toast.makeText(context, "broad", Toast.LENGTH_SHORT).show();

        MainActivity inst = MainActivity.instance();
        try {
            inst.getAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }

        inst.sendsms();
    }

}