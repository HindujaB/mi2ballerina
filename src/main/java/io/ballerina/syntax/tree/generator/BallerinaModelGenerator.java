package io.ballerina.syntax.tree.generator;

import io.ballerina.object.model.BallerinaPackage;
import org.apache.synapse.api.API;
import org.apache.synapse.config.SynapseConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BallerinaModelGenerator {
    private final List<BallerinaPackage.Module> modules ;
    private final List<BallerinaPackage.Import> imports;
    private final List<BallerinaPackage.Service> services;
    private final List<BallerinaPackage.Variable > variables;


    public BallerinaModelGenerator() {
        this.modules = new ArrayList<>();
        this.imports = new ArrayList<>();
        this.services = new ArrayList<>();
        this.variables = new ArrayList<>();
    }


    public BallerinaPackage generateBallerinaModel(SynapseConfiguration synapseConfig) {
        BallerinaPackage.DefaultPackage defaultPackage = new BallerinaPackage.DefaultPackage(
                GeneratorConstants.DEFAULT_ORG_NAME, GeneratorConstants.DEFAULT_PACKAGE_NAME,
                GeneratorConstants.DEFAULT_VERSION);
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
//        createEndpoints(synapseConfig);
//        createSequences(synapseConfig);
//        createTemplates(synapseConfig);
//        createProxyServices(synapseConfig);
//        createTasks(synapseConfig);
//        createEventSources(synapseConfig);
//        createExecutors(synapseConfig);
//        createMessageStores(synapseConfig);
//        createMessageProcessors(synapseConfig);
        createAPIs(synapseConfig.getAPIs());
//        createInboundEndpoint(synapseConfig);
        this.modules.add(new BallerinaPackage.Module(GeneratorConstants.DEFAULT_PACKAGE_NAME, imports, variables, services));
        return  new BallerinaPackage(defaultPackage, modules);
    }

    private static void createEndpoints(SynapseConfiguration synapseConfig) {

    }

    private void createAPIs(Collection<API> apiList) {
        if (apiList.isEmpty()) {
            return;
        }
        BallerinaPackage.Import importHttp = new BallerinaPackage.Import(GeneratorConstants.BALLERINA, GeneratorConstants.HTTP);
        this.imports.add(importHttp);
        for (API api : apiList) {
            ServiceGenerator serviceGenerator = new ServiceGenerator(api);
            serviceGenerator.generateAPIService(this.services, this.variables);
        }

    }


}
