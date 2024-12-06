package io.ballerina.model.generator;

import org.apache.synapse.api.dispatch.DispatcherHelper;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneratorUtils {

    /**
     * Generated resource function relative path node list.
     *
     * @param dispatcherHelper - Dispatch helper
     * @param queryParams - query parameters
     * @return - node lists
     * @throws BallerinaGeneratorException
     */
    public static String getRelativeResourcePath(DispatcherHelper dispatcherHelper, List<String> queryParams)
            throws BallerinaGeneratorException {
        StringBuilder pathBuilder = new StringBuilder();
        String uri = dispatcherHelper.getString();
        String[] uriParts = uri.split(GeneratorConstants.QUERY_PARAM_SEPARATOR);
        String path = uriParts[0];
        String[] pathNodes = path.split(GeneratorConstants.SLASH);
        if (pathNodes.length >= 2) {
            int previousLength = 0;
            for (String pathNode : pathNodes) {
                if (pathNode.equals(GeneratorConstants.ASTERISK)) {
                    continue;
                }
                if (pathNode.contains(GeneratorConstants.OPEN_CURLY_BRACE)) {
                    String pathParam = pathNode;
                    pathParam = pathParam.substring(pathParam.indexOf(GeneratorConstants.OPEN_CURLY_BRACE) + 1);
                    pathParam = pathParam.substring(0, pathParam.indexOf(GeneratorConstants.CLOSE_CURLY_BRACE));
                    extractPathParameterDetails(pathBuilder, pathNode, pathParam);
                } else if (!pathNode.isBlank()) {
                    pathBuilder.append(pathNode.trim());
                }
                previousLength = pathBuilder.length();
                pathBuilder.append(GeneratorConstants.SLASH);
            }
            pathBuilder.setLength(previousLength);
        } else if (pathNodes.length == 0) {
            pathBuilder.append(GeneratorConstants.DOT);
        } else {
            pathBuilder.append(pathNodes[1].trim());
        }
        populateQueryParameters(uriParts, queryParams);
        return pathBuilder.toString();
    }

    private static void extractPathParameterDetails(StringBuilder pathBuilder,
                                                   String pathNode, String pathParam) throws BallerinaGeneratorException {
        // check whether path parameter segment has special character
        String[] split = pathNode.split(GeneratorConstants.CLOSE_CURLY_BRACE, 2);
        Pattern pattern = Pattern.compile(GeneratorConstants.SPECIAL_CHARACTERS_REGEX);
        Matcher matcher = pattern.matcher(split[1]);
        boolean hasSpecialCharacter = matcher.find();
        String paramType = GeneratorConstants.STRING;
        pathBuilder.append(GeneratorConstants.OPEN_BRACKET).append(paramType).append(GeneratorConstants.SPACE)
                .append(pathParam).append(GeneratorConstants.CLOSE_BRACKET);
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
