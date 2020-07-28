package Orchestrator.Messages.Fields;

import java.util.HashMap;

public class VersionedNeighborhood
{
    private HashMap<String, VersionedNetworkTopology> neighbor;
    public VersionedNeighborhood()
    {
        this.neighbor = new HashMap<>();
    }

    public HashMap<String, VersionedNetworkTopology> getNeighbor() {
        return neighbor;
    }

    public void setNeighbor(HashMap<String, VersionedNetworkTopology> neighbor) {
        this.neighbor = neighbor;
    }

}
