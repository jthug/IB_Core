package com.lianer.core.SmartContract;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.lianer.common.utils.KLog;
import com.lianer.core.SmartContract.ETH.IBContract;
import com.lianer.core.SmartContract.ETH.IBDataContract;
import com.lianer.core.SmartContract.ETH.IBFactoryContract;
import com.lianer.core.SmartContract.TUSD.IBTUSDContract;
import com.lianer.core.SmartContract.TUSD.IBTUSDContractStatues;
import com.lianer.core.SmartContract.TUSD.IBTUSDDataContract;
import com.lianer.core.app.Constants;
import com.lianer.core.contract.bean.ContractBean;
import com.lianer.core.databean.ContractDetailBean;
import com.lianer.core.databean.NormalDataBean;
import com.lianer.core.databean.requestbean.RequestBalanceBean;
import com.lianer.core.manager.ERC20Manager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.utils.HttpUtil;
import com.lianer.core.wallet.bean.TokenProfileBean;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple14;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple9;
import org.web3j.tx.ChainId;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import io.reactivex.Flowable;
import retrofit2.Response;

/**
 * Created by TT on 2018/5/29.
 */

public class IBContractUtil extends BaseContract {

    private static final String TAG = "IBContractUtil";
//    private static IBContractUtil instance;
//
//    public IBContractUtil() {
//        super();
//    }
//
//    public static IBContractUtil getInstance() {
//        if (instance == null) {
//            instance = new IBContractUtil();
//        }
//        return instance;
//    }
//
//
//    //ETH 借贷合约状态
//    static IBContractStatues ibContractStatues;
//
//    public static IBContractStatues getIbContractStatues() {
//        return ibContractStatues;
//    }
//
//    public void setIbContractStatues(IBContractStatues ibContractStatues) {
//        this.ibContractStatues = ibContractStatues;
//    }
//
//    //ETH 借贷合约
//    IBContract IbContract;
//
//    public IBContract getIbContract() {
//        return IbContract;
//    }
//
//    public void setIbContract(IBContract ibContract) {
//        IbContract = ibContract;
//    }
//
//
//    //TUSD借贷合约
//    IBTUSDContract ibTusdContract ;
//
//    public IBTUSDContract getIbT usdContract() {
//        return ibTusdContract;
//    }
//
//    public void setIbTusdContract(IBTUSDContract ibTusdContract) {
//        this.ibTusdContract = ibTusdContract;
//    }
//
//
//
//    static IBTUSDContractStatues ibTusdContractStatues;
//
//    public static IBTUSDContractStatues getIbTusdContractStatues() {
//        return ibTusdContractStatues;
//    }
//
//    public void setIbTusdContractStatues(IBTUSDContractStatues ibTusdContractStatues) {
//        this.ibTusdContractStatues = ibTusdContractStatues;
//    }


    //获取Nonce
    public static long getNonce(Web3j web3j, String walletAddress) throws Exception {

        String jsonParams = "{\n" +
                "\t\"walletAddress\": \"" + walletAddress + "\"\n" +
                "}";

        Response<NormalDataBean> execute = HttpUtil.getCoreNonce(jsonParams).execute();
        int code = execute.code();
        if (200 == code) {
            NormalDataBean body = execute.body();
            String code1 = body.getCode();
            if (TextUtils.equals("200", code1)) {
                BigInteger integer = new BigInteger(body.getData().get(0).trim());
                return integer.longValue();
            } else {
                throw new Exception("网络异常");
            }
        } else {
            throw new Exception("网络异常");
        }

    }

    //获取交易Nonce并同步到本地
    public static BigInteger transactionNonce(Context context, Web3j web3j, String walletAddress) throws Exception {

        String jsonParams = "{\n" +
                "\t\"walletAddress\": \"" + walletAddress + "\"\n" +
                "}";


        Response<NormalDataBean> execute = HttpUtil.getCoreNonce(jsonParams).execute();
        int code = execute.code();
        if (200 == code) {
            NormalDataBean body = execute.body();
            String code1 = body.getCode();
            if (TextUtils.equals("200", code1)) {
                BigInteger bigInteger = new BigInteger(body.getData().get(0).trim());
                SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.TRANSACTION_INFO, Context.MODE_PRIVATE);
                sharedPreferences.edit().putLong(Constants.TRANSACTION_NONCE, bigInteger.longValue()).apply();
                return bigInteger;
            } else {
                throw new Exception("网络异常");
            }
        } else {
            throw new Exception("网络异常");
        }

    }

    //ETH 借贷合约部署
    public static Flowable<String> deployContract(Context context, Web3j web3j, Credentials credentials, String factoryAddress, BigInteger gasPrice, BigInteger gasLimit, String assetsAmount, String ethAmount, String tokenType, String cycle, String interest) throws Exception {

        //抵押金额（代币）
        BigInteger borrowerAmount = new BigInteger(assetsAmount);
        //贷款金额（ETH）
        BigInteger lenderAmount = new BigInteger(ethAmount);

        BigInteger tokenId = new BigInteger(tokenType);
        //限制天数
        BigInteger limitdays = new BigInteger(cycle);
        //利率
        BigInteger interestRate = new BigInteger(interest);


        BigInteger nonce = transactionNonce(context, web3j, credentials.getAddress());

        final Function function = new Function(
                "creatContract",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(borrowerAmount),
                        new org.web3j.abi.datatypes.generated.Uint256(lenderAmount),
                        new org.web3j.abi.datatypes.generated.Uint256(tokenId),
                        new org.web3j.abi.datatypes.generated.Uint256(limitdays),
                        new org.web3j.abi.datatypes.generated.Uint256(interestRate)),
                Collections.<TypeReference<?>>emptyList());
        String encodedFunction = FunctionEncoder.encode(function);
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, BigInteger.valueOf(5000000), Constants.FactoryAddress, encodedFunction);


        String hexValue = signData(rawTransaction, credentials);
        Log.e("xxxxxx-2", "gasprice=" + gasPrice + " gasLimit=" + gasLimit + " assetsAmount=" + assetsAmount + " ethAmount=" + ethAmount + " tokenType=" + tokenType + " cycle=" + cycle + " interest=" + interest+" tokenId="+tokenId);
//                    String txHash = web3j.ethSendRawTransaction(hexValue).sendAsync().get().getTransactionHash();
        String jsonParams = "{\n" +
                "\t\"hexValue\": \"" + hexValue + "\"\n" +
                "}";

        Response<NormalDataBean> execute = HttpUtil.sendTransaction(jsonParams).execute();
        if (execute.code() == 200) {
            NormalDataBean bean = execute.body();
            if (TextUtils.equals("200", bean.getCode())) {
                String txHash = bean.getData().get(0);
                return Flowable.just(txHash);
            } else {
                throw new Exception("网络异常");
            }
        } else {
            throw new Exception("网络异常");
        }


    }

    //TUSD 部署
    public static Flowable<String> deployTUSDContract(Context context, Web3j web3j, Credentials credentials, String factoryAddress, BigInteger gasPrice, BigInteger gasLimit, String mortgageAmount, String mortgageType, String loanAmount, String loanType, String cycle, String interest) {
        return Flowable.just(1)
                .flatMap(s -> {

                    //抵押金额
                    BigInteger borrowerAmount = new BigInteger(mortgageAmount);
                    //贷款金额
                    BigInteger lenderAmount = new BigInteger(loanAmount);

                    //抵押代币
                    BigInteger borrowerId = new BigInteger(mortgageType);
                    //借贷代币
                    BigInteger lenderId = new BigInteger(loanType);
                    //限制天数
                    BigInteger limitdays = new BigInteger(cycle);
                    //利率
                    BigInteger interestRate = new BigInteger(interest);


                    BigInteger nonce = transactionNonce(context, web3j, credentials.getAddress());

                    final Function function = new Function(
                            "creatContract",
                            Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(borrowerAmount),
                                    new org.web3j.abi.datatypes.generated.Uint256(borrowerId),
                                    new org.web3j.abi.datatypes.generated.Uint256(lenderAmount),
                                    new org.web3j.abi.datatypes.generated.Uint256(lenderId),
                                    new org.web3j.abi.datatypes.generated.Uint256(limitdays),
                                    new org.web3j.abi.datatypes.generated.Uint256(interestRate)),
                            Collections.<TypeReference<?>>emptyList());
                    String encodedFunction = FunctionEncoder.encode(function);
                    RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, factoryAddress, encodedFunction);

                    String hexValue = signData(rawTransaction, credentials);
                    String txHash = web3j.ethSendRawTransaction(hexValue).sendAsync().get().getTransactionHash();
                    return Flowable.just(txHash);
                });
    }

    //合约部署gas预计消耗
    public static Flowable<BigInteger> getDeployContractEstimateGas(Web3j web3j, String walletAddress, String fAddress, BigInteger gasPrice, BigInteger gasLimit, String assetsAmount, String ethAmount, String tokenType, String cycle, String interest) {
        return Flowable.just(1)
                .flatMap(s -> {
                    //抵押金额（代币）
                    BigInteger borrowerAmount = new BigInteger(assetsAmount);
                    //贷款金额（ETH）
                    BigInteger lenderAmount = new BigInteger(ethAmount);

                    BigInteger tokenId = new BigInteger(tokenType);
                    //限制天数
                    BigInteger limitdays = new BigInteger(cycle);
                    //利率
                    BigInteger interestRate = new BigInteger(interest);

                    EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(walletAddress, DefaultBlockParameterName.PENDING).sendAsync().get();
                    BigInteger nonce = ethGetTransactionCount.getTransactionCount();

                    final Function function = new Function(
                            "creatContract",
                            Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(borrowerAmount),
                                    new org.web3j.abi.datatypes.generated.Uint256(lenderAmount),
                                    new org.web3j.abi.datatypes.generated.Uint256(tokenId),
                                    new org.web3j.abi.datatypes.generated.Uint256(limitdays),
                                    new org.web3j.abi.datatypes.generated.Uint256(interestRate)),
                            Collections.<TypeReference<?>>emptyList());
                    String encodedFunction = FunctionEncoder.encode(function);
                    Transaction transaction = Transaction.createFunctionCallTransaction(walletAddress, nonce, gasPrice, gasLimit, fAddress, encodedFunction);
                    BigInteger value = web3j.ethEstimateGas(transaction).sendAsync().get().getAmountUsed();
                    return Flowable.just(value);
                });
    }

    //工厂合约创建合约
    public static Flowable<String> deployContract(Web3j web3j, IBFactoryContract contract, BigInteger gasPrice, BigInteger gasLimit, String assetsAmount, String ethAmount, String tokenType, String cycle, String interest) {
        return Flowable.just(1)
                .flatMap(s -> {

                    //抵押金额（代币）
                    BigInteger borrowerAmount = new BigInteger(assetsAmount);
                    //贷款金额（ETH）
                    BigInteger lenderAmount = new BigInteger(ethAmount);

                    BigInteger tokenId = new BigInteger(tokenType);
                    //限制天数
                    BigInteger limitdays = new BigInteger(cycle);
                    //利率
                    BigInteger interestRate = new BigInteger(interest);

                    //调用工厂合约创建合约
                    TransactionReceipt transactionReceipt = contract.creatContract(borrowerAmount, lenderAmount, tokenId, limitdays, interestRate).sendAsync().get();

                    String txHash = transactionReceipt.getTransactionHash();
                    return Flowable.just(txHash);
                });
    }


    //get DataContract Address
    public static Flowable<String> getDataContractAddress(Web3j web3j, String walletAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    MappingContract contract = MappingContract.load(Constants.MappingAddress, web3j, transactionManager, gasPrice, gasLimit);
                    String address = contract.checkAddress("loanData").sendAsync().get();

                    return Flowable.just(address);
                });

    }

    //get FactoryContract Address
    public static Flowable<String> getFactoryContractAddress(Web3j web3j, String walletAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    MappingContract contract = MappingContract.load(Constants.MappingAddress, web3j, transactionManager, gasPrice, gasLimit);
                    String address = contract.checkAddress("loanFactory").sendAsync().get();

                    return Flowable.just(address);
                });

    }

    //load IBContract
    public static Flowable<IBContract> loadIBContract(Web3j web3j, Credentials credentials, String contractAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    IBContract bContract = IBContract.load(contractAddress, web3j, credentials, gasPrice, gasLimit);
                    return Flowable.just(bContract);
                });
    }

    // readOnly IBContract
    public static Flowable<IBContract> readOnlyIBContract(Web3j web3j, String walletAddress, String contractAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    IBContract contract = IBContract.load(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
                    return Flowable.just(contract);
                });

    }


    //readOnly IBContract State
    public static Flowable<IBContractStatues> readOnlyIBContractState(Web3j web3j, String walletAddress, String contractAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    IBContract ibContract = IBContract.load(contractAddress, web3j, transactionManager, gasPrice, gasLimit);

                    IBContractStatues ibContractStatues = new IBContractStatues();
                    if (ibContract == null) {
                        return Flowable.just(ibContractStatues);
                    }
                    Tuple14<BigInteger, String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> result = ibContract.getContractInfo().sendAsync().get();

                    ibContractStatues.setContractState(result.getValue1() + "");
                    ibContractStatues.setBorrowerAddress(result.getValue2());
                    ibContractStatues.setInvestorAddress(result.getValue3());
                    ibContractStatues.setAmount(result.getValue4() + "");
                    ibContractStatues.setCycle(result.getValue5() + "");

                    ibContractStatues.setInterest(result.getValue6() + "");
                    ibContractStatues.setMortgage(result.getValue7() + "");
                    ibContractStatues.setNeedMortgage(result.getValue8() + "");
                    ibContractStatues.setTokenAddress(result.getValue9() + "");

                    ibContractStatues.setInvestmentTime(result.getValue10() + "");
                    ibContractStatues.setEndTime(result.getValue11() + "");
                    ibContractStatues.setExpire(result.getValue12() + "");
                    ibContractStatues.setExpiryTime(result.getValue13() + "");
                    ibContractStatues.setCreateTime(result.getValue14() + "");
                    ibContractStatues.setContractAddress(ibContract.getContractAddress());
                    KLog.w(ibContractStatues.toString());
                    return Flowable.just(ibContractStatues);
                });
    }


    //load IBDateContract
    public static Flowable<IBDataContract> loadIBDateContract(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String contractAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    IBDataContract contract = IBDataContract.load(contractAddress, web3j, credentials, gasPrice, gasLimit);
                    return Flowable.just(contract);
                });
    }

    //readOnly IBDateContract
    public static Flowable<IBDataContract> readOnlyIBDateContract(Web3j web3j, String walletAddress, String contractAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    IBDataContract contract = IBDataContract.load(contractAddress, web3j, transactionManager, gasPrice, gasLimit);

                    return Flowable.just(contract);
                });

    }

    //load IBFactoryContract
    public static Flowable<IBFactoryContract> loadIBFactoryContract(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String contractAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    IBFactoryContract contract = IBFactoryContract.load(contractAddress, web3j, credentials, gasPrice, gasLimit);
                    return Flowable.just(contract);
                });
    }

    //合约校验
    public static Flowable<Boolean> isIBContract(IBDataContract contract, String contractAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    boolean isIBContract = contract.checkContract(contractAddress).sendAsync().get();
                    return Flowable.just(isIBContract);
                });
    }

    //合约校验
    public static Flowable<Boolean> isIBContract(Web3j web3j, String walletAddress, String contractAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
//                    MappingContract contract = MappingContract.load(Constants.MappingAddress, web3j, transactionManager, gasPrice, gasLimit);
                    //获取数据合约地址 0x13b6ea8244c485910006d151e7fcd139d8307a97
//                    String address = contract.checkAddress("loanData").sendAsync().get();
                    String address = Constants.DataAddress;
                    //加载数据合约
                    IBDataContract ibDataContract = IBDataContract.load(address, web3j, transactionManager, gasPrice, gasLimit);
                    //校验
                    boolean isIBContract = ibDataContract.checkContract(contractAddress).sendAsync().get();
                    return Flowable.just(isIBContract);
                });
    }

    //合约校验
    public static Flowable<Boolean> isIBTusdContract(Web3j web3j, String walletAddress, String contractAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    MappingContract contract = MappingContract.load(Constants.MappingAddress, web3j, transactionManager, gasPrice, gasLimit);
                    //获取数据合约地址
                    String address = contract.checkAddress("loanSelfData").sendAsync().get();
                    //加载数据合约
                    IBTUSDDataContract ibTusdDataContract = IBTUSDDataContract.load(address, web3j, transactionManager, gasPrice, gasLimit);
                    //校验
                    boolean isIBContract = ibTusdDataContract.checkContract(contractAddress).sendAsync().get();
                    return Flowable.just(isIBContract);
                });
    }

    //readOnly IBTUSDContract
    public static Flowable<String> readOnlyIBTUSDContract(Web3j web3j, String walletAddress, String contractAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
//                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
//                    BigInteger gasLimit = BigInteger.valueOf(1000000);
//                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
//                    IBTUSDContract ibTusdContract = IBTUSDContract.load(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
//                    return Flowable.just(ibTusdContract);
                    return Flowable.just(contractAddress);
                });

    }

    //get TUSD DataContract Address
    public static Flowable<String> getTUSDDataContractAddress(Web3j web3j, String walletAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    MappingContract contract = MappingContract.load(Constants.MappingAddress, web3j, transactionManager, gasPrice, gasLimit);
                    String address = contract.checkAddress("loanSelfData").sendAsync().get();

                    return Flowable.just(address);
                });

    }

    //readOnly IBTUSDDateContract
    public static Flowable<IBTUSDDataContract> readOnlyIBTUSDDateContract(Web3j web3j, String walletAddress, String contractAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    IBTUSDDataContract contract = IBTUSDDataContract.load(contractAddress, web3j, transactionManager, gasPrice, gasLimit);

                    return Flowable.just(contract);
                });

    }


    // get TUSDFactoryContract Address
    public static Flowable<String> getTUSDFactoryContractAddress(Web3j web3j, String walletAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    MappingContract contract = MappingContract.load(Constants.MappingAddress, web3j, transactionManager, gasPrice, gasLimit);
                    String address = contract.checkAddress("loanSelfFactory").sendAsync().get();

                    return Flowable.just(address);
                });

    }

    /**
     * 领取分红
     *
     * @param web3j
     * @param credentials
     * @return
     */

    public static Flowable<TransactionReceipt> getEarn(Web3j web3j, Credentials credentials) {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    MappingContract contract = MappingContract.load(Constants.MappingAddress, web3j, credentials, gasPrice, gasLimit);

                    String address = contract.checkAddress("abonus").sendAsync().get();
                    IBAbonusContract ibAbonusContract = IBAbonusContract.load(address, web3j, credentials, gasPrice, gasLimit);
                    TransactionReceipt transactionReceipt = ibAbonusContract.getEth().sendAsync().get();
                    return Flowable.just(transactionReceipt);
                });
    }

    /**
     * 取回nest
     *
     * @param web3j
     * @param credentials
     * @param amout
     * @return
     */
    public static Flowable<TransactionReceipt> retrieve(Web3j web3j, Credentials credentials, BigInteger amout) {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    MappingContract contract = MappingContract.load(Constants.MappingAddress, web3j, credentials, gasPrice, gasLimit);

                    String address = contract.checkAddress("abonus").sendAsync().get();
                    IBAbonusContract ibAbonusContract = IBAbonusContract.load(address, web3j, credentials, gasPrice, gasLimit);
                    TransactionReceipt transactionReceipt = ibAbonusContract.retrieve(amout).sendAsync().get();
                    return Flowable.just(transactionReceipt);
                });
    }

    /**
     * 存入Nest
     *
     * @param web3j
     * @param credentials
     * @param amout
     * @return
     */
    public static Flowable<TransactionReceipt> depositIn(Web3j web3j, Credentials credentials, BigInteger amout) {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    MappingContract contract = MappingContract.load(Constants.MappingAddress, web3j, credentials, gasPrice, gasLimit);

                    String address = contract.checkAddress("abonus").sendAsync().get();
                    IBAbonusContract ibAbonusContract = IBAbonusContract.load(address, web3j, credentials, gasPrice, gasLimit);
                    KLog.e(amout.toString());
                    TransactionReceipt transactionReceipt = ibAbonusContract.toChangeInto(amout).sendAsync().get();
                    return Flowable.just(transactionReceipt);
                });

    }

    //获取分红信息
    public static Flowable<Tuple9<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>> getAbonusInfo(Web3j web3j, String walletAddress) {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    MappingContract contract = MappingContract.load(Constants.MappingAddress, web3j, transactionManager, gasPrice, gasLimit);

                    String address = contract.checkAddress("abonus").sendAsync().get();
                    IBAbonusContract ibAbonusContract = IBAbonusContract.load(address, web3j, transactionManager, gasPrice, gasLimit);
                    Tuple9<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> result = ibAbonusContract.getInfo().sendAsync().get();
                    return Flowable.just(result);
                });
    }

    //get Dividend Amount 分红数额
    public static Flowable<BigInteger> getDividendAmount(Web3j web3j, String walletAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    MappingContract contract = MappingContract.load(Constants.MappingAddress, web3j, transactionManager, gasPrice, gasLimit);

                    String address = contract.checkAddress("abonus").sendAsync().get();
                    IBDividendContract ibDividendContract = IBDividendContract.load(address, web3j, transactionManager, gasPrice, gasLimit);
                    BigInteger ethValue = ibDividendContract.getnum().sendAsync().get();

                    return Flowable.just(ethValue);
                });

    }

    //get Dividend Nest Amount 参与分红Nest数额
    public static Flowable<BigInteger> getDividendNestAmount(Web3j web3j, String walletAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    MappingContract contract = MappingContract.load(Constants.MappingAddress, web3j, transactionManager, gasPrice, gasLimit);

                    String address = contract.checkAddress("abonus").sendAsync().get();
                    IBDividendContract ibDividendContract = IBDividendContract.load(address, web3j, transactionManager, gasPrice, gasLimit);
                    BigInteger nestAmount = ibDividendContract.allValue().sendAsync().get();

                    return Flowable.just(nestAmount);
                });

    }

    //get Dividend Info 分红信息
    public static Flowable<String[]> getDividendInfo(Web3j web3j, String walletAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    MappingContract contract = MappingContract.load(Constants.MappingAddress, web3j, transactionManager, gasPrice, gasLimit);
                    //分红合约地址
                    String address = contract.checkAddress("abonus").sendAsync().get();
                    IBDividendContract ibDividendContract = IBDividendContract.load(address, web3j, transactionManager, gasPrice, gasLimit);
                    //分红池金额
                    BigInteger ethValue = ibDividendContract.getnum().sendAsync().get();
                    //参与分红nest总额
                    BigInteger nestAmount = ibDividendContract.allValue().sendAsync().get();
                    String[] dividendInfo = new String[3];
                    dividendInfo[0] = address;
                    dividendInfo[1] = ethValue + "";
                    dividendInfo[2] = nestAmount + "";
                    return Flowable.just(dividendInfo);
                });

    }

    //get Nest Amount 已挖数量
    public static Flowable<BigInteger> getNestAmount(Web3j web3j, String walletAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    MappingContract contract = MappingContract.load(Constants.MappingAddress, web3j, transactionManager, gasPrice, gasLimit);

                    String address = contract.checkAddress("mining").sendAsync().get();
                    IBMinePoolContract ibMinePoolContract = IBMinePoolContract.load(address, web3j, transactionManager, gasPrice, gasLimit);
                    BigInteger nestValue = ibMinePoolContract.getNestAmount().sendAsync().get();

                    return Flowable.just(nestValue);
                });

    }

    //get MinePool Parameter 获取时间衰减和总数衰减
    public static Flowable<BigInteger[]> getMinePoolParameter(Web3j web3j, String walletAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    MappingContract contract = MappingContract.load(Constants.MappingAddress, web3j, transactionManager, gasPrice, gasLimit);

                    String address = contract.checkAddress("mining").sendAsync().get();
                    IBMinePoolContract ibMinePoolContract = IBMinePoolContract.load(address, web3j, transactionManager, gasPrice, gasLimit);
                    Tuple2<BigInteger, BigInteger> tuple2 = ibMinePoolContract.getParameter().sendAsync().get();
                    BigInteger[] minePoolParameter = new BigInteger[2];
                    minePoolParameter[0] = tuple2.getValue1();
                    minePoolParameter[1] = tuple2.getValue2();

                    return Flowable.just(minePoolParameter);
                });

    }


    //getMinePoolInfo 时间衰减和总数衰减
    public static Flowable<BigInteger[]> getMinePoolInfo(Web3j web3j, String walletAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    MappingContract contract = MappingContract.load(Constants.MappingAddress, web3j, transactionManager, gasPrice, gasLimit);

                    String address = contract.checkAddress("mining").sendAsync().get();
                    IBMinePoolContract ibMinePoolContract = IBMinePoolContract.load(address, web3j, transactionManager, gasPrice, gasLimit);
                    BigInteger[] minePoolParameter = new BigInteger[3];

                    BigInteger nestValue = ibMinePoolContract.getNestAmount().sendAsync().get();
                    //已挖总量
                    minePoolParameter[0] = nestValue;

                    Tuple2<BigInteger, BigInteger> tuple2 = ibMinePoolContract.getParameter().sendAsync().get();
                    //时间衰减
                    minePoolParameter[1] = tuple2.getValue1();
                    //总数衰减
                    minePoolParameter[2] = tuple2.getValue2();

                    return Flowable.just(minePoolParameter);
                });

    }

    //getMiningAttenuate  d经过了多少天、a：衰减参数
    public static Flowable<BigInteger[]> getMiningAttenuate(Web3j web3j, String walletAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                    BigInteger gasLimit = BigInteger.valueOf(1000000);

                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    MappingContract contract = MappingContract.load(Constants.MappingAddress, web3j, transactionManager, gasPrice, gasLimit);

                    String address = contract.checkAddress("borrowMining").sendAsync().get();
                    IBMiningContract ibMiningContract = IBMiningContract.load(address, web3j, transactionManager, gasPrice, gasLimit);
                    Tuple2<BigInteger, BigInteger> tuple2 = ibMiningContract.amountPartOne().sendAsync().get();
                    BigInteger[] mining = new BigInteger[2];
                    mining[0] = tuple2.getValue1();
                    mining[1] = tuple2.getValue2();

                    return Flowable.just(mining);
                });

    }


    //get IBContract Status
    public static IBContractStatues getIBContractStatus(IBContract contract) throws Exception {
        IBContractStatues ibContractStatues = new IBContractStatues();
        if (contract == null) {
            return ibContractStatues;
        }
        Tuple14<BigInteger, String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> result = contract.getContractInfo().sendAsync().get();

        ibContractStatues.setContractState(result.getValue1() + "");
        ibContractStatues.setBorrowerAddress(result.getValue2());
        ibContractStatues.setInvestorAddress(result.getValue3());
        ibContractStatues.setAmount(result.getValue4() + "");
        ibContractStatues.setCycle(result.getValue5() + "");

        ibContractStatues.setInterest(result.getValue6() + "");
        ibContractStatues.setMortgage(result.getValue7() + "");
        ibContractStatues.setNeedMortgage(result.getValue8() + "");
        ibContractStatues.setTokenAddress(result.getValue9() + "");

        ibContractStatues.setInvestmentTime(result.getValue10() + "");
        ibContractStatues.setEndTime(result.getValue11() + "");
        ibContractStatues.setExpire(result.getValue12() + "");
        ibContractStatues.setExpiryTime(result.getValue13() + "");
        ibContractStatues.setCreateTime(result.getValue14() + "");
        ibContractStatues.setContractAddress(contract.getContractAddress());
        KLog.w(ibContractStatues.toString());


        return ibContractStatues;

    }

    //get IBTUSDContract Status
    public static IBTUSDContractStatues getTUSDContractStatus(String contractAddress) throws Exception {

        IBTUSDContractStatues ibTusdContractStatues = new IBTUSDContractStatues();

        String jsonParams = "{\n" +
                "\t\"contractAddress\": \"" + contractAddress + "\"\n" +
                "}";
        Response<ContractDetailBean> execute = HttpUtil.getContractData(jsonParams).execute();
        if (execute.code() == 200) {
            if (execute.body().getCode().equals("200")) {
                ContractDetailBean.DataBean dataBean = execute.body().getData().get(0);

                ibTusdContractStatues.setContractState(dataBean.getState());
                ibTusdContractStatues.setBorrowerAddress(dataBean.getBorrowerAddress());
                ibTusdContractStatues.setInvestorAddress(dataBean.getInvestorAddress());
                ibTusdContractStatues.setAmount(dataBean.getAmount());
                ibTusdContractStatues.setCycle(dataBean.getCycle());

                ibTusdContractStatues.setInterest(dataBean.getInterest());
                ibTusdContractStatues.setMortgage(dataBean.getMortgage());
                ibTusdContractStatues.setNeedMortgage(dataBean.getNeedMortgage());
                ibTusdContractStatues.setTokenAddress(dataBean.getTokenAddress());

                ibTusdContractStatues.setInvestmentTime(dataBean.getInvestmentTime());
                ibTusdContractStatues.setEndTime(dataBean.getEndTime());
                ibTusdContractStatues.setExpire(dataBean.getExpire());
                ibTusdContractStatues.setExpiryTime(dataBean.getExpiryTime());
                ibTusdContractStatues.setCreateTime(dataBean.getCreateTime());
            } else {
                return ibTusdContractStatues;
            }
        } else {
            return ibTusdContractStatues;
        }


//        if (contract == null) {
//            return ibTusdContractStatues;
//        }
//        Tuple14<BigInteger, String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> result = contract.getContractInfo().sendAsync().get();

//        ibTusdContractStatues.setContractState(result.getValue1() + "");
//        ibTusdContractStatues.setBorrowerAddress(result.getValue2());
//        ibTusdContractStatues.setInvestorAddress(result.getValue3());
//        ibTusdContractStatues.setAmount(result.getValue4() + "");
//        ibTusdContractStatues.setCycle(result.getValue5() + "");
//
//        ibTusdContractStatues.setInterest(result.getValue6() + "");
//        ibTusdContractStatues.setMortgage(result.getValue7() + "");
//        ibTusdContractStatues.setNeedMortgage(result.getValue8() + "");
//        ibTusdContractStatues.setTokenAddress(result.getValue9() + "");
//
//        ibTusdContractStatues.setInvestmentTime(result.getValue10() + "");
//        ibTusdContractStatues.setEndTime(result.getValue11() + "");
//        ibTusdContractStatues.setExpire(result.getValue12() + "");
//        ibTusdContractStatues.setExpiryTime(result.getValue13() + "");
//        ibTusdContractStatues.setCreateTime(result.getValue14() + "");


//        try {
//            Tuple3<BigInteger, String, String> tokenInfo = contract.getTokenInfo().sendAsync().get();
//            ibTusdContractStatues.setContractType(tokenInfo.getValue1() + "");
//            ibTusdContractStatues.setBorrowerToken(tokenInfo.getValue2() + "");
//            ibTusdContractStatues.setLenderToken(tokenInfo.getValue3() + "");
//        } catch (Exception e) {
//            ibTusdContractStatues.setContractType("0");
//            ibTusdContractStatues.setBorrowerToken(result.getValue9() + "");
//            ibTusdContractStatues.setLenderToken("ETH");
//        }
//
        ibTusdContractStatues.setContractAddress(contractAddress);
//        KLog.w(ibTusdContractStatues.toString());


        return ibTusdContractStatues;
    }

    //get IBTUSDContract Status
    public static Flowable<ContractBean> getTUSDContractInfo(Web3j web3j, String walletAddress, String contractAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
//                    BigInteger gasPrice = new BigInteger("1000000000");
//                    BigInteger gasLimit = BigInteger.valueOf(1000000);
//                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
//                    IBTUSDContract ibTusdContract = IBTUSDContract.load(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
//
//                    ContractBean bean = getTusdContractBean(getTUSDContractStatus(ibTusdContract));
                    ContractBean bean = getTusdContractBean(getTUSDContractStatus(contractAddress));

                    return Flowable.just(bean);
                });

    }


    //Invest
    public static Flowable<String> investSend(Context context, Web3j web3j, Credentials credentials, String contractAddress, BigInteger gasPrice, BigInteger gasLimit, BigInteger value) {
        return Flowable.just(1)
                .flatMap(s -> {

                    BigInteger nonce = transactionNonce(context, web3j, credentials.getAddress());

                    final Function function = new Function(
                            "sendLendAsset",
                            Arrays.<Type>asList(),
                            Collections.<TypeReference<?>>emptyList());
                    String encodedFunction = FunctionEncoder.encode(function);
                    RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress, value, encodedFunction);

                    String hexValue = signData(rawTransaction, credentials);

//                    String txHash = web3j.ethSendRawTransaction(hexValue).sendAsync().get().getTransactionHash();
//                    return Flowable.just(txHash);

                    String jsonParams = "{\n" +
                            "\t\"hexValue\": \"" + hexValue + "\"\n" +
                            "}";

                    Response<NormalDataBean> execute = HttpUtil.sendTransaction(jsonParams).execute();
                    if (execute.code() == 200) {
                        NormalDataBean bean = execute.body();
                        if (TextUtils.equals("200", bean.getCode())) {
                            String txHash = bean.getData().get(0);
                            return Flowable.just(txHash);
                        } else {
                            throw new Exception("网络异常");
                        }
                    } else {
                        throw new Exception("网络异常");
                    }
                });
    }

    //Repayment
    public static Flowable<String> sendRepayment(Context context, Web3j web3j, Credentials credentials, String contractAddress, BigInteger gasPrice, BigInteger gasLimit, BigInteger value) {
        return Flowable.just(1)
                .flatMap(s -> {

                    BigInteger nonce = transactionNonce(context, web3j, credentials.getAddress());

                    final Function function = new Function(
                            "sendRepayment",
                            Arrays.<Type>asList(),
                            Collections.<TypeReference<?>>emptyList());
                    String encodedFunction = FunctionEncoder.encode(function);
                    RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress, value, encodedFunction);

                    String hexValue = signData(rawTransaction, credentials);

//                    String txHash = web3j.ethSendRawTransaction(hexValue).sendAsync().get().getTransactionHash();
//                    return Flowable.just(txHash);

                    String jsonParams = "{\n" +
                            "\t\"hexValue\": \"" + hexValue + "\"\n" +
                            "}";

                    Response<NormalDataBean> execute = HttpUtil.sendTransaction(jsonParams).execute();
                    if (execute.code() == 200) {
                        NormalDataBean bean = execute.body();
                        if (TextUtils.equals("200", bean.getCode())) {
                            String txHash = bean.getData().get(0);
                            return Flowable.just(txHash);
                        } else {
                            throw new Exception("网络异常");
                        }
                    } else {
                        throw new Exception("网络异常");
                    }
                });
    }


    //OverDue
    public static Flowable<String> applyForAssets(Context context, Web3j web3j, Credentials credentials, String contractAddress, BigInteger gasPrice, BigInteger gasLimit) {
        return Flowable.just(1)
                .flatMap(s -> {

                    BigInteger nonce = transactionNonce(context, web3j, credentials.getAddress());

                    final Function function = new Function(
                            "applyForAssets",
                            Arrays.<Type>asList(),
                            Collections.<TypeReference<?>>emptyList());
                    String encodedFunction = FunctionEncoder.encode(function);
                    RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress, encodedFunction);

                    String hexValue = signData(rawTransaction, credentials);

                    String txHash = web3j.ethSendRawTransaction(hexValue).sendAsync().get().getTransactionHash();
                    return Flowable.just(txHash);
                });
    }


    //cancel Contract
    public static Flowable<String> cancelContract(Context context, Web3j web3j, Credentials credentials, String contractAddress, BigInteger gasPrice, BigInteger gasLimit) {
        return Flowable.just(1)
                .flatMap(s -> {

                    BigInteger nonce = transactionNonce(context, web3j, credentials.getAddress());

                    final Function function = new Function(
                            "cancelContract",
                            Arrays.<Type>asList(),
                            Collections.<TypeReference<?>>emptyList());
                    String encodedFunction = FunctionEncoder.encode(function);
                    RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress, encodedFunction);

                    String hexValue = signData(rawTransaction, credentials);


                    String jsonParams = "{\n" +
                            "\t\"hexValue\": \"" + hexValue + "\"\n" +
                            "}";
                    Response<NormalDataBean> execute = HttpUtil.sendTransaction(jsonParams).execute();
                    if (execute.code()==200){
                        if (execute.body().getCode().equals("200")){
                            String txHash = execute.body().getData().get(0);
                            return Flowable.just(txHash);
                        }else {
                            throw new Exception("网络或服务器异常");
                        }
                    }else {
                        throw new Exception("网络或服务器异常");
                    }
                });
    }


    //TUSD MortgageETH 抵押以太坊
    public static Flowable<String> MortgageETH(Context context, Web3j web3j, Credentials credentials, String contractAddress, BigInteger gasPrice, BigInteger gasLimit, BigInteger weiValue) {
        return Flowable.just(1)
                .flatMap(s -> {

                    BigInteger nonce = transactionNonce(context, web3j, credentials.getAddress());

                    final Function function = new Function(
                            "payEth",
                            Arrays.<Type>asList(),
                            Collections.<TypeReference<?>>emptyList());
                    String encodedFunction = FunctionEncoder.encode(function);
                    RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress, weiValue, encodedFunction);

                    String hexValue = signData(rawTransaction, credentials);

                    String txHash = web3j.ethSendRawTransaction(hexValue).sendAsync().get().getTransactionHash();
                    return Flowable.just(txHash);
                });
    }

    //TUSD Invest
    public static Flowable<String> investmentContracts(Context context, Web3j web3j, Credentials credentials, String factoryAddress, String contractAddress, BigInteger gasPrice, BigInteger gasLimit) {
        return Flowable.just(1)
                .flatMap(s -> {

                    BigInteger nonce = transactionNonce(context, web3j, credentials.getAddress());
                    final Function function = new Function(
                            "investmentContracts",
                            Arrays.<Type>asList(new Address(contractAddress)),
                            Collections.<TypeReference<?>>emptyList());
                    String encodedFunction = FunctionEncoder.encode(function);


                    RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, factoryAddress, encodedFunction);

                    String hexValue = signData(rawTransaction, credentials);

                    String txHash = web3j.ethSendRawTransaction(hexValue).sendAsync().get().getTransactionHash();
                    return Flowable.just(txHash);
                });
    }

    //TUSD Repayment
    public static Flowable<String> sendRepayment(Context context, Web3j web3j, Credentials credentials, String factoryAddress, String contractAddress, BigInteger gasPrice, BigInteger gasLimit) {
        return Flowable.just(1)
                .flatMap(s -> {

                    BigInteger nonce = transactionNonce(context, web3j, credentials.getAddress());
                    final Function function = new Function(
                            "sendRepayment",
                            Arrays.<Type>asList(new Address(contractAddress)),
                            Collections.<TypeReference<?>>emptyList());
                    String encodedFunction = FunctionEncoder.encode(function);
                    RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, factoryAddress, encodedFunction);

                    String hexValue = signData(rawTransaction, credentials);

                    String txHash = web3j.ethSendRawTransaction(hexValue).sendAsync().get().getTransactionHash();
                    return Flowable.just(txHash);
                });
    }


    //getEthBanlance 查询以太币账户余额
    public static Flowable<String> getEthBanlance(Admin web3j, String userAddress) throws Exception {

//        return Flowable.just(1)
//                .flatMap(s -> {
//                    //获取指定钱包的以太币余额
//                    EthGetBalance balance = web3j.ethGetBalance(userAddress, DefaultBlockParameterName.PENDING).sendAsync().get();
//                    //eth默认会部18个0
//                    String decimal = toDecimal(18, balance.getBalance());
//                    return Flowable.just(decimal);
//                });
        RequestBalanceBean requestBalanceBean = new RequestBalanceBean();
        requestBalanceBean.setAddress(userAddress);
        requestBalanceBean.setContractAddress("666");
        String json = new Gson().toJson(requestBalanceBean);
        Response<NormalDataBean> execute = HttpUtil.getBalance(json).execute();
        int code = execute.code();
        if (200 == code) {
            NormalDataBean body = execute.body();
            if (TextUtils.equals("200", body.getCode())) {
                String s = toDecimal(18, new BigInteger(body.getData().get(0).trim()));
                return Flowable.just(s);
            } else {
                throw new Exception("网络异常");
            }
        } else {
            throw new Exception("网络异常");
        }

    }

    //getEthGasPrice 返回当前的gas价格,这个值由最近几个块的gas价格的中值决定
    public static Flowable<BigInteger> getEthGasPrice(Admin web3j) throws Exception {

        Response<NormalDataBean> execute = HttpUtil.getAverageGasPrice().execute();
        if (execute.code() == 200) {
            NormalDataBean body = execute.body();
            if (TextUtils.equals("200", body.getCode())) {
                BigInteger bigInteger = new BigInteger(body.getData().get(0).trim());
                return Flowable.just(bigInteger);
            } else {
                throw new Exception("网络异常");
            }
        } else {
            throw new Exception("网络异常");
        }

    }

    //转换成符合 decimal 的数值
    public static String toDecimal(int decimal, BigInteger integer) {
//		String substring = str.substring(str.length() - decimal);
        StringBuffer sbf = new StringBuffer("1");
        for (int i = 0; i < decimal; i++) {
            sbf.append("0");
        }
        String balance = new BigDecimal(integer).divide(new BigDecimal(sbf.toString()), decimal, BigDecimal.ROUND_DOWN).toPlainString();
        return balance;
    }


    //ETH 转账离线签名

    public static Flowable<String> rawTransaction(Context context, Web3j web3j, String to, BigInteger gasPrice, BigInteger gasLimit, String amount, HLWallet wallet, Credentials credentials) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger nonce = transactionNonce(context, web3j, credentials.getAddress());
                    String hexValue = signedEthTransactionData(to, nonce, gasPrice, gasLimit, amount, wallet, credentials);
                    String txHash = web3j.ethSendRawTransaction(hexValue).sendAsync().get().getTransactionHash();

                    return Flowable.just(txHash);
                });
    }

    public static String signedEthTransactionData(String to, BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String amount, HLWallet wallet, Credentials credentials) throws Exception {

        BigInteger amountInWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, to, amountInWei);
        return signData(rawTransaction, wallet, credentials);
    }

    private static String signData(RawTransaction rawTransaction, HLWallet wallet, Credentials credentials) throws Exception {
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        return Numeric.toHexString(signMessage);
    }

    private static String signData(RawTransaction rawTransaction, Credentials credentials) throws Exception {
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, ChainId.MAINNET, credentials);
        return Numeric.toHexString(signMessage);
    }

    //erc20转账
    public static Flowable<String> ERC20Transfer(Context context, Web3j web3j, Credentials credentials, String contractAddress, String toAddress, BigInteger gasPrice, BigInteger gasLimit, String amount) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigDecimal decimals = new BigDecimal("10").pow(getTokenDecimals(web3j, credentials.getAddress(), contractAddress));
                    BigInteger amountInWei = new BigDecimal(amount).multiply(decimals).toBigInteger();

                    BigInteger nonce = transactionNonce(context, web3j, credentials.getAddress());
                    Function function = new Function(
                            "transfer",
                            Arrays.asList(new Address(toAddress), new Uint256(amountInWei)),
                            Arrays.asList(new TypeReference<Type>() {
                            }));
                    String encodedFunction = FunctionEncoder.encode(function);
                    RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress, encodedFunction);

                    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction,ChainId.MAINNET, credentials);
                    String hexValue = Numeric.toHexString(signedMessage);

//                    EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
//                    if (ethSendTransaction.hasError()) {
//                        Log.w(TAG, ethSendTransaction.getError().getMessage());
//
//                    } else {
//                        String transactionHash = ethSendTransaction.getTransactionHash();
//                        Log.w(TAG, transactionHash);
//
//                    }

                    String jsonParams = "{\n" +
                            "\t\"hexValue\": \"" + hexValue + "\"\n" +
                            "}";
                    Response<NormalDataBean> execute = HttpUtil.sendTransaction(jsonParams).execute();
                    if (execute.code()==200){
                        if (execute.body().getCode().equals("200")){
                            String txHash = execute.body().getData().get(0);
                            return Flowable.just(txHash);
                        }else {
                            KLog.e("失败2");
                            throw new Exception("网络或服务器异常");
                        }
                    }else {
                        KLog.e("失败1");
                        throw new Exception("网络或服务器异常");
                    }

                });
    }

    //erc20 授权  允许_spender多次取回您的帐户，最高达_value金额
    public static Flowable<String> ERC20Approve(Context context, Web3j web3j, Credentials credentials, String tokenAddress, String addressSpender, BigInteger gasPrice, BigInteger gasLimit, String amount) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger amountInWei = new BigInteger(amount);

                    BigInteger nonce = transactionNonce(context, web3j, credentials.getAddress());

                    final Function function = new Function(
                            "approve",
                            Arrays.<Type>asList(new Address(addressSpender),
                                    new Uint256(amountInWei)),
                            Collections.<TypeReference<?>>emptyList());
                    String encodedFunction = FunctionEncoder.encode(function);
                    RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, tokenAddress, encodedFunction);

                    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                    String hexValue = Numeric.toHexString(signedMessage);

                    EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
                    if (ethSendTransaction.hasError()) {
                        Log.w(TAG, ethSendTransaction.getError().getMessage());

                    } else {
                        String transactionHash = ethSendTransaction.getTransactionHash();
                        Log.w(TAG, transactionHash);

                    }
                    return Flowable.just(ethSendTransaction.getTransactionHash());
                });
    }

    //erc20 授权查询
    public static Flowable<BigInteger> ERC20Allowance(Web3j web3j, String tokenAddress, String walletAddress, String contractAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger gasPrice = BigInteger.valueOf(1000000000);
                    BigInteger gasLimit = BigInteger.valueOf(1000000);
                    ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
                    ERC20Manager erc20Manager = ERC20Manager.load(tokenAddress, web3j, transactionManager, gasPrice, gasLimit);
                    BigInteger remaining = null;
                    try {
                        remaining = erc20Manager.allowance(walletAddress, contractAddress).sendAsync().get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return Flowable.just(remaining);
                });
    }

    //Token转账gas预估
    public static Flowable<BigInteger> getTokenEstimateGas(Web3j web3j, String walletAddress, String contractAddress, String toAddress, String amount) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigDecimal decimals = new BigDecimal("10").pow(getTokenDecimals(web3j, walletAddress, contractAddress));
                    BigInteger amountInWei = new BigDecimal(amount).multiply(decimals).toBigInteger();

                    EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(walletAddress, DefaultBlockParameterName.PENDING).sendAsync().get();
                    BigInteger nonce = ethGetTransactionCount.getTransactionCount();

                    Function function = new Function(
                            "transfer",
                            Arrays.asList(new Address(toAddress), new Uint256(amountInWei)),
                            Arrays.asList(new TypeReference<Type>() {
                            }));
                    String encodedFunction = FunctionEncoder.encode(function);

                    Transaction transaction = Transaction.createFunctionCallTransaction(walletAddress, nonce, null, null, contractAddress, encodedFunction);

                    BigInteger value = web3j.ethEstimateGas(transaction).sendAsync().get().getAmountUsed();
                    KLog.w("TokenEstimateGas = " + value);

                    return Flowable.just(value);
                });
    }

    //get Token Balance
    public static Flowable<String> getTokenBalance(Web3j web3j, String walletAddress, String contractAddress) throws Exception {
        RequestBalanceBean bean = new RequestBalanceBean();
        bean.setAddress(walletAddress);
        bean.setContractAddress(contractAddress);
        String json = new Gson().toJson(bean);
        Response<NormalDataBean> execute = HttpUtil.getBalance(json).execute();
        int code = execute.code();
        if (code == 200) {
            NormalDataBean body = execute.body();
            if (TextUtils.equals("200", body.getCode())) {
                String decimals = getTokenDecimals(contractAddress);
                String value = toDecimal(Integer.parseInt(decimals), new BigInteger(body.getData().get(0).trim()));
                return Flowable.just(value);
            } else {
                throw new Exception("网络异常");
            }
        } else {
            throw new Exception("网络异常");
        }


    }

    private static String getTokenDecimals(String tokenAddress) {
        for (TokenProfileBean tokenProfileBean : Constants.tokenProfileBeans) {
            if (TextUtils.equals(tokenAddress, tokenProfileBean.getAddress())) {
                return tokenProfileBean.getDecimals();
            }
        }
        return "";
    }

    //获取代币余额
    public static Flowable<String> tokenBalance(Web3j web3j, String walletAddress, String tokenAddress) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    String data = "0x70a08231000000000000000000000000" + walletAddress.replace("0x", "");
                    String result = web3j.ethCall(Transaction.createEthCallTransaction(walletAddress, tokenAddress, data), DefaultBlockParameterName.PENDING).sendAsync().get().getValue();
                    KLog.w("token balance = " + result);
                    KLog.w("result = " + Numeric.decodeQuantity(result));
                    if (result.equals("0x")) {
                        return Flowable.just("0");
                    } else {
                        return Flowable.just(toDecimal(18, Numeric.decodeQuantity(result)));
                    }
                });
    }

    //获取 Token Decimals 位数
    public static int getTokenDecimals(Web3j web3j, String walletAddress, String contractAddress) {
//        BigInteger gasPrice = BigInteger.valueOf(1000000000);
//        BigInteger gasLimit = BigInteger.valueOf(1000000);
//        ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
//        ERC20Manager erc20Manager = ERC20Manager.load(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
//        BigInteger decimals = null;
//        try {
//            decimals = erc20Manager.decimals().sendAsync().get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return decimals.intValue();
        return 18;

    }


    //get ContractBean
    public static ContractBean getContractBean(IBContractStatues ibContractStatues) {

        ContractBean contractBean = new ContractBean();
        //合约地址
        contractBean.setContractAddress(ibContractStatues.getContractAddress());
        //合约状态
        contractBean.setContractState(ibContractStatues.getContractState());
        //借方地址
        contractBean.setBorrowerAddress(ibContractStatues.getBorrowerAddress());
        //投资人地址
        contractBean.setInvestorAddress(ibContractStatues.getInvestorAddress());
        //投资数量
        contractBean.setAmount(ibContractStatues.getAmount());
        //周期
        contractBean.setCycle(ibContractStatues.getCycle());
        //利率
        contractBean.setInterest(ibContractStatues.getInterest());
        //抵押资产数量
        contractBean.setMortgage(ibContractStatues.getMortgage());
        //手续费
        contractBean.setServiceCharge(new BigDecimal(ibContractStatues.getAmount()).divide(new BigDecimal("100")) + "");
        // 实际到账
        contractBean.setActualAccount(new BigDecimal(ibContractStatues.getAmount()).subtract(new BigDecimal(contractBean.getServiceCharge())) + "");
        // 需转入抵押资产
        contractBean.setNeedMortgage(ibContractStatues.getNeedMortgage());
        // Token地址
        contractBean.setTokenAddress(ibContractStatues.getTokenAddress());
        // 投资时间
        contractBean.setInvestmentTime(ibContractStatues.getInvestmentTime());
        //结束时间
        contractBean.setEndTime(ibContractStatues.getEndTime());
        //到期本息
        contractBean.setExpire(ibContractStatues.getExpire());
        //到期时间
        contractBean.setExpiryTime(ibContractStatues.getExpiryTime());
        //创建时间
        contractBean.setCreateTime(ibContractStatues.getCreateTime());

        KLog.w("contractBean : " + contractBean.toString());
        return contractBean;
    }

    //get Tusd ContractBean
    public static ContractBean getTusdContractBean(IBTUSDContractStatues statues) {

        ContractBean contractBean = new ContractBean();
        //合约地址
        contractBean.setContractAddress(statues.getContractAddress());
        //合约状态
        contractBean.setContractState(statues.getContractState());
        //借方地址
        contractBean.setBorrowerAddress(statues.getBorrowerAddress());
        //投资人地址
        contractBean.setInvestorAddress(statues.getInvestorAddress());
        //投资数量
        contractBean.setAmount(statues.getAmount());
        //周期
        contractBean.setCycle(statues.getCycle());
        //利率
        contractBean.setInterest(statues.getInterest());
        //抵押资产数量
        contractBean.setMortgage(statues.getMortgage());

        // 需转入抵押资产
        contractBean.setNeedMortgage(statues.getNeedMortgage());
        // Token地址
        contractBean.setTokenAddress(statues.getTokenAddress());
        // 投资时间
        contractBean.setInvestmentTime(statues.getInvestmentTime());
        //结束时间
        contractBean.setEndTime(statues.getEndTime());
        //到期本息
        contractBean.setExpire(statues.getExpire());
        //到期时间
        contractBean.setExpiryTime(statues.getExpiryTime());
        //创建时间
        contractBean.setCreateTime(statues.getCreateTime());
        //合约类型
        contractBean.setContractType(statues.getContractType());
        //抵押资产地址
        contractBean.setBorrowerToken(statues.getBorrowerToken());
        //借币token
        contractBean.setLenderToken(statues.getLenderToken());

        if (statues.getContractType() == null || statues.getContractType().equals("0")) {
            //手续费 1%
            contractBean.setServiceCharge(new BigDecimal(statues.getAmount()).divide(new BigDecimal("100")) + "");
            contractBean.setActualAccount(new BigDecimal(statues.getAmount()).subtract(new BigDecimal(contractBean.getServiceCharge())) + "");
        } else if (statues.getContractType().equals("1")) {
            //手续费 0.5%
            contractBean.setServiceCharge(new BigDecimal(statues.getMortgage()).divide(new BigDecimal("200")) + "");
            contractBean.setActualAccount(statues.getAmount());
        } else if (statues.getContractType().equals("2")) {
            //无手续费
            contractBean.setServiceCharge("0");
            contractBean.setActualAccount(statues.getAmount());
        }

        KLog.w("contractBean : " + contractBean.toString());
        return contractBean;
    }


}
