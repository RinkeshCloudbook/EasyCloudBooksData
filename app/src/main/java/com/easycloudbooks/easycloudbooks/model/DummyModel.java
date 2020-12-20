package com.easycloudbooks.easycloudbooks.model;

public class DummyModel {
	public static final int DRAWER_ITEM_TAG_Offline = 101;
	public static final int DRAWER_ITEM_TAG_NotFound = 102;
	public static final int DRAWER_ITEM_TAG_Update = 103;
	public static final int DRAWER_ITEM_TAG_NoCompanies = 104;
	public static final int DRAWER_ITEM_TAG_Home= 1;
	public static final int DRAWER_ITEM_TAG_Chat= 2;
	public static final int DRAWER_ITEM_TAG_Search= 8;

	public static final int DRAWER_ITEM_TAG_Contact= 3;
	public static final int DRAWER_ITEM_TAG_SearchContact= 1;
	public static final int DRAWER_ITEM_TAG_CreateContact= 3;

	public static final int DRAWER_ITEM_TAG_Company= 4;
	public static final int DRAWER_ITEM_TAG_SearchCompany= 2;
	public static final int DRAWER_ITEM_TAG_CreateCompany= 3;

	public static final int DRAWER_ITEM_TAG_Project= 5;
	public static final int DRAWER_ITEM_TAG_AboutUs= 6;
	public static final int DRAWER_ITEM_TAG_Logout=7;
	public static final int DRAWER_ITEM_TAG_Notification= 8;
	
	private long mId;
	private String mImageURL;
	private String mText;
	private String name_of_item;
	private int number_of_item;
	private int mNoOfNotification;
	private int mIconRes;
	private int tag;

	public DummyModel() {
	}


	public DummyModel(long id, String imageURL, String text, int iconRes, int tag,int NoOfNotification) {
		this(id,imageURL,text,iconRes,tag,NoOfNotification,"",0);
	}

	public DummyModel(long id, String imageURL, String text, int iconRes, int tag,int NoOfNotification,
					  String name_of_item,int number_of_item) {
		mId = id;
		mImageURL = imageURL;
		mText = text;
		mNoOfNotification =  NoOfNotification;
		mIconRes = iconRes;
		this.tag = tag;
		this.name_of_item = name_of_item;
		this.number_of_item = number_of_item;
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public String getImageURL() {
		return mImageURL;
	}

	public void setImageURL(String imageURL) {
		mImageURL = imageURL;
	}

	public String getText() {
		return mText;
	}
	public String get_Name_of_item() {
		return name_of_item;
	}
	public int get_Number_of_item() {
		return number_of_item;
	}

	public int getNoOfNotification() {
		return mNoOfNotification;
	}

	public void setText(String text) {
		mText = text;
	}
	public void set_Name_of_item(String text) {
		name_of_item = text;
	}
	public void set_Number_of_item(int number) {
		number_of_item = number;
	}
	public void setNoOfNotification(int number) {
		mNoOfNotification = number;
	}

	public int getIconRes() {
		return mIconRes;
	}

	public void setIconRes(int iconRes) {
		mIconRes = iconRes;
	}

	@Override
	public String toString() {
		return mText;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

}
