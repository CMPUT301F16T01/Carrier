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

    /**
     * Is used to add a driver to a request.
     * @param request The request we are modifying
     * @param driverTwo the driver that is being added as a driver for the request.
     */
    public void addDriver(Request request, Driver driverTwo) {
    }

    /**
     * Is used to show that the user has accepted the provided driver/
     * @param request The request that is being modified
     * @param driver The driver that is being accepted
     */
    public void acceptDriver(Request request, Driver driver) {
    }

    public void completeRequest(Request request) {
    }

    public void payForRequest(Request request) {
    }




    /**
     * Is used to provide a driver with a list of all open requests.
     * @return An array list of open requests
     */
    public ArrayList<Request> getOpenRequests(){
        return new ArrayList<Request>();
    }

    /**
     * Is used to provide a driver with a list of the requests that they have accepted.
     * @param driver The driver who is requesting the list of the requests they have accepted
     * @return An ArrayList of requests that the driver has accepted.
     */
    public ArrayList<Request> getAcceptedRequests(Driver driver) {
        return  new ArrayList<Request>();
    }

    /** Will be used to get a list of drivers that the user can choose to accept as there driver
     *
     * @param request The request that is being queried for a list of drivers.
     * @return An ArrayList of drivers that have accepted every request.
     */
    public ArrayList<Driver> getDrivers(Request request) {
        return new ArrayList<Driver>();
    }
}