package Orchestrator;

import org.eclipse.californium.elements.UdpMulticastConnector;
import org.eclipse.californium.elements.util.NetworkInterfacesUtil;
import org.eclipse.californium.elements.util.StringUtil;

import java.io.IOException;
import java.net.*;

public class UdpMulticastEndpoint extends UdpMulticastConnector {
    private NetworkInterface intf;
    private InetAddress multicastGroup;

    public UdpMulticastEndpoint(InetAddress intfAddress, InetSocketAddress localAddress, InetAddress... multicastGroups) throws SocketException {
        super(intfAddress, localAddress, multicastGroups);
        this.intf = NetworkInterface.getByInetAddress(intfAddress);
        this.multicastGroup = multicastGroups[0];


    }

    public UdpMulticastEndpoint(InetSocketAddress localAddress, InetAddress... multicastGroups) {
        super(localAddress, multicastGroups);
    }

    public UdpMulticastEndpoint(String ipAddress,String McastAddr) throws SocketException {
        this(new InetSocketAddress(ipAddress,5600).getAddress(),
                new InetSocketAddress(ipAddress,5600),new InetSocketAddress(McastAddr,0).getAddress());
    }

    @Override
    public String toString() {
        return "UdpMulticastEndpoint{" +
                "intf=" + intf +
                ", multicastGroup=" + multicastGroup +
                '}';
    }

    @Override
    public synchronized void start() throws IOException, IOException {
        if (this.running)
            return;


        MulticastSocket socket = new MulticastSocket(null);
        socket.setLoopbackMode(true);
        try {
            socket.bind(new InetSocketAddress(5600));
            LOGGER.info("socket {}, loopback mode {}",
                    StringUtil.toString((InetSocketAddress) socket.getLocalSocketAddress()), socket.getLoopbackMode());
            socket.joinGroup(new InetSocketAddress(multicastGroup,0),this.intf);
            init(socket);
        } catch (BindException ex) {
            socket.close();
            LOGGER.error("can't bind to {}", StringUtil.toString(localAddr));
            throw ex;
        } catch (SocketException ex) {
            socket.close();
            LOGGER.error("can't bind to {}", StringUtil.toString(localAddr));
            throw ex;
        }


    }
}
