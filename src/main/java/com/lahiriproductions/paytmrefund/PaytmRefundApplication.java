package com.lahiriproductions.paytmrefund;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paytm.pg.merchant.PaytmChecksum;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TreeMap;

@SpringBootApplication
public class PaytmRefundApplication {

    public static void main(String[] args) throws Exception {

        JSONObject paytmParams = new JSONObject();

        JSONObject body = new JSONObject();
        body.put("mid", "YtNFEJ16229131770765");
        body.put("txnType", "REFUND");
        body.put("orderId", "20220331102215585016");
        body.put("txnId", "20220331111212800110168993303580864");
        body.put("refId", "REFUNDID_" + System.currentTimeMillis());
        body.put("refundAmount", "63.00");

        /*
         * Generate checksum by parameters we have in body
         * You can get Checksum JAR from https://developer.paytm.com/docs/checksum/
         * Find your Merchant Key in your Paytm Dashboard at https://dashboard.paytm.com/next/apikeys
         */
        String checksum = PaytmChecksum.generateSignature(body.toString(), "VS38OUYA!Q9I25wO");

        JSONObject head = new JSONObject();
        head.put("signature", checksum);

        paytmParams.put("body", body);
        paytmParams.put("head", head);

        String post_data = paytmParams.toString();

        /* for Staging */
        URL url = new URL("https://securegw-stage.paytm.in/refund/apply");

        /* for Production */
// URL url = new URL("https://securegw.paytm.in/refund/apply");

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            DataOutputStream requestWriter = new DataOutputStream(connection.getOutputStream());
            requestWriter.writeBytes(post_data);
            requestWriter.close();
            String responseData = "";
            InputStream is = connection.getInputStream();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(is));
            if ((responseData = responseReader.readLine()) != null) {
                System.out.append("Response: " + responseData);
            }
            responseReader.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }


        SpringApplication.run(PaytmRefundApplication.class, args);



    }

}
