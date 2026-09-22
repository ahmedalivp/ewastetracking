package com.ewaste.app.data.local.db;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Double;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DropOffPointDao_Impl implements DropOffPointDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CachedDropOffPoint> __insertionAdapterOfCachedDropOffPoint;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public DropOffPointDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCachedDropOffPoint = new EntityInsertionAdapter<CachedDropOffPoint>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `cached_drop_off_points` (`id`,`label`,`lat`,`lng`,`facilityId`,`facilityName`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final CachedDropOffPoint entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getLabel() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getLabel());
        }
        if (entity.getLat() == null) {
          statement.bindNull(3);
        } else {
          statement.bindDouble(3, entity.getLat());
        }
        if (entity.getLng() == null) {
          statement.bindNull(4);
        } else {
          statement.bindDouble(4, entity.getLng());
        }
        if (entity.getFacilityId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getFacilityId());
        }
        if (entity.getFacilityName() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getFacilityName());
        }
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM cached_drop_off_points";
        return _query;
      }
    };
  }

  @Override
  public void insertAll(final List<CachedDropOffPoint> points) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfCachedDropOffPoint.insert(points);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void clearAll() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfClearAll.release(_stmt);
    }
  }

  @Override
  public List<CachedDropOffPoint> getAll() {
    final String _sql = "SELECT * FROM cached_drop_off_points";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "label");
      final int _cursorIndexOfLat = CursorUtil.getColumnIndexOrThrow(_cursor, "lat");
      final int _cursorIndexOfLng = CursorUtil.getColumnIndexOrThrow(_cursor, "lng");
      final int _cursorIndexOfFacilityId = CursorUtil.getColumnIndexOrThrow(_cursor, "facilityId");
      final int _cursorIndexOfFacilityName = CursorUtil.getColumnIndexOrThrow(_cursor, "facilityName");
      final List<CachedDropOffPoint> _result = new ArrayList<CachedDropOffPoint>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final CachedDropOffPoint _item;
        _item = new CachedDropOffPoint();
        final Long _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getLong(_cursorIndexOfId);
        }
        _item.setId(_tmpId);
        final String _tmpLabel;
        if (_cursor.isNull(_cursorIndexOfLabel)) {
          _tmpLabel = null;
        } else {
          _tmpLabel = _cursor.getString(_cursorIndexOfLabel);
        }
        _item.setLabel(_tmpLabel);
        final Double _tmpLat;
        if (_cursor.isNull(_cursorIndexOfLat)) {
          _tmpLat = null;
        } else {
          _tmpLat = _cursor.getDouble(_cursorIndexOfLat);
        }
        _item.setLat(_tmpLat);
        final Double _tmpLng;
        if (_cursor.isNull(_cursorIndexOfLng)) {
          _tmpLng = null;
        } else {
          _tmpLng = _cursor.getDouble(_cursorIndexOfLng);
        }
        _item.setLng(_tmpLng);
        final Long _tmpFacilityId;
        if (_cursor.isNull(_cursorIndexOfFacilityId)) {
          _tmpFacilityId = null;
        } else {
          _tmpFacilityId = _cursor.getLong(_cursorIndexOfFacilityId);
        }
        _item.setFacilityId(_tmpFacilityId);
        final String _tmpFacilityName;
        if (_cursor.isNull(_cursorIndexOfFacilityName)) {
          _tmpFacilityName = null;
        } else {
          _tmpFacilityName = _cursor.getString(_cursorIndexOfFacilityName);
        }
        _item.setFacilityName(_tmpFacilityName);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
