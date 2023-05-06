package nodomain.freeyourgadget.gadgetbridge.service.hsense;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.task.LoginTask;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.task.SentDataTask;

public class HSenseSendDataService {

    private SentDataTask sentDataTask;
    private LoginTask loginTask;
    private HSenseAuthManager hSenseAuthManager;

    public HSenseSendDataService(Context context) {
        this.sentDataTask = new SentDataTask(context);
        this.loginTask = new LoginTask(context);
        this.hSenseAuthManager = new HSenseAuthManager(context);
    }

    public void sentData() {
        try {
            if (hSenseAuthManager.checkIfLoginDataAvialiable()) {
                tryToSendData();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("Sentdata", "Noone was logged in application - data cannot be saved");

    }

    private void tryToSendData() throws ExecutionException, InterruptedException {
        if (hSenseAuthManager.checkIfJwtIsActive()) {
             sendData();
        } else {
            updateTokenAndSaveData();
        }
    }

    private void sendData() throws ExecutionException, InterruptedException {
        sentDataTask.execute(hSenseAuthManager.getJwtToken());
    }

    private void updateTokenAndSaveData() throws ExecutionException, InterruptedException {

        String jwt = loginTask.execute(hSenseAuthManager.getUsername(), hSenseAuthManager.getPassword()).get();
        if (jwt != null) {
            sentDataTask.execute(hSenseAuthManager.getJwtToken()).get();
        }

    }
}
