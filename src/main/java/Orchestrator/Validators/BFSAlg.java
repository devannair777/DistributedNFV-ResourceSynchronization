package Orchestrator.Validators;

import Orchestrator.Messages.Fields.VersionedNeighborhood;
import Orchestrator.Resources.NSHello;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BFSAlg
{
    private static Logger LOGGER = LoggerFactory.getLogger(BFSAlg.class.getCanonicalName());
    private static String selfHostName = "";
    private static HashMap<String,ArrayList<String>> result = new HashMap<>();

    public BFSAlg(String hostName)
    {
        selfHostName = hostName;

    }

    public static void setSelfHostName(String selfHostName) {
        BFSAlg.selfHostName = selfHostName;
    }

    public static HashMap<String, ArrayList<String>> getResult() {
        return result;
    }

    public static void compute() {
        LOGGER.info("Inside BFS Compute ...");

        try {
            ArrayList<String> resultWord = new ArrayList<>();  // Partition 1
            ArrayList<String> failedNodes = new ArrayList<>();  // Failed Nodes
            ArrayList<String> unKnownState = new ArrayList<>(); // Partition 2
            HashMap<String, VersionedNeighborhood> topo =
                    NSHello.getGlobalTopology();
            LinkedHashMap<String, VersionedNeighborhood> topology = new LinkedHashMap<>();
            topology.put(selfHostName, topo.get(selfHostName));

            for (String s : topo.keySet()) {
                if (s.equalsIgnoreCase(selfHostName)) {
                    //pass
                } else {
                    topology.put(s, topo.get(s));
                }
            }

            Queue<String> flowQueue = new LinkedList<>();   // Neighborhood Queue
            flowQueue.add(selfHostName);

            while (!flowQueue.isEmpty()) {
                String presentNode = flowQueue.remove();
                if (topology.get(presentNode).getNeighbor().keySet() != null && !resultWord.contains(presentNode)) {
                    flowQueue.addAll(topology.get(presentNode).getNeighbor().keySet());
                }
                if (!resultWord.contains(presentNode)) {
                    resultWord.add(presentNode);
                }
            }


            if (resultWord.size() < topology.keySet().size()) {
                for (String s : resultWord) {
                    topology.keySet().remove(s);
                }
                for (String nh : topology.keySet()) {
                    for (String p : resultWord) {
                        if (topology.get(nh).getNeighbor().containsKey(p)) {
                            if (!failedNodes.contains(nh)) {
                                failedNodes.add(nh);
                            }
                        }
                    }
                }
                for (String h : topology.keySet()) {
                    if (!failedNodes.contains(h)) {
                        if (!unKnownState.contains(h)) {
                            unKnownState.add(h);
                        }
                    }
                }


            }
            result.put("Partition 1", resultWord);
            result.put("Failed Nodes", failedNodes);
            result.put("Unknown States", unKnownState);
        }
        catch(Exception e)
        {
            LOGGER.error(e.getMessage());
        }

    }


}
