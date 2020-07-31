package Orchestrator.Scheduler;

import Orchestrator.Validators.BFSAlg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Scheduler  {

    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class.getCanonicalName());

    private Timer validationTimer ;
    private int updateInterval = 30000;
    private int delay = 30000;

    public Scheduler(String hostName)
    {
        BFSAlg.setSelfHostName(hostName);
        this.validationTimer = new Timer();
        this.validationTimer.schedule(new TopologyCompute(),delay,updateInterval);

    }

    private class TopologyCompute extends TimerTask
    {

        @Override
        public void run()
        {
            LOGGER.info("Inside Scheduler Run");
            BFSAlg.compute();
            Yaml yaml = new Yaml();
            FileWriter fwRt = null;
            BufferedWriter bwRt = null;
            try
            {
                fwRt = new FileWriter("NetworkStates.yaml",false);
                bwRt = new BufferedWriter(fwRt);
                yaml.dump(BFSAlg.getResult(),bwRt);

            } catch (IOException e) {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    fwRt.close();
                    bwRt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }


}
