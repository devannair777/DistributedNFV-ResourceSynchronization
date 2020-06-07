package Tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;

public class UDPEndpoint
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UDPEndpoint.class.getCanonicalName());


    public UDPEndpoint(){

    }

    private abstract class NetworkThread extends Thread
    {

        protected NetworkThread(String name)
        {
            super(name);
            setDaemon(true);
        }

        @Override
        public void run() {
           try
           {
               work();
           }
           catch (Exception e)
           {
               LOGGER.error(e.getMessage());
           }
        }

        protected abstract void work();


    }

    private class Sender extends NetworkThread
    {
        private DatagramPacket datagram;
        protected Sender(String name) {
            super(name);
        }

        @Override
        protected void work() {
           //TODO
        }
    }

    private class Receiver extends NetworkThread
    {
        private DatagramPacket datagram;

        protected Receiver(String name) {
            super(name);
        }

        @Override
        protected void work() {
            //TODO
        }
    }

}


