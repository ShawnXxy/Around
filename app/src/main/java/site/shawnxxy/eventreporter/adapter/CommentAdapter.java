package site.shawnxxy.eventreporter.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import site.shawnxxy.eventreporter.constructor.Comment;
import site.shawnxxy.eventreporter.constructor.Post;
import site.shawnxxy.eventreporter.R;
import site.shawnxxy.eventreporter.utils.Utils;

import static site.shawnxxy.eventreporter.utils.Utils.timeTransformer;

/**
 * Created by ShawnX on 9/17/17.
 */

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private final static int TYPE_EVENT = 0;
    private final static int TYPE_COMMENT = 1;

    private List<Comment> commentList;
    private Post post;

    private DatabaseReference databaseReference;
    private LayoutInflater inflater;

    public CommentAdapter(Context context) {
        this.context = context;
        commentList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setPost(final Post post) {
        this.post = post;
    }

    public void setComments(final List<Comment> comments) {
        this.commentList = comments;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_EVENT : TYPE_COMMENT;
    }

    @Override
    public int getItemCount() {
        return commentList.size() + 1;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView postUser;
        public TextView postTitle;
        public TextView postLocation;
        public TextView postDescription;
        public TextView postTime;
        public ImageView postImgView;
        public ImageButton btnLike;
        public ImageButton btnComment;
        public TextView postLikeNumber;
        public TextView postCommentNumber;

        public View layout;

        public PostViewHolder(View v) {
            super(v);
            layout = v;
            postUser = (TextView) v.findViewById(R.id.comment_main_user);
            postTitle = (TextView) v.findViewById(R.id.post_item_title);
            postLocation = (TextView) v.findViewById(R.id.post_item_location);
            postDescription = (TextView) v.findViewById(R.id.post_item_description);
            postTime = (TextView) v.findViewById(R.id.post_item_time);
            postImgView = (ImageView) v.findViewById(R.id.post_item_img);
            btnLike = (ImageButton) v.findViewById(R.id.btnLike);
            btnComment = (ImageButton) v.findViewById(R.id.btnComment);
            postLikeNumber = (TextView) v.findViewById(R.id.post_like_number);
            postCommentNumber = (TextView) v.findViewById(R.id.post_comment_number);
        }
    } // End

    /**
     *  create another holder that holds comment item
     */
    public class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView commentUser;
        public TextView commentTime;
        public TextView commentDescription;
        public View layout;
        public CommentViewHolder(View v) {
            super(v);
            layout = v;
            commentUser = (TextView) v.findViewById(R.id.comment_item_user);
            commentTime = (TextView) v.findViewById(R.id.comment_item_time);
            commentDescription = (TextView) v.findViewById(R.id.comment_item_description);
        }
    }

    /**
     *  create a view for holders
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType) {
            case TYPE_EVENT:
                v = inflater.inflate(R.layout.comment_main, parent, false);
                viewHolder = new PostViewHolder(v);
                break;
            case TYPE_COMMENT:
                v = inflater.inflate(R.layout.comment_item, parent, false);
                viewHolder = new CommentViewHolder(v);
                break;
        }
        return viewHolder;
    }

    /**
     *   shows view in recycler view
     * @param holder
     * @param position
     */
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case TYPE_EVENT:
                PostViewHolder viewHolderPost = (PostViewHolder) holder;
                configurePostView(viewHolderPost);
                break;
            case TYPE_COMMENT:
                CommentViewHolder viewHolderAds = (CommentViewHolder) holder;
                configureCommentView(viewHolderAds, position);
                break;
        }
    }

    private void configureCommentView(final CommentViewHolder holder, int position) {
        final Comment comment = commentList.get(position - 1);
        holder.commentUser.setText(comment.getCommenter());
        holder.commentTime.setText(Utils.timeTransformer(comment.getTime()));
        holder.commentDescription.setText(comment.getDescription());
    }

    boolean isLike = false;
    @SuppressLint("StaticFieldLeak")
    private void configurePostView(final PostViewHolder holder) {
        holder.postUser.setText(post.getUsername());
        holder.postTitle.setText(post.getTitle());
        String[] locations = post.getAddress().split(",");
        holder.postLocation.setText(locations[1] + "," + locations[2]);
        holder.postDescription.setText(post.getDescription());
        holder.postTime.setText(timeTransformer(post.getTime()));
        holder.postCommentNumber.setText(String.valueOf(post.getCommentNumber()));
        holder.postLikeNumber.setText(String.valueOf(post.getLike()));

        if (post.getImgUri() != null) {
            final String url = post.getImgUri();
            holder.postImgView.setVisibility(View.VISIBLE);
            new AsyncTask<Void, Void, Bitmap>(){
                @Override
                protected Bitmap doInBackground(Void... params) {
                    return Utils.getBitmapFromURL(url);
                }
                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    holder.postImgView.setImageBitmap(bitmap);
                }
            }.execute();
        } else {
            holder.postImgView.setVisibility(View.GONE);
        }
        // Add click post listener to Like
        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLike) {
                    holder.btnLike.setImageResource(R.drawable.ic_heart_outline_grey);
                    isLike = false;
                } else {
                    holder.btnLike.setImageResource(R.drawable.ic_heart_red);
                    isLike = true;
                }
                databaseReference.child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post recordedpost = snapshot.getValue(Post.class);
                            if (recordedpost.getId().equals(post.getId())) {
                                int number = recordedpost.getLike();
                                holder.postLikeNumber.setText(String.valueOf(number + 1));
                                snapshot.getRef().child("like").setValue(number + 1);
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

    }

}
