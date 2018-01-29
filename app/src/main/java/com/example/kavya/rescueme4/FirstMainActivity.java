package com.example.kavya.rescueme4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class FirstMainActivity extends AppCompatActivity {
    EditText userName;
    SharedPreferences loginData;
    Intent S ;
    Intent M;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginData = getSharedPreferences("name", Context.MODE_PRIVATE);

        S = new Intent(this, SettingsActivity.class);
        M = new Intent(this, MainActivity.class);
        String name = loginData.getString("name", "");
        String contactOne = loginData.getString("contactOne", "");
        if(name.matches("") || contactOne.matches("") )
            startActivity(S);
        else
            startActivity(M);
    }
}
