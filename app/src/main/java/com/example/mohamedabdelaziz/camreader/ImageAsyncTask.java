package com.example.mohamedabdelaziz.camreader;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * Created by Mohamed Abd ELaziz on 9/4/2017.
 */

public class ImageAsyncTask extends AsyncTask<Void, Void, Void> {
    Context context;
    byte[] bytes;

    public ImageAsyncTask(Context context, byte[] bytes) {
        this.context = context;
        this.bytes = bytes;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(context, "image saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/CamReader";
        File dir = new File(file_path);
        if (!dir.exists())
            dir.mkdirs();

        File photo = new File(file_path, "Cam" + new Date().getTime() + ".jpeg");
        try {
            FileOutputStream fos=new FileOutputStream(photo.getPath());

            fos.write(bytes);
            fos.close();
        }
        catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
        return null;
    }
}
