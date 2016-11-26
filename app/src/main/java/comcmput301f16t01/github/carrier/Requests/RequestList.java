package comcmput301f16t01.github.carrier.Requests;

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
    private static final int MAX_SIZE = 50;
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

    /** Appends new requests to the end of the list and notifies listeners of the update. */
    public void append(RequestList appendList) {
        for(Request request : appendList) {
            if(!this.contains(request.getId()) && this.size() < MAX_SIZE) {
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

    /** Verify that the status of the requests is still either "OPEN" or "OFFERED". */
    public void verifyAll() throws ExecutionException, InterruptedException {
        Iterator<Request> iterator = this.iterator();
        Log.i("Before", String.valueOf(this.size()));
        while(iterator.hasNext()) {
            if(!RequestController.verifyRequestAvailable(iterator.next())) {
                iterator.remove();
            }
        }
        Log.i("After", String.valueOf(this.size()));
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
