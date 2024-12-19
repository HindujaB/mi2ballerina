package io.ballerina.synapse.parser;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.api.API;
import org.apache.synapse.config.SynapseConfigUtils;
import org.apache.synapse.config.SynapseConfiguration;
import org.apache.synapse.config.SynapsePropertiesLoader;
import org.apache.synapse.config.xml.SynapseXMLConfigurationFactory;
import org.apache.synapse.config.xml.XMLConfigConstants;
import org.apache.synapse.config.xml.XMLConfigurationBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import javax.xml.stream.XMLStreamException;

import static org.apache.synapse.SynapseConstants.REGISTRY_FILE;
import static org.apache.synapse.config.xml.MultiXMLConfigurationBuilder.SEPARATE_REGISTRY_DEFINITION;
import static org.apache.synapse.config.xml.MultiXMLConfigurationBuilder.SEPARATE_TASK_MANAGER_DEFINITION;
import static org.apache.synapse.config.xml.MultiXMLConfigurationBuilder.TASK_MANAGER_FILE;

// Parse a synapse.xml file into a SynapseConfiguration object
public class SynapseConfigParser {

    public static SynapseConfiguration getConfiguration(String root) {
        Properties properties = SynapsePropertiesLoader.loadSynapseProperties();
        return getConfiguration(root, properties);
    }

    public static SynapseConfiguration getConfiguration(String root, Properties properties) {

        // First try to load the configuration from synapse.xml
        SynapseConfiguration synapseConfig = createConfigurationFromSynapseXML(root, properties);
        if (synapseConfig == null) {
            synapseConfig = SynapseConfigUtils.newConfiguration();
            synapseConfig.setDefaultQName(XMLConfigConstants.DEFINITIONS_ELT);
        }

        if (synapseConfig.getRegistry() == null) {
            // If the synapse.xml does not define a registry look for a registry.xml
            createRegistry(synapseConfig, root, properties);
        }

        if (synapseConfig.getTaskManager() == null) {
            // If the synapse.xml does not define a taskManager look for a task-manager.xml
            createTaskManager(synapseConfig, root, properties);
        }

        File rootDirectory = new File(root);
        if (!rootDirectory.exists() || !rootDirectory.isDirectory()) {
            throw new IllegalArgumentException("Invalid root path: " + root);
        }

        File[] subDirectories = rootDirectory.listFiles(File::isDirectory);
        if (subDirectories == null) {
            throw new IllegalStateException("No subdirectories found in the root path.");
        }

        for (File subDir : subDirectories) {
            String dirName = subDir.getName();
            switch (dirName.toLowerCase()) {
                case MISynapseConstants.SEQUENCES_DIR_NAME:
                    parseSequences(synapseConfig, subDir, properties);
                    break;
                case MISynapseConstants.TEMPLATES_DIR_NAME:
                    parseTemplates(synapseConfig, subDir, properties);
                    break;
                case MISynapseConstants.TASKS_DIR_NAME:
                    parseTasks(synapseConfig, subDir, properties);
                    break;
                case MISynapseConstants.PROXY_SERVICES_DIR_NAME:
                    parseProxyServices(synapseConfig, subDir, properties);
                    break;
                case MISynapseConstants.API_DIR_NAME:
                    parseAPIs(synapseConfig, subDir, properties);
                    break;
                case MISynapseConstants.ENDPOINTS_DIR_NAME:
                    parseEndpoints(synapseConfig, subDir, properties);
                    break;
                case MISynapseConstants.INBOUND_DIR_NAME:
                    parseInboundEndpoint(synapseConfig, subDir, properties);
                    break;
                case MISynapseConstants.LOCAL_ENTRIES_DIR_NAME:
                    parseLocalEntries(synapseConfig, subDir, properties);
                    break;
                case MISynapseConstants.MSG_PROCESSORS_DIR_NAME:
                    parseMessageProcessors(synapseConfig, subDir, properties);
                    break;
                case MISynapseConstants.MSG_STORES_DIR_NAME:
                    parseMessageStores(synapseConfig, subDir, properties);
                    break;
                case MISynapseConstants.DATA_SERVICES_DIR_NAME:
                    parseDataServices(synapseConfig, subDir, properties);
                    break;
                case MISynapseConstants.DATASOURCE_DIR_NAME:
                    parseDataSources(synapseConfig, subDir, properties);
                    break;
                default:
                    System.out.println("Unknown directory: " + dirName);
            }
        }
        return synapseConfig;
    }

    private static void parseDataSources(SynapseConfiguration synapseConfig, File subDir, Properties properties) {
        // TODO: Implement this
    }

    private static void parseDataServices(SynapseConfiguration synapseConfig, File subDir, Properties properties) {
        // TODO: Implement this
    }

    private static void parseMessageStores(SynapseConfiguration synapseConfig, File subDir, Properties properties) {
        // TODO: Implement this
    }

    private static void parseMessageProcessors(SynapseConfiguration synapseConfig, File subDir, Properties properties) {
        // TODO: Implement this
    }

    private static void parseLocalEntries(SynapseConfiguration synapseConfig, File subDir, Properties properties) {
        // TODO: Implement this
    }

    private static void parseInboundEndpoint(SynapseConfiguration synapseConfig, File subDir, Properties properties) {
        // TODO: Implement this
    }

    private static void parseProxyServices(SynapseConfiguration synapseConfig, File subDir, Properties properties) {
        // TODO: Implement this
    }

    private static void parseTasks(SynapseConfiguration synapseConfig, File subDir, Properties properties) {
        // TODO: Implement this
    }

    private static void parseTemplates(SynapseConfiguration synapseConfig, File subDir, Properties properties) {
        // TODO: Implement this
    }

    private static void parseSequences(SynapseConfiguration synapseConfig, File subDir, Properties properties) {
        // TODO: Implement this
    }

    private static void parseAPIs(SynapseConfiguration synapseConfig, File subDir, Properties properties) {
        Iterator<?> apiIterator = FileUtils.iterateFiles(subDir, new String[]{"xml"}, false);
        while (apiIterator.hasNext()) {
            File file = (File) apiIterator.next();
            try {
                OMElement document = getOMElement(file);
                API api = SynapseXMLConfigurationFactory.defineAPI(synapseConfig, document, properties, false);
                if (api != null) {
                    api.setFileName(file.getName());
                    synapseConfig.getArtifactDeploymentStore().addArtifact(file.getAbsolutePath(), api.getName());
                }
            } catch (Exception e) {
                String msg = "API configuration cannot be built from : " + file.getName();
                throw new RuntimeException(msg, e);
            }
        }
        // order the apis based on context descending order
        try {
            SynapseXMLConfigurationFactory.reOrderAPIs(synapseConfig);
        } catch (Exception e) {
            String msg = "Error while re-ordering apis";
            throw new RuntimeException(msg, e);
        }
    }

    private static void parseEndpoints(SynapseConfiguration synapseConfig, File subDir, Properties properties) {
        Iterator<?> endpointIterator = FileUtils.iterateFiles(subDir, new String[]{"xml"}, false);
        while (endpointIterator.hasNext()) {
            File file = (File) endpointIterator.next();
            try {
                OMElement document = getOMElement(file);
                SynapseXMLConfigurationFactory.defineEndpoint(synapseConfig, document, properties);
            } catch (Exception e) {
                String msg = "Endpoint configuration cannot be built from : " + file.getName();
                throw new RuntimeException(msg, e);
            }
        }

    }

    private static SynapseConfiguration createConfigurationFromSynapseXML(String rootDirPath, Properties properties) {

        File synapseXML = new File(rootDirPath, SynapseConstants.SYNAPSE_XML);
        if (!synapseXML.exists() || !synapseXML.isFile()) {
            return null;
        }

        FileInputStream is;
        SynapseConfiguration config = null;
        try {
            is = FileUtils.openInputStream(synapseXML);
        } catch (IOException e) {
            throw new RuntimeException("Error while opening the file: " + synapseXML.getName(), e);
        }

        try {
            config = XMLConfigurationBuilder.getConfiguration(is, properties);
            is.close();
        } catch (XMLStreamException e) {
            throw new RuntimeException("Error while loading the Synapse configuration from the " + synapseXML.getName() + " file", e);
        } catch (IOException e) {
            throw new RuntimeException("Error while closing the input stream from file: " + synapseXML.getName(), e);
        }

        return config;
    }

    private static void createRegistry(SynapseConfiguration synapseConfig, String rootDirPath, Properties properties) {

        File registryDef = new File(rootDirPath, REGISTRY_FILE);
        try {
            if (registryDef.exists() && registryDef.isFile()) {
                OMElement document = getOMElement(registryDef);
                SynapseXMLConfigurationFactory.defineRegistry(synapseConfig, document, properties);
                synapseConfig.setProperty(SEPARATE_REGISTRY_DEFINITION, String.valueOf(Boolean.TRUE));
            }
        } catch (Exception e) {
            String msg = "Registry configuration cannot be built from : " + registryDef.getName();
            throw new RuntimeException(msg, e);
        }
    }

    private static OMElement getOMElement(File file) {
        FileInputStream is;
        OMElement document;
        try {
            is = FileUtils.openInputStream(file);
        } catch (IOException e) {
            throw new RuntimeException("Error while opening the file: " + file.getName() + " for reading", e);
        }

        try {
            document = new StAXOMBuilder(is).getDocumentElement();
            document.build();
            is.close();
        } catch (XMLStreamException e) {
            throw new RuntimeException("Error while parsing the content of the file: " + file.getName(), e);
        } catch (IOException e) {
            throw new RuntimeException("Error while closing the input stream from the file: " + file.getName(), e);
        }
        return document;
    }

    private static void createTaskManager(SynapseConfiguration synapseConfig, String rootDirPath, Properties properties) {

        File taskManagerDef = new File(rootDirPath, TASK_MANAGER_FILE);
        try {
            if (taskManagerDef.exists() && taskManagerDef.isFile()) {
                OMElement document = getOMElement(taskManagerDef);
                SynapseXMLConfigurationFactory.defineTaskManager(synapseConfig, document, properties);
                synapseConfig.setProperty(SEPARATE_TASK_MANAGER_DEFINITION, String.valueOf(Boolean.TRUE));
            }
        } catch (Exception e) {
            String msg = "Task Manager configuration cannot be built from : " + taskManagerDef.getName();
            throw new RuntimeException(msg, e);
        }
    }

}
