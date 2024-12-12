package io.ballerina.model.generator.expression;

import io.ballerina.model.generator.BallerinaGeneratorException;
import io.ballerina.model.generator.ModelEnvironment;
import org.apache.synapse.config.xml.SynapsePath;
import org.apache.synapse.util.xpath.SynapseJsonPath;
import org.apache.synapse.util.xpath.SynapseXPath;

public class ExpressionHandlerFactory {

    public static ExpressionHandler getHandler(SynapsePath expression, ModelEnvironment modelEnvironment) {
        return switch (expression.getPathType()) {
            case SynapsePath.X_PATH-> new XPathExpressionHandler((SynapseXPath) expression, modelEnvironment);
            case SynapsePath.JSON_PATH ->
                    new JsonPathExpressionHandler((SynapseJsonPath) expression, modelEnvironment);
            default -> throw new BallerinaGeneratorException("Expression type yet to be supported");
        };
    }

}
