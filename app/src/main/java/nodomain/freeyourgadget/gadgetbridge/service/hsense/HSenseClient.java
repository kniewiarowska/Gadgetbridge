package nodomain.freeyourgadget.gadgetbridge.service.hsense;

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

    public final String hSenseUrl = "https://hsense.lovemyiot.org";
    public HSenseDataExporter hSenseDataExporter;

    public void register(String username, String password, String reaptedPassword, String emial) {

        try {
            URL hSenseEndpoint = new URL(hSenseUrl+ "/register");
            JSONObject registerObject = new JSONObject()
                    .put("username", username)
                    .put("password", password)
                    .put("repeated-password", reaptedPassword  )
                    .put("email", emial);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpsURLConnection connection =
                                (HttpsURLConnection) hSenseEndpoint.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        OutputStream os = connection.getOutputStream();
                        os.write(registerObject.toString().getBytes());
                        os.flush();
                        os.close();

                        int responseCode = connection.getResponseCode();
                        System.out.println("POST Response Code :: " + responseCode);
                        if (responseCode ==  connection.HTTP_OK) { //success
                            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String inputLine;
                            StringBuffer response = new StringBuffer();

                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();

                            // print result
                            System.out.println(response.toString());

                        } else {
                            System.out.println("POST request did not work.");

                        }

                        //TODO return toast result + print response from server
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public void login(String username, String password ) {

        try {
            URL hSenseEndpoint = new URL(hSenseUrl+ "/login");
            List<JSONObject> dataObject = hSenseDataExporter.getDataToPublish();

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpsURLConnection connection =
                                (HttpsURLConnection) hSenseEndpoint.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        OutputStream os = connection.getOutputStream();
                        os.write(dataObject.toString().getBytes());
                        os.flush();
                        os.close();

                        int responseCode = connection.getResponseCode();
                        System.out.println("POST Response Code :: " + responseCode);
                        if (responseCode ==  connection.HTTP_OK) { //success
                            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String inputLine;
                            StringBuffer response = new StringBuffer();

                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();
                            //TODO save jwt
                            // print result
                            System.out.println(response.toString());

                        } else {
                            System.out.println("POST request did not work.");

                        }

                        //TODO return toast result + print response from server
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

}
