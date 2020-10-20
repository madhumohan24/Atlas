package com.zoho.atlas.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zoho.atlas.model.CountryData;
import com.zoho.atlas.model.CountryDataDB;
import com.zoho.atlas.R;
import com.zoho.atlas.api.APIClient;
import com.zoho.atlas.api.APIInterface;
import com.zoho.atlas.database.AppDatabase;
import com.zoho.atlas.database.AppExecutors;
import com.zoho.atlas.utils.App;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    ImageView iv_loader;
    private String TAG = "MainActivity";
    private List<CountryData> countryData;
    private AppDatabase mDb;
    String lat = "", lng = "";
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        iv_loader = findViewById(R.id.iv_loader);
        Glide.with(this).load(R.drawable.map).into(iv_loader);
        mDb = AppDatabase.getInstance(getApplicationContext());

        if (askPermissionForRecord()) {
            if (!App.appUtils.isNetAvailable()) {
                alertUserP(SplashActivity.this, "Connection Error", "No Internet connection available", "OK");
            } else if (!lat.equals("0.0")) {
                Log.e("LAT", lat);
                Apicall(lat, lng);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }
    private boolean askPermissionForRecord() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return false;

        } else {

            return true;
        }
    }
    public void alertUserP(Context context, String title, String msg, String btn) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage(msg).setTitle(title).setCancelable(false)
                .setPositiveButton(btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                        finish();
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                   // Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    if (!App.appUtils.isNetAvailable()) {
                        alertUserP(SplashActivity.this, "Connection Error", "No Internet connection available", "OK");
                    } else if (!lat.equals("0.0")) {
                        Log.e("LAT", lat);
                        Apicall(lat, lng);
                    }
                } else {

                    Toast.makeText(getApplicationContext(), "Location Permission denied. Please enable via app settings", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void Apicall(final String latitude, final String longitute) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int dbrecord = mDb.countryDao().getCount();
                Log.e("DB VALUE", "" + dbrecord);
                if (dbrecord == 0) {
                    //Creating an object of our api interface
                    APIInterface apiInterface = APIClient.getClient("data").create(APIInterface.class);
                    Call<List<CountryData>> call = apiInterface.getcountrydata();
                    //calling the api
                    call.enqueue(new Callback<List<CountryData>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<CountryData>> call, @NonNull Response<List<CountryData>> response) {
                            assert response.body() != null;
                            Log.e(TAG, Objects.requireNonNull(response.body().toString()));
                            countryData = response.body();
                            Log.e(TAG, "Data size" + countryData.size());
                            int i = 0;
                            for (i = 0; i < countryData.size(); i++) {
                                final CountryDataDB country = new CountryDataDB(
                                        countryData.get(i).getName(),
                                        countryData.get(i).getCapital(),
                                        countryData.get(i).getPopulation(),
                                        countryData.get(i).getLatlng().toString(),
                                        countryData.get(i).getFlag());

                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Log.e(TAG, "Data size"+person.getNumber()+" "+person.getPincode());
                                        mDb.countryDao().insertPerson(country);
                                        //Log.e(TAG, "Inserted");
                                    }
                                });
                            }
                            if (countryData.size() == i) {
                                Intent intent = new Intent(SplashActivity.this, ListActivity.class);
                                intent.putExtra("lat", latitude);
                                intent.putExtra("lng", longitute);
                                startActivity(intent);
                                finish();
                            }


                        }

                        @Override
                        public void onFailure(@NonNull Call<List<CountryData>> call, @NonNull Throwable t) {
                            Log.e(TAG, Objects.requireNonNull(t.getMessage()));
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else if (dbrecord > 0) {
                    startActivity(new Intent(SplashActivity.this, ListActivity.class));
                    finish();
                }
            }
        });

    }
}