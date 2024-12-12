package io.ballerina.model.generator;

public class BallerinaGeneratorException extends RuntimeException {

    public BallerinaGeneratorException(String message) {
        super(message);
    }

    public BallerinaGeneratorException(String message, RuntimeException e) {
        super(message, e);
    }
}
