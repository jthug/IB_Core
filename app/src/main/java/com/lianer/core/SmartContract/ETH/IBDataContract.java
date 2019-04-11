package com.lianer.core.SmartContract.ETH;

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
public class IBDataContract extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b506040516020806103858339810180604052602081101561003057600080fd5b505160018054600160a060020a031916600160a060020a03909216919091179055610325806100606000396000f3fe608060405260043610610050577c01000000000000000000000000000000000000000000000000000000006000350463a781e7f88114610055578063b11ce2db1461008a578063d7b12454146100bd575b600080fd5b34801561006157600080fd5b506100886004803603602081101561007857600080fd5b5035600160a060020a0316610104565b005b34801561009657600080fd5b50610088600480360360208110156100ad57600080fd5b5035600160a060020a03166101d1565b3480156100c957600080fd5b506100f0600480360360208110156100e057600080fd5b5035600160a060020a03166102c3565b604080519115158252519081900360200190f35b600154604080517fa3bf06f10000000000000000000000000000000000000000000000000000000081523360048201529051600160a060020a039092169163a3bf06f191602480820192602092909190829003018186803b15801561016857600080fd5b505afa15801561017c573d6000803e3d6000fd5b505050506040513d602081101561019257600080fd5b505115156001146101a257600080fd5b6001805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0392909216919091179055565b600154604080517f8fe77e86000000000000000000000000000000000000000000000000000000008152602060048201819052600b60248301527f6c6f616e466163746f7279000000000000000000000000000000000000000000604483015291513393600160a060020a031692638fe77e869260648082019391829003018186803b15801561026057600080fd5b505afa158015610274573d6000803e3d6000fd5b505050506040513d602081101561028a57600080fd5b5051600160a060020a03161461029f57600080fd5b600160a060020a03166000908152602081905260409020805460ff19166001179055565b6000600160a060020a03821615156102da57600080fd5b50600160a060020a031660009081526020819052604090205460ff169056fea165627a7a72305820ccd1bfd9418d8919251eb4900ecd829e10f86e641c4c216eb61072158c1879a10029";

    protected IBDataContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected IBDataContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
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

    public static RemoteCall<IBDataContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String map) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(map)));
        return deployRemoteCall(IBDataContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<IBDataContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String map) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(map)));
        return deployRemoteCall(IBDataContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static IBDataContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new IBDataContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static IBDataContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new IBDataContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
