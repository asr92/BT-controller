package com.example.aravind.btremote;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import org.json.JSONException;
import org.json.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private EditText mInputView;
    private EditText mOutputView;
    private Switch mServiceSwitch;
    private Button mSendButton;
    private Button mIPButton;
    private Button mInitButton;
    private Button mStartQLButton;
    private Button mKillQLButton;

    private BluetoothServer mBluetoothServer;

    private static String preview0;
    private static String preview1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInputView = (EditText) findViewById(R.id.input);
        mOutputView = (EditText) findViewById(R.id.output);
        mServiceSwitch = (Switch) findViewById(R.id.serviceSwitch);
        mSendButton = (Button) findViewById(R.id.sendButton);
        mIPButton = (Button) findViewById(R.id.ipButton);
        mInitButton = (Button) findViewById(R.id.initButton);
        mStartQLButton = (Button) findViewById(R.id.startQLButton);
        mKillQLButton = (Button) findViewById(R.id.killQLButton);

        mBluetoothServer = new BluetoothServer();
        mBluetoothServer.setListener(mBluetoothServerListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBluetoothServer.stop();
        mBluetoothServer = null;
    }

    /**
     * Bluetooth server events listener.
     */
    private BluetoothServer.IBluetoothServerListener mBluetoothServerListener =
            new BluetoothServer.IBluetoothServerListener() {
                @Override
                public void onStarted() {
                    writeMessage("*** Server started, waiting for client connection ***");
                    mServiceSwitch.setChecked(true);
                }

                @Override
                public void onConnected() {
                    writeMessage("*** Client has connected ***");
                    mSendButton.setEnabled(true);
                    mIPButton.setEnabled(true);
                    mInitButton.setEnabled(true);
                    mStartQLButton.setEnabled(true);
                    mKillQLButton.setEnabled(true);
                }

                @Override
                public void onData(byte[] data) {
                    writeMessage(new String(data));
                }

                @Override
                public void onError(String message) {
                    writeError(message);
                }

                @Override
                public void onStopped() {
                    writeMessage("*** Server has stopped ***");
                    mKillQLButton.setEnabled(false);
                    mStartQLButton.setEnabled(false);
                    mInitButton.setEnabled(false);
                    mIPButton.setEnabled(false);
                    mSendButton.setEnabled(false);
                    mServiceSwitch.setChecked(false);
                }
            };

    public void onServiceClick(View view){
        if (mServiceSwitch.isChecked()){
            try {
                mBluetoothServer.start();
            } catch (BluetoothServer.BluetoothServerException e) {
                e.printStackTrace();
                writeError(e.getMessage());
            }
        } else {
            mBluetoothServer.stop();
        }
    }

    public void onSendClick(View view){
        try {
            mBluetoothServer.send(mOutputView.getText().toString().getBytes());
            mOutputView.setText("");
        } catch (BluetoothServer.BluetoothServerException e) {
            e.printStackTrace();
            writeError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            writeError(e.getMessage());
        }
    }

    public void onIPClick(View view){
        try {
            mBluetoothServer.send("./IP.sh".toString().getBytes());
        } catch (BluetoothServer.BluetoothServerException e) {
            e.printStackTrace();
            writeError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            writeError(e.getMessage());
        }
    }

    public void onInitClick(View view){

        Map<String, Integer> subpreview = new HashMap<String, Integer>();
        subpreview.put("type", 0);
        JSONObject payload = new JSONObject(subpreview);

        Map preview = new HashMap<>();
        preview.put("action", "preview");
        preview.put("payload", new JSONObject(subpreview).toString());

        JSONObject data = new JSONObject(preview);
        preview0 = data.toString();

        try {
            mBluetoothServer.send(preview0.getBytes());
        } catch (BluetoothServer.BluetoothServerException e) {
            e.printStackTrace();
            writeError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            writeError(e.getMessage());
        }
    }

    public void onStartQLClick(View view){

        Map<String, Integer> subpreview = new HashMap<String, Integer>();
        subpreview.put("type", 1);
        JSONObject payload = new JSONObject(subpreview);

        Map preview = new HashMap<>();
        preview.put("action", "preview");
        preview.put("payload", new JSONObject(subpreview).toString());

        JSONObject data = new JSONObject(preview);
        preview1 = data.toString();
        try {
            mBluetoothServer.send(preview1.getBytes());
        } catch (BluetoothServer.BluetoothServerException e) {
            e.printStackTrace();
            writeError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            writeError(e.getMessage());
        }
    }

    public void onKillQLClick(View view){
        try {
            mBluetoothServer.send("".toString().getBytes());
        } catch (BluetoothServer.BluetoothServerException e) {
            e.printStackTrace();
            writeError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            writeError(e.getMessage());
        }
    }

    private void writeMessage(String message){
        mInputView.setText(message + "\r\n" + mInputView.getText().toString());
    }

    private void writeError(String message){
        writeMessage("ERROR: " + message);
    }
}
