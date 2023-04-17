package nodomain.freeyourgadget.gadgetbridge.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.HSenseAuthManager;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.HSenseSendDataService;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.HSenseClient;

public class HSenseActivity extends AppCompatActivity {
    private HSenseAuthManager hSenseAuthManager;
    private TextView sentStatus;
    private TextView loginStatus;
    private Button saveButton;
    private Button logOutButton;
    private HSenseClient hSenseClient;
    private String JwtToken;
    private HSenseSendDataService hSenseSendDataService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hsense);
        loginStatus = findViewById(R.id.login_status);
        saveButton = findViewById(R.id.save_button);
        logOutButton = findViewById(R.id.logout_button);
        sentStatus = findViewById(R.id.last_sent_status);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        hSenseClient = new HSenseClient(this.getApplicationContext());
        hSenseAuthManager = new HSenseAuthManager(this.getApplicationContext());
        hSenseSendDataService = new HSenseSendDataService(this.getApplicationContext());

        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Integer integer = hSenseSendDataService.sentData();
                if(integer == 200){
                    //TODO success
                } else {
                    //TODO DUPA
                }
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hSenseAuthManager.logOut();
                finish();
            }
        });

        setLoginStatusText();
        setSentStatusText();
    }

    private void setLoginStatusText() {
        if (hSenseAuthManager.checkIfJwtIsActive()) {
            String login = hSenseAuthManager.getUsername();
            if (login != null) {
                loginStatus.setText("Logged as " + login);
                Log.i("HSense", "Logged as " + login);
                saveButton.setEnabled(true);
                logOutButton.setEnabled(true);
            }
        } else {
            Intent hSenseLoginIntent = new Intent(this, HSenseLoginActivity.class);
            startActivity(hSenseLoginIntent);
        }
    }

    private void setSentStatusText() {
        sentStatus.setText("Last update" +  sharedPreferences.getString("lastTimestamp", "0"));
    }


}