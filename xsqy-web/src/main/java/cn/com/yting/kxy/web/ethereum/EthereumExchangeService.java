/*
 * Created 2018-8-27 18:06:47
 */
package cn.com.yting.kxy.web.ethereum;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.Callable;

import cn.com.yting.kxy.core.TimeProvider;
import cn.com.yting.kxy.core.resetting.ResetTask;
import cn.com.yting.kxy.core.resource.ResourceContext;
import cn.com.yting.kxy.core.util.TimeUtils;
import cn.com.yting.kxy.web.KxyWebConstants;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.currency.CurrencyConstants;
import cn.com.yting.kxy.web.currency.CurrencyRecord;
import cn.com.yting.kxy.web.currency.CurrencyService;
import cn.com.yting.kxy.web.currency.kuaibi.KuaibiUnits;
import cn.com.yting.kxy.web.equipment.Equipment;
import cn.com.yting.kxy.web.equipment.EquipmentConstants;
import cn.com.yting.kxy.web.equipment.EquipmentRepository;
import cn.com.yting.kxy.web.equipment.resource.EquipmentProduce;
import cn.com.yting.kxy.web.pet.Pet;
import cn.com.yting.kxy.web.pet.PetRepository;
import cn.com.yting.kxy.web.pet.resource.PetInformations;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRelationRepository;
import cn.com.yting.kxy.web.player.PlayerRepository;
import cn.com.yting.kxy.web.sms.ShortMessageServiceApi;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.response.NoOpProcessor;

/**
 *
 * @author Azige
 */
@Service
/*
 * Serializable 隔离级别可能会导致定时任务查询全表的时候插入新的记录失败而使得交易无法被跟踪，因此不需要这个级别
 */
@Transactional
public class EthereumExchangeService implements InitializingBean, DisposableBean, ResetTask {

    private static final Logger LOG = LoggerFactory.getLogger(EthereumExchangeService.class);
    /**
     * 毫块币与块币最小单位的转换比例 服务端使用的块币基准单位是毫块币(1e-3块币)，区块链合约存储的块币基准单位是1e-18块币，比例是1e15
     */
    private static final BigInteger KUAIBI_CONVERTION_SCALE = BigInteger.TEN.pow(15);
    /**
     * 交易超时的时间，单位是毫秒
     */
    private static final long TRANSACTION_TIMEOUT = 600_000; // 10分钟

    @Value("${kxy.web.ethereum.endpoint}")
    private String ethereumEndpoint;
    @Value("${kxy.web.ethereum.kuaibi.address}")
    private String kuaibiAddress;

    @Autowired
    private WithdrawRequestRepository withdrawRequestRepository;
    @Autowired
    private DepositRequestRepository depositRequestRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PlayerRelationRepository playerRelationRepository;
    @Autowired
    private ExchangeRepository exchangeRepository;
    @Autowired
    private ExchangeSharedRepository exchangeSharedRepository;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CredentialsHolder credentialsHolder;
    @Autowired
    private TimeProvider timeProvider;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private ResourceContext resourceContext;
    @Autowired
    private ShortMessageServiceApi sms;

    private Web3j web3j;
    private Erc20 kuaibi;
    private TransactionTemplate transactionTemplate;

    private final Object serverKuaibiLock = new Object();
    private BigInteger serverKuaibi = BigInteger.ZERO;
    private FastRawTransactionManager tm;

    public EthereumExchangeService() {
    }

    public EthereumExchangeService(WithdrawRequestRepository withdrawRequestRepository, CurrencyService currencyService, CredentialsHolder credentialsHolder, TimeProvider timeProvider, PlatformTransactionManager transactionManager, ApplicationEventPublisher eventPublisher) {
        this.withdrawRequestRepository = withdrawRequestRepository;
        this.currencyService = currencyService;
        this.credentialsHolder = credentialsHolder;
        this.timeProvider = timeProvider;
        this.transactionManager = transactionManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        web3j = Web3j.build(new HttpService(ethereumEndpoint));
        // 配置在区块链网络上提交交易请求时，不用等待交易完成直接返回仅包含 tx hash 的交易回执。便于异步处理
        tm = new FastRawTransactionManager(web3j, credentialsHolder.getCredentials(), new NoOpProcessor(web3j));
        kuaibi = Erc20.load(kuaibiAddress, web3j, tm, DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT);
        transactionTemplate = new TransactionTemplate(transactionManager);
        Completable.fromAction(this::syncServerKuaibi)
                .retry(10)
                .subscribeOn(Schedulers.io())
                .subscribe();
        exchangeSharedRepository.init(ExchangeSharedRecord::new);
    }

    @Override
    public void destroy() throws Exception {
        web3j.shutdown();
    }

    public WithdrawRequest withdrawKuaibi(long accountId, long amount, String toAddress) {
        if (amount < EthereumExchangeConstants.WITHDRAW_MIN_AMOUNT) {
            throw EthereumExchangeException.提现块币数低于最低提现要求();
        }
        checkPlayerCondition(accountId);
        //
        ExchangeRecord exchangeRecord = exchangeRepository.findOrCreateRecord(accountId);
        if (exchangeRecord.getLastKuaibiWithdrawTime() != null) {
            LocalDate today = timeProvider.today();
            LocalDate lastWithdrawDate = TimeUtils.toOffsetTime(exchangeRecord.getLastKuaibiWithdrawTime()).toLocalDate();
            if (lastWithdrawDate.isBefore(today)) {
                exchangeRecord.setKuaibiWithdrawCount(0);
            }
        }
        if (exchangeRecord.getKuaibiWithdrawCount() + amount > EthereumExchangeConstants.LIMIT_PER_ACCOUNT_DAILY_WITHDRAW) {
            throw EthereumExchangeException.withdrawHitLimit();
        }
        ExchangeSharedRecord exchangeSharedRecord = exchangeSharedRepository.getTheRecordForWrite();
        if (exchangeSharedRecord.getKuaibiWithdrawCount() + amount > EthereumExchangeConstants.LIMIT_GLOBAL_DAILY_WITHDRAW) {
            throw EthereumExchangeException.withdrawHitGlobalLimit();
        }
        long fee = withdrawKuaibiFee(amount);
        CurrencyRecord currencyRecord = currencyService.findOrCreateRecord(accountId, CurrencyConstants.ID_毫仙石);
        if (currencyRecord.getAmount() < amount + fee) {
            throw KxyWebException.unknown("余额不足");
        }
        BigInteger valueToTransfer = BigInteger.valueOf(amount).multiply(KUAIBI_CONVERTION_SCALE);
        synchronized (serverKuaibiLock) {
            if (serverKuaibi.compareTo(valueToTransfer) < 0) {
                throw KxyWebException.unknown("服务端账户上块币不足");
            }
            serverKuaibi = serverKuaibi.subtract(valueToTransfer);
        }
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_毫仙石, amount, true, CurrencyConstants.PURPOSE_DECREMENT_提现);
        currencyService.decreaseCurrency(accountId, CurrencyConstants.ID_毫仙石, fee, true, CurrencyConstants.PURPOSE_DECREMENT_提现手续费);

        TransactionReceipt transactionReceipt;
        try {
            transactionReceipt = kuaibi.transfer(toAddress, valueToTransfer).send();
        } catch (Exception ex) {
            // 重设 FastRawTransactionManager 的自增 nonce 为未初始化状态，避免因为发起交易失败而导致 nonce 不正确从而使得交易无法正常处理
            tm.setNonce(BigInteger.valueOf(-1));
            LOG.error("发送转账请求失败", ex);
            throw KxyWebException.internalError("发送转账请求失败", ex);
        }

        Date currentTime = new Date(timeProvider.currentTime());
        exchangeRecord.setLastKuaibiWithdrawTime(currentTime);
        exchangeRecord.increaseKuaibiWithdrawCount(amount);
        exchangeSharedRecord.increaseKuaibiWithdrawCount(amount);

//        if (exchangeSharedRecord.getKuaibiWithdrawCount() > EthereumExchangeConstants.LIMIT_GLOBAL_DAILY_WITHDRAW_WARN) {
//            Completable.fromRunnable(() -> {
//                long product = EthereumExchangeConstants.LIMIT_GLOBAL_DAILY_WITHDRAW - exchangeSharedRecord.getKuaibiWithdrawCount();
//                sms.sendKuaibiWithdrawMessage("15976633491", product);
//                sms.sendKuaibiWithdrawMessage("13827736629", product);
//                sms.sendKuaibiWithdrawMessage("13718483267", product);
//            })
//                .subscribeOn(Schedulers.io())
//                .subscribe();
//        }
        WithdrawRequest request = new WithdrawRequest();
        request.setAccountId(accountId);
        request.setTransactionHash(transactionReceipt.getTransactionHash());
        request.setAmount(amount);
        request.setFee(fee);
        request.setRequestStatus(WithdrawRequestStatus.PENDING);
        request.setCreateTime(currentTime);
        request = withdrawRequestRepository.saveAndFlush(request);

        return request;
    }

    public DepositRequest depositeKuaibi(long accountId, long amount, String fromAddress) {
        if (amount < EthereumExchangeConstants.DEPOSIT_MIN_AMOUNT) {
            throw KxyWebException.unknown("存入数量过少");
        }
        BigInteger valueToTransfer = BigInteger.valueOf(amount).multiply(KUAIBI_CONVERTION_SCALE);
        if (!depositRequestRepository.findByAccountIdAndRequestStatus(accountId, DepositRequestStatus.PENDING).isEmpty()) {
            throw KxyWebException.unknown("当前有交易正在等待处理");
        }

        BigInteger allowance;
        try {
            allowance = kuaibi.allowance(fromAddress, credentialsHolder.getCredentials().getAddress()).send();
        } catch (Exception ex) {
            throw KxyWebException.internalError("查询允许转账的金额失败", ex);
        }
        if (allowance.compareTo(valueToTransfer) < 0) {
            throw KxyWebException.unknown("允许转账的金额不足");
        }

        TransactionReceipt transactionReceipt;
        try {
            transactionReceipt = kuaibi.transferFrom(fromAddress, credentialsHolder.getCredentials().getAddress(), valueToTransfer).send();
        } catch (Exception ex) {
            // 重设 FastRawTransactionManager 的自增 nonce 为未初始化状态，避免因为发起交易失败而导致 nonce 不正确从而使得交易无法正常处理
            tm.setNonce(BigInteger.valueOf(-1));
            LOG.error("发送转账请求失败", ex);
            throw KxyWebException.internalError("发送转账请求失败", ex);
        }
        DepositRequest request = new DepositRequest();
        request.setAccountId(accountId);
        request.setTransactionHash(transactionReceipt.getTransactionHash());
        request.setAmount(amount);
        request.setRequestStatus(DepositRequestStatus.PENDING);
        request.setCreateTime(new Date(timeProvider.currentTime()));
        request = depositRequestRepository.saveAndFlush(request);

        return request;
    }

    @Scheduled(cron = "0/10 * * * * *")
    @SuppressWarnings("CheckReturnValue")
    public void checkTransactionReceipts() {
        withdrawRequestRepository.findByRequestStatus(WithdrawRequestStatus.PENDING).forEach(request -> {
            Single.fromCallable(web3j.ethGetTransactionReceipt(request.getTransactionHash())::send)
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                        TransactionReceipt transactionReceipt = response.getResult();
                        transactionTemplate.execute(status -> {
                            WithdrawRequest r = withdrawRequestRepository.findByIdForWrite(request.getId());
                            if (r.getRequestStatus().equals(WithdrawRequestStatus.PENDING)) {
                                if (transactionReceipt != null) {
                                    if (transactionReceipt.isStatusOK()) {
                                        r.setRequestStatus(WithdrawRequestStatus.SUCCEEDED);
                                    } else {
                                        r.setRequestStatus(WithdrawRequestStatus.FAILED);
                                        refundForIncompletableTransaction(r);
                                    }
                                    eventPublisher.publishEvent(new WithdrawCompletedEvent(this, r));
                                } else if (timeProvider.currentTime() - r.getCreateTime().getTime() > TRANSACTION_TIMEOUT) {
                                    r.setRequestStatus(WithdrawRequestStatus.TIMEOUT);
                                    refundForIncompletableTransaction(r);
                                }
                            }
                            return null;
                        });
                    });
        });
        depositRequestRepository.findByRequestStatus(DepositRequestStatus.PENDING).forEach(request -> {
            Single.fromCallable(web3j.ethGetTransactionReceipt(request.getTransactionHash())::send)
                    .subscribeOn(Schedulers.io())
                    .subscribe(response -> {
                        TransactionReceipt transactionReceipt = response.getResult();
                        transactionTemplate.execute(status -> {
                            DepositRequest r = depositRequestRepository.findByIdForWrite(request.getId());
                            if (r.getRequestStatus().equals(DepositRequestStatus.PENDING)) {
                                if (transactionReceipt != null) {
                                    if (transactionReceipt.isStatusOK()) {
                                        r.setRequestStatus(DepositRequestStatus.SUCCEEDED);
                                        currencyService.increaseCurrency(r.getAccountId(), CurrencyConstants.ID_毫仙石, r.getAmount(), CurrencyConstants.PURPOSE_INCREMENT_充值);
                                    } else {
                                        r.setRequestStatus(DepositRequestStatus.FAILED);
                                    }
                                    eventPublisher.publishEvent(new DepositCompletedEvent(this, r));
                                } else if (timeProvider.currentTime() - r.getCreateTime().getTime() > TRANSACTION_TIMEOUT) {
                                    r.setRequestStatus(DepositRequestStatus.TIMEOUT);
                                }
                            }
                            return null;
                        });
                    });
        });
    }

    private void refundForIncompletableTransaction(WithdrawRequest request) {
        currencyService.increaseCurrency(request.getAccountId(), CurrencyConstants.ID_毫仙石, request.getAmount(), CurrencyConstants.PURPOSE_INCREMENT_提现失败退还);
        currencyService.increaseCurrency(request.getAccountId(), CurrencyConstants.ID_毫仙石, request.getFee(), CurrencyConstants.PURPOSE_INCREMENT_提现失败退还手续费);
        synchronized (serverKuaibiLock) {
            serverKuaibi = serverKuaibi.add(BigInteger.valueOf(request.getAmount()).multiply(KUAIBI_CONVERTION_SCALE));
        }
    }

    public BigInteger getServerKuaibi() {
        return serverKuaibi;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void syncServerKuaibi() throws Exception {
        synchronized (serverKuaibiLock) {
            serverKuaibi = kuaibi.balanceOf(credentialsHolder.getCredentials().getAddress()).send();
        }
    }

    public Erc20 getKuaibi() {
        return kuaibi;
    }

    private long withdrawKuaibiFee(long amount) {
        // 取整到整块币
        return ((long) Math.ceil(amount * 0.1 * 0.001)) * 1000;
    }

    private <T> T resetNonceOnFail(Callable<T> function) {
        try {
            return function.call();
        } catch (Exception ex) {
            // 重设 FastRawTransactionManager 的自增 nonce 为未初始化状态，避免因为发起交易失败而导致 nonce 不正确从而使得交易无法正常处理
            tm.setNonce(BigInteger.valueOf(-1));
            LOG.error("发送转账请求失败", ex);
            throw KxyWebException.internalError("发送转账请求失败", ex);
        }
    }

    private void checkPlayerCondition(long accountId) {
        Player player = playerRepository.findById(accountId).get();
        if (player.getPlayerLevel() < 50) {
            throw EthereumExchangeException.提现所需等级不足();
        }
        if (player.getFc() < 20000) {
            throw EthereumExchangeException.提现所需战斗力不足();
        }
    }

    @Override
    public void dailyReset() {
        ExchangeSharedRecord record = exchangeSharedRepository.getTheRecordForWrite();
        record.setKuaibiWithdrawCount(0);
        record.setKuaibiDepositCount(0);
    }

}
