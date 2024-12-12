package io.ballerina.model.generator.mediator;

import io.ballerina.model.generator.BallerinaGeneratorException;
import org.apache.synapse.Mediator;

public class MediatorHandlerFactory {

    public static MediatorHandler getHandler(Mediator mediator) {
        return switch (mediator.getClass().getName()) {
            case "org.apache.synapse.mediators.builtin.LogMediator" ->
                    new LogMediatorHandler((org.apache.synapse.mediators.builtin.LogMediator) mediator);
            case "org.apache.synapse.mediators.builtin.PropertyMediator" ->
                    new PropertyMediatorHandler((org.apache.synapse.mediators.builtin.PropertyMediator) mediator);
            case "org.apache.synapse.mediators.filters.SwitchMediator" ->
                    new SwitchMediatorHandler((org.apache.synapse.mediators.filters.SwitchMediator) mediator);
            case "org.apache.synapse.mediators.builtin.RespondMediator" ->
                    new RespondMediatorHandler((org.apache.synapse.mediators.builtin.RespondMediator) mediator);
            case "org.apache.synapse.config.xml.AnonymousListMediator",
                 "org.apache.synapse.mediators.base.SequenceMediator" ->
                    new AbstractListMediatorHandler((org.apache.synapse.mediators.AbstractListMediator) mediator);
            case "org.apache.synapse.mediators.builtin.CallMediator" ->
                    new CallMediatorHandler((org.apache.synapse.mediators.builtin.CallMediator) mediator);
            default -> throw new BallerinaGeneratorException("Mediator type yet to be supported " +
                                                             mediator.getClass().getName());
        };
    }
}
