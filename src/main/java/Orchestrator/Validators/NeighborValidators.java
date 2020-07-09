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
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class NeighborValidators  {

    private static final Logger LOGGER = LoggerFactory.getLogger(NeighborValidators.class.getCanonicalName());
    private ArrayList<Version> versionCache = new ArrayList<>();
    private Timer validationTimer ;
    private int updateInterval = 45000;
    private int delay = 30000;

    public NeighborValidators()
    {
        this.validationTimer = new Timer();
        this.validationTimer.schedule(new NeighborValidation(),delay,updateInterval);
        Version zVers = new Version();
        zVers.setVersion(0);
        for(int i=0;i<20;i++)
        {
            versionCache.add(zVers);
        }
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
            int index = 0;
                try
                {

                    Neighborhood nh = NSHello.getNeighborhood();
                    bufferedWriter = new BufferedWriter(new FileWriter("Partitions.log",true));

                    for(NeighborTopology nt : nh.getNeighbors())
                    {


                        Version t = nt.getRemoteTopology().getVersion();
                        int diff = (int) (t.getVersion() - versionCache.get(index).getVersion()) ;
                        if(diff == 0)
                        {
                            String log = new StringBuilder()
                                    .append("Possible Interface failure :" + nt.getHostId())
                                    .append(" after Version : "+ t.getVersion() )
                                    .append("\n")
                                    .toString();
                            bufferedWriter.write(log);
                        }

                        index += 1;
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

                index = 0;
                BufferedWriter bw = null;
                try
                {
                    bw = new BufferedWriter(new FileWriter("NetworkPartition.log",true));
                    ArrayList<SynchronizedOrchestratorResource> orcUpdate =
                    NSSynchronize.getMaximalResourceList();

                    for(SynchronizedOrchestratorResource syn : orcUpdate)
                    {
                        Version t2 = syn.getVersion();
                        int diff2 =   (t2.getVersion() - versionCache.get(index).getVersion()) ;
                        if(diff2 > 30)
                        {
                            String partitionLog = new StringBuilder()
                                    .append("Possible Desynchronization, failed node : " + syn.getHostId() )
                                    .append(" after Version : "+ t2 )
                                    .append("\n")
                                    .toString();
                            bw.write(partitionLog);
                        }
                        versionCache.set(index,t2);
                        index += 1;
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
