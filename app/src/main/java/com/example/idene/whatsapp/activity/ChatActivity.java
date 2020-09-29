package com.example.idene.whatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.adapter.MessagesAdapter;
import com.example.idene.whatsapp.config.ConfigurationFirebase;
import com.example.idene.whatsapp.helper.Base64Custom;
import com.example.idene.whatsapp.model.Conversation;
import com.example.idene.whatsapp.helper.UserFirebase;
import com.example.idene.whatsapp.model.Group;
import com.example.idene.whatsapp.model.Message;
import com.example.idene.whatsapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

//COMPLETED

public class ChatActivity extends AppCompatActivity {

    private TextView textViewName;
    private CircleImageView circleImageViewPhotograph;
    private User userDestination;
    private User sender;
    private EditText editMessage;
    private ImageView imageCamera;
    private DatabaseReference database;
    private StorageReference storage;
    private DatabaseReference messageRef;
    private ChildEventListener childEventListenerPosts;

    // identifier users fearful and recipient
    private String idUserSender;
    private String idUserRecipient;
    private Group group;

    private RecyclerView recyclerMessages;
    private MessagesAdapter adapter;
    private List<Message> messages = new ArrayList<>();
    private static final  int SELECTION_CAMERA =100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Toolbar Configuration
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initial settings
        textViewName = findViewById(R.id.textViewNameChat);
        circleImageViewPhotograph = findViewById(R.id.circleImagePhotographChat);
        editMessage = findViewById(R.id.editMessage);
        recyclerMessages = findViewById(R.id.recyclerMessages);
        imageCamera = findViewById(R.id.imageCamera);

        //retrieve data from the sending user
        idUserSender = UserFirebase.getIdentifierUser();
        sender = UserFirebase.getDataUserLogged();


        //recover data of the recipient user
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            if (bundle.containsKey("chatGroup")){
                //chat group
                group = (Group) bundle.getSerializable("chatGroup");
                idUserRecipient = group.getId();//the group id comes from the group

                textViewName.setText(group.getName());//name the group

                //recover image
                String photograph = group.getPhotograph();
                if (photograph != null){
                    Uri uri = Uri.parse(photograph);
                    Glide.with(ChatActivity.this)
                            .load(uri)
                            .into(circleImageViewPhotograph);
                }else{
                    circleImageViewPhotograph.setImageResource(R.drawable.standard);
                }


            }else{
                //chat user
                userDestination =(User) bundle.getSerializable("chatContact");
                textViewName.setText(userDestination.getName());

                //recover image
                String photograph = userDestination.getPhotograph();
                if (photograph != null){
                    Uri uri = Uri.parse(userDestination.getPhotograph());
                    Glide.with(ChatActivity.this)
                            .load(uri)
                            .into(circleImageViewPhotograph);
                }else{
                    circleImageViewPhotograph.setImageResource(R.drawable.standard);
                }

                //recover data only the recipient
                idUserRecipient = Base64Custom.encodeBase64(userDestination.getEmail());
            }
        }

        //configuration adapter
        adapter = new MessagesAdapter(messages,getApplicationContext());


        //recyclerview configuration
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMessages.setLayoutManager(layoutManager);
        recyclerMessages.setHasFixedSize(true);
        recyclerMessages.setAdapter(adapter);



        database = ConfigurationFirebase.getFirebaseDatabase();
        storage = ConfigurationFirebase.getFirebaseStorage();
        messageRef = database.child("messages")
                .child(idUserSender)
                .child(idUserRecipient);


        //camera click event
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//ACTION_IMAGE_CAPTURE use the camera
                if(i.resolveActivity(getPackageManager()) != null){//checks if intent was able to resolve the request to open the camera if the user has a camera
                    startActivityForResult(i, SELECTION_CAMERA);
                }
            }
        });
    }

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
                }

                //configure the image within the imageview
                if (image != null){

                    //recover image data to firebase
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG,70,byteArrayOutputStream);
                    byte[] information = byteArrayOutputStream.toByteArray();

                    //create image name
                    String nameImage = UUID.randomUUID().toString();

                    //configure firebase reference
                    StorageReference imageRef = storage.child("images")
                            .child("photograph")
                            .child(idUserSender)
                            .child(nameImage);

                    UploadTask uploadTask = imageRef.putBytes(information);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Error","Error uploading");
                            Toast.makeText(ChatActivity.this,"Error when uploading the image!",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(ChatActivity.this,"Image uploaded successfully !",Toast.LENGTH_SHORT).show();
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri url) {

                                    Message message = new Message();
                                    message.setIdUser(idUserSender);
                                    message.setMessage("image.jpeg");
                                    message.setImage(url.toString());
                                   // atualizaFotosUsuario(url);

                                    //save sender image
                                    saveMessage(idUserSender, idUserRecipient, message);

                                    //save image recipient
                                    saveMessage(idUserRecipient, idUserSender, message);

                                    Toast.makeText(ChatActivity.this,"Image sent successfully !",Toast.LENGTH_SHORT).show();
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



    public void send_Message(View view){

        String textMessage = editMessage.getText().toString();

        if (!textMessage.isEmpty()){

            if ( userDestination != null){

                Message message = new Message();
                message.setIdUser(idUserSender);
                message.setMessage(textMessage);

                //save the message to the sender
                saveMessage(idUserSender, idUserRecipient, message);

                //save the message to the recipient
                saveMessage(idUserRecipient, idUserSender, message);

                //save sender conversation
                saveConversation(idUserSender, idUserRecipient, userDestination, message,false);

                //save recepient conversation
                saveConversation(idUserRecipient, idUserSender, sender, message,false);


            }else{

                for ( User member: group.getMembers()){

                        String idSenderGroup = Base64Custom.encodeBase64(member.getEmail());//get email from group user and convert to base 64
                    String GroupUserLoggedID = UserFirebase.getIdentifierUser();

                    Message message = new Message();
                    message.setIdUser(GroupUserLoggedID);
                    message.setMessage(textMessage);
                    message.setName(sender.getName());//display the name in the message

                    //save message to member
                    saveMessage(idSenderGroup, idUserRecipient, message);

                    //save conversation
                    saveConversation(idSenderGroup, idUserRecipient, userDestination, message,true);

                }

            }



        }else{
            Toast.makeText(ChatActivity.this,"Enter a message to send !",Toast.LENGTH_SHORT).show();
        }

    }

    private void saveConversation(String idSender, String idRecipient, User userExhibition, Message msg , boolean isGroup){

        //save sender conversation
        Conversation conversation = new Conversation();
        conversation.setIdSender(idSender);
        conversation.setIdRecipient(idRecipient);
        conversation.setLastMessage(msg.getMessage());

        if (isGroup){//group conversation

            conversation.setIsGroup("true");
            conversation.setGroup(group);

        }else{//normal conversation

            //conventional conversation
            conversation.setUserExhibition(userExhibition);
            conversation.setIsGroup("false");

        }
        conversation.toSave();
    }

    private void saveMessage(String idSender, String idRecipient, Message msg){

        DatabaseReference database = ConfigurationFirebase.getFirebaseDatabase();
        DatabaseReference messegesRef = database.child("messeges");
        messegesRef.child(idSender)
                .child(idRecipient)
                .push()
                .setValue(msg);

        //clear text from the text box
        editMessage.setText("");

    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveMessages();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messageRef.removeEventListener(childEventListenerPosts);
    }

    private void retrieveMessages(){

        childEventListenerPosts = messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {//add item
                Message message = dataSnapshot.getValue(Message.class);
                messages.add(message);
                adapter.notifyDataSetChanged();//update adapter
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {//modify item

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {//remove item

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {// move item

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {//error

            }
        });

    }

}
