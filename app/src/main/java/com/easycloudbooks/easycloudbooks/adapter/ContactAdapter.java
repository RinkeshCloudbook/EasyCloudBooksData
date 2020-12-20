package com.easycloudbooks.easycloudbooks.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.activity.ContactFilterActivity;
import com.easycloudbooks.easycloudbooks.activity.ContectProfile;
import com.easycloudbooks.easycloudbooks.app.Config;
import com.easycloudbooks.easycloudbooks.common.DataDateTime;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.font.FButton;
import com.easycloudbooks.easycloudbooks.fragment.ContactFragment;
import com.easycloudbooks.easycloudbooks.model.ContactDetails;
import com.easycloudbooks.easycloudbooks.util.DataText;
import com.easycloudbooks.easycloudbooks.util.Helper;

import org.joda.time.DateTime;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    List<ContactDetails> contactList;
    List<ContactDetails> arraylist;
    List<ContactDetails> tempArrayList =new ArrayList<>();
    Context context;
    private Fragment contactFragment;
    public DateTime CallPhoneTime;

    public ContactAdapter(ContactFragment contactFragment, Context context, List<ContactDetails> contactList) {
        this.contactList = contactList;
        this.arraylist = contactList;
        this.contactFragment = contactFragment;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contect_list_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int i) {

       // holder.setIsRecyclable(false);

        holder.txt_FN.setText(contactList.get(i).FN);
        holder.txt_MN.setText(contactList.get(i).MN);
        holder.txt_LN.setText(contactList.get(i).LN);
        int count = contactList.get(i).totalCount;
        final String name = contactList.get(i).FN +" "+contactList.get(i).LN;

        //holder.icon.setText(list.get(position).getName().substring(0, 1));\
        String imageURL = contactList.get(i).imageUrl;

        if(imageURL == "null"){
            holder.imgCardImage.setImageResource(R.drawable.bg_circle);
            holder.imgCardImage.setColorFilter(null);
            String logoName = contactList.get(i).FN.substring(0, 1)+""+contactList.get(i).LN.substring(0, 1);
            holder.icon_text.setText(logoName);
            holder.icon_text.setVisibility(View.VISIBLE);
        }else{
           // Glide.with(context).load(DataText.GetImagePath(imageURL)).into(holder.imgCardImage);
            /*Glide.with(context)
                    .load(DataText.GetImagePath(imageURL))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.imgCardImage);*/
            Glide.with(context).load(com.easycloudbooks.easycloudbooks.common.DataText.GetImagePath(imageURL))
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    //.apply( new RequestOptions().transform(new RoundedCorners(20)).diskCacheStrategy(DiskCacheStrategy.ALL))
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.imgCardImage);
            holder.icon_text.setVisibility(View.GONE);
        }
        String email = contactList.get(i).email;
        final String phone = contactList.get(i).Ph;
        if(phone == null){
            holder.txt_email.setText(email);
        }else{
            holder.txt_email.setText(phone);
        }
        holder.whatApp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.whatsapp.com/send?phone=" +"+91"+phone;
                try {
                    PackageManager pm = context.getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setType("text/plain");
                    i.setData(Uri.parse(url));
                    contactFragment.startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(contactFragment.getActivity(), "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        holder.call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(contactFragment.getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            Config.MY_PERMISSIONS_REQUEST_CALL_PHONE);
                } else {
                    CallPhoneNumber(phone);
                }
            }
        });
        final String getId = String.valueOf(contactList.get(i).contectId);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContectProfile.class);
                intent.putExtra("D",getId);
                intent.putExtra("N",name);
                context.startActivity(intent);
               /* if(contactFragment instanceof ContactFragment){
                    ((ContactFragment)contactFragment).getcontactDetail(id);
                }*/
            }
        });
    }

    private void CallPhoneNumber(String getPhone) {
        DateTime NowTime = DataDateTime.Now().minusMinutes(10);
        if (NowTime.isBefore(CallPhoneTime)) {
            if (getPhone != null && !TextUtils.isEmpty(getPhone)) {
                DialNumber(getPhone);
            } else {
                SnakebarCustom.danger(contactFragment.getActivity(), contactFragment.getView(), "No Valid Phone Number found to Call", 1000);
            }
        }
    }

    private void DialNumber(String getPhone) {
        DateTime NowTime = DataDateTime.Now().minusMinutes(10);
        if (NowTime.isBefore(CallPhoneTime)) {
            //CompanyRelationJ message = CallPhoneMessage;
            if (ContextCompat.checkSelfPermission(contactFragment.getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                //SnakebarCustom.success(getApplication(), ContectProfile.this.getCurrentFocus(), "Calling" + "(" + phone + ")", 1000);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + getPhone));
                contactFragment.getActivity().startActivity(intent);
                //CallPhoneMessage = null;
            }
        }
    }
    public void saveTempList(List<ContactDetails> contactList){
        tempArrayList.clear();
        tempArrayList.addAll(contactList );
    }
public void resetList(){
        contactList.clear();
        contactList.addAll(tempArrayList);
        notifyDataSetChanged();
}
    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    contactList = arraylist;
                } else {

                    List<ContactDetails> filteredList = new ArrayList<>();
                    for (ContactDetails androidVersion : arraylist) {

                        if (androidVersion.FN.toLowerCase().contains(charString)) {

                            filteredList.add(androidVersion);
                        }
                    }
                    contactList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactList = (ArrayList<ContactDetails>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txt_FN,txt_MN,txt_LN,icon_text,txt_email;
        ImageView imgCardImage;
        FButton whatApp_button,call_button;

        public MyViewHolder(View itemView) {
            super(itemView);
            txt_FN =itemView.findViewById(R.id.txt_FN);
            txt_MN =itemView.findViewById(R.id.txt_LN);
            txt_LN =itemView.findViewById(R.id.txt_LN);
            imgCardImage =itemView.findViewById(R.id.card_image);
            icon_text =itemView.findViewById(R.id.icon_text);
            txt_email =itemView.findViewById(R.id.txt_email);
            whatApp_button =itemView.findViewById(R.id.whatApp_button);
            call_button =itemView.findViewById(R.id.call_button);
        }
    }
}
