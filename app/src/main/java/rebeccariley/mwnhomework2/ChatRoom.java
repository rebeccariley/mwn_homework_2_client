package rebeccariley.mwnhomework2;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.os.Handler;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ChatRoom extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        try { setup(); }
        catch (IOException e) { return; }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setup() throws IOException {
        // setup chat environment
        ListView chat_pane = (ListView) findViewById(R.id.chat_pane);
        _chat_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                                           new ArrayList<String>());
        chat_pane.setAdapter(_chat_adapter);

        // register handler
        _handler = new ChatHandler();

        // server connection code
        _chat_adapter.add("Connecting");
        _address = InetAddress.getByName("52.88.233.162");

        // verify server connection
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ChatConnectThread verify = new ChatConnectThread(_port_in);
        Future<Boolean> connected = executor.submit(verify);
        try {
            if (connected.get()) {
                _chat_adapter.add("Connected");
            } else {
                _chat_adapter.add("Connection attempt failed. Try restarting app.");
                return;
            }
        } catch (ExecutionException e) { return ; } catch (InterruptedException e) { return; }

        // set name, check server for availability
        _chat_adapter.add("Enter a username, then press send.");
        // name registration functionality and launching of chat functionality happens
        // in onSetClicked()
    }

    public void onSendClicked(View blank) {
        new Thread(new ChatSendThread(_name + ": " + ((EditText)findViewById(R.id.editText)).getText().toString())).start();
        ((EditText)findViewById(R.id.editText)).setText("");
    }

    public void onSetClicked(View black) {
        if (_chat_adapter != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            ChatNameThread set_name = new ChatNameThread(_port_out,((EditText)findViewById(R.id.editText)).getText().toString());
            Future<Boolean> successful = executor.submit(set_name);
            try {
                if (successful.get()) {
                    _name = ((EditText)findViewById(R.id.editText)).getText().toString();
                    _chat_adapter.add("Name set successfully");
                    ((TextView)findViewById(R.id.user)).setText(((EditText)findViewById(R.id.editText)).getText().toString() + ":");
                    findViewById(R.id.set_button).setVisibility(View.GONE);
                    findViewById(R.id.send_button).setVisibility(View.VISIBLE);
                    ((EditText)findViewById(R.id.editText)).setText("");
                    // start receive thread
                    new ChatReceiveThread().start();
                } else {
                    _chat_adapter.add("Name set unsuccessful. Choose a new name and try again.");
                    return;
                }
            } catch (ExecutionException e) { return ; } catch (InterruptedException e) { return; }
        }
        return;
    }

    private static ArrayAdapter<String> _chat_adapter;
    public static Handler _handler;

    public static InetAddress _address;
    public static int _port_out = 2000;
    public static int _port_in = 2001;
    public static String _name;

    private class ChatHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            final String message = msg.obj.toString();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _chat_adapter.add(message);
                }
            });
        }
    }
}


