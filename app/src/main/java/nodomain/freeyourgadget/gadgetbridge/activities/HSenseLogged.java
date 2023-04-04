package nodomain.freeyourgadget.gadgetbridge.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.HSenseClient;

public class HSenseLogged extends AppCompatActivity {

    private Button saveButton;
    private Button logOutButton;
    private HSenseClient hSenseClient;
    private String JwtToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hsense_logged);
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
    }
}