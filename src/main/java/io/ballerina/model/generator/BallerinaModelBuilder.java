package io.ballerina.model.generator;

import io.ballerina.object.model.BallerinaPackage;
import org.apache.synapse.api.API;
import org.apache.synapse.config.SynapseConfiguration;

import java.util.Collection;

public class BallerinaModelBuilder {

    public BallerinaPackage generateBallerinaModel(SynapseConfiguration synapseConfig) {
        ModelEnvironment modelEnvironment = new ModelEnvironment(synapseConfig);
//        if (synapseConfig.getRegistry() == null) {
//            // If the synapse.xml does not define a registry look for a registry.xml
//            createRegistry(synapseConfig);
//        }
//
//        if (synapseConfig.getTaskManager() == null) {
//            // If the synapse.xml does not define a taskManager look for a task-manager.xml
//            createTaskManager(synapseConfig);
//        }
//        createSynapseImports(synapseConfig);
//        createLocalEntries(synapseConfig);
//        createEndpoints(synapseConfig.getDefinedEndpoints(), modelEnvironment);
//        createSequences(synapseConfig);
//        createTemplates(synapseConfig);
//        createProxyServices(synapseConfig);
//        createTasks(synapseConfig);
//        createEventSources(synapseConfig);
//        createExecutors(synapseConfig);
//        createMessageStores(synapseConfig);
//        createMessageProcessors(synapseConfig);
//        createInboundEndpoint(synapseConfig);
        createAPIs(synapseConfig.getAPIs(), modelEnvironment);
        modelEnvironment.setDefaultPackage(new BallerinaPackage.DefaultPackage(
                GeneratorConstants.DEFAULT_ORG_NAME, GeneratorConstants.DEFAULT_PACKAGE_NAME,
                GeneratorConstants.DEFAULT_VERSION));
        modelEnvironment.createModule(GeneratorConstants.DEFAULT_PACKAGE_NAME);
        modelEnvironment.completeModule();
        return modelEnvironment.getBallerinaPackage();
    }

    private void createAPIs(Collection<API> apiList, ModelEnvironment modelEnvironment) {
        if (apiList.isEmpty()) {
            return;
        }
        BallerinaPackage.Import importHttp = new BallerinaPackage.Import(GeneratorConstants.BALLERINA, GeneratorConstants.HTTP);
        modelEnvironment.addImport(importHttp);
        for (API api : apiList) {
            APIGenerator apiGenerator = new APIGenerator(api, modelEnvironment);
            apiGenerator.generateAPIService();
        }

    }

}
