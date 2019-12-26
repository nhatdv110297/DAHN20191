package com.dv.nhat.nsmart.adpaters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.dv.nhat.nsmart.AddOrUpdateRoomActivity;
import com.dv.nhat.nsmart.HomeActivity;
import com.dv.nhat.nsmart.R;
import com.dv.nhat.nsmart.RoomActivity;
import com.dv.nhat.nsmart.helper.Icons;
import com.dv.nhat.nsmart.helper.ItemSelectListener;
import com.dv.nhat.nsmart.models.Room;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomHolder> {

    private Context context;
    private ArrayList<Room> listRoom;

    public RoomAdapter(Context context, ArrayList<Room> listRoom) {
        this.context = context;
        this.listRoom = listRoom;
    }

    @NonNull
    @Override
    public RoomHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recy_room,viewGroup,false);
        return new RoomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RoomHolder roomHolder, final int i) {
        final Room room = listRoom.get(i);
        roomHolder.imgRoom.setImageResource(Icons.getIndexIcon(room.getIndexIcon()));
        roomHolder.nameRoom.setText(room.getName());
        roomHolder.numberDevices.setText(room.getNumberDevices()+" devices");
        roomHolder.setItemSelectListener(new ItemSelectListener() {
            @Override
            public void onSelect() {
                ((HomeActivity)context).onClickItem(room);
            }

            @Override
            public void onLongSelect() {
                final PopupMenu popupMenu = new PopupMenu(context,roomHolder.itemView);
                popupMenu.getMenuInflater().inflate(R.menu.item_room_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id){
                            case R.id.infor:{
                                ((HomeActivity)context).showInforItem(room);
                            }break;
                            case R.id.update:{
                                ((HomeActivity)context).update(room,i);
                            }break;
                            case R.id.delete:{
                                ((HomeActivity)context).delete("idroom="+room.getId());
                            }break;
                            default:break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listRoom.size();
    }

    public class RoomHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView imgRoom;
        TextView nameRoom;
        TextView numberDevices;
        private ItemSelectListener itemSelectListener;

        public ItemSelectListener getItemSelectListener() {
            return itemSelectListener;
        }

        public void setItemSelectListener(ItemSelectListener itemSelectListener) {
            this.itemSelectListener = itemSelectListener;
        }

        public RoomHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.img_room);
            nameRoom = itemView.findViewById(R.id.name_room);
            numberDevices = itemView.findViewById(R.id.number_devices);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemSelectListener.onSelect();
        }

        @Override
        public boolean onLongClick(View v) {
            itemSelectListener.onLongSelect();
            return true;
        }
    }
}
