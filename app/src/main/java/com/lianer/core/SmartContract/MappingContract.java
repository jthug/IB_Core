package com.lianer.core.SmartContract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
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
public class MappingContract extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50336000908152600160208190526040909120805460ff1916909117905561047e8061003d6000396000f3fe608060405260043610610066577c0100000000000000000000000000000000000000000000000000000000600035046353b1e097811461006b5780638fe77e861461012b578063a3bf06f1146101fa578063b6518bdb14610241578063c7d5505714610274575b600080fd5b34801561007757600080fd5b506101296004803603604081101561008e57600080fd5b8101906020810181356401000000008111156100a957600080fd5b8201836020820111156100bb57600080fd5b803590602001918460018302840111640100000000831117156100dd57600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092955050509035600160a060020a031691506102a79050565b005b34801561013757600080fd5b506101de6004803603602081101561014e57600080fd5b81019060208101813564010000000081111561016957600080fd5b82018360208201111561017b57600080fd5b8035906020019184600183028401116401000000008311171561019d57600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092955061034e945050505050565b60408051600160a060020a039092168252519081900360200190f35b34801561020657600080fd5b5061022d6004803603602081101561021d57600080fd5b5035600160a060020a03166103be565b604080519115158252519081900360200190f35b34801561024d57600080fd5b506101296004803603602081101561026457600080fd5b5035600160a060020a03166103dc565b34801561028057600080fd5b506101296004803603602081101561029757600080fd5b5035600160a060020a031661041a565b6102b0336103be565b15156001146102be57600080fd5b806000836040518082805190602001908083835b602083106102f15780518252601f1990920191602091820191016102d2565b51815160209384036101000a60001901801990921691161790529201948552506040519384900301909220805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a03949094169390931790925550505050565b600080826040518082805190602001908083835b602083106103815780518252601f199092019160209182019101610362565b51815160209384036101000a6000190180199092169116179052920194855250604051938490030190922054600160a060020a0316949350505050565b600160a060020a031660009081526001602052604090205460ff1690565b6103e5336103be565b15156001146103f357600080fd5b600160a060020a03166000908152600160208190526040909120805460ff19169091179055565b610423336103be565b151560011461043157600080fd5b600160a060020a03166000908152600160205260409020805460ff1916905556fea165627a7a723058205fdd03ce3401b8f9efe784f68f1278ca942764d8d55bbdd8ef2699d0d21aa9190029";

    protected MappingContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected MappingContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> addContractAddress(String name, String contractAddress) {
        final Function function = new Function(
                "addContractAddress", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(name), 
                new Address(contractAddress)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> checkAddress(String name) {
        final Function function = new Function("checkAddress", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(name)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<Boolean> checkOwners(String man) {
        final Function function = new Function("checkOwners", 
                Arrays.<Type>asList(new Address(man)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> addSuperMan(String superMan) {
        final Function function = new Function(
                "addSuperMan", 
                Arrays.<Type>asList(new Address(superMan)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> deleteSuperMan(String superMan) {
        final Function function = new Function(
                "deleteSuperMan", 
                Arrays.<Type>asList(new Address(superMan)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<MappingContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(MappingContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<MappingContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(MappingContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static MappingContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new MappingContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static MappingContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new MappingContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
