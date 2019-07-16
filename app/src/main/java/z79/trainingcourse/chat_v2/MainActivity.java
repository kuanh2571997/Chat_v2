package z79.trainingcourse.chat_v2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FragmentTransaction fragmentTransaction;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance().getReference();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        LoginFragment loginFragment = new LoginFragment();
        fragmentTransaction.replace(R.id.frame, loginFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseUser = firebaseAuth.getInstance().getCurrentUser();
        if(mFirebaseUser==null){
            Toast.makeText(this, "No User Login", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "User Logined", Toast.LENGTH_SHORT).show();
            mData.child("users").child(mFirebaseUser.getUid()).child("status").setValue("Online");
            String name = mFirebaseUser.getDisplayName();
            String email = mFirebaseUser.getEmail();
            Uri uri = mFirebaseUser.getPhotoUrl();
            boolean emailVerified = mFirebaseUser.isEmailVerified();
            String uid = mFirebaseUser.getUid();

            Log.d("ketqua", "onStart: name: "+name+"\n email: "+email +"\n url: "+String.valueOf(uri) + "\n uid:"+uid+"\nverified: "+String.valueOf(emailVerified));

            Intent intent = new Intent(MainActivity.this, InforActivity.class);
            intent.putExtra("user", mFirebaseUser);
            finish();
            startActivity(intent);
        }
    }
}
