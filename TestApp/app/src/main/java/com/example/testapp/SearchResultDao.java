package com.example.testapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface SearchResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertResults(List<SearchResult> results);

    @Query("SELECT * FROM search_results")
    List<SearchResult> getAllResults();

    @Query("DELETE FROM search_results")
    void deleteAll();
}
