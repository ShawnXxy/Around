package site.shawnxxy.eventreporter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import site.shawnxxy.eventreporter.utils.AlertDialogManager;
import site.shawnxxy.eventreporter.utils.Utils;
import site.shawnxxy.eventreporter.adapter.CommentAdapter;

public class CommentActivity extends AppCompatActivity {

    /**
     *  attach recycler view to corresponding activity
     */
    private DatabaseReference mDatabaseReference;
    private RecyclerView mRecyclerView;
    private EditText mEditTextComment;
    private CommentAdapter commentAdapter;
    private Button mCommentSubmitButton;
    private RecyclerView.LayoutManager mLayoutManager;
    // Alert Dialog Manager
    private AlertDialogManager alert = new AlertDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
        final String postId = intent.getStringExtra("PostID");

        mRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler_view);
        mEditTextComment = (EditText) findViewById(R.id.comment_edittext);
        mCommentSubmitButton = (Button) findViewById(R.id.comment_submit);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        commentAdapter = new CommentAdapter(this);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // Add Button click event
        mCommentSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment(postId);
                mEditTextComment.setText("");
                getData(postId, commentAdapter);
            }
        });
        getData(postId, commentAdapter); // getData() defined below
    }

    private void getData(final String postId, final CommentAdapter commentAdapter) {
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot commentSnapshot = dataSnapshot.child("comments");
                List<Comment> comments = new ArrayList<>();
                for (DataSnapshot noteDataSnapshot : commentSnapshot.getChildren()) {
                    Comment comment = noteDataSnapshot.getValue(Comment.class);
                    if(comment.getPostId().equals(postId)) {
                        comments.add(comment);
                    }
                }
                mDatabaseReference.getRef().child("posts").child(postId).child("commentNumber").setValue(comments.size());
                commentAdapter.setComments(comments);

                DataSnapshot eventSnapshot = dataSnapshot.child("posts");
                for (DataSnapshot noteDataSnapshot : eventSnapshot.getChildren()) {
                    Post post = noteDataSnapshot.getValue(Post.class);
                    if(post.getId().equals(postId)) {
                        commentAdapter.setPost(post);
                        break;
                    }
                }
                if (mRecyclerView.getAdapter() != null) {
                    commentAdapter.notifyDataSetChanged();
                } else {
                    mRecyclerView.setAdapter(commentAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     *  Add Button click event
     * @param postId
     */
    private void sendComment(final String postId) {
        final String description = mEditTextComment.getText().toString();
        if (description.equals("")) {
            return;
        }
        Comment comment = new Comment();
        comment.setCommenter(Utils.username);
        comment.setPostId(postId);
        comment.setDescription(description);
        comment.setTime(System.currentTimeMillis());
        String key = mDatabaseReference.child("comments").push().getKey();
        comment.setCommentId(key);
        mDatabaseReference.child("comments").child(key).setValue(comment, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null ) {
//                    Toast toast = Toast.makeText(getApplicationContext(), "The comment is failed," + " please check you network status.", Toast.LENGTH_SHORT);
//                    toast.show();
                    alert.showAlertDialog(CommentActivity.this, "The comment is failed..", "Please check you network status!", false);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),"The comment is posted!", Toast.LENGTH_SHORT);
                    toast.show();

//                    Intent myIntent = new Intent(CommentActivity.this, CommentActivity.class);
//                    startActivity(myIntent);
                }
            }
        });
    }

}
