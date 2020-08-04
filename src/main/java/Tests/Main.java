package Tests;

import Formatters.Config;
import Formatters.ConfigLoader;
import Formatters.ResourceLoader;
import Formatters.Version;
import Orchestrator.Messages.Fields.*;
import Orchestrator.Resources.NSHello;
import Orchestrator.Resources.NSSynchronize;
import Orchestrator.Scheduler.Scheduler;
import Orchestrator.SynchronizationInterface;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.util.*;

public class Main {


    public static void main(String[] args) throws FileNotFoundException, SocketException,  InterruptedException {
        ConfigLoader.setFileName("conf.yaml");
        ResourceLoader.setFileName("resource.yaml");

        NFVResource rsCache = new NFVResource();

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
                NSHello nsHello = new NSHello(intf,hostId);
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
        Version v_init = new Version();
        Scheduler scheduler = new Scheduler(hostId);

        //while loop and schedule the whole process below
        int count = 0;

        int resource_version ;
        /*ArrayList<String> localInterfaces ;*/

        while(true)
        {

        //Synchronized orchestrator object of the scheduler //
        OrchestratorResource orc = ResourceLoader. getResourceFromYaml();
        NFVResource synOrch = new NFVResource();
        v_init.inc_version();

        synOrch.setNFVResources(orc);
        ///

            if(rsCache.getNFVResources() != null) {
                if (synOrch.getNFVResources().getNetworkResources().equals(rsCache.getNFVResources().getNetworkResources())
                        && synOrch.getNFVResources().getServiceResources().equals(rsCache.getNFVResources().getServiceResources()))
                {
                    // If Resource information has not changed
                    // then dont add it to the flooding queue
                    resource_version = NSSynchronize.getMaximalResourceList().get(hostId)
                            .getVersion();
                    synOrch.setVersion(resource_version);
                }
                else
                    {
                        //If resource information of present cnfvo node has changed then
                        // add it to the flooding queue

                    resource_version = v_init.getVersion();
                    synOrch.setVersion(resource_version);
                    NSSynchronize.getResourceCache().put(hostId, synOrch);
                    NSSynchronize.setIsUpdated(true);

                }
            }
            else
            {
                resource_version = v_init.getVersion();
                synOrch.setVersion(resource_version);
                NSSynchronize.getResourceCache().put(hostId, synOrch);
                NSSynchronize.setIsUpdated(true);
            }

        NSSynchronize.getMaximalResourceList().put(hostId,synOrch);

        for(SynchronizationInterface s : synchArray)
        {
            s.getNsHelloMsg().setHostId(hostId);
        }

            for(SynchronizationInterface si : synchArray)
            {

                try
                {
                    si.send_hello(hostId,v_init.getVersion(),resource_version);
                }
                catch (Exception e)
                {
                    System.out.println("Hello Message error:"+e.getMessage());
                    //Log when an interface goes down
                }
            }
            if(count%3 == 0)
            {
                NSHello.getRtNeighbourhood().getNeighbor().clear();
            }
            if(NSHello.isSendGetRequest())
            {
                for(SynchronizationInterface si : synchArray)
                {

                    try
                    {
                        si.request_Globalresource_view();
                    }
                    catch (Exception e)
                    {
                        System.out.println("The link is down !!" + si.getIpAddress());
                        //Log when an interface goes down
                    }
                }
                NSHello.setSendGetRequest(false);
                Thread.sleep(2000);
            }
            Thread.sleep(5000);

            if(! NSSynchronize.getResourceCache().isEmpty())
            {
                for(SynchronizationInterface si : synchArray)
                {

                    try
                    {
                        si.send_synchronize(NSSynchronize.getResourceCache());
                    }
                    catch (Exception e)
                    {
                        System.out.println("The link is down !!" + si.getIpAddress());
                        //Log when an interface goes down
                    }
                }

                NSSynchronize.getResourceCache().clear();
            }

        Thread.sleep(5000);
        rsCache = synOrch;

        if(NSSynchronize.isIsUpdated())
        {
            persistResourceList();
            NSSynchronize.setIsUpdated(false);
        }

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

}
