package com.easycloudbooks.easycloudbooks.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.crashlytics.android.Crashlytics;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.common.SweetAlertCustom;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.google.gson.JsonObject;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.easycloudbooks.easycloudbooks.constants.Constants.GATEWAY_RESULT;
import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_PAYMENT_SAVERESPONSE;
import static com.easycloudbooks.easycloudbooks.constants.Constants.PAYMENT_PAYTM_REQUEST;

public class GatewayActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    private static final String TAG = GatewayActivity.class.getSimpleName();
    SharedPreferences pref;
    private Context mContext;
    private View mView;
    private Bundle intentBundle ;
    private String Mode ;
    private String gatewaycompany;
    private String gatewaykey;
    private String gatewayupi,CheckSum;
    private String gatewaynote,gatewaydesc;
    private String gatewayimage;
    private String OrderId;
    private double amount;
    private Long ACId = 0L,PaymentId,PaymentCallId;
    private static final int TEZ_REQUEST_CODE = 123;
    private static final String GOOGLE_TEZ_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    private  ImageView gateway_logo;
    private  TextView gateway_company;
    private Boolean loading = false;
    private  JSONObject option;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_gateway);
        mContext = getApplicationContext();

        super.onCreate(savedInstanceState);
        mView = findViewById(R.id.update_parent_providers);
        gateway_logo = (ImageView)findViewById(R.id.gateway_logo);
        gateway_company= (TextView)findViewById(R.id.gateway_company);
        initpDialog();
        intentBundle =   getIntent().getExtras();
        Mode = "";
        if(intentBundle != null) {
            Mode = intentBundle.getString("mode");
            ACId = intentBundle.getLong("acid");
            amount = intentBundle.getDouble("amount");
            gatewaycompany = intentBundle.getString("gatewaycompany");
            gateway_company.setText(gatewaycompany);
            gatewayimage = intentBundle.getString("gatewayimage");
            gatewaykey = intentBundle.getString("gatewaykey");
            OrderId = intentBundle.getString("orderid");
            gatewayupi = intentBundle.getString("gatewayupi");
            gatewaynote = intentBundle.getString("gatewaynote");
            gatewaydesc = intentBundle.getString("gatewaydesc");
            PaymentId = intentBundle.getLong("gatewaypaymentid");
            PaymentCallId = intentBundle.getLong("gatewaycallid");
            CheckSum = intentBundle.getString("CheckSum");
            try {
                option = new JSONObject(intentBundle.getString("option"));
            }
            catch (JSONException ex)
            {
                option = null;
            }catch (Exception ex)
            {
                option = null;
            }
            if(gatewayimage != null) {
                if (gatewayimage != null && !TextUtils.isEmpty(gatewayimage)) {
                    Glide.with(mContext).load(gatewayimage)
                            .thumbnail(0.5f)
                            .transition(withCrossFade())
                            .apply( new RequestOptions().transform(new RoundedCorners(20)).diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(gateway_logo);
                    gateway_logo.setColorFilter(null);
                }
            }
            Log.w(TAG, "onCreate: "+Mode );
         /*   if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(GatewayActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
            }*/
            if(Mode.equals("googlepay")  ) {
                gatewayupi = "easycloud@kotak";
                gatewaycompany = "EasyCloud Consultants Pvt Ltd";
                gatewaykey = "5815";
                OrderId = "orderDPqGtii1uU32dXr6";
               // gatewaykey = "easycloud@kotak";
                gatewaynote = "test";
                String tn = String.valueOf( new Random().nextInt(9999999) + 10000000);
                amount = 5.0;
                Uri uri = new Uri.Builder()
                        .scheme("upi")
                        .authority("pay")
                        .appendQueryParameter("pa", "easycloud@kotak")
                        .appendQueryParameter("pn", "EasyCloudBooks")
                        .appendQueryParameter("mc", "5815")//"BCR2DN6T5WA3ZALM"
                        .appendQueryParameter("tr", tn)
                        .appendQueryParameter("tn", "test")
                        .appendQueryParameter("am","1.0")
                        .appendQueryParameter("cu", "INR")
                        .appendQueryParameter("url", "https://www.easycloudbooks.com")
                        .build();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                intent.setPackage(GOOGLE_TEZ_PACKAGE_NAME);
                startActivityForResult(intent, TEZ_REQUEST_CODE);
            }
            else if(Mode.equals("razorpay")) {
                startRazorpayPayment();
            }
            else if(Mode.equals("paytm")) {
                if(option != null) {
                    startPayTMPayment();
                }
                else{
                    Log.w(TAG, "onCreate: "+Mode );
                    Intent intent=new Intent();
                    intent.putExtra("Status","Error");
                    setResult(GATEWAY_RESULT,intent);
                    finish();
                }
            }
            else if(Mode.equals("payu")) {
                if(option != null) {
                    startPayUPayment();
                }
                else{
                    Log.w(TAG, "onCreate: "+Mode );
                    Intent intent=new Intent();
                    intent.putExtra("Status","Error");
                    setResult(GATEWAY_RESULT,intent);
                    finish();
                }
            }
            else{
                Log.w(TAG, "onCreate: "+Mode );
                Intent intent=new Intent();
                intent.putExtra("Status","Error");
                setResult(GATEWAY_RESULT,intent);
                finish();
            }
        }else{
            Intent intent=new Intent();
            intent.putExtra("Status","Error");
            setResult(GATEWAY_RESULT,intent);
            finish();
        }
    }

    public void startRazorpayPayment()
    {
        try {
            String ACImage = gatewayimage;
            String ACName = gatewaycompany != "" ? gatewaycompany : "EasyCloudBooks";
            Log.w("Message",ACImage);


            /**
             * Instantiate Checkout
             */
            Checkout checkout = new Checkout();
            checkout.setKeyID(gatewaykey);
            /**
             * Set your logo here
             */
            // checkout.setImage(ACbitmap);

            /**
             * Reference to current activity
             */
            final Activity activity = this;

            /**
             * Pass your payment options to the Razorpay Checkout as a JSONObject
             */

            JSONObject options = new JSONObject();


            options.put("name", ACName);
            options.put("image", ACImage);

            JSONObject notes = new JSONObject();
            notes.put("CHId", PaymentId);
            notes.put("CId", PaymentCallId);
            notes.put("ACId", ACId );
            options.put("notes", notes);


            options.put("description", gatewaydesc);
            options.put("order_id", OrderId);
            options.put("currency", "INR");


            options.put("amount", amount * 100);
            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    public void startPayTMPaymentRedirect()
    {
        try {
            Intent paytmIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putDouble("nativeSdkForMerchantAmount",  Double.parseDouble(option.getString("amount")));
            bundle.putString("orderid", option.getString("orderID"));
            bundle.putString("txnToken", option.getString("txnToken") );
            bundle.putString("mid", option.getString("mid"));

            paytmIntent.setComponent(new ComponentName("net.one97.paytm", "net.one97.paytm.AJRJarvisSplash"));
            paytmIntent.putExtra("paymentmode", 2); // You must have to pass hard coded 2 here, Else your transaction would not proceed.
            paytmIntent.putExtra("bill", bundle);
            startActivityForResult(paytmIntent, PAYMENT_PAYTM_REQUEST);
        } catch(Exception e) {
            Log.e(TAG, "Error in starting PayTM Checkout", e);
        }
    }

    public void startPayUPayment()
    {
        try {
            String ACImage = gatewayimage;
            String ACName = gatewaycompany != "" ? gatewaycompany : "EasyCloudBooks";
            PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

            //Use this to set your custom text on result screen button
            payUmoneyConfig.setDoneButtonText(ACName);

            //Use this to set your custom title for the activity
            payUmoneyConfig.setPayUmoneyActivityTitle(ACName);

            payUmoneyConfig.disableExitConfirmation(true);

            PayUmoneySdkInitializer.PaymentParam.Builder builder = new
                    PayUmoneySdkInitializer.PaymentParam.Builder();
            builder.setAmount(option.getString("amount"))                          // Payment amount
                    .setTxnId( option.getString("txnid"))                                               // Transaction ID
                    .setPhone( option.getString("phone"))                                           // User Phone number
                    .setProductName(option.getString("productInfo"))                   // Product Name or description
                    .setFirstName(option.getString("firstName"))                              // User First name
                    .setEmail(option.getString("email"))                                            // User Email ID
                    .setsUrl(option.getString("surl"))                    // Success URL (surl)
                    .setfUrl(option.getString("furl"))                     //Failure URL (furl)
                    .setUdf1(option.getString("udf1"))
                    .setUdf2(option.getString("udf2"))
                    .setUdf3(option.getString("udf3"))
                    .setUdf4("")
                    .setUdf5(option.getString("udf5"))
                    .setUdf6("")
                    .setUdf7("")
                    .setUdf8("")
                    .setUdf9("")
                    .setUdf10("")
                    .setIsDebug(false)                              // Integration environment - true (Debug)/ false(Production)
                    .setKey(option.getString("mkey"))                        // Merchant key
                    .setMerchantId(option.getString("mid"));

            PayUmoneySdkInitializer.PaymentParam paymentParam = builder.build();
//set the hash
            Log.w(TAG, "startPayUPayment: "+option.getString("hash") );
            paymentParam.setMerchantHash(option.getString("hash"));
            //paymentParam = calculateServerSideHashAndInitiatePayment1(paymentParam);

            PayUmoneyFlowManager.startPayUMoneyFlow( paymentParam,this,  R.style.AppTheme_blue,            true);
        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }


    private void launchPayUMoneyFlow() {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        //Use this to set your custom text on result screen button
        payUmoneyConfig.setDoneButtonText("EasyCloudBooks");

        //Use this to set your custom title for the activity
        payUmoneyConfig.setPayUmoneyActivityTitle("EasyCloudBooks");

        payUmoneyConfig.disableExitConfirmation(true);

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

        double amount = 1;

        String txnId = System.currentTimeMillis() + "";
        //String txnId = "TXNID720431525261327973";
        String phone = "7779015506";
        String productName ="Project";
        String firstName = "mitul";
        String email = "mitulpanch@gmail.com";
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";

        builder.setAmount(String.valueOf(amount))
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl("https://www.easycloudbooks.dev/payuresponse?ACId=74")
                .setfUrl("https://www.easycloudbooks.dev/payuresponse?ACId=74")
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(true)
                .setKey("SAKAREWF")
                .setMerchantId("6658373");

        try {
            PayUmoneySdkInitializer.PaymentParam mPaymentParams = builder.build();

            /*
             * Hash should always be generated from your server side.
             * */
            //    generateHashFromServer(mPaymentParams);

            /*            *//**
             * Do not use below code when going live
             * Below code is provided to generate hash from sdk.
             * It is recommended to generate hash from server side only.
             * */
          //  mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

           // if (AppPreference.selectedTheme != -1) {
           //     PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,this, AppPreference.selectedTheme,mAppPreference.isOverrideResultScreen());
           // } else {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,this, R.style.AppTheme_default, true);
          //  }

        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
           // payNowButton.setEnabled(true);
        }
    }




    public void startPayTMPayment()
    {
        try {
            String ACImage = gatewayimage;
            String ACName = gatewaycompany != "" ? gatewaycompany : "EasyCloudBooks";
            Log.w("Message",ACImage);
            PaytmPGService Service = PaytmPGService.getProductionService();
            HashMap<String, String> paramMap = new HashMap<String,String>();
            paramMap.put( "MID" , option.getString("MID"));
            paramMap.put( "ORDER_ID" , option.getString("ORDER_ID"));
            paramMap.put( "CUST_ID" , option.getString("CUST_ID"));
           // paramMap.put( "MOBILE_NO" , "7777777777");
           // paramMap.put( "EMAIL" , "username@emailprovider.com");
            paramMap.put( "CHANNEL_ID" , "WAP");
            paramMap.put( "TXN_AMOUNT" , option.getString("TXN_AMOUNT"));
            paramMap.put( "WEBSITE" ,  option.getString("WEBSITE"));
            paramMap.put( "INDUSTRY_TYPE_ID" , option.getString("INDUSTRY_TYPE_ID"));
           paramMap.put( "CALLBACK_URL", option.getString("CALLBACK_URL"));
            paramMap.put( "CHECKSUMHASH" , CheckSum);
           PaytmOrder Order = new PaytmOrder(paramMap);
            Service.initialize(Order,null);

            Service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {
                /*Call Backs*/
                public void someUIErrorOccurred(String inErrorMessage) {
                    Log.w(TAG, "someUIErrorOccurred: " +inErrorMessage);
                    onPayTMResponse("Client UI Error",null);
                }
                public void onTransactionResponse(Bundle inResponse) {

                    Log.w(TAG, "onTransactionResponse: "+inResponse.toString() );
                    //Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                    onPayTMResponse("Response",inResponse);
                }
                public void networkNotAvailable() {
                    Log.w(TAG, "networkNotAvailable: " );
                    onPayTMResponse("Client Network Error",null);
                }
                public void clientAuthenticationFailed(String inErrorMessage) {
                    Log.w(TAG, "clientAuthenticationFailed: " );
                    onPayTMResponse("Client Authentication Failed",null);
                }
                public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                    //Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage, Toast.LENGTH_LONG).show();
                    onPayTMResponse("Error Loading PAyTM",null);
                }
                public void onBackPressedCancelTransaction() {
                    Log.w(TAG, "onBackPressedCancelTransaction: " );

                    onPayTMResponse("Transaction Cancelled",null);
                }
                public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                    onPayTMResponse("Transaction Cancelled",null);
                    Log.w(TAG, "onTransactionCancel: "+inErrorMessage );
                    Log.w(TAG, "onTransactionCancel1: "+inResponse.toString() );
                   // Toast.makeText(getApplicationContext(), "Cancelled " + inResponse.toString(), Toast.LENGTH_LONG).show();
                }
            });
        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    public void onPayTMResponse( String response, Bundle inResponse) {
        try {
            Log.w(TAG, "onPayTMResponse: "+response);
            String Status = "Fail";
            String gateway_payment_id = "";
            String gateway_order_id = "";
            String gateway_method = "";
            boolean isValid = false;

            JSONObject options =null;
            if(inResponse != null)
            {
                String PayTMStatus = inResponse.getString("STATUS");
                if(PayTMStatus != null && PayTMStatus .equals("TXN_SUCCESS"))
                {
                    Status = "Success";
                    gateway_payment_id = inResponse.getString("TXNID");
                    gateway_order_id = inResponse.getString("BANKTXNID");
                    gateway_method = inResponse.getString("PAYMENTMODE");
                    isValid = true;
                }
                 options = new JSONObject();
                Set<String> keys = inResponse.keySet();
                for (String key : keys) {
                    try {
                        // json.put(key, bundle.get(key)); see edit below
                        options.put(key, JSONObject.wrap(inResponse.get(key)));
                    } catch(JSONException e) {
                        //Handle exception here
                    }
                }
                Log.w(TAG, "onPayTMResponse: "+options.toString() );
            }
            JSONObject cobj = new JSONObject();
            cobj.put("ACId",ACId);
            cobj.put("isValid",isValid);
            cobj.put("Message",response);
            cobj.put("PaymentId",PaymentId);
            cobj.put("PaymentCallId",PaymentCallId);
            cobj.put("gateway_payment_id",gateway_payment_id);
            cobj.put("gateway_order_id",gateway_order_id);
            cobj.put("order_id",OrderId);
            cobj.put("gateway_mode","paytm");
            cobj.put("Amount",amount);
            cobj.put("method",gateway_method);
            cobj.put("payment_order_id","");
            cobj.put("created_at","");
            cobj.put("card_id","");
            cobj.put("currency","");
            cobj.put("status","");
            cobj.put("card","");
            cobj.put("fee","");
            cobj.put("last4","");
            cobj.put("type","");
            cobj.put("get","");
            cobj.put("name","");
            cobj.put("options",options);
            SaveResponse(Status,cobj);
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }

    public void onPayUResponse( Intent data) {
        try {
            String Status = "Fail";
            String gateway_payment_id = "";
            String gateway_order_id = "";
            String gateway_method = "";
            String gateway_response = "";
            String response = "";
            JSONObject options = null;
            boolean isValid = false;
            if(data != null) {
                TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);

                Log.w(TAG, "onPayUResponse: payu");
                ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

                if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                    if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                        Status = "Success";
                        gateway_payment_id = "";
                        gateway_order_id = "";
                        gateway_method = "";
                        isValid = true;
                    } else {
                        //Failure Transaction
                    }

                    // Response from Payumoney
                    String payuResponse = transactionResponse.getPayuResponse();
                    Log.w(TAG, "onPayUResponse: " + payuResponse);
                    // Response from SURl and FURL
                    if (payuResponse != null && !payuResponse.equals("")) {

                        gateway_response = payuResponse;
                        try {
                            JSONObject robj = new JSONObject(payuResponse);
                            options = robj.getJSONObject("result");
                            if (options != null) {
                                gateway_order_id = options.getString("mihpayid");
                                gateway_payment_id = options.getString("paymentId");
                                gateway_method = options.getString("mode");
                            }
                        } catch (JSONException ex) {
                            Crashlytics.logException(ex);
                        }
                    }
                    String merchantResponse = transactionResponse.getTransactionDetails();
                    Log.w(TAG, "onPayUResponse: " + merchantResponse);
                } else if (resultModel != null && resultModel.getError() != null) {
                    Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
                } else {
                    Log.d(TAG, "Both objects are null!");
                }

                JSONObject cobj = new JSONObject();
                cobj.put("ACId",ACId);
                cobj.put("isValid",isValid);
                cobj.put("Message",response);
                cobj.put("PaymentId",PaymentId);
                cobj.put("PaymentCallId",PaymentCallId);
                cobj.put("gateway_payment_id",gateway_payment_id);
                cobj.put("fullresponse",gateway_response);
                cobj.put("gateway_order_id",gateway_order_id);
                cobj.put("order_id",OrderId);
                cobj.put("gateway_mode","payu");
                cobj.put("Amount",amount);
                cobj.put("method",gateway_method);
                cobj.put("payment_order_id","");
                cobj.put("created_at","");
                cobj.put("card_id","");
                cobj.put("currency","");
                cobj.put("status","");
                cobj.put("card","");
                cobj.put("fee","");
                cobj.put("last4","");
                cobj.put("type","");
                cobj.put("get","");
                cobj.put("name","");
                cobj.put("options",options);
                SaveResponse(Status,cobj);
            }
            else{
                gateway_response = "User Declined";

                FinishIntent(Status);
            }


        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }
    private SweetAlertCustom swc;

    protected void initpDialog() {
        if (swc == null)
            swc = new SweetAlertCustom(this);
        swc.CreatingLoadingDialog("Loading");
    }
    protected void showpDialog() {
        loading = true;
        swc.ShowLoading();
    }

    protected void hidepDialog() {
        loading = false;
        swc.HideLoading();
    }
    @Override
    public void onBackPressed() {
        //donothing
        SnakebarCustom.info(getApplicationContext(), mView, "Please Wait. Dont Press Back Button", 5000);
    }

    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID, PaymentData data) {
        try {
            Log.w(TAG, "onPaymentSuccess: "+razorpayPaymentID);
            Log.w(TAG, "onPaymentSuccess1: "+data);
            Log.w(TAG, "onPaymentSuccess2: "+data.getOrderId());
            Log.w(TAG, "onPaymentSuccess3: "+data.getPaymentId());
            Log.w(TAG, "onPaymentSuccess4: "+data.getUserContact());
            Log.w(TAG, "onPaymentSuccess5: "+data.getData());
            JSONObject cobj = new JSONObject();
            cobj.put("ACId",ACId);
            cobj.put("isValid",true);
            cobj.put("Message","");
            cobj.put("PaymentId",PaymentId);
            cobj.put("PaymentCallId",PaymentCallId);
            cobj.put("gateway_payment_id",razorpayPaymentID);
            cobj.put("gateway_order_id",data.getOrderId());
            cobj.put("order_id",OrderId);
            cobj.put("gateway_mode","razorpay");
            cobj.put("signature",data.getSignature());
            cobj.put("Amount",amount);
            cobj.put("method","");
            cobj.put("created_at","");
            cobj.put("card_id","");
            cobj.put("currency","");
            cobj.put("status","");
            cobj.put("card","");
            cobj.put("fee","");
            cobj.put("last4","");
            cobj.put("type","");
            cobj.put("get","");
            cobj.put("name","");
            SaveResponse("Success",cobj);
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.w(TAG, "onActivityResult: "+requestCode );
        Log.w(TAG, "onActivityResult: "+resultCode );
        Log.w(TAG, "onActivityResult: "+data );
        Log.w(TAG, "onActivityResult: "+PayUmoneyFlowManager.REQUEST_CODE_PAYMENT  );
        Log.w(TAG, "onActivityResult: "+RESULT_OK  );
        //Log.w("result", data.getStringExtra("Status"));
        if (requestCode == PAYMENT_PAYTM_REQUEST && data != null) {
            Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();
        }
        else if (requestCode == 123) {
            // Process based on the data in response.
            Log.d("result", data.getStringExtra("Status"));
            if(data.getStringExtra("Status").equals("FAILURE"))
                FinishIntent("Fail");
        }
        else if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK) {
            onPayUResponse(data);

        }
        else{
            FinishIntent("Fail");
        }
    }

    private  void FinishIntent(String Status)
    {
        IsLoading = false;
        Intent intent=new Intent();
        intent.putExtra("Status",Status);
        setResult(GATEWAY_RESULT,intent);
        finish();
    }
    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response, PaymentData data) {
        try {
            Log.w(TAG, "onPaymentError: "+response);
            Log.w(TAG, "onPaymentError: "+data);
            JSONObject cobj = new JSONObject();
            cobj.put("ACId",ACId);
            cobj.put("isValid",false);
            cobj.put("Message",response);
            cobj.put("PaymentId",PaymentId);
            cobj.put("PaymentCallId",PaymentCallId);
            cobj.put("order_id",OrderId);
            cobj.put("ateway_payment_id","");
            cobj.put("gateway_order_id","");
            cobj.put("gateway_mode","razorpay");
            cobj.put("Amount",amount);
            cobj.put("method","");
            cobj.put("created_at","");
            cobj.put("card_id","");
            cobj.put("currency","");
            cobj.put("status","");
            cobj.put("card","");
            cobj.put("fee","");
            cobj.put("last4","");
            cobj.put("type","");
            cobj.put("get","");
            cobj.put("name","");


            SaveResponse("Fail",cobj);
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }


        private  boolean IsLoading;
    private  CustomAuthRequest request;
    private void SaveResponse(final String Status, final JSONObject cobj) {
        showpDialog();
        request = new CustomAuthRequest(Request.Method.POST, METHOD_PAYMENT_SAVERESPONSE, null,0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w(TAG, response.toString() );
                        FinishIntent(Status);
                    }
                }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                SnakebarCustom.danger(mContext, mView, "Unable to fetch Order " , 5000);
                FinishIntent(Status);
            }
        }) {

            @Override
            protected JSONObject getParams() {
                try {
                    JSONObject params = new JSONObject();
                    params.put("cobj", cobj);
                    return params;
                } catch (JSONException ex) {
                    DataGlobal.SaveLog(TAG, ex);
                    return null;
                }
            }

            @Override
            protected void onCreateFinished(CustomAuthRequest request) {
                int socketTimeout = 300000;//0 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                request.customRequest.setRetryPolicy(policy);
                App.getInstance().addToRequestQueue(request);
            }
        };
    }

}
