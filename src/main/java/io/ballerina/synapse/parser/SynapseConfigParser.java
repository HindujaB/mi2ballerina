package io.ballerina.synapse.parser;

import org.apache.synapse.config.SynapseConfiguration;
import org.apache.synapse.config.SynapsePropertiesLoader;
import org.apache.synapse.config.xml.MultiXMLConfigurationBuilder;

import java.util.Properties;

// Parse a synapse.xml file into a SynapseConfiguration object
public class SynapseConfigParser {

    public static SynapseConfiguration parseSynapseConfig(String xmlFilePath) {
        Properties properties = SynapsePropertiesLoader.loadSynapseProperties();
        // Load the XML file as Configuration object
//        SynapseConfiguration configuration = SynapseConfigurationBuilder.getConfiguration(xmlFilePath, properties);
        return MultiXMLConfigurationBuilder.getConfiguration(xmlFilePath, properties);
    }
}