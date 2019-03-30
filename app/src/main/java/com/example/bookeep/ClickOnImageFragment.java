package com.example.bookeep;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.concurrent.ExecutionException;

public class ClickOnImageFragment extends Fragment {

    public ClickOnImageFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_click_on_image, container, false);
        ImageView imageView = view.findViewById(R.id.imageView3);
        final String imageURL = getArguments().getString("image");
        DownloadImageTask downloadImageTask = new DownloadImageTask();

        try {

            Bitmap bitmap = downloadImageTask.execute(imageURL).get();
            //bitmap = Bitmap.createScaledBitmap(bitmap, 380,380,false);
            imageView.setImageBitmap(bitmap);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return view;
    }
}
