package Orchestrator.Resources;

import Formatters.JsonFormatter;
import Orchestrator.Messages.Fields.Neighborhood;
import Orchestrator.Messages.Fields.VersionedNeighborhood;
import Orchestrator.Messages.Fields.VersionedNetworkTopology;
import Orchestrator.Messages.Hello;
import Orchestrator.Messages.Fields.NetworkTopology;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.io.IOException;
import java.util.HashMap;

public class NSHello extends CoapResource {

    private static VersionedNeighborhood maximalNeighgbors = new VersionedNeighborhood(); //Maximal neighborhood entry list
    private static VersionedNeighborhood rtNeighbors = new VersionedNeighborhood(); // Transient Versioned Neighborhood List

    private static HashMap<String,Integer> maximalVersionLedger = new HashMap<>(); //Maximal Version Ledger of all NFVO's
    private static HashMap<String, Neighborhood> globalTopology = new HashMap<>(); //Transient Global Topology view

    private static boolean sendGetRequest = false;

    private String ipAddress;
    private String selfHostId;

    public static VersionedNeighborhood getRtNeighbourhood() {
        return rtNeighbors;
    }

    public static VersionedNeighborhood getNeighborhood() {
        return maximalNeighgbors;
    }

    public static HashMap<String, Integer> getMaximalVersionLedger() {
        return maximalVersionLedger;
    }

    public static void setMaximalVersionLedger(HashMap<String, Integer> maximalVersionLedger) {
        NSHello.maximalVersionLedger = maximalVersionLedger;
    }

    public static HashMap<String, Neighborhood> getGlobalTopology() {
        return globalTopology;
    }

    public static boolean isSendGetRequest() {
        return sendGetRequest;
    }

    public static void setSendGetRequest(boolean sendGetRequest) {
        NSHello.sendGetRequest = sendGetRequest;
    }

    public NSHello() {
        super("hello");
    }

    public NSHello(String ipAddr,String selfHostId)
    {
        this();
        this.ipAddress = ipAddr;
        this.selfHostId = selfHostId;

    }

    @Override
    public void handlePOST(CoapExchange exchange) {

        String recvCtx = exchange.getSourceAddress().getHostAddress();
        if( ! recvCtx.equalsIgnoreCase(this.ipAddress))
        {

            //LOGGER.info("Inside POST Handler.Reveived Request from : " + recvCtx);
            try {
                Hello rcvdMsg = (Hello) JsonFormatter
                        .getObjectRepresentation(exchange.getRequestText(), Hello.class);
                NetworkTopology localNeighbor = new NetworkTopology();

                localNeighbor = rcvdMsg.getCnfvoInfo();
                String remoteHost = rcvdMsg.getHostId();
                HashMap<String,Neighborhood> received_topology = rcvdMsg.getGlobalTopologyLedger();


                // Maintaining the maximal Version Ledger

                HashMap<String,Integer> rcvd_version_ledger = rcvdMsg.getGlobalVersionLedger();

                VersionedNetworkTopology vnt = new VersionedNetworkTopology();
                vnt.setCNFVOMCastGrp(localNeighbor.getCNFVOMCastGrp());
                vnt.setVersion(rcvd_version_ledger.get(remoteHost));

                for(String s: rcvd_version_ledger.keySet())
                {
                    if(maximalVersionLedger.containsKey(s))
                    {
                        //if node's maximal version ledger has an entry corresponding to received
                        // messages's version ledger check versions and replace older entries
                        // of maximal version ledger

                        if(maximalVersionLedger.get(s) < rcvd_version_ledger.get(s))
                        {
                           /* if(rcvd_version_ledger.get(s) - maximalVersionLedger.get(s)  > 100 )
                            {
                                sendGetRequest = true;
                            }*/
                            //replace entry in maximal ledger
                            maximalVersionLedger.put(s,rcvd_version_ledger.get(s));
                            if(NSSynchronize.getMaximalResourceList().get(s) != null )
                            {
                                //NSSynchronize.getMaximalResourceList().get(s).setVersion(rcvd_version_ledger.get(s));
                            }
                        }

                        else
                        {
                            //pass
                        }



                    }
                    else
                    {
                        //if node's maximal version ledger does not have an entry corresponding to received
                        // messages's version ledger then add it to the maximal ledger
                        // and send a get request to all neighbors for resource information , as
                        // the present node could be a newly injected node in the network

                        maximalVersionLedger.put(s,rcvd_version_ledger.get(s));

                        if(NSSynchronize.getMaximalResourceList().get(s) != null)
                        {
                            //NSSynchronize.getMaximalResourceList().get(s).setVersion(rcvd_version_ledger.get(s));
                        }
                        else
                        {
                            sendGetRequest = true;
                        }
                    }

                }

                ///

                //Find if the given host Id already exists in the maximal list
                //If yes then update with new timestamp

                maximalNeighgbors.getNeighbor().put(remoteHost,vnt);
                rtNeighbors.getNeighbor().put(remoteHost,vnt);

                Neighborhood nh = new Neighborhood();
                NetworkTopology nt = new NetworkTopology();

                for(String neighborHost : rtNeighbors.getNeighbor().keySet())
                {
                    nt.setCNFVOMCastGrp(rtNeighbors.getNeighbor().get(neighborHost).getCNFVOMCastGrp());
                    nh.getNeighbor().put(neighborHost,nt);

                }
                globalTopology.put(this.selfHostId,nh);
                for(String h: received_topology.keySet())
                {
                    if(h.equalsIgnoreCase(this.selfHostId))
                    {
                        //pass
                    }
                    else
                    {
                        globalTopology.put(h, received_topology.get(h));
                    }
                }



                //If no Hello request received about the same keep maximal neighborhood
                // list as it is

                //Do the same for rtNeighborhoods as well//
                //If yes then update with new timestamp

                //If no create a new entry with the POST information

                LOGGER.info("Successfully updated Maximal and Real time NeighborTopology Lists");
                /*exchange.respond(CoAP.ResponseCode.CONTENT,
                        "", MediaTypeRegistry.APPLICATION_JSON);*/

            } catch (IOException ioe) {
                LOGGER.error(ioe.getMessage());
            }
            finally { }
        }
        else
        {
            //Only process requests that are not sent by the present interface as
            // the current interface also has a multicast server listening
            //pass
        }

    }
}
