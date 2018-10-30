package io.api.core.impl;

import io.api.core.IAccountProvider;
import io.api.error.EtherScanException;
import io.api.manager.IQueueManager;
import io.api.model.*;
import io.api.model.temporary.*;
import io.api.util.BasicUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ! NO DESCRIPTION !
 *
 * @author GoodforGod
 * @since 28.10.2018
 */
public class AccountProvider extends BasicProvider implements IAccountProvider {

    private static final int MAX_END_BLOCK = 999999999;
    private static final int MIN_START_BLOCK = 0;

    private static final String BALANCE_ACTION = ACTION_PARAM + "balance";
    private static final String BALANCE_MULTI_ACTION = ACTION_PARAM + "balancemulti";
    private static final String TX_ACTION = ACTION_PARAM + "txlist";
    private static final String TX_INTERNAL_ACTION = ACTION_PARAM + "txlistinternal";
    private static final String TX_TOKEN_ACTION = ACTION_PARAM + "tokentx";
    private static final String MINED_ACTION = ACTION_PARAM + "getminedblocks";

    private static final String BLOCK_TYPE_PARAM = "&blocktype=blocks";
    private static final String TAG_LATEST_PARAM = "&tag=latest";
    private static final String START_BLOCK_PARAM = "&startblock=";
    private static final String END_BLOCK_PARAM = "&endblock=";
    private static final String SORT_DESC_PARAM = "&sort=desc";
    private static final String SORT_ASC_PARAM = "&sort=asc";
    private static final String ADDRESS_PARAM = "&address=";
    private static final String TXHASH_PARAM = "&txhash=";
    private static final String OFFSET_PARAM = "&offset=";
    private static final String PAGE_PARAM = "&page=";

    public AccountProvider(final IQueueManager queueManager,
                           final String baseUrl,
                           final Map<String, String> header) {
        super(queueManager, "account", baseUrl, header);
    }

    @NotNull
    @Override
    public Balance balance(final String address) {
        BasicUtils.validateAddress(address);

        final String urlParams = BALANCE_ACTION + TAG_LATEST_PARAM + ADDRESS_PARAM + address;
        final StringResponseTO converted = getRequest(urlParams, StringResponseTO.class);
        if (converted.getStatus() != 1)
            throw new EtherScanException(converted.getMessage() + ", with status " + converted.getStatus());

        return new Balance(address, Long.valueOf(converted.getResult()));
    }

    @NotNull
    @Override
    public List<Balance> balances(final List<String> addresses) {
        if (BasicUtils.isEmpty(addresses))
            return Collections.emptyList();

        BasicUtils.validateAddresses(addresses);

        // Maximum addresses in batch request - 20
        final List<Balance> balances = new ArrayList<>();
        final List<List<String>> addressesAsBatches = BasicUtils.partition(addresses, 20);

        for (final List<String> batch : addressesAsBatches) {
            final String urlParams = BALANCE_MULTI_ACTION + TAG_LATEST_PARAM + ADDRESS_PARAM + toAddressParam(batch);
            final BalanceResponseTO converted = getRequest(urlParams, BalanceResponseTO.class);
            if (converted.getStatus() != 1)
                throw new EtherScanException(converted.getMessage() + ", with status " + converted.getStatus());

            if (!BasicUtils.isEmpty(converted.getBalances()))
                balances.addAll(converted.getBalances().stream()
                        .map(Balance::of)
                        .collect(Collectors.toList()));
        }

        return balances;
    }

    private String toAddressParam(final List<String> addresses) {
        return addresses.stream().collect(Collectors.joining(","));
    }

    @NotNull
    @Override
    public List<Tx> txs(final String address) {
        //TODO all txs implementations with pagination
        return txs(address, MIN_START_BLOCK);
    }

    @NotNull
    @Override
    public List<Tx> txs(final String address, final long startBlock) {
        return txs(address, startBlock, MAX_END_BLOCK);
    }

    @NotNull
    @Override
    public List<Tx> txs(final String address, final long startBlock, final long endBlock) {
        BasicUtils.validateAddress(address);

        final String blockParam = START_BLOCK_PARAM + startBlock + END_BLOCK_PARAM + endBlock;
        final String urlParams = TX_ACTION + ADDRESS_PARAM + address + blockParam + SORT_ASC_PARAM;
        final TxResponseTO converted = getRequest(urlParams, TxResponseTO.class);
        if (converted.getStatus() != 1)
            throw new EtherScanException(converted.getMessage() + " with status " + converted.getStatus());

        return (converted.getResult() == null)
                ? Collections.emptyList()
                : converted.getResult();
    }

    @NotNull
    @Override
    public List<TxInternal> txsInternal(final String address) {
        //TODO all txs implementations with pagination
        return txsInternal(address, MIN_START_BLOCK);
    }

    @NotNull
    @Override
    public List<TxInternal> txsInternal(final String address, final long startBlock) {
        return txsInternal(address, startBlock, MAX_END_BLOCK);
    }

    @NotNull
    @Override
    public List<TxInternal> txsInternal(final String address, final long startBlock, final long endBlock) {
        BasicUtils.validateAddress(address);

        final String blockParam = START_BLOCK_PARAM + startBlock + END_BLOCK_PARAM + endBlock;
        final String urlParams = TX_INTERNAL_ACTION + ADDRESS_PARAM + address + blockParam + SORT_ASC_PARAM;
        final TxInternalResponseTO converted = getRequest(urlParams, TxInternalResponseTO.class);
        if (converted.getStatus() != 1)
            throw new EtherScanException(converted.getMessage() + " with status " + converted.getStatus());

        return (converted.getResult() == null)
                ? Collections.emptyList()
                : converted.getResult();
    }

    @NotNull
    @Override
    public List<TxInternal> txsInternalByHash(final String txhash) {
        BasicUtils.validateTxHash(txhash);

        final String urlParams = TX_INTERNAL_ACTION + TXHASH_PARAM + txhash;
        final TxInternalResponseTO converted = getRequest(urlParams, TxInternalResponseTO.class);
        if (converted.getStatus() != 1)
            throw new EtherScanException(converted.getMessage() + " with status " + converted.getStatus());

        return (converted.getResult() == null)
                ? Collections.emptyList()
                : converted.getResult();
    }

    @NotNull
    @Override
    public List<TxToken> txsToken(final String address) {
        //TODO all txs implementations with pagination
        return txsToken(address, MIN_START_BLOCK);
    }

    @NotNull
    @Override
    public List<TxToken> txsToken(final String address, final long startBlock) {
        return txsToken(address, startBlock, MAX_END_BLOCK);
    }

    @NotNull
    @Override
    public List<TxToken> txsToken(final String address, final long startBlock, final long endBlock) {
        BasicUtils.validateAddress(address);

        final String blockParam = START_BLOCK_PARAM + startBlock + END_BLOCK_PARAM + endBlock;
        final String urlParams = TX_TOKEN_ACTION + ADDRESS_PARAM + address + blockParam + SORT_ASC_PARAM;
        final TxTokenResponseTO converted = getRequest(urlParams, TxTokenResponseTO.class);
        if (converted.getStatus() != 1)
            throw new EtherScanException(converted.getMessage() + " with status " + converted.getStatus());

        return (converted.getResult() == null)
                ? Collections.emptyList()
                : converted.getResult();
    }

    @NotNull
    @Override
    public List<Block> minedBlocks(final String address) {
        BasicUtils.validateAddress(address);

        final String urlParams = MINED_ACTION + BLOCK_TYPE_PARAM + ADDRESS_PARAM + address;
        final BlockResponseTO converted = getRequest(urlParams, BlockResponseTO.class);
        if (converted.getStatus() != 1)
            throw new EtherScanException(converted.getMessage() + " with status " + converted.getStatus());

        return (converted.getResult() == null)
                ? Collections.emptyList()
                : converted.getResult();
    }
}