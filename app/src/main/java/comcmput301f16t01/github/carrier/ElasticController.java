package comcmput301f16t01.github.carrier;

import java.util.Queue;

/**
 * Populates, saves, and loads RequestController and UserController.
 * This class is USED by them to save them
 */

public class ElasticController {
    private static boolean online = false;
    private static Queue<Request> requestQueue;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean status) {
        online = status;
    }

    public Queue<Request> getRequestQueue() {
        return requestQueue;
    }
}
