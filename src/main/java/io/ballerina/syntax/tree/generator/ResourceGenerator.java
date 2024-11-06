package io.ballerina.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.FunctionBodyBlockNode;
import io.ballerina.compiler.syntax.tree.FunctionDefinitionNode;
import io.ballerina.compiler.syntax.tree.FunctionSignatureNode;
import io.ballerina.compiler.syntax.tree.IdentifierToken;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.Token;
import org.apache.synapse.api.Resource;
import org.apache.synapse.api.dispatch.DispatcherHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createEmptyNodeList;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createIdentifierToken;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createNodeList;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createToken;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createFunctionBodyBlockNode;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createFunctionDefinitionNode;

public class ResourceGenerator {

    private final Resource resource;
    private final List<FunctionDefinitionNode> resources = new ArrayList<>();
    private final List<String> pathParams = new ArrayList<>();
    private final List<String> queryParams = new ArrayList<>();

    public ResourceGenerator(Resource resource) {
        this.resource = resource;
    }

    public void generateResourceFunctions() throws BallerinaGeneratorException {
        String[] methods = resource.getMethods();
        for (String method : methods) {
            NodeList<Token> qualifiersList = createNodeList(createIdentifierToken(
                    GeneratorConstants.RESOURCE, GeneratorUtils.SINGLE_WS_MINUTIAE, GeneratorUtils.SINGLE_WS_MINUTIAE));
            Token functionKeyWord = createIdentifierToken(GeneratorConstants.FUNCTION,
                    GeneratorUtils.SINGLE_WS_MINUTIAE, GeneratorUtils.SINGLE_WS_MINUTIAE);
            IdentifierToken functionName = createIdentifierToken(method.toLowerCase(Locale.ENGLISH),
                    GeneratorUtils.SINGLE_WS_MINUTIAE, GeneratorUtils.SINGLE_WS_MINUTIAE);
            DispatcherHelper dispatcherHelper = resource.getDispatcherHelper();
            NodeList<Node> relativeResourcePath = GeneratorUtils.getRelativeResourcePath(dispatcherHelper, pathParams, queryParams);
            FunctionSignatureGenerator functionSignatureGenerator = new FunctionSignatureGenerator(resource, pathParams, queryParams);
            FunctionSignatureNode functionSignatureNode = functionSignatureGenerator
                    .getFunctionSignature(method, relativeResourcePath);
            // Function Body Node
            // If path parameter has some special characters, extra body statements are added to handle the complexity.
            FunctionBodyBlockNode functionBodyBlockNode = createFunctionBodyBlockNode(
                    createToken(SyntaxKind.OPEN_BRACE_TOKEN), null, createEmptyNodeList(),
                    createToken(SyntaxKind.CLOSE_BRACE_TOKEN), null);
            FunctionDefinitionNode functionDefinitionNode = createFunctionDefinitionNode(
                    SyntaxKind.RESOURCE_ACCESSOR_DEFINITION, null, qualifiersList, functionKeyWord,
                    functionName, relativeResourcePath, functionSignatureNode, functionBodyBlockNode);
            resources.add(functionDefinitionNode);
            pathParams.clear();
            queryParams.clear();
        }
    }

    public Collection<? extends Node> getResources() {
        return this.resources;
    }

}
