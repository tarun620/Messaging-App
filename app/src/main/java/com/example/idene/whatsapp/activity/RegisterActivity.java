package com.example.idene.whatsapp.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.config.ConfigurationFirebase;
import com.example.idene.whatsapp.helper.Base64Custom;
import com.example.idene.whatsapp.helper.UserFirebase;
import com.example.idene.whatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

//COMPLETED

public class RegisterActivity extends AppCompatActivity {

    //CadastroActivity-RegisterActivity

    private TextInputEditText fieldName, fieldEmail, fieldPassword;
    //campoName-fieldName, campoEmail-fieldEmail,campoSenha-fieldPassword
    private FirebaseAuth authentication;
    //autenticacao-authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //activity_cadastro-activity_register

        fieldName = findViewById(R.id.editName);
        fieldEmail = findViewById(R.id.editEmail);
        fieldPassword = findViewById(R.id.editPassword);



    }
// cadastrarUsuario-registerUser
    public void registerUser(final User user){

//Usuario-User
//
        authentication = ConfigurationFirebase.getFirebaseAuthentication();
        authentication.createUserWithEmailAndPassword(
                user.getEmail(),user.getPassword()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this,"User registration Successful !",Toast.LENGTH_SHORT).show();
                    UserFirebase.updateUsername(user.getName());
                    finish();
                    //salvar dados dentro do firebase--save data within firebase
                    try{

                        String user_identifier = Base64Custom.encodeBase64(user.getEmail());
                        user.setId(user_identifier);
                        user.toSave();

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else{

                    String exception = "";
                    try {
                        throw task.getException();//recuperar a execao--recover the exception
                    }catch (FirebaseAuthWeakPasswordException e){
                        exception = "Enter a stronger password.";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        exception = "Please type a valid email id.";
                    }catch (FirebaseAuthUserCollisionException e){
                        exception = "This account has already been registered.";
                    } catch (Exception e) {
                        exception = "Error when registering user: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(RegisterActivity.this,exception,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    public void validateUsername(View view){
        //validarCadastroUsuario--validateUsername

        //Recuperar textos dos campos--Retrieve text from fields
        String textName = fieldName.getText().toString();
        String textEmail = fieldEmail.getText().toString();
        String textPassword = fieldPassword.getText().toString();

        if (!textName.isEmpty()){
            if (!textEmail.isEmpty()){
                if (!textPassword.isEmpty()){

                    User user = new User();
                    user.setName(textName);
                    user.setEmail(textEmail);
                    user.setPassword(textPassword);
                    registerUser(user);

                }else{
                    Toast.makeText(RegisterActivity.this,"Fill in your password.",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(RegisterActivity.this,"Fill in your email.",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(RegisterActivity.this,"Fill in your name.",Toast.LENGTH_SHORT).show();
        }


    }
}
