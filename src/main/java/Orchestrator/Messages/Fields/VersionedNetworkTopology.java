package Orchestrator.Messages.Fields;

public class VersionedNetworkTopology
{

    int version = 0;
    public VersionedNetworkTopology()
    {
    }


    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }


    @Override
    public String toString() {
        return "VersionedNetworkTopology{" +
                ", version=" + version +
                '}';
    }
}
