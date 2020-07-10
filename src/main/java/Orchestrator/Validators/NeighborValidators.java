package Orchestrator.Validators;

import Formatters.Version;
import Orchestrator.Messages.SynchronizedOrchestratorResource;
import Orchestrator.Resources.NSHello;
import Orchestrator.Resources.NSSynchronize;
import Orchestrator.Resources.NeighborTopology;
import Orchestrator.Resources.Neighborhood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;

public class NeighborValidators  {

    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborValidators.class.getCanonicalName());
    private static HashMap<String,Version> resourceversionCache = new HashMap<>();
    private static HashMap<String,Version> neighborVersionCache = new HashMap<>();
    private Timer validationTimer ;
    private int updateInterval = 45000;
    private int delay = 30000;
    private String selfName = "";

    public NeighborValidators(String thisname)
    {
        this.selfName = thisname;
        this.validationTimer = new Timer();
        this.validationTimer.schedule(new NeighborValidation(),delay,updateInterval);
        Version zVers = new Version();
        zVers.setVersion(0);
        for(int i=0;i<20;i++)
        {
            //versionCache.set(zVers);
        }
    }

    private class NeighborValidation extends TimerTask
    {

        @Override
        public void run()
        {
            LOGGER.info("Inside Neighbor Validator routine");
            BufferedWriter bufferedWriter = null;
            BufferedWriter bw3 = null;
            int index = 0;
                try
                {

                    Neighborhood nh = NSHello.getNeighborhood();
                    bufferedWriter = new BufferedWriter(new FileWriter("Partitions.log",true));

                    for(NeighborTopology nt : nh.getNeighbors())
                    {
                        if(neighborVersionCache.containsKey(nt.getHostId())) {
                            Version t = nt.getRemoteTopology().getVersion();
                            int diff = (int) (t.getVersion() - neighborVersionCache.get(nt.getHostId()).getVersion());
                            if (diff == 0) {
                                String log = new StringBuilder()
                                        .append("Possible Interface failure :" + nt.getHostId())
                                        .append(" after Version : " + t.getVersion())
                                        .append("\n")
                                        .toString();
                                bufferedWriter.write(log);
                            }
                            neighborVersionCache.get(nt.getHostId()).setVersion(nt.getRemoteTopology().getVersion().getVersion());
                        }
                        else
                        {
                            neighborVersionCache.put(nt.getHostId(),nt.getRemoteTopology().getVersion());
                        }

                       // index += 1;
                    }

                }
                catch (FileNotFoundException fne) {
                    LOGGER.error(fne.getMessage());
                } catch (IOException ioe) {
                    LOGGER.error(ioe.getMessage());
                } finally {
                    try {
                        bufferedWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                BufferedWriter bw = null;
                try
                {
                    bw = new BufferedWriter(new FileWriter("NetworkPartition.log",true));
                    bw3 = new BufferedWriter(new FileWriter("VersionCache.log",true));
                    ArrayList<SynchronizedOrchestratorResource> orcUpdate =
                    NSSynchronize.getMaximalResourceList();

                    for(SynchronizedOrchestratorResource syn : orcUpdate)
                    {
                        if(resourceversionCache.containsKey(syn.getHostId()) && !(syn.getHostId().equalsIgnoreCase(selfName))) {
                            Version t2 = syn.getVersion();
                            int diff2 = (t2.getVersion() - resourceversionCache.get(syn.getHostId()).getVersion());
                            if (diff2 == 0) {
                                String partitionLog = new StringBuilder()
                                        .append("Possible Desynchronization, failed node : " + syn.getHostId())
                                        .append(" after Version : " + t2.getVersion())
                                        .append("\n")
                                        .toString();
                                bw.write(partitionLog);
                            }


                            resourceversionCache.get(syn.getHostId()).setVersion(syn.getVersion().getVersion());
                        }
                        else
                        {
                            resourceversionCache.put(syn.getHostId(),syn.getVersion());
                        }
                       // index += 1;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        bw3.write("Index "+ index+"::"+resourceversionCache.toString()+"\n");
                        bw.close();
                        bw3.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

        }
    }


}
