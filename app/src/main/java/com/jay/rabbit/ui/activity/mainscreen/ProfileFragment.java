package com.jay.rabbit.ui.activity.mainscreen;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.jay.rabbit.R;
import com.jay.rabbit.model.User;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private CircleImageView profileImage;
    private TextView userNameTV;

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private StorageTask uploadTask;

    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;

    private Context context;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = view.findViewById(R.id.profile_image);
        userNameTV = view.findViewById(R.id.user_name);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        loadProfileImage();

        profileImage.setOnClickListener(v -> openImage());

        return view;
    }


    private void openImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }


    private String getFileExtension(Uri uri) {

        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void uploadImage(){

        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("uploading...");
        dialog.show();

        if (imageUri != null){

            StorageReference fileReference = storageReference.child(System.currentTimeMillis() +
            "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task ->
                    fileReference.getDownloadUrl()).addOnCompleteListener((OnCompleteListener<Uri>)
                    task -> {

                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    String uri = null;
                    if (downloadUri != null) {
                        uri = downloadUri.toString();
                    }

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                            .child(firebaseUser.getUid());

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("userImage", uri);
                    databaseReference.updateChildren(hashMap);

                    dialog.dismiss();

                } else {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show();        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){

            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(context, "upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }


    private void loadProfileImage() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    userNameTV.setText(user.getUserName());

                    if (user.getUserImage().equals("default")) {
                        profileImage.setImageResource(R.drawable.logo_rabbit);

                    } else {
                        Picasso.get().load(user.getUserImage()).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


}
