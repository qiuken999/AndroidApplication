package com.example.coco.a420project;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.speech.tts.TextToSpeech;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.coco.a420project.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * Created by coco on 17/4/11.
 */

public class Test extends AppCompatActivity {
    private TextView textView;
    private Button save;
    private Button read;
    private TextView answerT;
    private EditText edit;
    TextToSpeech ts;
    Button buttonTs;
    private String fileName = "save.txt";       //file name *internal storage!*

    private TextView answerO;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.test_page);     //virtical
        }else{
            setContentView(R.layout.another_test);      //landscape, horizontal
        }
        textView = (TextView)findViewById(R.id.textView2);
        buttonTs = (Button)findViewById(R.id.speech);


        textView.setText(Game.str);


        answerO = (TextView)findViewById(R.id.originT);


        ts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener(){
            public void onInit(int status){
                if(status != TextToSpeech.ERROR){
                    ts.setLanguage(Locale.CANADA);      //maybe later will add more choice with button
                }
            }
        });

        this.save = (Button)this.findViewById(R.id.saveB);
        this.edit = (EditText)this.findViewById(R.id.editText);
        this.read = (Button)this.findViewById(R.id.answer);
        answerT = (TextView)findViewById(R.id.answerText);
        /*save the input string into a internal storage: save.txt in ...data/file...catagory*/
        this.save.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                saveInput();        //call function to save strings in file
            }
        });

        /*read the compared answers*/
        this.read.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View vv){
                readOutput();
                answerO.setText("The original answer: "+"\n"+Game.temp+"\n");
            }
        });

        /*Speak the text in Game.class to give user prompt, when user fills blanks */
        buttonTs.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String toSpeak = Game.temp;     //the text in Game.class
                ts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH,null);
            }
        });
    }
    /*stop,maybe won't use later*/
    public void onPause(){
        if(ts != null){
            ts.stop();
            ts.shutdown();
        }
        super.onPause();;
    }
/*
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.
    }
*/
    /*save part, which is called by setonclicklistener above*/
    private void saveInput(){
        String input = this.edit.getText().toString();
        try {
            FileOutputStream out = this.openFileOutput(fileName, MODE_PRIVATE);

            out.write(input.getBytes());
            out.close();
            Toast.makeText(this, "File Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception exc){
            Toast.makeText(this, "Error: " + exc.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private void readOutput(){
        try {
            FileInputStream in = this.openFileInput(fileName);
            BufferedReader bufferR = new BufferedReader(new InputStreamReader(in));
            StringBuilder strB = new StringBuilder();
            String string = null;
            while (((string = bufferR.readLine()) != null)) {
                strB.append(string).append("\n");
            }
            this.answerT.setText("Your answer: "+ strB.toString());
        } catch (Exception exc){
            Toast.makeText(this, "Error: " + exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
