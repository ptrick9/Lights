package com.example.ptric.lightcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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


public class LightActivity extends AppCompatActivity {

    EchoWebSocketListener listener = new EchoWebSocketListener();

    ArrayList<Integer> lights = new ArrayList<Integer>();
    String config;
    String type;

    private RecyclerView recyclerView;
    public RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {

            Log.e("SOCKET", "opening");
        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            //output("Receiving : " + text);
            Log.e("SOCKET", text);
            try {
                JSONObject json = new JSONObject(text);
                String command = json.getString("command");
                if (command.equals("detail")) {
                    JSONObject jData = json.getJSONObject("data");
                    JSONArray jLights = jData.getJSONArray("lightValues");

                    lights.clear();
                    for(int i = 0; i < jLights.length(); i++) {
                        JSONObject light = jLights.getJSONObject(i);
                        lights.add(light.getInt("color"));
                    }
                    //mAdapter.notifyDataSetChanged();
                } else {
                    Log.e("JSON", "Error command not detail");
                }
            } catch (JSONException e) {
                Log.e("JSON", "ERROR parsing");
                e.printStackTrace();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.light_editing);

        Intent data = getIntent();
        config = data.getStringExtra("config");
        type = data.getStringExtra("type");




        recyclerView = (RecyclerView) findViewById(R.id.light_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new lightAdapter(lights);
        recyclerView.setAdapter(mAdapter);


        detail_config();

        Button addButton = this.findViewById(R.id.addLight);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int curSize = mAdapter.getItemCount();
                lights.add(0x000000);
                mAdapter.notifyDataSetChanged();

            }
        });

        Button saveButton = this.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_config();
            }
        });

        mAdapter.notifyDataSetChanged();


    }


    public void detail_config() {
        JSONObject command = new JSONObject();
        JSONObject data = new JSONObject();

        try {
            command.put("command", "detail_config");

            data.put("configName", config);

            command.put("data", data);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(MainActivity.getIP()).build();
        //Request request = new Request.Builder().url("ws://pilightcontroller.ddns.net:8765").build();
        WebSocket ws = client.newWebSocket(request, listener);
        ws.send(command.toString());
        Log.e("SOCKET", "sent");
    }


    public void send_config() {
        JSONObject command = new JSONObject();
        JSONObject data = new JSONObject();
        JSONArray lightVals = new JSONArray();
        if (type.equals("edit")) {
            try {
                command.put("command", "edit_config");

                data.put("configName", config);
                for(int i = 0; i < lights.size(); i++) {
                    JSONObject light = new JSONObject();
                    light.put("pos", i);
                    light.put("color", lights.get(i));
                    lightVals.put(light);
                }
                data.put("lightValues", lightVals);


                command.put("data", data);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(MainActivity.getIP()).build();
            //Request request = new Request.Builder().url("ws://pilightcontroller.ddns.net:8765").build();
            WebSocket ws = client.newWebSocket(request, listener);
            ws.send(command.toString());
            Log.e("SOCKET", "sent");
        }
    }

    public void removeAt(int i) {
        lights.remove(i);
        mAdapter.notifyDataSetChanged();
    }
}
