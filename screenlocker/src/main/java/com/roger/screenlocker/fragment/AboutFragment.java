package com.roger.screenlocker.fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.roger.screenlocker.R;
import com.roger.screenlocker.utils.ShimmerFrameLayout;

public class AboutFragment extends BaseFragment {
    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    int getLayoutId() {
        return R.layout.fragment_about;
    }

    @Override
    void initUI() {
        // TODO Auto-generated method stub
        mShimmerViewContainer = (ShimmerFrameLayout) mView.findViewById(R.id.shimmer_view_container);
        mView.findViewById(R.id.about_linearlayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.rogerblog.cn/");
                Intent intent = new  Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();

    }


    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }

}
