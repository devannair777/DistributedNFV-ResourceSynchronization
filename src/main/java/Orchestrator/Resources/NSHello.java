package Orchestrator.Resources;

import Formatters.JsonFormatter;
import Orchestrator.Messages.Hello;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.io.IOException;

public class NSHello extends CoapResource {

    private Hello helloMsg;

    public NSHello(String name) {
        super(name);
    }

    public NSHello()
    {
        this("hello");
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        LOGGER.info("Inside POST Handler");
        try
        {
            Hello rcvdMsg = (Hello) JsonFormatter
                    .getObjectRepresentation(exchange.getRequestText(),Hello.class);
            LOGGER.info(rcvdMsg.toString());
            exchange.respond(CoAP.ResponseCode.CONTENT,
                    "", MediaTypeRegistry.APPLICATION_JSON);
        }
        catch (IOException ioe)
        {
            LOGGER.error(ioe.getMessage());
        }

    }
}
