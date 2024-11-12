package com.example.testapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface SearchResultDAO {
    @Insert
    void insertSearchResult(SearchResult searchResult);

    @Insert
    void insertListSearchResult(List<SearchResult> searchResultList);

    @Delete
    void deleteSearchResult(SearchResult searchResult);

    @Update
    void updateSearchResult(SearchResult searchResult);

    @Query("SELECT * FROM search_result")
    List<SearchResult> getAll();
}
