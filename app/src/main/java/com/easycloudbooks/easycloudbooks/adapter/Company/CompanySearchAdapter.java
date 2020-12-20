package com.easycloudbooks.easycloudbooks.adapter.Company;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.fragment.DocumentUpload;
import com.easycloudbooks.easycloudbooks.model.CompanyRelationJ;
import java.util.ArrayList;
public class CompanySearchAdapter extends RecyclerView.Adapter<CompanySearchAdapter.ViewHolder> {

    Context context;
    ArrayList<CompanyRelationJ> list;
    Fragment mfragment;


    public CompanySearchAdapter(Fragment mFragment, Context context, ArrayList<CompanyRelationJ> list) {
        this.list = list;
        this.mfragment = mFragment;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.company_search_doc_rowlist,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int i) {
        holder.card_title.setText(list.get(i).Name);
        holder.card_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TEST","Id :"+list.get(i).UId);
                ((DocumentUpload) mfragment).getDocumentData(list.get(i).UId);
                ((DocumentUpload) mfragment).getSearchName(list.get(i).Name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView card_title,txt_cSearvice;
        FrameLayout card_container;
        ImageView img_cLogo;
        LinearLayout lin_csearch;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            card_title =itemView.findViewById(R.id.card_title);
            card_container =itemView.findViewById(R.id.card_container);
           /* txt_cSearvice =itemView.findViewById(R.id.txt_cSearvice);
            img_cLogo =itemView.findViewById(R.id.img_cLogo);
            lin_csearch =itemView.findViewById(R.id.lin_csearch);*/
        }

    }
}
