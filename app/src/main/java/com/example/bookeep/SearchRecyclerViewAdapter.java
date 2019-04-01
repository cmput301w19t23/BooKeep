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

/**
 * Searches books available
 * @author Kyle Fujishige, Nafee Khan, Jeff Kirker
 * @version 1.0.1
 * @see Book
 * @see User
 * @see SearchKeyWordsFragment
 * @see SearchActivity
 * @see SearchFragment
 */
public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder> {

    private final List<Book> mValues;
    private final SearchFragment.OnListFragmentInteractionListener mListener;

    public SearchRecyclerViewAdapter (List<Book> items,SearchFragment.OnListFragmentInteractionListener listener) {

        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_search, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder,int i) {

        viewHolder.mItem = mValues.get(i);
        viewHolder.mIdView.setText(viewHolder.mItem.getTitle());
        viewHolder.mContentView.setText(viewHolder.mItem.getAuthorsString());
        DownloadImageTask downloadImageTask = new DownloadImageTask();
        //Bitmap bookImage = null;
        try {
            Bitmap bookImage = downloadImageTask.execute(mValues.get(i).getBookImageURL()).get();
            viewHolder.imageView.setImageBitmap(bookImage);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(viewHolder.mItem);
                }
            }
        });

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

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.bookTitle);
            mContentView = (TextView) view.findViewById(R.id.bookAuthor);
            imageView = (ImageView) view.findViewById(R.id.book_cover);
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
