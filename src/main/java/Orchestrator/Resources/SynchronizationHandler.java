package Orchestrator.Resources;

import Formatters.JsonFormatter;
import Orchestrator.Messages.Fields.NFVResource;
import Orchestrator.Messages.Synchronize;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

public class SynchronizationHandler implements CoapHandler {
    private  static final Logger LOGGER = LoggerFactory.getLogger(SynchronizationHandler
                                            .class.getCanonicalName());
    private String ipAddress;

    public SynchronizationHandler(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    @Override
    public void onLoad(CoapResponse coapResponse) {
        LOGGER.info("Inside NS_Synch Response Handler");
        try
        {
            Synchronize syn = (Synchronize) JsonFormatter.getObjectRepresentation(coapResponse.getResponseText(),
                    Synchronize.class);
            HashMap<String,NFVResource> rcvdMap = syn.getSynchronizedResourceMap();
            HashMap<String, NFVResource> temp = NSSynchronize.getMaximalResourceList();
            for(String h : syn.getSynchronizedResourceMap().keySet())
            {
                if(temp.containsKey(h))
                {
                    if(temp.get(h).getVersion() < rcvdMap.get(h).getVersion())
                    {
                        temp.put(h,rcvdMap.get(h));
                    }
                    else
                    {
                        //pass
                    }
                }
                else
                {
                    temp.put(h,rcvdMap.get(h));
                }
            }
            NSSynchronize.setMaximalResourceList(temp);
            NSSynchronize.setIsUpdated(true);

        }
        catch(IOException ioe)
        {
            LOGGER.error(ioe.getMessage());
        }

    }

    @Override
    public void onError() {
        LOGGER.error("Error inside Synch Handler");

    }
}
