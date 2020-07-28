package Formatters;

import Orchestrator.Messages.Fields.OrchestratorResource;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
        OrchestratorResource resrc = null;
        FileReader fr = null;
        try {
            Map<String, List<String>> mp = yaml.load(new FileReader(fileName));
            resrc = new OrchestratorResource(mp);
        }
        catch (Exception e)
        {
            Yaml yaml = new Yaml();
            fr = new FileReader(fileName);

            resrc = yaml.load(fr);
            try {
                fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return resrc;
    }

}