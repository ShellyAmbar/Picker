package com.example.picker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendMessage extends AppCompatActivity implements View.OnClickListener {

    EditText fromUserName_edt,toUserName_edt,toUserPhone_edt,email_edt,body_edt;
    Button send_btn,addUser_btn,email_btn;
    private String userName;
    private String nameTo,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        userName="";
        nameTo="";
        phone="";
        userName= getIntent().getStringExtra("userName");
        nameTo=getIntent().getStringExtra("nameTo");
        phone=getIntent().getStringExtra("phone");
        fromUserName_edt=findViewById(R.id.fromUserName_edt);
        toUserName_edt=findViewById(R.id.Name_edt);
        toUserPhone_edt=findViewById(R.id.UserPhone_edt);
        email_edt=findViewById(R.id.email_edt);
        send_btn=findViewById(R.id.send_btn);
        addUser_btn=findViewById(R.id.addUser_btn);
        email_btn=findViewById(R.id.email_btn);
        body_edt=findViewById(R.id.body_edt);
        if(userName!=null){fromUserName_edt.setText(userName);}else{userName="";}
        if(nameTo!=null){toUserName_edt.setText(nameTo);}else{nameTo="";}
        if(phone!=null){toUserPhone_edt.setText(phone);}else{phone="";};
        email_btn.setOnClickListener(this);
        addUser_btn.setOnClickListener(this);
        send_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.email_btn:
                SendEmail();
                break;
            case R.id.addUser_btn:
                AddUser();
                break;
            case R.id.send_btn:
                SendSms();
                break;
        }
    }

    private void SendSms() {


       String phoneNo = toUserPhone_edt.getText().toString();
       String message = body_edt.getText().toString();

       if(phoneNo.isEmpty() || message.isEmpty()){
           Toast.makeText(this, "You must fill the phone and message ", Toast.LENGTH_SHORT).show();
       }else{
           Intent sInt = new Intent(Intent.ACTION_VIEW);
           sInt.putExtra("address",phoneNo);
           sInt.putExtra("sms_body",message);
           sInt.setType("vnd.android-dir/mms-sms");
           startActivity(Intent.createChooser(sInt, "Send sms via:"));
       }

    }
    private void AddUser(){
        String userPhone =toUserPhone_edt.getText().toString();
        if(!userPhone.isEmpty()){
            Intent intent =new Intent(SendMessage.this,AddNewContact.class);
            intent.putExtra("userName",toUserName_edt.getText().toString());
            intent.putExtra("email",email_edt.getText().toString());
            intent.putExtra("phone",toUserPhone_edt.getText().toString());

            startActivity(intent);
        }else{
            Toast.makeText(this, "You need to fill the phone first.", Toast.LENGTH_SHORT).show();
        }

    }

    private void SendEmail(){
        String email =email_edt.getText().toString();
        if(!email.isEmpty()){
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, email_edt.getText().toString());
            intent.putExtra(Intent.EXTRA_SUBJECT, body_edt.getText().toString());
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }else{
            Toast.makeText(this, "You need to fill the email address.", Toast.LENGTH_SHORT).show();
        }

    }

}
