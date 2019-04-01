package com.example.bookeep.Fragments;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookeep.Book;
import com.example.bookeep.DownloadImageTask;
import com.example.bookeep.Fragments.PendingRequestFragment.OnListFragmentInteractionListener;
import com.example.bookeep.R;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * {@link RecyclerView.Adapter} that can display a BookList and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 *
 * @author Jeff Kirker
 */
public class MyPendingRequestRecyclerViewAdapter extends RecyclerView.Adapter<MyPendingRequestRecyclerViewAdapter.ViewHolder> {

    private final List<Book> mValues;
    private final OnListFragmentInteractionListener mListener;

    /**
     * Constructor for the adapter.
     * @param items
     * @param listener
     */
    public MyPendingRequestRecyclerViewAdapter(List<Book> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    /**
     * Constructor for the view holder.
     * @param parent
     * @param viewType
     * @return ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_pendingrequest, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds the items in the list to be displayed to the view holder.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getTitle());
        holder.mContentView.setText(mValues.get(position).getAuthorsString());
        DownloadImageTask downloadImageTask = new DownloadImageTask();
        try {
            Bitmap bookImage = downloadImageTask.execute(mValues.get(position).getBookImageURL()).get();
            holder.mImageView.setImageBitmap(bookImage);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
    }

    /**
     * Counts the size of the list.
     * @return size of list
     */
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * The viewHolder. This takes all the attributes of the book object to be
     * displayed nad places them in appropriate views.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Book mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.book_title);
            mContentView = (TextView) view.findViewById(R.id.book_author);
            mImageView = view.findViewById(R.id.book_cover);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}