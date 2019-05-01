package com.example.onlineattendanceadmin.NavigationItem;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.onlineattendanceadmin.R;
import com.example.onlineattendanceadmin.RoutineSetupFragment;
import com.example.onlineattendanceadmin.RoutineViewFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class RoutineFragment extends Fragment {


    private Context context;
    private Button view_routine_btn,setup_routine_btn;

    public RoutineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_routine, container, false);

        view_routine_btn = view.findViewById(R.id.view_routine_btn);
        setup_routine_btn = view.findViewById(R.id.setup_routine_btn);

        view_routine_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoutineViewFragment routineViewFragment = new RoutineViewFragment();
                changeFragment(routineViewFragment);
            }
        });

        setup_routine_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoutineSetupFragment routineSetupFragment = new RoutineSetupFragment();
                changeFragment(routineSetupFragment);
            }
        });
        return view;
    }
    public void changeFragment(Fragment fragment){

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_main_layout,fragment);
        fragmentTransaction.commit();

    }

}
