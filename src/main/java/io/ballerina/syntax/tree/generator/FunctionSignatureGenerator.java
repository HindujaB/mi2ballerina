package io.ballerina.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.FunctionSignatureNode;
import io.ballerina.compiler.syntax.tree.IdentifierToken;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.ParameterNode;
import io.ballerina.compiler.syntax.tree.ReturnTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.SeparatedNodeList;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.Token;
import io.ballerina.compiler.syntax.tree.TypeDescriptorNode;
import org.apache.synapse.api.Resource;

import java.util.ArrayList;
import java.util.List;

import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createEmptyNodeList;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createIdentifierToken;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createNodeList;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createSeparatedNodeList;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createToken;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createBuiltinSimpleNameReferenceNode;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createFunctionSignatureNode;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createRequiredParameterNode;

public class FunctionSignatureGenerator {
    private final Resource resource;
    private final List<String> pathParams;
    private final List<String> queryParams;

    public FunctionSignatureGenerator(Resource resource, List<String> pathParams, List<String> queryParams) {
        this.resource = resource;
        this.pathParams = pathParams;
        this.queryParams = queryParams;
    }


    public boolean isNullableRequired() {
        return false;
    }

    public FunctionSignatureNode getFunctionSignature(String method, NodeList<Node> relativeResourcePath) {
        List<Node> params = generateParameters(method, relativeResourcePath);
        if (params.size() > 1) {
            params.remove(params.size() - 1);
        }
        SeparatedNodeList<ParameterNode> parameters = createSeparatedNodeList(params);
        ReturnTypeGenerator returnTypeGenerator = new ReturnTypeGenerator();
        ReturnTypeDescriptorNode returnNode;
        returnNode = returnTypeGenerator.getReturnTypeDescriptorNode();
        return createFunctionSignatureNode(createToken(SyntaxKind.OPEN_PAREN_TOKEN),
                parameters, createToken(SyntaxKind.CLOSE_PAREN_TOKEN), returnNode);
    }

    private List<Node> generateParameters(String method, NodeList<Node> relativeResourcePath) {
        List<Node> requiredParams = new ArrayList<>();
        Token comma = createToken(SyntaxKind.COMMA_TOKEN);
        // Handle header and query parameters
        //TODO : all are string parameters. Need to handle other types.
        //TODO : Need to handle defaultable parameters - no synapse information
        //TODO : No way of identifying header parameters

//        for (String parameter : pathParams) {
//            requiredParams.add(createParameterNode(parameter));
//            requiredParams.add(comma);
//        }
        for (String parameter : queryParams) {
            requiredParams.add(createParameterNode(parameter));
            requiredParams.add(comma);
        }
        return requiredParams;
    }

    public ParameterNode createParameterNode(String parameter) {
        String paramName = parameter.trim();
        String headerType = GeneratorConstants.STRING;
        if (parameter.isBlank()) {
            throw new BallerinaGeneratorException("parameter name is empty");
        }
        IdentifierToken parameterName = createIdentifierToken(paramName,
                AbstractNodeFactory.createEmptyMinutiaeList(), GeneratorUtils.SINGLE_WS_MINUTIAE);
        TypeDescriptorNode headerTypeName = createBuiltinSimpleNameReferenceNode(null, createIdentifierToken(
                    headerType, GeneratorUtils.SINGLE_WS_MINUTIAE,
                    GeneratorUtils.SINGLE_WS_MINUTIAE));
        return createRequiredParameterNode(createEmptyNodeList(), headerTypeName, parameterName);
    }

    public record ParametersGeneratorResult(List<Node> requiredParameters, List<Node> defaultableParameters) { }
}
