package comcmput301f16t01.github.carrier.Requests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import comcmput301f16t01.github.carrier.Listener;

/**
 * Adds listener functionality to the base ArrayList class.
 * TODO remove building up listeners (if there are any?)
 * @see Listener
 * @see Request
 */
public class RequestList extends ArrayList<Request> {
    private ArrayList<Listener> listeners = new ArrayList<>();
    /** Possible limit on the length of the array (-1 is default "unset" value). */
    private int maxArraySize = -1;

    public void setMaxArraySize(int maxArraySize) {
        this.maxArraySize = maxArraySize;
    }

    @Override
    public boolean add(Request e) {
        Boolean returnValue = super.add(e);
        notifyListeners();
        return returnValue;
    }

    public void addListener(Listener newListener ) {
        listeners.add( newListener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void notifyListeners() {
        for (Listener listener : listeners ) {
            listener.update();
        }
    }

    /** Replaces itself with a new instance and notifies listeners of the update. */
    public void replaceList( RequestList newList ) {
        this.clear();
        this.addAll( newList );
        notifyListeners();
    }

    /**
     * Removes request based on the request Id and notifies listeners of the update.
     * @param request The request to remove from the list
     */
    public boolean remove(Request request) {
        Iterator<Request> iterator = this.iterator();
        while(iterator.hasNext()) {
            if(iterator.next().getId().equals(request.getId())) {
                iterator.remove();
                notifyListeners();
                return true;
            }
        }
        return false;
    }

    /**
     * Appends new requests to the end of the list and notifies listeners of the update. If the
     * maxArraySize has been set, we check if there is room on the array (if there is no room, we
     * remove the first, or most recent, request and append the new request to the end). If the
     * maxArraySize has not been set or it has been set but we still have room on the array, we
     * simply add the request to the end of the array.
     * @param requests The RequestList to append to this RequestList
     */
    public void append(RequestList requests) {
        // by default, we ignore the size of the array
        boolean ignoreSize = true;
        // check if array size has not been set
        if (maxArraySize != -1) {
            // still room to add on this instance of the request list (cannot ignore the max array size)
            ignoreSize = this.size() < maxArraySize;
        }
        // go through all the request we need to append
        for (Request request : requests) {
            // still room on list (or we don't care about array size)
            // the request is not already in the list, simply add
            if (!this.contains(request.getId()) && ignoreSize) {
                this.add(request);
            } else if (!this.contains(request.getId())) {
                // no more room on list (we care about size of array list)
                // remove first request from beginning and append to end of list
                this.remove(0);
                this.add(request);
            }
        }
        notifyListeners();
    }

    /** Verify that the status of the requests is still either "OPEN" or "OFFERED". */
    public void verifyAll() throws ExecutionException, InterruptedException {
        Iterator<Request> iterator = this.iterator();
        while(iterator.hasNext()) {
            if(!RequestController.verifyRequestAvailable(iterator.next().getId())) {
                iterator.remove();
            }
        }
    }

    /** Checks if the request is already contained in the requestList. */
    boolean contains(String id) {
        for(Request request : this) {
            if(request.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
