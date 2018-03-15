package com.buttercell.survy.student;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.buttercell.survy.R;
import com.buttercell.survy.common.Common;
import com.buttercell.survy.model.Units;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentUnits extends Fragment {

    private static final String TAG = "StudentUnits";

    @BindView(R.id.txtUnitName)
    TextView txtUnitName;
    @BindView(R.id.unitList)
    RecyclerView unitList;
    @BindView(R.id.btnFinish)
    Button btnFinish;
    Unbinder unbinder;


    DatabaseReference mDatabase;
    FirebaseRecyclerAdapter<Units, UnitsViewHolder> adapter;

    public StudentUnits() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_units, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), StudentHome.class));
                getActivity().finish();
            }
        });

        txtUnitName.setText(Common.courseName);

        unitList.setLayoutManager(new LinearLayoutManager(getContext()));
        unitList.setHasFixedSize(true);

        mDatabase = FirebaseDatabase.getInstance().getReference("Courses").child(Common.courseKey).child("unitsOffered");

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Courses")
                .child(Common.courseKey)
                .child("unitsOffered");

        FirebaseRecyclerOptions<Units> options =
                new FirebaseRecyclerOptions.Builder<Units>()
                        .setQuery(query, Units.class)
                        .build();


        adapter = new FirebaseRecyclerAdapter<Units, UnitsViewHolder>(
                options
        ) {
            @Override
            protected void onBindViewHolder(@NonNull UnitsViewHolder holder, int position, @NonNull Units model) {
                Log.d(TAG, "onBindViewHolder: " + model);
                holder.setUnitName(model.getUnitName());

            }

            @Override
            public UnitsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.unit_layout, parent, false);

                return new UnitsViewHolder(view);
            }
        };
        adapter.notifyDataSetChanged();
        unitList.setAdapter(adapter);

    }

    public static class UnitsViewHolder extends RecyclerView.ViewHolder {


        public UnitsViewHolder(View itemView) {
            super(itemView);

        }

        public void setUnitName(String name) {
            TextView edtName = itemView.findViewById(R.id.edt_name);
            edtName.setText(name);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
