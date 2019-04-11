package com.lianer.core.contract.model;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lianer.common.base.QuickAdapter;
import com.lianer.core.R;
import com.lianer.core.contract.bean.MessageCenterBean;
import com.lianer.core.databinding.ActivityMessageCenterBinding;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.DBUtil;
import com.lianer.core.wallet.adapter.SlideMessageAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 消息中心逻辑处理
 */
public class MessageCenterModel implements View.OnClickListener {

    private Context mContext;
    public QuickAdapter quickAdapter;
    private int offset, freshState;
    private List<MessageCenterBean> mDataList;
    private List<Boolean> pollingBooleanList = new ArrayList<>();
    private ActivityMessageCenterBinding mMessageCenter;
    private SlideMessageAdapter slideMessageAdapter;
    private ImageView ivDelete;
    private LinearLayout llbottom;
    private Button btnCancel;
    private Button btnDelete;

    public MessageCenterModel(Context mContext, ActivityMessageCenterBinding messageCenter, List<MessageCenterBean> dataList) {
        this.mContext = mContext;
        this.mMessageCenter = messageCenter;
        this.mDataList = dataList;
    }

    public List<Boolean> getPollingBooleanList(){
        return pollingBooleanList;
    }

    public void initView(){
        llbottom = mMessageCenter.bottomLinear;
        ivDelete = mMessageCenter.ivDelete;
        btnCancel = mMessageCenter.btnCancel;
        btnDelete = mMessageCenter.btnDelete;

        ivDelete.setOnClickListener(this);
        llbottom.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    /**
     * 初始化刷新组件
     */
    public void initRefresh() {
        mMessageCenter.refreshLayout.setEnableLoadMore(true);
        mMessageCenter.refreshLayout.setOnRefreshListener(refreshlayout -> {
            offset = 0;
            freshState = 1;
            loadData();
        });
        mMessageCenter.refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            offset++;
            freshState = 2;
            loadData();
        });
    }

    /**
     * 初始化recyclerView
     */
    public void initRecyclerView() {
//        quickAdapter = new QuickAdapter<MessageCenterBean>(mDataList) {
//            @Override
//            public int getLayoutId(int viewType) {
//                return R.layout.item_message;
//            }
//
//            @SuppressLint("CheckResult")
//            @Override
//            public void convert(VH holder, MessageCenterBean messageCenterBean, int position) {
//                //设置时间
//                holder.setText(R.id.item_message_time, DateUtils.time(messageCenterBean.getTxCreateTime()));
//
//                //设置消息类型 打包中、打包成功、打包失败
//                switch (messageCenterBean.getPackingStatus()) {
//                    case 1:
//                        holder.setText(R.id.item_message_type, mContext.getString(R.string.packaging_success));
//                        holder.setTextColor(R.id.item_message_type, mContext.getResources().getColor(R.color.c8));
//                        break;
//                    case 2:
//                        holder.setText(R.id.item_message_type, mContext.getString(R.string.packaging_failure));
//                        holder.setTextColor(R.id.item_message_type, mContext.getResources().getColor(R.color.c11));
//                        break;
//                    default:
//                        holder.setText(R.id.item_message_type, mContext.getString(R.string.in_the_package));
//                        holder.setTextColor(R.id.item_message_type, mContext.getResources().getColor(R.color.c5));
//                        break;
//                }
//
//                //设置交易状态值
//                holder.setText(R.id.item_message_status, mContext.getString(ContractStatus.MESSAGE_STATUS[messageCenterBean.getTxStatusValue()]));
//
//
//                //设置交易hash
//                holder.setText(R.id.item_message_address, CommomUtil.splitWalletAddress(messageCenterBean.getTxHash()));
//
//                //设置条目的点击事件
//                holder.getView(R.id.item_message_main).setOnClickListener(v -> {
//                    //跳转到详情页面
//                    Intent intent = new Intent(mContext, TxRecordDetailAct.class);
//                    intent.putExtra("txType", mContext.getString(ContractStatus.MESSAGE_STATUS[messageCenterBean.getTxStatusValue()]));
//                    intent.putExtra("txHash", messageCenterBean.getTxHash());
//                    intent.putExtra("txPackingStatus", messageCenterBean.getPackingStatus());
//                    intent.putExtra("txTime", messageCenterBean.getTxCreateTime());
//                    intent.putExtra("navigateType", 1);
//                    intent.putExtra("transferAmount",messageCenterBean.getTransferAmount());
//                    intent.putExtra("tokenType",messageCenterBean.getTokenType());
//                    intent.putExtra("targetAddress",messageCenterBean.getTargetAddress());
////                    intent.putExtra("value",)
//                    mContext.startActivity(intent);
//                });
//
//                //轮询操作
//                //只有在进行中状态下才进行轮询
//                if (messageCenterBean.getPackingStatus() == 0) {
//                    try {
//                        new Thread(() -> PollingUtil.startPolling(mContext, pollingBooleanList, position, messageCenterBean.getTxHash(), messageCenterBean, new PollingUtil.OnUpdatePageData() {
//                            @Override
//                            public void onTxSuccess(MessageCenterBean centerBean) {
//                                mDataList.set(position, centerBean);
//                                quickAdapter.notifyDataSetChanged();
//                            }
//
//                            @Override
//                            public void onTxFailure(MessageCenterBean centerBean) {
//                                mDataList.set(position, centerBean);
//                                quickAdapter.notifyDataSetChanged();
//                            }
//                        })).start();
//                    }catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        //设置布局管理器
        mMessageCenter.recyclerView.setLayoutManager(layoutManager);
        slideMessageAdapter = new SlideMessageAdapter(mDataList, mContext,this);
        //设置Adapter
        mMessageCenter.recyclerView.setAdapter(slideMessageAdapter);
        //设置增加或删除条目的动画
        mMessageCenter.recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    /**
     * 加载列表数据
     */
    public void loadData() {

        Flowable.just(1)
                .map(integer -> DBUtil.queryByOffset(offset))//查询数据库消息列表数据
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<List<MessageCenterBean>>(mContext, freshState == 0) {
                    @Override
                    protected void success(List<MessageCenterBean> data) {
                        if (data == null) {
                            showContractEmptyView();
                            return;
                        }

                        if (freshState != 2) {
                            //有数据的话需要清空数据
                            if (mDataList.size() != 0) {
                                mDataList.clear();
                            }
                            if (pollingBooleanList.size() != 0) {
                                pollingBooleanList.clear();
                            }
                        }

                        //停止刷新
                        mMessageCenter.refreshLayout.finishRefresh();
                        //停止加载
                        mMessageCenter.refreshLayout.finishLoadMore();

                        if (data.size() != 0) {
                            mDataList.addAll(data);
                            for (int i = 0; i < mDataList.size(); i++) {
                                pollingBooleanList.add(false);
                            }
                            //显示列表，隐藏空布局
                            notifyListData();

                            //判断是否已无更多数据
                            if (data.size() < 20) {
                                mMessageCenter.refreshLayout.finishLoadMoreWithNoMoreData();
                            }
                        } else {
                            if (freshState != 2) {
                                //显示空布局，隐藏列表
                                showContractEmptyView();
                            } else {
                                mMessageCenter.refreshLayout.finishLoadMoreWithNoMoreData();
                            }
                        }
                    }

                    @Override
                    protected void failure(HLError error) {
                        showContractEmptyView();
                    }
                });
    }

    /**
     * 显示合约为空布局
     */
    private void showContractEmptyView() {
        mMessageCenter.refreshLayout.setVisibility(View.GONE);
        mMessageCenter.messageEmpty.setVisibility(View.VISIBLE);
    }

    /**
     * 刷新列表数据
     */
    private void notifyListData() {
        mMessageCenter.refreshLayout.setVisibility(View.VISIBLE);
        mMessageCenter.messageEmpty.setVisibility(View.GONE);
//        quickAdapter.notifyDataSetChanged();
        slideMessageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_delete:
                if (llbottom.getVisibility()== View.VISIBLE){
                    llbottom.setVisibility(View.GONE);
                    slideMessageAdapter.setBatchDelete(false);
                }else {
                    if (slideMessageAdapter.menuIsOpen()){
                        slideMessageAdapter.closeMenu();
                    }
                    int itemCount = slideMessageAdapter.getItemCount();
                    if (itemCount==0){
                        llbottom.setVisibility(View.GONE);
                        slideMessageAdapter.setBatchDelete(false);
                    }else {
                        llbottom.setVisibility(View.VISIBLE);
                        slideMessageAdapter.setBatchDelete(true);
                    }

                }
                break;
            case R.id.btn_cancel:
                llbottom.setVisibility(View.GONE);
                slideMessageAdapter.setBatchDelete(false);
                break;
            case R.id.btn_delete: //批量删除
                Map<Integer, MessageCenterBean> deleteMap = slideMessageAdapter.getDeleteMap();
                if (deleteMap==null||deleteMap.size()==0){
                    return;
                }
                slideMessageAdapter.batchDelete();
                llbottom.setVisibility(View.GONE);
                break;
        }
    }

}
