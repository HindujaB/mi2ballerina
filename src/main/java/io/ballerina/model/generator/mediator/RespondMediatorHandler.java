package io.ballerina.model.generator.mediator;

import io.ballerina.model.generator.ModelEnvironment;
import io.ballerina.object.model.BallerinaPackage;
import org.apache.synapse.mediators.builtin.RespondMediator;

import static io.ballerina.model.generator.GeneratorConstants.CALLER_VAR;
import static io.ballerina.model.generator.GeneratorConstants.CHECK;
import static io.ballerina.model.generator.GeneratorConstants.HTTP_RESPONSE;
import static io.ballerina.model.generator.GeneratorConstants.RESPONSE_VAR;
import static io.ballerina.model.generator.GeneratorConstants.RETURN;

public class RespondMediatorHandler extends MediatorHandler {

    public RespondMediatorHandler(RespondMediator respondMediator) {
        super(respondMediator);
    }

    @Override
    public void handleMediator(ModelEnvironment modelEnvironment) {
        if (!modelEnvironment.getLocalVars().containsKey(RESPONSE_VAR)) {
            BallerinaPackage.Variable res = new BallerinaPackage.Variable(RESPONSE_VAR, HTTP_RESPONSE + "?",
                    "()");
            String responseVar = HTTP_RESPONSE + " " + RESPONSE_VAR + " = new;";
            modelEnvironment.addStatement(new BallerinaPackage.BallerinaStatement(responseVar));
            modelEnvironment.getLocalVars().put(RESPONSE_VAR, res);
        }
        if (!modelEnvironment.getParameters().containsKey(CALLER_VAR)) {
            if (modelEnvironment.getLocalVars().containsKey(RESPONSE_VAR)) {
                String respondStatement = RETURN + " " + RESPONSE_VAR + ";";
                BallerinaPackage.Statement res = new BallerinaPackage.BallerinaStatement(respondStatement);
                modelEnvironment.addStatement(res);
                return;
            }
            BallerinaPackage.Parameter parameter = new BallerinaPackage.Parameter(CALLER_VAR, "http:Caller", "");
            modelEnvironment.getParameters().put(CALLER_VAR, parameter);
        }
        String respondStatement = CHECK + " " + CALLER_VAR + "->respond(" + RESPONSE_VAR + ");";
        BallerinaPackage.Statement res = new BallerinaPackage.BallerinaStatement(respondStatement);
        modelEnvironment.addStatement(res);
    }

}
