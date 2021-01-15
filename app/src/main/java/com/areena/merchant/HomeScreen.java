package com.areena.merchant;

import android.content.Intent;
import android.os.Bundle;

import com.areena.merchant.Fragments.DemoClassfragment;
import com.areena.merchant.Fragments.HiredFragment;
import com.areena.merchant.Fragments.HomeFragment;
import com.areena.merchant.Fragments.Profilefragment;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class HomeScreen extends AppCompatActivity {

    BubbleNavigationConstraintView bubbleNavigation;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home_screen );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        bubbleNavigation=(BubbleNavigationConstraintView)findViewById( R.id.top_navigation_constraint ) ;

        bubbleNavigation.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                //navigation changed, do something
                Fragment fragment = null;
                switch (position){
                    case 0:
                        fragment = new HomeFragment();
                        loadFragment( fragment );
                        getSupportActionBar().setTitle( "Home" );
                        break;
                    case 1:
                        fragment = new DemoClassfragment();
                        loadFragment( fragment );
                        getSupportActionBar().setTitle( "Demo Class" );
                        break;
                    case 2:
                        fragment = new HiredFragment();
                        loadFragment( fragment );
                        getSupportActionBar().setTitle( "Hired Tutions" );
                        break;
                    case 3:
                        fragment = new Profilefragment();
                        loadFragment( fragment );
                        getSupportActionBar().setTitle( "Profile" );
                        break;


                }
            }
        });

        loadFragment( new HomeFragment() );
        getSupportActionBar().setTitle( "Home" );


    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container, fragment )
                    .commit();
            return true;
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_scree_menu, menu);
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notification:
                // Action goes here
                startActivity( new Intent( HomeScreen.this,NotificationActivity.class ) );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
