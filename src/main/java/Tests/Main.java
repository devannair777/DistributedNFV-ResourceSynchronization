package Tests;

import Formatters.Config;
import Formatters.ConfigLoader;
import Formatters.ResourceLoader;
import Formatters.Version;
import Orchestrator.Messages.Fields.*;
import Orchestrator.Resources.NSHello;
import Orchestrator.Resources.NSSynchronize;
import Orchestrator.SynchronizationInterface;
import Orchestrator.Validators.ExpirationValidators;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

public class Main {


    public static void main(String[] args) throws FileNotFoundException, SocketException,  InterruptedException {
        ConfigLoader.setFileName("conf.yaml");
        ResourceLoader.setFileName("resource.yaml");

        NFVResource rsCache = new NFVResource();

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
        ExpirationValidators nv = new ExpirationValidators(hostId);

        //while loop and schedule the whole process below
        int count = 0;

        int resource_version ;
        ArrayList<String> localInterfaces ;
        while(count < 200)
        {
        localInterfaces = new ArrayList<>();
        //Synchronized orchestrator object of the scheduler //
        OrchestratorResource orc = ResourceLoader. getResourceFromYaml();
        NFVResource synOrch = new NFVResource();
        v_init.inc_version();

        synOrch.setNFVResources(orc);
        ///

            if(rsCache.getNFVResources() != null) {
                if (synOrch.getNFVResources().getNetworkResources().equals(rsCache.getNFVResources().getNetworkResources())
                        || synOrch.getNFVResources().getServiceResources().equals(rsCache.getNFVResources().getServiceResources()))
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

                }
            }
            else
            {
                resource_version = v_init.getVersion();
                synOrch.setVersion(resource_version);
                NSSynchronize.getResourceCache().put(hostId, synOrch);
            }

        NSSynchronize.getMaximalResourceList().put(hostId,synOrch);
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

        for(SynchronizationInterface s : synchArray)
        {
            s.getNsHelloMsg().setHostId(hostId);
            s.getNsHelloMsg().setCnfvoInfo(nt);
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
            VersionedNeighborhood rtNeighbors = NSHello.getRtNeighbourhood();
            NSHello.getRtNeighbourhood().getNeighbor().clear();
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
            System.out.println("Proceeding with synch messages after sleep");


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
        //maintain_global_topology(hostId);

        rsCache = synOrch;

        persistTransientLists(rtNeighbors);
        persistResourceList();

       // NSHello.getRtNeighbourhood().getNeighbor().clear();

       // System.out.println("After sleep,Cleared transient list");
        //NSHello.getNeighborhood().getNeighbors().clear(); Doesnt make the
        // MaximalNeighborTopology.yaml maximal
        //entry list
        count ++;
        }
        // while loop end
    }

    private static void maintain_global_topology(String hostId)
    {
        /*Neighborhood nh = new Neighborhood();
        NetworkTopology nt = new NetworkTopology();
        HashMap<String, VersionedNetworkTopology> rtNeighbors = NSHello.getRtNeighbourhood().getNeighbor();
        for(String neighborHost : rtNeighbors.keySet())
        {
            nt.setCNFVOMCastGrp(rtNeighbors.get(neighborHost).getCNFVOMCastGrp());
            nh.getNeighbor().put(neighborHost,nt);

        }
        NSHello.getGlobalTopology().put(hostId,nh);*/
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

    private static void persistTransientLists(VersionedNeighborhood vn)
    {
        Yaml yaml = new Yaml();
        FileWriter fwRt = null;
        BufferedWriter bwRt = null;
        try
        {
            fwRt = new FileWriter("Neighbors.yaml",false);
            bwRt = new BufferedWriter(fwRt);
            yaml.dump(vn,bwRt);

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
