package Tests;


import Orchestrator.UdpMulticastEndpoint;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;

import java.io.IOException;
import java.net.*;

public class Demo {

    public static void test1(String[] args) throws IOException {
        System.out.println("Distributed NFV Orchestration");
        if(args.length > 0 && args[0].equals("server")) {
            System.out.println("Starting MCast Server");
            InetSocketAddress mGroup = new InetSocketAddress("239.0.10.15",0);

            InetSocketAddress socket = new InetSocketAddress(5600);
            InetSocketAddress socket2 = new InetSocketAddress("192.168.0.2",5600);
            InetSocketAddress socket3 = new InetSocketAddress("192.168.1.1",5600);

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
            DatagramSocket clientEp = new DatagramSocket(5700);
            InetAddress mGroup = InetAddress.getByName("239.0.10.15");
            byte[] buf = new byte[256];
            buf = args[1].getBytes();
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

    public static void main(String[] args) throws IOException {
        test2(args);

    }
}
