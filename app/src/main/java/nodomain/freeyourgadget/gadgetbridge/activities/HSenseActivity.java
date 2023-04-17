package nodomain.freeyourgadget.gadgetbridge.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.HSenseClient;

public class HSenseActivity extends AppCompatActivity {
    private TextView loginStatus;
    private Button registerButton;
    private Button saveButton;
    private Button logOutButton;
    private HSenseClient hSenseClient;
    private String JwtToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hsense);
        loginStatus = findViewById(R.id.login_status);
        saveButton = findViewById(R.id.save_button);
        logOutButton = findViewById(R.id.logout_button);

        hSenseClient = new HSenseClient(this.getApplicationContext());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer status = hSenseClient.save();

                if (status != null && status == 200) {
                    Log.i("HSense", "Data sent to server with sucess");
                }
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hSenseClient.hSenseAuthManager.logOut();
            }
        });

        setStatusText();
    }

    private void setStatusText() {
        if (hSenseClient.hSenseAuthManager.checkIfJwtIsActive()) {
            String login = hSenseClient.hSenseAuthManager.getUsername();
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


}