package com.roger.screenlocker.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.roger.screenlocker.HomeActivity;
import com.roger.screenlocker.R;
import com.roger.screenlocker.utils.PullSeparateListView;
import com.roger.screenlocker.utils.switchbutton.SwitchButton;

public class MenuFragment extends Fragment {

    public View mView;
    private PullSeparateListView sListView;
    private Context sContext;
    private String[] sNewsList;
    private int[] sImageId;
    private View mHeaderView;
    public static boolean isOpen;
    private HomeActivity mHomeActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_menu, null);
        mHeaderView = inflater.inflate(R.layout.menu_list_header, null);
        return mView;
    }


    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sContext = getActivity();
        sNewsList = getResources().getStringArray(R.array.menu_names);
        sListView = (PullSeparateListView) mView.findViewById(R.id.xListView);
        sListView.addHeaderView(mHeaderView);
        sListView.setAdapter(new MenuAdapter());
        sListView.setSeparateAll(true);
        sImageId = new int[] { R.drawable.menu_1, R.drawable.menu_2,
                R.drawable.menu_3, R.drawable.menu_face, R.drawable.menu_4,
                R.drawable.menu_5, R.drawable.menu_6 };
        mHomeActivity = (HomeActivity) getActivity();
        isOpen = mHomeActivity.localSharedPreferences.getBoolean(
                mHomeActivity.PREFS_IS_OPEN, false);
    }


    // the meat of switching the above fragment
    private void switchFragment(int index) {
        if (getActivity() == null) return;

        if (getActivity() instanceof HomeActivity) {
            HomeActivity ra = (HomeActivity) getActivity();
            ra.position = index;
            ra.closeMenu();
        }
    }


    private class MenuAdapter extends BaseAdapter {

        private LayoutInflater mInflater;


        public MenuAdapter() {
            mInflater = LayoutInflater.from(sContext);
        }


        @Override public int getCount() {
            return sNewsList.length;
        }


        @Override public Object getItem(int position) {
            return sNewsList[position];
        }


        @Override public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder h = null;
            if (convertView == null) {
                h = new Holder();
                convertView = mInflater.inflate(R.layout.menu_list_item, null);
                h.content = (TextView) convertView.findViewById(
                        R.id.tv_content);
                h.image = (ImageView) convertView.findViewById(R.id.tv_image);
                h.tv_linear = (LinearLayout) convertView.findViewById(
                        R.id.tv_linear);
                h.mSwitchButton = (SwitchButton) convertView.findViewById(
                        R.id.btn_isopen);
                convertView.setTag(h);
            }
            else {
                h = (Holder) convertView.getTag();
            }
            h.content.setText(sNewsList[position]);
            h.image.setBackgroundResource(sImageId[position]);
            final int m = position;
            h.tv_linear.setOnClickListener(new OnClickListener() {

                @Override public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    switchFragment(m);
                }
            });
            return convertView;
        }


        private class Holder {
            public TextView content;
            public ImageView image;
            public LinearLayout tv_linear;
            public SwitchButton mSwitchButton;
        }
    }
}
