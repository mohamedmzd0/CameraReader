package com.example.mohamedabdelaziz.camreader;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by Mohamed Abd ELaziz on 9/4/2017.
 */

public class PDFAsyncTask extends AsyncTask<Void,Void,Void> {
    Context context;
    String text;


    public PDFAsyncTask(Context context, String text) {
        this.context = context;
        this.text = text;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context, "started", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(context, "complete", Toast.LENGTH_SHORT).show();
    }

}
