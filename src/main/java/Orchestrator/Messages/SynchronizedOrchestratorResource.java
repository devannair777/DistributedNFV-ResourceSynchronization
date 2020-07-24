package Orchestrator.Messages;

import Formatters.Version;

import java.sql.Timestamp;

public class SynchronizedOrchestratorResource
{
    private OrchestratorResource resource;
    private Version version;
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

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
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
                ", version=" + version +
                ", hostId='" + hostId + '\'' +
                '}';
    }
}
