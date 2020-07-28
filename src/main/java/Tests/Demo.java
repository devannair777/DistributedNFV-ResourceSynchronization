package Tests;

import Formatters.JsonFormatter;
import Formatters.ResourceLoader;
import Orchestrator.Messages.Fields.NFVResource;
import Orchestrator.Messages.Fields.OrchestratorResource;
import Orchestrator.Messages.Synchronize;

import java.io.IOException;
import java.util.ArrayList;

public class Demo
{
    public static void main(String[] args) throws IOException {

        test2();
    }

    public static void test1()
    {
        ArrayList<String> r1 = new ArrayList<>();
        ArrayList<String> r2 = new ArrayList<>();
        ArrayList<String> r3 = new ArrayList<>();

        r1.add("Resource1");
        r1.add("Resource2");

        r2.add("Resource1");
        r2.add("Resource2");

        /*r3.add("Resource2");
        r3.add("Resource1");*/

        System.out.println("List R1 :" +r1);
        System.out.println("List R2 :" +r2);
        System.out.println("List R3 :" +r3);

        System.out.println("Is R1 == R2 ? "+r1.equals(r2));
        System.out.println("Is R1 == R3 ? "+r1.equals(r3));
        System.out.println("Is R3 == R2 ? "+r3.equals(r2));

    }

    public static void test2() throws IOException {
        ResourceLoader.setFileName("test.yaml");
        String hostId = "host1";
        OrchestratorResource o = ResourceLoader.getResourceFromYaml();
        NFVResource sy = new NFVResource();

        Synchronize s = new Synchronize();


        sy.setVersion(5);
        sy.setNFVResources(o);

        s.getSynchronizedResourceMap().put(hostId,sy);

        String synchMsg = JsonFormatter.getjsonRepresentation(s);
        System.out.println(synchMsg);

        Synchronize s2 = new Synchronize();
        s2 = (Synchronize) JsonFormatter.getObjectRepresentation(synchMsg, Synchronize.class);
        System.out.println(s2);

    }
}
