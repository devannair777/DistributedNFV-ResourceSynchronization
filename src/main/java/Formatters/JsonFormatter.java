package Formatters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonFormatter
{
    private static ObjectMapper jsonParser = new ObjectMapper();


    public static String getjsonRepresentation(Object obj) throws JsonProcessingException {
        return jsonParser.writeValueAsString(obj);
    }

    public static Object getObjectRepresentation(String jsonString,Class cls) throws IOException {
        return jsonParser.readValue(jsonString,cls);
    }
}