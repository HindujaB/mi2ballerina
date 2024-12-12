package io.ballerina.model.generator.expression;

import io.ballerina.model.generator.BallerinaGeneratorException;
import io.ballerina.model.generator.GeneratorConstants;
import io.ballerina.model.generator.ModelEnvironment;
import org.apache.synapse.util.xpath.SynapseXPath;
import org.apache.synapse.util.xpath.SynapseXPathConstants;
import org.jaxen.expr.Expr;
import org.jaxen.expr.FilterExpr;
import org.jaxen.expr.FunctionCallExpr;
import org.jaxen.expr.LiteralExpr;
import org.jaxen.expr.LocationPath;

import java.util.List;

public class XPathExpressionHandler extends ExpressionHandler {

    private Expr rootExpr;

    public XPathExpressionHandler(SynapseXPath expression, ModelEnvironment modelEnvironment) {
        super(expression);
        this.modelEnvironment = modelEnvironment;
    }

    @Override
    public String getExpressionString() {
        rootExpr = expression.getRootExpr();
        return resolve();
    }

    /**
     * Resolves the root expression and returns the resolved string representation.
     */
    public String resolve() {
        return resolveExpression(rootExpr);
    }

    /**
     * Resolves an expression and returns its string representation.
     */
    private String resolveExpression(Expr expr) {
        return switch (expr) {
            case LocationPath locationPath -> handleLocationPath(locationPath);
            case FunctionCallExpr functionCallExpr -> handleFunctionCall(functionCallExpr);
            case FilterExpr filterExpr -> handleFilterExpr(filterExpr);
            case LiteralExpr literalExpr -> handleLiteralExpr(literalExpr);
            case null, default -> {
                String className = expr == null ? "null" : expr.getClass().getSimpleName();
                throw new UnsupportedOperationException("Unsupported expression type: " + className);
            }
        };
    }

    private String handleLocationPath(LocationPath locationPath) {
        String path = locationPath.getText();
        System.out.println("Resolving LocationPath: " + path);
//        modelEnvironment.update("locationPath", path);
        return path;
    }

    private String handleFunctionCall(FunctionCallExpr functionCall) {
        String functionName = functionCall.getFunctionName();
        List<?> parameters = functionCall.getParameters();
        switch (functionName) {
            case SynapseXPathConstants.GET_PROPERTY_FUNCTION:
                if (parameters.size() == 1) {
                    return resolveExpression((Expr) parameters.getFirst());
                }
                return resolveExpression((Expr) parameters.get(1));
            case SynapseXPathConstants.BASE64_ENCODE_FUNCTION:
                String baseStr;
                if (parameters.size() == 1) {
                    baseStr = resolveExpression((Expr) parameters.getFirst());
                } else {
                    baseStr = resolveExpression((Expr) parameters.get(1));
                }
                return baseStr + ".toBytes().toBase64()";
            case GeneratorConstants.CONCAT:
                StringBuilder concatStr = new StringBuilder();
                for (int i = 0; i < parameters.size(); i++) {
                    String param = resolveExpression((Expr) parameters.get(i));
                    if (parameters.get(i) instanceof LiteralExpr) {
                        concatStr.append("\"").append(param).append("\"");
                    } else {
                        concatStr.append(param);
                    }
                    if (i < parameters.size() - 1) {
                        concatStr.append(" + ");
                    }
                }
                return concatStr.toString();
            default:
                throw new BallerinaGeneratorException("Unsupported function: " + functionName);

        }
    }

    private String handleFilterExpr(FilterExpr filterExpr) {
        System.out.println("Resolving FilterExpr: " + filterExpr.getText());
        // Recursively resolve child expressions
        String childExpr = resolveExpression(filterExpr.getExpr());
        //        modelEnvironment.update("filterExpr", resolvedFilter);
        return "[" + childExpr + "]";
    }

    private String handleLiteralExpr(LiteralExpr literalExpr) {
        return literalExpr.getLiteral();
    }
}
