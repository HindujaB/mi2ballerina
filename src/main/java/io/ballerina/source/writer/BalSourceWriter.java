package io.ballerina.source.writer;

import io.ballerina.object.model.BallerinaPackage;
import io.ballerina.object.syntax.tree.generator.BallerinaCodeBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BalSourceWriter {

    public static void writeBalSource(BallerinaPackage ballerinaPackage, Path outpath) {
        BallerinaCodeBuilder builder = new BallerinaCodeBuilder();
        builder.build(ballerinaPackage, outpath);;
    }

}
