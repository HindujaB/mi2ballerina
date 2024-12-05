package io.ballerina.syntax.tree.generator;

import io.ballerina.object.model.BallerinaPackage;
import org.apache.synapse.api.Resource;
import org.apache.synapse.api.dispatch.DispatcherHelper;

import java.util.ArrayList;
import java.util.List;

public class ResourceGenerator {

    private final Resource resource;
    private final List<BallerinaPackage.Resource> resources = new ArrayList<>();
    private final List<String> pathParams = new ArrayList<>();
    private final List<String> queryParams = new ArrayList<>();
    private final List<BallerinaPackage.BodyStatement> statements = new ArrayList<>();
    private final List<BallerinaPackage.Parameter> parameters = new ArrayList<>();

    public ResourceGenerator(Resource resource) {
        this.resource = resource;
    }

    public void generateResourceFunctions() throws BallerinaGeneratorException {
        String[] methods = resource.getMethods();
        for (String method : methods) {
            DispatcherHelper dispatcherHelper = resource.getDispatcherHelper();
            String relativeResourcePath = GeneratorUtils.getRelativeResourcePath(dispatcherHelper, pathParams, queryParams);
            resources.add(new BallerinaPackage.Resource("", method, relativeResourcePath, parameters,
                    statements, pathParams, queryParams));
//            clearData();
        }
    }

//    private void clearData() {
//        pathParams.clear();
//        queryParams.clear();
//        statements.clear();
//        parameters.clear();
//    }

    public List<BallerinaPackage.Resource> getResources() {
        return this.resources;
    }

}
