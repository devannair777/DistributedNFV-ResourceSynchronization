package Tests;

import Formatters.Config;
import Formatters.ConfigLoader;
import Orchestrator.Messages.NetworkTopology;
import Orchestrator.Resources.NSHello;
import Orchestrator.Resources.NSSynchronize;
import Orchestrator.SynchronizationInterface;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.FileNotFoundException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, SocketException, JsonProcessingException {
        ConfigLoader.setFileName("conf.yaml");
        List<SynchronizationInterface> synchArray = new ArrayList<>();
        Config cf = ConfigLoader.getConfigfromYaml();
        String ipAddress = cf.getInterfaces().get(0);
        String synchMGroup = "239.0.10.15";

        String hostId = "Host" + ipAddress.split("\\.")[2]+
                ipAddress.split("\\.")[3];
        NetworkTopology nt = new NetworkTopology();
        nt.setCNFVOMCastGrp(synchMGroup);
        nt.setActiveInterfaces(cf.getInterfaces());

        Scanner sc = new Scanner(System.in);
        int i = 0;

        for(String intf : cf.getInterfaces())
        {
            NSHello nsHello = new NSHello();
            System.out.println("Starting Interface : " + intf+" :: MCast group : "+ synchMGroup + " ...");
            SynchronizationInterface s = new SynchronizationInterface(intf,synchMGroup);
            s.setNsHello(nsHello);
            s.getNsHelloMsg().setHostId(hostId);
            s.getNsHelloMsg().setLocalTopology(nt);
            System.out.println("Successfully Started Interface : " + intf+" :: MCast group : "+ synchMGroup);
            synchArray.add(s);
        }
        System.out.println("Press 1 to send hello messages : ");
        int k = sc.nextInt();
        if(k == 1)
        {
            for(SynchronizationInterface si : synchArray)
            {
                si.process();
            }
        }
    }
}
