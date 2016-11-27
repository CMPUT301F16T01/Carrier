package comcmput301f16t01.github.carrier.Requests;

import android.support.annotation.Nullable;
import android.util.Log;

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

    @Override
    public boolean add(Request e) {
        Boolean returnValue = super.add(e);
        notifyListeners();
        return returnValue;
    }

    public void addListener( Listener newListener ) {
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
     * Appends new requests to the end of the list and notifies listeners of the update. This method
     * includes a limit on the size of the ArrayList.
     * @param requests The RequestList to append to this RequestList
     * @param maxSize The maximum length of the ArrayList
     */
    public void append(RequestList requests, int maxSize) {
        for(Request request : requests) {
            if(!this.contains(request.getId()) && this.size() <  maxSize) {
                // still room on list and the request is not already in the list
                this.add(request);
            } else if(!this.contains(request.getId())) {
                // no more room on list, remove first request from beginning and append to end of list
                this.remove(0);
                this.add(request);
            }
        }
        notifyListeners();
    }

    /** Appends new requests to the end of the list and notifies listeners of the update.
     * @param requests The RequestList to append to this RequestList
     */
    public void append(RequestList requests) {
        for(Request request : requests) {
            if(!this.contains(request.getId())) {
                // the request is not already in the list
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
    private boolean contains(String id) {
        for(Request request : this) {
            if(request.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
