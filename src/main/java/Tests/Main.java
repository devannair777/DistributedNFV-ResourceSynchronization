package Tests;

import Formatters.Config;
import Formatters.ConfigLoader;
import Orchestrator.Messages.NetworkTopology;
import Orchestrator.Resources.NSHello;
import Orchestrator.SynchronizationInterface;
import Orchestrator.Validators.NeighborValidators;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, SocketException, JsonProcessingException, InterruptedException {
        ConfigLoader.setFileName("conf.yaml");
        Scanner sc = new Scanner(System.in);
        int i = 0;
        List<SynchronizationInterface> synchArray = new ArrayList<>();
        List<NSHello> helloRsrcArray = new ArrayList<>();

        Config cf = ConfigLoader.getConfigfromYaml();
        String ipAddress = cf.getInterfaces().get(0);
        String synchMGroup = "239.0.10.15";

        String hostId = "Host" + ipAddress.split("\\.")[2]+
                ipAddress.split("\\.")[3];

        for(String intf : cf.getInterfaces())
        {
            try
            {
                System.out.println("Starting Interface : " + intf + " :: MCast group : " + synchMGroup + " ...");
                SynchronizationInterface s = new SynchronizationInterface(intf, synchMGroup);
                NSHello nsHello = new NSHello(intf);
                s.setNsHello(nsHello);
                System.out.println("Successfully Started Interface : " + intf + " :: MCast group : " + synchMGroup);
                synchArray.add(s);
                helloRsrcArray.add(nsHello);
            }
            catch (Exception e)
            {
                //Potentialy log errors
            }
        }
        NeighborValidators nv = new NeighborValidators();
        //while loop and schedule the whole process below
        int count = 0;
        ArrayList<String> localInterfaces ;
        while(count < 20)
        {
        localInterfaces = new ArrayList<>();
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        NetworkTopology nt = new NetworkTopology();

        // Check if interfaces are running before assigning to local topology matrix

        for(String intf :  cf.getInterfaces())
        {
            NetworkInterface ni = NetworkInterface.getByInetAddress(
                    new InetSocketAddress(intf,5600).getAddress()
            );
            if(ni.isUp())
            {
                localInterfaces.add(intf);
            }

        }

        nt.setCNFVOMCastGrp(synchMGroup);
        nt.setActiveInterfaces(localInterfaces);
        nt.setTimestamp(ts);
        for(SynchronizationInterface s : synchArray)
        {

            s.getNsHelloMsg().setHostId(hostId);
            s.getNsHelloMsg().setLocalTopology(nt);
        }

        //System.out.println("Press 1 to send hello messages : ");
        //int j = sc.nextInt();
            for(SynchronizationInterface si : synchArray)
            {

                try
                {
                    si.process();
                }
                catch (Exception e)
                {
                    System.out.println("The link is down !!" + si.getIpAddress());
                    //Log when an interface goes down
                }
            }

        Thread.sleep(5000);
        System.out.println("After sleep");
        //NSHello.getNeighborhood().getNeighbors().clear(); Doesnt make the NeighborTopology.yaml maximal
        //entry list
        count ++;
        }
        // while loop end
    }
}
