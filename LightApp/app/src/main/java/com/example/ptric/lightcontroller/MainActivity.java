package com.example.ptric.lightcontroller;

import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class MainActivity extends AppCompatActivity {

    EchoWebSocketListener listener = new EchoWebSocketListener();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<String> configs = new ArrayList<String>();

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            //webSocket.send("Hello, it's SSaurel !");
            //webSocket.send("What's up ?");
            //webSocket.send(ByteString.decodeHex("deadbeef"));
            //webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !");
            Log.e("SOCKET", "opening");
        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            //output("Receiving : " + text);
            Log.e("SOCKET", text);
            try {
                JSONObject json = new JSONObject(text);
                String command = json.getString("command");
                if (command.equals("list")) {
                    JSONArray jConfigs = json.getJSONArray("data");
                    configs.clear();
                    for(int i = 0; i < jConfigs.length(); i++) {
                        configs.add(jConfigs.getString(i));
                    }
                } else {
                    Log.e("JSON", "Error command not list");
                }
            } catch (JSONException e) {
                Log.e("JSON", "ERROR parsing");
            }

        }
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            //output("Receiving bytes : " + bytes.hex());
            Log.e("SOCKET", bytes.hex());
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            //output("Closing : " + code + " / " + reason);
            Log.e("SOCKET", "closing");
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            //output("Error : " + t.getMessage());
            Log.e("SOCKET", "failure");
        }
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        list_configs();

        recyclerView = (RecyclerView) findViewById(R.id.config_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new configAdapter(configs);
        recyclerView.setAdapter(mAdapter);

        Switch onOff = (Switch) findViewById(R.id.on_off);
        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    Log.e("power", "on");
                    send_on_off(true);
                } else {
                    Log.e("power", "off");
                    send_on_off(false);
                }
            }
        });

        Button add = (Button) findViewById(R.id.addConfig);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText txtUrl = new EditText(MainActivity.this);

                // Set the default text to a link of the Queen
                txtUrl.setHint("Config Name");

                new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle)
                        .setTitle("Create New Config")
                        .setMessage("Enter Name of New Config")
                        .setView(txtUrl)
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                configs.add(txtUrl.getText().toString());
                                create_config(txtUrl.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();
                mAdapter.notifyDataSetChanged();
            }
        });

        SeekBar bright = (SeekBar) findViewById(R.id.brightBar);
        bright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                double level = (double)seekBar.getProgress() / (double)seekBar.getMax();
                sendLevel(level);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }

    public void send_select(String config) {
        JSONObject json = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            json.put("command", "select");
            data.put("config", config);
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        //Request request = new Request.Builder().url("ws://192.168.1.217:8765").build();
        Request request = new Request.Builder().url("ws://pilightcontroller.ddns.net:8765").build();
        WebSocket ws = client.newWebSocket(request, listener);
        ws.send(json.toString());
        Log.e("SOCKET", "sent");
    }

    public void sendLevel(double level) {
        JSONObject json = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            json.put("command", "brightness");

            data.put("level", level);

            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        //Request request = new Request.Builder().url("ws://192.168.1.217:8765").build();
        Request request = new Request.Builder().url("ws://pilightcontroller.ddns.net:8765").build();
        WebSocket ws = client.newWebSocket(request, listener);
        ws.send(json.toString());
        Log.e("SOCKET", "sent");
    }

    public void send_on_off(boolean on) {
        JSONObject json = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            json.put("command", "power");
            if (on) {
                data.put("state", "on");
            } else {
                data.put("state", "off");
            }
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        //Request request = new Request.Builder().url("ws://192.168.1.217:8765").build();
        Request request = new Request.Builder().url("ws://pilightcontroller.ddns.net:8765").build();
        WebSocket ws = client.newWebSocket(request, listener);
        ws.send(json.toString());
        Log.e("SOCKET", "sent");
    }

    public void remove_config(final String configName) {
        new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle)
                .setTitle("REMOVE")
                .setMessage("Do you REALLY want to remove? This is not recoverable.")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        reallyRemove(configName);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
        mAdapter.notifyDataSetChanged();
    }

    public void reallyRemove(String config) {
        JSONObject command = new JSONObject();
        JSONObject data = new JSONObject();
        JSONArray lightVals = new JSONArray();

        try {
            command.put("command", "delete_config");

            data.put("configName", config);

            command.put("data", data);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        //Request request = new Request.Builder().url("ws://192.168.1.217:8765").build();
        Request request = new Request.Builder().url("ws://pilightcontroller.ddns.net:8765").build();
        WebSocket ws = client.newWebSocket(request, listener);
        ws.send(command.toString());
        Log.e("SOCKET", "sent");
        configs.remove(config);
        mAdapter.notifyDataSetChanged();
    }

    public void refresh_clicked(View v) {
        list_configs();
        //mWebSocketClient.send(json.toString());
        mAdapter.notifyDataSetChanged();
    }

    public void list_configs() {
        JSONObject json = new JSONObject();
        try {
            json.put("command", "list_configs");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        //Request request = new Request.Builder().url("ws://192.168.1.217:8765").build();
        Request request = new Request.Builder().url("ws://pilightcontroller.ddns.net:8765").build();
        WebSocket ws = client.newWebSocket(request, listener);
        ws.send(json.toString());
        Log.e("SOCKET", "sent");
    }

    public void create_config(String config) {
        JSONObject command = new JSONObject();
        JSONObject data = new JSONObject();
        JSONArray lightVals = new JSONArray();

        try {
            command.put("command", "new_config");

            data.put("configName", config);

            data.put("lightValues", lightVals);


            command.put("data", data);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        //Request request = new Request.Builder().url("ws://192.168.1.217:8765").build();
        Request request = new Request.Builder().url("ws://pilightcontroller.ddns.net:8765").build();
        WebSocket ws = client.newWebSocket(request, listener);
        ws.send(command.toString());
        Log.e("SOCKET", "sent");

    }

}
