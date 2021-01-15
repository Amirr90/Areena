package com.areena.merchant.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.areena.merchant.MainActivity;
import com.areena.merchant.Model.HomeModel;
import com.areena.merchant.Model.TutionDataModel;
import com.areena.merchant.R;
import com.areena.merchant.RetrofitService;
import com.areena.merchant.TutionDetail;
import com.areena.merchant.Utility.Utils;
import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.areena.merchant.Utility.Utils.LEAD_STATUS_AVAILABLE;
import static com.areena.merchant.Utility.Utils.LEAD_STATUS_NEW;
import static com.areena.merchant.Utility.Utils.LEAD_STATUS_SOLD;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerRefreshLayout swipe_refresh;
    private RecyclerView recyclerView;
    private List<TutionDataModel> data;
    private ProgressBar progressBar;
    private LinearLayout noDataLayout;
    private HomeAdapter adapter;
    private TextView mResultCount;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_home, container, false );

        progressBar = (ProgressBar) view.findViewById( R.id.progressBar3 );
        swipe_refresh = (RecyclerRefreshLayout) view.findViewById( R.id.refresh_layout );
        recyclerView = (RecyclerView) view.findViewById( R.id.recycler_view );
        mResultCount = (TextView) view.findViewById( R.id.result_count );
        noDataLayout = (LinearLayout) view.findViewById( R.id.no_tution_data );
        recyclerView.setLayoutManager( new LinearLayoutManager( getActivity() ) );
        data = new ArrayList<>();
        adapter = new HomeAdapter( data, getActivity() );
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

    private void loadData(final HomeAdapter adapter) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl( "https://us-central1-phonenumber-4a8b0.cloudfunctions.net/" )
                    .addConverterFactory( GsonConverterFactory.create() )
                    .build();

            RetrofitService uploadInterFace = retrofit.create( RetrofitService.class );

            String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Call<HomeModel> call = uploadInterFace.gettutioEnquiry( uID );
            call.enqueue( new Callback<HomeModel>() {
                @Override
                public void onResponse(Call<HomeModel> call, Response<HomeModel> response) {
                    progressBar.setVisibility( View.GONE );
                    if (!response.isSuccessful()) {
                        Toast.makeText( getActivity(), "failed in reading data,try again", Toast.LENGTH_SHORT ).show();
                        return;
                    }
                    HomeModel body = response.body();
                    if (body != null) {
                        String merchantType = body.getMerchantType();
                        if (merchantType.equals( new Utils().MERCHANT_TYPE_TUTOR )) {
                            if (body.getTutionData().isEmpty()) {
                                //No Data in Tuition
                                noDataLayout.setVisibility( View.VISIBLE );
                                mResultCount.setVisibility( View.GONE );
                                recyclerView.setVisibility( View.GONE );


                            } else {
                                data.clear();
                                data.addAll( body.getTutionData() );
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
                public void onFailure(Call<HomeModel> call, Throwable t) {

                }
            } );
        } else {
            if (getActivity() != null) {
                startActivity( new Intent( getActivity(), MainActivity.class ) );
                getActivity().finish();
            }
        }

    }

    private class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
        List<TutionDataModel> list;
        Context context;

        public HomeAdapter(List<TutionDataModel> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public HomeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.home_view, parent, false );
            return new MyViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull HomeAdapter.MyViewHolder holder, final int position) {

            try {
                String sub_locality = list.get( position ).getSubLocality();
                String parentName = list.get( position ).getName();
                String subject = list.get( position ).getSubject();
                String classs = list.get( position ).getClasss();
                String gender = list.get( position ).getGender();
                String fee = list.get( position ).getFee();
                String status = list.get( position ).getStatus();

                long timeStamp = list.get( position ).getTimestamp();

                holder.mLocality.setText( "Locality: " + sub_locality );
                holder.mFee.setText( "Fee: " + fee );
                holder.mParentName.setText( "Parent: " + parentName );
                holder.mTutionFor.setText( "Tution for: " + classs );
                holder.mSubject.setText( "Subject: " + subject );
                holder.mGender.setText( "Gender: " + gender );
                holder.mTimestamp.setText( getTimesAgo( timeStamp ) );
                holder.mNewLeadStatus.setText( status );

                switch (status) {
                    case LEAD_STATUS_AVAILABLE:
                        holder.mNewLeadStatus.setTextColor( Color.parseColor( "#ff669900" ) );
                        break;
                    case LEAD_STATUS_NEW:
                        holder.mNewLeadStatus.setTextColor( Color.parseColor( "#ffffbb33" ) );
                        break;
                    case LEAD_STATUS_SOLD:
                        holder.mNewLeadStatus.setTextColor( Color.parseColor( "#FFC62828" ) );
                        break;
                }

                if (status.equals( LEAD_STATUS_SOLD )) {
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
                    changeTextColor( holder, getActivity().getResources().getColor( R.color.default_inactive_color ) );
                }


                holder.layout.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String tuitionId = list.get( position ).getId();
                        getActivity().startActivity( new Intent( getActivity(), TutionDetail.class ).putExtra( "id", tuitionId ) );

                    }
                } );
            } catch (Exception e) {
                //Toast.makeText( context, "error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
            }
        }

        private void changeTextColor(MyViewHolder holder, int color) {
            holder.mLocality.setTextColor( color );
            holder.mFee.setTextColor( color );
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
            //return 10;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView mLocality, mParentName, mTutionFor, mSubject, mGender, mTimestamp, mNewLeadStatus, mFee, mHiredtv;
            private Button mApplyBtn, mViewBtn;
            RelativeLayout layout;

            public MyViewHolder(@NonNull View itemView) {
                super( itemView );
                mLocality = (TextView) itemView.findViewById( R.id.locality );
                mParentName = (TextView) itemView.findViewById( R.id.parent_name );
                mTutionFor = (TextView) itemView.findViewById( R.id.tution_for );
                mSubject = (TextView) itemView.findViewById( R.id.subject );
                mGender = (TextView) itemView.findViewById( R.id.gender );
                mFee = (TextView) itemView.findViewById( R.id.tution_fee );
                mHiredtv = (TextView) itemView.findViewById( R.id.hired_tv );
                mTimestamp = (TextView) itemView.findViewById( R.id.leads_timeStamp );
                mNewLeadStatus = (TextView) itemView.findViewById( R.id.new_lead_indicator );
                mViewBtn = (Button) itemView.findViewById( R.id.button3 );
                mApplyBtn = (Button) itemView.findViewById( R.id.button5 );
                layout = (RelativeLayout) itemView.findViewById( R.id.detail_layout1 );


            }
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
}
