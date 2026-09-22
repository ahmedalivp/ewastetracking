package com.ewaste.app.data.local.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile DropOffPointDao _dropOffPointDao;

  private volatile SubmissionDao _submissionDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `cached_drop_off_points` (`id` INTEGER, `label` TEXT, `lat` REAL, `lng` REAL, `facilityId` INTEGER, `facilityName` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `cached_submissions` (`id` INTEGER, `categoryName` TEXT, `deviceDescription` TEXT, `dropOffPointLabel` TEXT, `status` TEXT, `submittedAt` TEXT, `componentCount` INTEGER NOT NULL, `hazardousMaterialCount` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c001c2b7084c317383a5db1081e55e4d')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `cached_drop_off_points`");
        db.execSQL("DROP TABLE IF EXISTS `cached_submissions`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsCachedDropOffPoints = new HashMap<String, TableInfo.Column>(6);
        _columnsCachedDropOffPoints.put("id", new TableInfo.Column("id", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedDropOffPoints.put("label", new TableInfo.Column("label", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedDropOffPoints.put("lat", new TableInfo.Column("lat", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedDropOffPoints.put("lng", new TableInfo.Column("lng", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedDropOffPoints.put("facilityId", new TableInfo.Column("facilityId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedDropOffPoints.put("facilityName", new TableInfo.Column("facilityName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCachedDropOffPoints = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCachedDropOffPoints = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCachedDropOffPoints = new TableInfo("cached_drop_off_points", _columnsCachedDropOffPoints, _foreignKeysCachedDropOffPoints, _indicesCachedDropOffPoints);
        final TableInfo _existingCachedDropOffPoints = TableInfo.read(db, "cached_drop_off_points");
        if (!_infoCachedDropOffPoints.equals(_existingCachedDropOffPoints)) {
          return new RoomOpenHelper.ValidationResult(false, "cached_drop_off_points(com.ewaste.app.data.local.db.CachedDropOffPoint).\n"
                  + " Expected:\n" + _infoCachedDropOffPoints + "\n"
                  + " Found:\n" + _existingCachedDropOffPoints);
        }
        final HashMap<String, TableInfo.Column> _columnsCachedSubmissions = new HashMap<String, TableInfo.Column>(8);
        _columnsCachedSubmissions.put("id", new TableInfo.Column("id", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedSubmissions.put("categoryName", new TableInfo.Column("categoryName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedSubmissions.put("deviceDescription", new TableInfo.Column("deviceDescription", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedSubmissions.put("dropOffPointLabel", new TableInfo.Column("dropOffPointLabel", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedSubmissions.put("status", new TableInfo.Column("status", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedSubmissions.put("submittedAt", new TableInfo.Column("submittedAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedSubmissions.put("componentCount", new TableInfo.Column("componentCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedSubmissions.put("hazardousMaterialCount", new TableInfo.Column("hazardousMaterialCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCachedSubmissions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCachedSubmissions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCachedSubmissions = new TableInfo("cached_submissions", _columnsCachedSubmissions, _foreignKeysCachedSubmissions, _indicesCachedSubmissions);
        final TableInfo _existingCachedSubmissions = TableInfo.read(db, "cached_submissions");
        if (!_infoCachedSubmissions.equals(_existingCachedSubmissions)) {
          return new RoomOpenHelper.ValidationResult(false, "cached_submissions(com.ewaste.app.data.local.db.CachedSubmission).\n"
                  + " Expected:\n" + _infoCachedSubmissions + "\n"
                  + " Found:\n" + _existingCachedSubmissions);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "c001c2b7084c317383a5db1081e55e4d", "8747e992d3dfc3a1e14a4020531233c8");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "cached_drop_off_points","cached_submissions");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `cached_drop_off_points`");
      _db.execSQL("DELETE FROM `cached_submissions`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(DropOffPointDao.class, DropOffPointDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SubmissionDao.class, SubmissionDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public DropOffPointDao dropOffPointDao() {
    if (_dropOffPointDao != null) {
      return _dropOffPointDao;
    } else {
      synchronized(this) {
        if(_dropOffPointDao == null) {
          _dropOffPointDao = new DropOffPointDao_Impl(this);
        }
        return _dropOffPointDao;
      }
    }
  }

  @Override
  public SubmissionDao submissionDao() {
    if (_submissionDao != null) {
      return _submissionDao;
    } else {
      synchronized(this) {
        if(_submissionDao == null) {
          _submissionDao = new SubmissionDao_Impl(this);
        }
        return _submissionDao;
      }
    }
  }
}
