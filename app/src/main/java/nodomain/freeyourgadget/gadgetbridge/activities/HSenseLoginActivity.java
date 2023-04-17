package nodomain.freeyourgadget.gadgetbridge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutionException;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.HSenseClient;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.task.LoginTask;

public class HSenseLoginActivity extends AppCompatActivity {

    private HSenseClient hSenseClient;
    private EditText loginEdit;
    private EditText passwordEdit;
    private Button loginButton;
    private LoginTask loginTask;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hsense_login);
        loginEdit = findViewById(R.id.login_edit);
        passwordEdit = findViewById(R.id.password_edit);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        loginTask = new LoginTask(this.getApplicationContext());

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String login = String.valueOf(loginEdit.getText());
                String password = String.valueOf(passwordEdit.getText());
                String jwt = loginAttempt(login, password);

                if (jwt == null) {
                    Toast.makeText(getApplicationContext(), "Login failed!", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(getApplicationContext(), RegisterHSenseActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    private String loginAttempt(String login, String password) {
        try {
            return loginTask.execute(login, password).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}