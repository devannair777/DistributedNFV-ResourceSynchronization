package Tests;

import Formatters.Config;
import Formatters.ConfigLoader;
import Formatters.ResourceLoader;
import Orchestrator.Messages.NetworkTopology;
import Orchestrator.Messages.OrchestratorResource;
import Orchestrator.Messages.SynchronizedOrchestratorResource;
import Orchestrator.Resources.NSHello;
import Orchestrator.Resources.NSSynchronize;
import Orchestrator.SynchronizationInterface;
import Orchestrator.Validators.NeighborValidators;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, SocketException,  InterruptedException {
        ConfigLoader.setFileName("conf.yaml");
        ResourceLoader.setFileName("resource.yaml");

        Scanner sc = new Scanner(System.in);
        int i = 0;
        List<SynchronizationInterface> synchArray = new ArrayList<>();
        List<NSHello> helloRsrcArray = new ArrayList<>();
        List<NSSynchronize> synchRsrcArray = new ArrayList<>();

        Config cf = ConfigLoader.getConfigfromYaml();
        String ipAddress = cf.getInterfaces().get(0);
        String synchMGroup = "239.0.10.15";

        String hostId = "Host" + ipAddress.split("\\.")[2]+
                ipAddress.split("\\.")[3];

        ///Initializing interfaces and resource information lists;
        for(String intf : cf.getInterfaces())
        {
            try
            {
                System.out.println("Starting Interface : " + intf + " :: MCast group : " + synchMGroup + " ...");
                SynchronizationInterface s = new SynchronizationInterface(intf, synchMGroup);
                NSHello nsHello = new NSHello(intf);
                NSSynchronize nsynch = new NSSynchronize(intf);
                s.setNsHello(nsHello);
                s.setNsSynchronize(nsynch);
                System.out.println("Successfully Started Interface : " + intf + " :: MCast group : " + synchMGroup);
                synchArray.add(s);

                helloRsrcArray.add(nsHello);
                synchRsrcArray.add(nsynch);
            }
            catch (Exception e)
            {
                //Potentialy log errors
            }
        }

        //Initialize a Dummy Resource list //
        Date init = new Date();
        Timestamp tsinit = new Timestamp(init.getTime());
        SynchronizedOrchestratorResource initSynchOrch = new SynchronizedOrchestratorResource();
        OrchestratorResource or1 = ResourceLoader.getResourceFromYaml();
        initSynchOrch.setHostId(hostId);
        initSynchOrch.setResource(or1);
        initSynchOrch.setTimestamp(tsinit);
        NSSynchronize.getMaximalResourceList().add(initSynchOrch);
        ///

        NeighborValidators nv = new NeighborValidators();
        //while loop and schedule the whole process below
        int count = 0;
        ArrayList<String> localInterfaces ;
        while(count < 200)
        {
        localInterfaces = new ArrayList<>();
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        //Synchronized orchestrator object of the scheduler //
        OrchestratorResource orc = ResourceLoader. getResourceFromYaml();
        SynchronizedOrchestratorResource synOrch = new SynchronizedOrchestratorResource();
        synOrch.setTimestamp(ts);
        synOrch.setResource(orc);
        synOrch.setHostId(hostId);
        ///
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
            Thread.sleep(2000);
            System.out.println("Proceeding with synch messages after sleep");
            for(SynchronizationInterface si : synchArray)
            {

                try
                {
                    si.synchronize(hostId,synOrch);
                }
                catch (Exception e)
                {
                    System.out.println("The link is down !!" + si.getIpAddress());
                    //Log when an interface goes down
                }
            }

        Thread.sleep(5000);

        persistTransientLists();
        persistResourceList();

        NSHello.getRtNeighbourhood().getNeighbors().clear();

       // System.out.println("After sleep,Cleared transient list");
        //NSHello.getNeighborhood().getNeighbors().clear(); Doesnt make the
        // MaximalNeighborTopology.yaml maximal
        //entry list
        count ++;
        }
        // while loop end
    }

    private static void persistResourceList()
    {
        Yaml yaml = new Yaml();
        FileWriter fw = null;
        BufferedWriter bw = null;

        try
        {
            fw = new FileWriter("Resources.yaml",false);
            bw = new BufferedWriter(fw);
            yaml.dump(NSSynchronize.getMaximalResourceList(),bw);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                fw.close();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static void persistTransientLists()
    {
        Yaml yaml = new Yaml();
        FileWriter fwRt = null;
        BufferedWriter bwRt = null;
        try
        {
            fwRt = new FileWriter("Neighbors.yaml",false);
            bwRt = new BufferedWriter(fwRt);
            yaml.dump(NSHello.getRtNeighbourhood(),bwRt);

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                fwRt.close();
                bwRt.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
