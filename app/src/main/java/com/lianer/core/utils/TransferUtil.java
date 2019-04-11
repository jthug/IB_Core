package com.lianer.core.utils;

import com.lianer.common.utils.KLog;
import com.lianer.core.app.Constants;
import com.lianer.core.manager.ERC20Manager;
import com.lianer.core.SmartContract.TokenERC20;
import com.lianer.core.model.HLWallet;
import com.orhanobut.logger.Logger;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.AdminFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import io.reactivex.Flowable;

import static org.web3j.tx.ManagedTransaction.GAS_PRICE;
import static org.web3j.tx.Transfer.GAS_LIMIT;

public class TransferUtil {
    private static final String netUrl = Constants.INFURA_NET_URL;
    private static Admin mWeb3j;

    public static Admin getWeb3j() {
        if (mWeb3j == null) {
            mWeb3j = AdminFactory.build(new HttpService(netUrl));
        }
        return mWeb3j;
    }


    /**
     * 获取ERC20 token余额
     */
    public static BigInteger getERC20Balance(Web3j web3j, String ERCContractAddress, String password, WalletFile walletFile, String tokenShowAddress) throws CipherException {
        ECKeyPair keyPair = Wallet.decrypt(password, walletFile);

        Credentials credentials = Credentials.create(keyPair);

        ERC20Manager erc20Manager = ERC20Manager.load(ERCContractAddress, web3j, credentials, GAS_PRICE, GAS_LIMIT);
        BigInteger tokenBalance = null;
        try {
            tokenBalance = erc20Manager.balanceOf(tokenShowAddress).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tokenBalance;
    }

    /**
     * 查询以太币账户余额
     *
     * @throws IOException
     */
    public static String getEthBanlance(Admin web3j, String userAddress) throws Exception {
        //获取指定钱包的以太币余额
        EthGetBalance balance = web3j.ethGetBalance(userAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
        //eth默认会部18个0
        String decimal = toDecimal(18, balance.getBalance());
        Logger.i("ETH:" + decimal);
        return decimal;
    }

    /**
     * 转换成符合 decimal 的数值
     *
     * @param decimal
     * @param integer
     * @return
     */
    public static String toDecimal(int decimal, BigInteger integer) {
//		String substring = str.substring(str.length() - decimal);
        StringBuffer sbf = new StringBuffer("1");
        for (int i = 0; i < decimal; i++) {
            sbf.append("0");
        }
        String balance = new BigDecimal(integer).divide(new BigDecimal(sbf.toString()), decimal, BigDecimal.ROUND_DOWN).toPlainString();
        return balance;
    }

    public static Flowable<String> rawTransaction(Admin web3j,
                                                  String to,
                                                  BigInteger gasPrice,
                                                  BigInteger gasLimit,
                                                  String amount,
                                                  HLWallet wallet,
                                                  Credentials credentials) throws Exception {
        return Flowable.just(1)
                .flatMap(s -> {
                    BigInteger nonce = web3j.ethGetTransactionCount(wallet.getAddress(), DefaultBlockParameterName.PENDING).sendAsync().get().getTransactionCount();
//                    BigInteger nonce = new BigInteger("90");
                    KLog.w("nonce:" + nonce);
                    String hexValue = signedEthTransactionData(to, nonce, gasPrice, gasLimit, amount, wallet, credentials);
                    String txHash = web3j.ethSendRawTransaction(hexValue).sendAsync().get().getTransactionHash();

                    return Flowable.just(txHash);
                });
    }


    /**
     * ETH 转账离线签名
     *
     * @param to          转入的钱包地址
     * @param nonce       以太坊nonce
     * @param gasPrice    gasPrice
     * @param gasLimit    gasLimit
     * @param amount      转账的eth数量
     * @param wallet      钱包对象
     * @param credentials 密码
     * @return 签名data
     */
    public static String signedEthTransactionData(String to,
                                                  BigInteger nonce,
                                                  BigInteger gasPrice,
                                                  BigInteger gasLimit,
                                                  String amount,
                                                  HLWallet wallet,
                                                  Credentials credentials) throws Exception {
//        // 把十进制的转换成ETH的Wei, 1ETH = 10^18 Wei
//        BigDecimal amountInWei = Convert.toWei(amount, Convert.Unit.ETHER);
        BigInteger amountInWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, to, amountInWei);
        return signData(rawTransaction, wallet, credentials);
    }

    private static String signData(RawTransaction rawTransaction, HLWallet wallet, Credentials credentials) throws Exception {
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        return Numeric.toHexString(signMessage);
    }

    public String signContractTransaction(String contractAddress,
                                          String to,
                                          BigInteger nonce,
                                          BigInteger gasPrice,
                                          BigInteger gasLimit,
                                          BigDecimal amount,
                                          BigDecimal decimal,
                                          HLWallet wallet,
                                          Credentials credentials) throws Exception {
        BigDecimal realValue = amount.multiply(decimal);
        Function function = new Function("transfer",
                Arrays.asList(new Address(to), new Uint256(realValue.toBigInteger())),
                Collections.emptyList());
        String data = FunctionEncoder.encode(function);
        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                contractAddress,
                data);
        return signData(rawTransaction, wallet, credentials);
    }


    /**
     * erc20转账
     *
     * @param credentials
     * @param toAddress   收款人地址
     * @param amount      代币数量
     */
    public static String ERC20Transfer(Credentials credentials, String contractAddress, String toAddress, BigInteger gasPrice, BigInteger gasLimit, String amount) throws Exception {

        BigInteger amountInWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
        Web3j web3j = Web3jFactory.build(new HttpService(netUrl));
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                credentials.getAddress(), DefaultBlockParameterName.PENDING).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(toAddress), new Uint256(amountInWei)),
                Arrays.asList(new TypeReference<Type>() {
                }));
        String encodedFunction = FunctionEncoder.encode(function);
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, contractAddress, encodedFunction);

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
        if (ethSendTransaction.hasError()) {
            KLog.w("hasError", ethSendTransaction.getError().getMessage());

        } else {
            String transactionHash = ethSendTransaction.getTransactionHash();
            KLog.w("transactionHash", transactionHash);

        }
        return ethSendTransaction.getTransactionHash();
    }

    private String getTokenBalance(String contractAddress, Credentials credentials) throws Exception {
        Web3j web3j = Web3jFactory.build(new HttpService(netUrl));
        Contract erc20Contract = getERC20Contract(web3j, contractAddress, credentials);
        return getERC20Balance(erc20Contract, credentials.getAddress());

    }

    /**
     * 查询erc 20 代币价格
     *
     * @throws Exception
     */
    public static String getERC20Balance(Contract contract, String userAddress) throws Exception {
        RemoteCall<BigInteger> balanceOf = ((TokenERC20) contract).balanceOf(userAddress);
        BigInteger send2 = balanceOf.sendAsync().get();

        String result = toDecimal(18, send2);

        return result;
    }

    /**
     * 获得 erc20 合约 实例
     *
     * @return
     */
    public static Contract getERC20Contract(Web3j web3j, String contractAddress, Credentials credentials) {
        BigInteger GAS_PRICE = Contract.GAS_PRICE;
        BigInteger GAS_LIMIT = Contract.GAS_LIMIT;
        TokenERC20 contract = TokenERC20.load(
                contractAddress, web3j, credentials, GAS_PRICE, GAS_LIMIT);
        return contract;
    }

    /**
     * ETH 转帐gas 预估
     *
     * @param wallet
     * @param to
     * @param amount
     * @return
     * @throws Exception
     */
    public static BigInteger getEthEstimateGas(HLWallet wallet, String to, String amount) throws Exception {
        Web3j web3j = Web3jFactory.build(new HttpService(netUrl));
        BigInteger amountInWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                wallet.getAddress(), DefaultBlockParameterName.PENDING).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        Transaction transaction = Transaction.createEtherTransaction(wallet.getAddress(), nonce, null, null, to, amountInWei);

        BigInteger value = web3j.ethEstimateGas(transaction).sendAsync().get().getAmountUsed();
        KLog.w("EthEstimateGas = " + value);
        return value;

    }




    /**
     * hash查询交易详情
     *
     * @throws
     */
    public static org.web3j.protocol.core.methods.response.Transaction getTransaction(Web3j web3j, String txHash) throws Exception {

        return web3j.ethGetTransactionByHash(txHash).sendAsync().get().getTransaction();
    }

//    /**
//     * 获取代币余额
//     *
//     * @throws
//     */
//    public static String getTokenBalance(Web3j web3j, String walletAddress, String tokenAddress) throws Exception {
//        String data = "0x70a08231000000000000000000000000" + walletAddress.replace("0x", "");
//        String result = web3j.ethCall(Transaction.createEthCallTransaction(walletAddress, tokenAddress, data), DefaultBlockParameterName.PENDING).sendAsync().get().getValue();
//        KLog.w("getTokenBalance = " + result);
//        if (result.equals("0x")) {
//            return "0";
//        } else {
//            return toDecimal(18, Numeric.decodeQuantity(result));
//        }
//
//    }

    /**
     * 获取代币余额
     *
     * @throws
     */
    public static String getTokenBalance(Web3j web3j, String walletAddress, String tokenAddress) throws Exception {
        BigInteger gasPrice =  BigInteger.valueOf(1000000000);
        BigInteger gasLimit = BigInteger.valueOf(1000000);
        ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletAddress);
        ERC20Manager erc20Manager = ERC20Manager.load(tokenAddress, web3j, transactionManager, gasPrice, gasLimit);
        BigInteger tokenBalance = null;
        BigInteger decimals =null;
        try {
            decimals = erc20Manager.decimals().sendAsync().get();
            tokenBalance = erc20Manager.balanceOf(walletAddress).sendAsync().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(decimals == null){
            return "0";
        }
        String value = toDecimal(decimals.intValue(), tokenBalance);
        KLog.w("tokenBalance = " +tokenBalance);
        KLog.w("decimals = " +decimals);
        KLog.w("value = " +value);
        return value;
    }

//    public static  IBContract  deploymentContract( Web3j web3j,Credentials credentials, BigInteger gasPrice, BigInteger gasLimit,int assetsAmount,int ethAmount,  String tokenAddress,int cycle, int interest ) throws Exception {
//        //抵押金额（代币）
//        BigInteger borrowerAmount = new BigInteger(assetsAmount+"");
//        //贷款金额（ETH）
//        BigInteger lenderAmount = new BigInteger(ethAmount+"");

//        BigInteger tokenId =  new BigInteger(tokenAddress);
//        //限制天数
//        BigInteger limitdays= new BigInteger(cycle+"");
//        //利率
//        BigInteger interestRate =  new BigInteger(interest+"");
//
//        //部署智能合约
//        IBContract contract = IBContract.deploy(web3j,credentials, gasPrice,gasLimit,borrowerAmount,lenderAmount,tokenId,limitdays,interestRate).sendAsync().get();
//
//        return contract;
//    }


//    public static Flowable<IBContract> deploymentContract(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, int assetsAmount, int ethAmount, String tokenAddress, int cycle, int interest) throws Exception {
//        return Flowable.just(1)
//                .flatMap(s -> {
//                    //抵押金额（代币）
//                    BigInteger borrowerAmount = new BigInteger(assetsAmount + "");
//                    //贷款金额（ETH）
//                    BigInteger lenderAmount = new BigInteger(ethAmount + "");
//
//                    BigInteger tokenId = new BigInteger(tokenAddress);
//                    //限制天数
//                    BigInteger limitdays = new BigInteger(cycle + "");
//                    //利率
//                    BigInteger interestRate = new BigInteger(interest + "");
//
//                    //部署智能合约
//                    IBContract contract = IBContract.deploy(web3j, credentials, gasPrice, gasLimit, borrowerAmount, lenderAmount, tokenId, limitdays, interestRate).sendAsync().get();
//                    return Flowable.just(contract);
//                });
//    }

    public static TransactionReceipt getContractDeployStatus(String hash) {
        Web3j web3j = Web3jFactory.build(new HttpService(netUrl));
        EthGetTransactionReceipt transactionReceipt = null;
        try {
            transactionReceipt = web3j.ethGetTransactionReceipt(hash).sendAsync().get();
            if (transactionReceipt == null) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        return transactionReceipt.getTransactionReceipt();

    }

    public static Flowable<TransactionReceipt> getTtransactionReceipt(String hash) {
        Web3j web3j = Web3jFactory.build(new HttpService(netUrl));
        EthGetTransactionReceipt transactionReceipt = null;
        try {
            transactionReceipt = web3j.ethGetTransactionReceipt(hash).sendAsync().get();
            if (transactionReceipt == null) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        return transactionReceipt.getTransactionReceipt() != null ? Flowable.just(transactionReceipt.getTransactionReceipt()) : null;

    }


}
