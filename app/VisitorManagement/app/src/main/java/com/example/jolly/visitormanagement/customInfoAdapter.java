package com.example.jolly.visitormanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class customInfoAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mwindow;
    private Context mcontext;

    public customInfoAdapter(Context mcontext) {
        this.mwindow = LayoutInflater.from(mcontext).inflate(R.layout.custon_info_window,null);
        this.mcontext = mcontext;
    }

    private void rendorWindowText(Marker marker,View view){
        String title = marker.getTitle();
        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        if(!title.equals("")){
            tvTitle.setText(title);
        }
        String snippet = marker.getSnippet();
        TextView tvsnippet = (TextView) view.findViewById(R.id.snippet);
        if(!snippet.equals("")){
            tvsnippet.setText(snippet);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        rendorWindowText(marker,mwindow);
        return mwindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendorWindowText(marker,mwindow);
        return mwindow;
    }
}
