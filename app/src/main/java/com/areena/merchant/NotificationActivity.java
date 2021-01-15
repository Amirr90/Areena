package com.areena.merchant;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class NotificationActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    List<DocumentSnapshot> data;
    FirebaseUser user;
    FirebaseFirestore db;
    ProgressBar progressBar;
    String TAG = "NotificationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_notification );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        setToolbar( toolbar, "Notification" );

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        data = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById( R.id.notification_rec );
        progressBar = (ProgressBar) findViewById( R.id.progressBar2 );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        NotificationAdapter adapter = new NotificationAdapter( data, this );
        recyclerView.setAdapter( adapter );
        loadData( adapter );
    }

    private void loadData(final NotificationAdapter adapter) {
        if (user != null) {
            db.collection( "HiredForDemoClass" )
                    .whereEqualTo( "tutorId", FirebaseAuth.getInstance().getCurrentUser().getUid() )
                    .orderBy( "timeStamp", Query.Direction.DESCENDING )
                    .addSnapshotListener( this, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            progressBar.setVisibility( View.GONE );
                            if (e == null) {
                                if (queryDocumentSnapshots != null) {
                                    if (queryDocumentSnapshots.isEmpty()) {
                                        Toast.makeText( NotificationActivity.this, "no Notification", Toast.LENGTH_SHORT ).show();
                                    } else {
                                        data.clear();
                                        data.addAll( queryDocumentSnapshots.getDocuments() );
                                        adapter.notifyDataSetChanged();
                                        recyclerView.setVisibility( View.VISIBLE );
                                    }

                                }
                            } else {

                                Log.d( TAG, e.getLocalizedMessage() );
                                Toast.makeText( NotificationActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
                            }
                        }
                    } );
        }
    }


    private void setToolbar(Toolbar toolbar, String id) {
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setDisplayShowHomeEnabled( true );
        getSupportActionBar().setTitle( id );
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
        List<DocumentSnapshot> list;
        Context context;

        public NotificationAdapter(List<DocumentSnapshot> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public NotificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.notification_view, parent, false );
            return new MyViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull final NotificationAdapter.MyViewHolder holder, final int position) {
            try {
                String userName = list.get( position ).getString( "userName" );
                //String tutionForClass = list.get( position ).getString( "userName" );

                long timeStamp = list.get( position ).getLong( "timeStamp" );
                long currenTimestamp = System.currentTimeMillis() - timeStamp;

                holder.userName.setText( userName+" send you request for demo class for 9th standard student" );
                holder.timeStamp.setText( getTimesAgo( timeStamp ) );

                boolean isSeen = list.get( position ).getBoolean( "isSeen" );
                changeLayoutColor( isSeen, holder );

                String requestStatus = list.get( position ).getString( "requestStatus" );
                if (requestStatus.equalsIgnoreCase( "pending" )) {
                    holder.mAcceptBtn.setEnabled( true );
                    holder.mDeclineBtn.setEnabled( true );
                } else if (requestStatus.equalsIgnoreCase( "accept" )) {
                    holder.mAcceptBtn.setEnabled( false );
                    holder.mDeclineBtn.setEnabled( false );
                    holder.mAcceptBtn.setText( "Accepted" );

                } else if (requestStatus.equalsIgnoreCase( "reject" )) {
                    holder.mAcceptBtn.setEnabled( false );
                    holder.mDeclineBtn.setEnabled( false );
                    holder.mDeclineBtn.setText( "Rejected" );

                }


                holder.mAcceptBtn.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean isSeen = list.get( position ).getBoolean( "isSeen" );
                        String id = list.get( position ).getId();
                        if (!isSeen) {
                            changeLayoutColor( true, holder );
                            changeSeenStatus( true, holder, id );
                        }
                        progressBar.setVisibility( View.VISIBLE );
                        holder.mAcceptBtn.setText( "Accepting" );

                        String status = "accept";
                        acceptDemoClassStatus( status, id, holder );
                    }
                } );

                holder.mDeclineBtn.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean isSeen = list.get( position ).getBoolean( "isSeen" );
                        String id = list.get( position ).getId();
                        if (!isSeen) {
                            changeSeenStatus( true, holder, id );
                        }
                        progressBar.setVisibility( View.VISIBLE );
                        holder.mDeclineBtn.setText( "Rejecting" );

                        String status = "reject";
                        rejectDemoClassStatus( status, id, holder );
                    }
                } );
            } catch (Exception e) {
            }

        }

        private String getTimesAgo(long time) {

            final int SECOND_MILLIS = 1000;
            final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
            final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
            final int DAY_MILLIS = 24 * HOUR_MILLIS;

            if (time < 1000000000000L) {
                time *= 1000;
            }

            long now = System.currentTimeMillis();
            if (time > now || time <= 0) {
                return null;
            }


            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes ago";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " days ago";
            }
        }

        private void changeSeenStatus(boolean b, final MyViewHolder holder, String id) {
            Map<String, Object> seenStatusMap = new HashMap<>();
            seenStatusMap.put( "isSeen", b );

            db.collection( "HiredForDemoClass" )
                    .document( id )
                    .update( seenStatusMap )
                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            changeLayoutColor( true, holder );
                        }
                    } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    changeLayoutColor( false, holder );
                }
            } );
        }

        private void changeLayoutColor(boolean isSeen, MyViewHolder holder) {
            if (!isSeen) {
                holder.layout.setBackgroundColor( context.getResources().getColor( R.color.colortransparent ) );
            } else {
                holder.layout.setBackgroundColor( Color.WHITE );
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView userName, timeStamp;
            private Button mAcceptBtn, mDeclineBtn;
            private RelativeLayout layout;

            public MyViewHolder(@NonNull View itemView) {
                super( itemView );
                userName = (TextView) itemView.findViewById( R.id.user_name );
                timeStamp = (TextView) itemView.findViewById( R.id.timestamp );

                mAcceptBtn = (Button) itemView.findViewById( R.id.accept );
                mDeclineBtn = (Button) itemView.findViewById( R.id.decline );

                layout = (RelativeLayout) itemView.findViewById( R.id.notification_lay );
            }
        }
    }

    private void rejectDemoClassStatus(String status, String id, final NotificationAdapter.MyViewHolder holder) {
        Map<String, Object> demoClassStatusMap = new HashMap<>();
        demoClassStatusMap.put( "requestStatus", status );
        db.collection( "HiredForDemoClass" )
                .document( id )
                .update( demoClassStatusMap )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility( View.GONE );
                        holder.mDeclineBtn.setText( "Rejected" );
                        holder.mDeclineBtn.setEnabled( false );
                        holder.mAcceptBtn.setEnabled( false );
                    }
                } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility( View.GONE );
                Toast.makeText( NotificationActivity.this, "can't reject, try again", Toast.LENGTH_SHORT ).show();
                holder.mDeclineBtn.setText( "Reject" );
            }
        } );
    }

    private void acceptDemoClassStatus(String status, String id, final NotificationAdapter.MyViewHolder holder) {
        Map<String, Object> demoClassStatusMap = new HashMap<>();
        demoClassStatusMap.put( "requestStatus", status );
        db.collection( "HiredForDemoClass" )
                .document( id )
                .update( demoClassStatusMap )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility( View.GONE );
                        holder.mAcceptBtn.setText( "Accepted" );
                        holder.mDeclineBtn.setEnabled( false );
                        holder.mAcceptBtn.setEnabled( false );
                    }
                } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility( View.GONE );
                Toast.makeText( NotificationActivity.this, "cant accept, try again", Toast.LENGTH_SHORT ).show();
                holder.mAcceptBtn.setText( "Accept" );
            }
        } );
    }
}
