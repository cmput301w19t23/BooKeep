package com.example.bookeep;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder> {

    private final List<Book> mValues;

    public SearchRecyclerViewAdapter (List<Book> items) {

        mValues = items;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_search, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder,int i) {
        viewHolder.mItem = mValues.get(i);
        viewHolder.mIdView.setText(mValues.get(i).getTitle());
        viewHolder.mContentView.setText(mValues.get(i).getAuthors().toString());
        viewHolder.mOwner.setText(mValues.get(i).getOwner());
        DownloadImageTask downloadImageTask = new DownloadImageTask();
        try {
            Bitmap bookImage = downloadImageTask.execute(mValues.get(i).getBookImageURL()).get();
            viewHolder.imageView.setImageBitmap(bookImage);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Book mItem;
        public final ImageView imageView;
        public final TextView mOwner;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.bookTitle);
            mContentView = (TextView) view.findViewById(R.id.bookAuthor);
            mOwner = view.findViewById(R.id.bookOwner);
            imageView = view.findViewById(R.id.book_cover);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

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


    }
}
