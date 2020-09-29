package com.example.idene.whatsapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.config.ConfigurationFirebase;
import com.example.idene.whatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

//COMPLETED

public class LoginActivity extends AppCompatActivity {

private TextInputEditText fieldEmail, fieldPassword;
    private FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authentication = ConfigurationFirebase.getFirebaseAuthentication();

        fieldEmail = findViewById(R.id.editLoginEmail);
        fieldPassword = findViewById(R.id.editLoginSenha);

    }

    public void loginToUser(User user){

        authentication.signInWithEmailAndPassword(
           user.getEmail(),user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful()){
                    openScreenMain();
                    finish();
                }else{

                    String exception = "";
                    try {
                        throw task.getException();//recover the exception
                    }catch (FirebaseAuthInvalidUserException e){
                        exception = "User is not registered";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        exception = "E-mail and password do not correspond to a registered user";
                    } catch (Exception e) {
                            exception = "Error when logging in user: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this,exception,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void validateUserAuthentication(View view){

        //Retrieve text from fields
        String textEmail = fieldEmail.getText().toString();
        String textPassword = fieldPassword.getText().toString();

        //validation
        if (!textEmail.isEmpty()){
                if (!textPassword.isEmpty()){

                    User user = new User();
                    user.setEmail(textEmail);
                    user.setPassword(textPassword);
                    loginToUser(user);

            }else{
                Toast.makeText(LoginActivity.this,"Fill in the email field !",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(LoginActivity.this,"Fill in the password field !",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
            FirebaseUser currentUser = authentication.getCurrentUser();
        if (currentUser != null){
            openScreenMain();
        }
    }

    public void openRegisterScreen(View view){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void openScreenMain(){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
