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
public final class SubmissionDao_Impl implements SubmissionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CachedSubmission> __insertionAdapterOfCachedSubmission;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public SubmissionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCachedSubmission = new EntityInsertionAdapter<CachedSubmission>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `cached_submissions` (`id`,`categoryName`,`deviceDescription`,`dropOffPointLabel`,`status`,`submittedAt`,`componentCount`,`hazardousMaterialCount`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final CachedSubmission entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindLong(1, entity.getId());
        }
        if (entity.getCategoryName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getCategoryName());
        }
        if (entity.getDeviceDescription() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDeviceDescription());
        }
        if (entity.getDropOffPointLabel() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDropOffPointLabel());
        }
        if (entity.getStatus() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getStatus());
        }
        if (entity.getSubmittedAt() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getSubmittedAt());
        }
        statement.bindLong(7, entity.getComponentCount());
        statement.bindLong(8, entity.getHazardousMaterialCount());
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM cached_submissions";
        return _query;
      }
    };
  }

  @Override
  public void insertAll(final List<CachedSubmission> submissions) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfCachedSubmission.insert(submissions);
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
  public List<CachedSubmission> getAll() {
    final String _sql = "SELECT * FROM cached_submissions ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfCategoryName = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryName");
      final int _cursorIndexOfDeviceDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceDescription");
      final int _cursorIndexOfDropOffPointLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "dropOffPointLabel");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final int _cursorIndexOfSubmittedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "submittedAt");
      final int _cursorIndexOfComponentCount = CursorUtil.getColumnIndexOrThrow(_cursor, "componentCount");
      final int _cursorIndexOfHazardousMaterialCount = CursorUtil.getColumnIndexOrThrow(_cursor, "hazardousMaterialCount");
      final List<CachedSubmission> _result = new ArrayList<CachedSubmission>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final CachedSubmission _item;
        _item = new CachedSubmission();
        final Long _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getLong(_cursorIndexOfId);
        }
        _item.setId(_tmpId);
        final String _tmpCategoryName;
        if (_cursor.isNull(_cursorIndexOfCategoryName)) {
          _tmpCategoryName = null;
        } else {
          _tmpCategoryName = _cursor.getString(_cursorIndexOfCategoryName);
        }
        _item.setCategoryName(_tmpCategoryName);
        final String _tmpDeviceDescription;
        if (_cursor.isNull(_cursorIndexOfDeviceDescription)) {
          _tmpDeviceDescription = null;
        } else {
          _tmpDeviceDescription = _cursor.getString(_cursorIndexOfDeviceDescription);
        }
        _item.setDeviceDescription(_tmpDeviceDescription);
        final String _tmpDropOffPointLabel;
        if (_cursor.isNull(_cursorIndexOfDropOffPointLabel)) {
          _tmpDropOffPointLabel = null;
        } else {
          _tmpDropOffPointLabel = _cursor.getString(_cursorIndexOfDropOffPointLabel);
        }
        _item.setDropOffPointLabel(_tmpDropOffPointLabel);
        final String _tmpStatus;
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _tmpStatus = null;
        } else {
          _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
        }
        _item.setStatus(_tmpStatus);
        final String _tmpSubmittedAt;
        if (_cursor.isNull(_cursorIndexOfSubmittedAt)) {
          _tmpSubmittedAt = null;
        } else {
          _tmpSubmittedAt = _cursor.getString(_cursorIndexOfSubmittedAt);
        }
        _item.setSubmittedAt(_tmpSubmittedAt);
        final int _tmpComponentCount;
        _tmpComponentCount = _cursor.getInt(_cursorIndexOfComponentCount);
        _item.setComponentCount(_tmpComponentCount);
        final int _tmpHazardousMaterialCount;
        _tmpHazardousMaterialCount = _cursor.getInt(_cursorIndexOfHazardousMaterialCount);
        _item.setHazardousMaterialCount(_tmpHazardousMaterialCount);
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
