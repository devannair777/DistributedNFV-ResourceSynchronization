package Orchestrator.Messages;

import Formatters.Version;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class NetworkTopology
{
    private String CNFVOMCastGrp = "";
    private List<String> activeInterfaces  ;
    //private Timestamp timestamp;
    private Version version;
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

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
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
                ", activeInterfaces=" + activeInterfaces +
                ", Version=" + version +
                '}';
    }
}
