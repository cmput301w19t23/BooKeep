package com.example.bookeep.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.bookeep.AddEditBookActivity;
import com.example.bookeep.Book;
import com.example.bookeep.Fragments.StandFragment.OnListFragmentInteractionListener;
import com.example.bookeep.MainActivity;
import com.example.bookeep.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.bookeep.BookStatus.AVAILABLE;

/**
 * {@link RecyclerView.Adapter} that can display a BookList and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyStandRecyclerViewAdapter extends RecyclerView.Adapter<MyStandRecyclerViewAdapter.ViewHolder> {

    private final List<Book> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyStandRecyclerViewAdapter(List<Book> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_stand, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getTitle());
        holder.mContentView.setText(mValues.get(position).getAuthors().toString());
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow, position);
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });

        holder.imageView.setImageBitmap(setPicture(holder.mItem.getBookImage(), holder));

    }

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

    public Bitmap setPicture(String ImageLink, ViewHolder holder) {
        if (ImageLink.startsWith("http")) {
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
        } else if (ImageLink != null){
            try {
                Uri selectedImage = Uri.parse(ImageLink);
                Bitmap bitmap;
                bitmap = MediaStore.Images.Media.getBitmap(holder.imageView.getContext().getContentResolver(), selectedImage);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * RecyclerView ViewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Book mItem;
        public final ImageButton overflow;
        public final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.book_title);
            mContentView = (TextView) view.findViewById(R.id.book_author);
            overflow = (ImageButton) view.findViewById(R.id.overflow_menu);
            imageView = view.findViewById(R.id.imageView4);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }


    /**
     * TODO: Get rid of this once firebase is fully integrated
     * @param COUNT
     * @return A dummy book list
     */
    public static ArrayList<Book> createBookList(int COUNT) {
        ArrayList<Book> BookList = new ArrayList<Book>();
        Book newBook = new Book();
        newBook.setTitle("Foundation");
        ArrayList<String> authors = new ArrayList<String>();
        authors.add("Isaac Asimov");
        newBook.setAuthor(authors);
        for (int i = 1; i <= COUNT; i++) {
            BookList.add(newBook);
        }

        return BookList;
    }

    /**
     * Method to show the popup menu when the overflow button is pressed
     * @param view
     * @param position
     */
    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.stand_card_overflow, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenuItemClickListener(position));
        popup.show();
    }

    /** Custom click listener to make sure each card has a unique menu
     *
     */
    class PopupMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position;

        public PopupMenuItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int id = menuItem.getItemId();

            if (id == R.id.delete_book) {
                removeBook(position);
            }
            return false;
        }
    }

    /**
     * 
     * @param position
     */
    public void removeBook(int position) {
        if (mValues.get(position).getStatus() == AVAILABLE) {
            mValues.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mValues.size());
        }
    }
}
