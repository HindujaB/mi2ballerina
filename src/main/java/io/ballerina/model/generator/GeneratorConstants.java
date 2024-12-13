package io.ballerina.model.generator;

public class GeneratorConstants {

    public static final String BALLERINA = "ballerina";
    public static final String HTTP = "http";
    public static final String LISTENER_TYPE = "http:Listener";

    //API related
    public static final String HOST = "host";
    public static final int DEFAULT_PORT = 8290;

    // Resource related
    public static final String SLASH = "/";
    public static final String DOT = ".";
    public static final String OPEN_BRACKET = "[";
    public static final String CLOSE_BRACKET = "]";
    public static final String OPEN_CURLY_BRACE = "{";
    public static final String CLOSE_CURLY_BRACE = "}";
    public static final String SPECIAL_CHARACTERS_REGEX = "[^a-zA-Z0-9]";
    public static final String ASTERISK = "*";

    public static final String STRING = "string";
    public static final String QUERY_PARAM_SEPARATOR = "\\?";
    public static final String HTTP_RESPONSE = "http:Response";

    public static final String DEFAULT_PACKAGE_NAME = "sample_service";
    public static final String DEFAULT_ORG_NAME = "testOrg";
    public static final String DEFAULT_VERSION = "0.1.0";

    public static final String SPACE = " ";
    public static final String PAYLOAD_VAR = "payload";
    public static final String CALLER_VAR = "caller";
    public static final String REQUEST_VAR = "request";
    public static final String RESPONSE_VAR = "response";

    public static final String HTTP_PAYLOAD_ANNOTATION = "@http:Payload";
    public static final String CHECK = "check";
    public static final String CONCAT = "concat";
    public static final String LOG = "log";
    public static final String ERROR_OR_NIL = "error?";
    public static final String PORT = "port";
    public static final String LISTENER_VAR = "\\$listener";
}
