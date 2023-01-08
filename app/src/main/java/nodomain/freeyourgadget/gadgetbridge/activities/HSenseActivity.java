package nodomain.freeyourgadget.gadgetbridge.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.HSenseClient;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.HSenseService;

public class HSenseActivity extends AppCompatActivity {

    private EditText loginEdit;
    private EditText passwordEdit;
    private Button loginButton;
    private Button registerButton;
    private HSenseClient hSenseClient;
    private String JwtToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hsense);

        loginEdit = findViewById(R.id.login_edit);
        passwordEdit = findViewById(R.id.password_edit);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        hSenseClient = new HSenseClient(this.getApplicationContext());

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String login = String.valueOf(loginEdit.getText());
                String password = String.valueOf(passwordEdit.getText());

                Integer status = hSenseClient.login(login, password);

                String toastMessage = "Login failed!";
                if(status != null && status == 200) {
                     toastMessage =  "Login sucessfull!";
                }

                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer status = hSenseClient.save();

                String toastMessage = "Login failed!";
                if(status != null && status == 200) {
                    toastMessage =  "Login sucessfull!";
                }
            }
        });

    }




}