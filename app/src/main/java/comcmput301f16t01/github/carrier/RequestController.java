package comcmput301f16t01.github.carrier;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Singleton Pattern
 * * modifies/returns a RequestList model
 * * @see Request
 * * @see RequestList
 */
public class RequestController {
    private static RequestList requestList = null;
    private Context saveContext = null;

    /**
     *
     */
    public RequestController() {
        if (requestList == null) {
            requestList = new RequestList();
        }
    }

    /**
     * @return the RequestList held by this controller.
     */
    public static RequestList getInstance() {
        if (requestList == null) {
            requestList = new RequestList();
        }
        return requestList;
    }

    /**
     * @param request
     */
    public void addRequest(Request request) {
    }

    /**
     * @param rider
     * @return
     */
    public ArrayList<Request> getRequests(Rider rider) {
        return new ArrayList<Request>();
    }

    /**
     *
     */
    public void reset() {
    }

    public void cancelRequest(Rider rider, Request request) {
    }

    public void addDriver(Request request, Driver driver) {
    }

    public void acceptDriver(Request request, Driver driver) {
    }

    public void completeRequest(Request request) {
    }

    public void payForRequest(Request request) {
    }

    public ArrayList<Request> searchByLocation(Location location) {
        return new ArrayList<>();
    }

    public ArrayList<Request> searchByKeyword(String query) {
        return new ArrayList<>();
    }
}