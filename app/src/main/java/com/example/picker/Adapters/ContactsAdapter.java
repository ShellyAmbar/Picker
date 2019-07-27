package com.example.picker.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.picker.Models.ContactModel;
import com.example.picker.R;
import com.example.picker.SendMessage;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.net.Proxy.Type.HTTP;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.viewHolder>{

    private List<ContactModel> modelContactList;
    private Context context;

    public ContactsAdapter(List<ContactModel> modelContactList, Context context) {
        this.modelContactList = modelContactList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact,parent,false);
        return new ContactsAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        final ContactModel contactModel =modelContactList.get(position);
        holder.contact_name.setText(contactModel.getContactName());
        //holder.contact_image.setImageURI(Uri.fromFile(new File(contactModel.getPictureUrl())));
        holder.contact_number.setText(contactModel.getContactNumber());
        holder.call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showCallLog = new Intent();
                showCallLog.setAction(Intent.ACTION_DIAL);
                //showCallLog.setType(CallLog.Calls.CONTENT_TYPE);
                showCallLog.setData(Uri.parse("tel:"+contactModel.getContactNumber().trim()));
                context.startActivity(showCallLog);

            }
        });
        holder.sms_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, SendMessage.class);
                intent.putExtra("phone",contactModel.getContactNumber());
                intent.putExtra("nameTo",contactModel.getContactName());
                intent.putExtra("userName",contactModel.getUserFrom());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return modelContactList.size() ;
    }
    public void FilterList(List<ContactModel> filterList)
    {
        modelContactList = filterList;
        notifyDataSetChanged();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        private CircleImageView contact_image;
        private TextView contact_name,contact_number;
        private ImageButton call_btn,sms_btn;
        public viewHolder(View itemView) {
            super(itemView);
            contact_image=itemView.findViewById(R.id.contact_image);
            contact_name=itemView.findViewById(R.id.contact_name);
            contact_number=itemView.findViewById(R.id.contact_number);
            call_btn=itemView.findViewById(R.id.call_btn);
            sms_btn=itemView.findViewById(R.id.sms_btn);
        }
    }
}
