package com.example.coco.a420project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import android.app.FragmentManager;


public class MainActivity extends AppCompatActivity implements java.io.Serializable{

    private ImageView image;
    private Button imageB;
    public static final String path="https://ooo.0o0.ooo/2017/04/13/58ef182c15a87.png"; //introduction image
    //private ProgressDialog dialog;
    Bitmap bit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);     //virtical
        }else{
            setContentView(R.layout.another_main);      //landscape, horizontal
        }
        image = (ImageView) findViewById(R.id.imageLogo);
        imageB = (Button) findViewById(R.id.intro);
        //image.setTag(path);


        initAnother();  //introduction image *AsyncTask! *
        init();     //go to Game.class
        initHigh();
    }


    private void init(){
        Button start = (Button)findViewById(R.id.button);

        start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Game.class));
            }
        });
    }

    private void initHigh(){
        Button next = (Button)findViewById(R.id.buttonH);

        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, High.class));
            }
        });
    }

    public void initAnother(){
        imageB.setOnClickListener(new OnClickListener(){
            public void onClick(View view){
                new Task().execute(path);
            }
        });
    }

    /*Class Task use AsyncTask to load image increasing speed*/
    public class Task extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            try{
                bit = BitmapFactory.decodeStream((InputStream)new URL(path).getContent());
            }catch (Exception exc){
                exc.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Bitmap result){
            super.onPostExecute(result);
            image.setImageBitmap(bit);
        }
    }
}
