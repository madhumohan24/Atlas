package com.zoho.atlas.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.zoho.atlas.model.CountryDataDB;

import java.util.List;

@Dao
public interface CountryDao {

    @Query("SELECT * FROM COUNTRY ORDER BY ID")
    List<CountryDataDB> loadAllPersons();

    @Query("SELECT COUNT(id) FROM COUNTRY")
    int getCount();

    @Insert
    void insertPerson(CountryDataDB country);

    @Query("SELECT * FROM COUNTRY WHERE id = :id")
    CountryDataDB loadPersonById(int id);
}
