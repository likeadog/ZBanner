package com.zhuang.zbanner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

public class BlankFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public BlankFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BlankFragment newInstance(String url, int position) {
        BlankFragment fragment = new BlankFragment();
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
        imageView.setImageURI(url);
        TextView textView = rootView.findViewById(R.id.textView);
        textView.setText(getArguments().getInt("position")+"");
        return rootView;
    }
}
