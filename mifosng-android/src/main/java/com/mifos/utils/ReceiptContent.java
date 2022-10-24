package com.mifos.utils;

/**
 * Created by Vijayalakshmi  on 18/10/2022.
 */
public class ReceiptContent {

    public static final String LOG_TAG = ReceiptContent.class.getSimpleName();


    public static String getDepositSlip(String acc_name, String acc_no, String date, String accountOpeningDae, Double accountBalance, Integer fieldOfficerId, Double amountEntered, Double currentBalance, int receiptNumber) {
        return "[C]<b><font size='normal'>LAXMI MULTISTATE COOP STY LTD</font></b>\n" +
                "[C]<b><font size='normal'>HAVERI</font></b> \n" +
                "[C]<b><font size='normal'>-----------------------------</font></b>\n" +
                "[L]" + "AGENT No." + "    :" + "[R]" + fieldOfficerId + "\n" +
                "[L]" + "<font size='normal'>RECEIPT DATE</font>" + " :" + "[L]" + "<font size='normal'>" + date + "</font>" + "\n" +
                "[L]" + "<font size='normal'>RECEIPT No.</font>" + "  :" + "[R]" + receiptNumber + "\n" +
                "[L]" + "<font size='normal'>ACC. No.</font>" + "     :" + "[R]" + "<font size='normal'>" + acc_no + "</font>" + "\n" +
                "[L]" + "<font size='normal'>ACC. NAME</font>" + "    :" + "[R]" + "<font size='normal'> " + acc_name + "</font>" + "\n" +
                "[L]" + "<font size='normal'>PREV. BAL.</font>" + "   :" + "[R]" + "<font size='normal'>" + accountBalance + "</font>" + "\n" +
                "[L]" + "<font size='normal'>DEP. AMT.</font>" + "    :" + "[R]" + "<font size='normal'>" + amountEntered + "</font>" + "\n" +
                "[L]" + "<font size='normal'>CUR. BAL.</font>" + "    :" + "[R]" + "<font size='normal'>" + currentBalance + "</font>" + "\n" +
                "[L]" + "ACC. OPN DT." + " :" + "[R]" + "<font size='normal'>" + accountOpeningDae + "</font>" + "\n" +
                "[C]<b><font size='normal'>-----------------------------</font></b>";

    }

    public static String defaultText() {
        return "[C]<b><font size='normal'>LAXMI MULTISTATE COOP STY LTD HAVERI</font></b> \n\n" + "[L]" + "AGENT No." + "    :" + "[R]" + "" + "\n" + "[L]" + "<font size='normal'>RECEIPT DATE</font>" + " :" + "[L]" + "<font size='normal'>19/10/2022 10:00</font>" + "\n" + "[L]" + "<font size='normal'>RECEIPT No.</font>" + "  :" + "[R]" + "01" + "\n" + "[L]" + "<font size='normal'>ACCOUNT No.</font>" + "  :" + "[R]" + "<font size='normal'>000009</font>" + "\n" + "[L]" + "<font size='normal'>ACC. NAME</font>" + "    :" + "[R]" + "<font size='normal'>Name L</font>" + "\n" + "[L]" + "<font size='normal'>PREV. BAL.</font>" + "   :" + "[R]" + "<font size='normal'>1200</font>" + "\n" + "[L]" + "<font size='normal'>DEP. AMT.</font>" + "    :" + "[R]" + "<font size='normal'>1000</font>" + "\n" + "[L]" + "<font size='normal'>CUR. BAL.</font>" + "    :" + "[R]" + "<font size='normal'>2200</font>" + "\n" + "[L]" + "ACC. OPN DT." + " :" + "[R]" + "<font size='normal'>11/22/22 11:12</font>" + "\n";

    }
}
