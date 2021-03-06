package com.lianer.core.SmartContract.ETH;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple14;
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
public class IBContract extends Contract {
    private static final String BINARY = "60806040523480156200001157600080fd5b5060405160c08062000f69833981018060405260c08110156200003357600080fd5b508051602082015160408301516060840151608085015160a09095015193949293919290916200006c32640100000000620001b6810204565b156200007757600080fd5b60028054600160a060020a031916321790556007869055600086116200009957fe5b60006001819055600486905560098054600160a060020a038716600160a060020a03199182168117909255825416179055620000e7836201518064010000000062000c61620001be82021704565b6005819055600010620000f657fe5b6006829055600082116200010657fe5b620001726127106200015d620001458262000130878964010000000062000c61620001be82021704565b9064010000000062000cd3620001f182021704565b6004549064010000000062000c61620001be82021704565b9064010000000062000c8a620001ff82021704565b600c8190556000106200018157fe5b600160085542600f5560118054600160a060020a031916600160a060020a0392909216919091179055506200024d9350505050565b6000903b1190565b6000821515620001d157506000620001eb565b50818102818382811515620001e257fe5b0414620001eb57fe5b92915050565b81810182811015620001eb57fe5b60008082116200020b57fe5b600082848115156200021957fe5b04905082848115156200022857fe5b0681840201841415156200023857fe5b82848115156200024457fe5b04949350505050565b610d0c806200025d6000396000f3fe608060405260043610610092577c0100000000000000000000000000000000000000000000000000000000600035046316279055811461009757806316e663f4146100de57806318669938146100f55780632b68bb2d146100fd5780635cf2c0dd146101125780637cc1f867146101395780639332aa4a146101f5578063a4fe8a441461020a578063c241267614610212575b600080fd5b3480156100a357600080fd5b506100ca600480360360208110156100ba57600080fd5b5035600160a060020a0316610243565b604080519115158252519081900360200190f35b3480156100ea57600080fd5b506100f361024b565b005b6100f361031f565b34801561010957600080fd5b506100ca610378565b34801561011e57600080fd5b5061012761047d565b60408051918252519081900360200190f35b34801561014557600080fd5b5061014e6104d7565b604051808f81526020018e600160a060020a0316600160a060020a031681526020018d600160a060020a0316600160a060020a031681526020018c81526020018b81526020018a815260200189815260200188815260200187600160a060020a0316600160a060020a031681526020018681526020018581526020018481526020018381526020018281526020019e50505050505050505050505050505060405180910390f35b34801561020157600080fd5b506100ca6106cc565b610127610759565b34801561021e57600080fd5b50610227610a3a565b60408051600160a060020a039092168252519081900360200190f35b6000903b1190565b600354600160a060020a0316331461026257600080fd5b61026a61047d565b60041461027657600080fd5b6003546000546040805160e060020a6370a0823102815230600482015290516102fd93600160a060020a039081169316916370a08231916024808301926020929190829003018186803b1580156102cc57600080fd5b505afa1580156102e0573d6000803e3d6000fd5b505050506040513d60208110156102f657600080fd5b5051610a49565b600e5461031490600160a060020a03163031610ad0565b600560015542600b55565b600254600160a060020a0316331461033657600080fd5b60015460021461034557600080fd5b600d5442111561035457600080fd5b600c543490811461036457600080fd5b61036c610b23565b50600360015542600b55565b600254600090600160a060020a0316331461039257600080fd5b6001541561039f57600080fd5b600080546040805160e060020a6370a082310281523060048201529051600160a060020a03909216916370a0823191602480820192602092909190829003018186803b1580156103ee57600080fd5b505afa158015610402573d6000803e3d6000fd5b505050506040513d602081101561041857600080fd5b50511115610476576002546000546040805160e060020a6370a08231028152306004820152905161047693600160a060020a039081169316916370a08231916024808301926020929190829003018186803b1580156102cc57600080fd5b5060015b90565b6000600154600014801561049457506104946106cc565b156104a15750600161047a565b60015460021480156104b45750600d5442115b80156104c357506104c36106cc565b156104d05750600461047a565b5060015490565b60008060008060008060008060008060008060008060008090506007546000809054906101000a9004600160a060020a0316600160a060020a03166370a08231306040518263ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004018082600160a060020a0316600160a060020a0316815260200191505060206040518083038186803b15801561057a57600080fd5b505afa15801561058e573d6000803e3d6000fd5b505050506040513d60208110156105a457600080fd5b50511015610639576000546040805160e060020a6370a08231028152306004820152905161063692600160a060020a0316916370a08231916024808301926020929190829003018186803b1580156105fb57600080fd5b505afa15801561060f573d6000803e3d6000fd5b505050506040513d602081101561062557600080fd5b50516007549063ffffffff610bb016565b90505b61064161047d565b600260009054906101000a9004600160a060020a0316600360009054906101000a9004600160a060020a031660045460055460065460075487600960009054906101000a9004600160a060020a0316600a54600b54600c54600d54600f549e509e509e509e509e509e509e509e509e509e509e509e509e509e5050909192939495969798999a9b9c9d565b600754600080546040805160e060020a6370a082310281523060048201529051929392600160a060020a03909216916370a0823191602480820192602092909190829003018186803b15801561072157600080fd5b505afa158015610735573d6000803e3d6000fd5b505050506040513d602081101561074b57600080fd5b505110156104765750600090565b600061076433610243565b1561076e57600080fd5b6001541561077b57600080fd5b6107836106cc565b151561078e57600080fd5b6004543490811461079e57600080fd5b6003805473ffffffffffffffffffffffffffffffffffffffff191633179055601154604080517f8fe77e86000000000000000000000000000000000000000000000000000000008152602060048201819052600660248301527f61626f6e7573000000000000000000000000000000000000000000000000000060448301529151600160a060020a039390931692638fe77e8692606480840193919291829003018186803b15801561084f57600080fd5b505afa158015610863573d6000803e3d6000fd5b505050506040513d602081101561087957600080fd5b5051600e805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a03928316179055601154604080517f8fe77e86000000000000000000000000000000000000000000000000000000008152602060048201819052600c60248301527f626f72726f774d696e696e670000000000000000000000000000000000000000604483015291519290931692638fe77e869260648083019392829003018186803b15801561092c57600080fd5b505afa158015610940573d6000803e3d6000fd5b505050506040513d602081101561095657600080fd5b50516010805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a0390921691909117905561098c610bc7565b50600260018190556010549054600354600954604080517f41872fb900000000000000000000000000000000000000000000000000000000815260048101879052600160a060020a039485166024820152928416604484015290831660648301525191909216916341872fb991608480830192600092919082900301818387803b158015610a1957600080fd5b505af1158015610a2d573d6000803e3d6000fd5b5050505060045491505090565b600054600160a060020a031681565b60008054604080517fa9059cbb000000000000000000000000000000000000000000000000000000008152600160a060020a038681166004830152602482018690529151919092169263a9059cbb926044808201939182900301818387803b158015610ab457600080fd5b505af1158015610ac8573d6000803e3d6000fd5b505050505050565b6000610ae483600160a060020a031661047a565b604051909150600160a060020a0382169083156108fc029084906000818181858888f19350505050158015610b1d573d6000803e3d6000fd5b50505050565b6000303111610b3157600080fd5b600c5430311015610b4157600080fd5b600354610b5890600160a060020a03163031610ad0565b6002546000546040805160e060020a6370a082310281523060048201529051610bae93600160a060020a039081169316916370a08231916024808301926020929190829003018186803b1580156102cc57600080fd5b565b600082821115610bbc57fe5b508082035b92915050565b60055442908101600d55600a556008546000908190610c0190606490610bf59030319063ffffffff610c6116565b9063ffffffff610c8a16565b905060008111610c0d57fe5b6000610c2030318363ffffffff610bb016565b905060008111610c2c57fe5b600e54610c4290600160a060020a031683610ad0565b600254610c5890600160a060020a031682610ad0565b60019250505090565b6000821515610c7257506000610bc1565b50818102818382811515610c8257fe5b0414610bc157fe5b6000808211610c9557fe5b60008284811515610ca257fe5b0490508284811515610cb057fe5b068184020184141515610cbf57fe5b8284811515610cca57fe5b04949350505050565b81810182811015610bc157fefea165627a7a72305820ab23d46a85a948719e2ceffbe135eca39c3a6f564d6ef470f5ff7e026ed42c130029";

    protected IBContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected IBContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<Boolean> isContract(String addr) {
        final Function function = new Function("isContract", 
                Arrays.<Type>asList(new Address(addr)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> applyForAssets() {
        final Function function = new Function(
                "applyForAssets", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> sendRepayment(BigInteger weiValue) {
        final Function function = new Function(
                "sendRepayment", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> cancelContract() {
        final Function function = new Function(
                "cancelContract", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> showContractState() {
        final Function function = new Function("showContractState", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Tuple14<BigInteger, String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>> getContractInfo() {
        final Function function = new Function("getContractInfo", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple14<BigInteger, String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple14<BigInteger, String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple14<BigInteger, String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple14<BigInteger, String, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (BigInteger) results.get(6).getValue(), 
                                (BigInteger) results.get(7).getValue(), 
                                (String) results.get(8).getValue(), 
                                (BigInteger) results.get(9).getValue(), 
                                (BigInteger) results.get(10).getValue(), 
                                (BigInteger) results.get(11).getValue(), 
                                (BigInteger) results.get(12).getValue(), 
                                (BigInteger) results.get(13).getValue());
                    }
                });
    }

    public RemoteCall<Boolean> isBorrowerAssetEnough() {
        final Function function = new Function("isBorrowerAssetEnough", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> sendLendAsset(BigInteger weiValue) {
        final Function function = new Function(
                "sendLendAsset", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<String> Token() {
        final Function function = new Function("Token", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<IBContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger borrowerAmount, BigInteger lenderAmount, String tokenAddr, BigInteger limitdays, BigInteger interestRate, String map) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Uint256(borrowerAmount),
                new Uint256(lenderAmount),
                new Address(tokenAddr),
                new Uint256(limitdays),
                new Uint256(interestRate),
                new Address(map)));
        return deployRemoteCall(IBContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<IBContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger borrowerAmount, BigInteger lenderAmount, String tokenAddr, BigInteger limitdays, BigInteger interestRate, String map) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Uint256(borrowerAmount),
                new Uint256(lenderAmount),
                new Address(tokenAddr),
                new Uint256(limitdays),
                new Uint256(interestRate),
                new Address(map)));
        return deployRemoteCall(IBContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static IBContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new IBContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static IBContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new IBContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
