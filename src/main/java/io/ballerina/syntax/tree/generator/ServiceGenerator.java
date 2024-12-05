package io.ballerina.syntax.tree.generator;

import io.ballerina.object.model.BallerinaPackage;
import org.apache.synapse.api.API;
import org.apache.synapse.api.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This Util class use for generating ballerina service file according to given yaml file.
 *
 * @since 1.3.0
 */
public class ServiceGenerator {

    List<BallerinaPackage.Resource> resources;

    private final API api;

    public ServiceGenerator(API api) {
        this.api = api;
    }

    public void generateAPIService(List<BallerinaPackage.Service> services, List<BallerinaPackage.Variable> variables) {
        ListenerGenerator listenerGenerator = new ListenerGenerator();
        BallerinaPackage.Listener listener = listenerGenerator.getListenerDeclaration(api);
        String basePath = listenerGenerator.getBasePath();
        if (resources == null) {
            resources = createResourceFunctions(api.getResources());
        }
        services.add(new BallerinaPackage.Service(basePath, List.of(listener), resources));
    }

    private List<BallerinaPackage.Resource> createResourceFunctions(Resource[] resources) {
        List<BallerinaPackage.Resource> functions = new ArrayList<>();
        for (Resource resource : resources) {
            functions.addAll(Objects.requireNonNull(createResource(resource)));
        }
        return functions;
    }

    private List<BallerinaPackage.Resource> createResource(Resource resource) {
        ResourceGenerator resourceGenerator = new ResourceGenerator(resource);
        try {
            resourceGenerator.generateResourceFunctions();
            return resourceGenerator.getResources();
        } catch (BallerinaGeneratorException e) {
            // this will catch the error level diagnostics that affects the function generation.
        }
        return null;
    }

}
