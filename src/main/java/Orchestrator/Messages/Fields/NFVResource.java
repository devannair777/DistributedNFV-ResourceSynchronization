package Orchestrator.Messages.Fields;

public class NFVResource
{
    private OrchestratorResource NFVResources;
    private int version;

    public NFVResource()
    {

    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public OrchestratorResource getNFVResources() {
        return NFVResources;
    }

    public void setNFVResources(OrchestratorResource NFVResources) {
        this.NFVResources = NFVResources;
    }

    @Override
    public String toString() {
        return "SynchronizedOrchestratorResource{" +
                "NFVResources=" + NFVResources +
                ", version=" + version +
                '}';
    }
}
