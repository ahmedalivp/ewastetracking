package com.ewaste.app.data.local.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SubmissionDao {

    @Query("SELECT * FROM cached_submissions ORDER BY id DESC")
    List<CachedSubmission> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CachedSubmission> submissions);

    @Query("DELETE FROM cached_submissions")
    void clearAll();
}
