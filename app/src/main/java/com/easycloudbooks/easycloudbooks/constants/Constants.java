package com.easycloudbooks.easycloudbooks.constants;

public interface Constants {

    public static final Boolean EMOJI_KEYBOARD = true; // false = Do not display your own Emoji keyboard | true = allow display your own Emoji keyboard

    public static final Boolean WEB_SITE_AVAILABLE = true; // false = Do not show menu items (Open in browser, Copy profile link) in profile page | true = show menu items (Open in browser, Copy profile link) in profile page

    public static final Boolean GOOGLE_PAY_TEST_BUTTON = false; // false = Do not show google pay test button in section upgrades

    public static final int MY_AD_AFTER_ITEM_NUMBER = 0;  //After first item

    public static final String APP_TEMP_FOLDER = "easycloudbooks"; //directory for temporary storage of images from the camera

    public static final int VIDEO_FILE_MAX_SIZE = 20971520; //Max size for video file in bytes | For example 5mb = 5*1024*1024

    // Google Pay settings | Settings from In-App Purchasing for Android | See documentation

    public static final String BILLING_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCA";

 public static final String WEB_SITE = "https://www.easycloudbooks.in/";  //web site url address
 public static final String GooglePath = "https://storage.googleapis.com/ecb-usercontent/";  //web site url address

    public static final String CLIENT_ID = "1";  // Client ID | For identify the application | Example: 12567

    public static final String API_DOMAIN = "https://www.easycloudbooks.in/";  // url address to which the application sends requests
    //public static final String API_User_DOMAIN = "https://ac-74.easycloudbooks.in/user/";  // url address to which the application sends requests

    public static String API_User_DOMAIN = "https://###.easycloudbooks.in/user/";

    public static final String API_FILE_EXTENSION = "";
    public static final String API_VERSION = "v2";


 public static final String AppType = "staff";
    public static final String METHOD_API_GetToken = API_DOMAIN + "api/token/getfbtoken" + API_FILE_EXTENSION;

    public static final String METHOD_ACCOUNT_SET_FCM_TOKEN = API_DOMAIN +"api/token/savefcm" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_GET_CURRENT_USER = API_User_DOMAIN +"api/user/getcurrentuser" + API_FILE_EXTENSION;



 public static final String METHOD_NOTIFICATIONS_GET = API_User_DOMAIN + "api/notification/getdata" + API_FILE_EXTENSION;
 public static final String METHOD_NOTIFICATIONS_Remove = API_User_DOMAIN + "api/notification/remove" + API_FILE_EXTENSION;
 public static final String METHOD_NOTIFICATIONS_acknowledge = API_User_DOMAIN + "api/notification/aknowledge" + API_FILE_EXTENSION;
 public static final String METHOD_PAYMENT_getorder = API_User_DOMAIN + "api/payment/getorder" + API_FILE_EXTENSION;
 public static final String METHOD_PAYMENT_GETGATEWAYS = API_User_DOMAIN + "api/payment/getgateways" + API_FILE_EXTENSION;
 public static final String METHOD_PAYMENT_GETALL = API_User_DOMAIN + "api/payment/getall" + API_FILE_EXTENSION;
 public static final String METHOD_PAYMENT_GET = API_User_DOMAIN + "api/payment/get" + API_FILE_EXTENSION;
 public static final String METHOD_PAYMENT_SAVERESPONSE = API_User_DOMAIN + "api/payment/saveresponse" + API_FILE_EXTENSION;
 public static final String METHOD_PROJECT_GET = API_User_DOMAIN + "api/projects/getappprojects" + API_FILE_EXTENSION;
 public static final String METHOD_PROJECT_GETDETAILS = API_User_DOMAIN + "api/projects/getprojectdetails" + API_FILE_EXTENSION;
 public static final String METHOD_APP_INVITE_GET = API_User_DOMAIN + "api/user/getinvitedetail" + API_FILE_EXTENSION;
 public static final String METHOD_APP_INVITE_JOIN = API_User_DOMAIN + "api/user/joininvite" + API_FILE_EXTENSION;
 public static final String METHOD_COMPANY_GET = API_User_DOMAIN + "api/company/getcompanies" + API_FILE_EXTENSION;
 public static final String METHOD_COMPANY_GETDETAIL = API_User_DOMAIN + "api/company/getdetails" + API_FILE_EXTENSION;
 public static final String METHOD_UPLOAD_COMPANY_PROFILE = API_User_DOMAIN + "api/file/processrequest" + API_FILE_EXTENSION;
 public static final String METHOD_DOCUMENT_GETDETAIL = API_User_DOMAIN + "api/document/processrequest" + API_FILE_EXTENSION;
 public static final String METHOD_DOCUMENT_SELECT_COMPANY = API_User_DOMAIN + "api/document/getcrs" + API_FILE_EXTENSION;
 public static final String METHOD_COMPANY_CONTACT_PROFILE = API_User_DOMAIN + "api/contact/getcontacts" + API_FILE_EXTENSION;
 public static final String METHOD_CONTACT_SEARCH = API_User_DOMAIN + "api/contact/loadclientdropdown" + API_FILE_EXTENSION;
 public static final String METHOD_COMPANY_SEARCH = API_User_DOMAIN + "api/company/getcrs" + API_FILE_EXTENSION;
 public static final String METHOD_COMPANY_CONTACT = API_User_DOMAIN + "api/company/getcompany" + API_FILE_EXTENSION;
 public static final String METHOD_CONTACT_PROFILE = API_User_DOMAIN + "api/contact/getcontact" + API_FILE_EXTENSION;
 public static final String METHOD_CONTACT_PROJECT_FILTER = API_User_DOMAIN + "api/contact/getcontactpopover" + API_FILE_EXTENSION;



   public static final String METHOD_APP_TERMS = "https://www.easycloudbooks.com/terms";
    public static final String METHOD_APP_PRIVACY = "https://www.easycloudbooks.com/privacy";
    //public static final String  METHOD_APP_THANKS= API_DOMAIN + "api/" + API_VERSION + "/app.thanks" + API_FILE_EXTENSION; //Later

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO = 1;                  //WRITE_EXTERNAL_STORAGE
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_COVER = 2;                  //WRITE_EXTERNAL_STORAGE
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 3;                               //ACCESS_COARSE_LOCATION
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_VIDEO_IMAGE = 4;                  //WRITE_EXTERNAL_STORAGE


    public static final int LIST_ITEMS = 20;

    public static final int POST_CHARACTERS_LIMIT = 1000;

    public static final int ENABLED = 1;
    public static final int DISABLED = 0;

    public static final int GCM_ENABLED = 1;
    public static final int GCM_DISABLED = 0;

    public static final int ADMOB_ENABLED = 1;
    public static final int ADMOB_DISABLED = 0;

    public static final int COMMENTS_ENABLED = 1;
    public static final int COMMENTS_DISABLED = 0;

    public static final int MESSAGES_ENABLED = 1;
    public static final int MESSAGES_DISABLED = 0;

    public static final int ERROR_SUCCESS = 0;

    public static final int SEX_UNKNOWN = 0;
    public static final int SEX_MALE = 1;
    public static final int SEX_FEMALE = 2;

    public static final int NOTIFY_TYPE_LIKE = 0;
    public static final int NOTIFY_TYPE_FOLLOWER = 1;
    public static final int NOTIFY_TYPE_MESSAGE = 2;
    public static final int NOTIFY_TYPE_COMMENT = 3;
    public static final int NOTIFY_TYPE_COMMENT_REPLY = 4;
    public static final int NOTIFY_TYPE_FRIEND_REQUEST_ACCEPTED = 5;
    public static final int NOTIFY_TYPE_GIFT = 6;

    public static final int NOTIFY_TYPE_IMAGE_COMMENT = 7;
    public static final int NOTIFY_TYPE_IMAGE_COMMENT_REPLY = 8;
    public static final int NOTIFY_TYPE_IMAGE_LIKE = 9;

    public static final int NOTIFY_TYPE_VIDEO_COMMENT = 10;
    public static final int NOTIFY_TYPE_VIDEO_COMMENT_REPLY = 11;
    public static final int NOTIFY_TYPE_VIDEO_LIKE = 12;

    public static final int GCM_NOTIFY_CONFIG = 0;
    public static final int GCM_NOTIFY_SYSTEM = 1;
    public static final int GCM_NOTIFY_CUSTOM = 2;
    public static final int GCM_NOTIFY_LIKE = 3;
    public static final int GCM_NOTIFY_ANSWER = 4;
    public static final int GCM_NOTIFY_QUESTION = 5;
    public static final int GCM_NOTIFY_COMMENT = 6;
    public static final int GCM_NOTIFY_FOLLOWER = 7;
    public static final int GCM_NOTIFY_PERSONAL = 8;
    public static final int GCM_NOTIFY_MESSAGE = 9;
    public static final int GCM_NOTIFY_COMMENT_REPLY = 10;
    public static final int GCM_FRIEND_REQUEST_INBOX = 11;
    public static final int GCM_FRIEND_REQUEST_ACCEPTED = 12;
    public static final int GCM_NOTIFY_GIFT = 14;
    public static final int GCM_NOTIFY_SEEN = 15;
    public static final int GCM_NOTIFY_TYPING = 16;
    public static final int GCM_NOTIFY_URL = 17;

    public static final int GCM_NOTIFY_IMAGE_COMMENT_REPLY = 18;
    public static final int GCM_NOTIFY_IMAGE_COMMENT = 19;
    public static final int GCM_NOTIFY_IMAGE_LIKE = 20;

    public static final int GCM_NOTIFY_VIDEO_COMMENT_REPLY = 21;
    public static final int GCM_NOTIFY_VIDEO_COMMENT = 22;
    public static final int GCM_NOTIFY_VIDEO_LIKE = 23;


    public static final int ERROR_LOGIN_TAKEN = 300;
    public static final int ERROR_EMAIL_TAKEN = 301;
    public static final int ERROR_FACEBOOK_ID_TAKEN = 302;

    public static final int ACCOUNT_STATE_ENABLED = 0;
    public static final int ACCOUNT_STATE_DISABLED = 1;
    public static final int ACCOUNT_STATE_BLOCKED = 2;
    public static final int ACCOUNT_STATE_DEACTIVATED = 3;

    public static final int ACCOUNT_TYPE_USER = 0;
    public static final int ACCOUNT_TYPE_GROUP = 1;

    public static final int ERROR_UNKNOWN = 100;
    public static final int ERROR_ACCESS_TOKEN = 101;

    public static final int ERROR_ACCOUNT_ID = 400;

    public static final int UPLOAD_TYPE_PHOTO = 0;
    public static final int UPLOAD_TYPE_COVER = 1;

    public static final int ACTION_NEW = 1;
    public static final int ACTION_EDIT = 2;
    public static final int SELECT_POST_IMG = 3;
    public static final int VIEW_CHAT = 4;
    public static final int CREATE_POST_IMG = 5;
    public static final int SELECT_CHAT_IMG = 6;
    public static final int CREATE_CHAT_IMG = 7;
    public static final int FEED_NEW_POST = 8;
    public static final int FRIENDS_SEARCH = 9;
    public static final int ITEM_EDIT = 10;
    public static final int STREAM_NEW_POST = 11;
    public static final int ITEM_REPOST = 12;
    public static final int CONTACT_NEW_USER = 201;
    public static final int CONTACT_EDIT_USER = 202;
    public static final int CONTACT_NEW_EMAIL = 211;
    public static final int CONTACT_EDIT_EMAIL = 212;
    public static final int CONTACT_NEW_PHONE = 221;
    public static final int CONTACT_EDIT_PHONE = 222;
    public static final int CONTACT_NEW_ADDRESS = 231;
    public static final int CONTACT_EDIT_ADDRESS = 232;

    public static final int ITEM_TYPE_IMAGE = 0;
    public static final int ITEM_TYPE_VIDEO = 1;
    public static final int ITEM_TYPE_POST = 2;
    public static final int ITEM_TYPE_COMMENT = 3;

    public static final String TAG = "TAG";

    public static final String HASHTAGS_COLOR = "#5BCFF2";


 public static final int GATEWAY_REQUEST = 500;
 public static final int GATEWAY_RESULT = 501;
 public static final int PAYMENT_DETAIL_REQUEST = 600;
 public static final int PAYMENT_DETAIL_RESULT = 601;
 public static final int PAYMENT_PAYTM_REQUEST = 1500;
 public static final int PAYMENT_PAYTM_RESULT = 1501;
 public static final int APP_UPDATE_REQUEST = 1600;
 public static final int APP_UPDATE_RESULT = 1601;
}