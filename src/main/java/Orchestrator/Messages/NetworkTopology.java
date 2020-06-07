package Orchestrator.Messages;

import java.util.ArrayList;
import java.util.List;

public class NetworkTopology
{
    private String CNFVOMCastGrp = "";
    private List<String> activeInterfaces ;
    public NetworkTopology()
    {
        this.activeInterfaces = new ArrayList<>();
    }

    public String getCNFVOMCastGrp() {
        return CNFVOMCastGrp;
    }

    public void setCNFVOMCastGrp(String CNFVOMCastGrp) {
        this.CNFVOMCastGrp = CNFVOMCastGrp;
    }

    public List<String> getActiveInterfaces() {
        return activeInterfaces;
    }

    public void setActiveInterfaces(List<String> activeInterfaces) {
        this.activeInterfaces = activeInterfaces;
    }
}
