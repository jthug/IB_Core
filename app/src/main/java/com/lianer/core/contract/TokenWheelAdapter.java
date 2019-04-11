package com.lianer.core.contract;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lianer.core.R;
import com.lianer.core.contract.bean.TokenMortgageBean;
import com.wx.wheelview.adapter.BaseWheelAdapter;

public class TokenWheelAdapter   extends BaseWheelAdapter<TokenMortgageBean> {
    private Context mContext;

    public TokenWheelAdapter(Context context) {
        mContext = context;
    }

    @Override
    protected View bindView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_mortgage_token, null);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.text);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(mList.get(position).getTokenAbbreviation());
        Glide.with(mContext).load(mList.get(position).getTokenImage()).into( viewHolder.imageView);
//        viewHolder.imageView.setImageResource(MortgageAssets.getTokenLogo(mContext,mList.get(position).getTokenType()));
        return convertView;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
