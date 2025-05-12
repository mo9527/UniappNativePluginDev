/*
package com.wanyi.plugins.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DatabaseInitialization extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static final String DATABASE_NAME = "app_database.db";
    private static final int DATABASE_VERSION = 2; // 更新版本号

    private static final String upgradeScriptPath = "sql/";
    private final Context mContext;

    // 单例模式
    private static DatabaseInitialization instance;

    private DatabaseInitialization(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context.getApplicationContext();
    }

    public static synchronized DatabaseInitialization init(Context context) {
        if (instance == null) {
            instance = new DatabaseInitialization(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        executeUpgradeScript(db, 1);
    }

    */
/**
     * 数据库升级脚本执行
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     *//*

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 执行从oldVersion到newVersion的每一步升级脚本
        Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        for (int version = oldVersion + 1; version <= newVersion; version++) {
            executeUpgradeScript(db, version);
            Log.i(TAG, "Upgraded database to version " + version);
        }
    }

    // 执行特定版本的升级脚本
    private void executeUpgradeScript(SQLiteDatabase db, int version) {
        String scriptFileName = upgradeScriptPath + "upgrade_v" + version + ".sql";
        try {
            String sqlScript = readSqlScriptFromAssets(scriptFileName);
            if (!sqlScript.trim().isEmpty()) {
                // 分割SQL语句并逐条执行
                Log.i(TAG, "执行数据库升级语句：" + sqlScript);
                String[] sqlStatements = sqlScript.split(";");
                for (String statement : sqlStatements) {
                    statement = statement.trim();
                    if (!statement.isEmpty()) {
                        db.execSQL(statement);
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error execute SQL script: " + e.getMessage());
        }
    }

    // 从assets文件夹读取SQL脚本
    private String readSqlScriptFromAssets(String fileName) throws IOException {
        StringBuilder script = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(mContext.getAssets().open(fileName)));
            String line;
            while ((line = reader.readLine()) != null) {
                script.append(line).append("\n");
            }
            return script.toString();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing reader: " + e.getMessage());
                }
            }
        }
    }

}
*/
