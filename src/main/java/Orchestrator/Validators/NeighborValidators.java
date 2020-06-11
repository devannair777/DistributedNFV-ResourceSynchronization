package Orchestrator.Validators;

import Orchestrator.Resources.NSHello;
import Orchestrator.Resources.NeighborTopology;
import Orchestrator.Resources.Neighborhood;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.sql.Timestamp;
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
            BufferedWriter bufferedWriter = null;

                try
                {
                    Neighborhood nh = NSHello.getNeighborhood();
                    bufferedWriter = new BufferedWriter(new FileWriter("Partitions.log",true));
                    for(NeighborTopology nt : nh.getNeighbors())
                    {
                        Date date = new Date();
                        Timestamp now = new Timestamp(date.getTime());
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

        }
    }


}
