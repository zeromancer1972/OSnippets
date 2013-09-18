package com.olb;


public class DbNameset implements Comparable<DbNameset>{

	private String path;
	private String title;

	public DbNameset(String path, String title) {
		
		this.path = path;
		this.title = title;
	}

	public String getPath() {
		return path;
	}

	public String getTitle() {
		return title;
	}

	public int compareTo(DbNameset o) {
		int result = this.getPath().compareToIgnoreCase(o.getPath());
		return result;
	}

}
