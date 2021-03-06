package com.lianer.core.SmartContract.TUSD;

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
public class IBTUSDContract extends Contract {
    private static final String BINARY = "608060405260006009553480156200001657600080fd5b5060405160e08062001718833981018060405260e08110156200003857600080fd5b508051602082015160408301516060840151608085015160a086015160c090960151949593949293919290919062000079326401000000006200043e810204565b156200008457600080fd5b60038054600160a060020a03191632179055600887905560008711620000a657fe5b60006002556005859055620000cd836201518064010000000062000d666200044682021704565b6006819055600010620000dc57fe5b600782905560008211620000ec57fe5b62000158612710620001436200012b8262000116878964010000000062000d666200044682021704565b9064010000000062000dbe6200047e82021704565b6005549064010000000062000d666200044682021704565b9064010000000062000d9a6200049182021704565b600d8190556000106200016757fe5b4260105560118054600160a060020a031916600160a060020a03831617905560008610156200019557600080fd5b60008411620001a357600080fd5b620001b786640100000000620004b7810204565b601154604080517f8fe77e86000000000000000000000000000000000000000000000000000000008152602060048201819052600f60248301527f6c6f616e53656c66466163746f7279000000000000000000000000000000000060448301529151600093600160a060020a031692638fe77e869260648082019391829003018186803b1580156200024857600080fd5b505afa1580156200025d573d6000803e3d6000fd5b505050506040513d60208110156200027457600080fd5b5051601354909150600114156200029057600560095562000366565b601354600214156200036657600081600160a060020a031663a0bb2b54896040518263ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004018082815260200191505060206040518083038186803b158015620002ff57600080fd5b505afa15801562000314573d6000803e3d6000fd5b505050506040513d60208110156200032b57600080fd5b50519050600160a060020a03811615156200034557600080fd5b60008054600160a060020a031916600160a060020a03929092169190911790555b600081600160a060020a031663a0bb2b54876040518263ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004018082815260200191505060206040518083038186803b158015620003c957600080fd5b505afa158015620003de573d6000803e3d6000fd5b505050506040513d6020811015620003f557600080fd5b50519050600160a060020a03811615156200040f57600080fd5b60018054600160a060020a031916600160a060020a039290921691909117905550620004dd9650505050505050565b6000903b1190565b6000821515620004595750600062000478565b8282028284828115156200046957fe5b04146200047557600080fd5b90505b92915050565b6000828201838110156200047557600080fd5b6000808211620004a057600080fd5b60008284811515620004ae57fe5b04949350505050565b801515620004ca576001601355620004da565b6000811115620004da5760026013555b50565b61122b80620004ed6000396000f3fe608060405260043610610110576000357c0100000000000000000000000000000000000000000000000000000000900480635cf2c0dd116100a7578063a4fe8a4411610076578063a4fe8a441461030d578063ab5f748214610322578063abb1dc4414610337578063c1845b371461037457610110565b80635cf2c0dd146102125780636de4928d146102275780637cc1f8671461023c5780639332aa4a146102f857610110565b806329e73df2116100e357806329e73df2146101905780632b68bb2d146101c15780634c5b8ece146101d6578063594837cf146101fd57610110565b8063162790551461011557806316e663f41461015c578063186699381461017357806321cca59b14610188575b600080fd5b34801561012157600080fd5b506101486004803603602081101561013857600080fd5b5035600160a060020a0316610389565b604080519115158252519081900360200190f35b34801561016857600080fd5b50610171610391565b005b34801561017f57600080fd5b506101716104a5565b61017161076c565b34801561019c57600080fd5b506101a56107ee565b60408051600160a060020a039092168252519081900360200190f35b3480156101cd57600080fd5b506101486107fe565b3480156101e257600080fd5b506101eb61093e565b60408051918252519081900360200190f35b34801561020957600080fd5b506101a5610944565b34801561021e57600080fd5b506101eb610953565b34801561023357600080fd5b506101eb6109ad565b34801561024857600080fd5b506102516109b3565b604051808f81526020018e600160a060020a0316600160a060020a031681526020018d600160a060020a0316600160a060020a031681526020018c81526020018b81526020018a815260200189815260200188815260200187600160a060020a0316600160a060020a031681526020018681526020018581526020018481526020018381526020018281526020019e50505050505050505050505050505060405180910390f35b34801561030457600080fd5b50610148610a4a565b34801561031957600080fd5b506101eb610b2a565b34801561032e57600080fd5b506101eb610c26565b34801561034357600080fd5b5061034c610c62565b60408051938452600160a060020a039283166020850152911682820152519081900360600190f35b34801561038057600080fd5b506101a5610c7d565b6000903b1190565b600454600160a060020a031633146103a857600080fd5b6103b0610953565b6004146103bc57600080fd5b601354600114156103e3576004546103de90600160a060020a03163031610c8c565b61049a565b6013546002141561049a57600480546000546040805160e060020a6370a0823102815230948101949094525161047993600160a060020a0393841693909216916370a08231916024808301926020929190829003018186803b15801561044857600080fd5b505afa15801561045c573d6000803e3d6000fd5b505050506040513d602081101561047257600080fd5b5051610cdf565b60003031111561049a57600f5461049a90600160a060020a03163031610c8c565b600560025542600c55565b6011546040805160e160020a6347f3bf43028152602060048201819052600f60248301527f6c6f616e53656c66466163746f72790000000000000000000000000000000000604483015291513393600160a060020a031692638fe77e869260648082019391829003018186803b15801561051e57600080fd5b505afa158015610532573d6000803e3d6000fd5b505050506040513d602081101561054857600080fd5b5051600160a060020a03161461055d57600080fd5b6013546001148015610570575060003031115b156105cd5760035460009061058d90600160a060020a03166107fb565b604051909150600160a060020a03821690303180156108fc02916000818181858888f193505050501580156105c6573d6000803e3d6000fd5b5050610761565b60135460021480156106575750600080546040805160e060020a6370a082310281523060048201529051600160a060020a03909216916370a0823191602480820192602092909190829003018186803b15801561062957600080fd5b505afa15801561063d573d6000803e3d6000fd5b505050506040513d602081101561065357600080fd5b5051115b15610761576000546003546040805160e060020a6370a082310281523060048201529051600160a060020a039384169363a9059cbb93169184916370a0823191602480820192602092909190829003018186803b1580156106b757600080fd5b505afa1580156106cb573d6000803e3d6000fd5b505050506040513d60208110156106e157600080fd5b5051604080517c010000000000000000000000000000000000000000000000000000000063ffffffff8616028152600160a060020a039093166004840152602483019190915251604480830192600092919082900301818387803b15801561074857600080fd5b505af115801561075c573d6000803e3d6000fd5b505050505b600360025542600c55565b600354600160a060020a0316331461078357600080fd5b60135460011461079257600080fd5b6002541561079f57600080fd5b60006107ca6103e86107be600954600854610d6690919063ffffffff16565b9063ffffffff610d9a16565b6008549091506107e0908263ffffffff610dbe16565b34146107eb57600080fd5b50565b600154600160a060020a03165b90565b600354600090600160a060020a0316331461081857600080fd5b6002541561082557600080fd5b60135460011415610856576000303111156108515760035461085190600160a060020a03163031610c8c565b610938565b6013546002141561093857600080546040805160e060020a6370a082310281523060048201529051600160a060020a03909216916370a0823191602480820192602092909190829003018186803b1580156108b057600080fd5b505afa1580156108c4573d6000803e3d6000fd5b505050506040513d60208110156108da57600080fd5b50511115610938576003546000546040805160e060020a6370a08231028152306004820152905161093893600160a060020a039081169316916370a08231916024808301926020929190829003018186803b15801561044857600080fd5b50600190565b600d5490565b600354600160a060020a031690565b6000600254600014801561096a575061096a610a4a565b15610977575060016107fb565b600254600214801561098a5750600e5442115b80156109995750610999610a4a565b156109a6575060046107fb565b5060025490565b60055490565b6000806000806000806000806000806000806000806109d0610953565b600354600454600554600654600754600854600160a060020a0395861695909416936109fa610dd0565b6000809054906101000a9004600160a060020a0316600b54600c54600d54600e546010549d509d509d509d509d509d509d509d509d509d509d509d509d509d50909192939495969798999a9b9c9d565b600060135460011415610a955760025460021415610a7e5760085430311015610a74576000610a77565b60015b90506107fb565b610a86610c26565b30311015610a74576000610a77565b601354600214156107fb576008546000546040805160e060020a6370a082310281523060048201529051600160a060020a03909216916370a0823191602480820192602092909190829003018186803b158015610af157600080fd5b505afa158015610b05573d6000803e3d6000fd5b505050506040513d6020811015610b1b57600080fd5b50511015610a74576000610a77565b6011546040805160e160020a6347f3bf43028152602060048201819052600f60248301527f6c6f616e53656c66466163746f72790000000000000000000000000000000000604483015291516000933393600160a060020a0390911692638fe77e869260648083019392829003018186803b158015610ba857600080fd5b505afa158015610bbc573d6000803e3d6000fd5b505050506040513d6020811015610bd257600080fd5b5051600160a060020a031614610be757600080fd5b6004805473ffffffffffffffffffffffffffffffffffffffff1916321790556002805560065442908101600e55600b55610c1f610f4d565b5060055490565b600080610c466103e86107be600954600854610d6690919063ffffffff16565b600854909150610c5c908263ffffffff610dbe16565b91505090565b601354600054600154600160a060020a039182169116909192565b600454600160a060020a031690565b6000610ca083600160a060020a03166107fb565b604051909150600160a060020a0382169083156108fc029084906000818181858888f19350505050158015610cd9573d6000803e3d6000fd5b50505050565b60008054604080517fa9059cbb000000000000000000000000000000000000000000000000000000008152600160a060020a038681166004830152602482018690529151919092169263a9059cbb926044808201939182900301818387803b158015610d4a57600080fd5b505af1158015610d5e573d6000803e3d6000fd5b505050505050565b6000821515610d7757506000610d94565b828202828482811515610d8657fe5b0414610d9157600080fd5b90505b92915050565b6000808211610da857600080fd5b60008284811515610db557fe5b04949350505050565b600082820183811015610d9157600080fd5b60135460009081906001148015610dee57506002610dec610953565b105b15610e1a5760085430311015610e1557600854610e1290303163ffffffff6111ea16565b90505b610f48565b6013546002148015610e3357506002610e31610953565b105b15610f48576008546000546040805160e060020a6370a082310281523060048201529051600160a060020a03909216916370a0823191602480820192602092909190829003018186803b158015610e8957600080fd5b505afa158015610e9d573d6000803e3d6000fd5b505050506040513d6020811015610eb357600080fd5b50511015610f48576000546040805160e060020a6370a082310281523060048201529051610f4592600160a060020a0316916370a08231916024808301926020929190829003018186803b158015610f0a57600080fd5b505afa158015610f1e573d6000803e3d6000fd5b505050506040513d6020811015610f3457600080fd5b50516008549063ffffffff6111ea16565b90505b905090565b601354600114156111e8576011546040805160e160020a6347f3bf43028152602060048201819052600660248301527f61626f6e7573000000000000000000000000000000000000000000000000000060448301529151600160a060020a0390931692638fe77e8692606480840193919291829003018186803b158015610fd357600080fd5b505afa158015610fe7573d6000803e3d6000fd5b505050506040513d6020811015610ffd57600080fd5b5051600f805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a03909216919091179055600954600854600091611046916103e8916107be9190610d66565b600854909150810130311461105a57600080fd5b6000811161106757600080fd5b600f5461107d90600160a060020a031682610c8c565b6011546040805160e160020a6347f3bf43028152602060048201819052601460248301527f6c6f616e53656c66426f72726f774d696e696e6700000000000000000000000060448301529151600160a060020a0390931692638fe77e8692606480840193919291829003018186803b1580156110f857600080fd5b505afa15801561110c573d6000803e3d6000fd5b505050506040513d602081101561112257600080fd5b50516012805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a03928316179081905560085460035460048054600154604080517f41872fb9000000000000000000000000000000000000000000000000000000008152938401959095529286166024830152851660448201529084166064820152905191909216916341872fb991608480830192600092919082900301818387803b1580156111ce57600080fd5b505af11580156111e2573d6000803e3d6000fd5b50505050505b565b6000828211156111f957600080fd5b5090039056fea165627a7a723058204fd53a7a1180ea79c76f517eb61c1e8fcb2fb5cf8a4f43d8f52d3e3ab0fcefae0029";

    protected IBTUSDContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected IBTUSDContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
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

    public RemoteCall<TransactionReceipt> sendRepayment() {
        final Function function = new Function(
                "sendRepayment", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> payEth(BigInteger weiValue) {
        final Function function = new Function(
                "payEth", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<String> checkLenderToken() {
        final Function function = new Function("checkLenderToken", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> cancelContract() {
        final Function function = new Function(
                "cancelContract", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> checkBorrowerPayable() {
        final Function function = new Function("checkBorrowerPayable", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> checkBorrower() {
        final Function function = new Function("checkBorrower", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> showContractState() {
        final Function function = new Function("showContractState", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> checkLenderAmount() {
        final Function function = new Function("checkLenderAmount", 
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

    public RemoteCall<TransactionReceipt> sendLendAsset() {
        final Function function = new Function(
                "sendLendAsset", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> checkAllEth() {
        final Function function = new Function("checkAllEth", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Tuple3<BigInteger, String, String>> getTokenInfo() {
        final Function function = new Function("getTokenInfo", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Address>() {}));
        return new RemoteCall<Tuple3<BigInteger, String, String>>(
                new Callable<Tuple3<BigInteger, String, String>>() {
                    @Override
                    public Tuple3<BigInteger, String, String> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<BigInteger, String, String>(
                                (BigInteger) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue());
                    }
                });
    }

    public RemoteCall<String> checkLender() {
        final Function function = new Function("checkLender", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public static RemoteCall<IBTUSDContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger borrowerAmount, BigInteger borrowerId, BigInteger lenderAmount, BigInteger lenderId, BigInteger limitdays, BigInteger interestRate, String map) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Uint256(borrowerAmount),
                new Uint256(borrowerId),
                new Uint256(lenderAmount),
                new Uint256(lenderId),
                new Uint256(limitdays),
                new Uint256(interestRate),
                new Address(map)));
        return deployRemoteCall(IBTUSDContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<IBTUSDContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger borrowerAmount, BigInteger borrowerId, BigInteger lenderAmount, BigInteger lenderId, BigInteger limitdays, BigInteger interestRate, String map) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Uint256(borrowerAmount),
                new Uint256(borrowerId),
                new Uint256(lenderAmount),
                new Uint256(lenderId),
                new Uint256(limitdays),
                new Uint256(interestRate),
                new Address(map)));
        return deployRemoteCall(IBTUSDContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static IBTUSDContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new IBTUSDContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static IBTUSDContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new IBTUSDContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
