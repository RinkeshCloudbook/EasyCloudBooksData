package com.easycloudbooks.easycloudbooks.adapter.Company;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import  	androidx.appcompat.app.AppCompatActivity ;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.activity.CompanyProfileActivity;
import com.easycloudbooks.easycloudbooks.activity.MainActivity;
import com.easycloudbooks.easycloudbooks.app.Config;
import com.easycloudbooks.easycloudbooks.common.DataDateTime;
import com.easycloudbooks.easycloudbooks.common.DataText;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.common.SweetAlertCustom;
import com.easycloudbooks.easycloudbooks.font.FButton;
import com.easycloudbooks.easycloudbooks.fragment.CompanyFragment;
import com.easycloudbooks.easycloudbooks.fragment.DocumentUpload;
import com.easycloudbooks.easycloudbooks.model.*;
import com.easycloudbooks.easycloudbooks.util.Helper;
import com.easycloudbooks.easycloudbooks.util.UIUtils;
import com.easycloudbooks.easycloudbooks.widget.ExpandableImageCardView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class CompanyListAdapter extends RecyclerView.Adapter<CompanyListAdapter.ViewHolder> {
    public static String TAG = CompanyListAdapter.class.getSimpleName();
    private List<CompanyRelationJ> mDataSet = new ArrayList<>();
    private List<CompanyRelationJ> arraylist = new ArrayList<>();
    private Context mContext;
    private CompanyListAdapter.AdapterListener listener;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    private static int currentSelectedIndex = -1;
    //private CompanyFragment _thisFragment;
    private CompanyFragment _thisFragment;
    private Activity _thisActivity;
    private RoleJ roleJ;

    public void SetRole(RoleJ roleJ)
    {
        this.roleJ = roleJ;
    }

    public CompanyListAdapter(Context mContext, List<CompanyRelationJ> messages,
                              CompanyListAdapter.AdapterListener listener,
                              CompanyFragment _thisFragment, AppCompatActivity activity, RoleJ roleJ) {
        this.mDataSet = messages;
        this.arraylist = messages;
        this._thisFragment = _thisFragment;
        this.roleJ = roleJ;
        this._thisActivity = _thisFragment.getActivity();
        this.mContext = mContext;
        initpDialog();
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
        setHasStableIds(true);
    }

    /*public CompanyListAdapter(Context mContext, List<CompanyRelationJ> messages,
                              CompanyListAdapter.AdapterListener listener,
                              DocumentUpload _thisFragment, AppCompatActivity activity, RoleJ roleJ) {
        this.mDataSet = messages;
        this.arraylist = messages;
        this._thisFragment = _thisFragment;
        this.roleJ = roleJ;
        this._thisActivity = _thisFragment.getActivity();
        this.mContext = mContext;
        initpDialog();
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
        setHasStableIds(true);
    }*/

    private SweetAlertCustom swc;

    protected void initpDialog() {
        if (swc == null)
            swc = new SweetAlertCustom(mContext);
        swc.CreatingLoadingDialog("Loading");
    }

    protected void showpDialog() {
        loading = true;
        swc.ShowLoading();
    }

    protected void hidepDialog() {
        loading = false;
        swc.HideLoading();
    }
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public long getItemId(int position) {
        return this.getItem(position).CRId;
    }

    /*public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    mDataSet = arraylist;
                } else {

                    List<CompanyRelationJ> filteredList = new ArrayList<>();
                    for (CompanyRelationJ androidVersion : arraylist) {

                        if (androidVersion.Name.toLowerCase().contains(charString)) {

                            filteredList.add(androidVersion);
                        }
                    }
                    mDataSet = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mDataSet;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDataSet = (ArrayList<CompanyRelationJ>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }*/





    public interface OnItemClickListener {
        void onClick(CompanyRelationJ mMessage);
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {


        public TextView txtCompanyName, company_services, pmr_name,company_legalstructure,company_industry,icon_text;
        public ImageView  imgProfile,imgCardImage,pmr_image,card_bar;
        public LinearLayout messageContainer;
        public CardView mContainer;
        public RelativeLayout company_container_3;
        public ImageButton company_profile_button;
        public  FButton call_button;
        public ViewHolder(View view) {
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

    public void Add(CompanyRelationJ mNewData, boolean NotifyChange) {
        mDataSet.add(mNewData);
        if (NotifyChange)
            notifyDataSetChanged();
    }
    public void clear() {
        mDataSet.clear();

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.company_row_list, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CompanyRelationJ message = mDataSet.get(position);
        // displaying text view data
        //Log.w(TAG, "onBindViewHolder: "+message.CRId );
        holder.txtCompanyName.setText(message.Name);
        holder.company_services.setText(message.SC + " Services");
        holder.company_legalstructure.setText(message.LS);
        holder.company_industry.setText(message.Ind);
        if (message.I != null && !TextUtils.isEmpty(message.I)) {
            Glide.with(mContext).load(DataText.GetImagePath(message.I))
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply( new RequestOptions().transform(new RoundedCorners(20)).diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(holder.imgCardImage);
            holder.imgCardImage.setColorFilter(null);
            holder.icon_text.setVisibility(View.GONE);
        } else {
            holder.imgCardImage.setImageResource(R.drawable.bg_square);
            holder.imgCardImage.setColorFilter(null);
            holder.icon_text.setText(Helper.getShortName(message.Name));
            holder.icon_text.setVisibility(View.VISIBLE);
        }

        if (message.CM != null )
        {
            //holder.company_container_devider_1.setVisibility(View.VISIBLE);
           // holder.company_container_devider_3.setVisibility(View.VISIBLE);
            holder.company_container_3.setVisibility(View.VISIBLE);
           // holder.company_container_devider_4.setVisibility(View.VISIBLE);

            holder.pmr_name.setText( message.CM.user.FN + " " + message.CM.user.LN);
            if (message.CM.user.I != null && !TextUtils.isEmpty(message.CM.user.I)) {
                Glide.with(mContext).load(DataText.GetImagePath(message.CM.user.I))
                        .thumbnail(0.5f)
                        .transition(withCrossFade())
                        .apply( new RequestOptions().circleCropTransform()
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(holder.pmr_image);
                holder.pmr_image.setColorFilter(null);
            } else {
                holder.pmr_image.setImageResource(R.drawable.bg_circle);
                holder.pmr_image.setColorFilter(null);
            }
        }
        else{
           // holder.company_container_devider_1.setVisibility(View.GONE);
           // holder.company_container_devider_3.setVisibility(View.GONE);
           // holder.company_container_devider_4.setVisibility(View.GONE);
            holder.company_container_3.setVisibility(View.GONE);

        }


        if (message.CM != null && message.CM.user.PPh != null && !TextUtils.isEmpty(message.CM.user.PPh)) {
            holder.call_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CallPhoneTime = DataDateTime.Now();
                    CallPhoneMessage = message;
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(_thisFragment.getActivity(),
                                new String[]{Manifest.permission.CALL_PHONE},
                                Config.MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    } else {
                        CallPhoneNumber();
                    }
                }
            });
            holder.call_button.setVisibility(View.VISIBLE);
        }
        else{
            holder.call_button.setVisibility(View.GONE);
        }
        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                CompanyRelationJ messagenew = mDataSet.get(position);
                Intent i = new Intent(_thisActivity, CompanyProfileActivity.class);
                i.putExtra("CR",messagenew);
                _thisActivity.startActivity(i);
            }
        });
        holder.company_profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                CompanyRelationJ messagenew = mDataSet.get(position);
                Intent i = new Intent(_thisActivity, CompanyProfileActivity.class);
                i.putExtra("CR",messagenew);
                _thisActivity.startActivity(i);
            }
        });

    }



    boolean loading = false;




    public DateTime CallPhoneTime;
    public CompanyRelationJ CallPhoneMessage;

    public void DialNumber(String PhoneNumber) {
        DateTime NowTime = DataDateTime.Now().minusMinutes(10);
        if (NowTime.isBefore(CallPhoneTime) && CallPhoneMessage != null) {
            CompanyRelationJ message = CallPhoneMessage;
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                SnakebarCustom.success(mContext, _thisFragment.getView(), "Calling" + message.CM.user.FN + "(" + PhoneNumber + ")", 1000);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + PhoneNumber));
                mContext.startActivity(intent);
                CallPhoneMessage = null;
            }
        }
    }

    public void CallPhoneNumber() {
        DateTime NowTime = DataDateTime.Now().minusMinutes(10);
        if (NowTime.isBefore(CallPhoneTime) && CallPhoneMessage != null) {
            CompanyRelationJ message = CallPhoneMessage;

            String Phone = message.CM  .user.PPh;String PhoneCode=message.CM  .user.PPhC;
            if (Phone != null && !TextUtils.isEmpty(Phone)) {
                DialNumber(Phone);
            } else {
                SnakebarCustom.danger(mContext, _thisFragment.getView(), "No Valid Phone Number found to Call", 1000);
            }
        }
    }

    private void applyClickEvents(final ViewHolder holder, final int position) {


        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position,holder);
            }
        });


    }

    /* private void applyCompanyProfilePicture(ChildViewHolder holder, CompanyRelationJ CO) {
     *//* if (!TextUtils.isEmpty(CO.getPicture())) {
            Glide.with(mContext).load(CO.getPicture())
                    .thumbnail(0.5f)
                    .transition(withCrossFade(R.anim.fade_in, 300))
                    .apply( new RequestOptions()
                            .transform(new RoundedCornersTransformation(mContext,8, 0))
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(holder.imgProfile);
            holder.imgProfile.setColorFilter(null);
        } else {*//*
        Glide.with(mContext).load(Config.CompanyImage)
                .thumbnail(0.5f)
                .transition(withCrossFade(R.anim.fade_in, 300))
                .apply( new RequestOptions()
                        .bitmapTransform(new RoundedCornersTransformation(mContext,8, 0))
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.imgProfile);
        holder.imgProfile.setColorFilter(null);
        //}
    }*/

    /*private void applyIconAnimation(ViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }
    }
*/
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    public CompanyRelationJ getItem(int position) {
        return mDataSet.get(position);
    }

    public void set(int position, CompanyRelationJ nMessage) {
        mDataSet.set(position, nMessage);
    }

 /*   private void applyImportant(ViewHolder holder, CompanyRelationJ message) {
        if (message.isImportant()) {
            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_black_24dp));
            holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_selected));
        } else {
            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_border_black_24dp));
            holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_normal));
        }
    }*/

   /* public void toggleSelection(int pos, ViewHolder holder) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        applyIconAnimation(holder,pos);
    }*/

    /*private void applyIconAnimation(View view, int position) {
        CompanyRelationJ message = mDataSet.get(position);
        RelativeLayout iconBack = (RelativeLayout) view.findViewById(R.id.icon_back);
        RelativeLayout iconFront = (RelativeLayout) view.findViewById(R.id.icon_front);
        if (selectedItems.get(position, false)) {
            iconFront.setVisibility(View.GONE);
            resetIconYAxis(iconBack);
            iconBack.setVisibility(View.VISIBLE);
            iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, iconBack, iconFront, true);
                resetCurrentIndex();
            }
        } else {
            iconBack.setVisibility(View.GONE);
            resetIconYAxis(iconFront);
            iconFront.setVisibility(View.VISIBLE);
            iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, iconBack, iconFront, false);
                resetCurrentIndex();
            }
        }
        notifyItemChanged(position,message);
    }*/

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        mDataSet.remove(position);
        //listener.onContactDeleted(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public interface AdapterListener {

        void onMessageRowClicked(int position, ViewHolder holder);

        //void onContactDeleted(int position);
    }

    public void ClearData() {
        mDataSet.clear();
    }

    private int mDuration = 300;
    private Interpolator mInterpolator = new LinearInterpolator();
    private int mLastPosition = -1;
    private boolean isFirstOnly = true;

    public void setFirstOnly(boolean firstOnly) {
        isFirstOnly = firstOnly;
    }

    private void animateItem(View view) {
        view.setTranslationY(UIUtils.getScreenHeight((Activity) view.getContext()));
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();

            /*ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(500);
            view.startAnimation(anim);*/
    }


}
