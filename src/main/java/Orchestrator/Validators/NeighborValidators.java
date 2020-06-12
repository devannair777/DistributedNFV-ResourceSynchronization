package Orchestrator.Validators;

import Orchestrator.Messages.SynchronizedOrchestratorResource;
import Orchestrator.Resources.NSHello;
import Orchestrator.Resources.NSSynchronize;
import Orchestrator.Resources.NeighborTopology;
import Orchestrator.Resources.Neighborhood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class NeighborValidators  {

    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborValidators.class.getCanonicalName());
    private Timer validationTimer ;
    private int updateInterval = 45000;
    private int delay = 30000;

    public NeighborValidators()
    {
        this.validationTimer = new Timer();
        this.validationTimer.schedule(new NeighborValidation(),delay,updateInterval);
    }

    private class NeighborValidation extends TimerTask
    {

        @Override
        public void run()
        {
            LOGGER.info("Inside Neighbor Validator routine");
            Date date = new Date();
            Timestamp now = new Timestamp(date.getTime());
            BufferedWriter bufferedWriter = null;

                try
                {

                    Neighborhood nh = NSHello.getNeighborhood();
                    bufferedWriter = new BufferedWriter(new FileWriter("Partitions.log",true));
                    for(NeighborTopology nt : nh.getNeighbors())
                    {


                        Timestamp t = nt.getRemoteTopology().getTimestamp();
                        int diff = (int) (now.getTime() - t.getTime())/1000 ;
                        if(diff > 30)
                        {
                            String log = new StringBuilder()
                                    .append("Possible Interface failure :" + nt.getHostId())
                                    .append(" after Time : "+ t + ". Down for :" +diff +" (sec)")
                                    .append("\n")
                                    .toString();
                            bufferedWriter.write(log);
                        }
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
                    ArrayList<SynchronizedOrchestratorResource> orcUpdate =
                    NSSynchronize.getMaximalResourceList();

                    for(SynchronizedOrchestratorResource syn : orcUpdate)
                    {
                        Timestamp t2 = syn.getTimestamp();
                        int diff2 = (int) (now.getTime() - t2.getTime())/1000 ;
                        if(diff2 > 30)
                        {
                            String partitionLog = new StringBuilder()
                                    .append("Possible Network Partition, failed node : " + syn.getHostId() )
                                    .append(" after Time : "+ t2 + ". Down for :" +diff2 +" (sec)")
                                    .append("\n")
                                    .toString();
                            bw.write(partitionLog);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

        }
    }


}
