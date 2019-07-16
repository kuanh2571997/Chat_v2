package z79.trainingcourse.chat_v2;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnRegister;
    private FirebaseAuth firebaseAuth;
    private FragmentTransaction fragmentTransaction;
    private View view;
    private DatabaseReference mDatabaseReference;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login, container, false);
        Init();

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment registerFragment = new RegisterFragment();
                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, registerFragment);
                fragmentTransaction.commit();
            }
        });
        return view;
    }


    private void Init(){
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPassword = view.findViewById(R.id.edtPass);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnRegister = view.findViewById(R.id.btnRegister);
    }

    private void Login(){
        String email, password;



        email = edtEmail.getText().toString();
        password = edtPassword.getText().toString();

        if(email.equals("") || password.equals("")){
            Toast.makeText(getActivity(), "Vui lòng nhập đẩy đủ thông tin", Toast.LENGTH_SHORT).show();
        }

        else{
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(), "Login Successfull", Toast.LENGTH_SHORT).show();
                        FirebaseUser currentUser = task.getResult().getUser();
                        mDatabaseReference.child("users").child(currentUser.getUid()).child("status").setValue("Online");
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), InforActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getActivity(), "Login fall!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }




}
