package io.ballerina.model.generator.mediator;

import io.ballerina.model.generator.ModelEnvironment;
import org.apache.synapse.Mediator;
import org.apache.synapse.mediators.AbstractListMediator;

public class AbstractListMediatorHandler extends MediatorHandler {

    public AbstractListMediatorHandler(AbstractListMediator mediator) {
        super(mediator);
    }

    @Override
    public void handleMediator(ModelEnvironment modelEnvironment) {
        modelEnvironment.enterContext(mediator);
        for (Mediator child : ((AbstractListMediator) mediator).getList()) {
            MediatorHandler handler = MediatorHandlerFactory.getHandler(child);
            handler.handleMediator(modelEnvironment);
        }
    }
}
