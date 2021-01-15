package com.areena.merchant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class BlockedMerchantActivity extends AppCompatActivity {
    String ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_blocked_merchant );

        if (getIntent().hasExtra( "id" )) {
            ID = getIntent().getStringExtra( "id" );

        } else {
            finish();
        }
    }
}
