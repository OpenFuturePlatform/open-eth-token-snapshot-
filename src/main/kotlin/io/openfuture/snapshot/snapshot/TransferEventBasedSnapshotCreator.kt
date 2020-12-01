package io.openfuture.snapshot.snapshot

import io.openfuture.snapshot.domain.WalletState
import org.web3j.protocol.core.methods.response.EthLog
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger

class TransferEventBasedSnapshotCreator(nodeAddress: String) : CommonSnapshotCreator(nodeAddress) {

    override fun snapshot(contractAddress: String, fromBlock: Int, toBlock: Int): Set<WalletState> {
        val walletStateMap: MutableMap<String, BigDecimal> = mutableMapOf()

        for (blockNumber in fromBlock..toBlock step BATCH_SIZE) {
            var nextBatch = blockNumber + BATCH_SIZE - 1
            if (nextBatch >= toBlock) nextBatch = toBlock

            println("Batch fetching addresses from $blockNumber to $nextBatch blocks")
            val logs = getTransferLogs(contractAddress, blockNumber, nextBatch)
            val transfers = fetchTransferEvents(logs)
            calculateBalances(walletStateMap, transfers)
        }

        return walletStateMap.map { WalletState(it.key, it.value) }.toSet()
    }

    private fun calculateBalances(walletStateMap: MutableMap<String, BigDecimal>, transfers: List<TransferEventData>) {
        transfers.forEach {
            val toAmount = walletStateMap.getOrDefault(it.toAddress, BigDecimal.ZERO)

            walletStateMap[it.toAddress] = toAmount.plus(it.amount)

            if (GENESIS_ADDRESS == it.fromAddress) {
                return
            }

            val fromAmount = walletStateMap.getOrDefault(it.fromAddress, BigDecimal.ZERO)

            walletStateMap.replace(it.fromAddress, fromAmount.minus(it.amount))
        }
    }

    private fun fetchTransferEvents(transferLogs: List<EthLog.LogResult<Any>>): List<TransferEventData> {
        return transferLogs.map {
            val log = (it.get() as EthLog.LogObject)
            val topics: List<String> = log.topics

            val fromAddress = decodeAddress(topics[1]).value
            val toAddress = decodeAddress(topics[2]).value
            val amount = BigDecimal(BigInteger(log.data.substring(2), 16))
            val converted = Convert.fromWei(amount, Convert.Unit.GWEI)

            TransferEventData(fromAddress, toAddress, converted)
        }
    }

    private data class TransferEventData(
            val fromAddress: String,
            val toAddress: String,
            val amount: BigDecimal
    )

    companion object {
        const val GENESIS_ADDRESS = "0x0000000000000000000000000000000000000000"
    }

}
