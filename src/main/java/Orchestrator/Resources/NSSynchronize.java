package Orchestrator.Resources;

import Formatters.JsonFormatter;
import Orchestrator.Messages.SynchronizedOrchestratorResource;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.io.IOException;
import java.util.ArrayList;

public class NSSynchronize extends CoapResource {

    String ipAddress;
    String selfhostId;
    private static ArrayList<SynchronizedOrchestratorResource> maximalResourceList
            = new ArrayList<>();


    public NSSynchronize(String ipAddress) {
        super("synchronize");
        this.ipAddress = ipAddress;
        //this.selfhostId = hostId;


    }

    public NSSynchronize()
    {
        super("synchronize");


    }

    public static ArrayList<SynchronizedOrchestratorResource> getMaximalResourceList() {
        return maximalResourceList;
    }

    @Override
    public void handlePOST(CoapExchange exchange) {

        LOGGER.info("Inside NS SYnchronize POST handler");
        String recvCtx = exchange.getSourceAddress().getHostAddress();

        if( ! recvCtx.equalsIgnoreCase(this.ipAddress))
        {

            try
            {
                SynchronizedOrchestratorResource synchOrch =
                        (SynchronizedOrchestratorResource)
                                JsonFormatter.getObjectRepresentation(exchange.getRequestText(),
                                        SynchronizedOrchestratorResource.class);
               int index = 0;
               boolean found = false;
                for(SynchronizedOrchestratorResource so : maximalResourceList)
                {
                    if(so.getHostId().equalsIgnoreCase(synchOrch.getHostId()))
                    {
                        found = true;
                        break;
                    }
                    else {
                        index++;
                    }

                }
                if(found)
                {
                    ///If element is found then replace only if it is of a later timestamp
                    ///otherwise discard the update
                    if(maximalResourceList.get(index).getTimestamp().after(
                            synchOrch.getTimestamp()
                    ))
                    {
                        //Do nothing
                    }
                    else
                        {
                            //The received packet is the latest representation of the
                            //resource information
                        maximalResourceList.set(index, synchOrch);
                    }
                }
                else
                {
                    maximalResourceList.add(synchOrch);
                }

            }
            catch (Exception e){}
            try
            {
                SynchronizedOrchestratorResource[] synchOrcharray =
                        (SynchronizedOrchestratorResource [])
                JsonFormatter.getObjectRepresentation(exchange.getRequestText(),
                        SynchronizedOrchestratorResource[].class);

                for(SynchronizedOrchestratorResource sr : synchOrcharray)
                {
                    int index = 0;
                    boolean found = false;
                    for(SynchronizedOrchestratorResource so : maximalResourceList)
                    {
                        if(so.getHostId().equalsIgnoreCase(sr.getHostId()))
                        {
                            found = true;
                            break;
                        }
                        else {
                            index++;
                        }

                    }
                    if(found)
                    {
                        ///If element is found then replace only if it is of a later timestamp
                        ///otherwise discard the update
                        if(maximalResourceList.get(index).getTimestamp().after(
                                sr.getTimestamp()
                        ))
                        {
                            //Do nothing
                        }
                        else
                        {
                            //The received packet is the latest representation of the
                            //resource information
                            maximalResourceList.set(index, sr);
                        }
                    }
                    else
                    {
                        maximalResourceList.add(sr);
                    }
                }

            }
            catch (Exception e){}

            exchange.respond(CoAP.ResponseCode.CONTENT,
                    "", MediaTypeRegistry.APPLICATION_JSON);
        }
        else
        {
            //Only process requests that are not sent by the present interface as
            // the current interface also has a multicast server listening
            //pass
        }
    }


}
