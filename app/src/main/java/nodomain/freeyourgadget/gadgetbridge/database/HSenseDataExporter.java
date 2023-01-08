package nodomain.freeyourgadget.gadgetbridge.database;
import static nodomain.freeyourgadget.gadgetbridge.GBApplication.DATABASE_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySampleDao;

public class HSenseDataExporter {

    private DBOpenHelper dbOpenHelper;
    private SharedPreferences sharedPreferences;
    public static final String DATABASE_NAME = "Gadgetbridge";

    public HSenseDataExporter(Context context) {
        dbOpenHelper = new DBOpenHelper(context, DATABASE_NAME, null);
        sharedPreferences = sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public List<JSONObject> getDataToPublish() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String lastTimestamp = sharedPreferences.getString("lastTimestamp", "0");
        Cursor cursor = getData(lastTimestamp);
        Log.d("DATABASE: content", String.valueOf(cursor.getCount()));
        List<JSONObject> data = new ArrayList<>();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {

                try {
                    JSONObject jsonObject = new JSONObject()
                            .put("timestamp", cursor.getString(0))
                            .put("device_id", cursor.getString(1))
                            .put("user_id", cursor.getString(2))
                            .put("raw_intensity", cursor.getString(3))
                            .put("steps", cursor.getString(4))
                            .put("heart_rate", cursor.getString(5));

                    Log.d("CURSOR content:", jsonObject.toString());

                    data.add(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }


    private Cursor getData(String timestamp) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                MiBandActivitySampleDao.TABLENAME
        };

        String selection = MiBandActivitySampleDao.Properties.Timestamp.columnName + " < ?";
        String[] selectionArgs = {timestamp};


        Cursor cursor = db.rawQuery("SELECT * FROM " + MiBandActivitySampleDao.TABLENAME, null);
//        db.query(
//                MiBandActivitySampleDao.Properties.Steps.columnName,   // The table to query
//                projection,             // The array of columns to return (pass null to get all)
//                selection,              // The columns for the WHERE clause
//                selectionArgs,          // The values for the WHERE clause
//                null,                   // don't group the rows
//                null,                   // don't filter by row groups
//                null    // The sort order
//        );

        return cursor;
    }

}


