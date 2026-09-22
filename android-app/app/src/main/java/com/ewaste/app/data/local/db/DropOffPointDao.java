package com.ewaste.app.data.local.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DropOffPointDao {

    @Query("SELECT * FROM cached_drop_off_points")
    List<CachedDropOffPoint> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CachedDropOffPoint> points);

    @Query("DELETE FROM cached_drop_off_points")
    void clearAll();
}
