package io.ballerina.syntax.tree.generator;

import io.ballerina.object.model.BallerinaPackage;
import org.apache.synapse.api.API;

import java.util.HashMap;
import java.util.Map;

public class ListenerGenerator {

    private String basePath = "/";

    public String getBasePath() {
        return basePath;
    }

    /**
     * Generate listener according to the given server details.
     * E
     *
     * @return {@link io.ballerina.object.model.BallerinaPackage.Listener} for server.
     */
    public BallerinaPackage.Listener getListenerDeclaration(API api) {
        // Assign host port value to listeners

        String host = api.getHost() == null ? "localhost" : api.getHost();
        int port = api.getPort() == -1 ? GeneratorConstants.DEFAULT_PORT : api.getPort();
        basePath = api.getContext();
        Map<String, String> config = new HashMap<>();
        config.put("port",String.valueOf(port));
        config.put("host", host);
//      TODO: incorporate version property
        return new BallerinaPackage.Listener(GeneratorConstants.LISTENER_TYPE, config);
    }

}
