package io.ballerina.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.AbstractNodeFactory;
import io.ballerina.compiler.syntax.tree.BasicLiteralNode;
import io.ballerina.compiler.syntax.tree.FunctionArgumentNode;
import io.ballerina.compiler.syntax.tree.IdentifierToken;
import io.ballerina.compiler.syntax.tree.ImplicitNewExpressionNode;
import io.ballerina.compiler.syntax.tree.ListenerDeclarationNode;
import io.ballerina.compiler.syntax.tree.MappingConstructorExpressionNode;
import io.ballerina.compiler.syntax.tree.MappingFieldNode;
import io.ballerina.compiler.syntax.tree.NamedArgumentNode;
import io.ballerina.compiler.syntax.tree.NodeFactory;
import io.ballerina.compiler.syntax.tree.ParenthesizedArgList;
import io.ballerina.compiler.syntax.tree.PositionalArgumentNode;
import io.ballerina.compiler.syntax.tree.QualifiedNameReferenceNode;
import io.ballerina.compiler.syntax.tree.SeparatedNodeList;
import io.ballerina.compiler.syntax.tree.SimpleNameReferenceNode;
import io.ballerina.compiler.syntax.tree.SyntaxKind;
import io.ballerina.compiler.syntax.tree.Token;
import org.apache.synapse.api.API;

import java.util.ArrayList;
import java.util.List;
import javax.tools.Diagnostic;

public class ListenerGenerator {

    private String basePath = "/";
    private final List<Diagnostic> diagnostics = new ArrayList<>();

    public String getBasePath() {
        return basePath;
    }

    /**
     * Generate listener according to the given server details.
     * E
     *
     *
     * @return {@link ListenerDeclarationNode} for server.
     */
    public ListenerDeclarationNode getListenerDeclarationNode(API api) {
        // Assign host port value to listeners
        String host = api.getHost() == null ? "localhost" : api.getHost();
        int port = api.getPort() == -1 ? GeneratorConstants.DEFAULT_PORT : api.getPort();
        basePath = api.getContext();
//      TODO: incorporate version property
        return getListenerDeclarationNode(port, host, api.getName());
    }

    private ListenerDeclarationNode getListenerDeclarationNode(Integer port, String host, String ep) {
        // Take first server to Map
        Token listenerKeyword = AbstractNodeFactory.createIdentifierToken("listener", GeneratorUtils.SINGLE_WS_MINUTIAE,
                GeneratorUtils.SINGLE_WS_MINUTIAE);
        // Create type descriptor
        Token modulePrefix = AbstractNodeFactory.createIdentifierToken("http", GeneratorUtils.SINGLE_WS_MINUTIAE,
                GeneratorUtils.SINGLE_WS_MINUTIAE);
        IdentifierToken identifier = AbstractNodeFactory.createIdentifierToken("Listener",
                GeneratorUtils.SINGLE_WS_MINUTIAE, AbstractNodeFactory.createEmptyMinutiaeList());
        QualifiedNameReferenceNode typeDescriptor = NodeFactory.createQualifiedNameReferenceNode(modulePrefix,
                AbstractNodeFactory.createToken(SyntaxKind.COLON_TOKEN), identifier);
        // Create variable
        Token variableName = AbstractNodeFactory.createIdentifierToken(ep, GeneratorUtils.SINGLE_WS_MINUTIAE,
                AbstractNodeFactory.createEmptyMinutiaeList());
        // Create initializer
        Token newKeyword = AbstractNodeFactory.createIdentifierToken(GeneratorConstants.NEW);

        Token literalToken = AbstractNodeFactory.createLiteralValueToken(SyntaxKind.DECIMAL_INTEGER_LITERAL_TOKEN
                , String.valueOf(port), GeneratorUtils.SINGLE_WS_MINUTIAE, GeneratorUtils.SINGLE_WS_MINUTIAE);
        BasicLiteralNode expression = NodeFactory.createBasicLiteralNode(SyntaxKind.NUMERIC_LITERAL, literalToken);

        PositionalArgumentNode portNode = NodeFactory.createPositionalArgumentNode(expression);

        Token name = AbstractNodeFactory.createIdentifierToken(GeneratorConstants.CONFIG);
        SimpleNameReferenceNode argumentName = NodeFactory.createSimpleNameReferenceNode(name);

        Token fieldName = AbstractNodeFactory.createIdentifierToken(GeneratorConstants.HOST);
        Token literalHostToken = AbstractNodeFactory.createIdentifierToken('"' + host + '"',
                GeneratorUtils.SINGLE_WS_MINUTIAE, GeneratorUtils.SINGLE_WS_MINUTIAE);
        BasicLiteralNode valueExpr = NodeFactory.createBasicLiteralNode(SyntaxKind.STRING_LITERAL,
                literalHostToken);
        MappingFieldNode hostNode = NodeFactory.createSpecificFieldNode(null, fieldName,
                AbstractNodeFactory.createToken(SyntaxKind.COLON_TOKEN), valueExpr);
        SeparatedNodeList<MappingFieldNode> fields = NodeFactory.createSeparatedNodeList(hostNode);

        MappingConstructorExpressionNode hostExpression = NodeFactory.createMappingConstructorExpressionNode(
                AbstractNodeFactory.createToken(SyntaxKind.OPEN_BRACE_TOKEN),
                fields, AbstractNodeFactory.createToken(SyntaxKind.CLOSE_BRACE_TOKEN));

        NamedArgumentNode namedArgumentNode = NodeFactory.createNamedArgumentNode(argumentName,
                AbstractNodeFactory.createToken(SyntaxKind.EQUAL_TOKEN), hostExpression);

        SeparatedNodeList<FunctionArgumentNode> arguments = NodeFactory.createSeparatedNodeList(portNode,
                AbstractNodeFactory.createToken(SyntaxKind.COMMA_TOKEN), namedArgumentNode);

        ParenthesizedArgList parenthesizedArgList = NodeFactory.createParenthesizedArgList(
                AbstractNodeFactory.createToken(SyntaxKind.OPEN_PAREN_TOKEN), arguments,
                AbstractNodeFactory.createToken(SyntaxKind.CLOSE_PAREN_TOKEN));
        ImplicitNewExpressionNode initializer = NodeFactory.createImplicitNewExpressionNode(newKeyword,
                parenthesizedArgList);

        return NodeFactory.createListenerDeclarationNode(null, null, listenerKeyword,
                typeDescriptor, variableName, AbstractNodeFactory.createToken(SyntaxKind.EQUAL_TOKEN), initializer,
                AbstractNodeFactory.createToken(SyntaxKind.SEMICOLON_TOKEN));
    }

    public List<Diagnostic> getDiagnostics() {
        return diagnostics;
    }

}
