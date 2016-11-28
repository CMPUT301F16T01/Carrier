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
     * Appends new offer commands to the end of the list.
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

    /** Checks if the request (based on the id from elastic search) is already contained in the OfferCommandList. */
    public boolean contains(String id) {
        for(OfferCommand offerCommand : this) {
            if(offerCommand.getRequest().getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /** Removes the request (based on the id from elastic search) from the OfferCommandList. */
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
