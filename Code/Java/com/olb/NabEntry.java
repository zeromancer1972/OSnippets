package com.olb;

public class NabEntry {
	
	private String displayName;
	private String canonicalName;
	private String entryType;
	
	public NabEntry(String displayName, String canonicalName, String entryType) {
		this.displayName = displayName;
		this.canonicalName = canonicalName;
		this.entryType = entryType;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getCanonicalName() {
		return canonicalName;
	}

	public String getEntryType() {
		return entryType;
	}
	
	
}
