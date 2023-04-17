package nodomain.freeyourgadget.gadgetbridge.service.hsense.client.task;

import static android.view.View.combineMeasuredStates;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import nodomain.freeyourgadget.gadgetbridge.service.hsense.HSenseAuthManager;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.HSenseClient;

public class RegisterTask extends AsyncTask<String, Void, String> {

    private HSenseClient hSenseClient;

    public RegisterTask(Context context) {
        this.hSenseClient = new HSenseClient(context);
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpsURLConnection connection = null;
        try {
            connection = createConnectionWithRegisterData(strings);
           int responseCode = connection.getResponseCode();
            Log.i(TAG, "POST Response Code :: " + responseCode);
            if (responseCode == connection.HTTP_OK) {
                return hSenseClient.getResponseFromEndpoint(connection);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpsURLConnection createConnectionWithRegisterData(String... strings) throws JSONException, IOException {

        List<String> userData = Arrays.stream(strings).collect(Collectors.toList());
        if (!userData.isEmpty()) {
            String username = userData.get(0).toString();
            String password = userData.get(1).toString();
            String repeatedPassword = userData.get(2).toString();
            String emial = userData.get(3).toString();
            return hSenseClient.getRegisterConnection(username, password, repeatedPassword, emial);
        }

    private String getResponse(HttpsURLConnection connection) throws IOException, JSONException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

    }
}
