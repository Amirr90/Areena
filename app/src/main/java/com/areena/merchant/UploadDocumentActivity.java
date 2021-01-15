package com.areena.merchant;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UploadDocumentActivity extends AppCompatActivity {
    String ID;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_upload_document );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        db = FirebaseFirestore.getInstance();

        if (getIntent().hasExtra( "id" )) {
            ID = getIntent().getStringExtra( "id" );
            setDocumentDetail( ID );
            setToolbar( toolbar, "Upload Document" );
            hideKeyboard();
        }

    }

    private void setDocumentDetail(String id) {
        final TextView tvstatus = (TextView) findViewById( R.id.textView2 );
        final  EditText mAdharNumber=(EditText)findViewById( R.id.editText ) ;
        db.collection( "MerchantList" )
                .document( id )
                .get()
                .addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot merchantSnap = task.getResult();
                            if (merchantSnap != null && merchantSnap.exists()) {
                                String status = merchantSnap.getString( "documentVerifiedStatus" );

                                if (status != null && status.equalsIgnoreCase( "rejected" )) {
                                    String rejectMsg=merchantSnap.getString("document.reject_msg");
                                    tvstatus.setText( status+"\n"+rejectMsg );
                                    tvstatus.setTextColor( Color.RED );
                                    tvstatus.setCompoundDrawables( null, null, getResources().getDrawable( R.drawable.ic_close_black_24dp ), null );
                                    setUploadedDocumentImage( merchantSnap );
                                } else if (status != null && status.equalsIgnoreCase( "pending" )) {
                                    tvstatus.setText( status );
                                    tvstatus.setTextColor( getResources().getColor( R.color.pending_status_color ) );
                                    tvstatus.setCompoundDrawables( null, null, getResources().getDrawable( R.drawable.ic_priority_high_black_24dp ), null );
                                    setUploadedDocumentImage( merchantSnap );
                                } else if (status != null && status.equalsIgnoreCase( "not_uploaded" )) {
                                    tvstatus.setText( "Upload Document" );
                                    tvstatus.setTextColor( getResources().getColor( R.color.pending_status_color ) );
                                    tvstatus.setCompoundDrawables( null, null, null, null );
                                    setUploadedDocumentImage( merchantSnap );
                                }
                                String aadharNumber=(String) merchantSnap.get( "document.aadhar_number" );
                                mAdharNumber.setText( aadharNumber );

                            }
                        }
                    }
                } );

    }

    private void setUploadedDocumentImage(DocumentSnapshot merchantSnap) {
        ImageView frontImage = findViewById( R.id.imageView );
        ImageView backImage = findViewById( R.id.imageView2 );
        String frontImageUrl = merchantSnap.getString( "document.aadhar_photo_front" );
        String backImageUrl = merchantSnap.getString( "document.aadhar_photo_back" );
        if (frontImageUrl != null && !frontImageUrl.equals( "" )) {
            Picasso.with( UploadDocumentActivity.this ).load( frontImageUrl ).into( frontImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Toast.makeText( UploadDocumentActivity.this, "cant upload front image ", Toast.LENGTH_SHORT ).show();

                }
            } );
        }

        if (backImageUrl != null && !backImageUrl.equals( "" )) {
            Picasso.with( UploadDocumentActivity.this ).load( backImageUrl ).into( backImage );
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

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService( Context.INPUT_METHOD_SERVICE );
            inputManager.hideSoftInputFromWindow( view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS );
        }
    }
}
