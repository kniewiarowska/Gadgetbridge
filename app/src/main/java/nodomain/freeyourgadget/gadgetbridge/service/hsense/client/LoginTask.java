package nodomain.freeyourgadget.gadgetbridge.service.hsense.client;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.net.ssl.HttpsURLConnection;

import nodomain.freeyourgadget.gadgetbridge.activities.HSenseLoginActivity;

public class LoginTask extends AsyncTask<String, Void, String> {

    private HSenseClient hSenseClient;
    private String username;
    private String password;

    public LoginTask(HSenseClient hSenseClient) {
        this.hSenseClient = hSenseClient;
    }

    @Override
    protected String doInBackground(String... strings) {

        HttpsURLConnection connection = null;
        try {
            getUserData(strings); //TODO
            connection = hSenseClient.getLoginConnection(username, password);
            int responseCode = connection.getResponseCode();
            Log.i(TAG, "POST Response Code :: " + responseCode);
            if (responseCode == connection.HTTP_OK) {
                return getJwtToken(connection);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void getUserData(String... strings) {
       List<String> userData = Arrays.stream(strings).collect(Collectors.toList());
       if(!userData.isEmpty()){
           this.username = userData.get(0).toString();
           this.password = userData.get(1).toString();
       }
    }

    @Override
    protected void onPostExecute(String jwtToken) {
        super.onPostExecute(jwtToken);

        if (jwtToken != null) {
            hSenseClient.setJwtToken(username, password, jwtToken);
        } else {
            Log.i(TAG, "POST request did not work.");

        }
   }


    private String getJwtToken(HttpsURLConnection connection) throws IOException, JSONException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONObject reply = new JSONObject(response.toString());
        return reply.get("jwt").toString();
    }


}
