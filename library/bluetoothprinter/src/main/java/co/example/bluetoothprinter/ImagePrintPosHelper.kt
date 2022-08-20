package co.example.bluetoothprinter

import android.graphics.Bitmap
import java.lang.StringBuilder

object ImagePrintPosHelper {
    private fun initImageCommand(bytesByLine: Int, bitmapHeight: Int): ByteArray {
        val xH = bytesByLine / 256
        val xL = bytesByLine - xH * 256
        val yH = bitmapHeight / 256
        val yL = bitmapHeight - yH * 256
        val imageBytes = ByteArray(8 + bytesByLine * bitmapHeight)
        System.arraycopy(
            byteArrayOf(
                0x1D, 0x76, 0x30, 0x00, xL.toByte(),
                xH.toByte(), yL.toByte(), yH.toByte()
            ), 0, imageBytes, 0, 8
        )
        return imageBytes
    }

    /**
     * Convert Bitmap instance to a byte array compatible with ESC/POS printer.
     *
     * @param bitmap Bitmap to be convert
     * @return Bytes contain the image in ESC/POS command
     */
    fun bitmapToBytes(bitmap: Bitmap): ByteArray {
        val bitmapWidth = bitmap.width
        val bitmapHeight = bitmap.height
        val bytesByLine = Math.ceil((bitmapWidth.toFloat() / 8f).toDouble()).toInt()
        val imageBytes = initImageCommand(bytesByLine, bitmapHeight)
        var i = 8
        for (posY in 0 until bitmapHeight) {
            var j = 0
            while (j < bitmapWidth) {
                val stringBinary = StringBuilder()
                for (k in 0..7) {
                    val posX = j + k
                    if (posX < bitmapWidth) {
                        val color = bitmap.getPixel(posX, posY)
                        val r = color shr 16 and 0xff
                        val g = color shr 8 and 0xff
                        val b = color and 0xff
                        if (r > 160 && g > 160 && b > 160) {
                            stringBinary.append("0")
                        } else {
                            stringBinary.append("1")
                        }
                    } else {
                        stringBinary.append("0")
                    }
                }
                imageBytes[i++] = stringBinary.toString().toInt(2).toByte()
                j += 8
            }
        }
        return imageBytes
    }

    /**
     * Convert byte array to a hexadecimal string of the image data.
     *
     * @param bytes Bytes contain the image in ESC/POS command.
     * @return A hexadecimal string of the image data.
     */
    fun bytesToHexadecimalString(bytes: ByteArray): String {
        val imageHexString = StringBuilder()
        for (aByte in bytes) {
            var hexString = Integer.toHexString(aByte.toInt() and 255)
            if (hexString.length == 1) {
                hexString = "0$hexString"
            }
            imageHexString.append(hexString)
        }
        return imageHexString.toString()
    }
}