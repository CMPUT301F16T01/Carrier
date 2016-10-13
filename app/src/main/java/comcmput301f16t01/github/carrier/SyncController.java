package comcmput301f16t01.github.carrier;

/**
 * Populates, saves, and loads RequestController and UserController.
 * This class is USED by them to save them
 */

public class SyncController {
    private static boolean online = false;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean status) {
        online = status;
    }
}
