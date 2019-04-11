package com.lianer.core.invest.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lianer.core.R;
import com.lianer.core.config.MortgageAssets;
import com.lianer.core.contract.ContractDetailAct;
import com.lianer.core.contract.bean.ContractResponse;
import com.lianer.core.invest.AllContractAct;

import org.web3j.utils.Convert;

import java.util.List;

import static com.lianer.core.app.Constants.CONTRACT_RESPONSE;

/**
 * 投资首页合约列表适配器
 */
public class InvestHomeAdapter extends RecyclerView.Adapter<InvestHomeAdapter.VH> {

    private final int VIEW_TYPE = 1;
//    private final int PROGRESS_VIEW = 2;
//    private final int IMAGE_VIEW = 3;

    private Context context;
    private List<ContractResponse> list;
    private AdapterView.OnItemClickListener clickListener;
    private String walletAddress;

    public InvestHomeAdapter(Context context, List<ContractResponse> list, AdapterView.OnItemClickListener clickListener) {
        this.context = context;
        this.list = list;
        this.clickListener = clickListener;
    }

    public void addOnItemClickListener(AdapterView.OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.size() == 11 && position == list.size() - 1) {
            return VIEW_TYPE;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE) {
            return VH.get(parent, R.layout.item_see_all_contract);
        } else {
            return VH.get(parent, R.layout.item_home_contract);
        }
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        if (list.size() == 11 && position == list.size() - 1) {
            initAllContract(holder);
        } else {
            setItemData(holder, list.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 设置recyclerView的item数据
     */
    private void setItemData(VH holder, ContractResponse data) {
        holder.setText(R.id.time_limit, data.getTimeLimit() + "");
        double interestRate = data.getInterestRate() / 10d;
        String interestRateStr = String.valueOf(interestRate);
        holder.setText(R.id.interest_rate, interestRate >= 1 ? interestRateStr.substring(0, interestRateStr.indexOf(".")) : interestRateStr);
        String mortageAssetsAmount = Convert.fromWei(data.getMortgageAssetsAmount(), Convert.Unit.ETHER).toString();
        if (mortageAssetsAmount.contains(".")) {
            mortageAssetsAmount = mortageAssetsAmount.substring(0, mortageAssetsAmount.indexOf("."));
        }
        holder.setText(R.id.mortgage_assets_amount, MortgageAssets.getTokenSymbol(context,data.getMortgageAssetsType()) + " " + mortageAssetsAmount);

        String priecToEth = Convert.fromWei(data.getPriceToEth(), Convert.Unit.ETHER).toString();
        if (priecToEth.contains(".")) {
            priecToEth = priecToEth.substring(0, priecToEth.indexOf("."));
        }
        holder.setText(R.id.price_to_eth, priecToEth + " ETH");

        String borrowAddress = data.getBorrowAddress();
        if (borrowAddress != null && borrowAddress.equalsIgnoreCase(walletAddress)) {//我的合约
            //借币额度
            holder.setText(R.id.investment_amount, Convert.fromWei(data.getBorrowAssetsAmount(), Convert.Unit.ETHER).toString());
            holder.setBackground(R.id.immediately_invest, R.drawable.gradient_orange_oval_btn);
            holder.setText(R.id.immediately_invest, context.getString(R.string.my_contract));
        } else {//投资合约
            //投资额度
            holder.setText(R.id.investment_amount, Convert.fromWei(data.getInvestmentAmount(), Convert.Unit.ETHER).toString());
            holder.setBackground(R.id.immediately_invest, R.drawable.gradient_oval_btn);
            holder.setText(R.id.immediately_invest, context.getString(R.string.now_invest));
        }
        //立即投资的点击事件处理
        holder.getView(R.id.immediately_invest).setOnClickListener(v -> {
            judgeWalletNavigate(data);

        });
        holder.getView(R.id.rl_item_home_contract).setOnClickListener(v -> judgeWalletNavigate(data));
    }

    /**
     * 初始化查看全部合约控件
     */
    private void initAllContract(VH holder) {
        LinearLayout seeAllContract = holder.getView(R.id.see_all_contract);
        seeAllContract.setOnClickListener(v -> {
            Intent intent = new Intent(context, AllContractAct.class);
            context.startActivity(intent);
        });
    }

    public static class VH extends RecyclerView.ViewHolder {
        private SparseArray<View> mViews;
        private View mConvertView;

        private VH(View v) {
            super(v);
            mConvertView = v;
            mViews = new SparseArray<>();
        }

        public static VH get(ViewGroup parent, int layoutId) {
            View convertView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            return new VH(convertView);
        }

        public <T extends View> T getView(int id) {
            View v = mViews.get(id);
            if (v == null) {
                v = mConvertView.findViewById(id);
                mViews.put(id, v);
            }
            return (T) v;
        }

        public void setText(int id, String value) {
            TextView textView = getView(id);
            textView.setText(value);
        }

        public void setTextColor(int id, int textColor) {
            TextView textView = getView(id);
            textView.setTextColor(textColor);
        }

        public void setImage(int id, int imgRes) {
            ImageView imageView = getView(id);
            imageView.setImageResource(imgRes);
            imageView.setVisibility(View.VISIBLE);
        }

        public void setBackground(int id, int backgroudRes) {
            TextView textView = getView(id);
            textView.setBackgroundResource(backgroudRes);
        }

    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    /**
     * 判读啊钱包跳转页面
     */
    private void judgeWalletNavigate(ContractResponse contractResponse) {
        Intent intent = new Intent(context, ContractDetailAct.class);
        intent.putExtra(CONTRACT_RESPONSE, contractResponse);
        context.startActivity(intent);
    }
}

