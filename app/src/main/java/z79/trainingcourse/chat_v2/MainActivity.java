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

public class MainActivity extends AppCompatActivity {

    private FragmentTransaction fragmentTransaction;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        LoginFragment loginFragment = new LoginFragment();
        fragmentTransaction.replace(R.id.frame, loginFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser =firebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser==null){
            Toast.makeText(this, "No User Login", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "User Logined", Toast.LENGTH_SHORT).show();
            String name = firebaseUser.getDisplayName();
            String email = firebaseUser.getEmail();
            Uri uri = firebaseUser.getPhotoUrl();
            boolean emailVerified = firebaseUser.isEmailVerified();
            String uid = firebaseUser.getUid();

            Log.d("ketqua", "onStart: name: "+name+"\n email: "+email +"\n url: "+String.valueOf(uri) + "\n uid:"+uid+"\nverified: "+String.valueOf(emailVerified));

            Intent intent = new Intent(MainActivity.this, InforActivity.class);
            intent.putExtra("user", firebaseUser);
            startActivity(intent);

        }
    }

    @Override
    protected void onDestroy() {
        Log.d("ketqua", "onDestroy: activity destroyed");
        super.onDestroy();
    }
}
