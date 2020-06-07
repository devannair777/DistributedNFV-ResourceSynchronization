package Formatters;

import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

public class ConfigLoader
{
    private static Yaml yaml = new Yaml();
    private static String fileName = "";
    private static String filePath = "";

    public static Yaml getYaml() {
        return yaml;
    }

    public static void setYaml(Yaml yaml) {
        ConfigLoader.yaml = yaml;
    }

    public static String getFileName() {
        return fileName;
    }

    public static void setFileName(String fileName) {
        ConfigLoader.fileName = fileName;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String filePath) {
        ConfigLoader.filePath = filePath;
    }

    public ConfigLoader(){}

    public  static Config getConfigfromYaml() throws FileNotFoundException {
        Map<String, List<String>> mp = yaml.load(new FileReader(fileName));
        Config cf = new Config(mp);
        return cf;
    }
}
