package com.example.onlineattendanceadmin.NavigationItem;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlineattendanceadmin.CheckSystem;
import com.example.onlineattendanceadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecurityCodeFragment extends Fragment {


    EditText security_code_et;
    TextView set_security_code_tv, security_code_tv;
    Button set_security_code_Btn;
    DatabaseReference databaseReference;
    Context context;
    AlertDialog dialog;

    public SecurityCodeFragment() {
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
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_security_code, container, false);

        security_code_et = view.findViewById(R.id.security_code_et);
        set_security_code_tv = view.findViewById(R.id.set_security_code_tv);
        set_security_code_Btn = view.findViewById(R.id.set_security_code_Btn);
        security_code_tv = view.findViewById(R.id.security_code_tv);
        dialog = new SpotsDialog.Builder().setContext(context)
                .setCancelable(false).setTheme(R.style.normal).build();

        databaseReference = FirebaseDatabase.getInstance().getReference("Security_Code");


        CheckSystem checkSystem = new CheckSystem(context);
        if (checkSystem.havingInternetConnection()) {
            dialog.show();
            getSecurityCode();
        }
        else {
            Toast.makeText(context, "Internet Connection Needed", Toast.LENGTH_SHORT).show();
        }
        set_security_code_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String securityCode = security_code_et.getText().toString();
                if (!TextUtils.isEmpty(securityCode)){
                    dialog.show();
                    databaseReference.setValue(securityCode).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                security_code_et.setText("");
                                Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                dialog.dismiss();
                            }

                        }
                    });
                }
                else {
                    Toast.makeText(context, "Enter Security Code", Toast.LENGTH_SHORT).show();
                }


            }
        });


        return view;
    }

    public void getSecurityCode() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String securityCode = dataSnapshot.getValue(String.class);
                security_code_tv.setText(securityCode);
                set_security_code_tv.setText("Change Security Code");
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                dialog.dismiss();
                Toast.makeText(context, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
