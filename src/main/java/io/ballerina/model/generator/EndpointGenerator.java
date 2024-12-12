package io.ballerina.model.generator;

import com.damnhandy.uri.template.UriTemplate;
import io.ballerina.object.model.BallerinaPackage;
import org.apache.synapse.endpoints.Endpoint;
import org.apache.synapse.endpoints.HTTPEndpoint;
import org.jetbrains.annotations.NotNull;

public class EndpointGenerator {

    private final Endpoint endpoint;
    private final ModelEnvironment modelEnvironment;
    private static final String URI_VAR = "uri.var.";

    public EndpointGenerator(Endpoint endpoint, ModelEnvironment modelEnvironment) {
        this.endpoint = endpoint;
        this.modelEnvironment = modelEnvironment;
    }

    public void generateEndpoint() {
        if (!(endpoint instanceof HTTPEndpoint httpEndpoint)) {
            throw new BallerinaGeneratorException("Unsupported endpoint type: " + endpoint.getClass().getName());
        }
        String name = httpEndpoint.getName();
        String clientStatement = getClientStatement(httpEndpoint, name);
        modelEnvironment.addStatement(new BallerinaPackage.BallerinaStatement(clientStatement));
    }

    @NotNull
    private static String getClientStatement(HTTPEndpoint httpEndpoint, String name) {
        UriTemplate url = httpEndpoint.getUriTemplate();
        String uriString = url.getTemplate();
        String[] variables = url.getVariables();
        for (String variable : variables) {
            if (variable.startsWith(URI_VAR)) {
                String varName = variable.substring(URI_VAR.length());
                uriString = uriString.replace("{" + variable + "}", "${" + varName + "}");
            }
        }
        return "http:Client " + name + " = check new (string `" + uriString + "`);";
    }
}
