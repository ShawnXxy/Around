package site.shawnxxy.eventreporter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import site.shawnxxy.eventreporter.R;
import site.shawnxxy.eventreporter.fragments.ConnectionFragment;
import site.shawnxxy.eventreporter.fragments.PostsFragment;
import site.shawnxxy.eventreporter.fragments.TrendsFragment;
import site.shawnxxy.eventreporter.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    // Session Manager Class
    SessionManager session;

    private Fragment mPostsFragment;

    @BindView(R.id.coordinatorlayout_post)
    CoordinatorLayout clContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

	    /**
		 *  Check session
		 */
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

	    /**
	     *  Adding toolbar to main screen
	     */
	    Toolbar toolbar = findViewById(R.id.toolbar);
	    setSupportActionBar(toolbar);


	    /**
	     *  Seeting Viewpager for each tab
	     */
	    ViewPager viewPager = findViewById(R.id.viewpager);
	    setupViewPager(viewPager);

	    /**
	     *  Set tabs inside Toolbar
	     */
	    TabLayout tabs = findViewById(R.id.tabs);
	    tabs.setupWithViewPager(viewPager);

	    /**
	     *  Add the fragment to main activity dynamically
         */
        // Create ReportEventFragment
//        if (mPostsFragment == null) {
//	        mPostsFragment = new PostsFragment();
//        }
//        // Add Fragment to the fragment
//        getSupportFragmentManager().beginTransaction().add(R.id.coordinatorlayout_post, mPostsFragment).commit();

        /**
         *  Drawer nav menu
         */
	    final DrawerLayout drawerLayout = findViewById(R.id.root);

	    // Remove text title in header
	    setSupportActionBar(toolbar);
	    getSupportActionBar().setDisplayShowTitleEnabled(false);

	    ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
	    drawerLayout.setDrawerListener(actionBarDrawerToggle);
	    actionBarDrawerToggle.syncState();

	    NavigationView navigationView = findViewById(R.id.nav_view);
	    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
		    @Override
		    public boolean onNavigationItemSelected(MenuItem item) {
			    drawerLayout.closeDrawers();
			    int id = item.getItemId();

			    if (id == R.id.menu_feed) {
			    	// Do something
				    return true;
			    } else if (id == R.id.menu_photo_you_liked) {
			    	// Do something
				    return true;
			    } else if (id == R.id.menu_news) {
			    	// Do something
			    } else if (id == R.id.menu_popular) {
			    	// Do something
				    return true;
			    } else if (id == R.id.menu_photos_nearby) {
			    	// Do something
				    Intent intent = new Intent(MainActivity.this, MainActivity.class);
				    startActivity(intent);
				    return true;
			    } else if (id == R.id.menu_rests_nearby) {
			    	// Do something
				    Intent intent = new Intent(MainActivity.this, RestaurantActivity.class);
				    startActivity(intent);
				    return true;
			    } else if (id == R.id.menu_events_nearby) {
			    	// Do something
				    return true;
			    } else if (id == R.id.menu_settings) {
			    	// Do something
				    return true;
			    } else if (id == R.id.menu_logout) {
			    	session.logoutUser();
				    return true;
			    } else if (id == R.id.menu_about) {
			    	// Do something
					return true;
			    }
			    return false;
		    }
	    });

	    /**
	     *  Adding Floating Action Button to bottom right of main view
	     */
	    FloatingActionButton fab = findViewById(R.id.fab);
	    fab.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    Intent postIntent = new Intent(MainActivity.this, PostActivity.class);
			    startActivity(postIntent);
		    }
	    });

//        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
//        // Set item click listener to the menu items
//        bottomNavigationView.setOnNavigationItemSelectedListener(
//                new BottomNavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.action_profile:
//                                getSupportFragmentManager().beginTransaction()
//                                        .replace(R.id.fragment_container, new PostsFragment()).commit();
//                                break;
//                            case R.id.action_events:
//                        }
//                        return false;
//                    }
//                }
//        );
    } // End of onCreate()

	/**
	 *  Add Fragments to Tabs
	 */
	private void setupViewPager(ViewPager viewPager) {
		Adapter adapter = new Adapter(getSupportFragmentManager());
		adapter.addFragment(new PostsFragment(), "Explore");
		adapter.addFragment(new ConnectionFragment(), "Connections");
		adapter.addFragment(new TrendsFragment(), "Trending");
		viewPager.setAdapter(adapter);
	}

	static class Adapter extends FragmentPagerAdapter {
		private final List<Fragment> mFragmentList = new ArrayList<>();
		private final List<String> mFragmentPostList = new ArrayList<>();
		public Adapter(FragmentManager manager) {
			super(manager);
		}

		@Override
		public Fragment getItem(int position) {
			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		public void addFragment(Fragment fragment, String title) {
			mFragmentList.add(fragment);
			mFragmentPostList.add(title);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mFragmentPostList.get(position);
		}
	} // End of setupViewPager()


}
