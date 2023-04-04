package nodomain.freeyourgadget.gadgetbridge.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.HSenseClient;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.HSenseService;

public class HSenseActivity extends AppCompatActivity {
    private TextView loginStatus;
    private EditText loginEdit;
    private EditText passwordEdit;
    private Button loginButton;
    private Button registerButton;
    private String JwtToken;
    private HSenseClient hSenseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hsense);
        loginStatus = findViewById(R.id.login_status);
        loginEdit = findViewById(R.id.login_edit);
        passwordEdit = findViewById(R.id.password_edit);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        hSenseClient = new HSenseClient(this.getApplicationContext());

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String login = String.valueOf(loginEdit.getText());
                String password = String.valueOf(passwordEdit.getText());
                startNextActivity(login, password);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    public void startNextActivity(String login, String password) {
        Integer status = hSenseClient.login(login, password);
        //if (status != null && status == 200) {
            Intent myIntent = new Intent(HSenseActivity.this, HSenseLogged.class);
            //myIntent.putExtra("key", value); //Optional parameters
            startActivity(myIntent);
           // finish();


        //}

    }



}