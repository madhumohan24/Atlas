package com.zoho.atlas.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zoho.atlas.keys.key;
import com.zoho.atlas.model.ForecastData;
import com.zoho.atlas.R;
import com.zoho.atlas.activity.MapsActivity;
import com.zoho.atlas.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class MapsWeatherAdapter extends RecyclerView.Adapter<MapsWeatherAdapter.MyViewHolder> {
    private Context context;
    MapsActivity activity;
    private List<ForecastData.forecastlist> mCountryList;

    public MapsWeatherAdapter(Context context, MapsActivity activity, ArrayList<ForecastData.forecastlist> mCountryList) {
        this.context = context;
        this.activity = activity;
        this.mCountryList = mCountryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_weather, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapsWeatherAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.status.setText(mCountryList.get(i).getWeather().get(0).getMain());
        myViewHolder.date.setText(mCountryList.get(i).getDt_txt());
        Glide.with(context).load(key.WEATHER_ICON_URL+mCountryList.get(i).getWeather().get(0).getIcon()+key.PNG_IMGFORMAT).error(R.drawable.ic_action_cloud).into(myViewHolder.icon);
       }

    @Override
    public int getItemCount() {
        if (mCountryList == null) {
            return 0;
        }
        return mCountryList.size();

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView status, date;
        ImageView icon;
        AppDatabase mDb;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            mDb = AppDatabase.getInstance(context);
            status = itemView.findViewById(R.id.tv_weatherstaus);
            date = itemView.findViewById(R.id.tv_date);
            icon = itemView.findViewById(R.id.iv_weathericon);
        }
    }
}
