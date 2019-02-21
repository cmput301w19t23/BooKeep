package com.example.nafee.bookeep;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutionException;
/**
 * authors: Nafee Khan, Kyle Fujishige
 * */
public class BookDetailsActivity extends AppCompatActivity {

    private ImageView bookImage;
    private EditText bookTitle;
    private EditText bookAuthors;
    private EditText isbn;
    private EditText bookDescription;
    private TextView bookStatus;
    private Button scanBook;
    private Button saveBook;
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        bookImage = (ImageView) findViewById(R.id.bookImage);
        bookTitle = (EditText) findViewById(R.id.editBookTitle);
        bookAuthors = (EditText) findViewById(R.id.editBookAuthor);
        isbn = (EditText) findViewById(R.id.editISBN);
        bookStatus = (TextView) findViewById(R.id.txtBookStatus);
        bookDescription = (EditText) findViewById(R.id.editBookDescription);
        scanBook = (Button) findViewById(R.id.btnScanBook);
        saveBook = (Button) findViewById(R.id.btnSaveBook);
         /*
        public void onClickScan(View v) {

            new IntentIntegrator(this).initiateScan();
        }*/
        scanBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new IntentIntegrator(BookDetailsActivity.this).initiateScan();

            }

        });

    }
    //taken from https://stackoverflow.com/questions/18543668/integrate-zxing-in-android-studio
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                //txtView = findViewById(R.id.text1);
                if(isNetworkAvailable()){
                    //GoogleApiRequest.execute("");
                    GoogleApiRequest googleApiRequest = new GoogleApiRequest();

                    try {
                        jsonObject = (JSONObject) googleApiRequest.execute(result.getContents()).get();
                        //txtView.setText(obj.toString());

                        JSONArray jsonArray = (JSONArray) jsonObject.getJSONArray("items");
                        JSONObject item1 = jsonArray.getJSONObject(0);
                        JSONObject volumeInfo = item1.getJSONObject("volumeInfo");

                        String title = volumeInfo.getString("title");
                        bookTitle.setText(title);

                        String authors = volumeInfo.getJSONArray("authors").getString(0);
                        bookAuthors.setText(authors);

                        //String isbn = volumeInfo.getString()
                        JSONArray industryIdentifiers = (JSONArray) volumeInfo.getJSONArray("industryIdentifiers");
                        JSONObject isbn13 = industryIdentifiers.getJSONObject(1);
                        String isbn13String = isbn13.getString("identifier");
                        isbn.setText(isbn13String);

                        String description = volumeInfo.getString("description");
                        bookDescription.setText(description);

                        JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                        String imageURL = imageLinks.getString("thumbnail");

                        //Drawable bookImage = (Drawable) loadImageFromWebOperations(imageURL);
                        DownloadImageTask downloadImageTask = new DownloadImageTask();
                        Bitmap bookImageBitMap = downloadImageTask.execute(imageURL).get();
                        bookImage.setImageBitmap(bookImageBitMap);




                        bookStatus.setText(BookStatus.AVAILABLE.toString());


                        //txtView.setText(author);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }



            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    /*
    public static Drawable loadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }*/

    //taken from https://stackoverflow.com/questions/6407324/how-to-display-image-from-url-on-android
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        //ImageView bmImage;
        //public DownloadImageTask(ImageView bmImage) {
           // BookDetailsActivity.this.bookImage = bmImage;
        //}

        protected Bitmap doInBackground(String... urls) {

            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;

        }

        protected void onPostExecute(Bitmap result) {
            //bmImage.setImageBitmap(result);
        }

    }

}

