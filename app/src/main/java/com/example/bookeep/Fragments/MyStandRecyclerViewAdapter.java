package com.example.bookeep.Fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bookeep.Book;
import com.example.bookeep.Fragments.StandFragment.OnListFragmentInteractionListener;
import com.example.bookeep.R;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link com.example.bookeep.Fragments.dummy.DummyBooks} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getTitle());
        holder.mContentView.setText(mValues.get(position).getAuthors().toString());

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

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Book mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.book_title);
            mContentView = (TextView) view.findViewById(R.id.book_author);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

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
}
