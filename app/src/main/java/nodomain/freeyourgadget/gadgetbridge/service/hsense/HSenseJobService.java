package nodomain.freeyourgadget.gadgetbridge.service.hsense;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.HSenseClient;
import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.task.SentDataTask;

public class HSenseJobService extends JobService {

    private final String TAG = "HSenseJobService";
    private boolean jobSetUpdated = false;
    private SentDataTask sentDataTask;

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
                Integer responseCode =

//TODO add SEND DATA TASK
                Log.i(TAG, "HSense Job Performed");
                if (responseCode != null && responseCode == 200) {
                    Log.i(TAG, "Job sucessfull");
                } else {
                    Log.i(TAG, "Job failed");
                }

                jobFinished(params, false);
            }

        }).start();
    }
}
