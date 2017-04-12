package com.krutarth07.sos2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bcgdv.asia.lib.fanmenu.FanMenuButtons;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.squareup.leakcanary.LeakCanary;
import com.yalantis.phoenix.PullToRefreshView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import im.delight.android.location.SimpleLocation;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends Activity {


    CharSequence options[] = new CharSequence[]{"Yes", "No"};

    private static MainActivity inst;
    FanMenuButtons sub;

    Button send;
    Button cam, mic, vid;
    TextView loc;
    ListView lv;

    ImageView pic;
    VideoView vidv;

    Button whatsapp, fb, twitter;

    SmsManager sms;
    SimpleLocation location;
    Geocoder geocoder;

    int tutorial;
    boolean connected = false;

    List<Address> addresses;
    double latitude, longitude;
    Thread thr;
    String strPhone;
    String strMessage;
    String link;
    String[] descriptionData = {"Tutorial", "Set Contacts", "Panic", "Sent"};

    String[] displayList;
    ArrayAdapter<String> adapter;
    List<String> list;
    SharedPreferences pref;

    StateProgressBar stateProgressBar;

    String address;
    String city;
    String state;
    String country;
    String postalCode;
    String a;
    int j,request=0;
    String requestno;
    String IMEI;
    TelephonyManager tm;

    public static MainActivity instance() {
        return inst;
    }
    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install((Application) getApplicationContext());

        tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = tm.getDeviceId();

        final PullToRefreshView mPullToRefreshView;
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                        checkinternet();
                        try {


                            if(connected==true){

                                getAddress();
                            }
                            else {

                                 startNewActivity(MainActivity.this,"net.usikkert.kouchat.android");
                            }
                        }catch (Exception e) {

                        }

                        getBatteryPercentage();
                    }
                }, 1000);
            }
        });



        //getActionBar().setIcon(R.drawable.app);
sub= (FanMenuButtons) findViewById(R.id.myFABSubmenu);

        sms = SmsManager.getDefault();

        cam = (Button) findViewById(R.id.camera);
        mic = (Button) findViewById(R.id.mic);
        vid = (Button) findViewById(R.id.video);

        pic = (ImageView) findViewById(R.id.pic);
        vidv = (VideoView) findViewById(R.id.videoView);

        whatsapp = (Button) findViewById(R.id.whatsapp);
        fb = (Button) findViewById(R.id.fb);
        twitter = (Button) findViewById(R.id.twi);

        send = (Button) findViewById(R.id.send);
        loc = (TextView) findViewById(R.id.loc);
        lv = (ListView) findViewById(R.id.lv);
        geocoder = new Geocoder(this, Locale.getDefault());

        pref = getSharedPreferences("details", MODE_PRIVATE);


        send.setBackgroundResource(R.drawable.norm);

        stateProgressBar = (StateProgressBar) findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);



///////////////////////////tutorial///////////////////////////////////////

        tutorial = pref.getInt("tutorial", 0);

        if(tutorial!=1) {

            ///////////////////new view////////////////////////
            new MaterialTapTargetPrompt.Builder(MainActivity.this)
                    .setTarget(findViewById(R.id.settings))
                    .setPrimaryText("Settings")
                    .setPrimaryTextTypeface(Typeface.DEFAULT_BOLD)
                    .setSecondaryText("Tap the button to add/delete the emergency contacts.")
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                        @Override
                        public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                            /////////////new view////////////////////////////////////////////////\
                            new MaterialTapTargetPrompt.Builder(MainActivity.this)
                                    .setTarget(findViewById(R.id.send))
                                    .setPrimaryText("PANIC BUTTON")
                                    .setPrimaryTextTypeface(Typeface.DEFAULT_BOLD)
                                    .setSecondaryText("Tap the button to send emergency message to selected contacts.")
                                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                        @Override
                                        public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                                            ///////////////////new view////////////////////////
                                            new MaterialTapTargetPrompt.Builder(MainActivity.this)
                                                    .setTarget(findViewById(R.id.fb))
                                                    .setPrimaryText("SOCIAL BUTTON")
                                                    .setPrimaryTextTypeface(Typeface.DEFAULT_BOLD)
                                                    .setSecondaryText("Tap these buttons to send messages or update status.")
                                                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                                        @Override
                                                        public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                                                            ///////////////////new view////////////////////////
                                                            new MaterialTapTargetPrompt.Builder(MainActivity.this)
                                                                    .setTarget(findViewById(R.id.camera))
                                                                    .setPrimaryText("Media Button")
                                                                    .setPrimaryTextTypeface(Typeface.DEFAULT_BOLD)
                                                                    .setSecondaryText("Tap these buttons to send image or videos as a message to selected contacts.")
                                                                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                                                        @Override
                                                                        public void onHidePrompt(MotionEvent event, boolean tappedTarget) {

                                                                            tutorial = 1;
                                                                            SharedPreferences.Editor edit = pref.edit();
                                                                            edit.putInt("tutorial", tutorial);
                                                                            edit.commit();
                                                                            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);

                                                                        }

                                                                        @Override
                                                                        public void onHidePromptComplete() {

                                                                        }
                                                                    })
                                                                    .show();
                                                        }

                                                        @Override
                                                        public void onHidePromptComplete() {

                                                        }
                                                    })
                                                    .show();
                                        }

                                        @Override
                                        public void onHidePromptComplete() {

                                        }
                                    })
                                    .show();

                        }

                        @Override
                        public void onHidePromptComplete() {

                        }
                    })
                    .show();
        }

/////////////////////////////////////location object////////////////////////
        location = new SimpleLocation(this);
        if (!location.hasLocationEnabled()) {
            // ask the user to enable location access
            SimpleLocation.openSettings(this);
        }


///////////////////////////////Thread/////////////////////
        thr = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {

                        Thread.sleep(2000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //     getAddress();

                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
       // thr.start();


        list = new ArrayList<String>();
        addresses = new ArrayList<Address>();

        checkinternet();
        getBatteryPercentage();
        try{
            if(connected==true) {

            getAddress();

            }else {

                startNewActivity(MainActivity.this, "net.usikkert.kouchat.android");

            }


        }
        catch (Exception e){}


/////////////////get Contacts/////////////////////////////

/*
        j = pref.getInt("size", 1);
        for (int i = 1; i <= j; i++) {
            a = pref.getString("contacts" + i, "");
            if (!a.equals("")) {
                list.add(a);
            }
        }
        adapter = new ArrayAdapter<String>(
                getApplicationContext(), android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);
*/
        int vnt=pref.getInt("adpcount",0);
        for (int i = 0;i<vnt; ++i){
            a = pref.getString(String.valueOf(i)+"c", "");

            if (!a.equals("")){
                list.add(a);
            } else {
                break; // Empty String means the default value was returned.
            }
        }
        adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,list);
        lv.setAdapter(adapter);


        if (a != "" && a != null) {
            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
        }

////////////////////////////panic button////////////////////////////////////////////////////
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkinternet();
                try {
                    getAddress();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                link="http://maps.google.com/?q="+latitude+","+longitude+"\n";
                strMessage = pref.getString("msg", "");
                strMessage = strMessage + "\n\n" + "My Location is : " + address + "," + city + "," + state + "," + country + "," + postalCode+"\n\n"+"Google Maps : "+link+"\n\n IMEI : "+IMEI;

                sendsms();

            }
        });

        toucheffect();
//////////////////////////////////camera////////////////////////////////////////
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);

                }
            }
        });

/////////////////////////////////video/////////////////////////////////////////////
        vid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, 2);

                }

              //  sub.toggleShow();

            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkinternet();

                try {


                    if(connected==true){

                        getAddress();
                    }
                    else {

                        startNewActivity(MainActivity.this,"net.usikkert.kouchat.android");

                    }
                }catch (Exception e) {

                }

                Intent intent = MainActivity.this.getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                if (intent == null) {
                    // Bring user to the market or let them choose an app?
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + "com.whatsapp"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, strMessage);
                    sendIntent.setType("text/plain");
                    sendIntent.setPackage("com.whatsapp");
                    startActivity(sendIntent);
                }

            }
        });
/////////////////////////////////////////////////////////////////////////////////
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkinternet();
                try {


                    if(connected==true){

                        getAddress();
                    }
                    else{

                        startNewActivity(MainActivity.this,"net.usikkert.kouchat.android");

                    }

                }catch (Exception e) {

                }

                Intent intent = MainActivity.this.getPackageManager().getLaunchIntentForPackage("com.facebook.katana");
                if (intent == null) {
                    // Bring user to the market or let them choose an app?
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + "com.facebook.katana"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setPackage("com.facebook.katana");
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, strMessage);
                    startActivity(sendIntent);
                }
            }
        });
//////////////////////////////////////////////////////////////////////////
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkinternet();
                try {


                    if(connected==true){

                        getAddress();
                    }
                    else{

                        startNewActivity(MainActivity.this,"net.usikkert.kouchat.android");

                    }

        }catch (Exception e) {

        }


                Intent intent = MainActivity.this.getPackageManager().getLaunchIntentForPackage("com.twitter.android");
                if (intent == null) {
                    // Bring user to the market or let them choose an app?
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + "com.twitter.android"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setPackage("com.twitter.android");
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, strMessage);
                    startActivity(sendIntent);
                }

            }
        });

    }     //END OF ON_CREATE////////////..................///////////////////.......................////////////////////
////////////////////////////////////////////////////////////////////////////
        private void checkinternet() {
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
            }
            else
                connected = false;
        }

    private void custommenu() {

        if (sub != null) {
            sub.setOnFanButtonClickListener(new FanMenuButtons.OnFanClickListener() {
                @Override
                public void onFanButtonClicked(int index) {
                    Toast.makeText(MainActivity.this, "ters", Toast.LENGTH_SHORT).show();
                }
            });

            sub.setOnFanAnimationListener(new FanMenuButtons.OnFanAnimationListener() {
                @Override
                public void onAnimateInStarted() {
                }

                @Override
                public void onAnimateOutStarted() {
                }

                @Override
                public void onAnimateInFinished() {
                }

                @Override
                public void onAnimateOutFinished() {
                }
            });
        }


    }

    /////////////////////////////address///////////////////////////////////////
    public void getAddress() throws IOException {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        //try {
        addresses = geocoder.getFromLocation(latitude, longitude, 1);
        if(addresses==null){
            Toast.makeText(this, "Nothing Found", Toast.LENGTH_SHORT).show();
        }

        //} catch (IOException e) {
        //   e.printStackTrace();
        // }
        else {
            address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            loc.setText(address + ", " + city + ", " + state + ", " + country + ", " + postalCode);
            link="http://maps.google.com/?q="+latitude+","+longitude+"\n";
            strMessage = pref.getString("msg", "");
            strMessage = strMessage + "\n\n" + "My Location is : " + address + "," + city + "," + state + "," + country + "," + postalCode+"\n\n"+"Google Maps : "+link+"\n\n IMEI : "+IMEI;

        }

    }

    //////////////////////////////////effect//////////////////////////////////////////
    public void toucheffect() {

        send.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        send.setBackgroundResource(R.drawable.press);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        send.setBackgroundResource(R.drawable.norm);
                        break;
                    }
                }
                return false;
            }
        });

        cam.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        cam.setBackgroundResource(R.drawable.camera_pressed);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        cam.setBackgroundResource(R.drawable.camera);
                        break;
                    }
                }
                return false;
            }
        });

        mic.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        mic.setBackgroundResource(R.drawable.mic_pressed);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        mic.setBackgroundResource(R.drawable.mic);
                        break;
                    }
                }
                return false;
            }
        });

        vid.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        vid.setBackgroundResource(R.drawable.video_pressed);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        vid.setBackgroundResource(R.drawable.video);
                        break;
                    }
                }
                return false;
            }
        });

        custommenu();

    }

    //////////////////send sms..///////////////////////////////////////

    void sendsms(){
        if (a != "" && a != null) {
            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
            for (int h = 0; h < lv.getCount(); h++) {

                try {

                    strPhone = String.valueOf(lv.getItemAtPosition(h));
                    Toast.makeText(MainActivity.this, ""+strPhone, Toast.LENGTH_SHORT).show();

                    sms.sendTextMessage(strPhone, null, strMessage, null, null);

                    stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);

                } catch (Exception e) {
                }
            }



            Toast.makeText(MainActivity.this, "Sent.", Toast.LENGTH_SHORT).show();

        } else {

            //stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);

            com.nispok.snackbar.Snackbar.with(getApplicationContext()).color(Color.GREEN).textColor(Color.BLACK) // context
                    .text("No Contacts Selected")// text to display
                    .show(MainActivity.this);


        }
    }

    //////////////////////////display pic,video////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {

            pic.setVisibility(View.VISIBLE);
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            pic.setImageBitmap(imageBitmap);

          /*ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            Intent sendIntent = new Intent();
            sendIntent.setType("image/*");
            sendIntent.putExtra(android.content.Intent.EXTRA_STREAM, imageBitmap);
            sendIntent.putExtra(Intent.EXTRA_TEXT,"com.whatsapp");

            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
                                            */

        }
        if (requestCode == 2 && resultCode == RESULT_OK) {

            vidv.setVisibility(View.VISIBLE);
            Uri videoUri = data.getData();
            vidv.setVideoURI(videoUri);
            vidv.start();
        }
    }


    ///////////////////menu////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.home).setVisible(false);

        return true;
    }

    ////////////////////////////////menu items////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection

        switch (item.getItemId()) {

            case R.id.settings:
                Intent i = new Intent(MainActivity.this, settingspane.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // make the device update its location
        location.beginUpdates();

        checkinternet();
        try {


            if(connected==true){

                getAddress();
            }
            else {

                startNewActivity(MainActivity.this, "net.usikkert.kouchat.android");
            }
        }catch (Exception e) {

        }
        // ...
    }

    @Override
    protected void onPause() {
        // stop location updates (saves battery)
        location.endUpdates();

        // ...

        super.onPause();
    }

    void getMessage(String addr,String msg) {

        request=0;
        requestno=addr;

          String lockeyword;
        lockeyword = pref.getString("lockeyword","");



            for(int i=0;i<lv.getCount();i++){
                        if(requestno.contains(String.valueOf(lv.getItemAtPosition(i)))) {
                            if (msg.equals(lockeyword)) {

                            request = 1;
                          Toast.makeText(MainActivity.this, "getloc", Toast.LENGTH_SHORT).show();

                    }
                }
        }

        if(request==1){
            checkinternet();
            try {
                getAddress();
            } catch (IOException e) {
                e.printStackTrace();
            }

            link="http://maps.google.com/?q="+latitude+","+longitude+"\n";
            strMessage = pref.getString("msg", "");
            strMessage = strMessage + "\n\n" + "My Location is : " + address + "," + city + "," + state + "," + country + "," + postalCode+"\n\n"+"Google Maps : "+link+"\n\n IMEI : "+IMEI;

            sendsms();
        }

    }


 /*   @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_UP) {
                    sendsms();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    Toast.makeText(inst, "down", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }*/

    public void startNewActivity(final Context context, final String packageName) {


            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Open offline Chat Program?");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int p) {

                    if (p == 0) {

                        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                        if (intent == null) {
                            // Bring user to the market or let them choose an app?
                           // intent = new Intent(Intent.ACTION_VIEW);
                            //intent.setData(Uri.parse("market://details?id=" + packageName));

                            String url = "https://drive.google.com/open?id=0B8zBHA0ymlTfTkFmX1VhQUN1bGM";
                            Intent in = new Intent(Intent.ACTION_VIEW);
                            in.setData(Uri.parse(url));
                            startActivity(in);

                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    }

                }
            });


            builder.show();

    }

    public void getBatteryPercentage() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (currentLevel >= 0 && scale > 0) {
                    level = (currentLevel * 100) / scale;

                    int batt=pref.getInt("battlevel",30);
                    if(level<=batt){

                        try {
                            getAddress();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sendsms();
                    }
                }

            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }


}



