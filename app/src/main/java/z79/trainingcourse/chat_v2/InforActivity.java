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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class InforActivity extends AppCompatActivity {
    private static final String TAG = "findconnected";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private CircleImageView imgAvatar;
    private TextView txtName, txtLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infor);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        InitActivity();
        SetInfor(firebaseUser);

        txtLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(InforActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.d(TAG, "onDataChange: connected");
                } else {
                    Log.d(TAG, "onDataChange: not connected");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
        

    }

    private void InitActivity(){
        imgAvatar = findViewById(R.id.imgAvatar);
        txtName = findViewById(R.id.txtName);
        txtLogOut = findViewById(R.id.txtLogOut);
    }

    private void SetInfor(FirebaseUser user){
        txtName.setText(user.getDisplayName());
        Glide.with(this).load(user.getPhotoUrl()).into(imgAvatar);
        Log.d(TAG, "SetInfor: "+user.getPhotoUrl());
    }

}
