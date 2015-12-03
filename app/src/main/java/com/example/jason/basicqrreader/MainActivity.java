package com.example.jason.basicqrreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View.OnClickListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ArrayList rpiAddresses = new ArrayList();
    private Button scannerButton;
    private Button manuallyAddButton;
    private TextView txtResult;
    private Spinner deviceList;
    private Button submitButton;
    private TextView currentDeviceName;
    Networking network = new Networking();

    private Button LED1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        network.connect();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addListenerOnButton();
        addLED1Listener();

        txtResult = (TextView) findViewById(R.id.resultText);
        Log.e("TAG", Integer.toString(rpiAddresses.size()));
        if (rpiAddresses.size() > 0) {
            txtResult.setText(rpiAddresses.get(0).toString());
        }

        manuallyAddButton = (Button) findViewById(R.id.manuallyAdd);
        manuallyAddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ManualQR.class);
                startActivityForResult(intent,1);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode ==1) {
            String result = data.getStringExtra("RESULT");
            rpiAddresses.add(result);
            addItemsOnSpinnerDeviceList(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addItemsOnSpinnerDeviceList(String result) {
        deviceList = (Spinner) findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();
        for (int i=0; i<rpiAddresses.size(); i++) {
            list.add(rpiAddresses.get(i).toString());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deviceList.setAdapter(dataAdapter);
    }


    public void addListenerOnButton() {
        deviceList = (Spinner) findViewById(R.id.spinner);
        submitButton = (Button) findViewById(R.id.submitbutton);
        currentDeviceName = (TextView) findViewById(R.id.resultText);

        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                network.send("NAME,ANDROID1");
                currentDeviceName.setText(deviceList.getSelectedItem().toString());
            }
        });
    }

    public void addLED1Listener() {
        LED1 = (Button) findViewById(R.id.LED1Button);
        LED1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                network.send("MSG,"+deviceList.getSelectedItem().toString()+",TOGGLE");
            }
        });
    }


}

