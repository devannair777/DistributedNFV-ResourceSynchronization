package Orchestrator.Messages;

import Orchestrator.Messages.Fields.NetworkTopology;
import Orchestrator.Messages.Fields.Neighborhood;

import java.util.HashMap;

public class Hello
{
    private String hostId = "";
    private HashMap<String, Neighborhood> globalTopologyLedger;
    private HashMap<String,Integer> globalVersionLedger;
    private NetworkTopology cnfvoInfo ;

    public Hello()
    {
        this.globalTopologyLedger = new HashMap<>();
        this.globalVersionLedger = new HashMap<>();
        this.cnfvoInfo = new NetworkTopology();
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public HashMap<String, Neighborhood> getGlobalTopologyLedger() {
        return globalTopologyLedger;
    }

    public void setGlobalTopologyLedger(HashMap<String, Neighborhood> globalTopologyLedger) {
        this.globalTopologyLedger = globalTopologyLedger;
    }

    public HashMap<String, Integer> getGlobalVersionLedger() {
        return globalVersionLedger;
    }

    public void setGlobalVersionLedger(HashMap<String, Integer> globalVersionLedger) {
        this.globalVersionLedger = globalVersionLedger;
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
                ", globalVersionLedger=" + globalVersionLedger +
                ", cnfvoInfo=" + cnfvoInfo +
                '}';
    }
}
