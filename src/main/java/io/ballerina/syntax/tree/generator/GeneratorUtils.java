package io.ballerina.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.BuiltinSimpleNameReferenceNode;
import io.ballerina.compiler.syntax.tree.IdentifierToken;
import io.ballerina.compiler.syntax.tree.ImportDeclarationNode;
import io.ballerina.compiler.syntax.tree.ImportOrgNameNode;
import io.ballerina.compiler.syntax.tree.Minutiae;
import io.ballerina.compiler.syntax.tree.MinutiaeList;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.QualifiedNameReferenceNode;
import io.ballerina.compiler.syntax.tree.ResourcePathParameterNode;
import io.ballerina.compiler.syntax.tree.SeparatedNodeList;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.Token;
import org.apache.synapse.api.dispatch.DispatcherHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createIdentifierToken;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createNodeList;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createToken;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createBuiltinSimpleNameReferenceNode;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createResourcePathParameterNode;
import static io.ballerina.compiler.syntax.tree.SyntaxKind.CLOSE_BRACKET_TOKEN;
import static io.ballerina.compiler.syntax.tree.SyntaxKind.OPEN_BRACKET_TOKEN;
import static io.ballerina.compiler.syntax.tree.SyntaxKind.SLASH_TOKEN;

public class GeneratorUtils {

    public static final MinutiaeList SINGLE_WS_MINUTIAE = getSingleWSMinutiae();

    private static MinutiaeList getSingleWSMinutiae() {
        Minutiae whitespace = AbstractNodeFactory.createWhitespaceMinutiae(" ");
        MinutiaeList leading = AbstractNodeFactory.createMinutiaeList(whitespace);
        return leading;
    }

    public static QualifiedNameReferenceNode getQualifiedNameReferenceNode(String modulePrefix, String identifier) {
        Token modulePrefixToken = AbstractNodeFactory.createIdentifierToken(modulePrefix);
        Token colon = AbstractNodeFactory.createIdentifierToken(":");
        IdentifierToken identifierToken = AbstractNodeFactory.createIdentifierToken(identifier);
        return NodeFactory.createQualifiedNameReferenceNode(modulePrefixToken, colon, identifierToken);
    }

    public static ImportDeclarationNode getImportDeclarationNode(String orgName, String moduleName) {

        Token importKeyword = AbstractNodeFactory.createIdentifierToken("import", SINGLE_WS_MINUTIAE,
                SINGLE_WS_MINUTIAE);
        Token orgNameToken = AbstractNodeFactory.createIdentifierToken(orgName);
        Token slashToken = AbstractNodeFactory.createIdentifierToken("/");
        ImportOrgNameNode importOrgNameNode = NodeFactory.createImportOrgNameNode(orgNameToken, slashToken);
        Token moduleNameToken = AbstractNodeFactory.createIdentifierToken(moduleName);
        SeparatedNodeList<IdentifierToken> moduleNodeList = AbstractNodeFactory
                .createSeparatedNodeList(moduleNameToken);
        Token semicolon = AbstractNodeFactory.createIdentifierToken(";");

        return NodeFactory.createImportDeclarationNode(importKeyword, importOrgNameNode, moduleNodeList, null,
                semicolon);
    }

//    public static <T extends Node> NodeList<T> createNodeList(T... nodes) {
//        STNode[] internalNodes = new STNode[nodes.length];
//
//        for(int index = 0; index < nodes.length; ++index) {
//            T node = nodes[index];
//            Objects.requireNonNull(node, "node should not be null");
//            internalNodes[index] = node.internalNode();
//        }
//
//        return new NodeList((NonTerminalNode) STNodeFactory.createNodeList(internalNodes).createUnlinkedFacade());
//    }

    /**
     * Generated resource function relative path node list.
     *
     * @param dispatcherHelper - Dispatch helper
     * @param pathParams
     * @param queryParams
     * @return - node lists
     * @throws BallerinaGeneratorException
     */
    public static NodeList<Node> getRelativeResourcePath(DispatcherHelper dispatcherHelper, List<String> pathParams,
                                                         List<String> queryParams)
            throws BallerinaGeneratorException {
        List<Node> functionRelativeResourcePath = new ArrayList<>();
        String uri = dispatcherHelper.getString();
        String[] uriParts = uri.split(GeneratorConstants.QUERY_PARAM_SEPARATOR);
        String path = uriParts[0];
        String[] pathNodes = path.split(GeneratorConstants.SLASH);
        if (pathNodes.length >= 2) {
            for (String pathNode : pathNodes) {
                if (pathNode.equals(GeneratorConstants.ASTERISK)) {
                    continue;
                }
                if (pathNode.contains(GeneratorConstants.OPEN_CURLY_BRACE)) {
                    String pathParam = pathNode;
                    pathParam = pathParam.substring(pathParam.indexOf(GeneratorConstants.OPEN_CURLY_BRACE) + 1);
                    pathParam = pathParam.substring(0, pathParam.indexOf(GeneratorConstants.CLOSE_CURLY_BRACE));
//                    pathParam = escapeIdentifier(pathParam);
                    pathParams.add(pathParam);
                    extractPathParameterDetails(functionRelativeResourcePath, pathNode, pathParam);
                } else if (!pathNode.isBlank()) {
                    IdentifierToken idToken = createIdentifierToken(pathNode.trim());
                    functionRelativeResourcePath.add(idToken);
                    functionRelativeResourcePath.add(createToken(SLASH_TOKEN));
                }
            }
            functionRelativeResourcePath.remove(functionRelativeResourcePath.size() - 1);
        } else if (pathNodes.length == 0) {
            IdentifierToken idToken = createIdentifierToken(".");
            functionRelativeResourcePath.add(idToken);
        } else {
            IdentifierToken idToken = createIdentifierToken(pathNodes[1].trim());
            functionRelativeResourcePath.add(idToken);
        }
        populateQueryParameters(uriParts, queryParams);
        return createNodeList(functionRelativeResourcePath);
    }

    private static void extractPathParameterDetails(List<Node> functionRelativeResourcePath,
                                                    String pathNode, String pathParam) throws BallerinaGeneratorException {
        // check whether path parameter segment has special character
        String[] split = pathNode.split(GeneratorConstants.CLOSE_CURLY_BRACE, 2);
        Pattern pattern = Pattern.compile(GeneratorConstants.SPECIAL_CHARACTERS_REGEX);
        Matcher matcher = pattern.matcher(split[1]);
        boolean hasSpecialCharacter = matcher.find();
        String paramType = GeneratorConstants.STRING;
        BuiltinSimpleNameReferenceNode builtSNRNode = createBuiltinSimpleNameReferenceNode(null,
                createIdentifierToken(paramType));
        IdentifierToken paramName = createIdentifierToken(pathParam);
        ResourcePathParameterNode resourcePathParameterNode = createResourcePathParameterNode(
                SyntaxKind.RESOURCE_PATH_SEGMENT_PARAM, createToken(OPEN_BRACKET_TOKEN),
                NodeFactory.createEmptyNodeList(), builtSNRNode, null, paramName,
                createToken(CLOSE_BRACKET_TOKEN));
        functionRelativeResourcePath.add(resourcePathParameterNode);
        functionRelativeResourcePath.add(createToken(SLASH_TOKEN));
    }

    public static void populateQueryParameters(String[] uriParts , List<String> queryParams) {
        String query = uriParts.length > 1 ? uriParts[1] : null;
        if (query != null && !query.isBlank()) {
            String[] queryNodes = query.split("&");  // Split query by "&"
            for (String queryNode : queryNodes) {
                String[] paramPair = queryNode.split("=");  // Split by "="
                String queryParam = paramPair[0];  // Extract query parameter name
                queryParams.add(queryParam);  // Add query param to list
                // Optional: Extract query parameter value if needed: String value = paramPair[1];
            }
        }
    }
}
