package io.ballerina.model.generator.mediator;

import io.ballerina.model.generator.ModelEnvironment;
import org.apache.synapse.Mediator;

public abstract class MediatorHandler {

    protected Mediator mediator;

    public MediatorHandler(Mediator mediator) {
        this.mediator = mediator;
    }

    public abstract void handleMediator(ModelEnvironment modelEnvironment);

}
