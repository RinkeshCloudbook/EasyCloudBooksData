package com.easycloudbooks.easycloudbooks.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.font.FButton;
import com.easycloudbooks.easycloudbooks.fragment.CompanyFragment;
import com.easycloudbooks.easycloudbooks.model.CompanyRelationJ;
import com.easycloudbooks.easycloudbooks.model.ContactDetails;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class GetCompanyAdapter extends RecyclerView.Adapter<GetCompanyAdapter.MyViewHolder> {

    List<CompanyRelationJ> contactList;
    Context context;
    private Fragment companyFragment;
    public DateTime CallPhoneTime;

    public GetCompanyAdapter(CompanyFragment companyFragment, Context context, ArrayList<CompanyRelationJ> tempContactList) {
        this.contactList = tempContactList;
        this.companyFragment = companyFragment;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.company_row_list,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Log.e("TEST","Company FN :"+contactList.get(i).Name);
        Log.e("TEST","Company UID :"+contactList.get(i).UId);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView txtCompanyName, company_services, pmr_name,company_legalstructure,company_industry,icon_text;
        public ImageView imgProfile,imgCardImage,pmr_image,card_bar;
        public LinearLayout messageContainer;
        public CardView mContainer;
        public RelativeLayout company_container_3;
        public ImageButton company_profile_button;
        public FButton call_button;

        public MyViewHolder(@NonNull View view) {
            super(view);

            mContainer = (CardView) view.findViewById(R.id.card_layout);
            company_profile_button = (ImageButton) view.findViewById(R.id.company_profile_button);
            txtCompanyName = (TextView) view.findViewById(R.id.card_title);
            company_services = (TextView) view.findViewById(R.id.card_sub_title);
            icon_text = (TextView) view.findViewById(R.id.icon_text);
            imgCardImage = (ImageView) view.findViewById(R.id.card_image);
            pmr_image = (ImageView) view.findViewById(R.id.pmr_image);
            card_bar = (ImageView) view.findViewById(R.id.card_bar);
            call_button= (FButton) view.findViewById(R.id.call_button);

            pmr_name = (TextView) view.findViewById(R.id.pmr_name);
            company_legalstructure = (TextView) view.findViewById(R.id.company_legalstructure);
            company_industry= (TextView) view.findViewById(R.id.company_industry);
            company_container_3= (RelativeLayout) view.findViewById(R.id.company_container_3);
        }
    }
}
