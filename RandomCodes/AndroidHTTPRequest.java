/*
This code is about how to send HTTP Request in Android.
I tried to make HTTP request in android 50+ times but failed. 
Then finally I got to know that WE ARE NOT ALLOWED TO MAKE HTTP REQUEST IN MAIN THREAD.
*/

package com.example.newrequestproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void SendTheRequest(View view)
    {
        //Getting TextView object
        mTextView = findViewById(R.id.midoutput);

        //Creating OkHttpClient instance
        OkHttpClient client = new OkHttpClient();
        
        String url = "https://pastebin.com/raw/mvFXpkPt";
        
        //Constructing a request using the specified URL
        Request request = new Request.Builder()
                .url(url)
                .build();
        
        /* Here, we use the "client" instance to make Request.
        Note that .enque(Callback) is used after newCall() and not execute().
        This is because execute() makes HTTP request in main thread which often crashes the application.
        Hence we enque() the request to make asynchronous call in the background thread. */
        client.newCall(request).enqueue(new Callback() {
            /* The onFailure and onResponse methods specify how to deal with the Response of the Request. */
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful())
                {
                    // Getting the response body as string
                    final String myResponse = response.body().string();
                    
                    /* Now we need to put the response in the TextView. But the TextView is only accessible by UI thread(main thread).
                    And currently we are in background thread where this request is being made. So, In order to write and execute code for the UI thread
                    from Background thread, we use the below code to do so */
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        // This run() method will run on UI thread and not in background thread.
                        public void run() {
                            mTextView.setText(myResponse);
                        }
                    });
                }
            }
        });
    }
}
