package nodomain.freeyourgadget.gadgetbridge.service.hsense.client.task;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

            connection = hSenseClient.sentDataConnection(jwt, miBandData);

            int responseCode = connection.getResponseCode();
            Log.i(TAG, "POST Response Code :: " + responseCode);
            return responseCode;

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
