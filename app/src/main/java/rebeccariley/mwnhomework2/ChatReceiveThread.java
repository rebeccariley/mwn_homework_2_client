package rebeccariley.mwnhomework2;

import android.os.Message;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ChatReceiveThread extends Thread {

    public ChatReceiveThread() {
        _buffer = new byte[SIZE];
        _packet = new DatagramPacket(_buffer, SIZE);
        try { _socket = new DatagramSocket(ChatRoom._port_in); }
        catch (SocketException e) { return; }
    }

    @Override
    public void run() {
        while (true) {
            try {
                _socket.receive(_packet);
                Message msg = Message.obtain();
                msg.obj = new String(_packet.getData(), 0, _packet.getLength());
                ChatRoom._handler.handleMessage(msg);
            } catch (IOException e) { return; }
        }
    }

    private byte[] _buffer;
    private int SIZE = 256;
    private DatagramSocket _socket;
    private DatagramPacket _packet;
}
