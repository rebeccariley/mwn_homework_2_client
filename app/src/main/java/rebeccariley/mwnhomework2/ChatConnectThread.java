package rebeccariley.mwnhomework2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Callable;

public class ChatConnectThread implements Callable<Boolean> {

    public ChatConnectThread(int port) {
        _port = port;
    }

    @Override
    public Boolean call() {
        try {
            DatagramSocket socket = new DatagramSocket(_port);
            DatagramPacket packet = new DatagramPacket("%%%verify_connection".getBytes(),
                                                       "%%%verify_connection".getBytes().length,
                                                       ChatRoom._address, ChatRoom._port_out);
            socket.send(packet);
            socket.receive(packet);
            socket.close();

            String test_verification = new String(packet.getData(), 0, packet.getLength());
            if (test_verification.equals("%%%verify_connection")) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (IOException e) { return false; }
    }

    private int _port;
}
