package Orchestrator.Messages;

import Orchestrator.Messages.Fields.NFVResource;

import java.util.HashMap;

public class Synchronize
{
    private HashMap<String,NFVResource> synchronizedResourceMap;

    public Synchronize()
    {
        this.synchronizedResourceMap = new HashMap<>();
    }

    public HashMap<String, NFVResource> getSynchronizedResourceMap() {
        return synchronizedResourceMap;
    }

    public void setSynchronizedResourceMap(HashMap<String, NFVResource> synchronizedResourceMap) {
        this.synchronizedResourceMap = synchronizedResourceMap;
    }

    @Override
    public String toString() {
        return "Synchronize{" +
                "synchronizedResourceMap=" + synchronizedResourceMap +
                '}';
    }
}
