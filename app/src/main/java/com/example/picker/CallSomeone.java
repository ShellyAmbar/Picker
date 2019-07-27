package com.example.picker;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.picker.Adapters.ContactsAdapter;
import com.example.picker.Models.ContactModel;

import java.util.ArrayList;
import java.util.List;

public class CallSomeone extends AppCompatActivity {

    private RecyclerView recycle_contacts;
    private EditText search_edit_contact;
    private ImageButton search_button;
    private List<ContactModel> listOfContacts;
    private ContactsAdapter contactsAdapter;
    private int CONTACTS_PERMISSION =1;
    private String userFrom;
    private ProgressDialog progressDialog;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_someone);
        recycle_contacts=findViewById(R.id.recycle_contacts);
        search_edit_contact=findViewById(R.id.search_edit_contact);
        search_button=findViewById(R.id.search_button);
        listOfContacts = new ArrayList();
        userFrom = getIntent().getStringExtra("userName");
        contactsAdapter = new ContactsAdapter(listOfContacts,CallSomeone.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycle_contacts.setHasFixedSize(true);
        recycle_contacts.setAdapter(contactsAdapter);
        recycle_contacts.setLayoutManager(layoutManager);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(
                    CallSomeone.this,
                    new String[]{Manifest.permission.READ_CONTACTS}
                    ,CONTACTS_PERMISSION);
        }else{

            ReadAllContacts();
        }


        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        search_edit_contact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                Filter(s.toString());
            }
        });
    }

    private void Filter(String name)
    {
        ArrayList<ContactModel> arrayList = new ArrayList<>();
        for(ContactModel contactModel : listOfContacts)
        {
            if(contactModel.getContactName().toLowerCase().contains(name.toLowerCase()))
            {
                arrayList.add(contactModel);
            }

        }
      contactsAdapter.FilterList(arrayList);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==CONTACTS_PERMISSION)
        {
            if(grantResults.length!=0)
            {
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    ReadAllContacts();
                }
            }
        }
    }

    private void ReadAllContacts()
    {

        LoadPeopleIntoList loadPeopleIntoList = new LoadPeopleIntoList();
        loadPeopleIntoList.execute();
    }

    private class LoadPeopleIntoList extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... voids) {


            ContactModel contactVO;

            int Update=1;
            int progress=1;
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            int SizeOfList = cursor.getCount();
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        contactVO = new ContactModel();
                        contactVO.setContactName(name);

                        Cursor phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id},
                                null);
                        if (phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contactVO.setContactNumber(phoneNumber);
                        }

                        phoneCursor.close();

                        contactVO.setPictureUrl("");
                        contactVO.setUserFrom(userFrom);
                        listOfContacts.add(contactVO);



                    }

                    progress++;
                    Update += progress*100/SizeOfList;
                    publishProgress(Update);
                }
                cursor.close();
            }



            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(CallSomeone.this);
            progressDialog.setTitle("Please wait..");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            contactsAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setProgress(values[0]);
        }
    }

}
