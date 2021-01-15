package com.areena.merchant.Utility;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;

import com.areena.merchant.MyFirebaseMessagingService;
import com.areena.merchant.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.areena.merchant.NotificationClass.DEMO_CHANNEL_ID;
import static com.areena.merchant.TutionDetail.mAddress;
import static com.areena.merchant.TutionDetail.mCity;
import static com.areena.merchant.TutionDetail.mContactNumber;
import static com.areena.merchant.TutionDetail.mCreditsToPay;
import static com.areena.merchant.TutionDetail.mFee;
import static com.areena.merchant.TutionDetail.mJobId;
import static com.areena.merchant.TutionDetail.mLocality;
import static com.areena.merchant.TutionDetail.mParent_Name;
import static com.areena.merchant.TutionDetail.mSubject;
import static com.areena.merchant.TutionDetail.mTitleHer;
import static com.areena.merchant.TutionDetail.mTuitionLeadBtn;
import static com.areena.merchant.TutionDetail.mtutionFor;
import static com.areena.merchant.TutionDetail.progressBar;

public class Utils {

    public String DOCUMENT_STATUS_PENDING = "pending";
    public String DOCUMENT_STATUS_VERIFIED = "verified";
    public String DOCUMENT_STATUS_REJECCTED = "rejected";
    public String DOCUMENT_STATUS_NOT_UPLOADED = "not_uploaded";
    public String name = "name";
    public String email = "email";
    public String phoneNumber = "phoneNumber";
    public String studentName = "studentName";
    public String classs = "classs";
    public String subject = "subject";
    public String gender = "gender";
    public String address = "address";
    public String monthlyfee = "monthlyfee";
    public String timestamp = "timestamp";
    public String city = "city";
    public String state = "state";
    public String locality = "locality";
    public String subLocality = "subLocality";
    public String about = "about";
    public String merchantType = "merchantType";
    public String MERCHANT_TYPE_TUTOR = "tutor";
    public String MERCHANT_TYPE_GYM = "gym";
    public String status;

    public static final String LEAD_STATUS_NEW = "New";
    public static final String LEAD_STATUS_SOLD = "Sold";
    public static final String LEAD_STATUS_AVAILABLE = "Available";

    public static final String DEMO_CLASS_STATUS_PENDING = "Pending";
    public static final String DEMO_CLASS_STATUS_ACCEPT = "Accept";
    public static final String DEMO_CLASS_STATUS_REJECT = "Reject";

    public static final String PAYMENT_METHOD_PAYTM = "PayTm";
    public static final String PAYMENT_METHOD_PHONEPAY = "PhonePay";
    public static final String PAYMENT_METHOD_WALLET = "Wallet";
    public static final String QUERY_HIRED_FOR_DEMO_CLASS="HiredForDemoClass";

    public static final String QUERY_MERCHANT = "MerchantList";

    public String uid = "uid";
    NotificationManagerCompat managerCompat;
    MyFirebaseMessagingService myFirebaseMessagingService;
    FirebaseFirestore db;

    TextView textView;

    public Utils(MyFirebaseMessagingService myFirebaseMessagingService) {
        this.myFirebaseMessagingService = myFirebaseMessagingService;
        managerCompat = NotificationManagerCompat.from( myFirebaseMessagingService );
    }

    public Utils() {

    }


    public void showNotification(String title, String body, String click_action, String notificationId) {

        Intent intent = new Intent( click_action );
        intent.putExtra( "notification_id", notificationId );
        PendingIntent pendingIntent = PendingIntent.getActivity( myFirebaseMessagingService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        Notification notification = new NotificationCompat.Builder( myFirebaseMessagingService, DEMO_CHANNEL_ID )
                .setSmallIcon( R.drawable.ic_launcher_foreground )
                .setContentTitle( title )
                .setContentText( body )
                .setPriority( NotificationCompat.PRIORITY_HIGH )
                .setCategory( NotificationCompat.CATEGORY_MESSAGE )
                .setContentIntent( pendingIntent )
                .build();

        managerCompat.notify( (int) System.currentTimeMillis(), notification );
    }

    public void getLeadStatus(String leadId) {

        db = FirebaseFirestore.getInstance();
        db.collection( "Tutor Enquiry" )
                .document( leadId )
                .get()
                .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressBar.setVisibility( View.GONE );
                        if (documentSnapshot != null) {
                            String mStatus = documentSnapshot.getString( "status" );
                            setTuitionDetail( documentSnapshot );
                            status = mStatus;
                            if (mStatus.equalsIgnoreCase( LEAD_STATUS_NEW ) || mStatus.equalsIgnoreCase( LEAD_STATUS_AVAILABLE )) {
                                mTuitionLeadBtn.setText( "Buy This Lead" );
                                mTuitionLeadBtn.setEnabled( true );
                                mTuitionLeadBtn.setBackgroundResource( R.drawable.next_btn );
                            } else {
                                mTuitionLeadBtn.setText( "Hired" );
                                mTuitionLeadBtn.setEnabled( false );
                                mTuitionLeadBtn.setBackgroundResource( R.drawable.disable_btn );
                            }
                        } else {
                            status = "failed";
                        }
                    }
                } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility( View.GONE );

            }
        } );
    }

    private void setTuitionDetail(DocumentSnapshot documentSnapshot) {
        try {
            mJobId.setText( documentSnapshot.getId() );
            mParent_Name.setText( documentSnapshot.getString( "name" ) );
            mCity.setText( documentSnapshot.getString( "city" ) );
            mLocality.setText( documentSnapshot.getString( "subLocality" ) );
            mtutionFor.setText( documentSnapshot.getString( "classs" ) );
            mCreditsToPay.setText( "10 Credits" );
            mContactNumber.setText( "xxxxxxxxxx" );
            mAddress.setText( "xxxxxxxxxx" );
            mFee.setText( documentSnapshot.getString( "fee" ) );
            mSubject.setText( documentSnapshot.getString( "subject" ) );
            mTitleHer.setText( "Teacher required for " + documentSnapshot.getString( "subject" ) + " at " + documentSnapshot.getString( "subLocality" ) );
        } catch (Exception e) {
        }
    }

    public void getWalletBalance(final TextView textView, String userId) {
        db = FirebaseFirestore.getInstance();
        db.collection( QUERY_MERCHANT )
                .document( userId )
                .get()
                .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot!=null){
                            textView.setText(String.valueOf( documentSnapshot.getLong( "balance" ) ));
                        }
                    }
                } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                textView.setText("0");
            }
        } );
    }

    public void showLowWalletDialog(Context context) {

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setMessage("Insufficient balance")
                .setPositiveButton( "DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                } )
                .setNegativeButton( "ADD CREDITS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                } ).show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.antic);
        textView.setTypeface(typeface);
    }


    public String getUserId(){
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return null;
    }
}



