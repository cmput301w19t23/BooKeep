package com.example.bookeep;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
/**
 * authors: Nafee Khan, Kyle Fujishige
 * */
public class AddEditBookActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private String currentUserID;
    private User currentUser;

    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;

    private ImageView bookImage;
    private EditText bookTitle;
    private EditText bookAuthors;
    private EditText isbn;
    private EditText bookDescription;
    private TextView bookStatus;
    private Button scanBook;
    private Button saveBook;
    private JSONObject jsonObject;
    private String bookLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_book);

        Intent intent = getIntent();
        setResult(RESULT_CANCELED, intent);

        bookImage = (ImageView) findViewById(R.id.bookImage);
        bookTitle = (EditText) findViewById(R.id.editBookTitle);
        bookAuthors = (EditText) findViewById(R.id.editBookAuthor);
        isbn = (EditText) findViewById(R.id.editISBN);
        bookStatus = (TextView) findViewById(R.id.txtBookStatus);
        bookDescription = (EditText) findViewById(R.id.editBookDescription);
        scanBook = (Button) findViewById(R.id.btnScanBook);
        saveBook = (Button) findViewById(R.id.btnSaveBook);

        scanBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddEditBookActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Permission has already been granted
                    new IntentIntegrator(AddEditBookActivity.this).initiateScan();
                } else {
                    // Permission is NOT granted
                    // Prompt the user for permission
                    ActivityCompat.requestPermissions(
                            AddEditBookActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA
                    );
                }
            }
        });

        isbn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (isbn.getText().toString().trim().length() == 13 ||
                            isbn.getText().toString().trim().length() == 10) {
                        setTextBoxes(isbn.getText().toString());
                    }
                }
            }
        });

    }

    public void saveButtonPressed(View view) {
        Boolean pass = Boolean.TRUE;

        if (bookTitle.getText().toString().isEmpty()) {
            bookTitle.setError("Missing title!");
            pass = Boolean.FALSE;
        }


        if (bookAuthors.getText().toString().isEmpty()) {
            bookAuthors.setError("Missing authors!");
            pass = Boolean.FALSE;
        }


        if (isbn.getText().toString().trim().isEmpty()) {
            isbn.setError("Missing isbn!");
            pass = Boolean.FALSE;
        }  else if (isbn.getText().toString().trim().length() == 10) {
            setTextBoxes(isbn.getText().toString());
        } else if (isbn.getText().toString().trim().length() != 13) {
            isbn.setError("Invalid isbn!");
            pass = Boolean.FALSE;
        }


        if (pass) {

            //Get the user object
            currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



            final Book book = new Book(isbn.getText().toString().trim(), currentUserID);

            ArrayList<String> Authors = new ArrayList<>();
            Authors.add(bookAuthors.getText().toString().trim());
            book.setAuthor(Authors);
            book.setTitle(bookTitle.getText().toString().trim());
            BitmapDrawable drawable = (BitmapDrawable) bookImage.getDrawable();
            //book.setBookImage(drawable.getBitmap());
            book.setStatus(BookStatus.AVAILABLE);
            final String bookID = book.getBookId();
            databaseReference.child("books").child(book.getBookId()).setValue(book);
            databaseReference.child("users").child(currentUserID).child("ownedIds").push().setValue(bookID);

            finish();


            //book gets saved here. Create book object, save the user ID in book
            //and save the book in users list of owned books. Return back to
            //activity/fragment that called this.
        }

    }

    public void ImageUpload(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, 69);
    }



    //https://developer.android.com/training/permissions/requesting#java
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                    new IntentIntegrator(AddEditBookActivity.this).initiateScan();
                } else {
                    // permission denied. Go back?
                }
                return;
            }

            // Make more cases to check for other permissions.
        }
    }

    public void setTextBoxes(String ISBN) {
        GoogleApiRequest googleApiRequest = new GoogleApiRequest();

        try {
            jsonObject = (JSONObject) googleApiRequest.execute(ISBN).get();
            //txtView.setText(obj.toString());

            JSONArray jsonArray = (JSONArray) jsonObject.getJSONArray("items");
            JSONObject item1 = jsonArray.getJSONObject(0);
            JSONObject volumeInfo = item1.getJSONObject("volumeInfo");

            String title = volumeInfo.getString("title");
            bookTitle.setText(title);
            bookTitle.setError(null);

            String authors = volumeInfo.getJSONArray("authors").getString(0);
            bookAuthors.setText(authors);
            bookAuthors.setError(null);

            //String isbn = volumeInfo.getString()
            JSONArray industryIdentifiers = (JSONArray) volumeInfo.getJSONArray("industryIdentifiers");
            JSONObject isbn13 = industryIdentifiers.getJSONObject(1);
            String isbn13String = isbn13.getString("identifier");
            isbn.setText(isbn13String);
            isbn.setError(null);

            String description = volumeInfo.getString("description");
            bookDescription.setText(description);

            JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
            bookLink = imageLinks.getString("thumbnail");
            bookImage.setImageBitmap(setPicture(bookLink));




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


    //taken from https://stackoverflow.com/questions/18543668/integrate-zxing-in-android-studio
    //also taken from https://stackoverflow.com/questions/38471963/setimagebitmap-not-displaying?rq=1
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 69 && resultCode == RESULT_OK) {
            if (data != null) {
                super.onActivityResult(requestCode, resultCode, data);
                Uri selectedImage = data.getData();
                bookLink = selectedImage.toString();
                bookImage.setImageBitmap(setPicture(bookLink));
            }
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Log.d("MainActivity", "Cancelled scan");
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("MainActivity", "Scanned");
                    Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    //txtView = findViewById(R.id.text1);
                    if (isNetworkAvailable()) {
                        if (result.getContents().length() == 10 || result.getContents().length() == 13)
                            setTextBoxes(result.getContents());
                        isbn.setText(result.getContents());
                    }
                }
            } else {
                // This is important, otherwise the result will not be passed to the fragment
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    //taken from https://stackoverflow.com/questions/6407324/how-to-display-image-from-url-on-android
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        //ImageView bmImage;
        //public DownloadImageTask(ImageView bmImage) {
        // AddEditBookActivity.this.bookImage = bmImage;
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

    public Bitmap setPicture(String ImageLink) {
        if (ImageLink.startsWith("http")) {
            if (isNetworkAvailable()) {
                DownloadImageTask downloadImageTask = new DownloadImageTask();
                Bitmap bookImageBitMap = null;
                try {
                    bookImageBitMap = downloadImageTask.execute(ImageLink).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return bookImageBitMap;
            }
        } else if (ImageLink != null){
            try {
                Uri selectedImage = Uri.parse(ImageLink);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString("Picture", bookLink);
        savedInstanceState.putString("Status", bookStatus.getText().toString());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        bookLink = savedInstanceState.getString("Picture");
        bookStatus.setText(savedInstanceState.getString("Status"));
        if (bookLink != null) {
            bookImage.setImageBitmap(setPicture(bookLink));
        }
    }
}

