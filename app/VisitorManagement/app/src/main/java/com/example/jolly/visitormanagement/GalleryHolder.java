package com.example.jolly.visitormanagement;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryHolder extends RecyclerView.ViewHolder {

    TextView tvContent;
    //    TextView tvOffice;
    ConstraintLayout containerCL;
    CardView containerCV;
    ImageView imFac;

    public GalleryHolder(View itemView) {
        super(itemView);
        tvContent = (TextView) itemView.findViewById(R.id.tvContent);
//        tvOffice = (TextView) itemView.findViewById(R.id.tvOffice);
//        swStatus = (Switch) itemView.findViewById(R.id.swStatus);
        containerCL = (ConstraintLayout) itemView.findViewById(R.id.containerCL);
        containerCV = (CardView) itemView.findViewById(R.id.containerCV);
        imFac = itemView.findViewById(R.id.imFac);

    }
}

