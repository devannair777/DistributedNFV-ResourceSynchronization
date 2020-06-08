package Orchestrator.Messages;

public class Hello
{
    private String hostId = "";
    private NetworkTopology localTopology;
    public Hello()
    {
        this.localTopology = new NetworkTopology();
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public NetworkTopology getLocalTopology() {
        return localTopology;
    }

    public void setLocalTopology(NetworkTopology localTopology) {
        this.localTopology = localTopology;
    }

    @Override
    public String toString() {
        return "Hello{" +
                "hostId='" + hostId + '\'' +
                ", localTopology=" + localTopology +
                '}';
    }
}
