package com.buttercell.survy.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.buttercell.survy.R;
import com.buttercell.survy.ViewSurvey;
import com.buttercell.survy.common.Common;
import com.buttercell.survy.model.Survey;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amush on 10-Mar-18.
 */

public class NewSurveyAdapter extends RecyclerView.Adapter<SurveyViewHolder> {
    private static final String TAG = "SurveyAdapter";
    private List<Survey> listData=new ArrayList<>();
    private Context context;

    public NewSurveyAdapter(List<Survey> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @Override
    public SurveyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        final View itemView = inflater.inflate(R.layout.survey_layout, parent, false);

        return new SurveyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SurveyViewHolder holder, final int position) {

        holder.txtSurveyName.setText(listData.get(position).getName());
        Glide.with(context).load(listData.get(position).getImage()).into(holder.surveyImage);

        Log.d(TAG, "onBindViewHolder: "+listData.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent=new Intent(context,ViewSurvey.class);
                intent.putExtra("survey_name", listData.get(position).getName());
                Common.survey_name = listData.get(position).getName();


//                Set Chama Key
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Surveys");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            if (postSnapshot.child("name").getValue().equals(Common.survey_name)) {
                                String key = postSnapshot.getKey();


                                Common.surveyKey = key;
                                intent.putExtra("key",key);
                                intent.putExtra("new","yes");
                                context.startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}

