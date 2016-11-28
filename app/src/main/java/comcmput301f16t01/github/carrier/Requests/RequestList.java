package comcmput301f16t01.github.carrier.Requests;

import java.util.ArrayList;
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

    public boolean contains(String id) {
        for (Request request : this) {
            if (request.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
