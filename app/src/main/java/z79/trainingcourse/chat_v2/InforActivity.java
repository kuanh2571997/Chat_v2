package z79.trainingcourse.chat_v2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class InforActivity extends AppCompatActivity {
    private static final String TAG = "InforActivity";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private CircleImageView imgAvatar;
    private TextView txtName, txtEmail, txtUID;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        InitActivity();
        SetInfor(firebaseUser);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(InforActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void InitActivity(){
        imgAvatar = findViewById(R.id.imgAvatar);
        txtEmail = findViewById(R.id.txtEmail);
        txtName = findViewById(R.id.txtName);
        txtUID = findViewById(R.id.txtUid);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void SetInfor(FirebaseUser user){
        txtName.append(user.getDisplayName());
        txtEmail.append(user.getEmail());
        txtUID.append(user.getUid());
        Glide.with(this).load(user.getPhotoUrl()).into(imgAvatar);
        Log.d(TAG, "SetInfor: "+user.getPhotoUrl());

    }
}
