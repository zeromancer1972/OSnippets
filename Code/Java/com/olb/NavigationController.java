/*
 * bean class for vertical navigation elements
 */

package com.olb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NavigationController implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private static List<Page> items = null;

	public NavigationController() {
		init();
	}
	
	public static void init(){
		items = new ArrayList<Page>();
		items.add(new Page("Vertical", "vertical.xsp", "icon-th-list"));
		items.add(new Page("Second page", "second_vertical.xsp", "icon-th"));
		items.add(new Page("Third page", "third_vertical.xsp", "icon-th"));
	}

	public List<Page> getItems() {
		return items;
	}

}
