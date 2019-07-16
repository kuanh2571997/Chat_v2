package z79.trainingcourse.chat_v2;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import z79.trainingcourse.chat_v2.Model.User;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private final int CAMERA_CODE_IMG = 1, GALLERY_CODE_IMG = 2;
    private static final String TAG = "ketqua";

    //View
    private EditText edtEmail, edtPassword, edtConfirmPassword, edtName;
    private Button  btnRegister, btnGallery;
    private FirebaseAuth firebaseAuth;
    private CircleImageView imgAvatar;
    private View view;
    private TextView mTxtRegister;

    //Firebase
    private DatabaseReference mdata;
    private StorageReference reference;
    private FirebaseStorage firebaseStorage;
    private FirebaseUser firebaseUser;

    //Create User
    private User user;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register, container, false);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        mdata = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        reference = firebaseStorage.getReference();
        Init();

        mTxtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_CODE_IMG);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, GALLERY_CODE_IMG);
            }
        });

        return view;
    }

    private void Init() {
        user = new User();
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPassword = view.findViewById(R.id.edtPass);
        btnRegister = view.findViewById(R.id.btnRegister);
        edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        btnGallery = view.findViewById(R.id.btnGallery);
        edtName = view.findViewById(R.id.edtName);
        mTxtRegister = view.findViewById(R.id.txtRegister);
    }

    private void Register() {
        final String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();
        final String name = edtName.getText().toString();

        if (email.equals("") || password.equals("") || confirmPassword.equals("") || name.equals("")) {
            Toast.makeText(getActivity(), "Please Write Full Your Infor", Toast.LENGTH_SHORT).show();
        } else if (!confirmPassword.equals(password)) {
            Toast.makeText(getActivity(), "Pass word is not equals", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                AuthResult authResult = task.getResult();
                                firebaseUser = authResult.getUser();
                                String uid = firebaseUser.getUid();
                                Log.d(TAG, "onComplete: "+uid);
                                UploadImage(uid);
                                user.setName(name);
                                user.setEmail(email);
                                user.setId(uid);
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();

                                firebaseUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: update successful");
                                    }
                                });
                                Toast.makeText(getActivity(), "Register Successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "onComplete: " + task.toString());
                                Toast.makeText(getActivity(), "Register fall!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_CODE_IMG && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgAvatar.setImageBitmap(bitmap);
        } else if (requestCode == GALLERY_CODE_IMG && resultCode == RESULT_OK && data != null) {
            Log.d(TAG, "onActivityResult: image from gallery");
            Uri uri = data.getData();
            imgAvatar.setImageURI(uri);
        } else {
            Toast.makeText(getActivity(), "No Image", Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void UploadImage(String imgName) {
        final StorageReference storageReference = reference.child(imgName+".png");
        imgAvatar.setDrawingCacheEnabled(true);
        imgAvatar.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        final byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Upload Fall!!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "Upload Successful", Toast.LENGTH_SHORT).show();

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: "+String.valueOf(uri));
                        user.setUrl(String.valueOf(uri));
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build();

                        firebaseUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: update successful");
                                user.setStatus("Online");
                                saveUser(user);
                                Intent intent = new Intent(getActivity(), InforActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        });
    }


    private void saveUser(User user){
        mdata.child("users").child(user.getId()).setValue(user);
    }

}
