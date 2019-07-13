package com.example.coco.a420project;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import com.vstechlab.easyfonts.EasyFonts;
import android.support.v7.app.AppCompatActivity;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.net.URL;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.StringTokenizer;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import java.util.Random;
import android.os.Environment;
import android.widget.Toast;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by coco on 17/4/8.
 */

public class Game extends AppCompatActivity{
    private TextView textView;
    public static String str, temp;     //to pass value between classes

    //record declare part
    Button rStart, rStop, rPlay, rStopPlay;     //Record Buttons
    String savePath = null;
    MediaRecorder mediaRecorder;
    Random random;
    String FileName = "abcdefghijklmno";        //use the letters to makeup random file name
    public static final int Permission = 1;     //set permission initial
    MediaPlayer mediaPlayer ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.game_page);     //virtical
        }else{
            setContentView(R.layout.another_game);      //landscape, horizontal
        }

        textView = (TextView)findViewById(R.id.text1);
        textView.setTypeface(EasyFonts.droidSerifRegular(this));  //*use the open source library-easyFonts!*

        //record part
        rStart = (Button) findViewById(R.id.button);
        rStop = (Button) findViewById(R.id.button2);
        rPlay = (Button) findViewById(R.id.button3);
        rStopPlay = (Button)findViewById(R.id.button4);

        //initial the start state
        rStop.setEnabled(false);
        rPlay.setEnabled(false);
        rStopPlay.setEnabled(false);

        random = new Random();
        record();   //call the record

        sendURLConnection();        //api request
        skip();     //intent. skip to Test act
    }
    //Because there is always timeout&thread problem. I use multiple threads to fix it.
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

                        temp=resp.toString();       //to use in later speak part
                        str=resp.toString();        //to use in later filling blanks
                        str=str.replaceAll("to","___");
                        str=str.replaceAll("is","___");
                        str=str.replaceAll("was","___");
                        str=str.replaceAll("the","___");
                        str=str.replaceAll("are","___");
                        str=str.replaceAll("were","___");
                        str=str.replaceAll("can","___");
                        str=str.replaceAll("of","___");
                        str=str.replaceAll("so","___");
                        str=str.replaceAll("on","___");
                        str=str.replaceAll("after","___");
                        str=str.replaceAll("before","___");
                        str=str.replaceAll("at","___");

                        //resp.append(str);
                    }
                    if(word.equals("url")){
                        String url;
                        token.nextToken();
                        url = token.nextToken();
                        //use webview to show the pic
                        WebView webView = (WebView)findViewById(R.id.image);

                        //using the below 2 could scale the image in a proper way
                        webView.getSettings().setLoadWithOverviewMode(true);
                        webView.getSettings().setUseWideViewPort(true);

                        webView.loadUrl(url);   //load image
                    }
                }
                textView.setText(resp);
            }
        });
    }
    /*Intent, skip to Test page.*/
    public void skip(){
        Button test = (Button)findViewById(R.id.buttonG);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Game.this, Test.class));
            }
        });
    }

    /*Record*/
    private void record(){

        //The start record button setting
        rStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check the record permission
                if(checkPermission()) {
                    savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                    CreateRandomFileName(5) + "AudioRecording.3gp";

                    Ready();        // Ready: set the source , output and encoding format and output file

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException exc) {
                        // TODO Auto-generated catch block
                        exc.printStackTrace();
                    } catch (IOException exc) {
                        // TODO Auto-generated catch block
                        exc.printStackTrace();
                    }

                    rStart.setEnabled(false);
                    rStop.setEnabled(true);

                    Toast.makeText(Game.this, "Starting Record.", Toast.LENGTH_LONG).show();
                } else {
                    requestPermission();    //ask permission
                }

            }
        });

        //the stop record button setting
        rStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();

                //initial setting
                rStop.setEnabled(false);
                rPlay.setEnabled(true);
                rStart.setEnabled(true);
                rStopPlay.setEnabled(false);

                Toast.makeText(Game.this, "Finishing Record.", Toast.LENGTH_LONG).show();
            }
        });

        //Play the record button setting
        rPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                //initial setting
                rStop.setEnabled(false);
                rStart.setEnabled(false);
                rStopPlay.setEnabled(true);

                mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(savePath);    //read record
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(Game.this, "Playing Record.", Toast.LENGTH_LONG).show();
            }
        });

        //stop playing record button setting
        rStopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rStop.setEnabled(false);
                rStart.setEnabled(true);
                rStopPlay.setEnabled(false);
                rPlay.setEnabled(true);

                if(mediaPlayer != null){        //playing
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    Ready();
                }
            }
        });
    }

    public void Ready(){
        //set the source , output and encoding format and output file
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(savePath);
    }

    //create random file name
    public String CreateRandomFileName(int str){

        StringBuilder stringBuilder = new StringBuilder(str);
        int i = 0 ;
        //str is the file name letter #
        while(i < str) {
            stringBuilder.append(FileName.charAt(random.nextInt(FileName.length())));
            i++ ;
        }
        return stringBuilder.toString();
    }

    //permit
    private void requestPermission() {
        ActivityCompat.requestPermissions(Game.this,
                new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, Permission);
    }

    //permit result;
    //May won't be used later
    //Show granted or not
    @Override
    public void onRequestPermissionsResult(int Code,
                                           String permissions[], int[] grantResults) {
        switch (Code) {
            case Permission:
                if (grantResults.length> 0) {
                    //store & record permit
                    boolean StorageP = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordP = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StorageP && RecordP) {
                        Toast.makeText(Game.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {    //no permit
                        Toast.makeText(Game.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    //check permit
    public boolean checkPermission() {
        //store
        int resultS = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);

        //record
        int resultR = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);

        return resultS == PackageManager.PERMISSION_GRANTED &&
                resultR == PackageManager.PERMISSION_GRANTED;
    }

}