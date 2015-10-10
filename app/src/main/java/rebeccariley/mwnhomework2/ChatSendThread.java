package rebeccariley.mwnhomework2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ChatSendThread implements Runnable {

    public ChatSendThread(String msg) {
        try { _socket = new DatagramSocket(ChatRoom._port_out); }
        catch (SocketException e) { return ;}
        _packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, ChatRoom._address, ChatRoom._port_out);
    }

    @Override
    public void run() {
        try { _socket.send(_packet); }
        catch (IOException e) { return; }
        _socket.close();
    }

    private DatagramSocket _socket;
    private DatagramPacket _packet;
}
