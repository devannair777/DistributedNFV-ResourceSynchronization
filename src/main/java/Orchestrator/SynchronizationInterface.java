package Orchestrator;

import Formatters.JsonFormatter;
import Orchestrator.Messages.Hello;
import Orchestrator.Messages.SynchronizedOrchestratorResource;
import Orchestrator.Resources.HelloHandler;
import Orchestrator.Resources.NSHello;
import Orchestrator.Resources.NSSynchronize;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketException;

public class SynchronizationInterface
{
    private static final Logger LOGGER = LoggerFactory.getLogger(
                                SynchronizationInterface.class.getCanonicalName());
    private String ipAddress = "";
    private String mGroup = "";
    private SynchronizedOrchestratorResource selfResource ;
    private static final int PORTNUM = 5700;
    private static final int SERVERPORT = 5600;
    private static String scheme = "coap://";
    private static String mCastURIBase =  "";
    private NSHello nsHello;
    private NSSynchronize nsSynchronize;
    private HelloHandler helloHandler;
    private Hello nsHelloMsg;



    private CoapEndpoint clientEp ;
    private CoapServer coapServer;
    private CoapClient coapClient;
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
        this.coapServer = new CoapServer();
        this.coapClient.setEndpoint(this.clientEp);
        this.coapServer.addEndpoint(this.serverEp);
        this.helloHandler = new HelloHandler(this);

        //this.nsHello = nsHello;
        //this.nsSynchronize = nsynch;
        this.coapServer.start();
        //this.coapServer.add(this.nsHello);
        //this.coapServer.add(this.nsSynchronize);

        LOGGER.info("Server started at Endpoint : " +  ume.toString());
        this.coapClient.useNONs();

    }

    public SynchronizedOrchestratorResource getSelfResource() {
        return selfResource;
    }

    public void setSelfResource(SynchronizedOrchestratorResource selfResource) {
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

    public void process() throws JsonProcessingException {
        LOGGER.info("Inside Process Method ...");
        this.coapClient.setURI(mCastURIBase + "hello");
        LOGGER.info("Coap Client Target uri : "+this.coapClient.getURI());
        String postMsg = JsonFormatter.getjsonRepresentation(this.nsHelloMsg);
        this.coapClient.post(helloHandler,postMsg, MediaTypeRegistry.APPLICATION_JSON);
        LOGGER.info("Successfully sent Hello Message");
        /**/
    }
}
