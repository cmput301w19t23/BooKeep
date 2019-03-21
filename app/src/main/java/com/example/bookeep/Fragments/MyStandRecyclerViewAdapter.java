package com.example.bookeep.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
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
import com.example.bookeep.R;
import com.example.bookeep.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
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
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    String currentUserId = firebaseUser.getUid();


    /**
     * Constructor for adapter.
     * @param items
     * @param listener
     */
    public MyStandRecyclerViewAdapter(List<Book> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;

    }

    /**
     * Constructor for view holder
     * @param parent
     * @param viewType
     * @return the view holder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_stand, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds the items in the list to be displayed to the view holder.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getTitle());
        holder.mContentView.setText(mValues.get(position).getAuthorsString());
        DownloadImageTask downloadImageTask = new DownloadImageTask();



        try {
            Bitmap bookImage = downloadImageTask.execute(mValues.get(position).getBookImageURL()).get();
            holder.imageView.setImageBitmap(bookImage);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //If new request, make the new request alert visible and turn the book
        // cover grayscale.
        if(mValues.get(position).getNewRequest()){
            holder.newRequestView.setVisibility(View.VISIBLE);
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            holder.imageView.setColorFilter(filter);
        }


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
    }

    /**
     *  Counts list size.
     * @return list size
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
        public final TextView mIdView;
        public final TextView mContentView;
        public Book mItem;
        public final ImageButton overflow;
        public final ImageView imageView;
        public final TextView newRequestView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.book_title);
            mContentView = (TextView) view.findViewById(R.id.book_author);
            overflow = (ImageButton) view.findViewById(R.id.overflow_menu);
            imageView = view.findViewById(R.id.book_cover);
            newRequestView = view.findViewById(R.id.new_request_view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    /**
     * Method to show the popup menu when the overflow button is pressed
     *
     * @param view
     * @param position
     */
    private void showPopupMenu(View view, int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.stand_card_overflow, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenuItemClickListener(position, view));
        popup.show();
    }

    /**
     * Custom click listener to make sure each card has a unique menu
     */
    class PopupMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        private int position;
        private View view;

        public PopupMenuItemClickListener(int position, View view) {
            this.position = position;
            this.view = view;
        }
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int id = menuItem.getItemId();

            if (id == R.id.delete_book) {
                removeBook(position);
            } else if (id == R.id.edit_book) {
                Intent intent = new Intent(view.getContext(), AddEditBookActivity.class);
                intent.putExtra("Book to edit", mValues.get(position));
                view.getContext().startActivity(intent);
            }
            return false;
        }
    }

    /** This method is for deleting books from the owned booklist.
     *
     * @param position
     */
    public void removeBook(final int position) {
        if (mValues.get(position).getStatus() == AVAILABLE) {

            databaseReference.child("books").child(mValues.get(position).getBookId()).removeValue();
            databaseReference.child("user-books").child(currentUserId).child(mValues.get(position).getBookId()).removeValue();
            databaseReference.child("users").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User currUser = dataSnapshot.getValue(User.class);
                    currUser.removeFromOwned(mValues.get(position).getBookId());
                    databaseReference.child("users").child(currentUserId).setValue(currUser);
                    mValues.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mValues.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }
    }


    /**
     * This class downloads the images when passed a URL.
     * TODO: Create a full class that can be called by the various methods that
     * use this. Super redundant.
     */
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
        protected void onPostExecute(Bitmap result) {}
    }
}
