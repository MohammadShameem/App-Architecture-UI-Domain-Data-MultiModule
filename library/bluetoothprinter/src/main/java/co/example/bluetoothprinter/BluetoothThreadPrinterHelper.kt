package co.example.bluetoothprinter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import co.example.common.constant.AppConstant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import java.util.*

private const val TAG = "BluetoothPrintService"

// Defines several constants used when transmitting messages between the
// service and the UI.


class BluetoothThreadPrinterHelper(
    context: Context
) {


    private val _connectionState = MutableStateFlow(STATE.START)
    val connectionState get() = _connectionState.asStateFlow()

    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

    private var connectThread: ConnectThread? = null
    private var connectedThread: ConnectedThread? = null


    private  var bluetoothAdapter:BluetoothAdapter? = null

    init {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        bluetoothAdapter = bluetoothManager?.adapter
    }

    /**
     * It will check bluetooth is enable or not
     * if not then it will open a system dialog
     * to turn on bluetooth
     * */
    fun isBluetoothEnable():Boolean{
        return bluetoothAdapter?.isEnabled ?: false
    }

    fun checkBluetoothAdapterIsAvailable():Boolean{
        return bluetoothAdapter != null
    }


    //--------------------get pair device---------------------------------
    @SuppressLint("MissingPermission")
    fun pairDevices(){
        if (bluetoothAdapter == null){
            //_printerErrorMessage.value = "Printer device is offline."
            _connectionState.value = STATE.DISCONNECTED
            return
        }
        val pairDevices = bluetoothAdapter?.bondedDevices
        pairDevices?.forEach{
            if (it.name == AppConstant.innerPrinter){
                startConnectThread(it)
            }
        }

    }

    @Synchronized
    private fun startConnectThread(device: BluetoothDevice) {
        resetActiveBluetoothThreads()
        // Start the thread to connect with the given device
        connectThread = ConnectThread(device)
        connectThread?.start()
    }
    private fun startConnectedThread(bluetoothSocket: BluetoothSocket?) {
        resetActiveBluetoothThreads()
        // Start the thread to manage the connection and perform transmissions
        connectedThread = ConnectedThread(bluetoothSocket)
        connectedThread?.start()
    }

    @Synchronized
    fun sendByteArray(bytes: ByteArray) {
        // Create temporary object
        var r: ConnectedThread?
        // Synchronize a copy of the ConnectedThread
        synchronized(this) {
            if (_connectionState.value != STATE.CONNECTED) return
            r = connectedThread
        }
        // Perform the write unSynchronized
        r?.write(bytes)
    }
    private fun resetActiveBluetoothThreads() {
        // Cancel the thread that completed the connection
        connectThread?.run {
            cancel()
            connectThread = null
        }

        // Cancel any thread currently running a connection
        connectedThread?.run {
            cancel()
            connectedThread = null
        }
    }
    private fun connectionLost() {
        _connectionState.value = STATE.DISCONNECTED
        resetActiveBluetoothThreads()
    }



    //Threads
    @SuppressLint("MissingPermission")
    private inner class ConnectThread(device: BluetoothDevice) : Thread() {
        private val bluetoothSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(uuid)
        }

        override fun run() {
            _connectionState.value = STATE.CONNECTING
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()

            try {
                /**
                 * Connect to the remote device through the socket.
                 * This call blocks until it succeeds or throws an IOException.
                 * If the connection fails, or if the connect() method times out (after about 12 seconds),
                 * then the method throws an IOException.
                 * */
                bluetoothSocket?.connect()

                // Reset the ConnectThread because we're done
                synchronized(this@BluetoothThreadPrinterHelper) {
                    connectThread = null
                }
                /**
                 *  The connection attempt succeeded.
                 *  Perform work associated with the connection in a separate thread.
                 *  */
                Log.d(TAG, "Printer Successfully Connected!")
                // Start connection on a new thread
                startConnectedThread(bluetoothSocket)
            } catch (e: IOException) {
                e.printStackTrace()
                connectionLost()
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                bluetoothSocket?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private inner class ConnectedThread(private val socket: BluetoothSocket?) : Thread() {
        private val outputStream = socket?.outputStream
        init {
            _connectionState.value = STATE.CONNECTED
        }

        override fun run() {
            // Keep listening to the InputStream until an exception occurs.
            while (_connectionState.value == STATE.CONNECTED) {
                // Read from the InputStream.
/*                numBytes = try {
                    inputStream?.read(buffer) ?: 0
                } catch (e: IOException) {
                    Log.e(TAG, "${this.name} Input stream was disconnected", e)
                    connectionLost()
                    break
                }*/
            }
        }

        // Call this from the main activity to send data to the remote device.
        fun write(bytes: ByteArray) {
            try {
                outputStream?.write(bytes)
                Log.d(TAG, "\"${String(bytes)}\" is printed!")
            } catch (e: IOException) {
                connectionLost()
                e.printStackTrace()
            }
        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {
                socket?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * @param text Text to print
     * @param size
     * @param isBold
     * @param alignment PrinterAlignment.LEFT,CENTER,RIGHT
     * */
    fun printEnglishText(text: String, size: TextSize, isBold: Boolean=false) {
        setCharacterSize(size)
        /**
         * Turn emphasized mode on/off
         * ASCII    ESC E n
         * Hex      0x1B 0x45 n
         * Decimal  27 69 n
         * When the LSB of n is 0, emphasized mode is turned off.
         * When the LSB of n is 1, emphasized mode is turned on.
         * */
        val emphasizedMode = byteArrayOf(0x1B, 0x45, 0x00)
        if (isBold) emphasizedMode[2] = 0x01
        else emphasizedMode[2] = 0x00
        sendByteArray(emphasizedMode)

        sendByteArray(text.toByteArray())
    }

    /**
     * @param text String
     * @param size Int 1~15
     * @param qrCodeType
     * */
    fun printQRCode(text: String, size: Int, qrCodeType: Int) {
        val textBytes = text.toByteArray(charset("UTF-8"))
        val commandLength = textBytes.size + 3
        val pL = commandLength % 256
        val pH = commandLength / 256

        sendByteArray(byteArrayOf(0x1D, 0x28, 0x6B, 0x04, 0x00, 0x31, 0x41, qrCodeType.toByte(), 0x00))
        sendByteArray(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x43, size.toByte()))
        sendByteArray(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x45, 0x30))

        val qrCodeCommand = ByteArray(textBytes.size + 8)
        System.arraycopy(byteArrayOf(0x1D, 0x28, 0x6B, pL.toByte(), pH.toByte(), 0x31, 0x50, 0x30),
            0, qrCodeCommand, 0, 8)
        System.arraycopy(textBytes, 0, qrCodeCommand, 8, textBytes.size)
        sendByteArray(qrCodeCommand)
        sendByteArray(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30))

    }


    fun printImage(bitmap: Bitmap){
        val imageBytes = ImagePrintPosHelper.bitmapToBytes(bitmap)
        ImagePrintPosHelper.bytesToHexadecimalString(imageBytes)
        sendByteArray(imageBytes)
    }


    private fun setCharacterSize(size: TextSize){
        /**
         * Select character size
         * ASCII   GS ! n
         * Hex     1D 21 n
         * Decimal 29 33 n
         */
        when(size){
            TextSize.NORMAL -> sendByteArray(byteArrayOf(29, 33, CharacterSize.NORMAL))
            TextSize.DOUBLE -> {
                sendByteArray(byteArrayOf(29, 33, CharacterSize.HEIGHT_x2))
                sendByteArray(byteArrayOf(29, 33, CharacterSize.WIDTH_x2))
            }
            TextSize.TRIPLE ->{
                sendByteArray(byteArrayOf(29, 33, CharacterSize.HEIGHT_x3))
                sendByteArray(byteArrayOf(29, 33, CharacterSize.WIDTH_x3))
            }
            TextSize.QUADRUPLE -> {
                sendByteArray(byteArrayOf(29, 33, CharacterSize.HEIGHT_x4))
                sendByteArray(byteArrayOf(29, 33, CharacterSize.WIDTH_x4))
            }
            TextSize.DOUBLE_HEIGHT -> sendByteArray(byteArrayOf(29, 33, CharacterSize.HEIGHT_x2))
            TextSize.DOUBLE_WIDTH -> sendByteArray(byteArrayOf(29, 33, CharacterSize.WIDTH_x2))
            TextSize.TRIPLE_HEIGHT -> sendByteArray(byteArrayOf(29, 33, CharacterSize.HEIGHT_x3))
            TextSize.TRIPLE_WIDTH -> sendByteArray(byteArrayOf(29, 33, CharacterSize.WIDTH_x3))
            TextSize.QUADRUPLE_HEIGHT -> sendByteArray(byteArrayOf(29, 33, CharacterSize.HEIGHT_x4))
            TextSize.QUADRUPLE_WIDTH -> sendByteArray(byteArrayOf(29, 33, CharacterSize.WIDTH_x4))
            TextSize.DOUBLE_2_5 -> sendByteArray(byteArrayOf(29, 33, CharacterSize.WIDTH_x2_5))
        }

    }


    fun setAlignment(alignment: Byte) {
        /**
         * Hex:     0x1B 0x61 n
         * Decimal: 27 97 n
         * n=0 for Left, 1 for Center, 2 for Right
         * */
        sendByteArray(byteArrayOf(27, 97, alignment))
    }

    fun enableUnderLine(isUnderLine: Boolean) {
        /**
         * Turn underline mode on/off
         * ASCII    ESC - n
         * Hex:     0x1B, 0x2D, n
         * Decimal: 27, 45, n
         * 0, 48 Turns off underline mode
         * 1, 49 Turns on underline mode (1-dot thick)
         * 2, 50 Turns on underline mode (2-dots thick)
         * */
        val textUnderlineCmd = byteArrayOf(0x1B, 0x2D, 0x00)
        if (isUnderLine) textUnderlineCmd[2] = 0x01
        sendByteArray(textUnderlineCmd)
    }
}

object Alignment{
    const val LEFT: Byte = 0
    const val CENTER: Byte = 1
    const val RIGHT: Byte = 2
}

object CharacterSize{
    const val NORMAL: Byte = 0
    const val HEIGHT_x2: Byte = 1
    const val HEIGHT_x3: Byte = 2
    const val HEIGHT_x4: Byte = 3

    const val WIDTH_x2: Byte = 16
    const val WIDTH_x2_5: Byte = 17

    const val WIDTH_x3: Byte = 32
    const val WIDTH_x4: Byte = 48
}

enum class TextSize {
    NORMAL,
    DOUBLE,
    DOUBLE_HEIGHT,
    DOUBLE_WIDTH,
    DOUBLE_2_5,
    TRIPLE,
    TRIPLE_HEIGHT,
    TRIPLE_WIDTH,
    QUADRUPLE,
    QUADRUPLE_HEIGHT,
    QUADRUPLE_WIDTH
}

enum class STATE {
    START,
    DISCONNECTED,     // we're doing nothing
    CONNECTING, // now initiating an outgoing connection
    CONNECTED
}



