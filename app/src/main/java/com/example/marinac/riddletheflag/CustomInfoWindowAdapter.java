package com.example.marinac.riddletheflag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

/**
 * Created by Teodor on 31/03/2018.
 */

public class CustomInfoWindowAdapter implements InfoWindowAdapter {
    protected final View mWindow;
    protected Context mContext;
    protected CustomInfoWindowAdapter(Context context)
    {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null);
    }

    private void RenderWindowText(Marker marker, View view)
    {
        String title = marker.getTitle();
        TextView tvTitle = (TextView)view.findViewById(R.id.title);
        if(!title.equals("")){
            tvTitle.setText(title);
        }
        String snippet = marker.getSnippet();
        TextView tvSnippet = (TextView)view.findViewById(R.id.snippet);
        if(!snippet.equals("")){
            tvSnippet.setText(snippet);
        }
    }


    @Override
    public View getInfoWindow(Marker marker) {
        RenderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        RenderWindowText(marker, mWindow);
        return mWindow;
    }
}
