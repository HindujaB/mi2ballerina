package io.ballerina.synapse.parser;

import io.ballerina.object.model.BallerinaPackage;
import io.ballerina.source.writer.BalSourceWriter;
import io.ballerina.model.generator.BallerinaModelGenerator;
import org.apache.synapse.config.SynapseConfiguration;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertNotNull;

public class ParserTest {

    @Test
    public void testParseSynapseConfig() {
        String xmlFilePath = "src/test/resources/sampleProject";

        // Call the method to parse the XML configuration
        SynapseConfiguration config = SynapseConfigParser.parseSynapseConfig(xmlFilePath);
        // Validate that the configuration is not null
        assertNotNull(config, "SynapseConfiguration should not be null");
        BallerinaModelGenerator ballerinaModelGenerator = new BallerinaModelGenerator();
        BallerinaPackage ballerinaPackage = ballerinaModelGenerator.generateBallerinaModel(config);
        Path outpath = Paths.get("src/test/resources", "output");
        BalSourceWriter.writeBalSource(ballerinaPackage, outpath);
    }

    @Test(expectedExceptions = Exception.class, enabled = false)
    public void testParseSynapseConfigWithInvalidPath() throws Exception {
        String invalidPath = "invalid/path/to/synapse.xml";

        // Expecting the parsing to throw an exception when the file path is invalid
        SynapseConfiguration configuration = SynapseConfigParser.parseSynapseConfig(invalidPath);
        BallerinaModelGenerator ballerinaModelGenerator = new BallerinaModelGenerator();
        ballerinaModelGenerator.generateBallerinaModel(configuration);
    }
}
