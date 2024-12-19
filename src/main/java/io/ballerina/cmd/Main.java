package io.ballerina.cmd;

import io.ballerina.model.generator.BallerinaModelBuilder;
import io.ballerina.object.model.BallerinaPackage;
import io.ballerina.source.writer.BalSourceWriter;
import io.ballerina.synapse.parser.SynapseConfigParser;
import org.apache.synapse.config.SynapseConfiguration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Main {

    private static String ARTIFACT_PATH = "src/main/wso2mi/artifacts";
    public static void main(String[] args) {

        if (args.length < 2) {
            System.err.println("Usage: java -jar SynapseConfigProcessor.jar <inputPath> <outputPath>");
            System.exit(1);
        }

        String inputPath = args[0];
        String outputPath = args[1];
        SynapseConfiguration config;
        try {
            Path path = Paths.get(inputPath, ARTIFACT_PATH);
            // Parse the Synapse configuration
            config = SynapseConfigParser.getConfiguration(path.toString());
            Objects.requireNonNull(config, "SynapseConfiguration is null. Please check the input path.");

            // Generate the Ballerina model
            BallerinaModelBuilder ballerinaModelGenerator = new BallerinaModelBuilder();
            BallerinaPackage ballerinaPackage = ballerinaModelGenerator.generateBallerinaModel(config);

            // Define the output path and write the Ballerina source
            Path outPath = Paths.get(outputPath);
            if (!outPath.toFile().exists()) {
                outPath.toFile().mkdirs();
            }
            BalSourceWriter.writeBalSource(ballerinaPackage, outPath);

            System.out.println("Ballerina source files generated successfully at: " + outPath.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error processing Synapse configuration: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
        System.exit(0); // need to remove
    }
}
