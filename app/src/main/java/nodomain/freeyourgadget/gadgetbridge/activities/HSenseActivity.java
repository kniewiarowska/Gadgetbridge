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
    private Button saveButton;
    private Button logOutButton;
    private HSenseClient hSenseClient;
    private String JwtToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hsense);
        loginStatus = findViewById(R.id.login_status);
        loginEdit = findViewById(R.id.login_edit);
        passwordEdit = findViewById(R.id.password_edit);
        loginButton = findViewById(R.id.login_button);
        saveButton = findViewById(R.id.save_button);
        logOutButton = findViewById(R.id.logout_button);
        registerButton = findViewById(R.id.register_button);

        hSenseClient = new HSenseClient(this.getApplicationContext());

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String login = String.valueOf(loginEdit.getText());
                String password = String.valueOf(passwordEdit.getText());

                Integer status = hSenseClient.login(login, password);

                String toastMessage = "Login failed!";
                if (status != null && status == 200) {
                    toastMessage = "Login sucessfull!";
                    setStatusText();
                }

                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT);
            }
        });

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


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerIntent);
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
                loginButton.setEnabled(false);
                registerButton.setEnabled(false);
                loginEdit.setEnabled(false);
                passwordEdit.setEnabled(false);

                //TODO fix

            }
        } else {
            loginStatus.setText("No one is logged. Please log to HSense service!");
            saveButton.setEnabled(false);
            logOutButton.setEnabled(false);
        }
    }


}