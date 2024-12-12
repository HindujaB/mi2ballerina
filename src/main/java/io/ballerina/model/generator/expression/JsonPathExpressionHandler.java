package io.ballerina.model.generator.expression;

import com.jayway.jsonpath.JsonPath;
import io.ballerina.model.generator.GeneratorConstants;
import io.ballerina.model.generator.ModelEnvironment;
import io.ballerina.object.model.BallerinaPackage;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.util.xpath.SynapseJsonPath;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonPathExpressionHandler extends ExpressionHandler {
    private static final String EXTRACT_PROP_REGEX = "^(\\$ctx|\\$trp|\\$axis2):([a-zA-Z0-9_-]+)";
    private static final Pattern EXTRACT_PROP_PATTERN = Pattern.compile(EXTRACT_PROP_REGEX);
    private static final String KEY_REGEX = "\\$\\['([^']+)'\\]";
    private static final Pattern KEY_PATTERN = Pattern.compile(KEY_REGEX);

    public JsonPathExpressionHandler(SynapseJsonPath expression, ModelEnvironment modelEnvironment) {
        super(expression);
        this.modelEnvironment = modelEnvironment;
    }

    @Override
    public String getExpressionString() {
        JsonPath jsonPath = ((SynapseJsonPath) expression).getJsonPath();
        String path = jsonPath.getPath();
        String payloadVar;
        Map<String, BallerinaPackage.Parameter> parameters = modelEnvironment.getParameters();
        if (parameters.containsKey(GeneratorConstants.PAYLOAD_VAR)) {
            payloadVar = parameters.get(GeneratorConstants.PAYLOAD_VAR).name();
        } else if (parameters.containsKey(GeneratorConstants.REQUEST_VAR)) {
            if (modelEnvironment.getLocalVars().containsKey(GeneratorConstants.PAYLOAD_VAR)) {
                payloadVar = modelEnvironment.getLocalVars().get(GeneratorConstants.PAYLOAD_VAR).name();
            } else {
                String requestVar = parameters.get(GeneratorConstants.REQUEST_VAR).name();
                BallerinaPackage.Statement json = new BallerinaPackage.BallerinaStatement(
                        "json " + GeneratorConstants.PAYLOAD_VAR + " = check " + requestVar + ".getJsonPayload()");
                modelEnvironment.addStatement(json);
                payloadVar = GeneratorConstants.PAYLOAD_VAR;
            }
        } else {
            payloadVar = GeneratorConstants.PAYLOAD_VAR;
            BallerinaPackage.Parameter parameter = new BallerinaPackage.Parameter(GeneratorConstants.PAYLOAD_VAR,
                    "json", GeneratorConstants.HTTP_PAYLOAD_ANNOTATION);
            parameters.put(GeneratorConstants.PAYLOAD_VAR, parameter);
        }
        return getJsonAccessExpression(payloadVar, path.trim());
    }

    private String getJsonAccessExpression(String payloadVar, String path) {
//        TODO: support special characters with quoted identifiers, escape characters
        Matcher extractPropMatcher = EXTRACT_PROP_PATTERN.matcher(path);
        String resolvedExpression = path;
        String propertyExpression;
        if (extractPropMatcher.find()) {
            propertyExpression = extractPropMatcher.group(0);
            path = path.substring(propertyExpression.length());
        }
        if (path.endsWith(".")) {
            resolvedExpression = resolvedExpression.substring(0, resolvedExpression.length() - 1);
        }
        if ("$".equals(path) || "$.".equals(path)) {
            resolvedExpression = payloadVar;
        }
        Matcher keyMatcher = KEY_PATTERN.matcher(resolvedExpression);
        if (keyMatcher.matches()) {
            String key = keyMatcher.group(1);
            return payloadVar + GeneratorConstants.DOT + key; // Group 1 contains the content inside $['']
        }
        String[] array = resolvedExpression.split("\\.");
        if (array.length > 1) {
            // handle json-path expressions ends with array notation Ex:- $.student.marks[0]
            if (array[array.length - 1].endsWith("]")) {
                array[array.length - 1] = array[array.length - 1].replaceAll("\\[(.*?)\\]", "");
                return StringUtils.join(array, ".");
            } else {
                String[] parent = Arrays.copyOf(array, array.length - 1);
                return StringUtils.join(parent, ".");
            }
        }
        return "";
    }
}
