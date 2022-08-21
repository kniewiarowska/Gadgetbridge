package nodomain.freeyourgadget.gadgetbridge.service.mqtt;

import static android.bluetooth.BluetoothProfile.GATT;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Properties;
import java.util.Timer;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ch.qos.logback.classic.net.SocketReceiver;

public class MqttConnection {
    private static SharedPreferences sharedPreferences;
    private int interation = 0;
    private String lastTimeStamp = "0";
    private final int MINUTES = 60;
    public Timer timer = new Timer();
    //private final List<BluetoothDevice> connectedDevices;
    private MqttAndroidClient mqttAndroidClient;
    private final String MQTT_BROKER_URI = "ssl://mqtt.tele.pw.edu.pl:8883";
    private final String clientId = MqttClient.generateClientId() ;
    private final String username = "kniewiarowska";
    private final String password = "123456";

    public MqttConnection(final Context context, final String action) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mqttAndroidClient = new MqttAndroidClient(
                context, MQTT_BROKER_URI, clientId);
        //BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        //connectedDevices = manager.getConnectedDevices(GATT);
        mqttAndroidClient.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable throwable) {
                Log.d("MQTT", ":connection lost");
//                if(action == "steps"){
//                    publishSteps(context);
//                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.w("MQTT", ":message arrived");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.w("MQTT", ":delivery complete");
            }
        });

        //connect(context, action);
    }

    public void connect(final Context context, final String action){
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();

        mqttConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttConnectOptions.setKeepAliveInterval(3600);

        Properties properties = new Properties();

        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            mqttConnectOptions.setSocketFactory(sc.getSocketFactory());
            mqttConnectOptions.setSSLProperties(properties);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT: success", String.valueOf(interation));
                    if(action == "steps"){
                        //publishSteps(context);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.v("MQTT", "Failed to connect to:" + MQTT_BROKER_URI + exception.toString());
                    if (interation < 10) {
                        connect(context, action);
                        interation++;
                    }
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void publish(String topic, String message){
        try {
            mqttAndroidClient.publish(topic, message.getBytes(), 0, false);
        } catch(MqttException mqttException){
            mqttException.printStackTrace();
        }
    }

//    private void publishSteps(Context context){
//        exportDB(context);
//        lastTimeStamp = sharedPreferences.getString("lastTimestamp", 0);
//        myDBHandler db = new myDBHandler(context, null, null, 1);
//        Cursor cursorStep = db.getSteps(lastTimeStamp);
//        String topic = "kniewiarrowska/miband/" + connectedDevices.toString() + "/steps";
//        String message = null;
//
//        if(cursorSteps.getCount() != 0) {
//            while (cursorStep.moveToNext()) {
//                Log.d(null, cursorStep.getString(0) + " " + cursorStep.getString(1));
//                try {
//                    message = new JSONObject()
//                            .put("deviceId", connectedDevices.toString())
//                            .put("steps", cursorStep.getString(1))
//                            .put("timestamp", cursorStep.getString(0)).toString();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                publish(topic, message);
//
//            }
//        }
//    }

    public void setCallback(MqttCallback callback){
        mqttAndroidClient.setCallback(callback);
    }

    public void exportDB(Context context) {

    }

    public  void exportShared(){

    }


}
