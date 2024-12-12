package io.ballerina.model.generator.mediator;

import io.ballerina.model.generator.ModelEnvironment;
import io.ballerina.model.generator.expression.ExpressionHandlerFactory;
import io.ballerina.object.model.BallerinaPackage;
import org.apache.synapse.config.xml.AnonymousListMediator;
import org.apache.synapse.config.xml.SwitchCase;
import org.apache.synapse.config.xml.SynapsePath;
import org.apache.synapse.mediators.filters.SwitchMediator;

import java.util.ArrayList;
import java.util.List;

public class SwitchMediatorHandler extends MediatorHandler {

    public SwitchMediatorHandler(SwitchMediator mediator) {
        super(mediator);
    }

    @Override
    public void handleMediator(ModelEnvironment modelEnvironment) {
//        modelEnvironment.enterContext(mediator); // not needed as it adds to the parent context
        SwitchMediator switchMediator = (SwitchMediator) mediator;
        SynapsePath source = switchMediator.getSource();
        String switchSrc = ExpressionHandlerFactory.getHandler(source, modelEnvironment).getExpressionString();
        List<BallerinaPackage.MatchPattern> matchPatternList = new ArrayList<>();
        for (SwitchCase caseBranch : switchMediator.getCases()) {
            String clause = caseBranch.getRegex().pattern();
            AnonymousListMediator caseMediator = caseBranch.getCaseMediator();
            MediatorHandlerFactory.getHandler(caseMediator).handleMediator(modelEnvironment);
            matchPatternList.add(new BallerinaPackage.MatchPattern("\"" + clause + "\"",
                    modelEnvironment.exitContext()));
        }
        BallerinaPackage.Statement statement = new BallerinaPackage.MatchStatement(switchSrc, matchPatternList);
        modelEnvironment.addStatement(statement);
    }

}
