package com.lianer.core.utils;

import com.lianer.core.api.ApiService;
import com.lianer.core.app.Constants;
import com.lianer.core.base.BaseBean;
import com.lianer.core.borrow.Bean.ExchangeRateBean;
import com.lianer.core.contract.bean.ContractInvestResponse;
import com.lianer.core.contract.bean.ContractResponse;
import com.lianer.core.contract.bean.TokenMortgageEntity;
import com.lianer.core.invest.bean.InvestmentBean;
import com.lianer.core.invest.model.BannerDelete;
import com.lianer.core.lauch.bean.VersionResponse;
import com.lianer.core.market.bean.BannerResponse;
import com.lianer.core.market.bean.MarketContractResponse;
import com.lianer.core.market.bean.MortgagedAssetsResponse;
import com.lianer.core.market.bean.TransactionRecordResponse;
import com.lianer.core.wallet.bean.IncomeDetailResponse;
import com.lianer.core.wallet.bean.TxRecordReponseBean;
import com.lianer.core.wallet.bean.ValueResponse;


import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 网络工具类
 *
 * @author allison
 */
public class HttpUtil {

    // 加盐
    private static final String SALT = "eyJpdiI6Ik1EZ3hObVJtWldVeE5UZzFNV1EwTnc9PSIsInZhbHVlIjoiYllcL0pBcjd4dkIzVkR1ek5cLzY4XC9JWnYwTFQ2K1hpanBXVG9NUW1NTlY4Zz0ifQ";
    private static final int DEFAULT_TIMEOUT = 10; //连接 超时的时间，单位：秒

    public static ApiService getApiService(String baseUrl) {
        String tempTime = String.valueOf(System.currentTimeMillis());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(new OkHttpClient.Builder().
                        addInterceptor(chain -> {
                            Request original = chain.request();

                            Request request = original.newBuilder()
                                    .header("sign", DigestUtils.md5Hex(tempTime + SALT))
                                    .header("timestamp", tempTime)
                                    .method(original.method(), original.body())
                                    .build();

                            return chain.proceed(request);
                        }).
                        connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).
                        readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).
                        writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS).build())
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(ScalarsConverterFactory.create())
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(ApiService.class);
    }


    /**
     * 发布合约到市场
     *
     * @param requstParams
     * @return
     */
    public static Flowable<BaseBean> publishContract(String requstParams) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
        return HttpUtil.getApiService(Constants.INTERFACE_URL).publishContractToMarket(requestBody);
    }

    /**
     * 发布Tusd合约到市场
     *
     * @param requstParams
     * @return
     */
    public static Flowable<BaseBean> publishTusdContract(String requstParams) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
        return HttpUtil.getApiService(Constants.INTERFACE_URL).publishTusdContractToMarket(requestBody);
    }

    /**
     * 获取banner数据
     *
     * @return
     */
    public static Flowable<BannerResponse> getBanners() {
        return HttpUtil.getApiService(Constants.INTERFACE_URL).getBanners();
    }

    /**
     * 获取成交记录
     *
     * @return
     */
    public static Flowable<TransactionRecordResponse> getTransactionRecord() {
        return HttpUtil.getApiService(Constants.INTERFACE_URL).getTransactionRecord();
    }


    /**
     * 获取抵押资产
     *
     * @return
     */
    public static Flowable<MortgagedAssetsResponse> getMortgagedAssets() {
        return HttpUtil.getApiService(Constants.INTERFACE_URL).getMortgagedAssets();
    }


    /**
     * 获取市场列表
     *
     * @param requstParams
     * @return
     */
    public static Flowable<MarketContractResponse> getMarketList(String requstParams) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
        return HttpUtil.getApiService(Constants.INTERFACE_URL).getMarketList(requestBody);
    }

    /**
     * 获取交易记录
     *
     * @param address
     * @param apikey
     * @param page
     * @param offset
     * @return
     */
    public static Flowable<TxRecordReponseBean> getTxRecord(String address, String apikey, int page, int offset) {
        return HttpUtil.getApiService(Constants.ETHSCAN_URL).getTxRecord(address, apikey, page, offset);
    }

    /**
     * 获取代币交易列表
     *
     * @param contractAddress 代币合约地址
     * @param address         钱包地址
     * @param page            页码
     * @param offset          偏移量
     * @param apikey          ApiKeyToken
     * @return Flowable<TxRecordReponseBean>
     */
    public static Flowable<TxRecordReponseBean> getTokenTxRecord(String contractAddress, String address, int page, int offset, String apikey) {
        return HttpUtil.getApiService(Constants.ETHSCAN_URL).getTokenTxRecord(contractAddress, address, page, offset, apikey);
    }


    /**
     * 创建合约
     *
     * @param requstParams     json字符串请求参数
     * @param contractCallBack 请求回调
     */
    public static void createContract(String requstParams, ContractStateCallBack contractCallBack) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
        HttpUtil.getApiService(Constants.INTERFACE_URL).createContract(body).enqueue(new Callback<ContractResponse>() {

            @Override
            public void onResponse(Call<ContractResponse> call, Response<ContractResponse> response) {
                contractCallBack.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<ContractResponse> call, Throwable t) {
                contractCallBack.onFailure(t.getMessage());
            }
        });
    }


    /**
     * 修改合约状态
     *
     * @param requstParams     json字符串请求参数
     * @param contractCallBack 请求回调
     */
    public static void updateContractState(String requstParams, updateContractCallBack contractCallBack) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
        HttpUtil.getApiService(Constants.INTERFACE_URL).updateContract(body).enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                contractCallBack.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                contractCallBack.onFailure(t.getMessage());
            }
        });
    }

    /**
     * 修改合约状态
     *
     * @param requstParams json字符串请求参数
     */
    public static Observable<String> updateContractState(String requstParams) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
        return getApiService(Constants.INTERFACE_URL).updateContractInfo(body);
    }

    /**
     * 获取合约单列表
     *
     * @param requstParams json字符串请求参数
     */
    public static Flowable<List<ContractResponse>> getContractList(String requstParams) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
        return HttpUtil.getApiService(Constants.INTERFACE_URL).getContractList(body);
    }

    /** 投资人操作start **/

    /**
     * 新增投资信息
     *
     * @param requstParams json字符串请求参数
     * @return Observable<ContractInvestResponse>
     */
    public static Observable<ContractInvestResponse> createInvest(String requstParams) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
        return getApiService(Constants.INTERFACE_URL).investCreate(body);
    }

    /**
     * 删除投资信息
     *
     * @param requstParams json字符串请求参数
     * @return Observable<Boolean> true or false
     */
    public static Observable<Boolean> deleteInvest(String requstParams) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
        return getApiService(Constants.INTERFACE_URL).investDelete(body);
    }

    /**
     * 获取投资信息
     *
     * @param requstParams json字符串请求参数
     * @return Observable<ContractInvestResponse>
     */
    public static Observable<ContractInvestResponse> getInvest(String requstParams) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
        return HttpUtil.getApiService(Constants.INTERFACE_URL).investGet(body);
    }

    /**
     * 修改投资信息
     *
     * @param requstParams json字符串请求参数
     * @return Observable<String>
     */
    public static Observable<String> updateInvest(String requstParams) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
        return getApiService(Constants.INTERFACE_URL).investUpdate(body);
    }

    /**
     * 查询投资信息
     *
     * @param requstParams json字符串请求参数
     * @return Observable<ContractResponse>
     */
    public static Observable<ContractResponse> queryInvest(String requstParams) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
        return getApiService(Constants.INTERFACE_URL).investQuery(body);
    }

    /**
     * 投资人操作end
     **/

    /**
     * 查询banner
     *
     * @param requestParams json字符串请求参数
     * @return Observable<List       <       BannerDelete>>
     */
    public static Flowable<List<BannerDelete>> queryBanner(String requestParams) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestParams);
        return getApiService(Constants.INTERFACE_URL).bannerQuery(body);
    }

    /**
     * 查询折扣率
     *
     * @param requestParams json字符串请求参数
     * @return Observable<List       <       BannerDelete>>
     */
    public static Flowable<List<ExchangeRateBean>> queryExchangRate(String requestParams) {

        return Flowable.just(1)
                .flatMap(s -> {
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestParams);
                    return getApiService(Constants.INTERFACE_URL).queryExchangRate(body);
                });
    }

    /**
     * 抵押资产
     */
    public static Flowable<TokenMortgageEntity> queryMortgageToken() {

        return Flowable.just(1)
                .flatMap(s -> {
//                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestParams);
                    return getApiService(Constants.INTERFACE_URL).queryMortgageToken();
                });
    }

    /**
     * TUSD抵押资产
     */
    public static Flowable<TokenMortgageEntity> queryTusdMortgageToken() {

        return Flowable.just(1)
                .flatMap(s -> {
//                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestParams);
                    return getApiService(Constants.INTERFACE_URL).queryTusdMortgageToken();
                });
    }

    /**
     * 查询版本信息
     *
     * @return
     */
    public static Flowable<VersionResponse> queryVersion(String requstParams) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
        return getApiService(Constants.INTERFACE_URL).versinQuery(body);
    }

    /**
     * 我的分红
     */
    public static Flowable<ValueResponse> queryMyDividend(String requstParams) {

        return Flowable.just(1)
                .flatMap(s -> {
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
                    return getApiService(Constants.INTERFACE_INCOME_URL).queryMyDividend(body);
                });
    }

    /**
     * 我的分红详情
     */
    public static Flowable<IncomeDetailResponse> queryMyDividendDetail(String requstParams) {

        return Flowable.just(1)
                .flatMap(s -> {
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
                    return getApiService(Constants.INTERFACE_INCOME_URL).queryMyDividendDetail(body);
                });
    }

    /**
     * 我的挖矿
     */
    public static Flowable<ValueResponse> queryMyMining(String requstParams) {

        return Flowable.just(1)
                .flatMap(s -> {
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
                    return getApiService(Constants.INTERFACE_INCOME_URL).queryMyMining(body);
                });
    }

    /**
     * 我的挖矿详情
     */
    public static Flowable<IncomeDetailResponse> queryMyMiningDetail(String requstParams) {

        return Flowable.just(1)
                .flatMap(s -> {
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
                    return getApiService(Constants.INTERFACE_INCOME_URL).queryMyMiningDetail(body);
                });
    }


    /**
     * 累计分红
     */
    public static Flowable<String> queryCumulativeDividend() {

        return Flowable.just(1)
                .flatMap(s -> {
//                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requstParams);
                    return getApiService(Constants.INTERFACE_INCOME_URL).queryCumulativeDividend();
                });
    }

    public interface HttpCallback {
        void onSuccess(TxRecordReponseBean txRecordReponseBean);

        void onFailure(String errorMsg);
    }

    public interface ContractCallBack {
        void onSuccess(List<ContractResponse> responses);

        void onFailure(String errorMsg);
    }

    public interface ContractStateCallBack {
        void onSuccess(ContractResponse responses);

        void onFailure(String errorMsg);
    }

    public interface updateContractCallBack {
        void onSuccess(Object responses);

        void onFailure(String errorMsg);
    }

    public interface GetInvestmentCallBack {
        void onSuccess(InvestmentBean responses);

        void onFailure(String errorMsg);
    }
}
