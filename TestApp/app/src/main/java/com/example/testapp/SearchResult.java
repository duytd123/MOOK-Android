package com.example.testapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "search_result")
public class SearchResult {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "search")
    private String search;

    public SearchResult(int id, String search) {
        this.id = id;
        this.search = search;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "id=" + id +
                ", search='" + search + '\'' +
                '}';
    }
}
