package com.lianer.core.wallet.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lianer.core.R;
import com.lianer.core.wallet.bean.TokenProfileBean;

import java.util.List;

public class AssetsAdapter extends RecyclerView.Adapter<AssetsAdapter.VH>{

    private OnItemAssetsValue onItemAssetsValue;

    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder{
        public final ImageView tokenLogo;
        public final TextView assetsName, assets_amount;
        public VH(View v) {
            super(v);
            tokenLogo = v.findViewById(R.id.token_logo);
            assetsName = v.findViewById(R.id.assets_name);
            assets_amount = v.findViewById(R.id.assets_amount);
        }
    }

    private List<TokenProfileBean> mDatas;
    public AssetsAdapter(List<TokenProfileBean> data) {
        this.mDatas = data;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(VH holder, int position) {
        //logo图片显示
        holder.assetsName.setText(mDatas.get(position).getSymbol());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //item 点击事件
            }
        });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String balance = Web3jManager.getInstance().getBalance("0x65C18AE8Cc5DecfD092DEF023e951087d1cF4Dbf") + "";
//                if (onItemAssetsValue != null) {
//                    onItemAssetsValue.setItemAssets(holder.assets_amount, balance);
//                }
//            }
//        }).start();
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallet_assets, null, false);
        return new VH(v);
    }

    public interface OnItemAssetsValue {

        void setItemAssets(TextView textView, String balance);

    };

    public void setOnItemAssetsValue(OnItemAssetsValue onItemAssetsValue) {
        this.onItemAssetsValue = onItemAssetsValue;
    }
}