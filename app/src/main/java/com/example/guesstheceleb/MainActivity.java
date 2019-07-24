package com.example.guesstheceleb;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView celebImageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    ArrayList<String> celebNames = new ArrayList<>();
    ArrayList<String> celebURLS = new ArrayList<>();

    int chosenCelebrity = 0;
    String celebUrl = "http://www.posh24.se/kandisar";
    String[] answers = new String[4];
    int correctAnswerPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        celebImageView = findViewById(R.id.celebImageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        String[] htmlData = downloadHtmlData(celebUrl);


        Pattern p = Pattern.compile("<img src=\"(.*?)\"");
        Matcher m = p.matcher(htmlData[0]);

        while(m.find()){
            System.out.println(m.group(1));
            celebURLS.add(m.group(1));
        }

        p = Pattern.compile("alt=\"(.*?)\"");
        m = p.matcher(htmlData[0]);

        while(m.find()){
            System.out.println(m.group(1));
            celebNames.add(m.group(1));
        }

        newQuestion();
    }

    protected class DownloadImages extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inbound = connection.getInputStream();

                Bitmap image = BitmapFactory.decodeStream(inbound);

                return image;
            }
            catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    protected void downloadImage(String Url){
        DownloadImages task = new DownloadImages();
        Bitmap image;
        try {
            image = task.execute(Url).get();
            celebImageView.setImageBitmap(image);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public class DownloadContent extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream input = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader((input));
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Something went wrong.";
            }

            return result;
        }
    }

    protected String[] downloadHtmlData(String URL) {
        String resultString = "";
        DownloadContent task = new DownloadContent();
        try {
            resultString = task.execute(URL).get();
            return resultString.split("<div class=\"listedArticles\">");
        } catch (Exception e) {
            Log.i("Exception:", e.toString());
            return null;
        }
    }

    protected void selectedOption(View view){
        if(view.getTag().toString().equals(String.valueOf(correctAnswerPosition))){
            Toast.makeText(getBaseContext(),"Correct :)", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getBaseContext(),celebNames.get(chosenCelebrity), Toast.LENGTH_SHORT).show();
        }
        newQuestion();
    }

    protected void newQuestion(){
        Random random = new Random();

        chosenCelebrity = random.nextInt(celebNames.size());

        downloadImage(celebURLS.get(chosenCelebrity));

        correctAnswerPosition = random.nextInt(4);
        int incorrectAnswer;

        for(int i=0; i<4;i++){
            if(i == correctAnswerPosition){
                answers[i] = celebNames.get(chosenCelebrity);
            }else{
                incorrectAnswer = random.nextInt(celebNames.size());
                while(incorrectAnswer == chosenCelebrity){
                    incorrectAnswer = random.nextInt(celebNames.size());
                }
                answers[i] = celebNames.get(incorrectAnswer);
            }
        }

        button0.setText(answers[0]);
        button1.setText(answers[1]);
        button2.setText(answers[2]);
        button3.setText(answers[3]);
    }
}
