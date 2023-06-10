package nodomain.freeyourgadget.gadgetbridge.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.HSenseClient;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.task.RegisterTask;

public class RegisterHSenseActivity extends AppCompatActivity {

    private RegisterTask registerTask;
    private HSenseClient hSenseClient;
    private EditText loginEdit;
    private EditText passwordEdit;
    private EditText password2Edit;
    private EditText emailEdit;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hSenseClient = new HSenseClient(getApplicationContext());
        setContentView(R.layout.activity_register);

        loginEdit = findViewById(R.id.login_register);
        passwordEdit = findViewById(R.id.password_register);
        password2Edit = findViewById(R.id.password_register_repeat);
        emailEdit = findViewById(R.id.email_register);
        registerButton = findViewById(R.id.register_save_button);

        registerTask = new RegisterTask(this.getApplicationContext());

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String login = String.valueOf(loginEdit.getText());
                String password = String.valueOf(passwordEdit.getText());
                String password2 = String.valueOf(password2Edit.getText());
                String email = String.valueOf(emailEdit.getText());

                String message = "";

                if (validateForm()) {
                    try {
                        message = registerTask.execute(login, password, password2, email).get();
                        Toast.makeText(hSenseClient.context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if(message == "Registered with success!"){
                    finish();
                }
              }
            });

        }

        private boolean validateForm () {
            //TODO write logic to validate data from form
            return true;
        }

    }
