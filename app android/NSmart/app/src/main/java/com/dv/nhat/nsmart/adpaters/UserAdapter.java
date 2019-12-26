package com.dv.nhat.nsmart.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.dv.nhat.nsmart.ManageUserActivity;
import com.dv.nhat.nsmart.R;
import com.dv.nhat.nsmart.RoomActivity;
import com.dv.nhat.nsmart.helper.ItemSelectListener;
import com.dv.nhat.nsmart.models.User;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {

    private Context context;
    private ArrayList<User> listUser;

    public UserAdapter(Context context, ArrayList<User> listUser) {
        this.context = context;
        this.listUser = listUser;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.item_recy_user,parent,false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserHolder holder, final int position) {
        final User user = listUser.get(position);
        holder.stt.setText(""+position);
        holder.txtUsername.setText(user.getUsername());
        holder.txtPass.setText(user.getPass());

        holder.setItemSelectListener(new ItemSelectListener() {
            @Override
            public void onSelect() {

            }

            @Override
            public void onLongSelect() {
                final PopupMenu popupMenu = new PopupMenu(context,holder.itemView);
                popupMenu.getMenuInflater().inflate(R.menu.item_user_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id){
                            case R.id.update:{
                                ((ManageUserActivity)context).update(user, position);
                            }break;
                            case R.id.delete:{
                                String params = "id="+user.getId();
                                ((ManageUserActivity)context).delete(params);
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
        return listUser.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView stt, txtUsername, txtPass;
        private ItemSelectListener itemSelectListener;

        public ItemSelectListener getItemSelectListener() {
            return itemSelectListener;
        }

        public void setItemSelectListener(ItemSelectListener itemSelectListener) {
            this.itemSelectListener = itemSelectListener;
        }

        public UserHolder(@NonNull View itemView) {
            super(itemView);

            stt = itemView.findViewById(R.id.stt);
            txtPass = itemView.findViewById(R.id.txt_pass);
            txtUsername= itemView.findViewById(R.id.txt_user);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            itemSelectListener.onLongSelect();
            return true;
        }
    }
}
