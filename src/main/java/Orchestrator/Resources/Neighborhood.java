package Orchestrator.Resources;

import java.util.ArrayList;

public class Neighborhood
{
    private ArrayList<NeighborTopology> neighbors;
    public Neighborhood(){
        this.neighbors = new ArrayList<>();
    }

    public ArrayList<NeighborTopology> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(ArrayList<NeighborTopology> neighbors) {
        this.neighbors = neighbors;
    }
}
