package com.easycloudbooks.easycloudbooks.adapter;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.structure.DrawerProfile;
import com.easycloudbooks.easycloudbooks.theme.DrawerTheme;
import com.easycloudbooks.easycloudbooks.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class DrawerProfileAdapter extends ArrayAdapter<DrawerProfile> {


    public DrawerProfileAdapter(Context context, List<DrawerProfile> dataSet) {
        super(context, R.layout.md_drawer_item, dataSet);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        DrawerProfile drawerProfile = getItem(position);

        assert drawerProfile != null;


        if (convertView == null || !(convertView instanceof RelativeLayout)) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.md_drawer_item, parent, false);
        }

        final ViewHolder viewHolder = new ViewHolder(convertView);


        if (position == 0) {
            viewHolder.getRoot().setSelected(true);
            viewHolder.getRoot().setClickable(false);

        } else {
            viewHolder.getRoot().setSelected(false);
            viewHolder.getRoot().setClickable(true);
        }

        if (drawerProfile.hasAvatar()) {
            viewHolder.getImageView().setVisibility(View.VISIBLE);
            ImageUtil.displaySquareImage(viewHolder.getImageView(), drawerProfile.getAvatar(), null);

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) viewHolder.getImageView().getLayoutParams();
            layoutParams.height = getContext().getResources().getDimensionPixelSize(R.dimen.md_avatar_size);
            layoutParams.width = getContext().getResources().getDimensionPixelSize(R.dimen.md_baseline_content) - getContext().getResources().getDimensionPixelSize(R.dimen.md_baseline);

            int imagePaddingEnd = getContext().getResources().getDimensionPixelSize(R.dimen.md_baseline_content) - getContext().getResources().getDimensionPixelSize(R.dimen.md_baseline) - getContext().getResources().getDimensionPixelSize(R.dimen.md_avatar_size);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                viewHolder.getImageView().setPaddingRelative(0, 0, imagePaddingEnd, 0);
            } else {
                viewHolder.getImageView().setPadding(0, 0, imagePaddingEnd, 0);
            }

        } else {
            viewHolder.getImageView().setVisibility(View.GONE);
        }
        if (drawerProfile.hasName()) {
            viewHolder.getTextViewPrimary().setText(drawerProfile.getName());

            if (drawerProfile.hasDescription()) {
                viewHolder.getTextViewSecondary().setText(drawerProfile.getDescription());
                viewHolder.getTextViewSecondary().setVisibility(View.VISIBLE);
                viewHolder.getTextViewSecondary().setMaxLines(1);
            } else {
                viewHolder.getTextViewSecondary().setVisibility(View.GONE);
            }
        } else if (drawerProfile.hasDescription()) {
            viewHolder.getTextViewPrimary().setText(drawerProfile.getDescription());

            viewHolder.getTextViewSecondary().setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }

    public void setDrawerTheme(DrawerTheme theme) {
        notifyDataSetChanged();
    }

    public List<DrawerProfile> getItems() {
        List<DrawerProfile> items = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            items.add(getItem(i));
        }
        return items;
    }

    public List<Long> getACs() {
        List<Long> items = new ArrayList<Long>();
        for (int i = 0; i < getCount(); i++) {
            items.add(getItem(i).getId());
        }
        return items;
    }

    private static class ViewHolder {
        private final FrameLayout mRoot;
        private final ImageView mImageView;
        private final TextView mTextViewPrimary;
        private final TextView mTextViewSecondary;

        public ViewHolder(View root) {
            mRoot = (FrameLayout) root;
            mImageView = (ImageView) root.findViewById(R.id.mdImage);
            mTextViewPrimary = (TextView) root.findViewById(R.id.mdTextPrimary);
            mTextViewSecondary = (TextView) root.findViewById(R.id.mdTextSecondary);
        }

        public FrameLayout getRoot() {
            return mRoot;
        }

        public ImageView getImageView() {
            return mImageView;
        }

        public TextView getTextViewPrimary() {
            return mTextViewPrimary;
        }

        public TextView getTextViewSecondary() {
            return mTextViewSecondary;
        }
    }
}