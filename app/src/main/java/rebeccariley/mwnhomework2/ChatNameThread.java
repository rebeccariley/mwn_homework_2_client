package rebeccariley.mwnhomework2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.Callable;

public class ChatNameThread implements Callable<Boolean> {

    public ChatNameThread(int port, String name) {
        _port = port;
        _name = name;
    }

    @Override
    public Boolean call() {
        try {
            DatagramSocket socket = new DatagramSocket(_port);
            DatagramPacket packet = new DatagramPacket(("%%%name" + _name).getBytes(),
                    ("%%%name" + _name).getBytes().length,
                    ChatRoom._address, ChatRoom._port_out);
            socket.send(packet);
            socket.receive(packet);
            socket.close();

            String name_set = new String(packet.getData(), 0, packet.getLength());
            if (name_set.startsWith("%%%av")) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (IOException e) { return false; }
    }

    private int _port;
    private String _name;
}

