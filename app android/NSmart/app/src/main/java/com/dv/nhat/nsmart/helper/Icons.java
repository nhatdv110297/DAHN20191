package com.dv.nhat.nsmart.helper;

import com.dv.nhat.nsmart.R;

public class Icons {
    private static Integer[] iconsRoom = {R.drawable.armchair, R.drawable.bathroom, R.drawable.double_bed, R.drawable.dining_table, R.drawable.kitchen};
    private static Integer[] iconsLight = {R.drawable.lamp, R.drawable.lamp_1, R.drawable.lamp_2, R.drawable.lamp_3, R.drawable.lamp_4, R.drawable.lamp_5, R.drawable.lamp_6, R.drawable.lamp_7, R.drawable.lamp_8, R.drawable.lamp_9};

    public static Integer[] getIconsRoom(){
        return iconsRoom;
    }

    public static int getIndexIcon(int index){
        return iconsRoom[index];
    }

    public static Integer[] getIconsLight() {
        return iconsLight;
    }

    public static int getIndexIconLight(int index){
        return iconsLight[index];
    }

}
