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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.parti.Parti;
import com.example.parti.adapters.ProjectRecyclerAdapter;
import com.example.parti.databinding.FragmentUserProfileBinding;
import com.example.parti.wrappers.Email;
import com.example.parti.wrappers.Major;
import com.example.parti.wrappers.Project;
import com.example.parti.wrappers.User;
import com.example.parti.wrappers.util.LinearLayoutManagerWrapper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UserProfileFragment extends Fragment {

    //public static final String CURRENT_USER_INDICATOR = "is_current_user";
    //public static final int EARLIEST_YEAR_OF_MATRIC = Parti.EARLIEST_YEAR_OF_MATRIC;
    private static final Major[] majors = Parti.MAJORS;

    private FragmentUserProfileBinding fragmentUserProfileBinding;
    private HashMap<String, Integer> majorMap = new HashMap<>();
    //boolean dataRead = false;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private User user;
    private User loggedInUser;
    private boolean isLoggedInUser;

    public UserProfileFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        int mapSize = majors.length;
        if (majorMap.isEmpty()) for (int i = 0; i < mapSize; i++) majorMap.put(majors[i].toString(), i);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        user = (User) getArguments().getSerializable(User.CLASS_ID);
        //isLoggedInUser = getArguments().getBoolean(CURRENT_USER_INDICATOR);
        isLoggedInUser = firebaseAuth.getCurrentUser().getUid().equals(user.getUuid());
        loggedInUser = ((Parti) getActivity().getApplication()).getLoggedInUser();

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentUserProfileBinding = FragmentUserProfileBinding.inflate(inflater, container, false);
        return fragmentUserProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        initialiseData();
        initialiseViews();

        fragmentUserProfileBinding.buttonUserProfileLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                //dataRead = false;
                ((Parti) UserProfileFragment.this.getActivity().getApplication()).setLoggedInUser(null);
                //Intent loginIntent = new Intent(UserProfileFragment.this.getContext(), LoginActivity.class);
                //startActivity(loginIntent);
                getActivity().finish();
            }
        });

        fragmentUserProfileBinding.buttonUserProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateInput()) return;
                if (!firebaseAuth.getCurrentUser().getUid().equals(user.getUuid())) return;
                updateProfile();
                uploadImage();
            }
        });

        fragmentUserProfileBinding.imageUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, Parti.PICK_IMAGE_REQUEST_CODE); //TODO use the updated version
            }
        });

        fragmentUserProfileBinding.buttonUserProfileTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = fragmentUserProfileBinding.inputUserProfileTransfer.getText().toString();
                if (input.isEmpty()) {
                    Toast.makeText(getContext(), "Empty transfer amount.", Toast.LENGTH_LONG).show();
                    return;
                }
                double amount = Double.parseDouble(input);
                if (amount <= 0.0) {
                    Toast.makeText(getContext(), "Non-positive transfer amount.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (loggedInUser.getParticipationPoints() < amount) {
                    Toast.makeText(getContext(), "You currently have insufficient amount of PPs to transfer.", Toast.LENGTH_LONG).show();
                    return;
                }
                user.receiveTransfer(amount);
                loggedInUser.transferPp(amount);
                Task<Void> uploadUser = uploadUser(user);
                Task<Void> uploadLoggedInUser = uploadUser(loggedInUser);
                Task<DocumentReference> sendConfirmationEmail = sendTransferConfirmationEmail(amount);
                Tasks.whenAllComplete(uploadUser, uploadLoggedInUser, sendConfirmationEmail).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Transferred successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to transfer", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Parti.PICK_IMAGE_REQUEST_CODE) { //pick an image from local gallery or remote resources and show it
            if (resultCode != Activity.RESULT_OK || data == null) {
                Toast.makeText(UserProfileFragment.this.getActivity(), "Failed to Pick Image", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = UserProfileFragment.this.getContext().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                fragmentUserProfileBinding.imageUserProfile.setImageBitmap(selectedImage);
            } catch (FileNotFoundException ex) {
                Toast.makeText(UserProfileFragment.this.getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLoggedInUser) initialiseData();
    }

    private void initialiseData() {
        displayValues();
        downloadImage();
        setUpRecyclerViews();
    }

    private void initialiseViews() {
        fragmentUserProfileBinding.imageUserProfile.setClickable(isLoggedInUser);
        fragmentUserProfileBinding.inputUserProfileAlias.setEnabled(isLoggedInUser);
        fragmentUserProfileBinding.spinnerUserProfileYearOfMatric.setEnabled(isLoggedInUser);
        fragmentUserProfileBinding.spinnerUserProfileMajor.setEnabled(isLoggedInUser);
        fragmentUserProfileBinding.inputUserProfileDescription.setEnabled(isLoggedInUser);

        String start = isLoggedInUser ? "You" : "This user";
        String noPosted = start + "did not post any project.";
        String noParticipated = start + "did not participate in any project.";
        fragmentUserProfileBinding.headerUserProfileNoPosted.setText(noPosted);
        fragmentUserProfileBinding.headerUserProfileNoParticipated.setText(noParticipated);

        fragmentUserProfileBinding.constraintUserProfileTransfer.setVisibility(isLoggedInUser ? View.GONE : View.VISIBLE);
        fragmentUserProfileBinding.constraintLayoutUserProfileButtons.setVisibility(isLoggedInUser ? View.VISIBLE : View.GONE);

        String tips = String.format(Locale.ENGLISH, "You can transfer some of your PPs to this user, but know that a conversion rate of %.2f will be applied.", Parti.PP_TRANSFER_CONVERSION_RATE);
        tips += String.format(Locale.ENGLISH, "\nYou currently have %.2f PPs.", loggedInUser.getParticipationPoints());
        fragmentUserProfileBinding.headerUserProfileTransferTips.setText(tips);
    }

    private void downloadImage() {
        //User user = ((Parti) getActivity().getApplication()).getLoggedInUser();
        //Download image
        StorageReference imageReference = firebaseStorage.getReference().child(user.getProfileImageId());
        final long ONE_MEGABYTE = 1024 * 1024;
        imageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                fragmentUserProfileBinding.imageUserProfile.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(UserProfileFragment.this.getContext(), "Failed to download profile image", Toast.LENGTH_LONG).show();
                //If failed, load the default local image;
                Glide.with(fragmentUserProfileBinding.imageUserProfile.getContext())
                        .load(android.R.drawable.sym_def_app_icon)
                        .into(fragmentUserProfileBinding.imageUserProfile);
            }
        });
    }

    private void displayValues() {

        String emailString = "Email: " + user.getEmail();
        String participationPointsString = "Participation Points: " + user.getParticipationPoints();
        String userIdString = "User ID: " + user.getUuid();

        fragmentUserProfileBinding.inputUserProfileEmail.setText(emailString);
        fragmentUserProfileBinding.inputUserProfileParticipationPoints.setText(participationPointsString);
        fragmentUserProfileBinding.inputUserProfileUserId.setText(userIdString);

        //String aliasHint = "Alias: " + user.getAlias();
        //String selfDecriptionHint = "Self Description: " + user.getSelfDescription();

        fragmentUserProfileBinding.inputUserProfileAlias.setText(user.getAlias());
        fragmentUserProfileBinding.spinnerUserProfileYearOfMatric.setSelection(Integer.parseInt(user.getYearOfMatric()) - Parti.EARLIEST_YEAR_OF_MATRIC);
        fragmentUserProfileBinding.spinnerUserProfileMajor.setSelection(majorMap.get(user.getMajor().toString()));
        fragmentUserProfileBinding.inputUserProfileDescription.setText(user.getSelfDescription());

        //dataRead = true;
    }

    private void setUpRecyclerViews() {
        List<String> posted = user.getProjectsPosted();
        boolean noPosted = posted.isEmpty();
        if (noPosted) {
            fragmentUserProfileBinding.headerUserProfileNoPosted.setVisibility(View.VISIBLE);
        } else {
            fragmentUserProfileBinding.headerUserProfileNoPosted.setVisibility(View.GONE);
            setAdapter(posted, fragmentUserProfileBinding.recyclerViewUserProfilePosted);
        }

        List<String> participated = user.getProjectsParticipated();
        boolean noParticipated = participated.isEmpty();
        if (noParticipated) {
            fragmentUserProfileBinding.headerUserProfileNoParticipated.setVisibility(View.VISIBLE);
        } else {
            fragmentUserProfileBinding.headerUserProfileNoParticipated.setVisibility(View.GONE);
            setAdapter(participated, fragmentUserProfileBinding.recyclerViewUserProfileParticipated);
        }
    }

    private void setAdapter(List<String> filter, RecyclerView recyclerView) {
        Query query = firebaseFirestore
                .collection(Parti.PROJECT_COLLECTION_PATH)
                .whereIn(Project.PROJECT_ID_FIELD, filter);
        FirestoreRecyclerOptions<Project> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Project>()
                .setQuery(query, Project.class)
                .setLifecycleOwner(this)
                .build();
        ProjectRecyclerAdapter projectRecyclerAdapter = new ProjectRecyclerAdapter(firestoreRecyclerOptions);
        recyclerView.setLayoutManager(new LinearLayoutManagerWrapper(getContext()));
        recyclerView.setAdapter(projectRecyclerAdapter);
    }

    private boolean validateInput() {
        if (fragmentUserProfileBinding.inputUserProfileDescription.getText().length() > Parti.MAX_SELF_DESCRIPTION_LENGTH) {
            Toast.makeText(UserProfileFragment.this.getContext(),"Your description cannot exceed 5000 characters",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (fragmentUserProfileBinding.inputUserProfileAlias.getText().toString().contains(" ")) {
            Toast.makeText(UserProfileFragment.this.getContext(),"No whitespaces in alias",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (fragmentUserProfileBinding.inputUserProfileAlias.getText().toString().isEmpty()) {
            Toast.makeText(UserProfileFragment.this.getContext(),"Empty alias",Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fragmentUserProfileBinding.inputUserProfileDescription.getText().toString().isEmpty()) {
            fragmentUserProfileBinding.inputUserProfileDescription.setText(Parti.DEFAULT_USER_SELF_DESCRIPTION);
        }
        return true;
    }

    private void updateProfile() {
        user.setAlias(fragmentUserProfileBinding.inputUserProfileAlias.getText().toString());
        user.setProfileImageId(Parti.PROFILE_IMAGE_COLLECTION_PATH + '/' + user.getUuid() + ".jpg");
        user.setYearOfMatric(fragmentUserProfileBinding.spinnerUserProfileYearOfMatric.getSelectedItem().toString());
        user.setMajor(majors[fragmentUserProfileBinding.spinnerUserProfileMajor.getSelectedItemPosition()]);
        user.setSelfDescription(fragmentUserProfileBinding.inputUserProfileDescription.getText().toString());

        firebaseFirestore.collection(Parti.USER_COLLECTION_PATH).document(user.getUuid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) Toast.makeText(getContext(), "Updated!", Toast.LENGTH_LONG).show();
                else Toast.makeText(getContext(), "Failed to update!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private Task<Void> uploadUser(User user) {
        return firebaseFirestore.collection(Parti.USER_COLLECTION_PATH).document(user.getUuid()).set(user);
    }

    private Task<DocumentReference> sendTransferConfirmationEmail(double amount) {
        String subject = Parti.DEFAULT_TRANSFER_CONFIRMATION_EMAIL_SUBJECT;
        StringBuilder text = new StringBuilder("Hi!")
                .append("\nThank you for using Parti.")
                .append("\nYour friend [ ").append(loggedInUser.getAlias()).append(" ] transferred ").append(String.format(Locale.ENGLISH, "%.2f", amount)).append(" PPs to you at ").append(LocalDateTime.now().toString())
                .append("\n\nBest Regards,")
                .append("\nThe Parti. team")
                .append("\n\n\nThis is a no-reply email.");
        String to = user.getEmail();
        Email email = new Email(to, subject, text.toString());
        return firebaseFirestore.collection(Parti.EMAIL_COLLECTION_PATH).add(email);
    }

    private void uploadImage() {
        //upload image
        String imageId = Parti.PROFILE_IMAGE_COLLECTION_PATH + '/' + user.getUuid() + ".jpg";
        fragmentUserProfileBinding.imageUserProfile.setDrawingCacheEnabled(true);
        fragmentUserProfileBinding.imageUserProfile.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) fragmentUserProfileBinding.imageUserProfile.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream); //TODO
        byte[] data = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask = firebaseStorage.getReference().child(imageId).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(UserProfileFragment.this.getContext(), "Something went wrong when uploading image", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Toast.makeText(UserProfileFragment.this.getContext(), "Image uploaded successfully", Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
    public void readData() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            String uuid = firebaseUser.getUid();
            user = ((Parti) getActivity().getApplication()).getLoggedInUser();
            if (user == null || !user.getUuid().equals(firebaseUser.getUid())) {
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                DocumentReference documentReference =
                        firebaseFirestore.collection(Parti.USER_COLLECTION_PATH).document(uuid);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User loggedInUser = documentSnapshot.toObject(User.class);
                        ((Parti) UserProfileFragment.this.getActivity().getApplication()).setLoggedInUser(loggedInUser);
                        downloadImage();
                        displayValues();
                    }
                });
            } else displayValues();
        }
    }
    */

    /*
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            readData();
        }
    }
    */
}