package Orchestrator.Resources;

import Formatters.JsonFormatter;
import Orchestrator.Messages.Hello;
import Orchestrator.Messages.NetworkTopology;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.io.IOException;

public class NSHello extends CoapResource {

    private static Neighborhood neighborhood = new Neighborhood(); //Maximal neighborhood entry list
    private static Neighborhood rtNeighbourhood = new Neighborhood();
    private Hello helloMsg;
    private String ipAddress;

    public static Neighborhood getRtNeighbourhood() {
        return rtNeighbourhood;
    }

    public static Neighborhood getNeighborhood() {
        return neighborhood;
    }

    public NSHello() {
        super("hello");
    }

    public NSHello(String ipAddr)
    {
        this();

        this.ipAddress = ipAddr;

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
                NeighborTopology n = new NeighborTopology();
                n.setHostId(rcvdMsg.getHostId());
                n.setRemoteTopology(rcvdMsg.getLocalTopology());
                //Find if the given host Id already exists in the maximal list
                int index = 0;
                boolean found = false;
                for(NeighborTopology nt : neighborhood.getNeighbors())
                {
                    if(nt.getHostId().equalsIgnoreCase(n.getHostId()))
                    {
                        found = true;
                        break;
                    }
                     index ++;
                }
                if(found)
                {
                    //If yes then update with new timestamp
                    neighborhood.getNeighbors().set(index,n);
                }
                else
                {
                    //If no create a new entry with the POST information
                    neighborhood.getNeighbors().add(n);
                }
                //If no Hello request received about the same keep maximal neighborhood
                // list as it is

                //Do the same for rtNeighborhoods as well//
                int index2 = 0;
                boolean found2 = false;
                for(NeighborTopology nt : rtNeighbourhood.getNeighbors())
                {
                    if(nt.getHostId().equalsIgnoreCase(n.getHostId()))
                    {
                        found2 = true;
                        break;
                    }
                    index2 ++;
                }
                if(found2)
                {
                    //If yes then update with new timestamp
                    rtNeighbourhood.getNeighbors().set(index2,n);
                }
                else
                {
                    //If no create a new entry with the POST information
                    rtNeighbourhood.getNeighbors().add(n);
                }

                LOGGER.info("Successfully updated Maximal and Real time NeighborTopology Lists");
                exchange.respond(CoAP.ResponseCode.CONTENT,
                        "", MediaTypeRegistry.APPLICATION_JSON);

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
