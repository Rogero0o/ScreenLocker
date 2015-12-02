package com.roger.screenlocker.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roger.screenlocker.BaseActivity;
import com.roger.screenlocker.HomeActivity;
import com.roger.screenlocker.R;
import com.roger.screenlocker.render.RendererFragment;
import com.roger.screenlocker.render.util.UriUtil;

/**
 * Created by Administrator on 2015/2/26.
 */
public abstract class BaseFragment extends Fragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    Context mContext;
    View mView;
    Typeface mContentFace;
    HomeActivity mHomeActivity;
    RendererFragment mRendererFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = this.getActivity();
        mView = inflater.inflate(getLayoutId(), null);
        mHomeActivity = (HomeActivity) getActivity();
        mContentFace = Typeface.createFromAsset(mContext.getAssets(),
                "font/cartoon.ttf");
        BaseActivity.localSharedPreferences.registerOnSharedPreferenceChangeListener(
                this);

        mRendererFragment = RendererFragment.createInstance(
                UriUtil.getImageAbsolutePath(getActivity(), BaseActivity.mUri));
        getFragmentManager().beginTransaction()
                            .add(R.id.frame_init, mRendererFragment)
                            .commit();
        initUI();
        return mView;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(BaseActivity.PREFS_IMAGE_PATH)) {
            mRendererFragment = null;
            mRendererFragment = RendererFragment.createInstance(
                    UriUtil.getImageAbsolutePath(getActivity(),
                            BaseActivity.mUri));
            getFragmentManager().beginTransaction()
                                .add(R.id.frame_init, mRendererFragment)
                                .commit();
        }
    }


    @Override public void onDestroy() {
        BaseActivity.localSharedPreferences.unregisterOnSharedPreferenceChangeListener(
                this);
        super.onDestroy();
    }


    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    abstract int getLayoutId();

    abstract void initUI();
}
