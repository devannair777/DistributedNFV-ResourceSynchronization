package Orchestrator;

import Formatters.JsonFormatter;
import Orchestrator.Messages.Hello;
import Orchestrator.Messages.Fields.NFVResource;
import Orchestrator.Messages.Synchronize;
import Orchestrator.Resources.HelloHandler;
import Orchestrator.Resources.NSHello;
import Orchestrator.Resources.NSSynchronize;
import Orchestrator.Resources.SynchronizationHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;

public class SynchronizationInterface
{
    private static final Logger LOGGER = LoggerFactory.getLogger(
                                SynchronizationInterface.class.getCanonicalName());
    private String ipAddress = "";
    private String mGroup = "";
    private NFVResource selfResource ;
    private static final int PORTNUM = 5700;
    private static final int SERVERPORT = 5600;
    private static String scheme = "coap://";
    private static String mCastURIBase =  "";
    private NSHello nsHello;
    private NSSynchronize nsSynchronize;
    private HelloHandler helloHandler;
    private SynchronizationHandler synchHandler;
    private Hello nsHelloMsg;



    private CoapEndpoint clientEp ;
    private CoapServer coapServer;
    private CoapClient coapClient;
    private CoapClient synchClient;
    private CoapEndpoint serverEp;


    public SynchronizationInterface(String ipAddress,String mGroup) throws SocketException {
        this.ipAddress = ipAddress;
        this.mGroup = mGroup;
        mCastURIBase = scheme + mGroup + ":"+SERVERPORT + "/";
        this.nsHelloMsg = new Hello();
        this.clientEp = new CoapEndpoint.Builder().setInetSocketAddress(
                new InetSocketAddress(ipAddress,PORTNUM)).build();
        UdpMulticastEndpoint ume = new UdpMulticastEndpoint(ipAddress,mGroup);
        this.serverEp = new CoapEndpoint.Builder().setConnector(ume).build();
        this.coapClient = new CoapClient();
        this.synchClient = new CoapClient();

        this.coapServer = new CoapServer();
        this.coapClient.setEndpoint(this.clientEp);
        this.synchClient.setEndpoint(this.clientEp);

        this.coapServer.addEndpoint(this.serverEp);
        this.helloHandler = new HelloHandler(this);
        this.synchHandler = new SynchronizationHandler(this.ipAddress);

        //this.nsHello = nsHello;
        //this.nsSynchronize = nsynch;
        this.coapServer.start();
        //this.coapServer.add(this.nsHello);
        //this.coapServer.add(this.nsSynchronize);

        LOGGER.info("Server started at Endpoint : " +  ume.toString());
        this.coapClient.useNONs();
        this.synchClient.useNONs();

    }

    public NFVResource getSelfResource() {
        return selfResource;
    }

    public void setSelfResource(NFVResource selfResource) {
        this.selfResource = selfResource;
    }

    public void setNsHello(NSHello nsHello) {
        this.nsHello = nsHello;
        this.coapServer.add(this.nsHello);
    }

    public void setNsSynchronize(NSSynchronize nsSynchronize) {
        this.nsSynchronize = nsSynchronize;
        this.coapServer.add(this.nsSynchronize);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Hello getNsHelloMsg() {
        return nsHelloMsg;
    }

    public void send_hello(String hostId,int hello_version,int resource_version) throws JsonProcessingException {

            LOGGER.info("Inside send_hello Method ...");
            this.coapClient.setURI(mCastURIBase + "hello");
            LOGGER.info("Coap Client Target uri : " + this.coapClient.getURI());
            NSHello.getMaximalVersionLedger().put(hostId, resource_version);

            NSHello.getGlobalTopology().put(hostId,NSHello.getRtNeighbourhood());
            NSHello.getGlobalTopology().get(hostId).setVersion(hello_version);
            NSHello.getGlobalTopology().get(hostId).setResVersion(resource_version);

            this.nsHelloMsg.setGlobalTopologyLedger(NSHello.getGlobalTopology());
            String helloMsg = JsonFormatter.getjsonRepresentation(this.nsHelloMsg);
            this.coapClient.post(helloHandler, helloMsg, MediaTypeRegistry.APPLICATION_JSON);
            LOGGER.info("Successfully sent Hello Message");


        /**/
    }

    public void request_Globalresource_view()
    {
        LOGGER.info("Inside request_Globalresource_view Method ...");
        this.synchClient.setURI(mCastURIBase + "synchronize");
        this.synchClient.get(this.synchHandler);

    }

    public void send_synchronize(HashMap<String, NFVResource> resourceCache) throws JsonProcessingException
    {
        LOGGER.info("Inside send_synchronize Method ...");
        this.synchClient.setURI(mCastURIBase + "synchronize");
        Synchronize s = new Synchronize();
        s.setSynchronizedResourceMap(resourceCache);
        String synchMsg = JsonFormatter.getjsonRepresentation(s);
        this.synchClient.post(synchHandler,synchMsg,MediaTypeRegistry.APPLICATION_JSON);
        LOGGER.info("Successfully sent Synchronize Message");
    }
}
