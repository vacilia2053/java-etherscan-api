package io.api.etherscan.model;

import io.api.etherscan.util.BasicUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * ! NO DESCRIPTION !
 *
 * @author GoodforGod
 * @since 28.10.2018
 */
abstract class BaseTx {

    private long blockNumber;
    private String timeStamp;
    private LocalDateTime _timeStamp;
    private String hash;
    private String from;
    private String to;
    private BigInteger value;
    private String contractAddress;
    private String input;
    private long gas;
    private long gasUsed;

    BaseTx() { }

    public BaseTx(long blockNumber, LocalDateTime _timeStamp, String hash,
                  String from, String to, BigInteger value, String contractAddress,
                  String input, long gas, long gasUsed) {
        this.blockNumber = blockNumber;
        this._timeStamp = _timeStamp;
        this.hash = hash;
        this.from = from;
        this.to = to;
        this.value = value;
        this.contractAddress = contractAddress;
        this.input = input;
        this.gas = gas;
        this.gasUsed = gasUsed;
    }

    //<editor-fold desc="Getter">
    public long getBlockNumber() {
        return blockNumber;
    }

    public LocalDateTime getTimeStamp() {
        if(_timeStamp == null && !BasicUtils.isEmpty(timeStamp))
            _timeStamp = LocalDateTime.ofEpochSecond(Long.valueOf(timeStamp), 0, ZoneOffset.UTC);
        return _timeStamp;
    }

    public String getHash() {
        return hash;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public BigInteger getValue() {
        return value;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String getInput() {
        return input;
    }

    public long getGas() {
        return gas;
    }

    public long getGasUsed() {
        return gasUsed;
    }
    //</editor-fold>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseTx baseTx = (BaseTx) o;

        return hash != null ? hash.equals(baseTx.hash) : baseTx.hash == null;
    }

    @Override
    public int hashCode() {
        return hash != null ? hash.hashCode() : 0;
    }
}