package io.ballerina.synapse.parser;

import io.ballerina.model.generator.BallerinaModelBuilder;
import io.ballerina.object.model.BallerinaPackage;
import io.ballerina.source.writer.BalSourceWriter;
import org.apache.synapse.config.SynapseConfiguration;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertNotNull;

public class ParserTest {

    @Test (enabled = false)
    public void testParseSynapseConfig() {
        String xmlFilePath = "src/test/resources/sampleProject";

        // Call the method to parse the XML configuration
        SynapseConfiguration config = SynapseConfigParser.getConfiguration(xmlFilePath);
        // Validate that the configuration is not null
        assertNotNull(config, "SynapseConfiguration should not be null");
        BallerinaModelBuilder ballerinaModelGenerator = new BallerinaModelBuilder();
        BallerinaPackage ballerinaPackage = ballerinaModelGenerator.generateBallerinaModel(config);
        Path outpath = Paths.get("src/test/resources", "output", "sampleProject");
        BalSourceWriter.writeBalSource(ballerinaPackage, outpath);
    }

    @Test
    public void testParseSynapseConfigMessageRouting() {
//        String xmlFilePath = "src/test/resources/sampleService";
        String xmlFilePath = "/Users/hinduja/Documents/WSO2_MI/synapse-projects/SampleServices/src/main/wso2mi/artifacts";
        // Call the method to parse the XML configuration
        SynapseConfiguration config = SynapseConfigParser.getConfiguration(xmlFilePath );
        // Validate that the configuration is not null
        assertNotNull(config, "SynapseConfiguration should not be null");
        BallerinaModelBuilder ballerinaModelGenerator = new BallerinaModelBuilder();
        BallerinaPackage ballerinaPackage = ballerinaModelGenerator.generateBallerinaModel(config);
        Path outpath = Paths.get("src/test/resources", "output", "sampleService");
        BalSourceWriter.writeBalSource(ballerinaPackage, outpath);
//        BCompileUtil.compile(outpath.toString());
    }

    @Test(expectedExceptions = Exception.class, enabled = false)
    public void testParseSynapseConfigWithInvalidPath() {
        String invalidPath = "invalid/path/to/synapse.xml";

        // Expecting the parsing to throw an exception when the file path is invalid
        SynapseConfiguration configuration = SynapseConfigParser.getConfiguration(invalidPath);
        BallerinaModelBuilder ballerinaModelGenerator = new BallerinaModelBuilder();
        ballerinaModelGenerator.generateBallerinaModel(configuration);
    }
}
