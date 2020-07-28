package Orchestrator.Messages.Fields;

import Orchestrator.Messages.Fields.NetworkTopology;

import java.util.HashMap;

public class Neighborhood
{
    private HashMap<String, NetworkTopology> neighbor;
    public Neighborhood()
    {
        this.neighbor = new HashMap<>();
    }

    public HashMap<String, NetworkTopology> getNeighbor() {
        return neighbor;
    }

    public void setNeighbor(HashMap<String, NetworkTopology> neighbor) {
        this.neighbor = neighbor;
    }
}
