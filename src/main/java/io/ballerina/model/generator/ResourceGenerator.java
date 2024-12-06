package io.ballerina.model.generator;

import io.ballerina.object.model.BallerinaPackage;
import org.apache.synapse.api.Resource;
import org.apache.synapse.api.dispatch.DispatcherHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResourceGenerator {

    private final Resource resource;
    private final List<BallerinaPackage.Resource> resources = new ArrayList<>();
    private final List<BallerinaPackage.BodyStatement> statements = new ArrayList<>();
    private final List<BallerinaPackage.Parameter> parameters = new ArrayList<>();

    public ResourceGenerator(Resource resource) {
        this.resource = resource;
    }

    public void generateResourceFunctions() throws BallerinaGeneratorException {
        String[] methods = resource.getMethods();
        for (String method : methods) {
            DispatcherHelper dispatcherHelper = resource.getDispatcherHelper();
            List<String> queryParams = new ArrayList<>();
            String relativeResourcePath = GeneratorUtils.getRelativeResourcePath(dispatcherHelper, queryParams);
            resources.add(new BallerinaPackage.Resource("", method.toLowerCase(Locale.ROOT),
                    relativeResourcePath, parameters, statements, queryParams, null));
        }
    }

    public List<BallerinaPackage.Resource> getResources() {
        return this.resources;
    }

}
