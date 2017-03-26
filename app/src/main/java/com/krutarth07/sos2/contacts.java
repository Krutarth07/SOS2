package com.krutarth07.sos2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.devs.squaremenu.OnMenuClickListener;
import com.devs.squaremenu.SquareMenu;

import java.util.ArrayList;
import java.util.List;

public class contacts extends Activity {

    //EditText msg;
    TextView cont;
    Button sub, pick;
    private static final int RESULT_PICK_CONTACT = 8500;
    ListView lv;
    List<String> list,list2;
    ArrayAdapter<String> adapter,adap2;
    CharSequence options[] = new CharSequence[]{"Yes", "No"};

    String phone = null, name=null;
    int k;

    SharedPreferences pref;
    SharedPreferences.Editor edit;

    @Override
    public void onBackPressed() {}


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getActionBar().setTitle("Contacts");

        lv = (ListView) findViewById(R.id.nolist);
       // msg = (EditText) findViewById(R.id.mssg);
        cont = (TextView) findViewById(R.id.cont);
        sub = (Button) findViewById(R.id.submit);
        pick = (Button) findViewById(R.id.pick);

        list = new ArrayList<String>();
        list2 = new ArrayList<String>();
        pref = getSharedPreferences("details", MODE_PRIVATE);
        edit = pref.edit();
        //i=pref.getInt("i",0);
//        j = pref.getInt("size",0);

        //msg.setText(pref.getString("msg", null));

        SquareMenu mSquareMenu = (SquareMenu) findViewById(R.id.square_menu);
        mSquareMenu.setOnMenuClickListener(new OnMenuClickListener(){
            @Override
            public void onMenuOpen() {  }
            @Override
            public void onMenuClose() { }
            @Override
            public void onClickMenu1() {
            }
            @Override
            public void onClickMenu2() { }
            @Override
            public void onClickMenu3() { }
        });
        //setcontdisp();
        getnames();

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int itempos, long l) {

                k=itempos;
                AlertDialog.Builder builder = new AlertDialog.Builder(contacts.this);
                builder.setTitle("Delete?");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int p) {

                        if(p==0){


                            list.remove(k);
                            list2.remove(k);

                           // list.set(k,"");
                           // list2.set(k,"");
                            adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,list);
                            adap2 = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,list2);
                            lv.setAdapter(adap2);
                            adapter.notifyDataSetChanged();
                            adap2.notifyDataSetChanged();
                            lv.setAdapter(adap2);
                            //Toast.makeText(details.this, "delete"+i, Toast.LENGTH_SHORT).show();

                            //edit.putString("contacts"+i,null);
                           // edit.putString("name"+i,null);
                            //edit.commit();
                        }

                    }
                });
                builder.show();

                return false;
            }
        });
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickContact();
            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edit.putInt("adpcount",adap2.getCount());
                //edit.putInt("size",i);
                //edit.putInt("i",i);
                edit.commit();
                //Toast.makeText(details.this, ""+i, Toast.LENGTH_SHORT).show();
                setnames();
                Intent i = new Intent(contacts.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    public void pickContact() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {

            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact `
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            phone = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);

            LayoutInflater lay = getLayoutInflater();
            View view = lay.inflate(R.layout.custom_toast,(ViewGroup)findViewById(R.id.custom_layout));

            Toast t1 = Toast.makeText(getApplicationContext(), "Successfully added.", Toast.LENGTH_SHORT);
            t1.setView(view);
            t1.show();

            list.add(phone);
            list2.add(name);

            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, list);
            adap2 = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,list2);
            lv.setAdapter(adap2);


        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();//////////////Performance Improvement/////////////////
    }


    public void setnames(){

        for(int i=0;i<adap2.getCount();++i){
            edit.putString(String.valueOf(i)+"c",adapter.getItem(i));
            edit.putString(String.valueOf(i),adap2.getItem(i));
        }
            edit.commit();
    }

    public void getnames(){
        int vnt=pref.getInt("adpcount",0);
        for (int i = 0;i<vnt; ++i){
            final String str = pref.getString(String.valueOf(i), "");
            final String str1 = pref.getString(String.valueOf(i)+"c", "");
            if (!str.equals("")){
                list.add(str1);
                list2.add(str);
            } else {
                break; // Empty String means the default value was returned.
            }
        }
        adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,list);
        adap2 = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,list2);
        lv.setAdapter(adap2);
    }



}
