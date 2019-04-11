package com.lianer.core.market;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lianer.core.R;

public class BannerFragment extends Fragment {
    private ImageView imageView;
    private String imageUrl;
    private OnImageClick onImageClick;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_banner, null);
        imageView = rootView.findViewById(R.id.image);
        RequestOptions options = new RequestOptions()
                .error(R.drawable.ic_default_banner);
        Glide.with(getContext())
                .load(imageUrl)
                .apply(options)
                .into(imageView);

        imageView.setOnClickListener(view -> {
            if (onImageClick != null) {
                onImageClick.click();
            }
        });
        return rootView;
    }

    public void bindData(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public interface OnImageClick {
        void click();
    }

    public void setOnImageClick(OnImageClick onImageClick) {
        this.onImageClick = onImageClick;
    }
}
