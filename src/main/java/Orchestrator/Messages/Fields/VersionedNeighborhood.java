package Orchestrator.Messages.Fields;

import java.util.HashMap;

public class VersionedNeighborhood
{
    private HashMap<String, VersionedNetworkTopology> neighbor;
    private int version = 0;
    private int resVersion = 0;

    public VersionedNeighborhood()
    {
        this.neighbor = new HashMap<>();
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getResVersion() {
        return resVersion;
    }

    public void setResVersion(int resVersion) {
        this.resVersion = resVersion;
    }

    public HashMap<String, VersionedNetworkTopology> getNeighbor() {
        return neighbor;
    }

    public void setNeighbor(HashMap<String, VersionedNetworkTopology> neighbor) {
        this.neighbor = neighbor;
    }

    @Override
    public String toString() {
        return "VersionedNeighborhood{" +
                "neighbor=" + neighbor +
                ", version=" + version +
                ", resVersion=" + resVersion +
                '}';
    }
}
