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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import nodomain.freeyourgadget.gadgetbridge.entities.MiBandActivitySampleDao;

public class HSenseDataExporter {

    private DBOpenHelper dbOpenHelper;
    private SharedPreferences sharedPreferences;
    public static final String DATABASE_NAME = "Gadgetbridge";

    public HSenseDataExporter(Context context) {
        dbOpenHelper = new DBOpenHelper(context, DATABASE_NAME, null);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public List<JSONObject> getDataToPublish() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        String lastTimestamp = sharedPreferences.getString("lastTimestamp", "0");
        String currentTimestamp = String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
       currentTimestamp = currentTimestamp.substring(0, currentTimestamp.length()-3);
        Cursor cursor = getData(lastTimestamp, currentTimestamp);
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
           updateSharedPreferances(currentTimestamp);
        }
        return data;
    }

    private void updateSharedPreferances(String currentTimestamp){
        sharedPreferences
                .edit()
                .putString("lastTimestamp", currentTimestamp)
                .commit();
    }

    private Cursor getData(String lastTimestamp, String currentTimestamp) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

        String selection =
                "SELECT * FROM "
                        + MiBandActivitySampleDao.TABLENAME
                        + " WHERE "
                        + MiBandActivitySampleDao.Properties.Timestamp.columnName
                        + " > ? AND "
                        + MiBandActivitySampleDao.Properties.Timestamp.columnName
                        + " <= ? ";

        String[] selectionArgs = new String[] {lastTimestamp, currentTimestamp};
        Cursor cursor = db.rawQuery(selection, selectionArgs);

        return cursor;
    }

}


