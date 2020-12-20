package com.easycloudbooks.easycloudbooks.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.activity.DocumentInside;
import com.easycloudbooks.easycloudbooks.activity.SearchOpration;
import com.easycloudbooks.easycloudbooks.fragment.DocumentUpload;
import com.easycloudbooks.easycloudbooks.model.Document_upload_model;

import java.util.ArrayList;
import java.util.List;

public class DocumentAdapterr extends RecyclerView.Adapter<DocumentAdapterr.MyViewHolder>{

    Context context;
    List<Document_upload_model> docList;
    String getHASH,getPhash,searchId,getCid;
    boolean clickFlage = false;

    public DocumentAdapterr(Context context, List<Document_upload_model> docList, String cwPhash, String cwHash, String cwd, String cId) {
        this.context = context;
        this.docList = docList;
        getHASH = cwPhash;
        getPhash = cwHash;
        searchId = cwd;
        getCid = cId;
        Log.e("TEST","Search :"+searchId);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.document_upload_item_list, parent, false);
        return new DocumentAdapterr.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int i) {
        holder.setIsRecyclable(false);
       // holder.bind(docList.get(i));

        holder.doc_item_name.setText(docList.get(i).mime);
        holder.doc_item_name.setText(docList.get(i).name);
        String mime = docList.get(i).mime;
        String getDir = docList.get(i).dirs;
        final String hash = docList.get(i).hash;

        if(getHASH.contains(hash) || getPhash.contains(hash)){
            holder.lin_doc.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }else if(mime.contains("application/javascript")){
            holder.folder.setImageResource(R.drawable.ic_tyoe_javascript);
        }else if(mime.contains("image/jpeg")){
            holder.folder.setImageResource(R.drawable.ic_type_jpg);
        }else if(mime.contains("image/png")){
            holder.folder.setImageResource(R.drawable.ic_type_png);
        }else if(mime.contains("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")){
            holder.folder.setImageResource(R.drawable.ic_type_xls);
        }else if(mime.contains("application/pdf")){
            holder.folder.setImageResource(R.drawable.ic_type_pdf);
        }else if(mime.contains("application/vnd.openxmlformats-officedocument.presentationml.presentation")){
            holder.folder.setImageResource(R.drawable.ic_type_ppt);
        }else if(mime.contains("text/plain")){
            holder.folder.setImageResource(R.drawable.ic_type_txt);
        }else if(mime.contains("application/vnd.openxmlformats-officedocument.wordprocessingml.document") || (mime.contains("application/msword"))){
            holder.folder.setImageResource(R.drawable.ic_type_doc);
        }else if(mime.contains("application/zip")){
            holder.folder.setImageResource(R.drawable.ic_type_zip);
        }else if(mime.contains("text/html")){
            holder.folder.setImageResource(R.drawable.ic_type_html);
        }else if(mime.contains("application/rtf")){
            holder.folder.setImageResource(R.drawable.ic_type_rtf);
        }else if(mime.contains("application/vnd.oasis.opendocument.text")){
            holder.folder.setImageResource(R.drawable.ic_type_odt);
        }else if(mime.contains("application/vnd.ms-excel")){
            holder.folder.setImageResource(R.drawable.ic_type_xls);
        }else if(mime.contains("application/vnd.oasis.opendocument.spreadsheet")){
            holder.folder.setImageResource(R.drawable.ic_type_ods);
        }else if(getDir.contains("0")){
            holder.lin_doc.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }else {
            holder.folder.setImageResource(R.drawable.ic_folder);
        }

        holder.card_container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                holder.imageView.setVisibility(View.VISIBLE);
                holder.bind(docList.get(i));
                ((DocumentInside) context).deleteVisible();
                //notifyDataSetChanged();
                return true;
            }
        });

        holder.card_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickFlage == false){
                    String hash = docList.get(i).hash;
                    String name = docList.get(i).name;

                    if(docList.get(i).mime.contains("directory")){
                        if (context instanceof DocumentInside)
                            ((DocumentInside)context).getDirInside(getCid,hash,name);
                    }else if(docList.get(i).mime.contains("application/zip")){
                        if(context instanceof DocumentInside)
                            //((DocumentInside)context).showBottomSheetDialog(docList.get(i).hash,getCid,docList.get(i).name,docList.get(i).mime);
                            ((DocumentInside) context).showImagePath(getCid,docList.get(i).hash);
                    }else {
                        if(context instanceof DocumentInside)
                            //((DocumentInside)context).showBottomSheetDialog(docList.get(i).hash,getCid,docList.get(i).name,docList.get(i).mime);
                            ((DocumentInside) context).showImagePath(getCid,docList.get(i).hash);
                    }
                }else if(clickFlage == true){
                    holder.imageView.setVisibility(View.VISIBLE);
                    holder.bind(docList.get(i));
                    ((DocumentInside) context).deleteVisible();
                }

            }
        });

        holder.img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchId.equalsIgnoreCase("s1")){
                    if (context instanceof SearchOpration)
                        ((SearchOpration) context).showBottomSheetDialog(docList.get(i).hash, getCid, docList.get(i).name, docList.get(i).mime);
                }else {
                    if (context instanceof DocumentInside)
                        ((DocumentInside) context).showBottomSheetDialog(docList.get(i).hash, getCid, docList.get(i).name, docList.get(i).mime);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return docList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView doc_item_name;
        ImageView folder,img_menu,imageView;
        CardView lin_doc;
        FrameLayout card_container;

        public MyViewHolder(View itemView) {
            super(itemView);
            doc_item_name =itemView.findViewById(R.id.doc_item_name);
            folder =itemView.findViewById(R.id.folder);
            lin_doc =itemView.findViewById(R.id.lin_doc);
            img_menu =itemView.findViewById(R.id.img_menu);
            imageView = itemView.findViewById(R.id.imageView);
            card_container = itemView.findViewById(R.id.card_container);
        }


        public void bind(final Document_upload_model document_upload_model) {
                    clickFlage = true;
                    imageView.setVisibility(document_upload_model.isChecked() ? View.VISIBLE : View.GONE);
                    document_upload_model.setChecked(!document_upload_model.isChecked());
                    imageView.setVisibility(document_upload_model.isChecked() ? View.VISIBLE : View.GONE);
                    document_upload_model.flage = true;
                    //notifyDataSetChanged();
        }
    }

    public ArrayList<Document_upload_model> getSelected() {
        ArrayList<Document_upload_model> selected = new ArrayList<>();
        for (int i = 0; i < docList.size(); i++) {
            if (docList.get(i).isChecked()) {
                selected.add(docList.get(i));
            }
        }
        return selected;
    }
}
