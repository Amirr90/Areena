package com.areena.merchant;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.areena.merchant.Model.MerchantTutorModel;
import com.areena.merchant.Utility.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {

    String ID;
    private EditText mName, mEmail, mPhoneNumber, mStudentName, mClass, mSubject, mGender, mAddress, mMonthlyFee;
    private EditText mEnquiryCity, mEnquiryState, mLocality, mSubLocality;
    private Button mSubmitEnquiryBtn;
    ArrayList mSelectedItems, mSelectedItemsClass;
    ProgressBar progressBar;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_update_profile );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );


        utils=new Utils();
        findViewById();
        if (getIntent().hasExtra( "id" )) {
            ID = getIntent().getStringExtra( "id" );
            //setDocumentDetail( ID );
            setToolbar( toolbar, "Update Information" );

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                mPhoneNumber.setText( FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() );
            }
        }

    }

    private void findViewById() {
        mName = (EditText) findViewById( R.id.editText1 );
        mEmail = (EditText) findViewById( R.id.editTextEmail );
        mPhoneNumber = (EditText) findViewById( R.id.editTextNumber );
        mStudentName = (EditText) findViewById( R.id.editTextStudentName );
        mClass = (EditText) findViewById( R.id.editTextClass );
        mSubject = (EditText) findViewById( R.id.editTextSubject );
        mGender = (EditText) findViewById( R.id.editTextGender );
        mAddress = (EditText) findViewById( R.id.editTextAddress );
        mSubmitEnquiryBtn = (Button) findViewById( R.id.button4 );
        mMonthlyFee = (EditText) findViewById( R.id.editTextFee );
        progressBar = (ProgressBar) findViewById( R.id.progressBar4 );
        mEnquiryCity = (EditText) findViewById( R.id.editTextCity );
        mEnquiryState = (EditText) findViewById( R.id.editTextState );
        mLocality = (EditText) findViewById( R.id.editTextLocality );
        mSubLocality = (EditText) findViewById( R.id.editTextSubLocality );
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

    public void selectMonthlyFee(View view) {
        final CharSequence[] items = {"₹2,000(for 60 minutes)", "₹2,500(for 60 minutes)", "₹3,000(for 60 minutes)", "₹3,500(for 60 minutes)",
                "₹4,000(for 60 minutes)", "₹4,500(for 60 minutes)", "₹5,000(for 60 minutes)"};

        AlertDialog.Builder builder = new AlertDialog.Builder( UpdateProfileActivity.this );
        builder.setTitle( "Make your selection" );
        builder.setItems( items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                mMonthlyFee.setText( items[item] );
                dialog.dismiss();

            }
        } ).show();
    }

    public void selectSubject(View view) {

        // where we will store or remove selected items
        mSelectedItems = new ArrayList<Integer>();
        final String[] choiceArray = getResources().getStringArray( R.array.choices );

        final AlertDialog.Builder builder = new AlertDialog.Builder( UpdateProfileActivity.this );

        // set the dialog title
        builder.setTitle( "Choose One or More" )
                .setMultiChoiceItems( R.array.choices, null, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        if (isChecked) {
                            // if the user checked the item, add it to the selected items
                            mSelectedItems.add( which );
                        } else if (mSelectedItems.contains( which )) {
                            // else if the item is already in the array, remove it
                            mSelectedItems.remove( Integer.valueOf( which ) );
                        }

                    }

                } )

                // Set the action buttons
                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        StringBuilder selectedIndex = new StringBuilder();
                        for (int a = 0; a < mSelectedItems.size(); a++) {
                            if (mSelectedItems.size() == 1) {
                                selectedIndex.append( choiceArray[(Integer) mSelectedItems.get( a )] );
                            } else {
                                selectedIndex.append( choiceArray[(Integer) mSelectedItems.get( a )] + "," );
                            }
                        }
                        mSubject.setText( selectedIndex.toString() );

                    }
                } )

                .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // removes the AlertDialog in the screen
                    }
                } )

                .show();

    }

    public void selectClass(View view) {

        // where we will store or remove selected items
        mSelectedItemsClass = new ArrayList<Integer>();
        final String[] choiceArray = getResources().getStringArray( R.array.choices_class );

        final AlertDialog.Builder builder = new AlertDialog.Builder( UpdateProfileActivity.this );

        // set the dialog title
        builder.setTitle( "Choose One or More" )
                .setMultiChoiceItems( R.array.choices_class, null, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        if (isChecked) {
                            // if the user checked the item, add it to the selected items
                            mSelectedItemsClass.add( which );
                        } else if (mSelectedItemsClass.contains( which )) {
                            // else if the item is already in the array, remove it
                            mSelectedItemsClass.remove( Integer.valueOf( which ) );
                        }

                    }

                } )

                // Set the action buttons
                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        StringBuilder selectedIndex = new StringBuilder();
                        for (int a = 0; a < mSelectedItemsClass.size(); a++) {
                            if (mSelectedItemsClass.size() == 1) {
                                selectedIndex.append( choiceArray[(Integer) mSelectedItemsClass.get( a )] );
                            } else {
                                selectedIndex.append( choiceArray[(Integer) mSelectedItemsClass.get( a )] + "," );
                            }
                        }
                        mClass.setText( selectedIndex.toString() );

                    }
                } )

                .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // removes the AlertDialog in the screen
                    }
                } )

                .show();

    }

    public void selectGender(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder( UpdateProfileActivity.this );

        final String[] gender = getResources().getStringArray( R.array.choices_gender );
        // Set the dialog title
        builder.setTitle( "Choose One" )

                .setSingleChoiceItems( R.array.choices_gender, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //showToast("Some actions maybe? Selected index: " + arg1);
                    }

                } )

                // Set the action buttons
                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // user clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog

                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        mGender.setText( gender[selectedPosition] );

                    }
                } )

                .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // removes the dialog from the screen

                    }
                } )

                .show();

    }

    public void submitEnquiry(final View view) {

        //Hiding KeyBoard
        hideKeyboard();


        //Checking null or Empty Value
        if (!mName.getText().toString().isEmpty() &&
                !mEmail.getText().toString().isEmpty() &&
                !mPhoneNumber.getText().toString().isEmpty() &&
                !mStudentName.getText().toString().isEmpty() &&
                !mClass.getText().toString().isEmpty() &&
                !mSubject.getText().toString().isEmpty() &&
                !mAddress.getText().toString().isEmpty() &&
                !mMonthlyFee.getText().toString().isEmpty() &&
                !mGender.getText().toString().isEmpty() &&
                FirebaseAuth.getInstance().getCurrentUser() != null &&
                !mEnquiryCity.getText().toString().isEmpty() &&
                !mEnquiryState.getText().toString().isEmpty() &&
                !mLocality.getText().toString().isEmpty() &&
                !mSubLocality.getText().toString().isEmpty()) {


            //Disable Button
            mSubmitEnquiryBtn.setEnabled( false );

            //Show Progressbar
            progressBar.setVisibility( View.VISIBLE );

            //setValue To Pojo

            Map<String, Object> profileMap = new HashMap<>();
            profileMap.put( utils.name, mName.getText().toString() );
            profileMap.put( utils.email, mEmail.getText().toString() );
            profileMap.put( utils.phoneNumber, mName.getText().toString() );
            profileMap.put( utils.about, mStudentName.getText().toString() );
            profileMap.put( utils.classs, mClass.getText().toString() );
            profileMap.put( utils.subject, mSubject.getText().toString() );
            profileMap.put( utils.gender, mGender.getText().toString() );
            profileMap.put( utils.address, mAddress.getText().toString() );
            profileMap.put( utils.monthlyfee, mMonthlyFee.getText().toString() );
            profileMap.put( utils.timestamp, System.currentTimeMillis() );
            profileMap.put( utils.city, mEnquiryCity.getText().toString() );
            profileMap.put( utils.state, mEnquiryState.getText().toString() );
            profileMap.put( utils.locality, mLocality.getText().toString() );
            profileMap.put( utils.subLocality, mSubLocality.getText().toString() );
            profileMap.put( utils.uid, ID );
            profileMap.put( utils.merchantType, utils.MERCHANT_TYPE_TUTOR );




            //trigger FirestoreDatabase
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection( "MerchantList" )
                    .document( ID )
                    .update( profileMap )
                    .addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility( View.VISIBLE );
                            if (task.isSuccessful()) {

                                Toast.makeText( UpdateProfileActivity.this, "Enquiry Submitted", Toast.LENGTH_SHORT ).show();
                                startActivity( new Intent( UpdateProfileActivity.this, UploadDocumentActivity.class ).putExtra( "id", ID ) );
                                finish();
                            } else {
                                Toast.makeText( UpdateProfileActivity.this, "Could't Submitted Profile information ", Toast.LENGTH_SHORT ).show();
                                mSubmitEnquiryBtn.setEnabled( true );
                            }
                        }
                    } );
        } else {
            Snackbar.make( view, "please fill all the fields", Snackbar.LENGTH_SHORT ).show();
        }
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
