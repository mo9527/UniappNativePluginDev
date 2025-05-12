package com.wanyi.plugins.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.wanyi.plugins.R;
import com.wanyi.plugins.dao.DeviceOperationLogDao;
import com.wanyi.plugins.dao.PickupCodeDao;
import com.wanyi.plugins.entity.DeviceOperationLog;
import com.wanyi.plugins.entity.PickupCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@androidx.room.Database(version = 2, exportSchema = false,
        entities = {PickupCode.class, DeviceOperationLog.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = "AppDatabase";

    public abstract PickupCodeDao pickupCodeDao();
    public abstract DeviceOperationLogDao deviceOperationLogDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    private static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database.db")
                            .addMigrations(getMigrations(context))
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    Log.i(TAG, "Database执行onCreate方法");
                                    executeInitSql(context, db);
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void executeInitSql(Context context, SupportSQLiteDatabase db){
        executeSqlFromResource(context, db, R.raw.init_sql);
    }

    private static Migration[] getMigrations(Context context) {
        List<Migration> migrations = new ArrayList<>();

        Migration migration1to2 = new Migration(2, 3) {
            @Override
            public void migrate(SupportSQLiteDatabase database) {
                executeSqlFromResource(context, database, R.raw.migration_1_to_2);
            }
        };
//        migrations.add(migration1to2);

        return migrations.toArray(new Migration[0]);
    }

    private static void executeSqlFromResource(Context context, SupportSQLiteDatabase database, int resourceId) {
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sql.append(line).append("\n");
            }
            reader.close();
            inputStream.close();

            String[] statements = sql.toString().split(";");
            Log.i(TAG, "执行数据库升级语句：" + sql);
            for (String statement : statements) {
                statement = statement.trim();
                if (!statement.isEmpty()) {
                    database.execSQL(statement);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute migration script", e);
        }
    }
}
