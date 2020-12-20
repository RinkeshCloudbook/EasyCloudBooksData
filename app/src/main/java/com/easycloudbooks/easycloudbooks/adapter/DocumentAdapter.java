package com.easycloudbooks.easycloudbooks.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.activity.DocumentInside;
import com.easycloudbooks.easycloudbooks.fragment.DocumentUpload;
import com.easycloudbooks.easycloudbooks.model.Document_upload_model;

import java.util.ArrayList;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.MyViewHolder>{

    private Fragment mFragment;
    Context context;
    List<Document_upload_model> docList;
    String getcwdHash,getcwdPhash,cId;
    boolean clickFlage = false;

    public DocumentAdapter(Fragment mFragment, Context context, List<Document_upload_model> docList, String cwdHash, String CRId) {
        this.mFragment = mFragment;
        this.context = context;
        this.docList = docList;
        getcwdHash = cwdHash;
        cId = CRId;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.document_upload_item_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int i) {

        holder.doc_item_name.setText(docList.get(i).mime);
        holder.doc_item_name.setText(docList.get(i).name);
        final String mime = docList.get(i).mime;
        String getDir = docList.get(i).dirs;
        final String hash = docList.get(i).hash;

        if(getcwdHash.contentEquals(hash)){
            holder.lin_doc.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            //holder.lin_doc.removeViewAt(i);
        } else if(mime.contains("application/javascript")){
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
        }else if(mime.contains("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
            holder.folder.setImageResource(R.drawable.ic_type_doc);
        }else if(mime.contains("application/zip")){
            holder.folder.setImageResource(R.drawable.ic_type_zip);
        }else if(mime.contains("text/xml")){
            holder.folder.setImageResource(R.drawable.ic_type_txt);
        }else if(getDir.contains("0")){
            holder.lin_doc.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }else {
            holder.folder.setImageResource(R.drawable.ic_folder);
        }

        /*if(getcwdHash.contentEquals(hash)){
           // holder.lin_doc.setVisibility(View.GONE);
            holder.folder.setImageResource(R.drawable.ic_folder);
        }else if(mime.contains("application/javascript") || mime.contains("image/jpeg")){
            holder.folder.setImageResource(R.drawable.ic_file);
        }else {
            holder.folder.setImageResource(R.drawable.ic_folder);
        }*/

        holder.card_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TEST","Click Content");
                if(clickFlage == false){
                    if(docList.get(i).mime.contains("directory")){
                        Intent intent = new Intent(context, DocumentInside.class);
                        intent.putExtra("target",docList.get(i).hash);
                        intent.putExtra("cId",cId);
                        intent.putExtra("name",docList.get(i).name);
                        context.startActivity(intent);
                    }else {
                        //((DocumentUpload)mFragment).showBottomSheetDialog(docList.get(i).hash,cId, docList.get(i).name,docList.get(i).mime);
                        ((DocumentUpload) mFragment).showImagePath(cId,docList.get(i).hash);
                    }
                }else {
                    holder.imageView.setVisibility(View.VISIBLE);
                    holder.bind(docList.get(i));
                    ((DocumentUpload) mFragment).deleteVisible();
                }
            }
        });
        holder.card_container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(mFragment instanceof DocumentUpload)
                   // ((DocumentUpload)mFragment).showBottomSheetDialog(docList.get(i).hash,cId,docList.get(i).name,docList.get(i).mime);
                holder.imageView.setVisibility(View.VISIBLE);
                holder.bind(docList.get(i));
                ((DocumentUpload) mFragment).deleteVisible();
                //notifyDataSetChanged();
                return true;

            }
        });
        holder.img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mFragment instanceof DocumentUpload)
                    ((DocumentUpload)mFragment).showBottomSheetDialog(docList.get(i).hash,cId, docList.get(i).name,docList.get(i).mime);
            }
        });
    }

    @Override
    public int getItemCount() {
        return docList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView doc_item_name;
        ImageView folder, img_menu, imageView;
        CardView lin_doc;
        FrameLayout card_container;

        public MyViewHolder(View itemView) {
            super(itemView);
            doc_item_name = itemView.findViewById(R.id.doc_item_name);
            folder = itemView.findViewById(R.id.folder);
            lin_doc = itemView.findViewById(R.id.lin_doc);
            img_menu = itemView.findViewById(R.id.img_menu);
            imageView = itemView.findViewById(R.id.imageView);
            card_container = itemView.findViewById(R.id.card_container);
        }

        public void bind(Document_upload_model document_upload_model) {
            clickFlage = true;
            imageView.setVisibility(document_upload_model.isChecked() ? View.VISIBLE : View.GONE);
            document_upload_model.setChecked(!document_upload_model.isChecked());
            imageView.setVisibility(document_upload_model.isChecked() ? View.VISIBLE : View.GONE);
            document_upload_model.flage = true;
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
