package Orchestrator.Resources;

import Formatters.JsonFormatter;
import Orchestrator.Messages.Fields.VersionedNeighborhood;
import Orchestrator.Messages.Fields.VersionedNetworkTopology;
import Orchestrator.Messages.Hello;
import Orchestrator.Messages.Fields.NetworkTopology;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.io.IOException;
import java.util.HashMap;

public class NSHello extends CoapResource {

    private static VersionedNeighborhood maximalNeighgbors = new VersionedNeighborhood(); //Maximal neighborhood entry list
    private static VersionedNeighborhood rtNeighbors = new VersionedNeighborhood(); // Transient Versioned Neighborhood List

    private static HashMap<String,Integer> maximalVersionLedger = new HashMap<>(); //Maximal Version Ledger of all NFVO's
    private static HashMap<String, VersionedNeighborhood> globalTopology = new HashMap<>(); //Transient Global Topology view

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

    public static HashMap<String, VersionedNeighborhood> getGlobalTopology() {
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

                //Contexts of the received message

                localNeighbor = rcvdMsg.getCnfvoInfo();
                String remoteHost = rcvdMsg.getHostId();
                HashMap<String,VersionedNeighborhood> received_topology = rcvdMsg.getGlobalTopologyLedger();
                HashMap<String,Integer> rcvd_version_ledger = rcvdMsg.getGlobalResourceVersionLedger();
                int received_node_version = received_topology.get(remoteHost).getVersion();

                // Maximal Neighbor Information representation section

                VersionedNetworkTopology received_node_vnt = new VersionedNetworkTopology();
                received_node_vnt.setCNFVOMCastGrp(localNeighbor.getCNFVOMCastGrp());
                received_node_vnt.setVersion(received_node_version);

                maximalNeighgbors.getNeighbor().put(remoteHost,received_node_vnt);
                rtNeighbors.getNeighbor().put(remoteHost,received_node_vnt);

                //Global Topology ledger section

                for(String s : received_topology.keySet())
                {
                    if(s.equalsIgnoreCase(this.selfHostId))
                    {

                    }
                    else
                    {
                        if (globalTopology.containsKey(s))
                        {
                            if(globalTopology.get(s).getVersion() < received_topology.get(s).getVersion())
                            {
                                globalTopology.put(s,received_topology.get(s));
                            }
                            else
                            {
                                //pass
                            }

                        }
                        else
                        {
                           globalTopology.put(s,received_topology.get(s));
                        }
                    }
                }

                //

                // Maintaining the maximal Version Ledger

                for(String s: rcvd_version_ledger.keySet())
                {
                    if(maximalVersionLedger.containsKey(s))
                    {
                        //if node's maximal version ledger has an entry corresponding to received
                        // messages's version ledger check versions and replace older entries
                        // of maximal version ledger

                        if(maximalVersionLedger.get(s) < rcvd_version_ledger.get(s))
                        {
                            //replace entry in maximal ledger
                            maximalVersionLedger.put(s,rcvd_version_ledger.get(s));
                            if(NSSynchronize.getMaximalResourceList().get(s) != null )
                            {   //Send GET request to all neighbors for resource information if the global resource view
                                // for any of the node, in the present node is outdated
                                if(NSSynchronize.getMaximalResourceList().get(s).getVersion() <
                                        rcvd_version_ledger.get(s) && !(s.equalsIgnoreCase(this.selfHostId)))
                                {
                                    sendGetRequest = true;
                                }
                                else
                                {
                                    //pass
                                }
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
