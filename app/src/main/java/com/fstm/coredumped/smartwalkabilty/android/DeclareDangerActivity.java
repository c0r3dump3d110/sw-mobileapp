package com.fstm.coredumped.smartwalkabilty.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.fstm.coredumped.android.R;
import com.fstm.coredumped.smartwalkabilty.core.danger.bo.Accident;
import com.fstm.coredumped.smartwalkabilty.core.danger.bo.Danger;
import com.fstm.coredumped.smartwalkabilty.core.danger.bo.Traveaux;
import com.fstm.coredumped.smartwalkabilty.core.danger.bo.Vol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeclareDangerActivity extends AppCompatActivity {
    private static final String VeryLow="Very Low";
    private static final String low="Low";
    private static final String Medium="Medium";
    private static final String High="High";
    private static final String VeryHigh="Very High";
    ClientSocket socket=new ClientSocket();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declare_danger);
        initAll();
    }
    void initAll(){
        Spinner spinner=(Spinner) findViewById(R.id.dangerType);
        List<Danger> dangers=new ArrayList<>();
        dangers.add(new Vol());
        dangers.add(new Accident());
        dangers.add(new Traveaux());
        ArrayAdapter<Danger> testArrayAdapter=new ArrayAdapter<Danger>(this, android.R.layout.simple_spinner_item,dangers);
        testArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(testArrayAdapter);
        //Degrees
        List<String> strings=new ArrayList<>();
        strings.add(VeryLow);
        strings.add(low);
        strings.add(Medium);
        strings.add(High);
        strings.add(VeryHigh);

        ArrayAdapter<String> degreesAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,strings);
        degreesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner degreeSpinner=findViewById(R.id.dangerDegree);
        degreeSpinner.setAdapter(degreesAdapter);

        findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Danger danger=(Danger)(spinner.getSelectedItem());
                int degree=GetDegree(degreeSpinner.getSelectedItem().toString());
                danger.setDegree(degree);
                socket.SendDeclareDangerReq(DeclareDangerActivity.this,danger);
            }
        });
        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void Done(boolean t){
        if(t){
            Toast.makeText(DeclareDangerActivity.this,"Thank you for your contribution",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(DeclareDangerActivity.this,"Thank you for your contribution but your request was not valid",Toast.LENGTH_LONG).show();
        }
        finish();
    }
    private int GetDegree(String d){
        switch (d){
            case VeryLow : return 1;
            case low:  return 2;
            case Medium : return  3;
            case High : return 4;
            case VeryHigh : return 5;
            default: return 0;
        }
    }
}