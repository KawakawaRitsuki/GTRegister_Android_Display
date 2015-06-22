package com.kawakawaplanning.gtregister_display;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity {

    public static TextView tv1;
    public static TextView tv2;
    public static TextView tv3;
    public static TextView tv4;
    public static TextView tv5;
    public static TextView tv6;
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();

        SubThread sub = new SubThread();
        Thread thread = new Thread(sub);
        thread.start();

        tv1 = (TextView)findViewById(R.id.textView1);
        tv2 = (TextView)findViewById(R.id.textView2);
        tv3 = (TextView)findViewById(R.id.textView3);
        tv4 = (TextView)findViewById(R.id.textView4);
        tv5 = (TextView)findViewById(R.id.textView5);
        tv6 = (TextView)findViewById(R.id.textView6);

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
}

class SubThread implements Runnable{

    static ServerSocket serverSocket;
    static Socket socket;

    public void run(){
        check();
    }

    public static void check(){

        serverSocket = null;
        try {
            serverSocket = new ServerSocket(10222);


            System.out.println("start wait...");
            // 接続があるまでブロック
            socket = serverSocket.accept();
            BufferedReader br =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            final String[] str = br.readLine().split(",");

            MainActivity.handler.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.tv1.setText(str[0]);
                    MainActivity.tv2.setText(str[1]);
                    MainActivity.tv3.setText(str[2]);


                    String regex = "^s";
                    Pattern p = Pattern.compile(regex);
                    Matcher m = p.matcher(str[3]);

                    if (m.find()){
                        MainActivity.tv4.setText(str[3].substring(1));
                        TextPaint paint = MainActivity.tv4.getPaint();
                        paint.setFlags(MainActivity.tv4.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        paint.setAntiAlias(true);
                    }else{
                        MainActivity.tv4.setText(str[3]);
                        TextPaint paint = MainActivity.tv4.getPaint();
                        paint.setFlags(MainActivity.tv4.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }

                    MainActivity.tv5.setText(str[4]);
                    MainActivity.tv6.setText(str[5]);
                }
            });


            socket.close();
            socket = null;
            serverSocket.close();
            serverSocket = null;
            check();
        } catch (IOException e) {
            System.out.println(e);

        }
    }
    private static boolean checkBeforeWritefile(File file){
        if (file.exists()){
            if (file.isFile() && file.canWrite()){
                return true;
            }
        }
        return false;
    }
}