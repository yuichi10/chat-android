package dev.yuichi.com.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by yuichi on 9/17/16.
 */
public class RoomListAdapter extends BaseAdapter{
    Context mContext;
    LayoutInflater mLayoutInflater = null;
    ArrayList<RoomListInfo> mRoomListInfos;

    public RoomListAdapter(Context context) {
        mRoomListInfos = new ArrayList<>();
        this.mContext = context;
        this.mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mRoomListInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mRoomListInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mRoomListInfos.get(position).getId();
    }

    public String getRoomID(int position) {
        return mRoomListInfos.get(position).getRoomID();
    }

    public void addRoomListItem(RoomListInfo roomListInfo) {
        mRoomListInfos.add(roomListInfo);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(R.layout.room_list_item,parent,false);
        ((TextView)convertView.findViewById(R.id.room_list_item_name)).setText(mRoomListInfos.get(position).getName());
        ((TextView)convertView.findViewById(R.id.room_list_item_comment)).setText(mRoomListInfos.get(position).getRoomID());
        return convertView;
    }
}
