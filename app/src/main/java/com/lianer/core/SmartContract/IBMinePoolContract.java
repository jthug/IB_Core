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
import org.web3j.tuples.generated.Tuple3;
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
public class IBMinePoolContract extends Contract {
    private static final String BINARY = "6080604052600a600155670de0b6b3a7640000600255610e10600355600060045560006005556000600655600060075534801561003b57600080fd5b506040516020806109e38339810180604052602081101561005b57600080fd5b505161006e640100000000610165810204565b60098054600160a060020a031916600160a060020a038381169190911791829055604080517f8fe77e860000000000000000000000000000000000000000000000000000000081526020600480830182905260248301527f6e65737400000000000000000000000000000000000000000000000000000000604483015291519390921692638fe77e86926064808201939291829003018186803b15801561011457600080fd5b505afa158015610128573d6000803e3d6000fd5b505050506040513d602081101561013e57600080fd5b505160008054600160a060020a031916600160a060020a0390921691909117905550610174565b42600481905560035401600555565b610860806101836000396000f3fe60806040526004361061007c577c010000000000000000000000000000000000000000000000000000000060003504631b49583b81146100815780633e769295146100bc57806340f4f7b9146100ef5780635d851a631461011d5780637974f8fe14610144578063a781e7f81461016e578063d8b65131146101a1575b600080fd5b34801561008d57600080fd5b506100ba600480360360408110156100a457600080fd5b5080359060200135600160a060020a03166101dc565b005b3480156100c857600080fd5b506100d1610349565b60408051938452602084019290925282820152519081900360600190f35b3480156100fb57600080fd5b50610104610357565b6040805192835260208301919091528051918290030190f35b34801561012957600080fd5b5061013261036d565b60408051918252519081900360200190f35b34801561015057600080fd5b506100ba6004803603602081101561016757600080fd5b5035610373565b34801561017a57600080fd5b506100ba6004803603602081101561019157600080fd5b5035600160a060020a031661041a565b3480156101ad57600080fd5b506100ba600480360360408110156101c457600080fd5b50600160a060020a03813516906020013515156105c6565b3360009081526008602052604090205460ff1615156001146101fd57600080fd5b600054604080517f70a0823100000000000000000000000000000000000000000000000000000000815230600482015290518492600160a060020a0316916370a08231916024808301926020929190829003018186803b15801561026057600080fd5b505afa158015610274573d6000803e3d6000fd5b505050506040513d602081101561028a57600080fd5b5051106103455760008054604080517fa9059cbb000000000000000000000000000000000000000000000000000000008152600160a060020a038581166004830152602482018790529151919092169263a9059cbb926044808201939182900301818387803b1580156102fc57600080fd5b505af1158015610310573d6000803e3d6000fd5b50505050600554421015156103345761032761068f565b6000600655610334610736565b600680548301905560078054830190555b5050565b600454600554600154909192565b600080600154610365610745565b915091509091565b60075490565b600954604080517fa3bf06f10000000000000000000000000000000000000000000000000000000081523360048201529051600160a060020a039092169163a3bf06f191602480820192602092909190829003018186803b1580156103d757600080fd5b505afa1580156103eb573d6000803e3d6000fd5b505050506040513d602081101561040157600080fd5b5051151560011461041157600080fd5b610e1002600355565b600954604080517fa3bf06f10000000000000000000000000000000000000000000000000000000081523360048201529051600160a060020a039092169163a3bf06f191602480820192602092909190829003018186803b15801561047e57600080fd5b505afa158015610492573d6000803e3d6000fd5b505050506040513d60208110156104a857600080fd5b505115156001146104b857600080fd5b6009805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a038381169190911791829055604080517f8fe77e860000000000000000000000000000000000000000000000000000000081526020600480830182905260248301527f6e65737400000000000000000000000000000000000000000000000000000000604483015291519390921692638fe77e86926064808201939291829003018186803b15801561056b57600080fd5b505afa15801561057f573d6000803e3d6000fd5b505050506040513d602081101561059557600080fd5b50516000805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0390921691909117905550565b600954604080517fa3bf06f10000000000000000000000000000000000000000000000000000000081523360048201529051600160a060020a039092169163a3bf06f191602480820192602092909190829003018186803b15801561062a57600080fd5b505afa15801561063e573d6000803e3d6000fd5b505050506040513d602081101561065457600080fd5b5051151560011461066457600080fd5b600160a060020a03919091166000908152600860205260409020805460ff1916911515919091179055565b600654692a5a058fc295ed000000106106ac57600a600155610734565b692a5a058fc295ed0000006006541180156106d35750697f0e10af47c1c700000060065411155b156106e2576008600155610734565b697f0e10af47c1c700000060065411801561070a57506a01a784379d99db4200000060065411155b15610719576005600155610734565b6a01a784379d99db4200000060065411156107345760036001555b565b42600481905560035401600555565b6000806107696b033b2e3c9fd0803ce80000006007546107c090919063ffffffff16565b9050670de0b6b3a7640000600982111561078257600991505b60005b828110156107b9576107af600a6107a384600863ffffffff61080b16565b9063ffffffff6107c016565b9150600101610785565b5091505090565b60008082116107cb57fe5b600082848115156107d857fe5b04905082848115156107e657fe5b0681840201841415156107f557fe5b828481151561080057fe5b049150505b92915050565b600082151561081c57506000610805565b5081810281838281151561082c57fe5b041461080557fefea165627a7a7230582003b9e03cb425f6b248b41508ed76023d96311a81a12eebf434f8fb71e0863d1b0029";

    protected IBMinePoolContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected IBMinePoolContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> addMiningInfo(BigInteger amount, String _to) {
        final Function function = new Function(
                "addMiningInfo", 
                Arrays.<Type>asList(new Uint256(amount),
                new org.web3j.abi.datatypes.Address(_to)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple3<BigInteger, BigInteger, BigInteger>> amountPartTime() {
        final Function function = new Function("amountPartTime", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple3<BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple3<BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple3<BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<Tuple2<BigInteger, BigInteger>> getParameter() {
        final Function function = new Function("getParameter", 
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

    public RemoteCall<BigInteger> getNestAmount() {
        final Function function = new Function("getNestAmount", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> changeTime(BigInteger longTime) {
        final Function function = new Function(
                "changeTime", 
                Arrays.<Type>asList(new Uint256(longTime)),
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

    public RemoteCall<TransactionReceipt> addExcavatingMachinery(String machinery, Boolean allow) {
        final Function function = new Function(
                "addExcavatingMachinery", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(machinery), 
                new org.web3j.abi.datatypes.Bool(allow)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<IBMinePoolContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String map) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(map)));
        return deployRemoteCall(IBMinePoolContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<IBMinePoolContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String map) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(map)));
        return deployRemoteCall(IBMinePoolContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static IBMinePoolContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new IBMinePoolContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static IBMinePoolContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new IBMinePoolContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}