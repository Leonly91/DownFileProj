package com.example.DownFileProj;

import android.app.*;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity {
    private Button showProgressBtn;
    private ProgressDialog progressDialog;
    private ImageView imageView;
    private static String file_url = "http://api.androidhive.info/progressdialog/hive.jpg";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        showProgressBtn = (Button)findViewById(R.id.download_btn);
        imageView = (ImageView)findViewById(R.id.download_img);
        showProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DownloadFromUrl().execute(file_url);
            }
        });
    }

    class DownloadFromUrl extends AsyncTask<String, String, String>{

        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Downloading the file. Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... file_url) {
            int count = 0;
            try{
                URL url = new URL(file_url[0]);
                URLConnection connection = url.openConnection();
                int totalLength = connection.getContentLength();

                InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);
                OutputStream outputStream = new FileOutputStream("/sdcard/downloadedfile.jpg");

                byte[] data = new byte[1024];
                long total = 0;
                while((count = inputStream.read(data)) != -1){
                    total += count;
                    publishProgress("" + (int)((total*100)/totalLength));
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            }catch(Exception e){
                Log.e("Error:", e.getMessage());
            }
            return null;
        }

        protected void onProgressUpdate(String...progress){
            if (progressDialog != null){
                progressDialog.setProgress(Integer.parseInt(progress[0]));
            }
        }

        protected void onPostExecute(String file_url){
            if (progressDialog != null){
                progressDialog.dismiss();
            }
            String imgPath = Environment.getExternalStorageDirectory() + "/downloadedfile.jpg";
            imageView.setImageDrawable(Drawable.createFromPath(imgPath));
        }
    }

}
