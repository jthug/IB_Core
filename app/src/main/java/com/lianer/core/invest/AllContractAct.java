package com.lianer.core.invest;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lianer.core.base.BaseActivity;
import com.lianer.common.utils.KLog;
import com.lianer.core.R;
import com.lianer.core.contract.bean.ContractResponse;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityAllContractBinding;
import com.lianer.core.invest.bean.ContractHomeRequest;
import com.lianer.core.invest.model.AllContractModel;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.HttpUtil;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.lianer.core.config.ContractStatus.ALL_CONTRACT;

/**
 * 全部合约
 */
public class AllContractAct extends BaseActivity {

    ActivityAllContractBinding allContractBinding;
    List<ContractResponse> dataList = new ArrayList<>();
    int offset, freshState;
    String orderby;//查询条件
    List<String> stateList = new ArrayList<>();
    List<String> mortgageAssetsTypeList = new ArrayList<>();
    AllContractModel contractModel = new AllContractModel(this);

    @Override
    protected void initViews() {
        allContractBinding = DataBindingUtil.setContentView(this, R.layout.activity_all_contract);
        initTitleBar();

        initClick();

        contractModel.initRecyclerView(allContractBinding.recyclerView, dataList);

        initFilterView();
        initRefresh();
    }

    /**
     * 初始化筛选视图
     */
    private void initFilterView() {
//        contractModel.initContractStatusRecyclerView(allContractBinding.include.contractStatus);
        contractModel.initAssetsTypeRecyclerView(allContractBinding.include.contractType);
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        allContractBinding.titlebar.showLeftDrawable();
        allContractBinding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightTextClick() {

            }

            @Override
            public void rightImgClick() {

            }
        });
    }

    boolean isSort = true, isInterestRate, isTimeLimit;
    /**
     * 初始化点击事件
     */
    private void initClick() {
        changeChooseStatus(allContractBinding.createDate);
        allContractBinding.createDate.setOnClickListener(v -> {
            changeChooseStatus(allContractBinding.createDate);
            changeSort(0);
            changeChooseData(isSort ? getString(R.string.contract_create_date_desc) : getString(R.string.contract_create_date_asc));
            hindDrawerLayout();
        });
        allContractBinding.interestRate.setOnClickListener(v -> {
            changeChooseStatus(allContractBinding.interestRate);
            changeSort(1);
            changeChooseData(isInterestRate ? getString(R.string.interest_rate_desc) : getString(R.string.interest_rate_asc));
            hindDrawerLayout();
        });
        allContractBinding.timeLimit.setOnClickListener(v -> {
            changeChooseStatus(allContractBinding.timeLimit);
            changeSort(2);
            changeChooseData(isTimeLimit ? getString(R.string.time_limit_asc) : getString(R.string.time_limit_desc));
            hindDrawerLayout();
        });
        allContractBinding.choose.setOnClickListener(v -> switchDrawerLayout());

        //筛选按钮（确定）
        allContractBinding.include.sure.setOnClickListener(v -> {
            hindDrawerLayout();

            if (mortgageAssetsTypeList.size() != 0) {
                mortgageAssetsTypeList.clear();
            }
//            stateList.addAll(contractModel.getStatusSelectedFilter());
            mortgageAssetsTypeList.addAll(contractModel.getTypeSelectedFilter());
            changeChooseData("");

        });
        //筛选按钮（重置）
        allContractBinding.include.reset.setOnClickListener(v -> {
//            hindDrawerLayout();

            //重置筛选为未选择状态
            contractModel.resetFilterData();
            //重新请求数据
//            if (stateList.size() != 0) {
//                stateList.clear();
//            }
            if (mortgageAssetsTypeList.size() != 0) {
                mortgageAssetsTypeList.clear();
            }
            changeChooseData("");

        });

    }

    /**
     * 改变排序规则
     */
    private void changeSort(int type) {
        Drawable downSelected = getResources().getDrawable(R.drawable.sort_choose_desc);
        downSelected.setBounds(0, 0, downSelected.getMinimumWidth(), downSelected.getMinimumHeight());
        Drawable upSelected = getResources().getDrawable(R.drawable.sort_choose_asc);
        upSelected.setBounds(0, 0, upSelected.getMinimumWidth(), upSelected.getMinimumHeight());

        switch (type) {
            case 0:
                isSort = !isSort;
                if (isInterestRate) {
                    isInterestRate = false;
                }
                if (isTimeLimit) {
                    isTimeLimit = false;
                }
                allContractBinding.createDate.setCompoundDrawables(null, null, isSort ? downSelected : upSelected, null);
                break;
            case 1:
                isInterestRate = !isInterestRate;
                if (isSort) {
                    isSort = false;
                }
                if (isTimeLimit) {
                    isTimeLimit = false;
                }
                allContractBinding.interestRate.setCompoundDrawables(null, null, isInterestRate ? downSelected : upSelected, null);
                break;
            case 2:
                isTimeLimit = !isTimeLimit;
                if (isSort) {
                    isSort = false;
                }
                if (isInterestRate) {
                    isInterestRate = false;
                }
                allContractBinding.timeLimit.setCompoundDrawables(null, null, isTimeLimit ? downSelected : upSelected, null);
                break;

        }

    }

    /**
     * 切换抽屉状态  显示/隐藏
     */
    private void switchDrawerLayout() {
        if (!allContractBinding.contractFilter.isDrawerOpen(allContractBinding.drawerLayout)) {
            allContractBinding.contractFilter.openDrawer(allContractBinding.drawerLayout);
        } else {
            allContractBinding.contractFilter.closeDrawer(allContractBinding.drawerLayout);
        }
    }

    /**
     * 隐藏抽屉栏
     */
    private void hindDrawerLayout() {
        if (allContractBinding.contractFilter.isDrawerOpen(allContractBinding.drawerLayout)) {
            allContractBinding.contractFilter.closeDrawer(allContractBinding.drawerLayout);
        }
    }

    /**
     * 根据筛选条件更新数据
     */
    private void changeChooseData(String queryCondition) {
        offset = 0;
        freshState = 0;
        if (!TextUtils.isEmpty(queryCondition)) {
            orderby = queryCondition;
        }
        initResponseData(initRequestBody());
    }

    /**
     * 改变筛选栏的状态
     */
    private void changeChooseStatus(TextView textView) {
        allContractBinding.createDate.setSelected(false);
        allContractBinding.interestRate.setSelected(false);
        allContractBinding.timeLimit.setSelected(false);

        Drawable unSelected = getResources().getDrawable(R.drawable.sort_no_choose);
        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        unSelected.setBounds(0, 0, unSelected.getMinimumWidth(), unSelected.getMinimumHeight());
        Drawable selected = getResources().getDrawable(R.drawable.sort_choose_desc);
        selected.setBounds(0, 0, selected.getMinimumWidth(), selected.getMinimumHeight());

        allContractBinding.createDate.setCompoundDrawables(null, null, unSelected, null);
        allContractBinding.interestRate.setCompoundDrawables(null, null, unSelected, null);
        allContractBinding.timeLimit.setCompoundDrawables(null, null, unSelected, null);

        textView.setCompoundDrawables(null, null, selected, null);
        textView.setSelected(true);
    }

    @Override
    protected void initData() {
        getContractDefaulData();
    }

    /**
     * 获取合约默认数据（为时间筛选状态）
     */
    private void getContractDefaulData() {
        offset = 0;
        orderby = getString(R.string.contract_create_date_desc);
        stateList.addAll(ALL_CONTRACT);
        initResponseData(initRequestBody());
    }

    /**
     * 初始化刷新组件
     */
    private void initRefresh() {
        allContractBinding.refreshLayout.setEnableAutoLoadMore(false);
        allContractBinding.refreshLayout.setOnRefreshListener(refreshlayout -> {
            offset = 0;
            freshState = 1;
            initResponseData(initRequestBody());
        });
        allContractBinding.refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            offset = dataList.size();
            freshState = 2;
            initResponseData(initRequestBody());
        });
    }

    /**
     * 加载请求参数
     */
    private String initRequestBody() {
        int count = 5;
        ContractHomeRequest homeRequest = new ContractHomeRequest();
        homeRequest.setContractStatusList(stateList);
        homeRequest.setMortgageAssetsTypeList(mortgageAssetsTypeList);
        homeRequest.setCount(count);
        homeRequest.setOffset(offset);
        homeRequest.setOrderby(orderby);
        Gson gson = new Gson();
        return gson.toJson(homeRequest);
    }

    /**
     * 获取响应数据
     */
    private void initResponseData(String jsonRequestParams) {
        KLog.i(jsonRequestParams);
        HttpUtil.getContractList(jsonRequestParams)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<List<ContractResponse>>() {
                    @Override
                    protected void success(List<ContractResponse> contractResponses) {
                        switch (freshState) {
                            case 0:
                                if (dataList.size() != 0) {
                                    dataList.clear();
                                }
                                if (contractResponses != null && contractResponses.size() != 0) {
                                    dataList.addAll(contractResponses);
                                } else {
//                                    showContractEmptyView();
                                }

                                contractModel.getQuickAdapter().notifyDataSetChanged();
                                break;
                            case 1:
                                if (dataList.size() != 0) {
                                    dataList.clear();
                                }
                                if (contractResponses != null && contractResponses.size() != 0) {
                                    dataList.addAll(contractResponses);
                                } else {

                                }

                                contractModel.getQuickAdapter().notifyDataSetChanged();
                                allContractBinding.refreshLayout.finishRefresh();
                                break;
                            case 2:
                                if (contractResponses != null && contractResponses.size() != 0) {
                                    dataList.addAll(contractResponses);
                                    allContractBinding.refreshLayout.finishLoadMore();
                                    contractModel.getQuickAdapter().notifyDataSetChanged();
                                } else {
                                    allContractBinding.refreshLayout.finishLoadMore();
                                    allContractBinding.refreshLayout.finishLoadMoreWithNoMoreData();
                                }
                                break;
                        }
                    }

                    @Override
                    protected void failure(HLError error) {
                        allContractBinding.refreshLayout.finishRefresh();
//                        showContractEmptyView();
                    }
                });
    }

    /**
     * 显示合约为空布局
     */
    private void showContractEmptyView() {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(100, 80));
        imageView.setImageResource(R.drawable.record_null);
        allContractBinding.refreshLayout.addView(imageView);
        allContractBinding.recyclerView.setVisibility(View.GONE);
    }

}
