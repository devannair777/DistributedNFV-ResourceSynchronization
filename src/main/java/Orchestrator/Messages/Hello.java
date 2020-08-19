package Orchestrator.Messages;

import Orchestrator.Messages.Fields.VersionedNeighborhood;

import java.util.HashMap;

public class Hello
{
    private String hostId = "";
    private HashMap<String, VersionedNeighborhood> globalTopologyState;

    public Hello()
    {
        this.globalTopologyState = new HashMap<>();
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public HashMap<String, VersionedNeighborhood> getGlobalTopologyState() {
        return globalTopologyState;
    }

    public void setGlobalTopologyState(HashMap<String, VersionedNeighborhood> globalTopologyState) {
        this.globalTopologyState = globalTopologyState;
    }

    @Override
    public String toString() {
        return "Hello{" +
                "hostId='" + hostId + '\'' +
                ", globalTopologyLedger=" + globalTopologyState +
                '}';
    }
}
