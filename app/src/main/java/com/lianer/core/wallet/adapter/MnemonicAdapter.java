package com.lianer.core.wallet.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lianer.core.R;

import java.util.List;

// ① 创建Adapter
public class MnemonicAdapter extends RecyclerView.Adapter<MnemonicAdapter.VH>{

    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder{
        public final TextView singleMnemonic;
        public VH(View v) {
            super(v);
            singleMnemonic = v.findViewById(R.id.singel_mnemonic);
        }
    }
    
    private List<String> mDatas;
    public MnemonicAdapter(List<String> data) {
        this.mDatas = data;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.singleMnemonic.setText(mDatas.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //item 点击事件
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mnemonic, null, false);
        return new VH(v);
    }
}