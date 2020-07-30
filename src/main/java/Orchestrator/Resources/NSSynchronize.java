package Orchestrator.Resources;

import Formatters.JsonFormatter;
import Orchestrator.Messages.Fields.NFVResource;
import Orchestrator.Messages.Synchronize;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.HashMap;

public class NSSynchronize extends CoapResource {

    String ipAddress;

    //Maximal Resource List
    private static HashMap<String, NFVResource> maximalResourceList = new HashMap<>();

    //Flooding Queue
    private static HashMap<String, NFVResource> resourceCache = new HashMap<>();


    public NSSynchronize(String ipAddress) {
        super("synchronize");
        this.ipAddress = ipAddress;
        //this.selfhostId = hostId;


    }

    public NSSynchronize()
    {
        super("synchronize");


    }

    public static HashMap<String, NFVResource> getMaximalResourceList() {
        return maximalResourceList;
    }

    public static void setMaximalResourceList(HashMap<String, NFVResource> maximalResourceList) {
        NSSynchronize.maximalResourceList = maximalResourceList;
    }

    public static HashMap<String, NFVResource> getResourceCache() {
        return resourceCache;
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        LOGGER.info("Inside NS Synchronize GET handler");
        String recvCtx = exchange.getSourceAddress().getHostAddress();
        if( ! recvCtx.equalsIgnoreCase(this.ipAddress)) {
            try {
                //Only reply to GET requests coming from IP Address not the same as the current ones
                // Needed because application uses multicast requests
                Synchronize sendSynMessage = new Synchronize();
                sendSynMessage.setSynchronizedResourceMap(NSSynchronize.getMaximalResourceList());

                String sendMsg = JsonFormatter.getjsonRepresentation(sendSynMessage);
                exchange.respond(CoAP.ResponseCode.CONTENT, sendMsg, MediaTypeRegistry.APPLICATION_JSON);

            }
            catch(JsonProcessingException jpe)
            {
                LOGGER.error(jpe.getMessage());
            }
        }
        else
        {
            //Only process requests that are not sent by the present interface as
            // the current interface also has a multicast server listening
            //pass
        }
    }

    @Override
    public void handlePOST(CoapExchange exchange) {

        LOGGER.info("Inside NS Synchronize POST handler");
        String recvCtx = exchange.getSourceAddress().getHostAddress();

        if( ! recvCtx.equalsIgnoreCase(this.ipAddress))
        {

            try
            {
                Synchronize rcvSynch = (Synchronize) JsonFormatter.getObjectRepresentation
                        (exchange.getRequestText(),Synchronize.class);
                LOGGER.info("Received Message : ",rcvSynch);
                for(String hostName : rcvSynch.getSynchronizedResourceMap().keySet())
                {
                    //If received message is already present in transient resource cache
                    //Dont flood it, otherwise move it to the flooding queue

                    if(resourceCache.containsKey(hostName))
                    {
                        //pass
                    }
                    else
                    {
                        if(maximalResourceList.containsKey(hostName))
                        {
                            //If Received Entry is  present in maximal resource queue then ...

                            if (maximalResourceList.get(hostName).getNFVResources().getServiceResources().
                                    equals(rcvSynch.getSynchronizedResourceMap().get(hostName).getNFVResources().getServiceResources())
                                    && maximalResourceList.get(hostName).getNFVResources().getNetworkResources().
                                    equals(rcvSynch.getSynchronizedResourceMap().get(hostName).getNFVResources().getNetworkResources()))
                            {
                                //If an entry that is already present in maximal resource list is the one that is received
                                //dont add it to the flooding queue or maximal queue
                            }
                            else {
                                //If an entry that is not already present in maximal resource list is the one that is received
                                //add it to the flooding queue and maximal queue
                                resourceCache.put(hostName, rcvSynch.getSynchronizedResourceMap().get(hostName));
                                maximalResourceList.put(hostName,rcvSynch.getSynchronizedResourceMap().get(hostName));

                            }

                        }
                        else
                        {
                            //If Received Entry is not present in maximal resource queue then add it to
                            //both flooding queue and maximal resource list
                            resourceCache.put(hostName, rcvSynch.getSynchronizedResourceMap().get(hostName));
                            maximalResourceList.put(hostName,rcvSynch.getSynchronizedResourceMap().get(hostName));
                        }
                    }
                }

            }
            catch (Exception e)
            {
                LOGGER.error(e.getMessage());
            }


            /*exchange.respond(CoAP.ResponseCode.CONTENT,
                    "", MediaTypeRegistry.APPLICATION_JSON);*/
        }
        else
        {
            //Only process requests that are not sent by the present interface as
            // the current interface also has a multicast server listening
            //pass
        }

        /*LOGGER.info("Maximal Resource List : " , maximalResourceList);
        LOGGER.info("Flooding Cache List : " , resourceCache);*/
    }


}
