/*
 * bean class for horizontal navigation elements
 */

package com.olb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class PageController implements Serializable {

	private static final long serialVersionUID = 1L;
	private static List<Page> items = null;

	public PageController() {
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void init() throws Exception {
		// a page consist of up to 3 parameters: label, url, icon class
		// omit icon class to define the link w/o an icon
		items = new ArrayList<Page>();
		items.add(new Page("Visit me!", "http://www.oliverbusse.eu", "icon-home", true, true));
		items.add(new Page("My Bookmarks", "http://mardou.dyndns.org/obpractise.nsf", "icon-bookmark", true, true));
		items.add(new Page("Subscribe", "rss.xsp", "icon-th-list"));
		items.add(new Page("Follow", "http://twitter.com/zeromancer1972", "icon-retweet", true, true));
		items.add(new Page("Disclaimer", "disclaimer.xsp", "icon-comment"));
		items.add(new Page("Mobile version", "m_index.xsp", "icon-briefcase"));
		items.add(new Page("Login", "?login&redirectto=/" + ExtLibUtil.getCurrentDatabase().getFilePath().replace("\\", "/"), "icon-lock", ExtLibUtil.getCurrentSession().getEffectiveUserName().equals(
				"Anonymous")));
		items.add(new Page("Logout", "?logout&redirectto=/" + ExtLibUtil.getCurrentDatabase().getFilePath().replace("\\", "/"), "icon-lock", !ExtLibUtil.getCurrentSession().getEffectiveUserName().equals(
				"Anonymous")));
	}

	public List<Page> getItems() {
		return items;
	}

}
