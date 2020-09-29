package com.example.idene.whatsapp.helper;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.idene.whatsapp.config.ConfigurationFirebase;
import com.example.idene.whatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

//COMPLETED

public class UserFirebase {
    //UsuarioFirebase-UserFirebase--// UserFirebase-UserFirebase

    public static String getIdentifierUser(){
        //getIdentificadorUsuario-getIdentifierUser
        FirebaseAuth user = ConfigurationFirebase.getFirebaseAuthentication();
        String email = user.getCurrentUser().getEmail();//getCurrentUser() recupera user atual--getCurrentUser () retrieves current user
        String user_identifier = Base64Custom.encodeBase64(email);

        return user_identifier;
    }

    //retornar o usuario--// return the user
    public static FirebaseUser getCurrentUser(){
        //getUsuarioAtual--getCurrentUser
        FirebaseAuth user = ConfigurationFirebase.getFirebaseAuthentication();
        return user.getCurrentUser();//getCurrentUser() returns current user
    }


    public static boolean updateUsername(String name){
        //atualizarNomeUsuario-updateUsername

        try {

            FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("profile","Error updating profile name");
                    }
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }


    }

    public static boolean updateUser(Uri url){
        //atualizarFotUsuario-updateUser

        try {

            FirebaseUser user = getCurrentUser();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setPhotoUri( url).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("profile","Error updating profile photo");
                    }
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }


    }

    public static User getDataUserLogged(){
        //getDadosUsuarioLogado--getDataUserLogged

        FirebaseUser firebaseUser = getCurrentUser();

        User user = new User();
        user.setEmail(firebaseUser.getEmail());
        user.setName(firebaseUser.getDisplayName());

        if (firebaseUser.getPhotoUrl() == null){
                user.setPhotograph("");
        }else{
            user.setPhotograph(firebaseUser.getPhotoUrl().toString());
        }
        return user;
    }
}
