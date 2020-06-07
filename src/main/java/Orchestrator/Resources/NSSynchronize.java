package Orchestrator.Resources;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class NSSynchronize extends CoapResource {
    public NSSynchronize(String name) {
        super(name);
    }

    public NSSynchronize()
    {
        this("synchronize");
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        super.handlePOST(exchange);
    }


}
