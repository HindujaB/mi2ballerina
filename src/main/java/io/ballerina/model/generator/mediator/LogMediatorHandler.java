package io.ballerina.model.generator.mediator;

import io.ballerina.model.generator.GeneratorConstants;
import io.ballerina.model.generator.ModelEnvironment;
import io.ballerina.model.generator.expression.ExpressionHandlerFactory;
import io.ballerina.object.model.BallerinaPackage;
import org.apache.synapse.mediators.builtin.LogMediator;

import java.util.ArrayList;
import java.util.List;

public class LogMediatorHandler extends MediatorHandler {

    public LogMediatorHandler(LogMediator logMediator) {
       super(logMediator);
    }

    @Override
    public void handleMediator(ModelEnvironment modelEnvironment) {
//        modelEnvironment.enterContext(mediator); // not needed as it adds to the parent context
        LogMediator logMediator = (LogMediator) mediator;
        modelEnvironment.addImport(new BallerinaPackage.Import(GeneratorConstants.BALLERINA, GeneratorConstants.LOG));
        String logFunction = "log:" + getLogFunction(logMediator.getCategory());
        List<String> parameters = new ArrayList<>();
        logMediator.getProperties().forEach((mediatorProperty) -> {
            System.out.println(mediatorProperty);
            if (mediatorProperty.getName().equals("message")) {
                String expr = ExpressionHandlerFactory.getHandler(mediatorProperty.getExpression(), modelEnvironment)
                        .getExpressionString();
                parameters.add(expr);
            }
        });
        BallerinaPackage.Statement log = new BallerinaPackage.CallStatement(logFunction, parameters);
        modelEnvironment.addStatement(log);
    }

    private String getLogFunction(int category) {
        return switch (category) {
            case LogMediator.CATEGORY_INFO -> "printInfo";
            case LogMediator.CATEGORY_DEBUG , LogMediator.CATEGORY_TRACE -> "printDebug";
            case LogMediator.CATEGORY_WARN -> "printWarn";
            default -> "printError";
        };
    }
}
