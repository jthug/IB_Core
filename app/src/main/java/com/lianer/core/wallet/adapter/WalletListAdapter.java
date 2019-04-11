package com.lianer.core.wallet.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lianer.core.R;
import com.lianer.core.model.HLWallet;

import java.util.List;

/**
 * Author by JT
 * Create at 2019/3/1 11:08
 */
public class WalletListAdapter extends RecyclerView.Adapter<WalletListAdapter.WalletListViewHolder> {

    private static final int FOOTVIEW = 0;
    private static final int ITEMVIEW = 1;
    public static final int IMPORTORCREATE = 2;
    public static final int WALLETSETTING = 3;
    public static final int SWITCHWALLET = 4;
    private OnItemClickListener mOnItemClickListener;
    List<HLWallet> datas;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public WalletListAdapter(List<HLWallet> datas) {
        this.datas = datas;
        this.datas.add(new HLWallet());

    }

    public void refreshData() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WalletListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (i == ITEMVIEW) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_wallet_manager_list, viewGroup, false);
        } else if (i == FOOTVIEW) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_foot_wallet_manager_list, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_foot_wallet_manager_list, viewGroup, false);
        }

        return new WalletListViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletListViewHolder walletListViewHolder, int i) {
        int type = walletListViewHolder.type;
        HLWallet hlWallet = datas.get(i);
        walletListViewHolder.itemView.setSelected(hlWallet.isCurrent);
        if (type == ITEMVIEW) {
            String walletName = hlWallet.getWalletName();
            String address = hlWallet.getAddress();
            String s = dealWith(address);
            walletListViewHolder.tvWalletAddress.setText(s);
            walletListViewHolder.tvWalletName.setText(walletName);
            walletListViewHolder.ivDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(WALLETSETTING, i);
                }
            });

            walletListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(SWITCHWALLET, i);
                }
            });
        }

        if (type == FOOTVIEW) {
            walletListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(IMPORTORCREATE, -1);
                }
            });
        }

    }

    private String dealWith(String address) {
        String s;
        if (address.length() > 16) {
            String substrin1 = address.substring(0, 8);
            String substring2 = address.substring(address.length() - 8);
            s = substrin1 + "..." + substring2;
        } else {
            s = address;
        }
        return s;
    }

    @Override
    public int getItemViewType(int position) {
        if (datas == null || datas.size() == 0) {
            return super.getItemViewType(position);
        } else {
            if (position == datas.size() - 1) {
                return FOOTVIEW;
            } else {
                return ITEMVIEW;
            }

        }
    }

    @Override
    public int getItemCount() {
        if (datas != null) {
            return datas.size();
        }
        return 0;
    }

    static class WalletListViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDetail;
        TextView tvWalletName;
        TextView tvWalletAddress;
        int type;

        public WalletListViewHolder(@NonNull View itemView, int type) {
            super(itemView);
            this.type = type;
            if (type == ITEMVIEW) {
                ivDetail = itemView.findViewById(R.id.iv_detail);
                tvWalletName = itemView.findViewById(R.id.tv_wallet_name);
                tvWalletAddress = itemView.findViewById(R.id.tv_wallet_address);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int tag, int position);
    }
}
