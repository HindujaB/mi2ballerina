package io.ballerina.model.generator.mediator;

import io.ballerina.model.generator.EndpointGenerator;
import io.ballerina.model.generator.ModelEnvironment;
import io.ballerina.object.model.BallerinaPackage;
import org.apache.synapse.endpoints.Endpoint;
import org.apache.synapse.endpoints.IndirectEndpoint;
import org.apache.synapse.mediators.builtin.CallMediator;

import java.util.Locale;

import static io.ballerina.model.generator.GeneratorConstants.HTTP_RESPONSE;
import static io.ballerina.model.generator.GeneratorConstants.REQUEST_VAR;
import static io.ballerina.model.generator.GeneratorConstants.RESPONSE_VAR;

public class CallMediatorHandler extends MediatorHandler {

    public CallMediatorHandler(CallMediator mediator) {
        super(mediator);
    }

    @Override
    public void handleMediator(ModelEnvironment modelEnvironment) {
        //        modelEnvironment.enterContext(mediator); // not needed as it adds to the parent context
        CallMediator callMediator = (CallMediator) mediator;
        Endpoint endpoint = callMediator.getEndpoint();
        StringBuilder statementBuilder = new StringBuilder();
        if (!modelEnvironment.getLocalVars().containsKey(RESPONSE_VAR)) {
            BallerinaPackage.Variable res = new BallerinaPackage.Variable(RESPONSE_VAR, HTTP_RESPONSE, null);
            String responseVar = HTTP_RESPONSE + " " + RESPONSE_VAR + " = new;";
            modelEnvironment.addParentStatement(new BallerinaPackage.BallerinaStatement(responseVar));
            modelEnvironment.getLocalVars().put(RESPONSE_VAR, res);
        }
        if (!modelEnvironment.getParameters().containsKey(REQUEST_VAR)) {
            BallerinaPackage.Parameter req = new BallerinaPackage.Parameter(REQUEST_VAR, "http:Request", "");
            modelEnvironment.getParameters().put(REQUEST_VAR, req);
        }

        statementBuilder.append(RESPONSE_VAR).append(" = check ");
        statementBuilder.append(getEndpointName(endpoint, modelEnvironment)).append("->");
        statementBuilder.append(modelEnvironment.currentResourceMethod.toLowerCase(Locale.ROOT)).append("(");
        statementBuilder.append("\"\"").append(",").append(REQUEST_VAR).append(");");
        BallerinaPackage.Statement call = new BallerinaPackage.BallerinaStatement(statementBuilder.toString());
        modelEnvironment.addStatement(call);

    }

    private String getEndpointName(Endpoint endpoint, ModelEnvironment modelEnvironment) {
        if (endpoint instanceof IndirectEndpoint indirectEndpoint) {
            String key = indirectEndpoint.getKey();
            Endpoint originalEndpoint = modelEnvironment.getOriginalEndpoint(key);
            EndpointGenerator endpointGenerator = new EndpointGenerator(originalEndpoint, modelEnvironment);
            endpointGenerator.generateEndpoint();
            return key;
        }
        return "";
    }
}
