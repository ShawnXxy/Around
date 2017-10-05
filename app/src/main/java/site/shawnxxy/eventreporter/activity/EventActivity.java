package site.shawnxxy.eventreporter.activity;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;

import site.shawnxxy.eventreporter.R;
import site.shawnxxy.eventreporter.fragments.EventsFragment;

public class EventActivity extends AppCompatActivity {

    private Fragment mEventsFragment;
//    EventsFragment eventsFragment;
    String username;
//    TextView usernameTextView;

    @BindView(R.id.relativelayout_event)
    CoordinatorLayout clContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        /**
         *  Add the fragment to event activity dynamically
         */
        // get username from MainActivity
//        Intent intent = getIntent();
//        username = intent.getStringExtra("Username");
//        usernameTextView = (TextView) findViewById(R.id.text_user);
//        usernameTextView.setText("Welcome, " + username);
        // Create ReportEventFragment
        if (mEventsFragment == null) {
            mEventsFragment = new EventsFragment();
        }
        // Add Fragment to the fragment
        getSupportFragmentManager().beginTransaction().add(R.id.relativelayout_event, mEventsFragment).commit();

//        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
//        // Set item click listener to the menu items
//        bottomNavigationView.setOnNavigationItemSelectedListener(
//                new BottomNavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.action_profile:
//                                getSupportFragmentManager().beginTransaction()
//                                        .replace(R.id.fragment_container, new EventsFragment()).commit();
//                                break;
//                            case R.id.action_events:
//                        }
//                        return false;
//                    }
//                }
//        );
    } // End of onCreate()

    public void showLikedSnackbar() {
        Snackbar.make(clContent, "Liked!", Snackbar.LENGTH_SHORT).show();
    }

    // Username used by fragment
//    public String getUsername() {
//        return username;
//    }
}
