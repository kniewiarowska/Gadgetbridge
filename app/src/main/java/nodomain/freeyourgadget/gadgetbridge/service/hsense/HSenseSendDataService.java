package nodomain.freeyourgadget.gadgetbridge.service.hsense;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.task.FeelingTask;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.task.LoginTask;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.task.SentDataTask;

public class HSenseSendDataService {

    private SentDataTask sentDataTask;
    private LoginTask loginTask;
    private FeelingTask feelingTask;
    private HSenseAuthManager hSenseAuthManager;

    public HSenseSendDataService(Context context) {
        this.sentDataTask = new SentDataTask(context);
        this.feelingTask = new FeelingTask(context);
        this.loginTask = new LoginTask(context);
        this.hSenseAuthManager = new HSenseAuthManager(context);
    }

    public void sentData() { //TODO change name
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

    public void sendFeelingRate(int feelingResult) {
       //TODO logic with login - in case jwt to end
        feelingTask.execute(String.valueOf(feelingResult), hSenseAuthManager.getJwtToken());
    }


}
