package com.example.kavya.rescueme4;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import android.widget.Toast;


import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SettingsActivity extends AppCompatActivity {
    private static final int PICK_CONTACT_REQUEST = 1;
    EditText userName;
    int randomNum;
    SharedPreferences loginData;
    SharedPreferences.Editor editor;
    EditText contactOneText;
    EditText contactTwoText;
    EditText contactThreeText;
    EditText phoneText;
    EditText otpText;
    EditText emailText;
    MyDBHandler dbHandler;
    int contactType;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_settings);
        loginData = getSharedPreferences("name", Context.MODE_PRIVATE);
        userName = (EditText) findViewById(R.id.nameInput);
        otpText = (EditText) findViewById(R.id.otpText);
        phoneText = (EditText) findViewById(R.id.phoneText);
        contactOneText = (EditText) findViewById(R.id.contactOneText);
        contactTwoText = (EditText) findViewById(R.id.contactTwoText);
        emailText = (EditText) findViewById(R.id.emailText);
        progressDialog = new ProgressDialog(this);
        contactThreeText = (EditText) findViewById(R.id.contactThreeText);
        String contactOne = loginData.getString("contactOne", "");
        String contactTwo = loginData.getString("contactTwo", "");
        String contactThree = loginData.getString("contactThree", "");
        String email = loginData.getString("email", "");
        String phone = loginData.getString("phone", "");
        String name = loginData.getString("name", "");
        userName.setText(name);
        emailText.setText(email);
        phoneText.setText(phone);

        contactOneText.setText(contactOne);
        contactTwoText.setText(contactTwo);
        contactThreeText.setText(contactThree);
        contactType = 0; //for picking contacts

        dbHandler = new MyDBHandler(this, null, null, 1);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        }

    }

    public void requestPermission() {
        //Requesting permissions
        String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, android.Manifest.permission.SEND_SMS, android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET, android.Manifest.permission.READ_PHONE_STATE};

        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        PERMISSIONS,
                        1);
            }
        }

    }

    @Override
    public void onBackPressed() {
        Intent first = new Intent(new Intent(this, FirstMainActivity.class));
        startActivity(first);

        //go to HOME screen
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


    public void pickContactOne(View view) {

        contactType = 1;

        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);


    }

    public void pickContactTwo(View view) {

        contactType = 2;

        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);


    }

    public void pickContactThree(View view) {

        contactType = 3;

        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok

        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case PICK_CONTACT_REQUEST:
                    contactPicked(data);

                    break;
            }
        } else {
            Log.e("SettingsActivity", "Failed to pick contact");

        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;

        try {

            String phoneNo = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            phoneNo = cursor.getString(phoneIndex);
            if (contactType == 1)
                contactOneText.setText(phoneNo);
            else if (contactType == 2)
                contactTwoText.setText(phoneNo);
            else
                contactThreeText.setText(phoneNo);


        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void getOtp(View view) {
        SharedPreferences.Editor editor = loginData.edit();

        randomNum = ThreadLocalRandom.current().nextInt(1000, 9998 + 1);

        if (phoneText.getText().toString().isEmpty() || !phoneText.getText().toString().matches("[2-9]{2}\\d{8}")) {
            Toast.makeText(this, "Enter Valid 10 digit number", Toast.LENGTH_LONG).show();

            return;
        }

        Toast.makeText(this, "Sending OTP", Toast.LENGTH_LONG).show();
        sendSMS(phoneText.getText().toString(), "Your 'Rescue Me' OTP is : " + randomNum);
        editor.putString("otp", Integer.toString(randomNum));
        editor.apply();

    }

    private void sendSMS(String phoneNumber, String message) {
       try {
           SmsManager sms = SmsManager.getDefault();
           sms.sendTextMessage(phoneNumber, null, message, null, null);
       }
           catch(Exception e)
        {

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void saveContactsName(View view) {
        SharedPreferences.Editor editor = loginData.edit();

        if (phoneText.getText().toString().isEmpty() || !phoneText.getText().toString().matches("[2-9]{2}\\d{8}")) {
            Toast.makeText(this, "Enter Valid 10 digit number", Toast.LENGTH_LONG).show();

            return;
        }

        String otpGot = loginData.getString("otp", "");
        if (otpGot.matches("")) {
            getOtp(null);
            return;
        }


        if (!otpText.getText().toString().equals(Integer.toString(randomNum))) {
            Toast.makeText(this, "OTP Wrong or Empty ", Toast.LENGTH_LONG).show();
            return;

        }
        if (otpText.getText().toString().isEmpty() || !otpText.getText().toString().equals(Integer.toString(randomNum))) {
            Toast.makeText(this, "OTP Wrong or Empty", Toast.LENGTH_LONG).show();
            otpText.requestFocus();
            return;
        }
        if (emailText.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailText.getText().toString()).matches()) {
            Toast.makeText(this, "Email Wrong or Empty", Toast.LENGTH_LONG).show();
            emailText.requestFocus();
            return;
        }


        editor.putString("contactOne", contactOneText.getText().toString());
        editor.putString("contactTwo", contactTwoText.getText().toString());
        editor.putString("contactThree", contactThreeText.getText().toString());
        editor.putString("name", userName.getText().toString());
        editor.putString("phone", phoneText.getText().toString());
        editor.putString("email", emailText.getText().toString());
        editor.apply();

        goToMain();
    }

    public void goToMain() {
        final String name = loginData.getString("name", "");
        final String contactOne = loginData.getString("contactOne", "");
        final String contactTwo = loginData.getString("contactTwo", "");
        final String contactThree = loginData.getString("contactThree", "");
        final String phone = loginData.getString("phone", "");
        final String email = loginData.getString("email", "");

        final Intent I = new Intent(this, MainActivity.class);
        if (name.matches("") || contactOne.matches(""))
            Toast.makeText(this, "Need Name And First Contact Info", Toast.LENGTH_SHORT).show();
        else {
            progressDialog.setMessage("Registering Please Wait...");
            progressDialog.show();

final String msg =  "Your Name: " + name + "\n" + "Your Email :" + email + "\n" + "Your Phone :" + phone + "\n" + "Your Contact One :" + contactOne + "\n" + "Your Contact Two :" + contactTwo + "\n" + "Your Contact Three :" + contactThree + "\n";
            new Thread(new Runnable() {

                public void run() {
                    try {

                    GMailSender sender = new GMailSender("rescuemeproject2018@gmail.com", "rescuemeproject2018123");
                        sender.sendMail("Your Rescue Me App Details",
                               msg,
                                "rescuemeproject2018@gmail.com",
                                email);
                      wait(2000);

                    } catch (Exception e) {
                        Log.e("SendMail", "Outside:"+e.getMessage());


                    }
            }

        }).start();

dbHandler.setDetails(name,email,phone);

            Toast.makeText(this, "Your App Details is being Sent to your Mail. Keep your mobile DATA on", Toast.LENGTH_LONG).show();
            startActivity(I);


        }
    }
}
