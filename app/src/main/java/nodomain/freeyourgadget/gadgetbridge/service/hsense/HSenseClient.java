package nodomain.freeyourgadget.gadgetbridge.service.hsense;

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

public class HSenseClient {

    public Context context;
    public final String hSenseUrl = "https://hsense.lovemyiot.org";
    public HSenseDataExporter hSenseDataExporter;
    public HSenseAuthManager hSenseAuthManager;
    private String TAG = "HSenseClient";

    public HSenseClient(Context context) {
        this.context = context;
        this.hSenseDataExporter = new HSenseDataExporter(context);
        this.hSenseAuthManager = new HSenseAuthManager(context);
    }

    private HttpsURLConnection prepareConnection(URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        return connection;
    }

    private HttpsURLConnection prepareConnectionAndExecutePOSTRequest(URL url, JSONObject jsonObject) throws IOException {

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


    private BufferedReader getResponseFromEndpoint(HttpsURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;

        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return in;
    }

    public void register(String username, String password, String reaptedPassword, String emial) {

        try {
            URL registerEndpoint = new URL(hSenseUrl + "/register");
            JSONObject registerObject = new JSONObject()
                    .put("username", username)
                    .put("password", password)
                    .put("repeated-password", reaptedPassword)
                    .put("email", emial);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpsURLConnection connection = prepareConnectionAndExecutePOSTRequest(registerEndpoint, registerObject);

                        int responseCode = connection.getResponseCode();
                        Log.i("HSenseClient", "POST Response Code :: " + responseCode);

                        if (responseCode == connection.HTTP_OK) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String inputLine;
                            StringBuffer response = new StringBuffer();

                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }

                            in.close();
                        } else {
                            Log.i(TAG, "POST request did not work.");
                            //TODO throw exception when request did not work
                            //TODO print response from server
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public Integer login(String username, String password) {

        final Integer[] responseCodeValue = new Integer[1];

        try {
            URL loginEndpoint = new URL(hSenseUrl + "/login");
            JSONObject dataObject = new JSONObject()
                    .put("username", username)
                    .put("password", password);

            AsyncTask.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        HttpsURLConnection connection = prepareConnectionAndExecutePOSTRequest(loginEndpoint, dataObject);

                        int responseCode = connection.getResponseCode();
                        Log.i(TAG, "POST Response Code :: " + responseCode);

                        if (responseCode == connection.HTTP_OK) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String inputLine;
                            StringBuffer response = new StringBuffer();

                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }

                            in.close();
                            JSONObject reply = new JSONObject(response.toString());
                            String jwt = reply.get("jwt").toString();
                            hSenseAuthManager.setUpAuthData(username, password, jwt);

                        } else {
                            Log.i(TAG, "POST request did not work.");
                        }
                        responseCodeValue[0] = responseCode;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (MalformedURLException | JSONException e) {
            e.printStackTrace();
        }

        return responseCodeValue[0];
    }

    private boolean checkIfTokenIsActive() {
        return hSenseAuthManager.checkIfJwtIsActive();
    }

    private String getActiveJwtToken() {
        if (!checkIfTokenIsActive()) {
            updateJwtManager();
            Log.i("HSenseClient","Token not active");
        } else {
            Log.i("HSenseClient","Token is active");

        }
        return hSenseAuthManager.getJwtToken();
    }

    private void updateJwtManager() {
        String username = hSenseAuthManager.getUsername();
        String password = hSenseAuthManager.getPassword();

        Integer resonCode = login(username, password);
        if (resonCode != null && resonCode == 200) {
            Log.i("HSenseClient", "Update sucessfull");
        } else {
            //TODO throw exception
        }
    }

    public Integer save() {
        final Integer[] responseCodeValue = new Integer[1];

        try {
            URL loginEndpoint = new URL(hSenseUrl + "/hsense/save");
            getActiveJwtToken();
            List<JSONObject> dataObject = hSenseDataExporter.getDataToPublish();
            AsyncTask.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        HttpsURLConnection connection = postDataToServer(loginEndpoint, dataObject, hSenseAuthManager.getJwtToken());
                        int responseCode = connection.getResponseCode();
                        Log.i(TAG, "POST Response Code :: " + responseCode);

                        if (responseCode == connection.HTTP_OK) {
                            BufferedReader in = getResponseFromEndpoint(connection);
                        } else {
                            Log.i(TAG, "POST request did not work.");
                        }

                        responseCodeValue[0] = responseCode;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return responseCodeValue[0];
    }

}
