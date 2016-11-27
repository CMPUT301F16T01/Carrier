package comcmput301f16t01.github.carrier.Requests;

import java.util.ArrayList;

/**
 * @see Offer
 */
public class OfferCommandList extends ArrayList<OfferCommand> {
    /** Replaces itself with a new instance and notifies listeners of the update. */
    public void replaceList( OfferCommandList newList ) {
        this.clear();
        this.addAll( newList );
    }

    /**
     * Appends new offers to the end of the list.
     * @param offerCommands The RequestList to append to this RequestList
     */
    public void append(OfferCommandList offerCommands) {
        for(OfferCommand offerCommand : offerCommands) {
            if(!this.contains(offerCommand.getRequest().getId())) {
                // the offer is not already in the list
                this.add(offerCommand);
            }
        }
    }

    /** Checks if the request is already contained in the requestList. */
    public boolean contains(String id) {
        for(OfferCommand offerCommand : this) {
            if(offerCommand.getRequest().getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public boolean remove(String id) {
        for(OfferCommand offerCommand : this) {
            if(offerCommand.getRequest().getId().equals(id)) {
                this.remove(offerCommand);
                return true;
            }
        }
        return false;
    }
}
