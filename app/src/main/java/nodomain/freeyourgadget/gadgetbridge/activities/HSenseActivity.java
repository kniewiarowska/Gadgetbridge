package nodomain.freeyourgadget.gadgetbridge.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
    private LinearLayout linearLayout;
    private ImageView feelButton1;
    private ImageView feelButton2;
    private ImageView feelButton3;
    private ImageView feelButton4;
    private ImageView feelButton5;
    private int feelingResult = 0;
    private List<ImageView> formButtons = new ArrayList<>();
    private ImageView chosenResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hsense);
        loginStatus = findViewById(R.id.login_status);
        saveButton = findViewById(R.id.save_button);
        logOutButton = findViewById(R.id.logout_button);
        sentStatus = findViewById(R.id.last_status);
        linearLayout = findViewById(R.id.fellForm);

        chosenResult = null;
        feelButton1 = findViewById(R.id.fellButton1);
        feelButton2 = findViewById(R.id.fellButton2);
        feelButton3 = findViewById(R.id.fellButton3);
        feelButton4 = findViewById(R.id.fellButton4);
        feelButton5 = findViewById(R.id.fellButton5);

        formButtons.add(feelButton1);
        formButtons.add(feelButton2);
        formButtons.add(feelButton3);
        formButtons.add(feelButton4);
        formButtons.add(feelButton5);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        hSenseClient = new HSenseClient(this.getApplicationContext());
        hSenseAuthManager = new HSenseAuthManager(this.getApplicationContext());
        hSenseSendDataService = new HSenseSendDataService(this.getApplicationContext());

        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                hSenseSendDataService.sentData();
                hSenseSendDataService.sendFeelingRate(feelingResult);
                recreate();
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hSenseAuthManager.logOut();
                finish();
            }
        });

        formButtons.forEach(button -> button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (chosenResult == null) {
                            setTheResult(formButtons.indexOf(button), button);
                        } else {
                            resetButtons();
                        }
                    }
                }
        ));


        setLoginStatusText();
        setSentStatusText();
    }

    private void setTheResult(int result, ImageView chosenImage) {
        this.feelingResult = result + 1;
        this.chosenResult = chosenImage;
        formButtons.stream()
                .filter(feelButton -> feelButton != chosenImage)
                .forEach(feelButton -> {
                    feelButton.setClickable(false);
                    feelButton.setBackgroundResource(android.R.color.darker_gray);
                });
        chosenImage.setImageResource(R.drawable.check_px);
        saveButton.setEnabled(true);
    }

    private void resetButtons() {
        chosenResult = null;
        feelingResult = 0;
        feelButton1.setImageResource(R.drawable.ic_1_sentiment_sad_px);
        feelButton2.setImageResource(R.drawable.ic_2_mood_bad_px);
        feelButton3.setImageResource(R.drawable.ic_3_sentiment_neutral_px);
        feelButton4.setImageResource(R.drawable.ic_4_mood_px);
        feelButton5.setImageResource(R.drawable.ic_5_sentiment_excited_px);

        feelButton1.setBackgroundResource(R.color.feel_color1);
        feelButton2.setBackgroundResource(R.color.feel_color2);
        feelButton3.setBackgroundResource(R.color.feel_color3);
        feelButton4.setBackgroundResource(R.color.feel_color4);
        feelButton5.setBackgroundResource(R.color.feel_color5);

        formButtons.forEach(button -> button.setClickable(true));
        saveButton.setEnabled(false);
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
        sentStatus.setText("Last update " + sharedPreferences.getString("lastTimestamp", "0"));
    }


}