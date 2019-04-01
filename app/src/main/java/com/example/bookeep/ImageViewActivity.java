package com.example.bookeep;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.concurrent.ExecutionException;

import cn.bluemobi.dylan.photoview.library.PhotoViewAttacher;

/**
 *  Sets and displays images assigned to books and users
 */
public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        Intent intent = getIntent();
        String imageURL = intent.getStringExtra("image");
        DownloadImageTask downloadImageTask = new DownloadImageTask();
        ImageView imageView = findViewById(R.id.imageView);
        try {

            Bitmap bitmap = downloadImageTask.execute(imageURL).get();
            imageView.setImageBitmap(bitmap);
            PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView);
            photoViewAttacher.update();


        } catch (ExecutionException e) {
            imageView.setImageResource(R.drawable.profile_pic);
        } catch (InterruptedException e) {
            imageView.setImageResource(R.drawable.profile_pic);
        }
    }
}
