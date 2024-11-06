package io.ballerina.syntax.tree.generator;

import io.ballerina.compiler.syntax.tree.ReturnTypeDescriptorNode;
import io.ballerina.compiler.syntax.tree.TypeDescriptorNode;

import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createEmptyNodeList;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createIdentifierToken;
import static io.ballerina.compiler.syntax.tree.AbstractNodeFactory.createToken;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createReturnTypeDescriptorNode;
import static io.ballerina.compiler.syntax.tree.NodeFactory.createSimpleNameReferenceNode;
import static io.ballerina.compiler.syntax.tree.SyntaxKind.RETURNS_KEYWORD;

public class ReturnTypeGenerator {

    public ReturnTypeDescriptorNode getReturnTypeDescriptorNode() {
        TypeDescriptorNode defaultType = createSimpleNameReferenceNode(createIdentifierToken(
                GeneratorConstants.HTTP_RESPONSE));
        return createReturnTypeDescriptorNode(createToken(RETURNS_KEYWORD), createEmptyNodeList(),
                defaultType);
    }
}
