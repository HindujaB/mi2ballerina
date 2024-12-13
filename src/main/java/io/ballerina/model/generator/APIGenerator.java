package io.ballerina.model.generator;

import io.ballerina.object.model.BallerinaPackage;
import org.apache.synapse.api.API;
import org.apache.synapse.api.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This Util class use for generating ballerina service file according to given yaml file.
 *
 * @since 1.3.0
 */
public class APIGenerator {

    List<BallerinaPackage.Resource> resources;

    private final API api;
    private String basePath = "/";
    private final ModelEnvironment modelEnvironment;

    public APIGenerator(API api, ModelEnvironment modelEnvironment) {
        this.api = api;
        this.modelEnvironment = modelEnvironment;
    }

    public void generateAPIService() {
        if (resources == null) {
            resources = createResourceFunctions(api.getResources());
        }
        basePath = api.getContext();
        modelEnvironment.addService(new BallerinaPackage.Service(basePath, List.of(getAPIEndpointListener(api)),
                resources));
    }

    private List<BallerinaPackage.Resource> createResourceFunctions(Resource[] resources) {
        List<BallerinaPackage.Resource> functions = new ArrayList<>();
        for (Resource resource : resources) {
            functions.addAll(Objects.requireNonNull(createResource(resource)));
        }
        return functions;
    }

    private List<BallerinaPackage.Resource> createResource(Resource resource) {
        ResourceGenerator resourceGenerator = new ResourceGenerator(resource, modelEnvironment);
        try {
            resourceGenerator.generateResourceFunctions();
            return resourceGenerator.getResources();
        } catch (BallerinaGeneratorException e) {
            throw new BallerinaGeneratorException("Error occurred while generating resource functions", e);
        }
    }

    public String getAPIEndpointListener(API api) {
        // Assign host port value to listeners
        String host = api.getHost() == null ? "localhost" : api.getHost();
        int port = api.getPort() == -1 ? GeneratorConstants.DEFAULT_PORT : api.getPort();
        Map<String, String> config = new HashMap<>();
        config.put(GeneratorConstants.PORT, String.valueOf(port));
        config.put(GeneratorConstants.HOST, host);
//      TODO: incorporate version property
         return modelEnvironment.addNewListener(config);
    }
}
