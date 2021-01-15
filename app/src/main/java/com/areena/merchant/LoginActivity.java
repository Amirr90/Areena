package com.areena.merchant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.areena.merchant.Utility.Utils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    FirebaseFirestore db;
    ProgressBar progressBar;
    Utils utils;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );


        utils = new Utils();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar) findViewById( R.id.progressBar );
        if (auth.getCurrentUser() == null) {
            showLoginScreen();
        } else {
            checkUser( auth.getCurrentUser().getUid() );
        }
    }

    private void showLoginScreen() {
        ProgressDialog dialog = new ProgressDialog( this );
        dialog.setTitle( "Loading" );
        dialog.setMessage( "Please wait" );
        dialog.setCancelable( false );
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                // new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build()
                // new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders( providers )
                        .setTosAndPrivacyPolicyUrls(
                                "https://example.com/terms.html",
                                "https://example.com/privacy.html" )
                        //.setLogo(R.drawable.logoo)      // Set logo drawable
                        .setTheme( R.style.AppTheme_NoActionBar )
                        .build(),
                10 );

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == 10) {
            IdpResponse response = IdpResponse.fromResultIntent( data );

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Toast.makeText( this, "sign in successfully", Toast.LENGTH_SHORT ).show();
                    progressBar.setVisibility( View.VISIBLE );
                    checkUser( user.getUid() );


                }

            } else {
                Toast.makeText( this, "Sign in failed", Toast.LENGTH_SHORT ).show();
                showLoginScreen();
            }
        }
    }

    private void checkUser(final String uid) {
        db.collection( "MerchantList" )
                .document( uid )
                .get()
                .addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().exists()) {
                                //UserFound
                                DocumentSnapshot merchantsnap=task.getResult();
                                boolean merchantActiveStatus=merchantsnap.getBoolean( "isActive" );
                                if (merchantActiveStatus){
                                    String name=merchantsnap.getString( "name" );
                                    String address=merchantsnap.getString( "address" );
                                    if (name!=null && address!=null && !name.equals( "default" ) && !address.equalsIgnoreCase( "default" )){
                                        checkDocumentStatus( uid );
                                    }
                                    else {
                                        updateProfileActivity(uid);
                                    }
                                }
                                else {
                                    showBlockedUserActivity(uid);
                                }


                            } else {
                                createNewMerchant( uid );
                            }
                        }
                    }
                } );
    }

    private void updateProfileActivity(String uid) {
        startActivity( new Intent( LoginActivity.this,UpdateProfileActivity.class ).putExtra( "id",uid ) );
        finish();
    }

    private void showBlockedUserActivity(String uid) {
        startActivity( new Intent( LoginActivity.this,BlockedMerchantActivity.class ).putExtra( "id",uid ) );
        finish();
    }


    private void createNewMerchant(final String uid) {
        Map<String, Object> merchantDocument = new HashMap<>();
        merchantDocument.put( "profile", "" );
        merchantDocument.put( "aadhar_photo_front", "" );
        merchantDocument.put( "aadhar_photo_back", "" );
        merchantDocument.put( "aadhar_number", "" );

        Map<String, Object> merchantMap = new HashMap<>();
        merchantMap.put( "name", "default" );
        merchantMap.put( "uid", uid );
        merchantMap.put( "icon", "default" );
        merchantMap.put( "isActive", true );
        merchantMap.put( "isVerified", false );
        merchantMap.put( "about", "default" );
        merchantMap.put( "address", "" );
        merchantMap.put( "city", "default" );
        merchantMap.put( "locality", "default" );
        merchantMap.put( "fee", 0 );
        merchantMap.put( "monthlyfee", 0 );
        merchantMap.put( "state", "default" );
        merchantMap.put( "rating", 0 );
        merchantMap.put( "pervisitFee", 0 );
        merchantMap.put( "balance", 0 );
        merchantMap.put( "token_id", FirebaseInstanceId.getInstance().getToken(  ) );
        merchantMap.put( "document", merchantDocument );
        merchantMap.put( "documentVerifiedStatus", utils.DOCUMENT_STATUS_NOT_UPLOADED );
        merchantMap.put( "phoneNumber", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() );

        db.collection( "MerchantList" ).document( uid )
                .set( merchantMap )
                .addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility( View.GONE );
                        if (task.isSuccessful()) {
                            checkDocumentStatus( uid );

                        } else {
                            Toast.makeText( LoginActivity.this, "could't create user, try again", Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );


    }

    private void sendTaskToUploadDocumentActivity(String uid) {
        startActivity( new Intent( LoginActivity.this,UploadDocumentActivity.class ).putExtra( "id",uid ) );
        finish();
    }

    private void checkDocumentStatus(final String uid) {

        db.collection( "MerchantList" )
                .document( uid )
                .get()
                .addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && task.getResult().exists()) {
                                DocumentSnapshot merchantSnap = task.getResult();
                                String documentVerifyStatus = merchantSnap.getString( "documentVerifiedStatus" );
                                if (documentVerifyStatus != null) {
                                    if (documentVerifyStatus.equals( "verified" )){
                                        //Toast.makeText( LoginActivity.this, "verified", Toast.LENGTH_SHORT ).show();
                                        sendTaskToHomeScreen();
                                    }
                                    else {
                                        Toast.makeText( LoginActivity.this, documentVerifyStatus, Toast.LENGTH_SHORT ).show();
                                        sendTaskToUploadDocumentActivity( uid );
                                    }
                                } else {
                                    Toast.makeText( LoginActivity.this, "Document Status is null", Toast.LENGTH_SHORT ).show();
                                }
                            }

                        } else {
                            Toast.makeText( LoginActivity.this, "could't verify document Status", Toast.LENGTH_SHORT ).show();
                        }
                    }
                } );

    }

    private void sendTaskToHomeScreen() {
        startActivity( new Intent( LoginActivity.this, HomeScreen.class ) );
        finish();
    }

}
