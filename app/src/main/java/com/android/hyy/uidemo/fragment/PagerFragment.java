package com.android.hyy.uidemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.uidemo.R;

/**
 * Created by hyy on 2016/3/18.
 */
public class PagerFragment extends Fragment {
    private String[] contents = new String[]{"第一页","第二页","第三页"};
    private int[] background = new int[]{R.mipmap.rain,R.mipmap.nt_snow,R.mipmap.hazy};
    private static final String ARG_PRAMA1 ="position";
    private int mPosition;

    public PagerFragment(){}
    public  static  PagerFragment newInstance(int position){
        PagerFragment pagerFragment = new PagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PRAMA1,position);
        pagerFragment.setArguments(bundle);
        return  pagerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mPosition = getArguments().getInt(ARG_PRAMA1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment, null);
        TextView textview = (TextView) view.findViewById(R.id.textview);
        ImageView image_background = (ImageView) view.findViewById(R.id.image_background);
        textview.setText(contents[mPosition]);
        image_background.setBackgroundResource(background[mPosition]);

        return view;
    }
}
