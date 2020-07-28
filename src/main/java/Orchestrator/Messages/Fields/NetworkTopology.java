package Orchestrator.Messages.Fields;



public class NetworkTopology
{
    private String CNFVOMCastGrp = "";
    public NetworkTopology()
    {
    }

    public String getCNFVOMCastGrp() {
        return CNFVOMCastGrp;
    }

    public void setCNFVOMCastGrp(String CNFVOMCastGrp) {
        this.CNFVOMCastGrp = CNFVOMCastGrp;
    }

    @Override
    public String toString() {
        return "NetworkTopology{" +
                "CNFVOMCastGrp='" + CNFVOMCastGrp + '\'' +
                '}';
    }
}
