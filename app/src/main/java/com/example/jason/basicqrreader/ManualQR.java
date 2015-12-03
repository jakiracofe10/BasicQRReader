package com.example.jason.basicqrreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Jason on 12/3/2015.
 */
public class ManualQR extends Activity implements OnClickListener {

    String result;
    Button sendResultBtn;
    EditText userInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_qr);

        userInput = (EditText) findViewById(R.id.etX);
        sendResultBtn = (Button) findViewById(R.id.btnSendResult);
        sendResultBtn.setOnClickListener(this);


        Intent intent = getIntent();
    }

    @Override
    public void onClick (View v) {
        Intent returnIntent = new Intent();
        result = userInput.getText().toString();
        returnIntent.putExtra("RESULT",result);
        setResult(1, returnIntent);
        finish();
    }

}
