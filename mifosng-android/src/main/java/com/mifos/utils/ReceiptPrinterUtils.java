package com.mifos.utils;
/**
 * Created by Vijayalakshmi  on 18/10/2022.
 */
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;

public class ReceiptPrinterUtils {

    public static void printReceipt(String text) {

        DeviceConnection printerConnection = BluetoothPrintersConnections.selectFirstPaired();
        EscPosPrinter printer = null;
        try {
            printer = new EscPosPrinter(printerConnection, 400, 42f, 30);
            printer.printFormattedText(text);

        } catch (EscPosConnectionException e) {
            e.printStackTrace();
        } catch (EscPosEncodingException e) {
            e.printStackTrace();
        } catch (EscPosBarcodeException e) {
            e.printStackTrace();
        } catch (EscPosParserException e) {
            e.printStackTrace();
        }

    }
}
