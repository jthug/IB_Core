package com.lianer.core.SmartContract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tuples.generated.Tuple9;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.1.1.
 */
public class IBAbonusContract extends Contract {
    private static final String BINARY = "608060405262093a8060025560006003556201518060045560006005556000600655600160075534801561003257600080fd5b5060405160208061152d8339810180604052602081101561005257600080fd5b5051600180546001600160a01b0319166001600160a01b038084169190911791829055604080517f8fe77e860000000000000000000000000000000000000000000000000000000081526020600480830182905260248301527f6e65737400000000000000000000000000000000000000000000000000000000604483015291519390921692638fe77e86926064808201939291829003018186803b1580156100fa57600080fd5b505afa15801561010e573d6000803e3d6000fd5b505050506040513d602081101561012457600080fd5b5051600080546001600160a01b039092166001600160a01b0319909216919091179055506113d6806101576000396000f3fe6080604052600436106100a75760003560e01c80637711e857116100645780637711e857146102085780638f88708b1461022f578063a456d81214610259578063a781e7f81461026e578063cb05b93e146102a1578063e945a4d2146102b6576100a7565b806316279055146100a957806337becf21146100f05780635a9b0b891461011a5780635f62054f14610181578063659cf235146101965780637014e74d146101c0575b005b3480156100b557600080fd5b506100dc600480360360208110156100cc57600080fd5b50356001600160a01b03166102e0565b604080519115158252519081900360200190f35b3480156100fc57600080fd5b506100a76004803603602081101561011357600080fd5b50356102e6565b34801561012657600080fd5b5061012f6105a9565b604080519a8b5260208b0199909952898901979097526060890195909552608088019390935260a087019190915260c086015260e0850152610100840152151561012083015251908190036101400190f35b34801561018d57600080fd5b506100dc61091e565b3480156101a257600080fd5b506100a7600480360360208110156101b957600080fd5b5035610a34565b3480156101cc57600080fd5b506101d5610ae2565b604080519687526020870195909552858501939093526060850191909152608084015260a0830152519081900360c00190f35b34801561021457600080fd5b5061021d610b99565b60408051918252519081900360200190f35b34801561023b57600080fd5b506100a76004803603602081101561025257600080fd5b5035610c00565b34801561026557600080fd5b5061021d610db6565b34801561027a57600080fd5b506100a76004803603602081101561029157600080fd5b50356001600160a01b0316610e6d565b3480156102ad57600080fd5b506100a7610f35565b3480156102c257600080fd5b506100a7600480360360208110156102d957600080fd5b50356111ae565b3b151590565b6102ef336102e0565b156102f957600080fd5b60005460408051600160e01b6370a08231028152336004820152905183926001600160a01b0316916370a08231916024808301926020929190829003018186803b15801561034657600080fd5b505afa15801561035a573d6000803e3d6000fd5b505050506040513d602081101561037057600080fd5b5051101561037d57600080fd5b60005460408051600160e11b636eb1769f028152336004820152306024820152905183926001600160a01b03169163dd62ed3e916044808301926020929190829003018186803b1580156103d057600080fd5b505afa1580156103e4573d6000803e3d6000fd5b505050506040513d60208110156103fa57600080fd5b5051101561040757600080fd5b61040f61125b565b6000805460408051600160e01b6323b872dd0281523360048201523060248201526044810185905290516001600160a01b03909216926323b872dd926064808401936020939083900390910190829087803b15801561046d57600080fd5b505af1158015610481573d6000803e3d6000fd5b505050506040513d602081101561049757600080fd5b50516104a257600080fd5b600960006104bc600160075461132090919063ffffffff16565b81526020808201929092526040908101600090812033825290925290205415801561050157506007546000908152600960209081526040808320338452909152902054155b80156105105750600754600114155b1561055d5733600090815260086020526040812054600754909160099161053e90600163ffffffff61132016565b8152602080820192909252604090810160009081203382529092529020555b3360009081526008602052604090205461057d908263ffffffff61133716565b336000818152600860209081526040808320859055600754835260098252808320938352929052205550565b60008060008060008060008060008060004290506105d460025460035461132090919063ffffffff16565b811015801561060b57506106076004546105fb60025460035461132090919063ffffffff16565b9063ffffffff61133716565b8111155b1561073a5761062d6004546105fb60025460035461132090919063ffffffff16565b995060055498506006549750600080905060006009600061065a600160075461132090919063ffffffff16565b81526020808201929092526040908101600090812033825290925290205411156106bd5760096000610698600160075461132090919063ffffffff16565b8152602080820192909252604090810160009081203382529092529020549050610733565b600960006106d7600160075461132090919063ffffffff16565b81526020808201929092526040908101600090812033825290925290205415801561071c57506007546000908152600960209081526040808320338452909152902054155b156107335750336000908152600860205260409020545b95506107ca565b600099503031985061074a610db6565b60005460408051600160e01b6370a082310281523360048201529051929a506001600160a01b03909116916370a0823191602480820192602092909190829003018186803b15801561079b57600080fd5b505afa1580156107af573d6000803e3d6000fd5b505050506040513d60208110156107c557600080fd5b505195505b6107ea866107de818c63ffffffff61134416565b9063ffffffff61136916565b94506107f4610b99565b3360008181526008602090815260408083205492548151600160e11b636eb1769f02815260048101959095523060248601529051949f50919a506001600160a01b039091169263dd62ed3e926044808201939291829003018186803b15801561085c57600080fd5b505afa158015610870573d6000803e3d6000fd5b505050506040513d602081101561088657600080fd5b505160005460408051600160e01b6370a0823102815233600482015290519296506001600160a01b03909116916370a0823191602480820192602092909190829003018186803b1580156108d957600080fd5b505afa1580156108ed573d6000803e3d6000fd5b505050506040513d602081101561090357600080fd5b5051925061090f61091e565b91505090919293949596979899565b600354600090429081106109cf5760006109496002546107de6003548561132090919063ffffffff16565b9050600061098561097660025461096a60018661132090919063ffffffff16565b9063ffffffff61134416565b6003549063ffffffff61133716565b9050600061099e6004548361133790919063ffffffff16565b90508184101580156109b05750808411155b156109c2576001945050505050610a31565b6000945050505050610a31565b600354811080156109f457506002546003546109f09163ffffffff61132016565b8110155b8015610a1c5750610a186004546105fb60025460035461132090919063ffffffff16565b8111155b15610a2b576001915050610a31565b60009150505b90565b60015460408051600160e01b63a3bf06f102815233600482015290516001600160a01b039092169163a3bf06f191602480820192602092909190829003018186803b158015610a8257600080fd5b505afa158015610a96573d6000803e3d6000fd5b505050506040513d6020811015610aac57600080fd5b50511515600114610abc57600080fd5b60008111610ac957600080fd5b610adc816201518063ffffffff61134416565b60025550565b60015460408051600160e01b63a3bf06f10281523360048201529051600092839283928392839283926001600160a01b039092169163a3bf06f191602480820192602092909190829003018186803b158015610b3d57600080fd5b505afa158015610b51573d6000803e3d6000fd5b505050506040513d6020811015610b6757600080fd5b50511515600114610b7757600080fd5b5050600254600354600454600554600654600754949993985091965094509250565b6000804290508060035410610bb2575050600354610a31565b6000610bcf6002546107de6003548561132090919063ffffffff16565b9050610bf7610976610be883600163ffffffff61133716565b6002549063ffffffff61134416565b92505050610a31565b610c09336102e0565b15610c1357600080fd5b80610c1d57600080fd5b33600090815260086020526040902054811115610c3957600080fd5b610c4161125b565b60096000610c5b600160075461132090919063ffffffff16565b815260208082019290925260409081016000908120338252909252902054158015610ca057506007546000908152600960209081526040808320338452909152902054155b8015610caf5750600754600114155b15610cfc57336000908152600860205260408120546007549091600991610cdd90600163ffffffff61132016565b8152602080820192909252604090810160009081203382529092529020555b33600090815260086020526040902054610d1c908263ffffffff61132016565b3360008181526008602090815260408083208590556007548352600982528083208484529091528082209390935580548351600160e01b63a9059cbb02815260048101939093526024830185905292516001600160a01b039093169263a9059cbb92604480820193929182900301818387803b158015610d9b57600080fd5b505af1158015610daf573d6000803e3d6000fd5b5050505050565b6000805460408051600160e01b6370a0823102815273c4f6d5d82422b95b25952b141381aecd3c0280b0600482015290516b204fce5e3e25026110000000928492610e66926001600160a01b03909216916370a0823191602480820192602092909190829003018186803b158015610e2d57600080fd5b505afa158015610e41573d6000803e3d6000fd5b505050506040513d6020811015610e5757600080fd5b5051839063ffffffff61132016565b9250505090565b600180546001600160a01b0319166001600160a01b03838116919091179182905560408051600160e11b6347f3bf43028152602060048083018290526024830152600160e21b631b995cdd02604483015291519390921692638fe77e86926064808201939291829003018186803b158015610ee757600080fd5b505afa158015610efb573d6000803e3d6000fd5b505050506040513d6020811015610f1157600080fd5b5051600080546001600160a01b0319166001600160a01b0390921691909117905550565b610f3e336102e0565b15610f4857600080fd5b610f5061125b565b6002546003544291610f68919063ffffffff61132016565b8110158015610f935750610f8f6004546105fb60025460035461132090919063ffffffff16565b8111155b610f9c57600080fd5b60075460011415610fac57600080fd5b600a6000610fc6600160075461132090919063ffffffff16565b81526020808201929092526040908101600090812033825290925290205460ff16151560011415610ff657600080fd5b6007546000908190600990829061101490600163ffffffff61132016565b81526020808201929092526040908101600090812033825290925290205411156110775760096000611052600160075461132090919063ffffffff16565b81526020808201929092526040908101600090812033825290925290205490506110ed565b60096000611091600160075461132090919063ffffffff16565b8152602080820192909252604090810160009081203382529092529020541580156110d657506007546000908152600960209081526040808320338452909152902054155b156110ed5750336000908152600860205260409020545b600081116110fa57600080fd5b60006006541161110957600080fd5b60006111266006546107de6005548561134490919063ffffffff16565b90506000811161113557600080fd5b6001600a6000611151600160075461132090919063ffffffff16565b815260208082019290925260409081016000908120338083529352818120805460ff1916941515949094179093555190916108fc841502918491818181858888f193505050501580156111a8573d6000803e3d6000fd5b50505050565b60015460408051600160e01b63a3bf06f102815233600482015290516001600160a01b039092169163a3bf06f191602480820192602092909190829003018186803b1580156111fc57600080fd5b505afa158015611210573d6000803e3d6000fd5b505050506040513d602081101561122657600080fd5b5051151560011461123657600080fd5b6000811161124357600080fd5b61125581610e1063ffffffff61134416565b60045550565b611264336102e0565b1561126e57600080fd5b6003544290811061131d5760006112966002546107de6003548561132090919063ffffffff16565b905060006112b761097660025461096a60018661132090919063ffffffff16565b905060006112d06004548361133790919063ffffffff16565b90508184101580156112e25750808411155b156111a8576112ef610b99565b60035560075461130690600163ffffffff61133716565b6007553031600555611316610db6565b6006555050505b50565b60008282111561132c57fe5b508082035b92915050565b8181018281101561133157fe5b60008261135357506000611331565b508181028183828161136157fe5b041461133157fe5b600080821161137457fe5b600082848161137f57fe5b04905082848161138b57fe5b0681840201841461139857fe5b8284816113a157fe5b0494935050505056fea165627a7a723058205a4b3f3ebc2662ba03aba29484a1e3ee2e78cc3c400b0e3f34ee2f1ddf6710130029";

    public static final String FUNC_ISCONTRACT = "isContract";

    public static final String FUNC_TOCHANGEINTO = "toChangeInto";

    public static final String FUNC_GETINFO = "getInfo";

    public static final String FUNC_IFGETETH = "ifGetEth";

    public static final String FUNC_CHANGETIMELIMIT = "changeTimeLimit";

    public static final String FUNC_CHECKVARIABLE = "checkVariable";

    public static final String FUNC_GETNEXTTIME = "getNextTime";

    public static final String FUNC_RETRIEVE = "retrieve";

    public static final String FUNC_ALLVALUE = "allValue";

    public static final String FUNC_CHANGEMAPPING = "changeMapping";

    public static final String FUNC_GETETH = "getEth";

    public static final String FUNC_CHANGEGETABONUSTIMELIMIT = "changeGetAbonusTimeLimit";

    protected IBAbonusContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected IBAbonusContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<Boolean> isContract(String addr) {
        final Function function = new Function(FUNC_ISCONTRACT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(addr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> toChangeInto(BigInteger amount) {
        final Function function = new Function(
                FUNC_TOCHANGEINTO, 
                Arrays.<Type>asList(new Uint256(amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple9<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>> getInfo() {
        final Function function = new Function(FUNC_GETINFO, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple9<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple9<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple9<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple9<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (BigInteger) results.get(6).getValue(), 
                                (BigInteger) results.get(7).getValue(), 
                                (BigInteger) results.get(8).getValue());
                    }
                });
    }

    public RemoteCall<Boolean> ifGetEth() {
        final Function function = new Function(FUNC_IFGETETH, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> changeTimeLimit(BigInteger num) {
        final Function function = new Function(
                FUNC_CHANGETIMELIMIT, 
                Arrays.<Type>asList(new Uint256(num)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple6<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>> checkVariable() {
        final Function function = new Function(FUNC_CHECKVARIABLE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple6<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple6<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple6<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple6<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue());
                    }
                });
    }

    public RemoteCall<BigInteger> getNextTime() {
        final Function function = new Function(FUNC_GETNEXTTIME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> retrieve(BigInteger amount) {
        final Function function = new Function(
                FUNC_RETRIEVE, 
                Arrays.<Type>asList(new Uint256(amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> allValue() {
        final Function function = new Function(FUNC_ALLVALUE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> changeMapping(String map) {
        final Function function = new Function(
                FUNC_CHANGEMAPPING, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(map)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> getEth() {
        final Function function = new Function(
                FUNC_GETETH, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> changeGetAbonusTimeLimit(BigInteger num) {
        final Function function = new Function(
                FUNC_CHANGEGETABONUSTIMELIMIT, 
                Arrays.<Type>asList(new Uint256(num)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static IBAbonusContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new IBAbonusContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static IBAbonusContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new IBAbonusContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }


    public static RemoteCall<IBAbonusContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String map) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(map)));
        return deployRemoteCall(IBAbonusContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<IBAbonusContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String map) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(map)));
        return deployRemoteCall(IBAbonusContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }
}
