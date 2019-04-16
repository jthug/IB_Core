package com.lianer.core.wallet;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lianer.common.utils.ACache;
import com.lianer.common.utils.KLog;
import com.lianer.common.utils.Utils;
import com.lianer.core.base.BaseFragment;
import com.lianer.common.base.QuickAdapter;
import com.lianer.common.utils.SPUtils;
import com.lianer.core.R;
import com.lianer.core.app.Constants;
import com.lianer.core.config.Constant;
import com.lianer.core.config.Tag;
import com.lianer.core.contract.MessageCenterAct;
import com.lianer.core.contract.bean.MessageCenterEventBean;
import com.lianer.core.custom.GlideRoundTransform;
import com.lianer.core.custom.RVItemDecoration;
import com.lianer.core.databinding.FragmentWalletBinding;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.SnackbarUtil;
import com.lianer.core.utils.TransferUtil;
import com.lianer.core.wallet.bean.EventBusBean;
import com.lianer.core.wallet.bean.TokenProfileBean;
import com.lianer.core.wallet.bean.WalletAddrEventBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 钱包页
 *
 * @author allison
 */
public class WalletFrag extends BaseFragment {

    private FragmentWalletBinding fragmentWalletBinding;
    private List<TokenProfileBean> tokenProfileBeans = new ArrayList<>();
    private QuickAdapter quickAdapter;

    @Override
    public void onResume() {
        super.onResume();

        //铃铛的图片
        if (ACache.get(getContext()).getAsString(Tag.IS_RED_NOTIFICATION) != null && ACache.get(getContext()).getAsString(Tag.IS_RED_NOTIFICATION).equals("true")) {
            fragmentWalletBinding.ivLeft.setImageResource(R.drawable.notification_red_dot_selector);
        } else {
            fragmentWalletBinding.ivLeft.setImageResource(R.drawable.notification_selector);
        }

        if (quickAdapter != null) {
            initAssetsData();
        }
        initHead();
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentWalletBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet, container, false);

        fragmentWalletBinding.ivLeft.setOnClickListener(v -> {
            //点击之后铃铛取消红点
            fragmentWalletBinding.ivLeft.setImageResource(R.drawable.notification_selector);
            //把本地消息红点状态数据修改为false
            ACache.get(getContext()).put(Tag.IS_RED_NOTIFICATION, "false");
            Intent intent = new Intent(getContext(), MessageCenterAct.class);
            startActivity(intent);
        });
        fragmentWalletBinding.setting.setOnClickListener(v -> navigateToWalletManager());

        fragmentWalletBinding.llMore.setOnClickListener(v->navigateToWalletList());

        initHead();

        return fragmentWalletBinding.getRoot();
    }

    private void initHead() {
        //二维码
        fragmentWalletBinding.QRcodeScan.setOnClickListener(v -> {
//            // 创建IntentIntegrator对象
//            IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
//            // 开始扫描
//            intentIntegrator.initiateScan();
            startActivity(new Intent(getActivity(), WalletAddressActivity.class));
        });
        //查看收益
        fragmentWalletBinding.viewEarnings.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), IncomeActivity.class));
        });
        //钱包地址
        fragmentWalletBinding.walletAddress.setOnClickListener(v -> {
            SnackbarUtil.DefaultSnackbar(fragmentWalletBinding.getRoot(), getActivity().getString(R.string.copy_success)).show();
            CommomUtil.copyText(getContext(), HLWalletManager.shared().getCurrentWallet(getContext()).getAddress());
        });
        //添加资产
        fragmentWalletBinding.addAssets.setOnClickListener(v -> navigateToAssetsList());
        //设置当前地址
        fragmentWalletBinding.walletAddress.setText(CommomUtil.splitWalletAddress(HLWalletManager.shared().getCurrentWallet(getContext()).getAddress()));
        //设置当前钱包名称
        fragmentWalletBinding.tvWalletName.setText(HLWalletManager.shared().getCurrentWallet(getContext()).getWalletName());
    }

    private void navigateToWalletList() {
        Intent intent = new Intent(getContext(), WalletManagerListAct.class);
        startActivity(intent);
    }

    @Override
    protected void initData() {
        super.initData();

        initRecyclerView();
    }

    /**
     * 初始化recyclerView
     */
    private void initRecyclerView() {
        quickAdapter = new QuickAdapter<TokenProfileBean>(tokenProfileBeans) {

            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_wallet_assets;
            }

            @Override
            public void convert(VH holder, TokenProfileBean data, int position) {
                //代币简称
                String symbol = data.getSymbol();
                //当前钱包地址
                String walletAddress = HLWalletManager.shared().getCurrentWallet(getContext()).getWalletFile().getAddress();

//                //代币logo
//                ImageView tokenLogo = holder.getView(R.id.token_logo);
//                if (symbol.equalsIgnoreCase(Constants.ASSETS_ETH)) {
//                    tokenLogo.setImageResource(R.drawable.ic_eth);
//                }
//
//
//                //TODO nest代币
//                else if (symbol.equalsIgnoreCase(Constants.ASSETS_NEST)) {
//                    tokenLogo.setImageResource(R.drawable.ic_nest);
//                }
//                else {
//                    RequestOptions options = new RequestOptions()
//                            .placeholder(R.drawable.ic_default_eth)
//                            .transform(new GlideRoundTransform(getContext(), (int) getResources().getDimension(R.dimen.dp_20)));
//                    Glide.with(getContext())
//                            .load(Constants.ASSET_IMAGE_ADDRESS + data.getAddress() + Constants.PNG)
//                            .apply(options)
//                            .into(tokenLogo);
////                            .transform(new GlideRoundTransform(getContext(), (int) getResources().getDimension(R.dimen.dp_20))).into(tokenLogo);
//                }

                //代币logo
                ImageView tokenLogo = holder.getView(R.id.token_logo);
                if (symbol.equalsIgnoreCase("HPB")) {
                    tokenLogo.setImageResource(R.drawable.ic_default_eth);
                }


                //TODO nest代币
                else if (symbol.equalsIgnoreCase("ZXF")) {
                    tokenLogo.setImageResource(R.drawable.ic_default_eth);
                }else if (symbol.equalsIgnoreCase("HHQ")){
                    tokenLogo.setImageResource(R.drawable.ic_default_eth);
                } else {
                    RequestOptions options = new RequestOptions()
                            .placeholder(R.drawable.ic_default_eth)
                            .transform(new GlideRoundTransform(getContext(), (int) getResources().getDimension(R.dimen.dp_20)));
                    Glide.with(getContext())
                            .load(Constants.ASSET_IMAGE_ADDRESS + data.getAddress() + Constants.PNG)
                            .apply(options)
                            .into(tokenLogo);
//                            .transform(new GlideRoundTransform(getContext(), (int) getResources().getDimension(R.dimen.dp_20))).into(tokenLogo);
                }



                //代币名称
                holder.setText(R.id.assets_name, data.getSymbol());


                switch (position % 4) {
                    case 0:
                        holder.getView(R.id.token_bg).setBackgroundResource(R.drawable.ic_wallet_n_bg);
                        break;
                    case 1:
                        holder.getView(R.id.token_bg).setBackgroundResource(R.drawable.ic_wallet_e_bg);
                        break;
                    case 2:
                        holder.getView(R.id.token_bg).setBackgroundResource(R.drawable.ic_wallet_s_bg);
                        break;
                    case 3:
                        holder.getView(R.id.token_bg).setBackgroundResource(R.drawable.ic_wallet_t_bg);
                        break;
                }

                //币种条目点击事件
                holder.getView(R.id.rl_wallet_assets).setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), AssetsDetailAct.class);
                    if (symbol.equalsIgnoreCase(Constants.ASSETS_ETH)) {
                        intent.putExtra(Constants.ASSETS_LOGO, Constants.ASSETS_ETH);
                    } else {
                        intent.putExtra(Constants.ASSETS_LOGO, data.getAddress());
                    }
                    intent.putExtra(Constants.ASSETS_NAME, data.getSymbol());
                    intent.putExtra(Constants.ASSETS_AMOUNT, data.getAssets());
                    if (data.getSymbol().equalsIgnoreCase(Constants.ASSETS_ETH)) {
                        intent.putExtra(Constants.ASSETS_ADDRESS, HLWalletManager.shared().getCurrentWallet(getContext()).getAddress());
                    } else {
                        intent.putExtra(Constants.ASSETS_ADDRESS, data.getAddress());
                    }
                    startActivity(intent);
                });

                if(data.getTokenBalance() != null) {
                    //币种条目的币值获取
                    holder.setText(R.id.assets_amount, Utils.switchTo4Decimal(data.getTokenBalance()));
                }
                //币种条目的币值获取
                Flowable.just(walletAddress)
                        .map(s -> {
//                            if (symbol.equalsIgnoreCase(Constants.ASSETS_ETH)) {
//                                return TransferUtil.getEthBanlance(TransferUtil.getWeb3j(), Constant.PREFIX_16 + walletAddress);
//                            }
                            if (symbol.equalsIgnoreCase("hpb")) {
                                return TransferUtil.getEthBanlance(TransferUtil.getWeb3j(), Constant.PREFIX_16 + walletAddress);
                            }
                            return TransferUtil.getTokenBalance(TransferUtil.getWeb3j(), Constant.PREFIX_16 + walletAddress, data.getAddress());
//                            return TransferUtil.getTokenBalance(TransferUtil.getWeb3j(), Constant.PREFIX_16 + walletAddress, "0x0361afd753b74937bb7adefcfbc0ae9b831a3524");
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new HLSubscriber<String>() {
                            @Override
                            protected void success(String balance) {
                                String tokenBalance = Utils.switchTo4Decimal(balance);
                                data.setTokenBalance(tokenBalance);
                                holder.setText(R.id.assets_amount, Utils.switchTo4Decimal(balance));
                            }

                            @Override
                            protected void failure(HLError error) {
                                KLog.i(error.getMessage());
                            }
                        });

            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //设置布局管理器
        fragmentWalletBinding.rvWalletAssets.setLayoutManager(layoutManager);
        fragmentWalletBinding.rvWalletAssets.addItemDecoration(new RVItemDecoration(LinearLayout.VERTICAL, 30));
        //设置Adapter
        fragmentWalletBinding.rvWalletAssets.setAdapter(quickAdapter);
        //设置增加或删除条目的动画
        fragmentWalletBinding.rvWalletAssets.setItemAnimator(new DefaultItemAnimator());

        initAssetsData();
    }

    /**
     * 初始化已添加的资产
     */
    private void initAssetsData() {
        if (tokenProfileBeans.size() != 0) {
            tokenProfileBeans.clear();
        }
        List<TokenProfileBean> tempTokenProfileBeanList = new ArrayList<>();
        TokenProfileBean ethTokenProfileBean = null;
        //TODO nest代币
        TokenProfileBean nestTokenProfileBean = null;
        //hhq
        TokenProfileBean hhqProfileBean = null;
        for (TokenProfileBean tokenProfileBean : Constants.tokenProfileBeans) {
            if (tokenProfileBean.getSymbol().equalsIgnoreCase("HHQ"))
            KLog.e("symbol="+tokenProfileBean.getSymbol());
//            //固定添加ETH
//            if (tokenProfileBean.getSymbol().equalsIgnoreCase(Constants.ASSETS_ETH)) {
//                ethTokenProfileBean = tokenProfileBean;
//            }
//            //TODO nest代币
//            else if (tokenProfileBean.getSymbol().equalsIgnoreCase(Constants.ASSETS_NEST)) {
//                nestTokenProfileBean = tokenProfileBean;
//            }
//            else {
//                if (SPUtils.getInstance().getBoolean(tokenProfileBean.getSymbol())) {
//                    tempTokenProfileBeanList.add(tokenProfileBean);
//                }
//            }

            //固定添加hpb
            if (tokenProfileBean.getSymbol().equalsIgnoreCase("HPB")) {
                ethTokenProfileBean = tokenProfileBean;
            }
            //TODO zxf代币
            else if (tokenProfileBean.getSymbol().equalsIgnoreCase("ZXF")) {
                nestTokenProfileBean = tokenProfileBean;
            }else if (tokenProfileBean.getSymbol().equalsIgnoreCase("HHQ")){ //hhq
                hhqProfileBean = tokenProfileBean;
            } else {
                if (SPUtils.getInstance().getBoolean(tokenProfileBean.getSymbol())) {
                    tempTokenProfileBeanList.add(tokenProfileBean);
                }
            }


        }

        //置顶eth及nest
        tokenProfileBeans.add(ethTokenProfileBean);
        //TODO nest代币
        tokenProfileBeans.add(nestTokenProfileBean);
        //hhq
        tokenProfileBeans.add(hhqProfileBean);
        if (tempTokenProfileBeanList.size() != 0) {
            //对已添加资产进行排序
            Collections.sort(tempTokenProfileBeanList);
            //加入列表
            tokenProfileBeans.addAll(tempTokenProfileBeanList);
        }

        quickAdapter.notifyDataSetChanged();
    }

    /**
     * 跳转到资产列表页
     */
    private void navigateToAssetsList() {
        Intent intent = new Intent(getContext(), AddTokenTypeAct.class);
        startActivity(intent);
    }

    /**
     * 跳转到钱包管理页
     */
    private void navigateToWalletManager() {
        Intent intent = new Intent(getContext(), SettingAct.class);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void walletAddrEvent(WalletAddrEventBean event) {
        fragmentWalletBinding.walletAddress.setText(HLWalletManager.shared().getCurrentWallet(getContext()).getAddress());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void assetsEvent(EventBusBean event) {
        if (event.getEventBusTag() == 0) {
            initAssetsData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEvent(MessageCenterEventBean event) {
        if (fragmentWalletBinding != null) {
            fragmentWalletBinding.ivLeft.setImageResource(R.drawable.notification_red_dot_selector);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (tokenProfileBeans.size() != 0) {
            tokenProfileBeans.clear();
        }
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

}
