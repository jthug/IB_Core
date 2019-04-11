package com.lianer.core.SmartContract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.3.1.
 */
public class IBMiningContract extends Contract {
    private static final String BINARY = "60806040526050600055601460015534801561001a57600080fd5b506040516020806112ca8339810180604052602081101561003a57600080fd5b505160088054600160a060020a031916600160a060020a038084169190911791829055604080517f8fe77e86000000000000000000000000000000000000000000000000000000008152602060048201819052600660248301527f6d696e696e670000000000000000000000000000000000000000000000000000604483015291519390921692638fe77e86926064808201939291829003018186803b1580156100e357600080fd5b505afa1580156100f7573d6000803e3d6000fd5b505050506040513d602081101561010d57600080fd5b505160068054600160a060020a031916600160a060020a0392831617905560088054604080517f8fe77e8600000000000000000000000000000000000000000000000000000000815260206004820181905260248201949094527f6c6f616e44617461000000000000000000000000000000000000000000000000604482015290519190931692638fe77e86926064808301939192829003018186803b1580156101b657600080fd5b505afa1580156101ca573d6000803e3d6000fd5b505050506040513d60208110156101e057600080fd5b505160078054600160a060020a03909216600160a060020a03199092169190911790555060026020819052683635c9adc5dea000007fac33ff75c19e70fe83507db0d683fd3465c996598dc972688b7ace676c89077b55682b5e3af16b188000007fe90b7bceb6e7df5418fb78d8ee546e97c83a08bbccc01a0644d599ccd2a7c2e055600052682086ac3510526000007f679795a0195a1b76cdebb7c51d74e058aee92919b8c3389af86ef24535e8a28c5542600355611025806102a56000396000f3fe6080604052600436106100795760e060020a60003504632cdc9050811461007e57806341872fb9146100dd578063a638980c1461012a578063a781e7f814610165578063bd2bbe1514610198578063cc7b3d74146101d3578063d2256a2f14610201578063f66f282a14610216578063f785ccd01461022b575b600080fd5b34801561008a57600080fd5b506100b7600480360360408110156100a157600080fd5b5080359060200135600160a060020a031661025b565b604080519485526020850193909352838301919091526060830152519081900360800190f35b3480156100e957600080fd5b506101286004803603608081101561010057600080fd5b50803590600160a060020a036020820135811691604081013582169160609091013516610325565b005b34801561013657600080fd5b506101286004803603604081101561014d57600080fd5b50600160a060020a03813516906020013515156104a7565b34801561017157600080fd5b506101286004803603602081101561018857600080fd5b5035600160a060020a031661055a565b3480156101a457600080fd5b50610128600480360360408110156101bb57600080fd5b50600160a060020a03813516906020013515156107d1565b3480156101df57600080fd5b506101e8610884565b6040805192835260208301919091528051918290030190f35b34801561020d57600080fd5b506101e8610925565b34801561022257600080fd5b506101e86109ae565b34801561023757600080fd5b506101286004803603604081101561024e57600080fd5b5080359060200135610a35565b600654604080517f40f4f7b9000000000000000000000000000000000000000000000000000000008152815160009384938493849384938493600160a060020a03909116926340f4f7b99260048083019392829003018186803b1580156102c157600080fd5b505afa1580156102d5573d6000803e3d6000fd5b505050506040513d60408110156102eb57600080fd5b5080516020909101519092509050600061030488610ae2565b90508183826103128c610b47565b929c919b50995090975095505050505050565b600754604080517fd7b124540000000000000000000000000000000000000000000000000000000081523360048201529051600160a060020a039092169163d7b1245491602480820192602092909190829003018186803b15801561038957600080fd5b505afa15801561039d573d6000803e3d6000fd5b505050506040513d60208110156103b357600080fd5b505115156001146103c357600080fd5b600654604080517f40f4f7b900000000000000000000000000000000000000000000000000000000815281516000938493600160a060020a03909116926340f4f7b99260048083019392829003018186803b15801561042157600080fd5b505afa158015610435573d6000803e3d6000fd5b505050506040513d604081101561044b57600080fd5b5080516020909101519092509050600061046484610ae2565b9050600061047488848487610c84565b90506000610480610cc0565b905061049c610495838363ffffffff610ce816565b8989610d33565b505050505050505050565b6008546040805160e060020a63a3bf06f10281523360048201529051600160a060020a039092169163a3bf06f191602480820192602092909190829003018186803b1580156104f557600080fd5b505afa158015610509573d6000803e3d6000fd5b505050506040513d602081101561051f57600080fd5b5051151560011461052f57600080fd5b600160a060020a03919091166000908152600460205260409020805460ff1916911515919091179055565b6008546040805160e060020a63a3bf06f10281523360048201529051600160a060020a039092169163a3bf06f191602480820192602092909190829003018186803b1580156105a857600080fd5b505afa1580156105bc573d6000803e3d6000fd5b505050506040513d60208110156105d257600080fd5b505115156001146105e257600080fd5b6008805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a038381169190911791829055604080517f8fe77e86000000000000000000000000000000000000000000000000000000008152602060048201819052600660248301527f6d696e696e670000000000000000000000000000000000000000000000000000604483015291519390921692638fe77e86926064808201939291829003018186803b15801561069657600080fd5b505afa1580156106aa573d6000803e3d6000fd5b505050506040513d60208110156106c057600080fd5b50516006805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392831617905560088054604080517f8fe77e8600000000000000000000000000000000000000000000000000000000815260206004820181905260248201949094527f6c6f616e44617461000000000000000000000000000000000000000000000000604482015290519190931692638fe77e86926064808301939192829003018186803b15801561077657600080fd5b505afa15801561078a573d6000803e3d6000fd5b505050506040513d60208110156107a057600080fd5b50516007805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0390921691909117905550565b6008546040805160e060020a63a3bf06f10281523360048201529051600160a060020a039092169163a3bf06f191602480820192602092909190829003018186803b15801561081f57600080fd5b505afa158015610833573d6000803e3d6000fd5b505050506040513d602081101561084957600080fd5b5051151560011461085957600080fd5b600160a060020a03919091166000908152600560205260409020805460ff1916911515919091179055565b60035460009081904203816108a2826201518063ffffffff610ce816565b9050610e428111156108b35750610e425b600260008181526020919091527f679795a0195a1b76cdebb7c51d74e058aee92919b8c3389af86ef24535e8a28c54905b8281101561091a576109106103e8610904846103e763ffffffff610fbe16565b9063ffffffff610ce816565b91506001016108e4565b509093509150509091565b6003546000908190420381610943826201518063ffffffff610ce816565b9050610e428111156109545750610e425b6001600090815260026020527fe90b7bceb6e7df5418fb78d8ee546e97c83a08bbccc01a0644d599ccd2a7c2e054905b8281101561091a576109a46103e8610904846103e763ffffffff610fbe16565b9150600101610984565b60035460009081904203816109cc826201518063ffffffff610ce816565b9050610e428111156109dd5750610e425b600080805260026020527fac33ff75c19e70fe83507db0d683fd3465c996598dc972688b7ace676c89077b54905b8281101561091a57610a2b6103e8610904846103e763ffffffff610fbe16565b9150600101610a0b565b6008546040805160e060020a63a3bf06f10281523360048201529051600160a060020a039092169163a3bf06f191602480820192602092909190829003018186803b158015610a8357600080fd5b505afa158015610a97573d6000803e3d6000fd5b505050506040513d6020811015610aad57600080fd5b50511515600114610abd57600080fd5b60008211610aca57600080fd5b60008111610ad757600080fd5b600091909155600155565b600160a060020a03811660009081526004602052604081205460ff16151560011415610b105750600a610b42565b600160a060020a03821660009081526005602052604090205460ff16151560011415610b3e57506008610b42565b5060015b919050565b600354600090420381610b63826201518063ffffffff610ce816565b9050610e42811115610b745750610e425b600068056bc75e2d63100000851015610bb857506000805260026020527fac33ff75c19e70fe83507db0d683fd3465c996598dc972688b7ace676c89077b54610c4e565b68056bc75e2d631000008510158015610bd95750683635c9adc5dea0000085105b15610c105750600160005260026020527fe90b7bceb6e7df5418fb78d8ee546e97c83a08bbccc01a0644d599ccd2a7c2e054610c4e565b683635c9adc5dea000008510610c4e5750600260008190526020527f679795a0195a1b76cdebb7c51d74e058aee92919b8c3389af86ef24535e8a28c545b60005b82811015610c7b57610c716103e8610904846103e763ffffffff610fbe16565b9150600101610c51565b50949350505050565b600080610c9086610b47565b9050610cb681610caa858188818c8c63ffffffff610fbe16565b9063ffffffff610fbe16565b9695505050505050565b6000670de0b6b3a7640000610ce2600a610caa8181858063ffffffff610fbe16565b91505090565b6000808211610cf357fe5b60008284811515610d0057fe5b0490508284811515610d0e57fe5b068184020184141515610d1d57fe5b8284811515610d2857fe5b049150505b92915050565b6000610d4b606461090486601463ffffffff610fbe16565b600654600054919250600160a060020a031690631b49583b90610d7f9060649061090490610caa8a8863ffffffff610fe716565b856040518363ffffffff1660e060020a0281526004018083815260200182600160a060020a0316600160a060020a0316815260200192505050600060405180830381600087803b158015610dd257600080fd5b505af1158015610de6573d6000803e3d6000fd5b5050600654600154600160a060020a039091169250631b49583b9150610e1d9060649061090490610caa8a8863ffffffff610fe716565b846040518363ffffffff1660e060020a0281526004018083815260200182600160a060020a0316600160a060020a0316815260200192505050600060405180830381600087803b158015610e7057600080fd5b505af1158015610e84573d6000803e3d6000fd5b5050600654600854604080517f8fe77e86000000000000000000000000000000000000000000000000000000008152602060048201819052600760248301527f6765746e6573740000000000000000000000000000000000000000000000000060448301529151600160a060020a039485169650631b49583b9550879490931692638fe77e8692606480840193919291829003018186803b158015610f2857600080fd5b505afa158015610f3c573d6000803e3d6000fd5b505050506040513d6020811015610f5257600080fd5b50516040805160e060020a63ffffffff86160281526004810193909352600160a060020a03909116602483015251604480830192600092919082900301818387803b158015610fa057600080fd5b505af1158015610fb4573d6000803e3d6000fd5b5050505050505050565b6000821515610fcf57506000610d2d565b50818102818382811515610fdf57fe5b0414610d2d57fe5b600082821115610ff357fe5b5090039056fea165627a7a72305820ec39249c5274dc06eb7b94004451801bb404863408fc954ec862dc66a7317c2b0029";

    protected IBMiningContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected IBMiningContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<Tuple4<BigInteger, BigInteger, BigInteger, BigInteger>> checkMining(BigInteger amount, String token) {
        final Function function = new Function("checkMining", 
                Arrays.<Type>asList(new Uint256(amount),
                new org.web3j.abi.datatypes.Address(token)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple4<BigInteger, BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple4<BigInteger, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple4<BigInteger, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<BigInteger, BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> startMining(BigInteger money, String borrower, String lender, String token) {
        final Function function = new Function(
                "startMining", 
                Arrays.<Type>asList(new Uint256(money),
                new org.web3j.abi.datatypes.Address(borrower), 
                new org.web3j.abi.datatypes.Address(lender), 
                new org.web3j.abi.datatypes.Address(token)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> addMarket(String token, Boolean allow) {
        final Function function = new Function(
                "addMarket", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(token), 
                new org.web3j.abi.datatypes.Bool(allow)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> changeMapping(String map) {
        final Function function = new Function(
                "changeMapping", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(map)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> addWarehouse(String token, Boolean allow) {
        final Function function = new Function(
                "addWarehouse", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(token), 
                new org.web3j.abi.datatypes.Bool(allow)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple2<BigInteger, BigInteger>> amountPartThree() {
        final Function function = new Function("amountPartThree", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple2<BigInteger, BigInteger>>(
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteCall<Tuple2<BigInteger, BigInteger>> amountPartTwo() {
        final Function function = new Function("amountPartTwo", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple2<BigInteger, BigInteger>>(
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteCall<Tuple2<BigInteger, BigInteger>> amountPartOne() {
        final Function function = new Function("amountPartOne", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple2<BigInteger, BigInteger>>(
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> setRatio(BigInteger _debitRatio, BigInteger _investorRatio) {
        final Function function = new Function(
                "setRatio", 
                Arrays.<Type>asList(new Uint256(_debitRatio),
                new Uint256(_investorRatio)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<IBMiningContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String map) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(map)));
        return deployRemoteCall(IBMiningContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<IBMiningContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String map) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(map)));
        return deployRemoteCall(IBMiningContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static IBMiningContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new IBMiningContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static IBMiningContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new IBMiningContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}