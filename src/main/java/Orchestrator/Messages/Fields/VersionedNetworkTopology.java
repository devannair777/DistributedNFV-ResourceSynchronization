package Orchestrator.Messages.Fields;

public class VersionedNetworkTopology
{
    private String CNFVOMCastGrp = "";
    int version = 0;
    public VersionedNetworkTopology()
    {
    }

    public String getCNFVOMCastGrp() {
        return CNFVOMCastGrp;
    }

    public void setCNFVOMCastGrp(String CNFVOMCastGrp) {
        this.CNFVOMCastGrp = CNFVOMCastGrp;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    /*public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }*/

    @Override
    public String toString() {
        return "NetworkTopology{" +
                "CNFVOMCastGrp='" + CNFVOMCastGrp + '\'' +
                '}';
    }
}
