package com.example.bookeep;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.bookeep.RequestsOnBookFragment.OnListFragmentInteractionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * {@link RecyclerView.Adapter} that can display a  makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 * @author Nafee Khan, Jeff Kirker
 * @version 1.0.1
 */
public class RequestsOnBookRecyclerViewAdapter extends RecyclerView.Adapter<RequestsOnBookRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<User> mValues;
    private final OnListFragmentInteractionListener mListener;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    String currentUserId = firebaseUser.getUid();
    private String mBookId;
    private Book mBook;
    private List<String> mRequesters;



    public RequestsOnBookRecyclerViewAdapter(ArrayList<User> items, String bookId, OnListFragmentInteractionListener listener, List<String> requesters) {
        mValues = items;
        mListener = listener;
        mBookId = bookId;
        mRequesters = requesters;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //mItem is the requester.
        holder.mItem = mValues.get(position);
        if(holder.mItem != null) {
            String borrowerId = holder.mItem.getUserId();
            //holder.mIdView.setText(mValues.get(position).id);
            //holder.mContentView.setText(mValues.get(position).content);
            holder.txtRequesterName.setText(mValues.get(position).getFirstname() + " " + mValues.get(position).getLastname());
            holder.txtRequesterUsername.setText("@" + mValues.get(position).getUserName());
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DownloadImageTask downloadImageTask = new DownloadImageTask();
            try {
                Bitmap bitmap = downloadImageTask.execute(holder.mItem.getImageURL()).get();
                holder.imVRequesterPic.setImageBitmap(bitmap);

            } catch (ExecutionException e) {
                holder.imVRequesterPic.setImageResource(R.drawable.profile_pic);
            } catch (InterruptedException e) {
                holder.imVRequesterPic.setImageResource(R.drawable.profile_pic);
            }
            DatabaseReference ratingRef = database.getReference("borrowerRatings").child(holder.mItem.getUserId());
            ratingRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Rating rating = dataSnapshot.getValue(LenderRating.class);
                    if (rating == null) {
                        rating = new LenderRating(holder.mItem.getUserId());
                    } else {
                        rating.recalculateRating();
                    }
                    Float overallRating = rating.getRating();
                    int numRatings = rating.getNumRatings();
                    holder.borrowerRatingBar.setRating(overallRating);
                    String numReviewsString = "(" + numRatings + ")";
                    holder.numReviews.setText(numReviewsString);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            holder.btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference.child("books").child(mBookId).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            mBook = dataSnapshot.getValue(Book.class);
                            mBook.setNewAccepted();
                            mBook.setCurrentBorrower(holder.mItem.getUserId());
                            mBook.setStatus(BookStatus.ACCEPTED);
                            mBook.clearRequesters();
                            databaseReference.child("books").child(mBookId).setValue(mBook);
                            databaseReference.child("user-books").child(currentUserId).child(mBookId).setValue(mBook);
                            databaseReference.child("user-borrowed").child(holder.mItem.getUserId()).child(mBookId).setValue(mBook);

                            for (int i = 0; i < mRequesters.size(); i++) {
                                databaseReference.child("user-requested")
                                        .child(mRequesters.get(i))
                                        .child(mBook.getBookId())
                                        .removeValue();
                            }


                            databaseReference.child("users").child(holder.mItem.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    User confirmedRequester = dataSnapshot.getValue(User.class);
                                    confirmedRequester.addToBorrowed(mBookId);
                                    databaseReference.child("users").child(confirmedRequester.getUserId()).setValue(confirmedRequester);
                                    mValues.clear();
                                    notifyItemRangeRemoved(0, mValues.size());


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
            holder.btnDeclineRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference.child("books").child(mBookId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            mBook = dataSnapshot.getValue(Book.class);

                            mBook.removeRequester(mValues.get(position).getUserId());
                            if (mBook.getRequesterIds().size() == 0) {
                                mBook.setStatus(BookStatus.AVAILABLE);
                            }
                            databaseReference.child("user-requested")
                                    .child(holder.mItem.getUserId())
                                    .child(mBook.getBookId())
                                    .removeValue();

                            for (int i = 0; i < mRequesters.size(); i++) {
                                databaseReference.child("user-requested")
                                        .child(mRequesters.get(i))
                                        .child(mBook.getBookId())
                                        .setValue(mBook);
                            }


                            databaseReference.child("books").child(mBookId).setValue(mBook);
                            databaseReference.child("user-books").child(currentUserId).child(mBookId).setValue(mBook);
                            removeRequester(position);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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

    }

    /**
     * gets the number of items
     * @return int of number of items
     */
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final TextView mIdView;
        //public final TextView mContentView;
        //public DummyItem mItem;
        public final TextView txtRequesterName;
        public final TextView txtRequesterUsername;
        public final Button btnAcceptRequest;
        public final Button btnDeclineRequest;
        public final ImageView imVRequesterPic;
        public final RatingBar borrowerRatingBar;
        public final TextView numReviews;
        public User mItem;

        public ViewHolder(View view) {

            super(view);
            mView = view;
            //mIdView = (TextView) view.findViewById(R.id.item_number);
            //mContentView = (TextView) view.findViewById(R.id.content);
            txtRequesterName = view.findViewById(R.id.requester_name);
            txtRequesterUsername = view.findViewById(R.id.requester_username);
            btnAcceptRequest = view.findViewById(R.id.accept_request);
            btnDeclineRequest = view.findViewById(R.id.decline_request);
            imVRequesterPic = view.findViewById(R.id.user_image_request);
            borrowerRatingBar = view.findViewById(R.id.borrower_ratingbar);
            numReviews = view.findViewById(R.id.review_count);

        }

        @Override
        public String toString() {

            return super.toString() + " '" + txtRequesterUsername.getText() + "'";

        }

    }

    /**
     * removes requester from the books requesters
     * @param position int position of requester to be removec
     */
    public void removeRequester(int position){

        if(mValues.get(position) != null ){

            mValues.remove(position);
            //notifyItemRemoved(position);
            //notifyItemRangeChanged(position, mValues.size());
            notifyDataSetChanged();
        }
    }
}
