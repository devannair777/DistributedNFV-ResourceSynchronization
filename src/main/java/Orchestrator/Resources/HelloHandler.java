package Orchestrator.Resources;

import Orchestrator.SynchronizationInterface;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloHandler implements CoapHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloHandler.class
                                            .getCanonicalName()) ;
    private SynchronizationInterface synchInterface;
    public HelloHandler(SynchronizationInterface si)
    {
        this.synchInterface = si;
    }

    @Override
    public void onLoad(CoapResponse coapResponse) {
        String dstIPAddr = coapResponse.advanced().getSourceContext().toString()
                            .split("\\:")[0]
                            .split("\\(")[1];

            LOGGER.info("Inside Hello Response Handler for SynchInterface : " + synchInterface.getIpAddress());
            LOGGER.info("Received Response from Interface : "+dstIPAddr);

        /*else
        {
            //Process responses received only from remote interfaces as local interfaces
            //also respond to multicast requests
            //pass
        }*/

    }

    @Override
    public void onError() {

    }
}
