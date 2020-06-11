package Orchestrator.Resources;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronizationHandler implements CoapHandler {
    private  static final Logger LOGGER = LoggerFactory.getLogger(SynchronizationHandler
                                            .class.getCanonicalName());

    @Override
    public void onLoad(CoapResponse coapResponse) {
        LOGGER.info("Inside Synch Handler");

    }

    @Override
    public void onError() {
        LOGGER.error("Error inside Synch Handler");

    }
}
