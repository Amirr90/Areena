package com.areena.merchant.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.areena.merchant.MainActivity;
import com.areena.merchant.Model.DemoClassData;
import com.areena.merchant.Model.DemoClassDataModel;
import com.areena.merchant.R;
import com.areena.merchant.RetrofitService;
import com.areena.merchant.Utility.Utils;
import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.areena.merchant.Utility.Utils.DEMO_CLASS_STATUS_ACCEPT;
import static com.areena.merchant.Utility.Utils.DEMO_CLASS_STATUS_PENDING;
import static com.areena.merchant.Utility.Utils.DEMO_CLASS_STATUS_REJECT;
import static com.areena.merchant.Utility.Utils.LEAD_STATUS_AVAILABLE;
import static com.areena.merchant.Utility.Utils.LEAD_STATUS_NEW;
import static com.areena.merchant.Utility.Utils.LEAD_STATUS_SOLD;
import static com.areena.merchant.Utility.Utils.QUERY_HIRED_FOR_DEMO_CLASS;

/**
 * A simple {@link Fragment} subclass.
 */
public class DemoClassfragment extends Fragment {


    private RecyclerRefreshLayout swipe_refresh;
    private RecyclerView recyclerView;
    private List<DemoClassData> data;
    private ProgressBar progressBar;
    private LinearLayout noDataLayout;
    private DemoClassAdapter adapter;
    private TextView mResultCount;


    public DemoClassfragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_demo_classfragment, container, false );
        progressBar = (ProgressBar) view.findViewById( R.id.progressBar3 );
        swipe_refresh = (RecyclerRefreshLayout) view.findViewById( R.id.refresh_layout );
        recyclerView = (RecyclerView) view.findViewById( R.id.recycler_view );
        mResultCount = (TextView) view.findViewById( R.id.result_count );
        noDataLayout = (LinearLayout) view.findViewById( R.id.no_tution_data );
        recyclerView.setLayoutManager( new LinearLayoutManager( getActivity() ) );
        data = new ArrayList<>();
        adapter = new DemoClassAdapter( data, getActivity() );
        recyclerView.setAdapter( adapter );
        loadData( adapter );

        swipe_refresh.setRefreshStyle( RecyclerRefreshLayout.RefreshStyle.NORMAL );
        swipe_refresh.setOnRefreshListener( new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh.setNestedScrollingEnabled( true );
                loadData( adapter );
            }
        } );

        return view;
    }

    private void loadData(final DemoClassAdapter adapter) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl( "https://us-central1-phonenumber-4a8b0.cloudfunctions.net/" )
                    .addConverterFactory( GsonConverterFactory.create() )
                    .build();

            RetrofitService uploadInterFace = retrofit.create( RetrofitService.class );

            String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Call<DemoClassDataModel> call = uploadInterFace.getDemoClassData( uID );
            call.enqueue( new Callback<DemoClassDataModel>() {
                @Override
                public void onResponse(Call<DemoClassDataModel> call, Response<DemoClassDataModel> response) {
                    progressBar.setVisibility( View.GONE );
                    if (!response.isSuccessful()) {
                        Toast.makeText( getActivity(), "failed in reading data,try again", Toast.LENGTH_SHORT ).show();
                        return;
                    }
                    DemoClassDataModel body = response.body();
                    if (body != null) {
                        String merchantType = body.getMerchantType();
                        if (merchantType.equals( new Utils().MERCHANT_TYPE_TUTOR )) {
                            //merchant Is Tutor Type
                            // HomeScreen.
                            if (body.getDemoClassData().isEmpty()) {
                                //No Data in Tution
                                noDataLayout.setVisibility( View.VISIBLE );
                                mResultCount.setVisibility( View.GONE );
                                recyclerView.setVisibility( View.GONE );


                            } else {
                                data.clear();
                                data.addAll( body.getDemoClassData() );
                                noDataLayout.setVisibility( View.GONE );
                                recyclerView.setVisibility( View.VISIBLE );
                                swipe_refresh.setRefreshing( false );
                                mResultCount.setText( data.size() + " Results found" );
                                mResultCount.setVisibility( View.VISIBLE );
                                adapter.notifyDataSetChanged();
                            }

                        } else {
                            //merchant Is Gym Type
                        }
                    }
                }

                @Override
                public void onFailure(Call<DemoClassDataModel> call, Throwable t) {

                }
            } );
        } else {
            if (getActivity() != null) {
                startActivity( new Intent( getActivity(), MainActivity.class ) );
                getActivity().finish();
            }
        }

    }

    private class DemoClassAdapter extends RecyclerView.Adapter<DemoClassAdapter.MyViewHolder> {
        List<DemoClassData> list;
        Context context;

        public DemoClassAdapter(List<DemoClassData> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public DemoClassAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.home_view, parent, false );
            return new MyViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull final DemoClassAdapter.MyViewHolder holder, final int position) {

            try {
                String userName = list.get( position ).getUserName();
                String address = list.get( position ).getUserAddress();

                final String status = list.get( position ).getRequestStatus();

                long timeStamp = list.get( position ).getTimeStamp();

                holder.from.setText( "Request from: " + userName );
                holder.mAddress.setText( "Locality: " + address );
                holder.mTimestamp.setText( getTimesAgo( timeStamp ) );
                holder.mNewLeadStatus.setText( status );

                switch (status) {
                    case DEMO_CLASS_STATUS_ACCEPT:
                        holder.mNewLeadStatus.setTextColor( Color.parseColor( "#ff669900" ) );
                        break;
                    case DEMO_CLASS_STATUS_PENDING:
                        holder.mNewLeadStatus.setTextColor( Color.parseColor( "#ffffbb33" ) );
                        break;
                    case DEMO_CLASS_STATUS_REJECT:
                        holder.mNewLeadStatus.setTextColor( getActivity().getResources().getColor( R.color.red ) );
                        break;
                }

                if (status.equalsIgnoreCase( DEMO_CLASS_STATUS_REJECT )) {
                    holder.layout.setEnabled( false );
                    holder.mApplyBtn.setEnabled( false );
                    holder.mViewBtn.setEnabled( false );
                    holder.mHiredtv.setVisibility( View.VISIBLE );
                    changeTextColor( holder, getActivity().getResources().getColor( R.color.lisght_grey ) );
                } else {
                    holder.layout.setEnabled( true );
                    holder.mApplyBtn.setEnabled( true );
                    holder.mViewBtn.setEnabled( true );
                    holder.mHiredtv.setVisibility( View.GONE );
                    holder.mHiredtv.setText( "Rejected" );
                    changeTextColor( holder, getActivity().getResources().getColor( R.color.default_inactive_color ) );
                }

                holder.layout.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.layout.isEnabled()) {
                            String status = list.get( position ).getRequestStatus();
                            if (!status.equalsIgnoreCase( DEMO_CLASS_STATUS_ACCEPT )) {
                                String id = list.get( position ).getId();
                                showUpdateDemoClassDialog( holder, id );
                            }
                        }
                    }
                } );

                if (status.equalsIgnoreCase( DEMO_CLASS_STATUS_PENDING )) {
                    holder.layout.setEnabled( true );
                } else {
                    holder.layout.setEnabled( false );
                }


            } catch (Exception e) {
                Toast.makeText( context, "error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
            }
        }

        private void showUpdateDemoClassDialog(final MyViewHolder holder, final String id) {


            final CharSequence[] items = {DEMO_CLASS_STATUS_ACCEPT, DEMO_CLASS_STATUS_REJECT};

            AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
            builder.setTitle("Make your selection");
            builder.setItems( items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    String status = items[item].toString();
                    updateDemoClassStatus( status, id, holder );
                    dialog.dismiss();

                }
            } ).setPositiveButton( "DISMISS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            } ).show();
        }

        private void updateDemoClassStatus(String status, String id, final MyViewHolder holder) {
            progressBar.setVisibility( View.VISIBLE );
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            Map<String, Object> map = new HashMap<>();
            map.put( "requestStatus", status );
            firestore.collection( QUERY_HIRED_FOR_DEMO_CLASS )
                    .document( id )
                    .update( map )
                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility( View.GONE );
                            Toast.makeText( context, "updated successfully", Toast.LENGTH_SHORT ).show();
                            holder.layout.setEnabled( false );
                        }
                    } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility( View.GONE );
                    Toast.makeText( context, "try again", Toast.LENGTH_SHORT ).show();
                }
            } );
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

        private void changeTextColor(DemoClassAdapter.MyViewHolder holder, int color) {
            holder.from.setTextColor( color );
            holder.mAddress.setTextColor( color );
            holder.mTutionFor.setTextColor( color );
            holder.mGender.setTextColor( color );
            holder.mSubject.setTextColor( color );
            holder.mParentName.setTextColor( color );
            holder.mTimestamp.setTextColor( color );
            holder.mApplyBtn.setTextColor( color );
            holder.mViewBtn.setTextColor( color );


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView from, mParentName, mTutionFor, mSubject, mGender, mTimestamp, mNewLeadStatus, mAddress, mHiredtv;
            private Button mApplyBtn, mViewBtn;
            RelativeLayout layout;

            public MyViewHolder(@NonNull View itemView) {
                super( itemView );
                from = (TextView) itemView.findViewById( R.id.locality );
                mParentName = (TextView) itemView.findViewById( R.id.parent_name );
                mTutionFor = (TextView) itemView.findViewById( R.id.tution_for );
                mSubject = (TextView) itemView.findViewById( R.id.subject );
                mGender = (TextView) itemView.findViewById( R.id.gender );
                mAddress = (TextView) itemView.findViewById( R.id.tution_fee );
                mHiredtv = (TextView) itemView.findViewById( R.id.hired_tv );
                mTimestamp = (TextView) itemView.findViewById( R.id.leads_timeStamp );
                mNewLeadStatus = (TextView) itemView.findViewById( R.id.new_lead_indicator );
                mViewBtn = (Button) itemView.findViewById( R.id.button3 );
                mApplyBtn = (Button) itemView.findViewById( R.id.button5 );
                layout = (RelativeLayout) itemView.findViewById( R.id.detail_layout1 );

                mTutionFor.setVisibility( View.GONE );
                mGender.setVisibility( View.GONE );


            }
        }
    }
}
