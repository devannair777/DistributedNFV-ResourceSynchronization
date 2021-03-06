package Orchestrator;

import Formatters.JsonFormatter;
import Orchestrator.Messages.Hello;
import Orchestrator.Messages.OrchestratorResource;
import Orchestrator.Messages.SynchronizedOrchestratorResource;
import Orchestrator.Resources.HelloHandler;
import Orchestrator.Resources.NSHello;
import Orchestrator.Resources.NSSynchronize;
import Orchestrator.Resources.SynchronizationHandler;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

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
        this.synchHandler = new SynchronizationHandler();

        //this.nsHello = nsHello;
        //this.nsSynchronize = nsynch;
        this.coapServer.start();
        //this.coapServer.add(this.nsHello);
        //this.coapServer.add(this.nsSynchronize);

        LOGGER.info("Server started at Endpoint : " +  ume.toString());
        this.coapClient.useNONs();
        this.synchClient.useNONs();

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
        String helloMsg = JsonFormatter.getjsonRepresentation(this.nsHelloMsg);
        this.coapClient.post(helloHandler,helloMsg, MediaTypeRegistry.APPLICATION_JSON);
        LOGGER.info("Successfully sent Hello Message");

        /**/
    }

    public void synchronize(String hostId, SynchronizedOrchestratorResource orsc) throws JsonProcessingException {
        this.synchClient.setURI(mCastURIBase+"synchronize");
        ArrayList<SynchronizedOrchestratorResource> synchOrchList ;
        synchOrchList = NSSynchronize.getMaximalResourceList();
        int index = 0;
        for(SynchronizedOrchestratorResource synchOrch : synchOrchList)
        {
            // Only update the synchronized orchestrator data of the
            // present node in the maximal resource list keep
            // others intact

            if(synchOrch.getHostId().equalsIgnoreCase(hostId))
            {
                synchOrch = orsc;
                synchOrchList.set(index,synchOrch);
                break;
            }
            //If resource of present information is not present
            // then add keep others intact
            else
            { }
            index ++;
        }
        String synchMessage = JsonFormatter.getjsonRepresentation(synchOrchList);
        this.synchClient.post(synchHandler,synchMessage,MediaTypeRegistry
                                                                .APPLICATION_JSON );




    }
}
