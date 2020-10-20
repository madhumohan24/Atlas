package com.zoho.atlas.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.zoho.atlas.model.CountryDataDB;
import com.zoho.atlas.R;
import com.zoho.atlas.activity.ListActivity;
import com.zoho.atlas.activity.MapsActivity;
import com.zoho.atlas.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<CountryDataDB> mCountryList;
    ListActivity activity;
    List<CountryDataDB> arraylist =  new ArrayList<>();
    public CountryAdapter(Context context, ListActivity activity) {
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_country, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.countryname.setText(arraylist.get(i).getName());
        myViewHolder.captial.setText(arraylist.get(i).getCity());
        GlideToVectorYou
                .init()
                .with(context)
                .setPlaceHolder(R.drawable.ic_action_cloud,R.drawable.ic_action_cloud)
                .load(Uri.parse(arraylist.get(i).getFlag()),myViewHolder.flag);
    }

    @Override
    public int getItemCount() {
        if (arraylist == null) {
            return 0;
        }
        return arraylist.size();

    }

    public void setTasks(List<CountryDataDB> personList) {
        mCountryList = personList;
        arraylist = new ArrayList<>(personList);
        notifyDataSetChanged();
    }

    public List<CountryDataDB> getTasks() {

        return mCountryList;
    }

    @Override
    public Filter getFilter() {


        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    arraylist = mCountryList;
                } else {
                    List<CountryDataDB> filteredList = new ArrayList<>();
                    for (CountryDataDB row : mCountryList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    arraylist = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = arraylist;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                arraylist = (ArrayList<CountryDataDB>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView countryname, captial;
        ImageView flag;
        AppDatabase mDb;
        CardView cardView;
        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            mDb = AppDatabase.getInstance(context);
            countryname = itemView.findViewById(R.id.tv_countryname);
            captial = itemView.findViewById(R.id.tv_capital);
            cardView = itemView.findViewById(R.id.card);
            flag = itemView.findViewById(R.id.iv_flag);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int elementId = arraylist.get(getAdapterPosition()).getId();
                    Intent i = new Intent(context, MapsActivity.class);
                    i.putExtra("id", elementId);
                    context.startActivity(i);
                }
            });
        }
    }
    public void updatelist(){
        arraylist.clear();
        arraylist = mCountryList;
        notifyDataSetChanged();
    }

}
