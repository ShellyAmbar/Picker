package com.example.picker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddNewContact extends AppCompatActivity {
    EditText Name_edt,UserPhone_edt,email_edt,description_edt;
    Button add_btn,upload_btn;
    CircleImageView photo;
    private final int REQUEST_IMAGE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);
        Name_edt=findViewById(R.id.Name_edt);
        UserPhone_edt=findViewById(R.id.UserPhone_edt);
        email_edt=findViewById(R.id.email_edt);
        description_edt=findViewById(R.id.description_edt);
        add_btn=findViewById(R.id.add_btn);
        photo=findViewById(R.id.photo);
        upload_btn=findViewById(R.id.upload_btn);
        final String userEmail = getIntent().getStringExtra("email");
        final String userName = getIntent().getStringExtra("userName");
        String userPhone=getIntent().getStringExtra("phone");

        Name_edt.setText(userName);
        UserPhone_edt.setText(userPhone);
        email_edt.setText(userEmail);

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.NAME,userName );
                intent.putExtra(ContactsContract.Intents.Insert.EMAIL,userEmail);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_IMAGE);

            }
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {

                case REQUEST_IMAGE:
                    if (resultCode == Activity.RESULT_OK) {
                        Picasso.get().load(data.getData()).into(photo);

                    } else if (resultCode == Activity.RESULT_CANCELED) {

                    }
                    break;
            }
        } catch (Exception e) {

        }
    }
}
