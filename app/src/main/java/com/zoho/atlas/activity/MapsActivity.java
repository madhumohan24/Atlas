package com.zoho.atlas.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zoho.atlas.R;
import com.zoho.atlas.adapter.MapsWeatherAdapter;
import com.zoho.atlas.api.APIClient;
import com.zoho.atlas.api.APIInterface;
import com.zoho.atlas.database.AppDatabase;
import com.zoho.atlas.database.AppExecutors;
import com.zoho.atlas.keys.key;
import com.zoho.atlas.model.CountryDataDB;
import com.zoho.atlas.model.ForecastData;
import com.zoho.atlas.utils.MyMarkerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private AppDatabase mDb;
    int id;
    String lat, lng;
    Intent intent;
    private LineChart chart;
    ForecastData forecastData;
    String TAG = "MapsActivity";
    private RecyclerView mRecyclerView;
    ImageView edit_Image;
    String url="";
    TextView population,city,country;
    ArrayList<Entry> values = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mDb = AppDatabase.getInstance(getApplicationContext());
        intent = getIntent();
        mRecyclerView = findViewById(R.id.recyclerViewmap);
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", 0);
        }
        edit_Image = findViewById(R.id.edit_Image);
        population = findViewById(R.id.population);
        city = findViewById(R.id.city);
        country = findViewById(R.id.country);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.e("ID", "Data size" + id);
                CountryDataDB countryDataDB = mDb.countryDao().loadPersonById(id);
                if (countryDataDB != null) {
                    String latlong1 = countryDataDB.getLatlng().replace("[", "");
                    String latlong2 = latlong1.replace("]", "");
                    String[] latlong = latlong2.split(",");
                    lat = latlong[0].trim();
                    lng = latlong[1].trim();
                    Log.e("MAP LAT LNG", lat + " , " + lng);
                    url = countryDataDB.getFlag();
                    population.setText(""+countryDataDB.getPopulation());
                    city.setText(countryDataDB.getCity());
                    country.setText(countryDataDB.getName());
                    Apicall(lat,lng);
                }
            }
        });
        GlideToVectorYou
                .init()
                .with(this)
                .setPlaceHolder(R.drawable.ic_action_cloud,R.drawable.ic_action_cloud)
                .load(Uri.parse(url), edit_Image);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng pinlocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        googleMap.addMarker(new MarkerOptions().position(pinlocation).title(lat+","+lng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(pinlocation));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pinlocation, 8));
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
                //Log.e(TAG, ""+response.raw().code());
                //Log.e(TAG, ""+response.raw().request().url());
                //Log.e(TAG, ""+response.raw().request().toString());
                //Log.e(TAG, ""+response.body().toString());
                forecastData = response.body();
                values.clear();
               // Log.e(TAG, "Data size" + forecastData.getCnt());
                for (int i = 0; i < forecastData.getList().size(); i++) {
                    try {
                        //Log.e(TAG, "Data size 3" + ""+forecastData.getList().get(i).getMain().getTemp());
                        DecimalFormat df = new DecimalFormat("#.##");
                        String formattedtemp = df.format(forecastData.getList().get(i).getMain().getTemp());
                        float val = Float.parseFloat(formattedtemp);
                        values.add(new Entry(i, val, getResources().getDrawable(R.drawable.cuverd_rect)));
                        //Log.e(TAG, "Data size 2" + val);
                    } catch (Exception e) {
                        Log.e(TAG, ""+e.toString());
                    }
                }

                setChart();
                MapsWeatherAdapter imageAdapter = new MapsWeatherAdapter(MapsActivity.this,MapsActivity.this,forecastData.getList());
                mRecyclerView.setLayoutManager(new LinearLayoutManager(MapsActivity.this, LinearLayoutManager.HORIZONTAL, false));
                mRecyclerView.setAdapter(imageAdapter);

            }
            @Override
            public void onFailure(@NonNull Call<ForecastData> call, @NonNull Throwable t) {
                Log.e(TAG, Objects.requireNonNull(t.getMessage()));
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    public void setChart(){
        setTitle("LineChart");
        {   // // Chart Style // //
            chart = findViewById(R.id.chart1);
            chart.setBackgroundColor(Color.WHITE);
            chart.getDescription().setEnabled(false);

            // enable touch gestures
            chart.setTouchEnabled(true);

            chart.setDrawGridBackground(false);
            chart.getAxisLeft().setDrawGridLines(false);
            chart.getXAxis().setEnabled(false);

            // create marker to display box when values are selected
            MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

            // Set the marker to the chart
            mv.setChartView(chart);
            chart.setMarker(mv);

            // enable scaling and dragging
            chart.setDragEnabled(true);
            chart.setScaleEnabled(true);

            // force pinch zoom along both axis
            chart.setPinchZoom(true);
        }

        YAxis yAxis;
        {   // // Y-Axis Style // //
            yAxis = chart.getAxisLeft();

            // disable dual axis (only use LEFT axis)
            chart.getAxisRight().setEnabled(false);
            // axis range
            yAxis.setAxisMaximum(100f);
            yAxis.setAxisMinimum(-50f);
        }


        {   // // Create Limit Lines // //
            LimitLine llXAxis = new LimitLine(9f, "Index 10");
            llXAxis.setLineWidth(4f);
            llXAxis.enableDashedLine(10f, 10f, 0f);
            llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            llXAxis.setTextSize(10f);
        }


        setData();

        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }
    private void setData() {


        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "Today's Temperature ");

            set1.setDrawIcons(false);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);

            // line thickness and point size
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);

            // draw points as solid circles
            set1.setDrawCircleHole(false);

            // text size of values
            set1.setValueTextSize(9f);

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f);

            // set the filled area
            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);

            // set data
            chart.setData(data);
        }
    }
}