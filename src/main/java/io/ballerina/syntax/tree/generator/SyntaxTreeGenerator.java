package io.ballerina.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.tools.text.TextDocument;
import io.ballerina.tools.text.TextDocuments;
import org.apache.synapse.api.API;
import org.apache.synapse.config.SynapseConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createIdentifierToken;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createNodeList;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createSeparatedNodeList;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createModulePartNode;

public class SyntaxTreeGenerator {
    private SyntaxTree syntaxTree;
    private List<ModuleMemberDeclarationNode> moduleMembers ;
    private List<ImportDeclarationNode> imports;


    public SyntaxTreeGenerator() {
        TextDocument textDocument = TextDocuments.from("");
        this.syntaxTree = SyntaxTree.from(textDocument);
        this.moduleMembers = new ArrayList<>();
        this.imports = new ArrayList<>();
    }


    public SyntaxTree generateSyntaxtree(SynapseConfiguration synapseConfig) {
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
        Token eofToken = createIdentifierToken("");
        ModulePartNode modulePartNode = createModulePartNode(createNodeList(this.imports),
                createNodeList(this.moduleMembers), eofToken);
        syntaxTree = syntaxTree.modifyWith(modulePartNode);
        return this.syntaxTree;
    }

    private static void createEndpoints(SynapseConfiguration synapseConfig) {

    }

    private void createAPIs(Collection<API> apiList) {
        if (apiList.isEmpty()) {
            return;
        }
        ImportDeclarationNode importForHttp = GeneratorUtils.getImportDeclarationNode(GeneratorConstants.BALLERINA
                , GeneratorConstants.HTTP);
        this.imports.add(importForHttp);
        for (API api : apiList) {
            ServiceGenerator serviceGenerator = new ServiceGenerator(api);
            serviceGenerator.generateAPIService(this.moduleMembers);
        }

    }


}
