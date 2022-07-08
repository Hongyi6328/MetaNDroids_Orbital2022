package com.example.parti.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.parti.Parti;
import com.example.parti.databinding.FragmentMyProfileBinding;
import com.example.parti.ui.login.LoginActivity;
import com.example.parti.wrappers.Major;
import com.example.parti.wrappers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class MyProfileFragment extends Fragment {

    FragmentMyProfileBinding fragmentMyProfileBinding;
    HashMap<String, Integer> majorMap = new HashMap<>();
    boolean dataRead = false;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;

    public static final int EARLIEST_YEAR_OF_MATRIC = Parti.EARLIEST_YEAR_OF_MATRIC;
    Major[] majors = Parti.MAJORS;

    public MyProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        int mapSize = majors.length;
        if (majorMap.isEmpty()) for (int i = 0; i < mapSize; i++) majorMap.put(majors[i].toString(), i);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentMyProfileBinding = FragmentMyProfileBinding.inflate(inflater, container, false);

        fragmentMyProfileBinding.logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                dataRead = false;
                ((Parti) MyProfileFragment.this.getActivity().getApplication()).setLoggedInUser(null);
                Intent loginIntent = new Intent(MyProfileFragment.this.getContext(), LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        fragmentMyProfileBinding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentMyProfileBinding.selfDescription.getText().length() > Parti.MAX_SELF_DESCRIPTION_LENGTH) {
                    Toast.makeText(MyProfileFragment.this.getContext(),"Your description cannot exceed 500 characters",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (fragmentMyProfileBinding.alias.getText().toString().contains(" ")) {
                    Toast.makeText(MyProfileFragment.this.getContext(),"No whitespaces in alias",Toast.LENGTH_SHORT).show();
                }
                User user = ((Parti) getActivity().getApplication()).getLoggedInUser();
                user.setAlias(fragmentMyProfileBinding.alias.getText().toString());
                user.setYearOfMatric(fragmentMyProfileBinding.yearOfMatric.getSelectedItem().toString());
                user.setMajor(majors[fragmentMyProfileBinding.major.getSelectedItemPosition()]);
                user.setSelfDescription(fragmentMyProfileBinding.selfDescription.getText().toString());

                firebaseFirestore.collection(Parti.USER_COLLECTION_PATH).document(user.getUuid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) Toast.makeText(getContext(), "Updated!", Toast.LENGTH_LONG).show();
                        else Toast.makeText(getContext(), "Failed to update!", Toast.LENGTH_LONG).show();
                    }
                });

                //upload image
                String imageId = Parti.PROFILE_IMAGE_COLLECTION_PATH + '/' + user.getUuid() + ".jpg";
                fragmentMyProfileBinding.profileImage.setDrawingCacheEnabled(true);
                fragmentMyProfileBinding.profileImage.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) fragmentMyProfileBinding.profileImage.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream); //TODO
                byte[] data = byteArrayOutputStream.toByteArray();
                UploadTask uploadTask = firebaseStorage.getReference().child(imageId).putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(MyProfileFragment.this.getContext(), "Something went wrong when uploading image", Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        Toast.makeText(MyProfileFragment.this.getContext(), "Image uploaded successfully", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        fragmentMyProfileBinding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, Parti.PICK_IMAGE_REQUEST_CODE); //TODO
            }
        });

        return fragmentMyProfileBinding.getRoot();
    }

    public void readData() { // TODO: This method is a bit tedious, can try to replace it with ValueEventListener.onDataChange(DataSnapshot)
        User user = ((Parti) getActivity().getApplication()).getLoggedInUser();
        if (user != null && !dataRead) {

            Glide.with(fragmentMyProfileBinding.profileImage.getContext())
                    .load(android.R.drawable.sym_def_app_icon) //TODO
                    .into(fragmentMyProfileBinding.profileImage);

            String emailString = "Email: " + user.getEmail();
            String participationPointsString = "Participation Points: " + user.getParticipationPoints();
            String userIdString = "User ID: " + user.getUuid();

            fragmentMyProfileBinding.email.setText(emailString);
            fragmentMyProfileBinding.participationPoints.setText(participationPointsString);
            fragmentMyProfileBinding.userId.setText(userIdString);

            //String aliasHint = "Alias: " + user.getAlias();
            //String selfDecriptionHint = "Self Description: " + user.getSelfDescription();

            fragmentMyProfileBinding.alias.setText(user.getAlias());
            fragmentMyProfileBinding.yearOfMatric.setSelection(Integer.parseInt(user.getYearOfMatric()) - EARLIEST_YEAR_OF_MATRIC);
            fragmentMyProfileBinding.major.setSelection(majorMap.get(user.getMajor().toString()));
            fragmentMyProfileBinding.selfDescription.setText(user.getSelfDescription());

            dataRead = true;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            readData();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Parti.PICK_IMAGE_REQUEST_CODE) { //pick an image from local gallery or remote resources and show it
            if (resultCode != Activity.RESULT_OK || data == null) {
                Toast.makeText(MyProfileFragment.this.getActivity(), "Failed to Pick Image", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = MyProfileFragment.this.getContext().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                fragmentMyProfileBinding.profileImage.setImageBitmap(selectedImage);
            } catch (FileNotFoundException ex) {
                Toast.makeText(MyProfileFragment.this.getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
}