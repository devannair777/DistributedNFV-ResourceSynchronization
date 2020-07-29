package Orchestrator.Messages;

import Orchestrator.Messages.Fields.NetworkTopology;
import Orchestrator.Messages.Fields.VersionedNeighborhood;

import java.util.HashMap;

public class Hello
{
    private String hostId = "";
    private HashMap<String, VersionedNeighborhood> globalTopologyLedger;
    private HashMap<String,Integer> globalResourceVersionLedger;
    private NetworkTopology cnfvoInfo ;

    public Hello()
    {
        this.globalTopologyLedger = new HashMap<>();
        this.globalResourceVersionLedger = new HashMap<>();
        this.cnfvoInfo = new NetworkTopology();
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public HashMap<String, Integer> getGlobalResourceVersionLedger() {
        return globalResourceVersionLedger;
    }

    public void setGlobalResourceVersionLedger(HashMap<String, Integer> globalResourceVersionLedger) {
        this.globalResourceVersionLedger = globalResourceVersionLedger;
    }

    public HashMap<String, VersionedNeighborhood> getGlobalTopologyLedger() {
        return globalTopologyLedger;
    }

    public void setGlobalTopologyLedger(HashMap<String, VersionedNeighborhood> globalTopologyLedger) {
        this.globalTopologyLedger = globalTopologyLedger;
    }

    public NetworkTopology getCnfvoInfo() {
        return cnfvoInfo;
    }

    public void setCnfvoInfo(NetworkTopology cnfvoInfo) {
        this.cnfvoInfo = cnfvoInfo;
    }

    @Override
    public String toString() {
        return "Hello{" +
                "hostId='" + hostId + '\'' +
                ", globalTopologyLedger=" + globalTopologyLedger +
                ", globalVersionLedger=" + globalResourceVersionLedger +
                ", cnfvoInfo=" + cnfvoInfo +
                '}';
    }
}
