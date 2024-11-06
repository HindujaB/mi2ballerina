package io.ballerina.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.ExpressionNode;
import io.ballerina.compiler.syntax.tree.ListenerDeclarationNode;
import io.ballerina.compiler.syntax.tree.MetadataNode;
import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.SeparatedNodeList;
import io.ballerina.compiler.syntax.tree.ServiceDeclarationNode;
import io.ballerina.compiler.syntax.tree.SimpleNameReferenceNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.compiler.syntax.tree.TypeDescriptorNode;
import org.apache.synapse.api.API;
import org.apache.synapse.api.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createEmptyNodeList;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createIdentifierToken;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createNodeList;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createSeparatedNodeList;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createToken;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createServiceDeclarationNode;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createSimpleNameReferenceNode;

/**
 * This Util class use for generating ballerina service file according to given yaml file.
 *
 * @since 1.3.0
 */
public class ServiceGenerator {

    private SyntaxTree syntaxTree;
    List<Node> functionsList;

    private final API api;

    public ServiceGenerator(API api) {
        this.api = api;
    }

    public void generateAPIService(List<ModuleMemberDeclarationNode> moduleMembers) {
        ListenerGenerator listenerGenerator = new ListenerGenerator();
        ListenerDeclarationNode listenerDeclarationNode = listenerGenerator.getListenerDeclarationNode(api);
        NodeList<Node> absoluteResourcePath = createBasePathNodeList(listenerGenerator);
        SimpleNameReferenceNode listenerName = createSimpleNameReferenceNode(listenerDeclarationNode.variableName());
        SeparatedNodeList<ExpressionNode> expressions = createSeparatedNodeList(listenerName);

        if (functionsList == null) {
            functionsList = createResourceFunctions(api.getResources());
        }
        NodeList<Node> members = createNodeList(functionsList);
        MetadataNode metadataNode = null;
        TypeDescriptorNode serviceType = null;
        ServiceDeclarationNode serviceDeclarationNode = createServiceDeclarationNode(
                metadataNode, createEmptyNodeList(), createToken(SyntaxKind.SERVICE_KEYWORD,
                        GeneratorUtils.SINGLE_WS_MINUTIAE, GeneratorUtils.SINGLE_WS_MINUTIAE),
                serviceType, absoluteResourcePath, createToken(SyntaxKind.ON_KEYWORD,
                        GeneratorUtils.SINGLE_WS_MINUTIAE, GeneratorUtils.SINGLE_WS_MINUTIAE), expressions,
                createToken(SyntaxKind.OPEN_BRACE_TOKEN), members, createToken(SyntaxKind.CLOSE_BRACE_TOKEN), null);

        // Add module member declaration
        moduleMembers.add(listenerDeclarationNode);
        moduleMembers.add(serviceDeclarationNode);
    }

    private NodeList<Node> createBasePathNodeList(ListenerGenerator listener) {

        if (GeneratorConstants.PATH_SEPARATOR.equals(listener.getBasePath())) {
            return createNodeList(createIdentifierToken(listener.getBasePath()));
        } else {
            String[] basePathNode = listener.getBasePath().split(GeneratorConstants.PATH_SEPARATOR);
            List<Node> basePath = Arrays.stream(basePathNode).filter(node -> !node.isBlank())
                    .map(node -> createIdentifierToken(GeneratorConstants.PATH_SEPARATOR + node))
                    .collect(Collectors.toList());
            return createNodeList(basePath);
        }
    }

    private List<Node> createResourceFunctions(Resource[] resources) {
        List<Node> functions = new ArrayList<>();
        for (Resource resource : resources) {
            functions.addAll(Objects.requireNonNull(createResource(resource)));
        }
        return functions;
    }

    private Collection<? extends Node> createResource(Resource resource) {
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
