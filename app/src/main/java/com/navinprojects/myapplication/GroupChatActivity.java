package com.navinprojects.myapplication;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class GroupChatActivity extends AppCompatActivity {
    ListView listOfMessages;
    EditText message_box_editTex;
    ImageView chat_messenger_sent_imageView;
    private FirebaseAuth mAuth;
    public FirebaseListAdapter<FirebaseMessage> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        message_box_editTex = (EditText) findViewById(R.id.message_box_editText);
        chat_messenger_sent_imageView = (ImageView) findViewById(R.id.chat_messenger_sent_imageView);
        listOfMessages = (ListView) findViewById(R.id.list_of_messages);

        chat_messenger_sent_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sent the message to the realtime database
                FirebaseDatabase.getInstance()
                        .getReference()
                        .push()
                        .setValue(new FirebaseMessage(message_box_editTex.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getEmail())
                        );

                // Clear the input after message sent
                message_box_editTex.setText("");
            }
        });

        displayChatMessages(); //all the message in the database will be displayed.

    }

    private void displayChatMessages() {

        //Firebase Adapter automatically inflates the layout and binds the values, we no need to write Adapter class separately.

        adapter = new FirebaseListAdapter<FirebaseMessage>(this, FirebaseMessage.class, R.layout.message_layout, FirebaseDatabase.getInstance().getReference()) {

            @Override
            protected void populateView(View v, FirebaseMessage model, int position) {

                // Get references to the views of message.xml
                TextView messageText = (TextView) v.findViewById(R.id.chat_messenger_textView);
                TextView messageUser = (TextView) v.findViewById(R.id.chat_user_textView);
                TextView messageTime = (TextView) v.findViewById(R.id.chat_timestamp_textView);
                ImageView profileImage1 = (ImageView) v.findViewById(R.id.user_icon);
                ImageView profileImage2 = (ImageView) v.findViewById(R.id.user_icon2);


                // if the data holds current user, then layout will be in green color ash color.
                if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(model.getMessageUser())) {

                    profileImage1.setVisibility(View.INVISIBLE);
                    profileImage2.setVisibility(View.VISIBLE);
                    messageText.setBackgroundResource(R.drawable.messenger_border_green);
                    messageText.setText(model.getMessageText());
                    messageTime.setText(model.getMessageUser());
                    messageUser.setText(DateFormat.format("dd-MM (HH:mm)",
                            model.getMessageTime()));
                } else {

                    profileImage1.setVisibility(View.VISIBLE);
                    profileImage2.setVisibility(View.INVISIBLE);
                    messageText.setBackgroundResource(R.drawable.messenger_border_ash);
                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getMessageUser());
                    messageTime.setText(DateFormat.format("dd-MM (HH:mm)",
                            model.getMessageTime()));
                }

            }
        };
        // listView sets adapter.
        listOfMessages.setAdapter(adapter);
        listOfMessages.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listOfMessages.setStackFromBottom(true); // always last view will be focused.
    }
}

