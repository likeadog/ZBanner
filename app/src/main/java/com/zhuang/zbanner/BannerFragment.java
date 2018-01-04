package com.zhuang.zbanner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

public class BannerFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public BannerFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BannerFragment newInstance(String url, int position) {
        BannerFragment fragment = new BannerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_NUMBER, url);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        SimpleDraweeView imageView = rootView.findViewById(R.id.imageView);
        String url = getArguments().getString(ARG_SECTION_NUMBER);
        final int position = getArguments().getInt("position");
        imageView.setImageURI(url);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"click:position"+position,Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }
}
