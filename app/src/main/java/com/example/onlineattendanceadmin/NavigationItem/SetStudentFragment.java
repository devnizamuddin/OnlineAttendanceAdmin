package com.example.onlineattendanceadmin.NavigationItem;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.onlineattendanceadmin.CheckSystem;
import com.example.onlineattendanceadmin.PoJo.StudentPoJo;
import com.example.onlineattendanceadmin.R;
import com.example.onlineattendanceadmin.ViewStudentFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetStudentFragment extends Fragment {


    private Spinner department_Sp, semester_Sp, section_Sp;
    private EditText student_Name_Et, id_Et;
    private Button set_btn, view_student_btn;
    private Context context;
    private CheckSystem checkSystem;
    private DatabaseReference databaseReference;
    AlertDialog dialog;


    public SetStudentFragment() {
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
        View view = inflater.inflate(R.layout.fragment_set_student, container, false);


        department_Sp = view.findViewById(R.id.department_Sp);
        semester_Sp = view.findViewById(R.id.semester_Sp);
        section_Sp = view.findViewById(R.id.section_Sp);

        id_Et = view.findViewById(R.id.id_Et);
        student_Name_Et = view.findViewById(R.id.student_Name_Et);
        set_btn = view.findViewById(R.id.set_btn);
        view_student_btn = view.findViewById(R.id.view_student_btn);
        dialog = new SpotsDialog.Builder().setContext(context)
                .setCancelable(false).setTheme(R.style.normal).build();

        databaseReference = FirebaseDatabase.getInstance().getReference("Student");

        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.
                createFromResource(context, R.array.department, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> semesterAdapter = ArrayAdapter.
                createFromResource(context, R.array.semester, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.
                createFromResource(context, R.array.section, android.R.layout.simple_spinner_dropdown_item);

        department_Sp.setAdapter(departmentAdapter);
        semester_Sp.setAdapter(semesterAdapter);
        section_Sp.setAdapter(sectionAdapter);

        set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String department = department_Sp.getSelectedItem().toString();
                String semester = semester_Sp.getSelectedItem().toString();
                String section = section_Sp.getSelectedItem().toString();
                String id = id_Et.getText().toString();
                String name = student_Name_Et.getText().toString();

                if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(name)){

                    if (checkSystem.havingInternetConnection()){

                        dialog.show();
                        sendDataToFireBase(department, semester, section, id, name);
                    }

                }
                else {
                    Toast.makeText(context, "All field are required", Toast.LENGTH_SHORT).show();
                }


            }
        });
        view_student_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new ViewStudentFragment());
            }
        });


        return view;
    }

    private void sendDataToFireBase(String department, String semester, String section, String id, String name) {

        databaseReference.child(department).child(semester)
                .child(section)
                .child(id)
                .setValue(new StudentPoJo(id, name)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    Toast.makeText(context, "Student Added", Toast.LENGTH_SHORT).show();
                    student_Name_Et.setText("");
                    id_Et.setText("");
                } else {
                    dialog.dismiss();
                    Toast.makeText(context, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void changeFragment(Fragment fragment){

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_main_layout,fragment).addToBackStack(null);
        fragmentTransaction.commit();

    }
}
