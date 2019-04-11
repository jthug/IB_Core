package com.lianer.core.wallet.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lianer.common.utils.DateUtils;
import com.lianer.core.R;
import com.lianer.core.config.ContractStatus;
import com.lianer.core.contract.bean.MessageCenterBean;
import com.lianer.core.contract.model.MessageCenterModel;
import com.lianer.core.custom.RecyclerItemView;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.DBUtil;
import com.lianer.core.utils.PollingUtil;
import com.lianer.core.utils.RecyclerUtils;
import com.lianer.core.wallet.TxRecordDetailAct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlideMessageAdapter extends RecyclerView.Adapter<SlideMessageAdapter.SlideHolder> implements RecyclerItemView.onSlidingButtonListener {

    private List<MessageCenterBean> datas;
    private Context context;
    private RecyclerItemView recyclerItemView;
    private MessageCenterModel messageCenterModel;
    private boolean batchDelete = false; //批量删除
    private Map<Integer,MessageCenterBean> deleteMap = new HashMap<>(); //被选中的需要批量删除的消息
    public SlideMessageAdapter(List<MessageCenterBean> datas,Context context,MessageCenterModel messageCenterModel) {
        this.datas = datas;
        this.context = context;
        this.messageCenterModel = messageCenterModel;
    }

    @NonNull
    @Override
    public SlideHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message_center, viewGroup, false);
        return new SlideHolder(view);
    }

    public Map<Integer,MessageCenterBean> getDeleteMap(){
        return deleteMap;
    }

    @Override
    public void onBindViewHolder(@NonNull SlideHolder slideHolder, int i) {
        slideHolder.rlLeft.getLayoutParams().width = RecyclerUtils.getScreenWidth(context);
        slideHolder.rlLeft.setSelected(batchDelete);
//        slideHolder.rlLeft.setEnabled(false);
        MessageCenterBean messageCenterBean = datas.get(i);
        if (batchDelete){
            slideHolder.ivSelect.setVisibility(View.VISIBLE);
            slideHolder.llSlide.setVisibility(View.GONE); //隐藏删除按钮
        }else {
            slideHolder.ivSelect.setVisibility(View.GONE);
            slideHolder.llSlide.setVisibility(View.VISIBLE); //显示删除按钮
        }
        //解决Item复用导致的状态错乱问题
        boolean containsKey1 = deleteMap.containsKey(i);
        if (containsKey1){
            slideHolder.ivSelect.setImageResource(R.drawable.ic_export_selected);
        }else {
            slideHolder.ivSelect.setImageResource(R.drawable.ic_operate_normal);
        }

        slideHolder.ivSelect.setOnClickListener(v->{
//            boolean selected = slideHolder.ivSelect.isSelected();
//            if (selected){
//                slideHolder.ivSelect.setImageResource(R.drawable.ic_export_selected);
//                slideHolder.ivSelect.setSelected(false);
//                deleteMap.put(i,messageCenterBean);
//            }else {
//                slideHolder.ivSelect.setImageResource(R.drawable.ic_operate_normal);
//                slideHolder.ivSelect.setSelected(true);
//                deleteMap.remove(i);
//            }
            boolean containsKey = deleteMap.containsKey(i);
            if (containsKey){
                slideHolder.ivSelect.setImageResource(R.drawable.ic_operate_normal);
                deleteMap.remove(i);
            }else {
                slideHolder.ivSelect.setImageResource(R.drawable.ic_export_selected);
                deleteMap.put(i,messageCenterBean);
            }
        });
        //设置时间
        slideHolder.tvMessageTime.setText(DateUtils.time(messageCenterBean.getTxCreateTime()));

        //设置消息类型 打包中、打包成功、打包失败
        switch (messageCenterBean.getPackingStatus()) {
            case 1:
                slideHolder.tvMessageType.setText(context.getString(R.string.packaging_success));
                slideHolder.tvMessageType.setTextColor(context.getResources().getColor(R.color.c8));
                slideHolder.tvDelete.setEnabled(true);
                break;
            case 2:
                slideHolder.tvMessageType.setText(context.getString(R.string.packaging_failure));
                slideHolder.tvMessageType.setTextColor(context.getResources().getColor(R.color.c11));
                slideHolder.tvDelete.setEnabled(true);
                break;
            default:
                slideHolder.tvMessageType.setText(context.getString(R.string.in_the_package));
                slideHolder.tvMessageType.setTextColor(context.getResources().getColor(R.color.c5));
                slideHolder.tvDelete.setEnabled(false);
                slideHolder.ivSelect.setVisibility(View.GONE);
                break;
        }

        //设置交易状态值
        slideHolder.tvMessageStatus.setText(context.getString(ContractStatus.MESSAGE_STATUS[messageCenterBean.getTxStatusValue()]));

        //设置交易hash
        slideHolder.tvMessageAddress.setText(CommomUtil.splitWalletAddress(messageCenterBean.getTxHash()));

        //设置条目的点击事件
        slideHolder.rlLeft.setOnClickListener(v -> {
            //判断是否有删除菜单打开
            if (menuIsOpen()) {
                closeMenu();//关闭菜单
                return;
            }

            //跳转到详情页面
            Intent intent = new Intent(context, TxRecordDetailAct.class);
            intent.putExtra("txType", context.getString(ContractStatus.MESSAGE_STATUS[messageCenterBean.getTxStatusValue()]));
            intent.putExtra("txHash", messageCenterBean.getTxHash());
            intent.putExtra("txPackingStatus", messageCenterBean.getPackingStatus());
            intent.putExtra("txTime", messageCenterBean.getTxCreateTime());
            intent.putExtra("navigateType", 1);
            intent.putExtra("transferAmount",messageCenterBean.getTransferAmount());
            intent.putExtra("tokenType",messageCenterBean.getTokenType());
            intent.putExtra("targetAddress",messageCenterBean.getTargetAddress());
//                    intent.putExtra("value",)
            context.startActivity(intent);
        });

        //点击删除
        slideHolder.tvDelete.setOnClickListener(v->{
            DBUtil.delete(messageCenterBean);
            datas.remove(i);
            notifyDataSetChanged();
        });

        //轮询操作
        //只有在进行中状态下才进行轮询
        if (messageCenterBean.getPackingStatus() == 0) {
            try {
                new Thread(() -> PollingUtil.startPolling(context, messageCenterModel.getPollingBooleanList(), i, messageCenterBean.getTxHash(), messageCenterBean, new PollingUtil.OnUpdatePageData() {
                    @Override
                    public void onTxSuccess(MessageCenterBean centerBean) {
//                        datas.set(i, centerBean);
                        datas.get(i).setPackingStatus(1);
                        Log.e("xxxxxx","打包状态："+centerBean.getPackingStatus());
                        notifyDataSetChanged();

                    }

                    @Override
                    public void onTxFailure(MessageCenterBean centerBean) {
//                        datas.set(i, centerBean);
                        datas.get(i).setPackingStatus(2);
                        notifyDataSetChanged();
                    }
                })).start();
            }catch (Exception e) {
                e.printStackTrace();
            }
//            //后台服务查询避免内存泄漏
//            boolean serviceRunning = CommomUtil.isServiceRunning(context, "com.lianer.nest.app.PollingService");
//            if (!serviceRunning){
//                Log.e("xxxxxx","hahaha");
//                context.startService(new Intent(context, PollingService.class));
//            }
        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void onMenuIsOpen(View view) {
        recyclerItemView = (RecyclerItemView) view;
    }

    @Override
    public void onDownOrMove(RecyclerItemView recycler) {
        if (menuIsOpen()){
            if (recyclerItemView!=recycler){
                closeMenu();
            }
        }
    }

    //关闭菜单
    public void closeMenu() {
        recyclerItemView.closeMenu();
        recyclerItemView = null;

    }

    // 判断是否有菜单打开
    public Boolean menuIsOpen() {
        if(recyclerItemView != null){
            return true;
        }
        return false;
    }

    public void setBatchDelete(boolean batchDelete){
        this.batchDelete = batchDelete;
        deleteMap.clear();
        notifyDataSetChanged();
    }

    /**
     * 批量删除
     */
    public void batchDelete(){
        if (deleteMap.size()>0){
            for (Map.Entry<Integer,MessageCenterBean> entry: deleteMap.entrySet()){
                DBUtil.delete(entry.getValue());
//                datas.remove(entry.getKey().intValue());
                datas.remove(entry.getValue());
            }
            batchDelete = false;
            deleteMap.clear();
            notifyDataSetChanged();

        }
    }


    class SlideHolder extends RecyclerView.ViewHolder{
        RelativeLayout rlLeft;
        TextView tvMessageTime;
        TextView tvMessageType;
        TextView tvMessageStatus;
        TextView tvMessageAddress;
        TextView tvDelete;
        ImageView ivSelect;
        View itemView;
        LinearLayout llSlide;
        public SlideHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            rlLeft = itemView.findViewById(R.id.rl_left);
            tvMessageTime=itemView.findViewById(R.id.item_message_time);
            tvMessageType = itemView.findViewById(R.id.item_message_type);
            tvMessageStatus = itemView.findViewById(R.id.item_message_status);
            tvMessageAddress = itemView.findViewById(R.id.item_message_address);
            tvDelete = itemView.findViewById(R.id.delete);
            ivSelect = itemView.findViewById(R.id.iv_select);
            llSlide = itemView.findViewById(R.id.slide);
            ((RecyclerItemView)itemView).setSlidingButtonListener(SlideMessageAdapter.this);
        }
    }


}
