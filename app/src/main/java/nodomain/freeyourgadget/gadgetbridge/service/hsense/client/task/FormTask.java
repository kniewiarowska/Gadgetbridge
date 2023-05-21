package nodomain.freeyourgadget.gadgetbridge.service.hsense.client.task;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Lists;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import nodomain.freeyourgadget.gadgetbridge.service.hsense.HSenseAuthManager;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.HSenseClient;

public class FormTask extends AsyncTask<String, Void, String> {

    private HSenseClient hSenseClient;
    private HSenseAuthManager hSenseAuthManager;

    public FormTask(Context context) {
        this.hSenseClient = new HSenseClient(context);
        this.hSenseAuthManager = new HSenseAuthManager(context);
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpsURLConnection connection = null;
        try {
            List<String> userData = Arrays.stream(strings).collect(Collectors.toList());
            if (!userData.isEmpty()) {
                String result = userData.get(0).toString();
                String jwt = userData.get(1).toString();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject
                            .put("timestamp", LocalDateTime.now())
                            .put("user_id", hSenseAuthManager.getUsername())
                            .put("feeling_rate", result);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                sendFormResult(jsonObject, jwt, connection);

            }
            //TODO clean up
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int sendFormResult(JSONObject formResult, String jwt, HttpsURLConnection connection) throws JSONException, IOException {
        int responseCode = 0;
        connection = hSenseClient.sentResultConnection(jwt, formResult);
        responseCode = connection.getResponseCode();
        Log.i(TAG, "POST Response Code :: " + responseCode);

        return responseCode;
    }


}
