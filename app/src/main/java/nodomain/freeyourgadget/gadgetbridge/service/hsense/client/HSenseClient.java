package nodomain.freeyourgadget.gadgetbridge.service.hsense.client;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import nodomain.freeyourgadget.gadgetbridge.database.HSenseDataExporter;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.HSenseAuthManager;

public class HSenseClient {

    public Context context;
    public final String hSenseUrl = "https://hsense.lovemyiot.org";
    private String TAG = "HSenseClient";

    public HSenseClient(Context context) {
        this.context = context;
    }

    private HttpsURLConnection prepareConnection(URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        return connection;
    }

    public HttpsURLConnection prepareConnectionAndExecutePOSTRequest(URL url, JSONObject jsonObject) throws IOException {

        HttpsURLConnection connection = prepareConnection(url);
        OutputStream os = connection.getOutputStream();
        os.write(jsonObject.toString().getBytes());
        os.flush();
        os.close();
        return connection;
    }

    private HttpsURLConnection postDataToServer(URL url, List<JSONObject> jsonObject, String jwt) throws IOException {
        HttpsURLConnection connection = prepareConnection(url);
        connection.setRequestProperty("Authorization", "Bearer " + jwt);

        OutputStream os = connection.getOutputStream();
        os.write(jsonObject.toString().getBytes());
        os.flush();
        os.close();
        return connection;
    }

    public String getResponseFromEndpoint(HttpsURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;

        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return in.toString();
    }

    private JSONObject getLoginData(String username, String password) throws MalformedURLException, JSONException {
        return new JSONObject()
                .put("username", username)
                .put("password", password);
    }

    private JSONObject getRegisterData(String username, String password, String reaptedPassword, String emial) throws MalformedURLException, JSONException {
        URL registerEndpoint = new URL(hSenseUrl + "/register");
        return new JSONObject()
                .put("username", username)
                .put("password", password)
                .put("repeated-password", reaptedPassword)
                .put("email", emial);
    }

    public HttpsURLConnection getLoginConnection(String username, String password) throws IOException, JSONException {
        JSONObject loginData = getLoginData(username, password);
        URL loginEndpoint = new URL(hSenseUrl + "/login");
        return prepareConnectionAndExecutePOSTRequest(loginEndpoint, loginData);
    }

    public HttpsURLConnection getRegisterConnection(String username, String password, String reaptedPassword, String emial) throws IOException, JSONException {
        JSONObject registerData = getRegisterData(username, password, reaptedPassword, emial);
        URL registerEndpoint = new URL(hSenseUrl + "/register");
        return prepareConnectionAndExecutePOSTRequest(registerEndpoint, registerData);
    }

    public HttpsURLConnection sentDataConnection(String jwt, List<JSONObject> miBandData) throws IOException, JSONException {
        URL loginEndpoint = new URL(hSenseUrl + "/hsense/save");
        return postDataToServer(loginEndpoint, miBandData, jwt);
    }
}
