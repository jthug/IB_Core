package com.lianer.core.SmartContract.TUSD;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
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
public class IBTUSDDataContract extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b5060405160208061036b8339810180604052602081101561003057600080fd5b505160018054600160a060020a031916600160a060020a0390921691909117905561030b806100606000396000f3fe608060405234801561001057600080fd5b506004361061005d577c01000000000000000000000000000000000000000000000000000000006000350463a781e7f88114610062578063b11ce2db1461008a578063d7b12454146100b0575b600080fd5b6100886004803603602081101561007857600080fd5b5035600160a060020a03166100ea565b005b610088600480360360208110156100a057600080fd5b5035600160a060020a03166101b7565b6100d6600480360360208110156100c657600080fd5b5035600160a060020a03166102a9565b604080519115158252519081900360200190f35b600154604080517fa3bf06f10000000000000000000000000000000000000000000000000000000081523360048201529051600160a060020a039092169163a3bf06f191602480820192602092909190829003018186803b15801561014e57600080fd5b505afa158015610162573d6000803e3d6000fd5b505050506040513d602081101561017857600080fd5b5051151560011461018857600080fd5b6001805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b600154604080517f8fe77e86000000000000000000000000000000000000000000000000000000008152602060048201819052600f60248301527f6c6f616e53656c66466163746f72790000000000000000000000000000000000604483015291513393600160a060020a031692638fe77e869260648082019391829003018186803b15801561024657600080fd5b505afa15801561025a573d6000803e3d6000fd5b505050506040513d602081101561027057600080fd5b5051600160a060020a03161461028557600080fd5b600160a060020a03166000908152602081905260409020805460ff19166001179055565b6000600160a060020a03821615156102c057600080fd5b50600160a060020a031660009081526020819052604090205460ff169056fea165627a7a723058206e8c5b5ee0db37891827d838447ec0cff6d2c57a9deccd32a3c95a9624009e5c0029";

    protected IBTUSDDataContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected IBTUSDDataContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> changeMapping(String map) {
        final Function function = new Function(
                "changeMapping", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(map)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> addContractAddress(String contractAddress) {
        final Function function = new Function(
                "addContractAddress", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(contractAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Boolean> checkContract(String contractAddress) {
        final Function function = new Function("checkContract", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(contractAddress)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public static RemoteCall<IBTUSDDataContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String map) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(map)));
        return deployRemoteCall(IBTUSDDataContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<IBTUSDDataContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String map) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(map)));
        return deployRemoteCall(IBTUSDDataContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static IBTUSDDataContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new IBTUSDDataContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static IBTUSDDataContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new IBTUSDDataContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
