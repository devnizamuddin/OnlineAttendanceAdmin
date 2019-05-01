package com.example.onlineattendanceadmin;


import android.app.AlertDialog;
import android.app.TimePickerDialog;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.onlineattendanceadmin.PoJo.ClassPoJo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import dmax.dialog.SpotsDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class RoutineSetupFragment extends Fragment {

    private Spinner department_Sp, semester_Sp, day_Sp, section_Sp;
    private EditText starting_Time_Et, end_Time_Et, subject_Name_Et, teacher_name;
    private Context context;
    private Button submit_Btn, view_routine_Btn;
    private int hour, minute;
    private Calendar calendar;
    private String startOrEnd;
    private DatabaseReference databaseReference;
    AlertDialog dialog;
    public RoutineSetupFragment() {
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

        View view = inflater.inflate(R.layout.fragment_routine_setup, container, false);

        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        department_Sp = view.findViewById(R.id.department_Sp);
        semester_Sp = view.findViewById(R.id.semester_Sp);
        day_Sp = view.findViewById(R.id.day_Sp);
        starting_Time_Et = view.findViewById(R.id.starting_Time_Et);
        end_Time_Et = view.findViewById(R.id.end_Time_Et);
        subject_Name_Et = view.findViewById(R.id.subject_Name_Et);
        submit_Btn = view.findViewById(R.id.submit_Btn);
        teacher_name = view.findViewById(R.id.teacher_Name_Et);
        section_Sp = view.findViewById(R.id.section_Sp);
        view_routine_Btn = view.findViewById(R.id.view_routine_Btn);
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

        databaseReference.child("CSE").child("One").child("Sat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot classDetail : dataSnapshot.getChildren()) {
                    ClassPoJo classPoJo = classDetail.getValue(ClassPoJo.class);
                    if (classPoJo != null) {
                        Toast.makeText(context, "" + classPoJo.getStartingTime(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        starting_Time_Et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    startOrEnd = "start";
                    selectTime();
                }
                return true;
            }
        });
        end_Time_Et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    startOrEnd = "end";
                    selectTime();
                }
                return true;
            }
        });

        view_routine_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new RoutineViewFragment());
            }
        });

        submit_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String department = department_Sp.getSelectedItem().toString();
                String semester = semester_Sp.getSelectedItem().toString();
                String section = section_Sp.getSelectedItem().toString();
                String day = day_Sp.getSelectedItem().toString();
                String startingTime = starting_Time_Et.getText().toString();
                String endTime = end_Time_Et.getText().toString();
                String subject = subject_Name_Et.getText().toString();
                String teacherName = teacher_name.getText().toString();
                if (!TextUtils.isEmpty(startingTime) && !TextUtils.isEmpty(endTime)
                        && !TextUtils.isEmpty(subject) && !TextUtils.isEmpty(teacherName)) {
                    //all field having data
                    dialog.show();
                    sendDataToFireBase(department,semester,section,day,subject,teacherName,startingTime,endTime);
                } else {
                    Toast.makeText(context, "All field are required", Toast.LENGTH_SHORT).show();
                }


            }
        });

        return view;
    }

    public void selectTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context, timeListener, hour, minute, true
        );
        timePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
            calendar.set(0, 0, 0, hourOfDay, minute);
            String finalTime = sdf.format(calendar.getTime());
            if (startOrEnd.equals("start")) {
                starting_Time_Et.setText(finalTime);
            } else if (startOrEnd.equals("end")) {
                end_Time_Et.setText(finalTime);

            }
        }
    };

    public void changeFragment(Fragment fragment) {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_main_layout, fragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    private void sendDataToFireBase(String department, String semester, String section, String day
            , String subject, String teacherName, String startingTime, String endTime) {
        String id = databaseReference.push().getKey();
        ClassPoJo classPoJo = new ClassPoJo(id, subject, teacherName, startingTime, endTime);

        databaseReference.child(department).child(semester).child(section).child(day).child(id).setValue(classPoJo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    subject_Name_Et.setText("");
                    teacher_name.setText("");
                    starting_Time_Et.setText("");
                    end_Time_Et.setText("");
                    Toast.makeText(context, "Subject Added", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    Toast.makeText(context, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
