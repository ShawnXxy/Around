package site.shawnxxy.eventreporter.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import site.shawnxxy.eventreporter.R;
import site.shawnxxy.eventreporter.activity.CommentActivity;
import site.shawnxxy.eventreporter.constructor.Post;
import site.shawnxxy.eventreporter.utils.Utils;

/**
 * Created by ShawnX on 9/10/17.
 */

//public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder>{
public class PostListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Post> postList;
    //  admob CardView
    private Context context;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_ADS = 1;
    private AdLoader.Builder builder;
    private LayoutInflater inflater;
    private DatabaseReference databaseReference;
    private Map<Integer, NativeExpressAdView> map = new HashMap<>();
    // btnLike action notification
    public static final String ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button";

    /**
     *
     *  Implement cardview for admob
     * @param posts
     * @param context
     */
    // constructor
    public PostListAdapter(List<Post> posts, final Context context) {
//        postList = posts;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // make new posts shown on the top
        Collections.sort(posts, new Comparator<Post>() {
            @Override
            public int compare(Post a, Post b) {
                return (int) (b.getTime() - a.getTime());
            }
        });
        postList = new ArrayList<>();
        int count = 0;
        // admob frequencies
        for (int i = 0; i < posts.size(); i++) {
            if (i % 5 == 1) {
                //Use a set to record advertisement position
                map.put(i + count, new NativeExpressAdView(context));
                count++;
                postList.add(new Post());
            }
            postList.add(posts.get(i));
        }
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }
    public Map<Integer, NativeExpressAdView> getMap() {
        return map;
    }
    public List<Post> getPostList() {
        return postList;
    }

    /**
     *  to show different views in recycler view
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        // Views for event content
        public TextView username;
        public TextView title;
        public TextView location;
        public TextView description;
        public TextView time;
        public ImageView imgview;
        // Extra views for Like and Comments
        public ImageButton btnLike;
        public ImageButton btnComment;
        public ImageButton btnMore;
        public TextView like_number;
        public TextView postCommentNumber;


        public View layout;
        public ViewHolder(View v) {
            super(v);
            layout = v;
            // Views for event content
            username = (TextView) v.findViewById(R.id.comment_main_user);
            title = (TextView) v.findViewById(R.id.post_item_title);
            location = (TextView) v.findViewById(R.id.post_item_location);
            description = (TextView) v.findViewById(R.id.post_item_description);
            time = (TextView) v.findViewById(R.id.post_item_time);
            imgview = (ImageView) v.findViewById(R.id.post_item_img);
            // Extra views for Like and Comments and Repost
            btnLike= (ImageButton) v.findViewById(R.id.btnLike);
            btnComment = (ImageButton) v.findViewById(R.id.btnComment);
            btnMore = (ImageButton) v.findViewById(R.id.btnMore);
            like_number = (TextView) v.findViewById(R.id.post_like_number);
            postCommentNumber = (TextView) v.findViewById(R.id.post_comment_number);
        }
    }
    public class ViewHolderAds extends RecyclerView.ViewHolder {
        public ViewHolderAds(View v) {
            super(v);
        }
    }
    @Override
    public int getItemViewType(int position) {
        return map.containsKey(position) ? TYPE_ADS : TYPE_ITEM;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;
        switch (viewType) {
            case TYPE_ITEM:
                v = inflater.inflate(R.layout.post_list_item, parent, false);
                viewHolder = new ViewHolder(v);
                break;
            case TYPE_ADS:
                v = inflater.inflate(R.layout.ads_container,
                        parent, false);
                viewHolder = new ViewHolderAds(v);
                break;
        }
        return viewHolder;
    }

    /**
     *  showing row views differently in recyclerview
     * @param holder
     * @param position
     */
    boolean isLike = false;
    private void configureItemView(final ViewHolder holder, final int position) {
        final Post post = postList.get(position);
        holder.title.setText(post.getTitle());
        holder.username.setText(post.getUsername());
        String[] locations = post.getAddress().split(",");
        holder.location.setText(locations[1] + "," + locations[2]);
        holder.description.setText(post.getDescription());
        holder.time.setText(Utils.timeTransformer(post.getTime()));
        // Get Like number and set the number to the textview
        holder.like_number.setText(String.valueOf(post.getLike()));
        holder.postCommentNumber.setText(String.valueOf(post.getCommentNumber()));

        if (post.getImgUri() != null) {
            final String url = post.getImgUri();
            holder.imgview.setVisibility(View.VISIBLE);
            new AsyncTask<Void, Void, Bitmap>(){
                @Override
                protected Bitmap doInBackground(Void... params) {
                    return Utils.getBitmapFromURL(url);
                }
                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    holder.imgview.setImageBitmap(bitmap);
                }
            }.execute();
        } else {
            holder.imgview.setVisibility(View.GONE);
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
                    int adapterPosition = holder.getAdapterPosition();
                    notifyItemChanged(adapterPosition, ACTION_LIKE_BUTTON_CLICKED);
//                    if (context instanceof MainActivity) {
//                        ((MainActivity) context).showLikedSnackbar();
//                    }
                }
                databaseReference.child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post recordedpost = snapshot.getValue(Post.class);
                            if (recordedpost.getId().equals(post.getId())) {
                                int number = recordedpost.getLike();
                                holder.like_number.setText(String.valueOf(number + 1));
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
        // Comments Activity Intent
        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                String postId = post.getId();
                intent.putExtra("PostID", postId);
                context.startActivity(intent);
            }
        });
        // btnMore
        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    } // End of configureItemView()

    private void configureAdsView(final ViewHolderAds adsHolder, final int position) {
        ViewHolderAds nativeExpressHolder = (ViewHolderAds) adsHolder;
        if (!map.containsKey(position)) {
            return;
        }
        NativeExpressAdView adView = map.get(position);
        ViewGroup adCardView = (ViewGroup) nativeExpressHolder.itemView;
        if (adCardView.getChildCount() > 0) {
            adCardView.removeAllViews();
        }

        if (adView.getParent() != null) {
            ((ViewGroup) adView.getParent()).removeView(adView);
        }

        adCardView.addView(adView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case TYPE_ITEM:
                ViewHolder viewHolderItem = (ViewHolder) holder;
                configureItemView(viewHolderItem, position);
                break;
            case TYPE_ADS:
                ViewHolderAds viewHolderAds = (ViewHolderAds) holder;
                configureAdsView(viewHolderAds, position);
                break;
        }
//        final Post event = postList.get(position);
//        holder.title.setText(event.getTitle());
//        String[] locations = event.getAddress().split(",");
//        holder.location.setText(locations[1] + "," + locations[2]);
//        holder.description.setText(event.getDescription());
//        holder.time.setText(Utils.timeTransformer(event.getTime()));
//
//        if (event.getImgUri() != null) {
//            final String url = event.getImgUri();
//            holder.imgview.setVisibility(View.VISIBLE);
//            new AsyncTask<Void, Void, Bitmap>(){
//                @Override
//                protected Bitmap doInBackground(Void... params) {
//                    return Utils.getBitmapFromURL(url);
//                }
//                @Override
//                protected void onPostExecute(Bitmap bitmap) {
//                    holder.imgview.setImageBitmap(bitmap);
//                }
//            }.execute();
//        } else {
//            holder.imgview.setVisibility(View.GONE);
//        }
    } // End of onBindVieHolder()
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return postList.size();
    }

    /**
     *  Return the view back to listview
     * @param position
     * @param post
     */
    public void add(int position, Post post) {
        postList.add(position, post);
        notifyItemInserted(position);
    }
    public void remove(int position) {
        postList.remove(position);
        notifyItemRemoved(position);
    }
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(
//                parent.getContext());
//        View v = inflater.inflate(R.layout.post_list_item, parent, false);
//        ViewHolder vh = new ViewHolder(v);
//        return vh;
//    }

} // END
