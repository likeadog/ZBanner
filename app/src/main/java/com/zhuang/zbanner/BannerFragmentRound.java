package com.zhuang.zbanner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class BannerFragmentRound extends Fragment {

    private static final String RES_ID = "resId";
    private static final String POSITION = "position";

    public BannerFragmentRound() {
    }

    public static BannerFragmentRound newInstance(int resId, int position) {
        BannerFragmentRound fragment = new BannerFragmentRound();
        Bundle args = new Bundle();
        args.putInt(RES_ID, resId);
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_banner1, container, false);
        ImageView imageView = rootView.findViewById(R.id.imageView);
        int resId = getArguments().getInt(RES_ID);
        final int position = getArguments().getInt(POSITION);
        imageView.setImageResource(resId);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "click:position=" + position, Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }
}
