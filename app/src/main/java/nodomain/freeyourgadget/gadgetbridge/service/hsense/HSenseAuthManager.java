package nodomain.freeyourgadget.gadgetbridge.service.hsense;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.time.LocalDateTime;

public class HSenseAuthManager {

    private final String JWT = "jwt";
    private final String JWT_TOKEN_DATE = "jwt-date";
    private final String USERNAME = "username";
    private final String PASSWORD = "password";

    SharedPreferences sharedPreferences;

    public HSenseAuthManager(Context context) {
        sharedPreferences = context.getSharedPreferences("MyPref", 0);
    }

    public void setUpAuthData(String username, String password, String jwtToken) {
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        // write all the data entered by the user in SharedPreference and apply
        myEdit.putString(USERNAME, username);
        myEdit.putString(PASSWORD, password);
        myEdit.putString(JWT, jwtToken);
        myEdit.putString(JWT_TOKEN_DATE, LocalDateTime.now().toString());
        myEdit.apply();
        myEdit.commit();
    }

    public String getPassword() {
        return sharedPreferences.getString(PASSWORD, null);
    }

    public String getUsername() {
        return sharedPreferences.getString(USERNAME, null);
    }

    public String getJwtToken() {
        return sharedPreferences.getString(JWT, null);
    }

    public boolean checkIfJwtIsActive() {
        String jwtTokenDate = sharedPreferences.getString(JWT_TOKEN_DATE, null);
        if (jwtTokenDate == null) {
            Log.i("HSenseAuth", "JWT date is empty");
            return false;
        }
        LocalDateTime jwtDate = LocalDateTime.parse(jwtTokenDate);

        if (LocalDateTime.now().isBefore(jwtDate.plusDays(1))) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkIfLoginDataAvialiable(){
        return (getUsername() != null & getPassword() != null);
    }

    public void updateJwt(String jwt) {
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        myEdit.putString(JWT, jwt);
        myEdit.putString(JWT_TOKEN_DATE, LocalDateTime.now().toString());
        myEdit.apply();
        myEdit.commit();
    }

    public void logOut() {
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        // write all the data entered by the user in SharedPreference and apply
        myEdit.putString(USERNAME, null);
        myEdit.putString(PASSWORD, null);
        myEdit.putString(JWT, null);
        myEdit.putString(JWT_TOKEN_DATE, null);
        myEdit.apply();
        myEdit.commit();
    }

}
