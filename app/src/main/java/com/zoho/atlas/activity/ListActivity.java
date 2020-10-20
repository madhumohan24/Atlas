package com.zoho.atlas.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zoho.atlas.keys.key;
import com.zoho.atlas.model.CountryDataDB;
import com.zoho.atlas.model.ForecastData;
import com.zoho.atlas.adapter.WeatherAdapter;
import com.zoho.atlas.adapter.CountryAdapter;
import com.zoho.atlas.R;
import com.zoho.atlas.api.APIClient;
import com.zoho.atlas.api.APIInterface;
import com.zoho.atlas.database.AppDatabase;
import com.zoho.atlas.database.AppExecutors;
import com.zoho.atlas.utils.GPSTracker;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView,mRecyclerViewmap;
    private CountryAdapter mAdapter;
    private AppDatabase mDb;
    LinearLayout rootview;
    EditText searchEt;
    Intent intent;
    String lat="",lng="";
    Button btnSearch,btnClear;
    ForecastData forecastData;
    TextView tv_recyclerViewheading;
    String TAG="Main Activity";
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
        }else{
            GPSTracker gps = new GPSTracker(this);
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            lat = ""+latitude;
            lng = ""+longitude;
            Apicall(lat,lng);
        }

        rootview = findViewById(R.id.rootview);
        searchEt = findViewById(R.id.search_et);
        btnSearch = findViewById(R.id.btnSearch);
        btnClear = findViewById(R.id.btnClear);
        tv_recyclerViewheading = findViewById(R.id.tv_recyclerViewheading);


        mRecyclerViewmap = findViewById(R.id.recyclerViewmap);
       mRecyclerView = findViewById(R.id.recyclerView);
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

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    // Then just use the following hide keyboard:
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(rootview.getWindowToken(), 0);

                        String searchWord = searchEt.getText().toString();
                        if(searchWord.isEmpty()){
                            searchEt.setError("Please Enter Something");
                            searchEt.requestFocus();

                        }else{
                            if(searchWord.length() > 1) {
                                mAdapter.getFilter().filter(searchWord);
                            }
                        }

            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEt.setText("");
            }
        });
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(searchEt.getText().length()>1){
                    btnClear.setVisibility(View.VISIBLE);
                }else if(searchEt.getText().length()==0){
                    mAdapter.updatelist();
                    btnClear.setVisibility(View.GONE);
                }else{
                    btnClear.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Then just use the following hide keyboard:
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(rootview.getWindowToken(), 0);
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchWord = v.getText().toString();
                    if(searchWord.isEmpty()){
                        searchEt.setError("Please Enter Something");
                        searchEt.requestFocus();

                    }else{
                        if(searchWord.length() > 1) {
                            mAdapter.getFilter().filter(searchWord);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
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
               /* Log.e(TAG, ""+response.raw().code());
                Log.e(TAG, ""+response.raw().request().url());
                Log.e(TAG, ""+response.raw().request().toString());
                Log.e(TAG, ""+response.body().toString());*/

                WeatherAdapter imageAdapter = new WeatherAdapter(ListActivity.this, ListActivity.this,forecastData.getList());
                mRecyclerViewmap.setLayoutManager(new LinearLayoutManager(ListActivity.this, LinearLayoutManager.HORIZONTAL, false));
                mRecyclerViewmap.setAdapter(imageAdapter);
                if(forecastData.getList().size()>1){
                    mRecyclerViewmap.setVisibility(View.VISIBLE);
                    tv_recyclerViewheading.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onFailure(@NonNull Call<ForecastData> call, @NonNull Throwable t) {
                Log.e(TAG, Objects.requireNonNull(t.getMessage()));
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}