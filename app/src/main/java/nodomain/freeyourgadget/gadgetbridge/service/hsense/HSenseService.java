package nodomain.freeyourgadget.gadgetbridge.service.hsense;

import java.util.Timer;

import nodomain.freeyourgadget.gadgetbridge.service.hsense.client.HSenseClient;

public class HSenseService {
    private HSenseClient hSenseClient;
    private String lastTimeStamp = "0";
    private final int MINUTES = 60;
    public Timer timer = new Timer();

}

