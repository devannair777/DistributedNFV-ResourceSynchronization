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


    }

    @Override
    public void onError() {


    }
}
