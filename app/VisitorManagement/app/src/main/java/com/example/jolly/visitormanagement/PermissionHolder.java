package com.example.jolly.visitormanagement;


import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class PermissionHolder extends RecyclerView.ViewHolder {

    TextView tvPerm, tvContent;
    //    Switch swStatus;
    ConstraintLayout containerCL;
    CardView containerCV;
    FloatingActionButton fabUnlock;

    public PermissionHolder(View itemView) {
        super(itemView);
        tvPerm = (TextView) itemView.findViewById(R.id.tvPerm);
        tvContent = (TextView) itemView.findViewById(R.id.tvContent);
        fabUnlock = itemView.findViewById(R.id.fabUnlock);
        fabUnlock.setVisibility(View.GONE);
//        swStatus = (Switch) itemView.findViewById(R.id.swStatus);
        containerCL = (ConstraintLayout) itemView.findViewById(R.id.containerCL);
        containerCV = (CardView) itemView.findViewById(R.id.containerCV);
    }
}
