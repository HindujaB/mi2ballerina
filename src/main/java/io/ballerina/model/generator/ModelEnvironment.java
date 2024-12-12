package io.ballerina.model.generator;

import io.ballerina.object.model.BallerinaPackage;
import org.apache.synapse.Mediator;
import org.apache.synapse.config.SynapseConfiguration;
import org.apache.synapse.endpoints.Endpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelEnvironment {

    private BallerinaPackage.Module currentModule;
    private Mediator currentMediator;
    private Mediator parentMediator = null;
    public String currentResourceMethod;
    private final SynapseConfiguration synapseConfig;

    public ModelEnvironment(SynapseConfiguration synapseConfig) {
        this.synapseConfig = synapseConfig;
    }

    public void addStatement(BallerinaPackage.Statement statement) {
        contextStatementsMap.get(currentMediator).add(statement);
    }

    public Map<String, BallerinaPackage.Parameter> getParameters() {
        return parameters;
    }

    public Map<String, BallerinaPackage.Variable> getLocalVars() {
        return localVars;
    }

    private final Map<Mediator, List<BallerinaPackage.Statement>> contextStatementsMap = new HashMap<>();
    private Map<String, BallerinaPackage.Parameter> parameters = new HashMap<>();
    private Map<String, BallerinaPackage.Variable> localVars = new HashMap<>();

    public void enterContext(Mediator mediator) {
        this.parentMediator = this.currentMediator;
        this.currentMediator = mediator;
        contextStatementsMap.put(mediator, new ArrayList<>());
    }

    public List<BallerinaPackage.Statement> exitContext() {
        List<BallerinaPackage.Statement> statements = contextStatementsMap.remove(this.currentMediator);
        this.currentMediator = this.parentMediator;
        return statements;
    }

    private BallerinaPackage.DefaultPackage defaultPackage = null;
    private final List<BallerinaPackage.Module> modules = new ArrayList<>();
    private final List<BallerinaPackage.Import> imports = new ArrayList<>();
    private final List<BallerinaPackage.Service> services = new ArrayList<>();
    private final List<BallerinaPackage.Variable> variables = new ArrayList<>();
    private final List<BallerinaPackage.Function> functions = new ArrayList<>();

    public void createModule(String moduleName) {
        currentModule = new BallerinaPackage.Module(moduleName, imports, variables, services);
    }

    public void addImport(BallerinaPackage.Import importNode) {
        if (imports.contains(importNode)) {
            return;
        }
        imports.add(importNode);
    }

    public void addService(BallerinaPackage.Service service) {
        services.add(service);
    }

    public void setDefaultPackage(BallerinaPackage.DefaultPackage defaultPackage) {
        this.defaultPackage = defaultPackage;
    }

    public BallerinaPackage getBallerinaPackage() {
        return new BallerinaPackage(defaultPackage, modules);
    }

    public void completeModule() {
        this.modules.add(currentModule);
    }

    public void clearResource() {
        this.parameters = new HashMap<>();
        this.localVars = new HashMap<>();
        this.currentResourceMethod = null;
    }

    public void setCurrentResourceMethod(String method) {
        this.currentResourceMethod = method;
    }

    public void addParentStatement(BallerinaPackage.Statement ballerinaStatement) {
        contextStatementsMap.get(parentMediator).add(ballerinaStatement);

    }

    public Endpoint getOriginalEndpoint(String name) {
        return synapseConfig.getDefinedEndpoints().get(name);
    }
}
