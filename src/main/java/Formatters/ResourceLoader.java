package Formatters;

import Orchestrator.Messages.OrchestratorResource;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

public class ResourceLoader
{
    private static Yaml yaml = new Yaml();
    private static String fileName = "";
    private static String filePath = "";

    public static Yaml getYaml() {
        return yaml;
    }

    public static void setYaml(Yaml yaml) {
        ResourceLoader.yaml = yaml;
    }

    public static String getFileName() {
        return fileName;
    }

    public static void setFileName(String fileName) {
        ResourceLoader.fileName = fileName;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String filePath) {
        ResourceLoader.filePath = filePath;
    }

    public ResourceLoader(){}

    public  static OrchestratorResource getResourceFromYaml() throws FileNotFoundException {
        Map<String, List<String>> mp = yaml.load(new FileReader(fileName));
        OrchestratorResource resrc = new OrchestratorResource(mp);
        return resrc;
    }

}