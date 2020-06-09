package Orchestrator.Resources;

import Formatters.JsonFormatter;
import Orchestrator.Messages.Hello;
import Orchestrator.Messages.NetworkTopology;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class NSHello extends CoapResource {

    private static Neighborhood neighborhood = new Neighborhood(); //Maximal neighborhood entry list
    private Hello helloMsg;
    private String ipAddress;

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
            BufferedWriter bufferedWriter = null;
            FileWriter fw = null;
            LOGGER.info("Inside POST Handler.Reveived Request from : " + recvCtx);
            try {
                Hello rcvdMsg = (Hello) JsonFormatter
                        .getObjectRepresentation(exchange.getRequestText(), Hello.class);
                //LOGGER.info(rcvdMsg.toString());
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
                // neighborhood.getNeighbors().add(n);
                Yaml yaml = new Yaml();
                fw = new FileWriter("NeighborTopology.yaml",false);
                bufferedWriter = new BufferedWriter(fw);
                yaml.dump(neighborhood,bufferedWriter);
                LOGGER.info("Successfully updated NeighborTopology");
                exchange.respond(CoAP.ResponseCode.CONTENT,
                        "", MediaTypeRegistry.APPLICATION_JSON);

            } catch (IOException ioe) {
                LOGGER.error(ioe.getMessage());
            }
            finally {
                try {
                    bufferedWriter.close();
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            //Only process requests that are not sent by the present interface as
            // the current interface also has a multicast server listening
            //pass
        }

    }
}
