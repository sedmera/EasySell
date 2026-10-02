package com.example.easysell.printer

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import com.example.easysell.data.local.OrderWithItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.roundToInt

class BluetoothPrinter(
    private val context: Context
) {

    companion object {
        private const val PRINTER_NAME = "RPP02N"

        private val SPP_UUID: UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        private val CP852: Charset =
            Charset.forName("IBM852")
    }

    /**
     * Najde mezi spárovanými Bluetooth zařízeními tiskárnu RPP02N.
     */
    @SuppressLint("MissingPermission")
    private fun findPrinter(): BluetoothDevice? {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        val bluetoothAdapter: BluetoothAdapter =
            bluetoothManager.adapter ?: return null

        if (!bluetoothAdapter.isEnabled) {
            return null
        }

        return bluetoothAdapter.bondedDevices
            .firstOrNull { device ->
                device.name?.contains(
                    PRINTER_NAME,
                    ignoreCase = true
                ) == true
            }
    }

    /**
     * Vytiskne skutečnou objednávku.
     */
    suspend fun printOrder(
        order: OrderWithItems
    ): Result<Unit> = withContext(Dispatchers.IO) {

        val device = findPrinter()
            ?: return@withContext Result.failure(
                IllegalStateException(
                    "Tiskárna RPP02N nebyla nalezena mezi spárovanými zařízeními."
                )
            )

        var lastException: Exception? = null

        repeat(2) { attempt ->

            try {
                val socket =
                    device.createRfcommSocketToServiceRecord(SPP_UUID)

                socket.use {
                    it.connect()

                    val outputStream = it.outputStream

                    val data = buildReceipt(order)

                    outputStream.write(data)
                    outputStream.flush()

                    // Necháme tiskárnu data dokončit před uzavřením spojení.
                    delay(700)
                }

                return@withContext Result.success(Unit)

            } catch (e: Exception) {
                lastException = e

                if (attempt == 0) {
                    delay(1000)
                }
            }
        }

        Result.failure(
            lastException
                ?: IllegalStateException("Tisk účtenky se nezdařil.")
        )
    }

    /**
     * Vygeneruje obsah účtenky pro 58mm papír.
     *
     * 32 znaků je bezpečná šířka pro běžné ESC/POS tiskárny této kategorie.
     */
    private fun buildReceipt(
        order: OrderWithItems
    ): ByteArray {

        val output = ByteArrayOutputStream()

        // Inicializace tiskárny
        output.write(
            byteArrayOf(
                ESC,
                AT
            )
        )

        // CP852 - české znaky
        output.write(
            byteArrayOf(
                ESC,
                CHARSET,
                CP852_TABLE
            )
        )

        // -----------------------------
        // HLAVIČKA
        // -----------------------------

        output.write(
            byteArrayOf(
                ESC,
                ALIGN,
                CENTER
            )
        )

        output.write(
            byteArrayOf(
                ESC,
                BOLD,
                1
            )
        )

        output.write(
            "PROMLS\n".toBytes()
        )

        output.write(
            byteArrayOf(
                ESC,
                BOLD,
                0
            )
        )

        output.write(
            "ÚČTENKA\n".toBytes()
        )

        output.write(
            "\n".toBytes()
        )

        // -----------------------------
        // INFORMACE O OBJEDNÁVCE
        // -----------------------------

        output.write(
            byteArrayOf(
                ESC,
                ALIGN,
                LEFT
            )
        )

        output.write(
            "Bc. Kateřina Sedmerová\n".toBytes()
        )

        output.write(
            "IČO: 29947332\n".toBytes()
        )

        output.write(
            "Bohutice 116, 671 76\n".toBytes()
        )

        output.write(
            "\n".toBytes()
        )

        val dateFormat =
            SimpleDateFormat(
                "dd.MM.yyyy HH:mm",
                Locale.getDefault()
            )

        output.write(
            "Datum: ${dateFormat.format(Date(order.order.createdAt))}\n"
                .toBytes()
        )

        output.write(
            "ID: ${order.order.id}\n"
                .toBytes()
        )

        output.write(
            "-".repeat(32)
                .toBytes()
        )

        output.write(
            "\n".toBytes()
        )

        // -----------------------------
        // POLOŽKY
        // -----------------------------

        for (item in order.items) {

            val itemTotal =
                item.unitPrice * item.quantity

            // Název produktu
            writeWrappedText(
                output = output,
                text = item.productName,
                width = 32
            )

            // Množství + cena
            val quantityLine =
                "${item.quantity} x ${formatMoney(item.unitPrice)}"

            output.write(
                quantityLine.toBytes()
            )

            output.write(
                "\n".toBytes()
            )

            // Cena položky
            val totalLine =
                "  Celkem: ${formatMoney(itemTotal)}"

            output.write(
                totalLine.toBytes()
            )

            output.write(
                "\n\n".toBytes()
            )
        }

        // -----------------------------
        // CELKEM
        // -----------------------------

        output.write(
            "-".repeat(32)
                .toBytes()
        )

        output.write(
            "\n".toBytes()
        )

        output.write(
            byteArrayOf(
                ESC,
                BOLD,
                1
            )
        )

        output.write(
            "CELKEM: ${formatMoney(order.order.total)}\n"
                .toBytes()
        )

        output.write(
            byteArrayOf(
                ESC,
                BOLD,
                0
            )
        )

        output.write(
            "\n".toBytes()
        )

        output.write(
            byteArrayOf(
                ESC,
                ALIGN,
                CENTER
            )
        )

        output.write(
            "Děkujeme za nákup!\n".toBytes()
        )

        output.write(
            "\n\n\n".toBytes()
        )

        return output.toByteArray()
    }

    /**
     * Zalomení dlouhého názvu produktu.
     */
    private fun writeWrappedText(
        output: ByteArrayOutputStream,
        text: String,
        width: Int
    ) {
        var remaining = text.trim()

        while (remaining.length > width) {

            var splitIndex =
                remaining.lastIndexOf(
                    ' ',
                    width
                )

            if (splitIndex <= 0) {
                splitIndex = width
            }

            val line =
                remaining.substring(
                    0,
                    splitIndex
                ).trim()

            output.write(
                "$line\n".toBytes()
            )

            remaining =
                remaining.substring(
                    splitIndex
                ).trim()
        }

        if (remaining.isNotEmpty()) {
            output.write(
                "$remaining\n".toBytes()
            )
        }
    }

    /**
     * Peníze vždy s desetinnou tečkou a Kč.
     */
    private fun formatMoney(
        value: Double
    ): String {
        val rounded =
            (value * 100.0).roundToInt() / 100.0

        return String.format(
            Locale.US,
            "%.2f Kč",
            rounded
        )
    }

    private fun String.toBytes(): ByteArray =
        toByteArray(CP852)

    // ESC/POS příkazy
    private val ESC: Byte = 0x1B
    private val AT: Byte = 0x40
    private val ALIGN: Byte = 0x61

    private val LEFT: Byte = 0x00
    private val CENTER: Byte = 0x01

    private val BOLD: Byte = 0x45

    // ESC t 18 = CP852 / Latin 2
    private val CHARSET: Byte = 0x74
    private val CP852_TABLE: Byte = 18
}