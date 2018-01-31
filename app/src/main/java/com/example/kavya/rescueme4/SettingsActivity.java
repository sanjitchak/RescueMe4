package com.example.kavya.rescueme4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private static final int PICK_CONTACT_REQUEST = 1;
    EditText userName;
    SharedPreferences loginData;
    SharedPreferences.Editor editor;
    EditText contactOneText;
    EditText contactTwoText;
    EditText contactThreeText;
    int contactType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_settings);
        loginData = getSharedPreferences("name", Context.MODE_PRIVATE);
        userName = (EditText) findViewById(R.id.nameInput);

        contactOneText = (EditText) findViewById(R.id.contactOneText);
        contactTwoText = (EditText) findViewById(R.id.contactTwoText);
        contactThreeText = (EditText) findViewById(R.id.contactThreeText);
        String contactOne = loginData.getString("contactOne", "");
        String contactTwo = loginData.getString("contactTwo", "");
        String contactThree = loginData.getString("contactThree", "");
        String name = loginData.getString("name","");
        userName.setText(name);
        contactOneText.setText(contactOne);
        contactTwoText.setText(contactTwo);
        contactThreeText.setText(contactThree);
        contactType = 0;

    }
    @Override
    public void onBackPressed() {
        //go to HOME screen
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public void saveData(View view) {

        SharedPreferences.Editor editor = loginData.edit();

        if (userName.getText().toString().matches(""))
            Toast.makeText(this, "Put a Name", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Name Saved", Toast.LENGTH_SHORT).show();

        editor.putString("name", userName.getText().toString());

        editor.apply();
        userName.setText("");

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

    public void getData(View view) {

        String name = loginData.getString("name", "");

        String msg = "Saved Name: " + name;
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void saveContacts(View view) {
        Toast.makeText(this, "Contacts Saved", Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = loginData.edit();
        editor.putString("contactOne", contactOneText.getText().toString());
        editor.putString("contactTwo", contactTwoText.getText().toString());
        editor.putString("contactThree", contactThreeText.getText().toString());

        editor.apply();

    }

    public void goToMain(View view) {
        String name = loginData.getString("name", "");
        String contactOne = loginData.getString("contactOne", "");

        Intent I = new Intent(this, MainActivity.class);
        if (name.matches("") || contactOne.matches(""))
            Toast.makeText(this, "Need a Name And First Contact Info", Toast.LENGTH_SHORT).show();
        else
            startActivity(I);
    }
}
