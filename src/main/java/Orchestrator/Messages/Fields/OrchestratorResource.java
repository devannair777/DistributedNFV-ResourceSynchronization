package Orchestrator.Messages.Fields;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrchestratorResource
{
    private List<String> NetworkResources;
    private List<String> ServiceResources;

    public OrchestratorResource()
    {
        NetworkResources = new ArrayList<>();
        ServiceResources = new ArrayList<>();

    }

    public OrchestratorResource(Map<String,List<String>> mp)
    {
        this.NetworkResources = mp.get("NetworkResources");
        this.ServiceResources = mp.get("ServiceResources");
    }

    public List<String> getNetworkResources() {
        return NetworkResources;
    }

    public void setNetworkResources(List<String> networkResources) {
        NetworkResources = networkResources;
    }

    public List<String> getServiceResources() {
        return ServiceResources;
    }

    public void setServiceResources(List<String> serviceResources) {
        ServiceResources = serviceResources;
    }

    @Override
    public String toString() {
        return "OrchestratorResource{" +
                "NetworkResources=" + NetworkResources +
                ", ServiceResources=" + ServiceResources +
                '}';
    }
}
