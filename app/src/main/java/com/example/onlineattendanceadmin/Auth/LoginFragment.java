package com.example.onlineattendanceadmin.Auth;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlineattendanceadmin.MainActivity;
import com.example.onlineattendanceadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    EditText email_et,password_et;
    Button login_btn,sign_up_btn;
    FirebaseAuth firebaseAuth;
    TextView forget_password_tv;
    AlertDialog dialog;
    private Context context;
    public LoginFragment() {
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        email_et = view.findViewById(R.id.email_et);
        password_et = view.findViewById(R.id.password_et);
        login_btn = view.findViewById(R.id.login_btn);
        sign_up_btn = view.findViewById(R.id.sign_up_btn);
        forget_password_tv = view.findViewById(R.id.forget_password_tv);
        dialog = new SpotsDialog.Builder().setContext(context)
                .setCancelable(false).setTheme(R.style.login).build();


        firebaseAuth = FirebaseAuth.getInstance();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                String email = email_et.getText().toString();
                String password = password_et.getText().toString();
                loginUser(email,password);
            }
        });
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(new SignUpFragment());
            }
        });

        forget_password_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeFragment(new ForgetPasswordFragment());
            }
        });


        return view;
    }
    private void loginUser(String email, String password) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        dialog.dismiss();
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context, MainActivity.class));
                        getActivity().finish();

                    } else {
                        dialog.dismiss();
                        Toast.makeText(context, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            dialog.dismiss();
            Toast.makeText(context, "Email And Password Required", Toast.LENGTH_SHORT).show();
        }

    }
    public void changeFragment(Fragment fragment){

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.login_container,fragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

}
