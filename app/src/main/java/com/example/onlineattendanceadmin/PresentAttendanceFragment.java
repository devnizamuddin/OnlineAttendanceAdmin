package com.example.onlineattendanceadmin;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlineattendanceadmin.PoJo.StudentPoJo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import dmax.dialog.SpotsDialog;


public class PresentAttendanceFragment extends Fragment {


    private DatabaseReference databaseReference, studentDatabaseReference;
    private Spinner semester_Sp, department_Sp, section_Sp;
    private Context context;
    private EditText subject_Name_Et, date_Et;
    private TextView total_present_tv, find_present_student_tv, total_student_tv;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private Calendar calendar;
    private Button find_btn, total_attendance_btn;
    private ArrayList<StudentPoJo> presentStudentPoJos, totalStudentPoJos;

    CheckSystem checkSystem;
    AlertDialog dialog;

    public PresentAttendanceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        checkSystem = new CheckSystem(context);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_present_attendance, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("Attendance");
        studentDatabaseReference = FirebaseDatabase.getInstance().getReference("Student");

        totalStudentPoJos = new ArrayList<>();
        presentStudentPoJos = new ArrayList<>();

        dialog = new SpotsDialog.Builder().setContext(context)
                .setCancelable(false).setTheme(R.style.normal).build();


        subject_Name_Et = view.findViewById(R.id.subject_Name_Et);
        date_Et = view.findViewById(R.id.date_Et);
        find_btn = view.findViewById(R.id.find_btn);
        total_attendance_btn = view.findViewById(R.id.total_attendance_btn);
        total_present_tv = view.findViewById(R.id.total_present_tv);
        find_present_student_tv = view.findViewById(R.id.find_present_student_tv);
        total_student_tv = view.findViewById(R.id.total_student_tv);

        semester_Sp = view.findViewById(R.id.semester_Sp);
        section_Sp = view.findViewById(R.id.section_Sp);
        department_Sp = view.findViewById(R.id.department_Sp);
        calendar = Calendar.getInstance();


        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.
                createFromResource(context, R.array.department, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.
                createFromResource(context, R.array.semester, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.
                createFromResource(context, R.array.section, android.R.layout.simple_spinner_dropdown_item);

        department_Sp.setAdapter(departmentAdapter);
        semester_Sp.setAdapter(semesterAdapter);
        section_Sp.setAdapter(sectionAdapter);

        date_Et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, dateSetListener, year, month, dayOfMonth);

                    datePickerDialog.show();
                }
                return true;

            }
        });

        find_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

                String department = department_Sp.getSelectedItem().toString();
                String semester = semester_Sp.getSelectedItem().toString();
                String section = section_Sp.getSelectedItem().toString();

                String subject = subject_Name_Et.getText().toString();
                String date = date_Et.getText().toString();


                //Having internet connection
                if (!TextUtils.isEmpty(subject) && !TextUtils.isEmpty(date)) {
                    //Having all field
                    if (checkSystem.havingInternetConnection()) {
                        findTotalStudent(department, semester, section);
                        findPresentStudent(department, semester, section, subject, date);
                    }
                    else {
                        dialog.dismiss();
                    }
                } else {
                    // Not having all field
                    dialog.dismiss();
                    Toast.makeText(context, "All field required", Toast.LENGTH_SHORT).show();
                }


            }
        });

        find_present_student_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (presentStudentPoJos.size()>0) {
                    ListFragment listFragment = ListFragment.getInstance(presentStudentPoJos);
                    changeFragment(listFragment);
                } else {
                    Toast.makeText(context, "No student found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        total_attendance_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new TotalAttendanceFragment());
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                calendar.set(year, month, dayOfMonth);
                String finalDate = sdf.format(calendar.getTime());
                date_Et.setText(finalDate);
            }
        };
        return view;
    }

    private void findTotalStudent(String department, String semester, String section) {

        studentDatabaseReference.child(department).child(semester).child(section).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    totalStudentPoJos.clear();
                    for (DataSnapshot idShot : dataSnapshot.getChildren()) {
                               /* String id = idShot.getKey();
                                ids.add(id);*/
                        StudentPoJo studentPoJo = idShot.getValue(StudentPoJo.class);
                        totalStudentPoJos.add(studentPoJo);
                    }
                    dialog.dismiss();
                    //find_present_student_tv.setVisibility(TextView.VISIBLE);
                    total_student_tv.setText(String.valueOf(totalStudentPoJos.size()));
                } else {
                    dialog.dismiss();
                    total_student_tv.setText("Not Found");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dialog.dismiss();
                Toast.makeText(context, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void findPresentStudent(String department, String semester, String section, String subject, String date) {

        databaseReference.child(department).child(semester).child(section)
                .child(subject).child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    presentStudentPoJos.clear();
                    for (DataSnapshot idShot : dataSnapshot.getChildren()) {
                               /* String id = idShot.getKey();
                                ids.add(id);*/
                        StudentPoJo studentPoJo = idShot.getValue(StudentPoJo.class);
                        presentStudentPoJos.add(studentPoJo);
                    }
                    dialog.dismiss();
                    total_present_tv.setText(String.valueOf(presentStudentPoJos.size()));
                } else {
                    dialog.dismiss();
                    total_present_tv.setText("Not Found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                dialog.dismiss();
                Toast.makeText(context, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeFragment(Fragment fragment) {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_main_layout, fragment).addToBackStack("");
        fragmentTransaction.commit();

    }


}
