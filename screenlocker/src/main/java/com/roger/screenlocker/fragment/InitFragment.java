package com.roger.screenlocker.fragment;

import android.widget.TextView;
import com.roger.screenlocker.R;
import com.roger.screenlocker.utils.ShimmerFrameLayout;

public class InitFragment extends BaseFragment {
    private ShimmerFrameLayout mShimmerViewContainer;


    @Override int getLayoutId() {
        return R.layout.fragment_initsys;
    }


    @Override void initUI() {
        // TODO Auto-generated method stub
        initTextFont();
        mShimmerViewContainer = (ShimmerFrameLayout) mView.findViewById(
                R.id.shimmer_view_container);
    }


    private void initTextFont() {
        ((TextView) mView.findViewById(R.id.text_init1)).setTypeface(
                mContentFace);
        ((TextView) mView.findViewById(R.id.text_init2)).setTypeface(
                mContentFace);
        ((TextView) mView.findViewById(R.id.text_init3)).setTypeface(
                mContentFace);
    }


    @Override public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }


    @Override public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }
}
