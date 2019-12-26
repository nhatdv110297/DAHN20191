package com.dv.nhat.nsmart.adpaters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dv.nhat.nsmart.HomeActivity;
import com.dv.nhat.nsmart.R;
import com.dv.nhat.nsmart.RoomActivity;
import com.dv.nhat.nsmart.helper.Icons;
import com.dv.nhat.nsmart.helper.ItemSelectListener;
import com.dv.nhat.nsmart.models.Buttonn;
import com.google.android.material.circularreveal.cardview.CircularRevealCardView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButtonHolder> {

    private Context context;
    private ArrayList<Buttonn> listButton;

    public ButtonAdapter(Context context, ArrayList<Buttonn> listButton) {
        this.context = context;
        this.listButton = listButton;
    }

    @NonNull
    @Override
    public ButtonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.item_recy_button,parent,false);
        return new ButtonHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ButtonHolder holder, final int position) {
        final Buttonn bt = listButton.get(position);
        holder.nameButton.setText(bt.getName());
        holder.iconButton.setImageResource(Icons.getIndexIconLight(bt.getIndexIcon()));
        if(bt.getState()==0){
            holder.satte.setCircularRevealScrimColor(Color.GREEN);
        }
        else{
            holder.satte.setCircularRevealScrimColor(Color.GRAY);
        }

        holder.setItemSelectListener(new ItemSelectListener() {
            @Override
            public void onSelect() {
                ((RoomActivity)context).onClickItem(position);
            }

            @Override
            public void onLongSelect() {
                final PopupMenu popupMenu = new PopupMenu(context,holder.itemView);
                popupMenu.getMenuInflater().inflate(R.menu.item_room_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id){
                            case R.id.infor:{
                                ((RoomActivity)context).showInforItem(bt);
                            }break;
                            case R.id.update:{
                                ((RoomActivity)context).update(bt,position);
                            }break;
                            case R.id.delete:{
                                ((RoomActivity)context).delete("id="+bt.getId());
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
        return listButton.size();
    }

    public class ButtonHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

        CircularRevealCardView satte;
        ImageView iconButton;
        TextView nameButton;

        private ItemSelectListener itemSelectListener;

        public ItemSelectListener getItemSelectListener() {
            return itemSelectListener;
        }

        public void setItemSelectListener(ItemSelectListener itemSelectListener) {
            this.itemSelectListener = itemSelectListener;
        }


        public ButtonHolder(@NonNull View itemView) {
            super(itemView);
            satte = itemView.findViewById(R.id.state_button);
            iconButton = itemView.findViewById(R.id.icon_button);
            nameButton = itemView.findViewById(R.id.name_button);
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
