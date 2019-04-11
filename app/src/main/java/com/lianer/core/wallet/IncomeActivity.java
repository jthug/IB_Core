package com.lianer.core.wallet;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.TextView;
import com.google.gson.Gson;
import com.lianer.common.utils.KLog;
import com.lianer.common.utils.language.LanguageType;
import com.lianer.common.utils.language.MultiLanguageUtil;
import com.lianer.core.R;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.app.Constants;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.borrow.BannerDetailAct;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityEarningsBinding;
import com.lianer.core.etherscan.EtherScanWebActivity;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.HttpUtil;
import com.lianer.core.utils.TransferUtil;
import com.lianer.core.wallet.bean.ValueResponse;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 收益
 *
 * @author bowen
 */
public class IncomeActivity extends BaseActivity implements View.OnClickListener {

    private ActivityEarningsBinding mBinding;
    private HLWallet mWallet;
    //矿池挖矿数量衰减X
    private double paramX;
    //矿池单位时间衰减
    private double paramY;
    //抵押借贷挖矿时间衰减* 单笔借贷量衰减
    private double paramBN;
    //保留四位
    DecimalFormat fourFormat = new DecimalFormat("0.####");

    //参与分红nest
    private double totalDividendNest ;
    //ETH分红总额
    private double totalEthDividend ;
    //我的分红
    private Double myDividend;
    //我的挖矿
    private Double myMining;
    @Override
    protected void initViews() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_earnings);
        mBinding.titlebar.showLeftDrawable();
        mBinding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
            @Override
            public void leftClick() {
                onBackPressed();
            }

            @Override
            public void rightTextClick() {

            }

            @Override
            public void rightImgClick() {

            }
        });
        mWallet = HLWalletManager.shared().getCurrentWallet(this);
        getDividendInfo();
        getMinePoolInfo();
        queryServer();
        fillAddress(mBinding.nestTokenAddress,Constants.ASSETS_NEST_ADDRESS);
        mBinding.myDividend.setOnClickListener(this);
        mBinding.myMining.setOnClickListener(this);
        mBinding.nestMining.setOnClickListener(this);
        mBinding.btnEarnDetail.setOnClickListener(this);
    }

    @Override
    protected void initData() {


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.my_mining:
                Intent intentMining = new Intent(this,IncomeDetailActivity.class);
                intentMining.putExtra("Tpye","Mining");
                intentMining.putExtra("tokenValue",myMining);
                startActivity(intentMining);
                break;

            case R.id.my_dividend:
                Intent intentDividend = new Intent(this,IncomeDetailActivity.class);
                intentDividend.putExtra("Tpye","Dividend");
                intentDividend.putExtra("tokenValue",myDividend);
                startActivity(intentDividend);
                break;

            //Nest挖矿机制
            case R.id.nest_mining:
                Intent intent = new Intent(this, BannerDetailAct.class);
                if (MultiLanguageUtil.getInstance().getLanguageType() == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
                    intent.putExtra("webUrl", Constants.NEST_MINING_CHINESE);
                } else {
                    intent.putExtra("webUrl", Constants.NEST_MINING_ENGLISH);
                }
                startActivity(intent);
                break;
            case R.id.btn_earn_detail:
                Intent earnIntent = new Intent(this, MyEarningActivity.class);
                startActivity(earnIntent);
                break;
        }

    }


    private void getDividendInfo(){
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getDividendInfo(TransferUtil.getWeb3j(), mWallet.getAddress()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String[]>(IncomeActivity.this, false) {
                    @Override
                    protected void success(String[] dividendInfo) {

                        KLog.w("分红合约 :"+dividendInfo[0]);
                        fillAddress( mBinding.dividendContractAddress,dividendInfo[0]);

                        KLog.w("当前分红池金额 :"+dividendInfo[1]);
                        totalEthDividend = Convert.fromWei( dividendInfo[1], Convert.Unit.ETHER).doubleValue();
                        mBinding.dividingPool.setText(uniteduction(totalEthDividend,"ETH"));

                        KLog.w("参与分红nest总额 :"+ dividendInfo[2]);
                        totalDividendNest = Convert.fromWei( dividendInfo[2], Convert.Unit.ETHER).doubleValue();

                        getNestBanlace();
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.w("failure :"+ error);
                    }
                });

    }


    private void getMinePoolInfo(){
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getMinePoolInfo(TransferUtil.getWeb3j(), mWallet.getAddress()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<BigInteger[]>(IncomeActivity.this, false) {
                    @Override
                    protected void success(BigInteger[] minePoolParameter) {
                        KLog.w("已挖总量 :"+ minePoolParameter[0]);
                        Long diggedValue = Convert.fromWei( minePoolParameter[0]+"", Convert.Unit.ETHER).longValue();
                        Long remainderValue = 10000000000L - diggedValue;
                        mBinding.digged.setText(valueDisplay(diggedValue));
                        mBinding.remainder.setText(valueDisplay(remainderValue));

                        KLog.w(diggedValue / 10000000000d +"");
                        KLog.w(percentDisplay(diggedValue / 10000000000d) +"");
                        mBinding.diggedRate.setText(getString(R.string.digged,percentDisplay(diggedValue / 10000000000d)));
                        mBinding.remainderRate.setText(getString(R.string.remainder,percentDisplay(remainderValue / 10000000000d)));

                        KLog.w("时间衰减 :"+minePoolParameter[1]);
                        paramY = minePoolParameter[1].doubleValue()/10;
                        KLog.w("总数衰减 :"+minePoolParameter[2]);
                        paramX = Convert.fromWei( minePoolParameter[2]+"", Convert.Unit.ETHER).doubleValue();
                        getMiningAttenuate();
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.w("failure :"+ error);
                    }
                });

    }

    private void getMiningAttenuate(){
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getMiningAttenuate(TransferUtil.getWeb3j(), mWallet.getAddress()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<BigInteger[]>(IncomeActivity.this, false) {
                    @Override
                    protected void success(BigInteger[] value) {
//                        KLog.w("时间衰减 :"+value[0]);
                        KLog.w("衰减参数 :"+value[1]);
                        paramBN = Convert.fromWei( value[1]+"", Convert.Unit.ETHER).doubleValue();

                        double miningValue  = paramX * paramY * paramBN*0.8;
                        KLog.w("挖矿 :"+miningValue);
                        mBinding.borrowingEfficiency.setText(uniteduction(miningValue*0.8,"NEST"));
                        mBinding.creditEfficiency.setText(uniteduction(miningValue*0.2,"NEST"));
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.w("failure :"+ error);
                    }
                });

    }

    private void getNestBanlace() {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getTokenBalance(TransferUtil.getWeb3j(), mWallet.getAddress(), Constants.ASSETS_NEST_ADDRESS))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(IncomeActivity.this, false) {
                    @Override
                    protected void success(String data) {
                        KLog.w("Nest Banlace:"+data);
                        double nextValue = Double.valueOf(data);
                        double expectedIncome;
                        if(nextValue >= 1000 ){
                            expectedIncome = nextValue / totalDividendNest * totalEthDividend * 0.9d;
                        }else{
                            expectedIncome = 0;
                        }
                        KLog.w("我的分红:"+expectedIncome);
                        if(expectedIncome < 0.000000000000000001){
                            expectedIncome = 0;
                        }

                        mBinding.expectedDividends.setText(uniteductionOmit(expectedIncome,"ETH"));
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.w("failure :"+ error);
                    }
                });

    }


    public void queryServer() {

        Address address  = new Address();
        address.setToAddress(mWallet.getAddress());


        //我的分红
        HttpUtil.queryMyDividend(new Gson().toJson(address))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<ValueResponse>(IncomeActivity.this, false) {
                    @Override
                    protected void success(ValueResponse data) {
                        KLog.w("我的分红 :"+     data);
                        myDividend =  Convert.fromWei( data.getValue(), Convert.Unit.ETHER).doubleValue();
                        mBinding.myDividend.setText(uniteduction(myDividend,"ETH"));
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.w("failure :"+ error);
                    }
                });



        HttpUtil.queryMyMining(new Gson().toJson(address))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<ValueResponse>(IncomeActivity.this, false) {
                    @Override
                    protected void success(ValueResponse data) {
                        KLog.w("我的挖矿 :"+     data);
                        myMining = Convert.fromWei( data.getValue(), Convert.Unit.ETHER).doubleValue();
                        mBinding.myMining.setText(uniteduction(myMining,"NEST"));
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.w("failure :"+ error);
                    }
                });

        HttpUtil.queryCumulativeDividend()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(IncomeActivity.this, false) {
                    @Override
                    protected void success(String data) {
                        KLog.w("累计分红 :"+     data);
                         String value = fourFormat.format(Convert.fromWei( data, Convert.Unit.ETHER).doubleValue());
                        mBinding.cumulativeDividend.setText(value);
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.w("failure :"+ error);
                    }
                });
    }



    public  class Address{
        private String toAddress;

        public String getToAddress() {
            return toAddress;
        }

        public void setToAddress(String toAddress) {
            this.toAddress = toAddress;
        }
    }

    public  class AddressAndPage{
        private String toAddress;
        private String page;

        public String getToAddress() {
            return toAddress;
        }

        public void setToAddress(String toAddress) {
            this.toAddress = toAddress;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }
    }




    public static String valueDisplay(long value) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(value);
    }

    public static String percentDisplay(double value) {
        DecimalFormat df = new DecimalFormat("00.00%");
        return df.format(value);
    }

    /**
     * 单位缩小
     * @param value
     * @param token
     * @return
     */
    private SpannableString uniteduction(Double value,String token){

        SpannableString spannableString = new SpannableString(fourFormat.format(value)+" "+token);

        RelativeSizeSpan sizeSpan01 = new RelativeSizeSpan(1f);
        RelativeSizeSpan sizeSpan02 = new RelativeSizeSpan(0.6f);

        spannableString.setSpan(sizeSpan01, 0, spannableString.length()-token.length()-1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(sizeSpan02, spannableString.length()-token.length()-1, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return  spannableString;
    }

    /**
     * 单位缩小
     * @param value
     * @param token
     * @return
     */
    private SpannableString uniteductionOmit(Double value,String token){
        String s = BigDecimal.valueOf(value).setScale(8, BigDecimal.ROUND_DOWN).toPlainString();
        SpannableString spannableString = new SpannableString(s+" "+token);

        RelativeSizeSpan sizeSpan01 = new RelativeSizeSpan(1f);
        RelativeSizeSpan sizeSpan02 = new RelativeSizeSpan(0.6f);

        spannableString.setSpan(sizeSpan01, 0, spannableString.length()-token.length()-1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(sizeSpan02, spannableString.length()-token.length()-1, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return  spannableString;
    }

    //填充地址
    private void fillAddress(TextView targetView, String address) {
        targetView.setTextColor(getResources().getColor(R.color.c3));
        String text = address.substring(0, 8) + "..." + address.substring(address.length() - 8, address.length());
        targetView.setText(text);
        targetView.setOnClickListener(v -> {
            Intent intent = new Intent(IncomeActivity.this, EtherScanWebActivity.class);
            intent.putExtra("ContractAddress", address);
            startActivity(intent);
        });
        targetView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CommomUtil.copy(IncomeActivity.this,mBinding.getRoot(),address);
                return true;
            }
        });
    }

}
