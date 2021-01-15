package com.areena.merchant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.areena.merchant.Model.CheckSumModel;
import com.areena.merchant.Utility.Utils;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener;
import com.google.firebase.auth.FirebaseAuth;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.areena.merchant.Utility.Utils.PAYMENT_METHOD_PAYTM;
import static com.areena.merchant.Utility.Utils.PAYMENT_METHOD_PHONEPAY;
import static com.areena.merchant.Utility.Utils.PAYMENT_METHOD_WALLET;


public class PaymentPage extends AppCompatActivity {
    RadioGroup radioGroup;
    ProgressBar progressBar;
    String amount, custId;
    TextView mWalletBalance;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_payment_page );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        radioGroup = (RadioGroup) findViewById( R.id.radio_grp );
        progressBar = (ProgressBar) findViewById( R.id.progressBar2 );
        mWalletBalance = (TextView) findViewById( R.id.merchant_wallet );
        utils = new Utils();


        if (getIntent().hasExtra( "type" )) {
            setToolbar( toolbar, "Pay 10 Credits" );
            amount = getIntent().getStringExtra( "amount" );
            custId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            utils.getWalletBalance( mWalletBalance, custId );
        } else {
            finish();
        }

    }

    public void StartTransaction(View view) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton payment_method_button = (RadioButton) findViewById( selectedId );
        if (selectedId == -1) {
            Toast.makeText( PaymentPage.this, "Nothing selected", Toast.LENGTH_SHORT ).show();
        } else {
            //Toast.makeText( PaymentPage.this, payment_method_button.getText(), Toast.LENGTH_SHORT ).show();
            String payment_method = payment_method_button.getText().toString();
            if (payment_method.equalsIgnoreCase( PAYMENT_METHOD_PAYTM )) {
                startPayTmTransaction();
            } else if (payment_method.equals( PAYMENT_METHOD_PHONEPAY )) {
                startPhonePayTransaction();
            } else if (payment_method.equals( PAYMENT_METHOD_WALLET )) {
                startWalletTransaction();
            }
            //doTransaction( payment_method );
        }
    }

    private void startWalletTransaction() {
        progressBar.setVisibility( View.VISIBLE );
        String walletMoney = mWalletBalance.getText().toString();
        if (Long.parseLong( walletMoney ) < Long.parseLong( amount )) {
            utils.showLowWalletDialog( PaymentPage.this );
            return;
        }
        //

    }

    private void startPhonePayTransaction() {

    }

    private void startPayTmTransaction() {
        if (ContextCompat.checkSelfPermission( PaymentPage.this, Manifest.permission.READ_SMS ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( PaymentPage.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101 );
        } else {
            generateCheckSum();
        }
    }

    private void generateCheckSum() {
        progressBar.setVisibility( View.VISIBLE );
        final String orderId = generateString();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( "https://us-central1-phonenumber-4a8b0.cloudfunctions.net/" )
                .addConverterFactory( GsonConverterFactory.create() )
                .build();

        RetrofitService uploadInterFace = retrofit.create( RetrofitService.class );
        Call<CheckSumModel> call = uploadInterFace.getCheckSum( orderId, custId, amount );

        call.enqueue( new Callback<CheckSumModel>() {
            @Override
            public void onResponse(Call<CheckSumModel> call, Response<CheckSumModel> response) {
                progressBar.setVisibility( View.GONE );
                if (response.isSuccessful()) {

                    CheckSumModel sumModel = response.body();
                    try {
                        String checkSum = sumModel.getChecksum();
                        initPayTmTransaction( checkSum, orderId, custId, amount );

                    } catch (Exception e) {
                        Toast.makeText( PaymentPage.this, "no checkSum Found", Toast.LENGTH_SHORT ).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckSumModel> call, Throwable t) {
                progressBar.setVisibility( View.GONE );
                Toast.makeText( PaymentPage.this, "try again", Toast.LENGTH_SHORT ).show();
            }
        } );

    }

    private void initPayTmTransaction(String sum, String id, String custId, final String amount) {
        String call_back_url = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
        PaytmPGService Service = PaytmPGService.getStagingService( "" );
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put( "MID", "foeWYd15235910797914" );
        paramMap.put( "ORDER_ID", id );
        paramMap.put( "CUST_ID", custId );
        paramMap.put( "CHANNEL_ID", "WAP" );
        paramMap.put( "TXN_AMOUNT", amount );
        paramMap.put( "WEBSITE", "WEBSTAGING" );
        paramMap.put( "INDUSTRY_TYPE_ID", "Retail" );
        paramMap.put( "CALLBACK_URL", call_back_url );
        paramMap.put( "CHECKSUMHASH", sum );
         /* paramMap.put( "MOBILE_NO", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() );
        paramMap.put( "EMAIL", "aamirr.1232@gmail.com" );*/
        PaytmOrder Order = new PaytmOrder( paramMap );

        Service.initialize( Order, null );


        Service.startPaymentTransaction( this, true, true, new PaytmPaymentTransactionCallback() {
            //Call Backs
            public void someUIErrorOccurred(String inErrorMessage) {
                //  Display the error message as below
                Toast.makeText( getApplicationContext(), "UI Error " + inErrorMessage, Toast.LENGTH_LONG ).show();
            }

            public void onTransactionResponse(Bundle inResponse) {
                String status = inResponse.getString( "STATUS" );
                if (status.equals( "TXN_SUCCESS" )) {
                    String tittle = "Transaction Successful";
                    String msg = "You have successfully paid ₹" + amount;
                    int icon = R.drawable.check_ok_gif;
                    //showDialog( tittle, msg, icon );
                    //utils.showSuccessDialog(PaymentPage.this);
                }
                Toast.makeText( getApplicationContext(), "Payment Transaction response " + status, Toast.LENGTH_LONG ).show();
            }

            public void networkNotAvailable() {
                // Display the message as below
                Toast.makeText( getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG ).show();
            }

            public void clientAuthenticationFailed(String inErrorMessage) {
                //Display the message as below
                Toast.makeText( getApplicationContext(), "Authentication failed: Server error" + inErrorMessage, Toast.LENGTH_LONG ).show();
            }

            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                //Display the message as below
                Toast.makeText( getApplicationContext(), "Unable to load webpage " + inErrorMessage, Toast.LENGTH_LONG ).show();
            }

            public void onBackPressedCancelTransaction() {
                //Display the message as below
                Toast.makeText( getApplicationContext(), "Transaction cancelled", Toast.LENGTH_LONG ).show();
                String tittle = "Transaction Cancelled";
                String msg = "Transaction of ₹" + amount + " is cancelled";
                int icon = R.drawable.check_ok_gif;
                showDialog( tittle, msg, icon );
            }

            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
            }
        } );
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

    private String generateString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll( "-", "" );
    }

    private void showDialog(String tittle, String msg, int icon) {
        new TTFancyGifDialog.Builder( PaymentPage.this )
                .setTitle( tittle )
                .setMessage( msg )
                .setPositiveBtnText( "View detail" )
                .setPositiveBtnBackground( "#22b573" )
                .setGifResource( icon )      //pass your gif, png or jpg
                .isCancellable( false )
                .OnPositiveClicked( new TTFancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        startActivity( new Intent( PaymentPage.this, HomeScreen.class ) );
                        finish();
                    }
                } )
                .build();
    }


}
