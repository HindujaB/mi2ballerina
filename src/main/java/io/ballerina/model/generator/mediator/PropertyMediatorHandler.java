package io.ballerina.model.generator.mediator;

import io.ballerina.model.generator.BallerinaGeneratorException;
import io.ballerina.model.generator.GeneratorConstants;
import io.ballerina.model.generator.ModelEnvironment;
import io.ballerina.model.generator.expression.ExpressionHandlerFactory;
import io.ballerina.object.model.BallerinaPackage;
import org.apache.synapse.config.xml.SynapsePath;
import org.apache.synapse.mediators.builtin.PropertyMediator;

/**
 * {@code PropertyMediatorHandler} responsible for handling {@code PropertyMediator} in ballerina.
 * This can be mapped to ballerina variable assignment.
 */
public class PropertyMediatorHandler extends MediatorHandler {

    public PropertyMediatorHandler(PropertyMediator mediator) {
        super(mediator);
    }

    @Override
    public void handleMediator(ModelEnvironment modelEnvironment) {
//        modelEnvironment.enterContext(mediator); // not needed as it adds to the parent context
        String type = getVariableType();
        String name = ((PropertyMediator) mediator).getName();
        Object value = ((PropertyMediator) mediator).getValue();
        if (value == null) {
            SynapsePath expr = ((PropertyMediator) mediator).getExpression();
            String expression = ExpressionHandlerFactory.getHandler(expr, modelEnvironment).getExpressionString();
            BallerinaPackage.Statement statement = new BallerinaPackage.BallerinaStatement(type + " " + name + " = "
                                       + GeneratorConstants.CHECK + " " + expression + ";");
            modelEnvironment.addStatement(statement);
        }

    }

    private String getVariableType() {
        return switch (mediator.getType()) {
            case "STRING" -> "string";
            case "INTEGER", "SHORT" -> "byte";
            case "BOOLEAN" -> "boolean";
            case "DOUBLE", "FLOAT" -> "float";
            case "LONG" -> "int";
            case "OM" -> "xml";
            case "JSON" -> "json";
            default -> throw new BallerinaGeneratorException("Unsupported variable type");
        };
    }
}
