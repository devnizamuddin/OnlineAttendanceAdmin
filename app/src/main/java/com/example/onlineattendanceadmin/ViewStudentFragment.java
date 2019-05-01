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

import com.example.onlineattendanceadmin.Adapter.StudentAdapter;
import com.example.onlineattendanceadmin.PoJo.StudentPoJo;
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
public class ViewStudentFragment extends Fragment {

    private Spinner department_Sp, semester_Sp, section_Sp;
    private CheckSystem checkSystem;
    private Context context;
    private Button view_btn;
    private DatabaseReference databaseReference;
    private ArrayList<StudentPoJo> studentPoJos;
    private StudentAdapter studentAdapter;
    private RecyclerView studentRv;
    AlertDialog dialog;


    public ViewStudentFragment() {
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
        View view = inflater.inflate(R.layout.fragment_view_student, container, false);

        department_Sp = view.findViewById(R.id.department_Sp);
        semester_Sp = view.findViewById(R.id.semester_Sp);
        section_Sp = view.findViewById(R.id.section_Sp);
        view_btn = view.findViewById(R.id.view_btn);
        studentRv = view.findViewById(R.id.studentRv);

        databaseReference = FirebaseDatabase.getInstance().getReference("Student");
        studentPoJos = new ArrayList<>();
        dialog = new SpotsDialog.Builder().setContext(context)
                .setCancelable(false).setTheme(R.style.normal).build();

        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.
                createFromResource(context, R.array.department, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.
                createFromResource(context, R.array.semester, android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.
                createFromResource(context, R.array.section, android.R.layout.simple_spinner_dropdown_item);

        department_Sp.setAdapter(departmentAdapter);
        semester_Sp.setAdapter(semesterAdapter);
        section_Sp.setAdapter(sectionAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        studentRv.setLayoutManager(layoutManager);
        studentAdapter = new StudentAdapter(context, studentPoJos);
        studentRv.setAdapter(studentAdapter);

        view_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSystem.havingInternetConnection()) {
                    dialog.show();
                    String department = department_Sp.getSelectedItem().toString();
                    String semester = semester_Sp.getSelectedItem().toString();
                    String section = section_Sp.getSelectedItem().toString();

                    databaseReference.child(department).child(semester).child(section).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                studentPoJos.clear();
                                for (DataSnapshot obj : dataSnapshot.getChildren()) {
                                    StudentPoJo studentPoJo = obj.getValue(StudentPoJo.class);
                                    studentPoJos.add(studentPoJo);
                                }
                                studentAdapter.updateStudent(studentPoJos);
                                dialog.dismiss();

                            } else {
                                studentPoJos.clear();
                                studentAdapter.updateStudent(studentPoJos);
                                dialog.dismiss();

                                Toast.makeText(context, "No student Found", Toast.LENGTH_SHORT).show();
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
        });


        return view;
    }

}
