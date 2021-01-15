package com.areena.merchant;

import android.content.Intent;
import android.os.Bundle;

import com.areena.merchant.Utility.Utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TutionDetail extends AppCompatActivity {
    String id;
    Utils utils;
    public static Button mTuitionLeadBtn;
    public static TextView mJobId,mParent_Name,mCity,mLocality,mtutionFor,mCreditsToPay,mContactNumber,mAddress,mFee,mSubject, mTitleHer;
    public static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tution_detail );
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        mTuitionLeadBtn = (Button) findViewById( R.id.button6 );
        findViewById(  );

        utils = new Utils();
        if (getIntent().hasExtra( "id" )) {
            id = getIntent().getStringExtra( "id" );
            setToolbar( toolbar, "Tution Detail" );

            utils.getLeadStatus( id );

        }

    }

    private void findViewById() {
        mJobId=(TextView)findViewById( R.id.job_id );
        mParent_Name=(TextView)findViewById( R.id.parent_name );
        mCity=(TextView)findViewById( R.id.city );
        mLocality=(TextView)findViewById( R.id.locality );
        mtutionFor=(TextView)findViewById( R.id.tution_for );
        mCreditsToPay=(TextView)findViewById( R.id.credits_to_pay );
        mContactNumber=(TextView)findViewById( R.id.contact_number );
        mAddress=(TextView)findViewById( R.id.address );
        mFee=(TextView)findViewById( R.id.tution_Fee );
        mTitleHer =(TextView)findViewById( R.id.textView5 );
        mSubject=(TextView)findViewById( R.id.subject );
        progressBar=(ProgressBar)findViewById( R.id.progressBar5 ) ;

        mTuitionLeadBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity( new Intent( TutionDetail.this,PaymentPage.class ).putExtra( "type","type" )
                .putExtra( "amount","10" ));

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
}
