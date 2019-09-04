package com.lianer.core.api;


import com.lianer.core.base.BaseBean;
import com.lianer.core.borrow.Bean.ExchangeRateBean;
import com.lianer.core.contract.bean.ContractInvestResponse;
import com.lianer.core.contract.bean.ContractResponse;
import com.lianer.core.contract.bean.TokenMortgageEntity;
import com.lianer.core.databean.ContractDetailBean;
import com.lianer.core.databean.InfoDataBean;
import com.lianer.core.databean.NormalDataBean;
import com.lianer.core.invest.model.BannerDelete;
import com.lianer.core.lauch.bean.VersionResponse;
import com.lianer.core.market.bean.BannerResponse;
import com.lianer.core.market.bean.MarketContractResponse;
import com.lianer.core.market.bean.MortgagedAssetsResponse;
import com.lianer.core.market.bean.TransactionRecordResponse;
import com.lianer.core.wallet.bean.IncomeDetailResponse;
import com.lianer.core.wallet.bean.TxRecordReponseBean;
import com.lianer.core.wallet.bean.ValueResponse;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    /**
     * 发布合约到市场
     * @param info
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/IBApi/Market/Add")
    Flowable<BaseBean> publishContractToMarket(@Body RequestBody info);


    /**
     * 发布合约到市场
     * @param info
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/IBApi/Market/tusd/add")
    Flowable<BaseBean> publishTusdContractToMarket(@Body RequestBody info);


    /**
     * 获取banner数据
     * @return
     */
    @GET("/IBApi/Banner/Get")
    Flowable<BannerResponse> getBanners();

    /**
     * 获取成交记录
     * @return
     */
    @GET("/total/statistics/get/market")
    Flowable<TransactionRecordResponse> getTransactionRecord();

    /**
     * 获取抵押资产
     * @return
     */
    @GET("/address/Get")
    Flowable<MortgagedAssetsResponse> getMortgagedAssets();

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/IBApi/Market/GetList")
    Flowable<MarketContractResponse> getMarketList(@Body RequestBody info);

    /**
     * 获取交易记录
     * @param address 地址
     * @param apikey  唯一key
     * @param page    页码
     * @param offset  页数
     * @return
     */
    @GET("/api?module=account&action=txlist&startblock=0&endblock=99999999&sort=desc")
    Flowable<TxRecordReponseBean> getTxRecord(@Query("address") String address,
                                              @Query("apikey") String apikey,
                                              @Query("page") int page,
                                              @Query("offset") int offset);

    /**
     * 获取代币交易列表
     * @param contractAddress
     * @param address
     * @param page
     * @param offset
     * @param apikey
     * @return
     */
    @GET("/api?module=account&action=tokentx&sort=desc")
    Flowable<TxRecordReponseBean> getTokenTxRecord(@Query("contractaddress") String contractAddress,
                                                   @Query("address") String address,
                                                   @Query("page") int page,
                                                   @Query("offset") int offset,
                                                   @Query("apikey") String apikey);

    /**
     * 获取合约列表单
     * @param info
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/api/Contract/Query")
    Flowable<List<ContractResponse>> getContractList(@Body RequestBody info);

    /**
     * 合约相关操作：修改合约
     * @param info
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/api/Contract/Update")
    Call<String>  updateContract(@Body RequestBody info);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/api/Contract/Update")
    Observable<String>  updateContractInfo(@Body RequestBody info);


    /**
     * 合约相关操作：创建合约
     * @param info
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/api/Contract/Create")
    Call<ContractResponse>  createContract(@Body RequestBody info);

    /**
     * 新增投资信息
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/api/Investment/Create")
    Observable<ContractInvestResponse> investCreate(@Body RequestBody info);

    /**
     * 删除投资信息
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/api/Investment/Delete")
    Observable<Boolean> investDelete(@Body RequestBody info);

    /**
     * 获取投资信息
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/api/Investment/Get")
    Observable<ContractInvestResponse> investGet(@Body RequestBody info);

    /**
     * 修改投资信息
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/api/Investment/Update")
    Observable<String> investUpdate(@Body RequestBody info);

    /**
     * 查询投资信息
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/api/Investment/Query")
    Observable<ContractResponse> investQuery(@Body RequestBody info);

    /**
     * 获取首页banner列表
     * @param info
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/api/Banner/Query")
    Flowable<List<BannerDelete>> bannerQuery(@Body RequestBody info);

    /**
     * 获取token折扣率
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/api/Options/Query")
    Flowable<List<ExchangeRateBean>> queryExchangRate(@Body RequestBody info);


    /**
     * 获取抵押token
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("/IBApi/MortgagedAssets/Get")
    Flowable<TokenMortgageEntity> queryMortgageToken();

    /**
     * 获取TUSD抵押token
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("/IBApi/MortgagedAssets/GetCoinTusd")
    Flowable<TokenMortgageEntity> queryTusdMortgageToken();

    /**
     * 查询版本信息
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/IBApi/Edition/Get/deviceType")
    Flowable<VersionResponse> versinQuery(@Body RequestBody info);

    /**
     * 获取我的分红
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/my/dividend/get/toAddress")
    Flowable<ValueResponse> queryMyDividend(@Body RequestBody info);

    /**
     * 获取我的分红详情
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/dividend/transaction/show/my/dividend")
    Flowable<IncomeDetailResponse> queryMyDividendDetail(@Body RequestBody info);

    /**
     * 获取我的挖矿
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/my/mining/get/toAddress")
    Flowable<ValueResponse> queryMyMining(@Body RequestBody info);

    /**
     * 获取我的挖矿详情
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/mining/transaction/show/my/mining")
    Flowable<IncomeDetailResponse> queryMyMiningDetail(@Body RequestBody info);


    /**
     * 获取累计分红
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("/all/dividend/Get/dividend")
    Flowable<String> queryCumulativeDividend();

    /**
     * 获取累计挖矿
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @GET("/all/dividend/Get/mining")
    Flowable<String> queryCumulativeMining();

    //=====================================================================

    /**
     * 获取nonce
     * @param info
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/hpb/private/function/getNonce")
    Call<NormalDataBean> getCoreNonce(@Body RequestBody info);

    /**
     * 获取余额
     * @param info
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/hpb/private/function/getBalance")
    Call<NormalDataBean> getBalance(@Body RequestBody info);

    /**
     * 获取平均gasprice
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/hpb/private/function/getGasPrice")
    Call<NormalDataBean> getAverageGasPrice();


    /**
     * 发送交易
     * @param info
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/hpb/private/function/sendTransaction")
    Call<NormalDataBean> sendTransaction(@Body RequestBody info);

    /**
     * 查询交易状态
     * @param info
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/hpb/private/function/checkTransactionHashStatus")
    Call<NormalDataBean> checkTransactionHashStatus(@Body RequestBody info);

    /**
     * 根据哈希查询借贷合约地址
     * @param info
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/hpb/private/function/getContractAddress")
    Flowable<NormalDataBean> getContractAddress(@Body RequestBody info);

    /**
     * 查询相关数据
     * @param info
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/hpb/private/function/selectTransactionHash")
    Call<InfoDataBean> selectTransactionHash(@Body RequestBody info);

    /**
     * 验证是否是借贷合约
     * @param info
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/hpb/private/function/isContract")
    Flowable<NormalDataBean> isContract(@Body RequestBody info);

    /**
     * 验证是否是借贷合约
     * @param info
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/hpb/private/function/isContract")
    Call<NormalDataBean> checkContract(@Body RequestBody info);

    /**
     * 获取合约详情
     * @param info
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("/hpb/private/function/getContractData")
    Call<ContractDetailBean> getContractData(@Body RequestBody info);



}