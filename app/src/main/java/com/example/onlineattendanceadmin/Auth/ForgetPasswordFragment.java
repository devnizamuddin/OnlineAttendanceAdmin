package com.example.onlineattendanceadmin.Auth;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.example.onlineattendanceadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgetPasswordFragment extends Fragment {

    private Button sendBtn;
    private EditText email_EtF;
    FirebaseAuth firebaseAuth;
    AlertDialog dialog;
    private Context context;

    public ForgetPasswordFragment() {
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
        final View view = inflater.inflate(R.layout.fragment_forget_password, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        sendBtn = view.findViewById(R.id.send_Btn);
        email_EtF = view.findViewById(R.id.email_EtF);

        dialog = new SpotsDialog.Builder().setContext(context)
                .setCancelable(false).setTheme(R.style.normal).build();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String email = email_EtF.getText().toString();
                if (!email.isEmpty()){
                    dialog.show();
                    //email not null
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                email_EtF.setText("");
                                dialog.dismiss();
                                Snackbar.make(view,"A reset password link send in your email",Snackbar.LENGTH_LONG).show();
                            }
                            else {
                                dialog.dismiss();
                                Snackbar.make(v,""+task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else {
                   // email null
                    Snackbar.make(view,"Please Enter your email first",Snackbar.LENGTH_LONG).show();
                }

            }
        });

        return view;
    }

}
