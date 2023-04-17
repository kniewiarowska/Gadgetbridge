package nodomain.freeyourgadget.gadgetbridge.service.hsense.client;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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

public class RegisterTask extends AsyncTask<String, Void, String> {

    private HSenseClient hSenseClient;
    private String username;
    private String password;
    private String repeatedPassword;
    private String emial;

    @Override
    protected String doInBackground(String... strings) {
        HttpsURLConnection connection = null;
        try {
            getUserData(strings); //TODO
            connection = hSenseClient.getRegisterConnection(username, password, repeatedPassword, emial);
            int responseCode = connection.getResponseCode();
            Log.i(TAG, "POST Response Code :: " + responseCode);
            if (responseCode == connection.HTTP_OK) {
                return getResponse(connection);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getUserData(String... strings) {
        List<String> userData = Arrays.stream(strings).collect(Collectors.toList());
        if (!userData.isEmpty()) {
            this.username = userData.get(0).toString();
            this.password = userData.get(1).toString();
            this.repeatedPassword = userData.get(2).toString();
            this.emial = userData.get(3).toString();
        }
    }

    private String getResponse(HttpsURLConnection connection) throws IOException, JSONException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
