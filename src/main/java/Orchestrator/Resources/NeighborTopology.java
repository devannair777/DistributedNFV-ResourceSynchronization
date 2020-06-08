package Orchestrator.Resources;

import Orchestrator.Messages.NetworkTopology;

public class NeighborTopology
{
    private String hostId;
    private NetworkTopology remoteTopology;

    public NeighborTopology(){}

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public NetworkTopology getRemoteTopology() {
        return remoteTopology;
    }

    public void setRemoteTopology(NetworkTopology remoteTopology) {
        this.remoteTopology = remoteTopology;
    }
}
