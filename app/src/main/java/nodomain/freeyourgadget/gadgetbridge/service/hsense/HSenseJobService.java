package nodomain.freeyourgadget.gadgetbridge.service.hsense;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.HSenseClient;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.task.SentDataTask;

public class HSenseJobService extends JobService {

    private final String TAG = "HSenseJobService";
    private boolean jobSetUpdated = false;
    private HSenseSendDataService hSenseSendDataService = new HSenseSendDataService(getApplicationContext());

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i(TAG, "HSenseJob is started");
        doBackgroundWork(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(TAG, "HSenseJob is stoped");
        return false;
    }

    private void doBackgroundWork(JobParameters params) {

        new Thread( new Runnable() {

            @Override
            public void run() {
                HSenseClient hSenseClient = new HSenseClient(getApplicationContext());
                hSenseSendDataService.sentData();
                Log.i(TAG, "HSense Job Performed");
                jobFinished(params, false);
            }

        }).start();
    }
}
