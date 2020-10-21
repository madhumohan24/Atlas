package com.zoho.atlas.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zoho.atlas.R;
import com.zoho.atlas.adapter.CountryAdapter;
import com.zoho.atlas.adapter.WeatherAdapter;
import com.zoho.atlas.api.APIClient;
import com.zoho.atlas.api.APIInterface;
import com.zoho.atlas.database.AppDatabase;
import com.zoho.atlas.database.AppExecutors;
import com.zoho.atlas.keys.key;
import com.zoho.atlas.model.CountryDataDB;
import com.zoho.atlas.model.ForecastData;
import com.zoho.atlas.utils.GPSTracker;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerViewmap;
    private CountryAdapter mAdapter;
    private AppDatabase mDb;
    Intent intent;
    String lat="",lng="";
    ForecastData forecastData;
    TextView tv_recyclerViewheading;
    SearchView searchView;
    Button btn_currentforcast;
    String TAG="List Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lisiting);
        mDb = AppDatabase.getInstance(getApplicationContext());

        if (intent != null && intent.hasExtra("lat")) {
            lat = intent.getStringExtra("lat");
        }
        if (intent != null && intent.hasExtra("lng")) {
            lng = intent.getStringExtra("lng");
        }
        assert lat != null;
        if(!lat.equals("")){
            Apicall(lat,lng);
        }
        btn_currentforcast = findViewById(R.id.btn_currentforcast);
        tv_recyclerViewheading = findViewById(R.id.tv_recyclerViewheading);
        mRecyclerViewmap = findViewById(R.id.recyclerViewmap);

        btn_currentforcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSTracker gps = new GPSTracker(ListActivity.this);
                if (!gps.canGetLocation()) {
                    gps.showSettingsAlert();
                } else {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    lat = ""+latitude;
                    lng = ""+longitude;
                    if(!lat.equals("0.0")){
                        Apicall(lat,lng);
                    }else{
                        Toast.makeText(ListActivity.this,"can't able to locate the location. Please try again and check permission and location in settings",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new CountryAdapter(this, ListActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<CountryDataDB> country = mDb.countryDao().loadAllPersons();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setTasks(country);
                    }
                });
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    //some operation
                    return false;
                }
            });
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //some operation
                }
            });
            EditText searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            searchPlate.setHint("Search");
            View searchPlateView = searchView.findViewById(androidx.appcompat.R.id.search_plate);
            searchPlateView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            // use this method for search process
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // use this method when query submitted
                    //Toast.makeText(ListActivity.this, query, Toast.LENGTH_SHORT).show();
                    if(query.isEmpty()){
                        Toast.makeText(ListActivity.this, "Please Enter Something", Toast.LENGTH_SHORT).show();

                    }else{
                        if(query.length() > 1) {
                            mAdapter.getFilter().filter(query);
                        }
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // use this method for auto complete search process
                    mAdapter.getFilter().filter(newText);
                    return false;
                }
            });
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        }
        return super.onCreateOptionsMenu(menu);
    }


    public void Apicall(String lat, String lng) {
        Log.e(TAG, "API CALL");
        //Creating an object of our api interface
        APIInterface apiInterface = APIClient.getClient(key.WEATHERDATA).create(APIInterface.class);
        Call<ForecastData> call = apiInterface.forecastdata(lat, lng, key.CNT_VALUE, key.UNITS_VALUE, key.OPEN_WEATHER_APIKEY);
        //calling the api
        call.enqueue(new Callback<ForecastData>() {
            @Override
            public void onResponse(@NonNull Call<ForecastData> call, @NonNull Response<ForecastData> response) {
                forecastData = null;
                forecastData = response.body();
                // Log.e(TAG, ""+response.raw().code());
                Log.e(TAG, ""+response.raw().request().url());
                //Log.e(TAG, ""+response.raw().request().toString());
                //Log.e(TAG, ""+response.body().toString());

                WeatherAdapter imageAdapter = new WeatherAdapter(ListActivity.this, ListActivity.this,forecastData.getList());
                mRecyclerViewmap.setLayoutManager(new LinearLayoutManager(ListActivity.this, LinearLayoutManager.HORIZONTAL, false));
                mRecyclerViewmap.setAdapter(imageAdapter);
                if(forecastData.getList().size()>1){
                    mRecyclerViewmap.setVisibility(View.VISIBLE);
                    tv_recyclerViewheading.setVisibility(View.VISIBLE);
                    btn_currentforcast.setVisibility(View.GONE);
                }else{
                    mRecyclerViewmap.setVisibility(View.GONE);
                    tv_recyclerViewheading.setVisibility(View.GONE);
                    btn_currentforcast.setVisibility(View.VISIBLE);

                }

            }
            @Override
            public void onFailure(@NonNull Call<ForecastData> call, @NonNull Throwable t) {
                Log.e(TAG, Objects.requireNonNull(t.getMessage()));
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            //findViewById(R.id.default_title).setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

}