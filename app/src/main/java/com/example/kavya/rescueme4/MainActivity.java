package com.example.kavya.rescueme4;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    public static final String TAG = "somuTag";
    LocationManager manager ;
    TextView latitudeText;
    String latitude;
    String longitude;
    String cityName ;
    String postalCode;
    //gps location part
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager locationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
             showAlert();
        String latitude = null;
        String longitude = null;
        String cityName = null;
        String postalCode = null;

        //Requesting permissions
        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.SEND_SMS,Manifest.permission.READ_CONTACTS,Manifest.permission.ACCESS_COARSE_LOCATION};
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            for (String permission : PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            PERMISSIONS,
                            1);
                }
            }
        }

        //latitudeText = (TextView) findViewById(R.id.latitudeText);
    }


    private void showAlert() {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
        Toast.makeText(this, "ENABLE LOCATION & SET LOCATION TO HIGH ACCURACY", Toast.LENGTH_LONG).show();

    }
    @Override
    public void onBackPressed() {
        //go to HOME screen
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public void backToSettings (View view)
    {
        Intent I = new Intent(this, SettingsActivity.class);

        startActivity(I);
    }

    public void rescueMe(View view)
    {

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlert();
            return;

        }
        SharedPreferences loginData = getSharedPreferences("name", Context.MODE_PRIVATE);
        String name = loginData.getString("name", "");
        String contactOne = loginData.getString("contactOne","");
        String contactTwo = loginData.getString("contactTwo","");
        String contactThree = loginData.getString("contactThree","");
        //continue looping before it gets value
       while(latitude==null || longitude==null || cityName ==null || postalCode == null )
       {

       }
        if(!contactOne.matches(""))
 sendSMS(contactOne, "Hi! This is "+name+". "+"HELP ME!\nMy Coordinates: "+latitude+" , "+longitude+"\n"+"City Name: "+cityName+"\n"+"Postal Code: "+postalCode);

        if(!contactTwo.matches(""))
            sendSMS(contactTwo, "Hi! This is "+name+". "+"HELP ME!\nMy Coordinates: "+latitude+" , "+longitude+"\n"+"City Name: "+cityName+"\n"+"Postal Code: "+postalCode);

        if(!contactThree.matches(""))
            sendSMS(contactThree, "Hi! This is "+name+". "+"HELP ME!\nMy Coordinates: "+latitude+" , "+longitude+"\n"+"City Name: "+cityName+"\n"+"Postal Code: "+postalCode);

    }
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {

            double latitude = mLocation.getLatitude();
            double longitude = mLocation.getLongitude();
            this.longitude=Double.toString(longitude);
            this.latitude = Double.toString(latitude);



        } else {
            Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }

    }
    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);

    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        Geocoder gcd = new Geocoder(getBaseContext(),
                Locale.getDefault());
        List<android.location.Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0)
            {   //System.out.println(addresses.get(0).getLocality());
           // latitudeText.setText(addresses.get(0).getPostalCode());
            cityName = addresses.get(0).getLocality();
                postalCode = addresses.get(0).getPostalCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
