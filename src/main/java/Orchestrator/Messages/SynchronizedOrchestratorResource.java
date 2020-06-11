package Orchestrator.Messages;

import java.sql.Timestamp;

public class SynchronizedOrchestratorResource
{
    private OrchestratorResource resource;
    private Timestamp timestamp;
    private String hostId = "";

    public SynchronizedOrchestratorResource()
    {

    }

    public OrchestratorResource getResource() {
        return resource;
    }

    public void setResource(OrchestratorResource resource) {
        this.resource = resource;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    @Override
    public String toString() {
        return "SynchronizedOrchestratorResource{" +
                "resource=" + resource +
                ", timestamp=" + timestamp +
                ", hostId='" + hostId + '\'' +
                '}';
    }
}
