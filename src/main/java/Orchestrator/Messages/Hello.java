package Orchestrator.Messages;

import Orchestrator.Messages.Fields.VersionedNeighborhood;

import java.util.HashMap;

public class Hello
{
    private String hostId = "";
    private HashMap<String, VersionedNeighborhood> globalTopologyLedger;

    public Hello()
    {
        this.globalTopologyLedger = new HashMap<>();
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public HashMap<String, VersionedNeighborhood> getGlobalTopologyLedger() {
        return globalTopologyLedger;
    }

    public void setGlobalTopologyLedger(HashMap<String, VersionedNeighborhood> globalTopologyLedger) {
        this.globalTopologyLedger = globalTopologyLedger;
    }

    @Override
    public String toString() {
        return "Hello{" +
                "hostId='" + hostId + '\'' +
                ", globalTopologyLedger=" + globalTopologyLedger +
                '}';
    }
}
