package com.example.kavya.rescueme4;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    EditText userName;
    SharedPreferences loginData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginData = getSharedPreferences("name", Context.MODE_PRIVATE);
        userName = (EditText) findViewById(R.id.nameInput);


        setContentView(R.layout.activity_settings);


    }
    public void saveData(View view){

        SharedPreferences.Editor editor = loginData.edit();

        if(userName.getText().toString().matches(""))
         Toast.makeText(this,"Put a Name",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this,"Name Saved",Toast.LENGTH_LONG).show();

       editor.putString("name", userName.getText().toString());

        editor.apply();
         userName.setText("");

    }
/*
   public void getData(View view){
        SharedPreferences loginData = getSharedPreferences("name", Context.MODE_PRIVATE);
        String name = loginData.getString("name", "");
       // String pw = loginData.getString("password","");
        String msg = "Saved User Name: " + name ;
        dataView.setText(msg);
    }*/

}
