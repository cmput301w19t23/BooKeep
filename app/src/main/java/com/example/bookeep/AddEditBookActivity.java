package com.example.bookeep;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * This activity will allow the user to add books to the database or edit a book already in it. It
 * allows the user to manually enter all the fields or to scan the isbn of the book to auto-populate
 * the required fields.
 * @author Nafee Khan, Kyle Fujishige, jkirker
 * @see Book
 * @see User
 * @version 1.0.1
 * */
public class AddEditBookActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    private String currentUserID;
    private Book book;

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
    private User currentUser;
    private String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_book);

        // Initialize DB reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        setResult(RESULT_CANCELED,intent);

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Add a New Book");
        actionBar.setDisplayHomeAsUpEnabled(true);

        bookImage = (ImageView) findViewById(R.id.bookImage);
        bookTitle = (EditText) findViewById(R.id.editBookTitle);
        bookAuthors = (EditText) findViewById(R.id.editBookAuthor);
        isbn = (EditText) findViewById(R.id.editISBN);
        bookStatus = (TextView) findViewById(R.id.txtBookStatus);
        bookDescription = (EditText) findViewById(R.id.editBookDescription);
        scanBook = (Button) findViewById(R.id.btnScanBook);
        saveBook = (Button) findViewById(R.id.btnSaveBook);
        bookStatus.setText(BookStatus.AVAILABLE.toString());

        Bundle bundle = intent.getExtras();

        if (bundle != null) {

            book = (Book) intent.getSerializableExtra("Book to edit");

            databaseReference.child("books").child(book.getBookId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    book = dataSnapshot.getValue(Book.class);
                    bookTitle.setText(book.getTitle());
                    bookAuthors.setText(book.getAuthorsString());
                    isbn.setText(book.getISBN());
                    bookStatus.setText(book.getStatus().toString());
                    bookDescription.setText(book.getDescription());
                    DownloadImageTask downloadImageTask = new DownloadImageTask();

                    try {
                        Bitmap bitmap = downloadImageTask.execute(book.getBookImageURL()).get();
                        bookImage.setImageBitmap(bitmap);

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }
        scanBook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddEditBookActivity.this,Manifest.permission.CAMERA)
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
            public void onFocusChange(View v,boolean hasFocus) {
                if (!hasFocus) {
                    if (isbn.getText().toString().trim().length() == 13 ||
                            isbn.getText().toString().trim().length() == 10) {
                        setTextBoxes(isbn.getText().toString());
                    }
                }
            }

        });

    }

    /**
     * This function supports back navigation.
     * @return
     */
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    /**
     * When users presses save button this method is called. Will push the book to the database but
     * first check to make sure fields are properly filled out.
     * @param view View
     */
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
        } else if (isbn.getText().toString().trim().length() == 10) {
            setTextBoxes(isbn.getText().toString());
        } else if (isbn.getText().toString().trim().length() != 13) {
            isbn.setError("Invalid isbn!");
            pass = Boolean.FALSE;
        }
        //if all fields are entered properly the book is added to firebase
        if (pass) {
            //Get the user object
            currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (book == null) {
                book = new Book(isbn.getText().toString().trim(),currentUserID);

                ArrayList<String> Authors = new ArrayList<>();
                Authors.add(bookAuthors.getText().toString().trim());
                book.setAuthor(Authors);
                book.setTitle(bookTitle.getText().toString().trim());
                book.setDescription(bookDescription.getText().toString().trim());

                if (imageURL == null) {
                    Bitmap bitmap;
                    bookImage.setDrawingCacheEnabled(true);
                    bookImage.buildDrawingCache();
                    bitmap = ((BitmapDrawable) bookImage.getDrawable()).getBitmap();
                    uploadImageToFireBase(bitmap);
                } else {
                    book.setBookImageURL(imageURL);
                }
                book.setStatus(BookStatus.AVAILABLE);
                book.setISBN(isbn.getText().toString());

                // Add book to "user-books" sorted by userID
                databaseReference.child("user-books").child(currentUserID).child(book.getBookId()).setValue(book);
                databaseReference.child("users").child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        currentUser = dataSnapshot.getValue(User.class);
                        currentUser.addToOwned(book.getBookId());
                        databaseReference.child("users").child(currentUserID).setValue(currentUser);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

                databaseReference.child("books").child(book.getBookId()).setValue(book);


            } else {

                book.setISBN(isbn.getText().toString().trim());
                ArrayList<String> Authors = new ArrayList<>();
                Authors.add(bookAuthors.getText().toString().trim());
                book.setAuthor(Authors);
                book.setTitle(bookTitle.getText().toString().trim());
                deleteImageFromFireBase(book.getBookImageURL());
                if (imageURL == null) {
                    Bitmap bitmap;
                    bookImage.setDrawingCacheEnabled(true);
                    bookImage.buildDrawingCache();
                    bitmap = ((BitmapDrawable) bookImage.getDrawable()).getBitmap();
                    uploadImageToFireBase(bitmap);
                } else {
                    book.setBookImageURL(imageURL);
                }
                book.setDescription(bookDescription.getText().toString().trim());
                book.setBookImageURL(book.getBookImageURL());

                //Replace all requested instances of the book with the edited version.
                List<String> mRequesters = book.getRequesterIds();
                for(int i = 0; i<mRequesters.size(); i++){
                    databaseReference.child("user-requested")
                            .child(mRequesters.get(i))
                            .child(book.getBookId())
                            .setValue(book);
                }
                databaseReference.child("user-books").child(currentUserID).child(book.getBookId()).setValue(book);
                databaseReference.child("books").child(book.getBookId()).setValue(book);

            }
            // Return to stand and display books
            finish();
        }
    }

    /**
     * Gets a user uploaded image
     * @param view View
     */
    public void ImageUpload(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,69);

    }

    /**
     * asks for camera persmission
     * see https://developer.android.com/training/permissions/requesting#java
     *
     * @param requestCode int
     * @param permissions Sting[]
     * @param grantResults int[]
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                    new IntentIntegrator(AddEditBookActivity.this).initiateScan();
                } else {
                    // permission denied. Go back.
                }
                return;
            }
        }
    }

    /**
     * Gets data from isbn to fill the text boxes, uses googleApiRequest to get info with and isbn
     * and then parses the the returned json object for book details
     * @param ISBN String
     */
    public void setTextBoxes(String ISBN) {
        GoogleApiRequest googleApiRequest = new GoogleApiRequest();
        if (isNetworkAvailable()) {

            try {
                jsonObject = (JSONObject) googleApiRequest.execute(ISBN).get();
                JSONArray jsonArray = (JSONArray) jsonObject.getJSONArray("items");
                JSONObject item1 = jsonArray.getJSONObject(0);
                JSONObject volumeInfo = item1.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");
                bookTitle.setText(title);
                bookTitle.setError(null);

                String authors = volumeInfo.getJSONArray("authors").getString(0);
                bookAuthors.setText(authors);
                bookAuthors.setError(null);

                JSONArray industryIdentifiers = (JSONArray) volumeInfo.getJSONArray("industryIdentifiers");

                JSONObject isbn0 = industryIdentifiers.getJSONObject(0);
                String isbn0String = isbn0.getString("identifier");

                JSONObject isbn1 = industryIdentifiers.getJSONObject(1);
                String isbn1String = isbn1.getString("identifier");

                if(isbn0String.length() == 13) {
                    isbn.setText(isbn0String);
                    isbn.setError(null);
                } else {
                    isbn.setText(isbn1String);
                    isbn.setError(null);
                }
                String description = volumeInfo.getString("description");
                bookDescription.setText(description);

                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                imageURL = imageLinks.getString("thumbnail");

                DownloadImageTask downloadImageTask = new DownloadImageTask();
                Bitmap bookImageBitMap = downloadImageTask.execute(imageURL).get();
                bookImage.setImageBitmap(bookImageBitMap);

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //taken from https://stackoverflow.com/questions/18543668/integrate-zxing-in-android-studio
    //also taken from https://stackoverflow.com/questions/38471963/setimagebitmap-not-displaying?rq=1

    /**
     * taken from https://stackoverflow.com/questions/18543668/integrate-zxing-in-android-studio
     * also taken from https://stackoverflow.com/questions/38471963/setimagebitmap-not-displaying?rq=1
     * @param requestCode int
     * @param resultCode int
     * @param data Intent
     */
    public void onActivityResult(int requestCode,int resultCode,Intent data) {

        if (requestCode == 69 && resultCode == RESULT_OK) {
            if (data != null) {
                super.onActivityResult(requestCode,resultCode,data);
                Uri selectedImage = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                    bookImage.setImageBitmap(bitmap);
                    imageURL = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
            if (result != null) {
                if (result.getContents() == null) {
                    Log.d("MainActivity","Cancelled scan");
                    Toast.makeText(this,"Cancelled",Toast.LENGTH_LONG).show();
                } else {
                    Log.d("MainActivity","Scanned");
                    Toast.makeText(this,"Scanned: " + result.getContents(),Toast.LENGTH_LONG).show();

                    if (isNetworkAvailable()) {
                        if (result.getContents().length() == 10 || result.getContents().length() == 13)
                            setTextBoxes(result.getContents());
                        isbn.setText(result.getContents());
                    }
                }
            } else {
                // This is important, otherwise the result will not be passed to the fragment
                super.onActivityResult(requestCode,resultCode,data);
            }
        }

    }

    /**
     * Returns true if network available and false otherwise
     * @return boolean
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    // resources used for this method:
    // https://firebase.google.com/docs/storage/android/upload-files#get_a_download_url

    /**
     * Upload an image bitmap taken from the imageview, and place it into firebase.
     * @param bitmap
     */
    public void uploadImageToFireBase (Bitmap bitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        Calendar now = Calendar.getInstance();
        String strDate = Integer.toString(now.get(Calendar.YEAR)) +
                Integer.toString(now.get(Calendar.MONTH)) +
                Integer.toString(now.get(Calendar.DAY_OF_MONTH)) +
                Integer.toString(now.get(Calendar.HOUR_OF_DAY)) +
                Integer.toString(now.get(Calendar.MINUTE)) +
                Integer.toString(now.get(Calendar.SECOND)) +
                Integer.toString(now.get(Calendar.MILLISECOND));

        StorageReference storageReference = storage.getReferenceFromUrl("gs://bookeep-684ab.appspot.com");
        final StorageReference imageRef = storageReference.child(strDate + ".jpg");

        Bitmap resize = Bitmap.createScaledBitmap(bitmap, 270,270, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resize.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imageRef.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    imageURL = task.getResult().toString();
                    book.setBookImageURL(imageURL);
                    databaseReference.child("user-books").child(currentUserID).child(book.getBookId()).setValue(book);
                    databaseReference.child("books").child(book.getBookId()).setValue(book);
                }
                else {
                    return;
                }
            }
        });
    }

    public void deleteImageFromFireBase (String fireBaseUrl) {
        String[] strings = fireBaseUrl.split("\\?");
        strings = strings[0].split("/");
        String storageLink = strings[strings.length-1];
        if (storageLink.startsWith("2019")) {
            StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReferenceFromUrl("gs://bookeep-684ab.appspot.com").child(storageLink);
            storageReference.delete();
        }
    }

    public void onDeleteButtonClicked(View view) {
        bookImage.setImageResource(R.drawable.common_full_open_on_phone);
        imageURL = null;
    }
}
