package com.dv.nhat.nsmart.adpaters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dv.nhat.nsmart.AddOrUpdateButtonActivity;
import com.dv.nhat.nsmart.AddOrUpdateRoomActivity;
import com.dv.nhat.nsmart.R;
import com.dv.nhat.nsmart.models.Icon;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IconsAdapter extends RecyclerView.Adapter<IconsAdapter.IconHolder> {

    private Context context;
    private ArrayList<Integer> arrIcons;

    public IconsAdapter(Context context, ArrayList<Integer> arrIcons) {
        this.context = context;
        this.arrIcons = arrIcons;
    }

    @NonNull
    @Override
    public IconHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_icon,parent,false);
        return new IconHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IconHolder holder, int position) {
        holder.img.setImageResource(arrIcons.get(position));
    }

    @Override
    public int getItemCount() {
        return arrIcons.size();
    }

    public class IconHolder extends RecyclerView.ViewHolder {

        ImageView img;

        public IconHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(context instanceof AddOrUpdateRoomActivity){
                        ((AddOrUpdateRoomActivity)context).onSelectIconRoom(getAdapterPosition());
                    }
                    else if(context instanceof AddOrUpdateButtonActivity){
                        ((AddOrUpdateButtonActivity)context).onSelectIconRoom(getAdapterPosition());
                    }
                }
            });
        }
    }
}
