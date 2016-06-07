package com.harsha.celebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebrityUrls = new ArrayList<String>();
    ArrayList<String> celebrityNames = new ArrayList<String>();
    int chosenCelebrity = 0;
    int positionOfCorrectAnswer = 0;
    String[] options = new String[4];

    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);

        DownloadWebContentTask task = new DownloadWebContentTask();
        String result = null;

        try {

            result = task.execute("http://www.posh24.com/celebrities").get();

            String[] splitResult = result.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while (m.find()) {

                celebrityUrls.add(m.group(1));

            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while (m.find()) {

                celebrityNames.add(m.group(1));

            }


        } catch (InterruptedException e) {

            e.printStackTrace();

        } catch (ExecutionException e) {

            e.printStackTrace();

        }

        nextQuestion();

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

    // Downloading web content from "http://www.posh24.com/celebrities" and storing the webcontent in a string
    public class DownloadWebContentTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection)url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();
                }

                return result;

            }
            catch (Exception e) {

                e.printStackTrace();

            }

            return null;
        }
    }

    // Image is downloaded from a particular Url(Celebrity Image).
    public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;


            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }

            return null;
        }
    }

    // method for validating whether the chosen option is correct or not
    public void celebrityChosen(View view) {

        if (view.getTag().toString().equals(Integer.toString(positionOfCorrectAnswer))) {

            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(getApplicationContext(), "Wrong! It was " + celebrityNames.get(chosenCelebrity), Toast.LENGTH_LONG).show();

        }

        nextQuestion();

    }


    // method for next question
    public void nextQuestion() {

        Random random = new Random();
        chosenCelebrity = random.nextInt(celebrityUrls.size());

        ImageDownloaderTask imageTask = new ImageDownloaderTask();

        Bitmap celebImage;

        try {

            celebImage = imageTask.execute(celebrityUrls.get(chosenCelebrity)).get();

            imageView.setImageBitmap(celebImage);

            positionOfCorrectAnswer = random.nextInt(4);

            int incorrectAnswerLocation;

            for (int i=0; i<4; i++) {

                if (i == positionOfCorrectAnswer) {

                    options[i] = celebrityNames.get(chosenCelebrity);

                } else {

                    incorrectAnswerLocation = random.nextInt(celebrityUrls.size());

                    while (incorrectAnswerLocation == chosenCelebrity) {

                        incorrectAnswerLocation = random.nextInt(celebrityUrls.size());

                    }

                    options[i] = celebrityNames.get(incorrectAnswerLocation);


                }


            }

            button0.setText(options[0]);
            button1.setText(options[1]);
            button2.setText(options[2]);
            button3.setText(options[3]);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
