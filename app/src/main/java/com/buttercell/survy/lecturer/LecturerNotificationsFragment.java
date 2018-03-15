package com.buttercell.survy.lecturer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buttercell.survy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LecturerNotificationsFragment extends Fragment {


    public LecturerNotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lecturer_notifications, container, false);
    }

}
