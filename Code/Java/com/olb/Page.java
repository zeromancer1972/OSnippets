package com.olb;

public class Page {

	private String label;
	private String url;
	private String icon;
	private boolean render;
	private boolean newwindow;

	public boolean getRender() {
		return render;
	}

	public String getIcon() {
		return icon;
	}

	public String getLabel() {
		return label;
	}

	public String getUrl() {
		return url;
	}

	public boolean isNewwindow() {
		return newwindow;
	}

	public Page(String label, String url) {
		this.label = label;
		this.url = url;
		this.icon = "";
		this.render = true;
	}

	public Page(String label, String url, String icon) {
		this.label = label;
		this.url = url;
		this.icon = icon;
		this.render = true;
	}


	public Page(String label, String url, String icon, boolean render) {
		this.label = label;
		this.url = url;
		this.icon = icon;
		this.render = render;
	}
	
	public Page(String label, String url, String icon, boolean render, boolean newwindow) {
		this.label = label;
		this.url = url;
		this.icon = icon;
		this.render = render;
		this.newwindow = newwindow;
	}
}
