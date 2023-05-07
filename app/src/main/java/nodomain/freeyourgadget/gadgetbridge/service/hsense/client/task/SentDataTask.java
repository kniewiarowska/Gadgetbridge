package nodomain.freeyourgadget.gadgetbridge.service.hsense.client.task;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Lists;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import nodomain.freeyourgadget.gadgetbridge.database.HSenseDataExporter;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.HSenseClient;

public class SentDataTask extends AsyncTask<String, Void, Integer> {

    private HSenseClient hSenseClient;
    private HSenseDataExporter hSenseDataExporter;

    public SentDataTask(Context context) {
        this.hSenseClient = new HSenseClient(context);
        this.hSenseDataExporter = new HSenseDataExporter(context);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        HttpsURLConnection connection = null;
        try {
            List<JSONObject> miBandData = hSenseDataExporter.getDataToPublish();
            String jwt = Arrays.stream(strings).findAny().get();
            sendRecords(miBandData, jwt, connection);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int sendRecords(List<JSONObject> collection, String jwt, HttpsURLConnection connection) throws JSONException, IOException {
        List<List<JSONObject>> output = Lists.partition(collection, 100);
        int responseCode = 0;
        for(List<JSONObject> list : output){
            connection = hSenseClient.sentDataConnection(jwt, list);
            responseCode = connection.getResponseCode();
            Log.i(TAG, "POST Response Code :: " + responseCode);
        }
        return responseCode;
    }



}


