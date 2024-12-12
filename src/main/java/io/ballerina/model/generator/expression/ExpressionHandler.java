package io.ballerina.model.generator.expression;

import io.ballerina.model.generator.ModelEnvironment;
import org.apache.synapse.config.xml.SynapsePath;

public abstract class ExpressionHandler {

    protected final SynapsePath expression;
    protected ModelEnvironment modelEnvironment;

    public ExpressionHandler(SynapsePath expression) {
        this.expression = expression;

    }

    public abstract String getExpressionString();
}
