package com.example.onlineattendanceadmin;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.onlineattendanceadmin.Adapter.RoutineAdapter;
import com.example.onlineattendanceadmin.PoJo.ClassPoJo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class RoutineViewFragment extends Fragment {

    private Context context;
    private RecyclerView routineRv;
    private Button find_btn;
    private Spinner day_Sp, semester_Sp, department_Sp, section_Sp;
    private DatabaseReference databaseReference;
    private ArrayList<ClassPoJo> classPoJos;
    private RoutineAdapter routineAdapter;
    private CheckSystem checkSystem;
    AlertDialog dialog;

    public RoutineViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        checkSystem = new CheckSystem(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_routine_view, container, false);

        routineRv = view.findViewById(R.id.routineRv);
        find_btn = view.findViewById(R.id.find_btn);

        day_Sp = view.findViewById(R.id.day_Sp);
        semester_Sp = view.findViewById(R.id.semester_Sp);
        department_Sp = view.findViewById(R.id.department_Sp);
        section_Sp = view.findViewById(R.id.section_Sp);
        dialog = new SpotsDialog.Builder().setContext(context)
                .setCancelable(false).setTheme(R.style.normal).build();


        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.
                createFromResource(context, R.array.department, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.
                createFromResource(context, R.array.semester, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.
                createFromResource(context, R.array.day, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.
                createFromResource(context, R.array.section, android.R.layout.simple_spinner_dropdown_item);


        department_Sp.setAdapter(departmentAdapter);
        semester_Sp.setAdapter(semesterAdapter);
        day_Sp.setAdapter(dayAdapter);
        section_Sp.setAdapter(sectionAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Routine");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        routineRv.setLayoutManager(layoutManager);
        classPoJos = new ArrayList<>();
        routineAdapter = new RoutineAdapter(context, classPoJos);
        routineRv.setAdapter(routineAdapter);

        find_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String day = day_Sp.getSelectedItem().toString();
                String semester = semester_Sp.getSelectedItem().toString();
                String department = department_Sp.getSelectedItem().toString();
                String section = section_Sp.getSelectedItem().toString();

                if (checkSystem.havingInternetConnection()) {
                    dialog.show();
                    getDataFromFireBase(department, semester, section, day);

                }
            }
        });


        return view;
    }

    private void getDataFromFireBase(String department, String semester, String section, String day) {

        databaseReference.child(department).child(semester).child(section).child(day).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    classPoJos.clear();
                    for (DataSnapshot obj : dataSnapshot.getChildren()) {
                        ClassPoJo classPoJo = obj.getValue(ClassPoJo.class);
                        classPoJos.add(classPoJo);
                    }
                    dialog.dismiss();
                    routineAdapter.updateRoutine(classPoJos);
                } else {
                    classPoJos.clear();
                    routineAdapter.updateRoutine(classPoJos);
                    dialog.dismiss();
                    Toast.makeText(context, "No class found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                dialog.dismiss();
                Toast.makeText(context, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
