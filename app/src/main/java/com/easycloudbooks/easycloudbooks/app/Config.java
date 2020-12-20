package com.easycloudbooks.easycloudbooks.app;

public class Config {
    public static String CloseActivity = "CLOSING_RUNNING_ACTIVITY";
    public static String Module_CallerId_Status = "CallerIdModule";
    public  static  int DIALOG_PHONE_NUMBER_CHOOSE = 30;
    public  static  String Permission_Finished = "OneTimePermissions";
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String PaymentReceived = "PR";

    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String ACID_CHANGED = "acidchanged";
    public static final String PAYMENT_CHANGED = "paymentchanged";
    public static final String UPDATE_UI = "getCurrentUser";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String ACCOUTANT_COMPANY_CHANGED = "accountantCompanyChanged";

    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    public static final int MY_PERMISSIONS_READ_PHONE_STATE = 2;
    public static final int MY_PERMISSIONS_SYSTEM_ALERT_WINDOW = 3;
    public static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 4;
    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    public static String UserImage = "https://cdn.easycloudbooks.com/ecb/www/images/user.png";
    public static String CompanyImage = "https://cdn.easycloudbooks.com/ecb/www/images/company.png";
    public static String ECBImage = "https://cdn.easycloudbooks.com/ecb/www/contents/images/thumb64/ecb.jpg";
    public static String RoleName = "Executive";
    public static boolean IsOffline = false;
    public static boolean IsOldVersion = false;

    public  static final int RESULT_OK = -1;
    public  static final int RESULT_ERROR = 1;
    public  static final int RESULT_CANCEL = 0;


}
