package com.example.idene.whatsapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.config.ConfigurationFirebase;
import com.example.idene.whatsapp.helper.Permission;
import com.example.idene.whatsapp.helper.UserFirebase;
import com.example.idene.whatsapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

//COMPLETED

public class ConfigurationsActivity extends AppCompatActivity {

    //ConfiguracoesActivity--ConfigurationsActivity

    //create permissions
    private String[] permissionsNecessary = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private ImageButton imageButtonCamera, imageButtonGallery;
    private static final  int SELECTION_CAMERA =100;
    private static final  int SELECTION_GALLERY =200;
    private CircleImageView circleImageViewProfile;
    private EditText editProfileName;
    private ImageView imageUpdateName;
    private StorageReference storageReference;
    private String idIdentifierUser;
    private User userLogged;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        //storageReference instantiating the reference
        storageReference = ConfigurationFirebase.getFirebaseStorage();
        //save user id
        idIdentifierUser = UserFirebase.getIdentifierUser();
        //validate permisssions
        Permission.ValidPermission(permissionsNecessary,this,1);
        //
        userLogged = UserFirebase.getDataUserLogged();


        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGallery = findViewById(R.id.imageButtonGallery);
        circleImageViewProfile = findViewById(R.id.circleImageViewFotoPerfil);
        editProfileName = findViewById(R.id.editProfileName);
        imageUpdateName = findViewById(R.id.imageUpdateName);


        //Configure the toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);//support for previous versions
        //configuration to display the back button on the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//getSupportActionBar() object that allows us to change a toolbar object


        //Recover user data
        FirebaseUser user = UserFirebase.getCurrentUser();
        Uri url = user.getPhotoUrl();

        if(url != null){
            Glide.with(ConfigurationsActivity.this).load(url).into(circleImageViewProfile);
        }else {
            circleImageViewProfile.setImageResource(R.drawable.standard);
        }
        //retrieves the user's name in firebase
        editProfileName.setText(user.getDisplayName());


        //click
        //open camera
        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//ACTION_IMAGE_CAPTURE use the camera
                if(i.resolveActivity(getPackageManager()) != null){//checks if intent was able to resolve the request to open the camera if the user has a camera
                    startActivityForResult(i, SELECTION_CAMERA);
                }

            }
        });

        //open Gallery
        imageButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//ACTION_PICK
                if(i.resolveActivity(getPackageManager()) != null){//checks if intent was able to resolve the request to open the photo gallery if the user has a camera
                    startActivityForResult(i, SELECTION_GALLERY);
                }

            }
        });

        imageUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = editProfileName.getText().toString();
                boolean isReturn = UserFirebase.updateUsername(name);
                if(isReturn){

                    userLogged.setName(name);
                    userLogged.update();

                    Toast.makeText(ConfigurationsActivity.this,"Name changed successfully !",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    //retrieve data from the intent call
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap image =null;

            try {

                switch(requestCode){
                    case SELECTION_CAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECTION_GALLERY:
                        Uri localImageSelects = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(),localImageSelects);
                        break;
                }

                //configure the image within the imageview
                if (image != null){

                    circleImageViewProfile.setImageBitmap( image );

                    //recover image data to firebase
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG,70,byteArrayOutputStream);
                    byte[] information = byteArrayOutputStream.toByteArray();

                    //Save image to firebase
                    StorageReference imagemRef = storageReference
                            .child("images")
                            .child("profile")
                            //.child(identificadorUsuario)
                            //.child("perfil.jpeg");
                            .child(idIdentifierUser + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(information);
                    uploadTask.addOnFailureListener(new OnFailureListener() {//Mensagem falha
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfigurationsActivity.this,"Error when uploading the image!",Toast.LENGTH_SHORT).show();


                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {//Mensagem sucesso
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ConfigurationsActivity.this,"Image uploaded successfully !",Toast.LENGTH_SHORT).show();

                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri url) {
                                updatePhotosUser(url);
                            }
                        });
                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    public void updatePhotosUser(Uri url){

        boolean isReturn = UserFirebase.updateUser(url);
        if (isReturn){
            userLogged.setPhotograph(url.toString());
            userLogged.update();
            Toast.makeText(ConfigurationsActivity.this,"Your photo has been changed!",Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissionResult:grantResults) {//go through the array of grantResults integers to see if any permissions have been denied

            if (permissionResult ==PackageManager.PERMISSION_DENIED){
                alertValidationPermision();
            }

        }
    }

    private void alertValidationPermision(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission denied");
        builder.setMessage("To use the app, you must give the permissions");
        builder.setCancelable(false);//forces the user to click on confirm without leaving the dialog
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}


