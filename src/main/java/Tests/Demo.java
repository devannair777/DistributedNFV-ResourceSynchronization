package Tests;


import Formatters.ResourceLoader;
import Orchestrator.Messages.OrchestratorResource;
import Orchestrator.UdpMulticastEndpoint;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Demo {

    public static void test1(String[] args) throws IOException {
        System.out.println("Distributed NFV Orchestration");
        if(args.length > 0 && args[0].equals("server")) {
            System.out.println("Starting MCast Server");
            InetSocketAddress mGroup = new InetSocketAddress("239.0.10.15",0);

            InetSocketAddress socket = new InetSocketAddress(5600);
            InetSocketAddress socket2 = new InetSocketAddress(args[1],5600);
            InetSocketAddress socket3 = new InetSocketAddress(args[2],5600);

            MulticastSocket mCastSocket = new MulticastSocket(null);
            mCastSocket.setLoopbackMode(false);
            mCastSocket.bind(socket);
            NetworkInterface ni1 = NetworkInterface.getByInetAddress(socket2.getAddress());
            NetworkInterface ni2 = NetworkInterface.getByInetAddress(socket3.getAddress());

            mCastSocket.joinGroup(mGroup,ni1);
            mCastSocket.joinGroup(mGroup,ni2);
            System.out.println("Successfully started server and joined group");
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf,buf.length);
            mCastSocket.receive(packet);
            InetAddress addr = packet.getAddress();
            System.out.println("Packet Received from address : "+addr.toString());
        }
        else if(args.length > 0 && args[0].equalsIgnoreCase("client"))
        {
            System.out.println("Starting Client application");
            MulticastSocket clientEp = new MulticastSocket(5700);
            InetSocketAddress s1 = new InetSocketAddress(args[1],5700);
          //  InetSocketAddress sd = new InetSocketAddress(5700);

            NetworkInterface ni1 = NetworkInterface.getByInetAddress(s1.getAddress());
            InetAddress mGroup = InetAddress.getByName("239.0.10.15");

            clientEp.setInterface(s1.getAddress());
            clientEp.setTimeToLive(3);
            byte[] buf = new byte[256];
            buf = args[2].getBytes();
            DatagramPacket req = new DatagramPacket(buf,buf.length,mGroup,5600);

            clientEp.send(req);
        }
    }

    public static void test2(String[] args) throws IOException {
        System.out.println("Distributed NFV Orchestration");
        if(args.length > 0 && args[0].equals("server"))
        {
            UdpMulticastEndpoint ep1 = new UdpMulticastEndpoint("192.168.0.2","239.0.10.15");
            UdpMulticastEndpoint ep2 = new UdpMulticastEndpoint("192.168.1.1","239.0.10.15");
            CoapEndpoint coapEp1 = new CoapEndpoint.Builder().setConnector(ep1).build();
            CoapEndpoint coapEp2 = new CoapEndpoint.Builder().setConnector(ep2).build();
            //ep1.start();
            CoapServer coapServer = new CoapServer();
            coapServer.addEndpoint(coapEp1);
            coapServer.addEndpoint(coapEp2);
            coapServer.start();
            System.out.println("Successfully started ep1");
            //ep2.start();
            System.out.println("Successfully started ep2");
            while(true);
        }
        else if(args.length > 0 && args[0].equalsIgnoreCase("client"))
        {
            System.out.println("Starting Client application");
            DatagramSocket clientEp = new DatagramSocket(5700);
            InetAddress mGroup = InetAddress.getByName("239.0.10.15");
            byte[] buf = new byte[256];
            buf = args[1].getBytes();
            DatagramPacket req = new DatagramPacket(buf,buf.length,mGroup,5600);
            clientEp.send(req);
        }

    }

    public static void test3()
    {
        Date then = new Date();
        Timestamp ts = new Timestamp(then.getTime());

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter when you wish to compare");
        int k = sc.nextInt();
        Date now = new Date();
        Timestamp ts2 = new Timestamp(now.getTime());
        int diff = (int) (ts2.getTime() - ts.getTime())/1000;
        System.out.println("Comparison of two times : "
        + diff + " sec");

    }

    public static void test4()
    {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(7);
        list.add(5);
        list.add(6);

        System.out.println("List before modification : "+list);

        list.set(2,4);
        System.out.println("List after modification : "+list);
    }

    public static void test5() throws SocketException {
        NetworkInterface ni = NetworkInterface.getByInetAddress(new InetSocketAddress("192.168.0.2",2000).getAddress());
        System.out.println("Interface status : "+ni.isUp());
    }

    public static void test6() throws IOException {
        ArrayList<String> nl = new ArrayList<>();
        ArrayList<String> sl = new ArrayList<>();


        nl.add("Net1");nl.add("Net2");
        sl.add("Sl1");sl.add("Sl2");
        OrchestratorResource or = new OrchestratorResource();
        or.setNetworkResources(nl);
        or.setServiceResources(sl);

        Yaml yaml = new Yaml();
        BufferedWriter bw = new BufferedWriter(new FileWriter("test.yaml"));
        yaml.dump(or,bw);

    }

    public static void test7() throws FileNotFoundException {

        ResourceLoader.setFileName("test2.yaml");
        OrchestratorResource r = ResourceLoader.getResourceFromYaml();
        System.out.println(r);

        ResourceLoader.setFileName("test.yaml");
        OrchestratorResource r2 = ResourceLoader.getResourceFromYaml();
        System.out.println(r2);
    }

    public static void main(String[] args) throws IOException {
        test1(args);
        //test2(args);
        //test4();
        //test5();
        //test6();
        //test7();
    }
}
