package com.zoho.atlas.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "country")
public class CountryDataDB {
    @PrimaryKey(autoGenerate = true)
    int id;
    String name;
    String city;
    int population;
    String latlng;
    String flag;

    @Ignore
    public CountryDataDB(String name, String city, int population, String latlng, String flag) {
        this.name = name;
        this.city = city;
        this.population = population;
        this.latlng = latlng;
        this.flag = flag;
    }

    public CountryDataDB(int id, String name, String city, int population, String latlng, String flag) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.population = population;
        this.latlng = latlng;
        this.flag = flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
