package Tests;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.elements.Connector;
import org.eclipse.californium.elements.UdpMulticastConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ServerInterface {
    private CoapServer coapServer;
    private static int interfaceIdGen = 0;
    private String interfaceID = "ep_";
    private final Logger LOGGER = LoggerFactory.getLogger(ServerInterface.class.getCanonicalName());

    private String ipAddress="";
    private final int portNumber = 5683;
    private boolean isRunning = false;
    private static List<String> clientsAttached;

    public ServerInterface(String ipAddress, String multicastAddress) throws UnknownHostException {
        clientsAttached = new ArrayList<>();
        this.coapServer = new CoapServer();
        this.ipAddress = ipAddress;
        interfaceIdGen += 1;
        this.interfaceID += interfaceIdGen;

        NetworkConfig config = NetworkConfig.getStandard();
        CoapEndpoint coapEndpoint = createEndpoints(config,ipAddress,multicastAddress);

        this.coapServer.addEndpoint(coapEndpoint);
    }
    public CoapServer getCoapServer()
    {
        return this.coapServer;
    }

    private static CoapEndpoint createEndpoints(NetworkConfig config,String ipAddress,String mcastAddr) throws UnknownHostException {
        int port = config.getInt(NetworkConfig.Keys.COAP_PORT);
        InetSocketAddress localAddress = new InetSocketAddress(port);
        InetAddress mAddr = (new InetSocketAddress(mcastAddr, 0)).getAddress();
        Connector connector = new UdpMulticastConnector(localAddress, mAddr);
        return new CoapEndpoint.Builder().setNetworkConfig(config).setConnector(connector).build();
    }

    public static List<String> getClientsAttached() {
        return clientsAttached;
    }

    public boolean addResource(CoapResource resource)
    {
        this.coapServer.add(resource);
        return true;
    }

    public boolean startCoapServer()
    {
        try
        {
            this.coapServer.start();
            this.isRunning = true;
            LOGGER.info("Server Successfully Started at Endpoint :"+this.ipAddress);
            LOGGER.info(this.toString());
        }
        catch(IllegalStateException ie)
        {
            LOGGER.error(ie.getMessage());
        }
        return true;
    }

    @Override
    public String toString() {
        return  "\n********* Interface ************"+
                "\nInterface ID : " + interfaceID  +
                "\nIP Address : " + ipAddress  +
                "\nportNumber : " + portNumber +
                "\nRunning (?) : " + isRunning +
                "\n*******************************";
    }

    public boolean stopCoapServer()
    {
        this.coapServer.stop();
        this.isRunning = false;
        this.coapServer.destroy();
        LOGGER.info("Server Stopped and Endpoint : "+this.ipAddress+" destroyed");
        return true;
    }

}

