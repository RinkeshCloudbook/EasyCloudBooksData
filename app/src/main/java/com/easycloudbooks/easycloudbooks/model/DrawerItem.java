package com.easycloudbooks.easycloudbooks.model;

public class DrawerItem {
	/* Commented tags are expected in future updates.
	 */

	
	public DrawerItem(int icon, int title, int tag) {
		this.icon = icon;
		this.title = title;
		this.tag = tag;
	}

	private int icon;
	private int title;
    private int tag;
    private int id;

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public int getTitle() {
		return title;
	}

	public void setTitle(int title) {
		this.title = title;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}
}
