package com.easycloudbooks.easycloudbooks.adapter.Notification;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.activity.MainActivity;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.common.DataDateTime;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.common.SweetAlertCustom;
import com.easycloudbooks.easycloudbooks.font.FButton;
import com.easycloudbooks.easycloudbooks.fragment.NotificationFragment;
import com.easycloudbooks.easycloudbooks.model.*;
import com.easycloudbooks.easycloudbooks.model.RoleJ;
import com.easycloudbooks.easycloudbooks.util.Helper;
import com.easycloudbooks.easycloudbooks.util.UIUtils;
import com.easycloudbooks.easycloudbooks.view.ResizableImageView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.easycloudbooks.easycloudbooks.app.Config.ECBImage;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {
    public static String TAG = NotificationListAdapter.class.getSimpleName();
    private List<UserNotificationJ> mDataSet = new ArrayList<>();
    private Context mContext;
    private NotificationListAdapter.MessageAdapterListener listener;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    private static int currentSelectedIndex = -1;
    private NotificationFragment _thisFragment;
    private MainActivity _thisActivity;
    private RoleJ roleJ;
    private List<ACJM> ACs;
    public void SetRole(RoleJ roleJ)
    {
        this.roleJ = roleJ;
    }

    public NotificationListAdapter(Context mContext, List<UserNotificationJ> messages,
                                   NotificationListAdapter.MessageAdapterListener listener,
                                   NotificationFragment _thisFragment, AppCompatActivity activity, RoleJ roleJ) {
        this.mDataSet = messages;
        this._thisFragment = _thisFragment;
        this.ACs = App.getInstance().getACs();
        this.roleJ = roleJ;
        this._thisActivity = (MainActivity) _thisFragment.getActivity();
        this.mContext = mContext;
        initpDialog();
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
        setHasStableIds(true);
    }
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



    private static class GuidToIdMap {
        private final Map<String, Long> guidToIdMapping = new HashMap<String, Long>();
        private final Map<Long, String> idToGuidMapping = new HashMap<Long, String>();

        private long idGenerator = 0;

        public long getIdByGuid(final String guid) {
            if ( ! guidToIdMapping.containsKey(guid)) {
                final long id = idGenerator++;
                guidToIdMapping.put(guid, id);
                idToGuidMapping.put(id,   guid);
            }
            return guidToIdMapping.get(guid);
        }

        public String getGuidById(final long id) {
            if (idToGuidMapping.containsKey(id)) {
                return idToGuidMapping.get(id);
            }
            return null;
        }
    }



    @Override
    public long getItemId(int position) {
        final String guid = this.getItem(position).Id;
        return guidToIdMap.getIdByGuid(guid);
    }


    private final GuidToIdMap guidToIdMap = new GuidToIdMap();
    public class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView txtNotificationTitle, card_sub_title, pmr_name,notification_time,notification_note,icon_text;
        public ImageView  imgProfile,imgCardImage,pmr_image,card_bar;
        public  ResizableImageView notification_image;
        public LinearLayout messageContainer;
       //public RelativeLayout detail_container_image, iconFront;
        public FrameLayout mContainer;
        public CardView card_layout;
       // public LinearLayout message_container_parent;
        public  FButton call_button;



        public ViewHolder(View view) {
            super(view);
            mContainer = (FrameLayout) view.findViewById(R.id.container);
            txtNotificationTitle = (TextView) view.findViewById(R.id.card_title);
            card_sub_title = (TextView) view.findViewById(R.id.card_sub_title);
            icon_text = (TextView) view.findViewById(R.id.icon_text);
            imgCardImage = (ImageView) view.findViewById(R.id.card_image);
            pmr_image = (ImageView) view.findViewById(R.id.pmr_image);
            card_bar = (ImageView) view.findViewById(R.id.card_bar);
            notification_image = (ResizableImageView) view.findViewById(R.id.notification_image);
           // message_container_parent = (LinearLayout) view.findViewById(R.id.message_container_parent);
            call_button= (FButton) view.findViewById(R.id.call_button);
            notification_note = (TextView) view.findViewById(R.id.notification_note);
            notification_time = (TextView) view.findViewById(R.id.notification_time);
            card_layout= (CardView) view.findViewById(R.id.card_layout);
           // detail_container_image= (RelativeLayout) view.findViewById(R.id.detail_container_image);
        }

    }

    public void Add(UserNotificationJ mNewData, boolean NotifyChange) {
        mDataSet.add(mNewData);
        if (NotifyChange)
            notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_row_list, parent, false);

        return new ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final UserNotificationJ message = mDataSet.get(position);
       // Log.d("id", holder.getItemId()+"");
       // Log.w(TAG, "onBindViewHolder: "+message.CD.toString() );
        holder.txtNotificationTitle.setText(message.T);
        holder.notification_note.setText(message.D.replace("\\n", System.getProperty("line.separator")));
        holder.notification_time.setText(DataDateTime.GetFromNow(message.CD));
        if (message.ACId > 0 ) {
            ACJM AC = App.getInstance().getAC(message.ACId);
            if(AC != null) {
                holder.card_sub_title.setText("By "+AC.N);
                String ACImage = AC.I;
                //if (ACImage == null || ACImage.equals("") || ACImage.equals("null"))
                //    ACImage = Config.CompanyImage;
                if (ACImage != null && !TextUtils.isEmpty(ACImage)) {
                    Glide.with(mContext).load(ACImage)
                            .thumbnail(0.5f)
                            .transition(withCrossFade())
                            .apply( new RequestOptions().transform(new RoundedCorners(20)).diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(holder.imgCardImage);
                    holder.imgCardImage.setColorFilter(null);
                } else {
                    holder.imgCardImage.setImageResource(R.drawable.bg_square);
                    holder.imgCardImage.setColorFilter(null);
                    holder.icon_text.setText(Helper.getShortName(message.CR.Name));
                }

            }else {
                holder.card_sub_title.setText("By Unknown");
                holder.imgCardImage.setImageResource(R.drawable.bg_square);
                holder.imgCardImage.setColorFilter(null);
            }
        } else {
            Glide.with(mContext).load(ECBImage)
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply( new RequestOptions().transform(new RoundedCorners(20)).diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(holder.imgCardImage);
            holder.card_sub_title.setText("By EasyCloudBooks");
            holder.imgCardImage.setColorFilter(null);
        }
        if(message.IN) {
            holder.card_bar.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(), R.color.theme_success, null));
        }
        else{
            holder.card_bar.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(), R.color.gray_border, null));
        }
        String nobjimage = message.GetImage();
        if(nobjimage != null && nobjimage != "")
        {
            //holder.message_container_parent.setBackgroundColor(ResourcesCompat.getColor(mContext.getResources(), R.color.transparent_background_black, null));
           // holder.detail_container_image.setVisibility(View.VISIBLE);
            holder.notification_image.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(nobjimage)
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                    .into(holder.notification_image);
        }
        else
        {

            holder.notification_image.setVisibility(View.GONE);
            //holder.detail_container_image.setVisibility(View.GONE);
        }

        holder.card_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                itemClicked(position);
            }
        });

    }



    boolean loading = false;

    public DateTime CallPhoneTime;
    public UserNotificationJ CallPhoneMessage;

    public void DialNumber(String PhoneNumber) {
        DateTime NowTime = DataDateTime.Now().minusMinutes(10);
        if (NowTime.isBefore(CallPhoneTime) && CallPhoneMessage != null) {
            UserNotificationJ message = CallPhoneMessage;
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                SnakebarCustom.success(mContext, _thisFragment.getView(), "Calling" + message.T + "(" + PhoneNumber + ")", 1000);
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
            UserNotificationJ message = CallPhoneMessage;

           /* String Phone = message.PMR  .user.PPh;String PhoneCode=message.PMR  .user.PPhC;
            if (Phone != null && !TextUtils.isEmpty(Phone)) {
                DialNumber(Phone);
            } else {
                SnakebarCustom.danger(mContext, _thisFragment.getView(), "No Valid Phone Number found to Call", 1000);
            }*/
        }
    }


    private void applyProfilePicture(ViewHolder holder, UserNotificationJ message) {

        holder.imgProfile.setImageResource(R.drawable.bg_circle);
        holder.imgProfile.setColorFilter(message.getColor());

    }




    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    public UserNotificationJ getItem(int position) {
        return mDataSet.get(position);
    }

    public void set(int position, UserNotificationJ nMessage) {
        mDataSet.set(position, nMessage);
    }


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

        UserNotificationJ message = mDataSet.get(position);
        listener.onNotificationDeleted(position,message);
        mDataSet.remove(position);

        notifyItemRemoved(position);
        resetCurrentIndex();
        notifyItemRangeChanged(position, mDataSet.size());
        if(mDataSet.size() == 0)
        {
            listener.onNotificationRefresh();
        }
    }

    public void itemClicked(int position) {

        UserNotificationJ message = mDataSet.get(position);


        listener.onNotificationClicked(position,message);
        message.setRead(true);
        message.SetNobjStatus("click");
        message.SetIsNew(false);
        notifyItemChanged(position);
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public interface MessageAdapterListener {

        void onNotificationDeleted(int position, UserNotificationJ message);
        void onNotificationClicked(int position, UserNotificationJ message);
        void onNotificationRefresh( );
    }

    public void ClearData() {
        //Log.w(TAG, "ClearData: " );
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
