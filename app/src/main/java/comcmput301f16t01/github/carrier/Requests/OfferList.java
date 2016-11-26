package comcmput301f16t01.github.carrier.Requests;

import java.util.ArrayList;

/**
 * @see Offer
 */
public class OfferList extends ArrayList<Offer> {
    /** Replaces itself with a new instance and notifies listeners of the update. */
    public void replaceList( OfferList newList ) {
        this.clear();
        this.addAll( newList );
    }

    /**
     * Appends new offers to the end of the list.
     * @param offers The RequestList to append to this RequestList
     */
    public void append(OfferList offers) {
        for(Offer offer : offers) {
            if(!this.contains(offer.getRequestID())) {
                // the offer is not already in the list
                this.add(offer);
            }
        }
    }

    /** Checks if the request is already contained in the requestList. */
    private boolean contains(String id) {
        for(Offer offer : this) {
            if(offer.getRequestID().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
