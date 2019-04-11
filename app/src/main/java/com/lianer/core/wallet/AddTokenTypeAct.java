package com.lianer.core.wallet;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lianer.common.utils.KLog;
import com.lianer.core.base.BaseActivity;
import com.lianer.common.base.QuickAdapter;
import com.lianer.common.utils.SPUtils;
import com.lianer.core.R;
import com.lianer.core.app.Constants;
import com.lianer.core.custom.GlideRoundTransform;
import com.lianer.core.databinding.ActivityAddTokenTypeBinding;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.wallet.bean.EventBusBean;
import com.lianer.core.wallet.bean.TokenProfileBean;
import com.suke.widget.SwitchButton;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 增加币种
 *
 * @author allison
 */
public class AddTokenTypeAct extends BaseActivity {

    private ActivityAddTokenTypeBinding addTokenTypeBinding;
    private List<TokenProfileBean> tokenProfileBeans = new ArrayList<>();
    private List<TokenProfileBean> tempFilterTokenProfileBeans = new ArrayList<>();
    private List<TokenProfileBean> allTempTokenProfileBeans = new ArrayList<>();//所有数据的临时备份
    private QuickAdapter quickAdapter;


    @Override
    protected void initViews() {
        initSearchLayout();

        initRecyclerView();
    }

    /**
     * 初始化搜索布局相关控件
     */
    private void initSearchLayout() {

        addTokenTypeBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_token_type);
        addTokenTypeBinding.searchBack.setOnClickListener(v -> finish());
        addTokenTypeBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getSearchData(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
//        addTokenTypeBinding.etSearch.clearFocus();//取消焦点
//        InputMethodManager imm = (InputMethodManager) AddTokenTypeAct.this
//                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.hideSoftInputFromWindow(addTokenTypeBinding.etSearch.getWindowToken(), 0);
//        }
    }

    /**
     * 获取搜索数据
     */
    private void getSearchData(CharSequence s) {

        //判断搜索的字符串是否符合钱包地址类型
        if (s.toString().toLowerCase().contains("0x")) {
            for (TokenProfileBean tokenProfileBean : allTempTokenProfileBeans) {
                //根据钱包地址筛选符合的数据
                if (tokenProfileBean.getAddress().toLowerCase().contains(s.toString().toLowerCase())) {
                    tempFilterTokenProfileBeans.add(tokenProfileBean);
                }
            }
        } else {
            for (TokenProfileBean tokenProfileBean : allTempTokenProfileBeans) {
                //根据简称筛选符合的数据
                if (tokenProfileBean.getSymbol().toLowerCase().contains(s.toString().toLowerCase())) {
                    tempFilterTokenProfileBeans.add(tokenProfileBean);
                }
            }
        }

        KLog.i(tempFilterTokenProfileBeans.size() + tempFilterTokenProfileBeans.toString());

        int searchResult = tempFilterTokenProfileBeans.size();
        addTokenTypeBinding.rvTokenType.setVisibility(searchResult == 0 ? View.GONE : View.VISIBLE);
        addTokenTypeBinding.searchEmpty.setVisibility(searchResult == 0 ? View.VISIBLE : View.GONE);
        if (searchResult != 0) {
            if (tokenProfileBeans.size() != 0) {
                tokenProfileBeans.clear();
            }
            tokenProfileBeans.addAll(tempFilterTokenProfileBeans);
            if (tempFilterTokenProfileBeans.size() != 0) {
                tempFilterTokenProfileBeans.clear();
            }
            quickAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void initData() {
        initAssetsList();
    }

    /**
     * 初始化recyclerView
     */
    private void initRecyclerView() {
        quickAdapter = new QuickAdapter<TokenProfileBean>(tokenProfileBeans) {

            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_token_type;
            }

            @Override
            public void convert(VH holder, TokenProfileBean data, int position) {
                //加载资产图标
                ImageView tokenLogo = holder.getView(R.id.token_logo);
                if (data.getSymbol().equalsIgnoreCase(Constants.ASSETS_ETH)) {
                    tokenLogo.setImageResource(R.drawable.ic_eth);

                }
                //TODO nest代币
                else if (data.getSymbol().equalsIgnoreCase(Constants.ASSETS_NEST)) {
                    tokenLogo.setImageResource(R.drawable.ic_nest);
                }
                else {
                    RequestOptions options = new RequestOptions()
                            .placeholder(R.drawable.ic_default_eth)
                            .transform(new GlideRoundTransform(AddTokenTypeAct.this, (int) getResources().getDimension(R.dimen.dp_20)));
                    Glide.with(AddTokenTypeAct.this)
                            .load(Constants.ASSET_IMAGE_ADDRESS + data.getAddress() + Constants.PNG)
                            .apply(options)
                            .into(tokenLogo);
                }




                //设置资产简称
                holder.setText(R.id.token_symbol, data.getSymbol());
                //设置资产名称
                holder.setText(R.id.token_name, String.format("（%s）", data.getSymbol()));
                //设置资产地址
                holder.setText(R.id.token_address, CommomUtil.splitWalletAddress(data.getAddress()));

                SwitchButton switchButton = holder.getView(R.id.switch_button);
                //判断资产是否是eth
                if (data.getSymbol().equalsIgnoreCase("eth")) {
                    switchButton.setVisibility(View.GONE);
                } else if (data.getSymbol().equalsIgnoreCase("nest")) {
                    switchButton.setVisibility(View.GONE);
                } else {
                    switchButton.setVisibility(View.VISIBLE);
                    switchButton.setChecked(SPUtils.getInstance().getBoolean(data.getSymbol(), false));
                }

                switchButton.setOnCheckedChangeListener((view, isChecked) -> {
                    switchButton.setChecked(isChecked);
                    SPUtils.getInstance().put(data.getSymbol(), isChecked);
                    EventBus.getDefault().post(new EventBusBean(0));
                });
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        addTokenTypeBinding.rvTokenType.setLayoutManager(layoutManager);
        //设置Adapter
        addTokenTypeBinding.rvTokenType.setAdapter(quickAdapter);
        //设置增加或删除条目的动画
        addTokenTypeBinding.rvTokenType.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 初始化资产列表
     */
    private void initAssetsList() {

        //已选资产临时数据
        List<TokenProfileBean> tempSelectedAssets = new ArrayList<>();
        //未选资产临时数据
        List<TokenProfileBean> tempUnselectAssets = new ArrayList<>();
        //eth资产临时数据
        final TokenProfileBean[] ethTokenProfileBean = {null};
        final TokenProfileBean[] nestTokenProfileBean = {null};

        Flowable.just(1)
                .map((Function<Integer, Object>) integer -> {
                    for (TokenProfileBean tokenProfileBean : Constants.tokenProfileBeans) {
                        if (tokenProfileBean.getSymbol().equalsIgnoreCase(Constants.ASSETS_ETH)) {
                            ethTokenProfileBean[0] = tokenProfileBean;
                            ethTokenProfileBean[0].setAddress(HLWalletManager.shared().getCurrentWallet(AddTokenTypeAct.this).getAddress());
                        }
                        //TODO nest代币
                        else if (tokenProfileBean.getSymbol().equalsIgnoreCase(Constants.ASSETS_NEST)) {
                            nestTokenProfileBean[0] = tokenProfileBean;
                        }
                        else {
                            if (!SPUtils.getInstance().getBoolean(tokenProfileBean.getSymbol(), false)) {
                                tempSelectedAssets.add(tokenProfileBean);
                            } else {
                                tempUnselectAssets.add(tokenProfileBean);
                            }
                        }


                    }

                    //对已选资产进行排序
                    Collections.sort(tempSelectedAssets);
                    //对未选资产进行排序
                    Collections.sort(tempUnselectAssets);

                    //置顶eth、nest
                    allTempTokenProfileBeans.add(ethTokenProfileBean[0]);
                    //TODO nest代币
                    allTempTokenProfileBeans.add(nestTokenProfileBean[0]);
                    //添加未选资产
                    allTempTokenProfileBeans.addAll(tempUnselectAssets);
                    //添加已选资产
                    allTempTokenProfileBeans.addAll(tempSelectedAssets);
                    tokenProfileBeans.addAll(allTempTokenProfileBeans);
                    return integer;
                })
                .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new HLSubscriber<Object>(AddTokenTypeAct.this, true) {
            @Override
            protected void success(Object data) {
                //更新列表数据
                quickAdapter.notifyDataSetChanged();
            }

            @Override
            protected void failure(HLError error) {
                KLog.i(error.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tokenProfileBeans.size() > 0) tokenProfileBeans.clear();
        if (tempFilterTokenProfileBeans.size() > 0) tempFilterTokenProfileBeans.clear();
        if (allTempTokenProfileBeans.size() > 0) allTempTokenProfileBeans.clear();
    }

}
