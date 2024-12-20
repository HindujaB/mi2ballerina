package io.ballerina.model.generator;

import io.ballerina.model.generator.mediator.MediatorHandler;
import io.ballerina.model.generator.mediator.MediatorHandlerFactory;
import io.ballerina.object.model.BallerinaPackage;
import org.apache.synapse.api.Resource;
import org.apache.synapse.api.dispatch.DispatcherHelper;
import org.apache.synapse.mediators.base.SequenceMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResourceGenerator {

    private final Resource resource;
    private final List<BallerinaPackage.Resource> resources = new ArrayList<>();
    private final ModelEnvironment modelEnvironment;

    public ResourceGenerator(Resource resource, ModelEnvironment modelEnvironment) {
        this.resource = resource;
        this.modelEnvironment = modelEnvironment;
    }

    public void generateResourceFunctions() throws BallerinaGeneratorException {
        String[] methods = resource.getMethods();
        for (String method : methods) {
            modelEnvironment.setCurrentResourceMethod(method);
            DispatcherHelper dispatcherHelper = resource.getDispatcherHelper();
            List<String> queryParams = new ArrayList<>();
            String relativeResourcePath = GeneratorUtils.getRelativeResourcePath(dispatcherHelper, queryParams);
            List<BallerinaPackage.Statement> statements = generateSequences();
            String returnType = getReturnType();
            resources.add(new BallerinaPackage.Resource("", method.toLowerCase(Locale.ROOT), relativeResourcePath, modelEnvironment.getParameters().values().stream().toList(), statements, queryParams, returnType));
            modelEnvironment.clearResource();
        }
    }

    private String getReturnType() {
        if (modelEnvironment.getParameters().containsKey(GeneratorConstants.CALLER_VAR)) {
            return GeneratorConstants.ERROR_OR_NIL;
        }
        if (modelEnvironment.getLocalVars().containsKey(GeneratorConstants.RESPONSE_VAR)) {
            return GeneratorConstants.RESPONSE_ERROR_VAR;
        }
        return null;
    }

    private List<BallerinaPackage.Statement> generateSequences() {
        SequenceMediator inSequence = this.resource.getInSequence();
        MediatorHandler handler = MediatorHandlerFactory.getHandler(inSequence);
        handler.handleMediator(modelEnvironment);
        return new ArrayList<>(modelEnvironment.exitContext());
    }

    public List<BallerinaPackage.Resource> getResources() {
        return this.resources;
    }

}
