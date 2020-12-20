package com.easycloudbooks.easycloudbooks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.model.DrawerItem;
import com.easycloudbooks.easycloudbooks.model.DummyModel;
import java.util.ArrayList;
import java.util.List;

public class DrawerMainAdapter extends BaseAdapter {

    private List<DummyModel> mDrawerItems;
    private LayoutInflater mInflater;

    public DrawerMainAdapter(Context context, List<DummyModel> items) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDrawerItems = items;
    }

    @Override
    public int getCount() {
        return mDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mDrawerItems.get(position);
    }

    public List<DummyModel> getItems() {
        return mDrawerItems;
    }

    @Override
    public long getItemId(int position) {
        return mDrawerItems.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(
                    R.layout.list_view_item_navigation_drawer_social, parent,
                    false);
            holder = new ViewHolder();
            holder.icon = (TextView) convertView
                    .findViewById(R.id.icon_social_navigation_item);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.number_of_notification = (TextView) convertView.findViewById(R.id.number_of_notification);
            holder.number_of_item = (TextView) convertView.findViewById(R.id.number_of_item);
            holder.name_of_item = (TextView) convertView.findViewById(R.id.name_of_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DummyModel item = mDrawerItems.get(position);

        holder.icon.setText(item.getIconRes());
        holder.number_of_item.setText(Long.toString(item.get_Number_of_item()));
        holder.name_of_item.setText(item.get_Name_of_item());
        holder.title.setText(item.getText());
        if(item.getNoOfNotification() > 0) {
            holder.number_of_notification.setVisibility(View.VISIBLE);
            holder.number_of_notification.setText(Long.toString(item.getNoOfNotification()));
        }
        else
        {
            holder.number_of_notification.setVisibility(View.GONE);
            holder.number_of_notification.setText("");
        }
        return convertView;
    }

    private static class ViewHolder {
        public TextView icon;
        public/* Roboto */ TextView title;
        public/* Roboto */ TextView number_of_notification;
        public/* Roboto */ TextView number_of_item;
        public/* Roboto */ TextView name_of_item;
    }
}
