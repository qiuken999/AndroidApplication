package com.example.coco.a420project;

import com.example.coco.a420project.*;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.os.Bundle;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;
import com.vstechlab.easyfonts.EasyFonts;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.StringTokenizer;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by coco on 17/4/12.
 */

public class High extends AppCompatActivity{

    private TextView textView, textView2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.high_page);     //virtical
        }else{
            setContentView(R.layout.another_high);      //landscape, horizontal
        }

        textView = (TextView)findViewById(R.id.text1);
        textView2 = (TextView)findViewById(R.id.text2);
        textView.setTypeface(EasyFonts.droidSerifRegular(this));
        textView2.setTypeface(EasyFonts.droidRobot(this));

        sendURLConnection();

        textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setVisibility(View.INVISIBLE);
            }
        }, 10000);

        textView2.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView2.setVisibility(View.INVISIBLE);
                textView2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView2.setVisibility(View.VISIBLE);
                    }
                }, 9000);
            }
        }, 0);

        //editW = (EditText)findViewById(R.id.write);

        //textView.setTypeface(EasyFonts.droidSerifRegular(this));  //*use the open source library-easyFonts!*

        skip();     //intent. skip to Test act
    }
    //Because there is always timeout&thread problem. I use multiple threads to fix it.

    /*Intent, skip to Test page.*/
    public void skip(){
        Button test = (Button)findViewById(R.id.buttonG);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(High.this, MainActivity.class));
            }
        });
    }

    public void sendURLConnection()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;    //connection initialize
                BufferedReader br = null;   //read buffer initialize

                try{
                    URL url = new URL("https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY");  //the demo key
                    con = (HttpURLConnection)url.openConnection();
                    con.setConnectTimeout(5000);
                    con.setReadTimeout(5000);

                    InputStream input = con.getInputStream();  //back to the input stream that reads from the open connection
                    br = new BufferedReader(new InputStreamReader(input));    //buffer
                    //response object
                    StringBuilder response = new StringBuilder();
                    String backText;
                    //append method to add return info to the obj
                    while ((backText = br.readLine())!=null){
                        response.append(backText);
                    }
                    showItems(response.toString());     //obj to string

                } catch (MalformedURLException exc) {     //thrown to a malformed url has occurred
                    exc.printStackTrace();
                } catch (IOException exc) {     //IO prob
                    exc.printStackTrace();
                }
                finally{
                    if(br!=null){
                        try{
                            br.close();         //close read buffer
                        } catch (IOException exc) {
                            exc.printStackTrace();
                        }
                    }
                    if(con!=null){
                        con.disconnect();        //disconnect
                    }
                }
            }
        }).start();
    }

    //show title/date/explain/image; mutilple threads; token method
    public void showItems(final String response){

        runOnUiThread(new Runnable() {      //thread problem
            @Override
            public void run() {     //thread prob
                StringBuilder resp = new StringBuilder();       //response output
                StringTokenizer token = new StringTokenizer(response, "\" ");

                int i;
                //token format: label(t1)  :(t2)  content(t3)
                while (token.hasMoreTokens()) {
                    String word = token.nextToken();    //use "word" to check the lable

                    // if(word.equals("title")){
                    //resp.append(word+token.nextToken()+"\t"+token.nextToken()+ "\n\n");
                    //}
                    if(word.equals("explanation")){
                        resp.append(word+token.nextToken()+"\t");
                        for(i=0;i<80;i++){              //set shown text size
                            resp.append(token.nextToken()+" ");
                        }
                        resp.append("......"+"\n\n");

                    }

                }
                textView.setText(resp);
                textView2.setText(resp);

            }
        });
    }

}
