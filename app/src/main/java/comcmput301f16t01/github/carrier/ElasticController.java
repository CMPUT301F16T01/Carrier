package comcmput301f16t01.github.carrier;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

/**
 * Superclass for all Elastic Controllers that implements the connection to the elastic search server
 * for getting, posting, deleting, etc. in the child classes.
 */
public class ElasticController {
    protected static JestDroidClient client;

    /** Sets up the client to be used for Elastic Search */
    protected static void verifySettings() {
        if (client == null) {
            DroidClientConfig.Builder builder =
                    new DroidClientConfig.Builder("https://search-aircraftcarrier-xpdt7duieu6pwl22453nqvjgee.us-west-2.es.amazonaws.com/");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }
}