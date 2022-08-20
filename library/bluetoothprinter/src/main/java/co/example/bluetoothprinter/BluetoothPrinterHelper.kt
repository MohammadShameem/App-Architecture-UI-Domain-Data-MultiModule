package co.example.bluetoothprinter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.graphics.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import kotlin.jvm.Throws

@Deprecated(message = "Not used")
class BluetoothPrinterHelper( var context: Context){
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var inputStream: InputStream
    lateinit var outPutStream: OutputStream
    private var readBuffer = ByteArray(1024)
    private var readBufferPosition:Int = 0
    private var stopWorker:Boolean = false
    private val arrayOfByte = byteArrayOf(27, 33, 0)
    enum class TextAlign{ LEFT , CENTER , RIGHT }
    enum class TextSize{ SMALL , MEDIUM , LARGE }


    private  var bluetoothAdapter:BluetoothAdapter? = null
    private val _printerErrorMessage = MutableLiveData<String>()
    val printerErrorMessage:LiveData<String> get() = _printerErrorMessage
    private val _printerSuccessMessage = MutableLiveData<String>()
    val printerSuccessMessage:LiveData<String> get() = _printerSuccessMessage

    init {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        bluetoothAdapter = bluetoothManager?.adapter
    }


    //----------------------printer operation------------------------
    fun checkBluetoothAdapterIsAvailable():Boolean{
        return bluetoothAdapter != null
    }

    /**
     * It will check bluetooth is enable or not
     * if not then it will open a system dialog
     * to turn on bluetooth
     * */
    fun isBlueToothEnable():Boolean{
        return bluetoothAdapter?.isEnabled ?: false
    }

    //--------------------get pair device---------------------------------
    @SuppressLint("MissingPermission")
    fun pairDevices(){
        if (bluetoothAdapter == null){
            _printerErrorMessage.value = "Printer device is offline."
            return
        }
        val pairDevices = bluetoothAdapter?.bondedDevices
        pairDevices?.forEach{
            if (it.name == "InnerPrinter"){
                bluetoothDevice = it
                connectWithPrinter()
            }
        }

    }


    //-------------------connect with printer---------------------------
    @SuppressLint("MissingPermission")
    private fun connectWithPrinter(){
        if (::bluetoothDevice.isInitialized){
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO){
                    val uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
                    val bluetoothSocket: BluetoothSocket by lazy(LazyThreadSafetyMode.NONE) {
                        bluetoothDevice.createRfcommSocketToServiceRecord(uuid)
                    }
                    try {
                        bluetoothSocket.connect()
                        _printerSuccessMessage.postValue("Device successfully connected")
                        inputStream = bluetoothSocket.inputStream
                        outPutStream = bluetoothSocket.outputStream
                        beginToListenCommand()
                    }catch (e: Exception){
                        bluetoothSocket.close()
                        _printerErrorMessage.postValue("Printer device is offline.")
                    }
                }

            }
        }else _printerErrorMessage.value = "Printer device is offline."
    }


    //--------------------- start send to begin command----------------------
    private fun beginToListenCommand(){
        val newLine:Byte = 10
        try {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO){
                    while (!stopWorker){
                        val bytesAvailable = inputStream.available()
                        if (bytesAvailable>0){
                            val packetBytes = ByteArray(bytesAvailable)
                            inputStream.read(packetBytes)
                            for (i in 0..bytesAvailable){
                                val b = packetBytes[i]
                                if (b == newLine){
                                    val encodedBytes = ByteArray(readBufferPosition)
                                    System.arraycopy(readBuffer,0,encodedBytes,0,encodedBytes.size)
                                    readBufferPosition = 0
                                }else{
                                    readBuffer[readBufferPosition++] = b
                                }
                            }
                        }
                    }
                }
            }
        }catch (e:Exception){
            _printerErrorMessage.postValue("Printer device is offline.")
        }
    }


    @Throws(Exception::class)
    fun printText(text:String, textSize:Int, isBold:Boolean, sizeIndex:Int){
        try {
            val format = byteArrayOf(27, 33, 0)
            if (isBold){
                format[2] = (textSize or arrayOfByte[sizeIndex].toInt() or 0x8).toByte()
            }else{
                format[2] = (textSize or arrayOfByte[sizeIndex].toInt()).toByte()
            }
            outPutStream.write(format)
            outPutStream.write(text.toByteArray(),0,text.toByteArray().size)
        }catch (e:Exception){
            throw Exception("Printing Device is offline")
        }
    }

    @Throws(Exception::class)
    fun textAlignment(textAlign:TextAlign){
        val textAlignFormat =  byteArrayOf(0x1B, 0x61, 0x00)
        try {
            when(textAlign){
                TextAlign.RIGHT -> textAlignFormat[2] =  0x02
                TextAlign.CENTER -> textAlignFormat[2] = 0x01
                TextAlign.LEFT -> textAlignFormat[2]  = 0x00
            }
            outPutStream.write(textAlignFormat)
        }catch (e:Exception){
            throw Exception("Printing Device is offline")
        }
    }


    @Throws(Exception::class)
    fun textUnderLine(isUnderLine:Boolean){
        val textUnderFormat = byteArrayOf(0x1B, 0x2D, 0x00)
        try {
            if(isUnderLine) textUnderFormat[2] =  0x01
            else textUnderFormat[2] =  0x00
            outPutStream.write(textUnderFormat)
        }catch (e:Exception){
            throw Exception("Printing Device is offline")
        }
    }



    @Throws(Exception::class)
    fun printImage(bitmap: Bitmap){
        try {
            val imageBytes = ImagePrintPosHelper.bitmapToBytes(bitmap)
            ImagePrintPosHelper.bytesToHexadecimalString(imageBytes)
            outPutStream.write(imageBytes)
        }catch (e:Exception){
            throw Exception("Printing Device is offline")
        }
    }

    //size range 1 to 16
    fun printQRCode(text:String, size:Int,qrCodeType:Int){
        try {
            val textBytes = text.toByteArray(charset("UTF-8"))
            val commandLength = textBytes.size + 3
            val pL = commandLength % 256
            val pH = commandLength / 256

            outPutStream.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x04, 0x00, 0x31, 0x41, qrCodeType.toByte(), 0x00))
            outPutStream.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x43, size.toByte()))
            outPutStream.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x45, 0x30))

            val qrCodeCommand = ByteArray(textBytes.size + 8)
            System.arraycopy(byteArrayOf(0x1D, 0x28, 0x6B, pL.toByte(), pH.toByte(), 0x31, 0x50, 0x30),
                0, qrCodeCommand, 0, 8)
            System.arraycopy(textBytes, 0, qrCodeCommand, 8, textBytes.size)
            outPutStream.write(qrCodeCommand)
            outPutStream.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30))
        }catch (e:Exception){
            throw Exception("Printing Device is offline")
        }
    }
}