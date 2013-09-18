package com.olb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import javax.faces.context.FacesContext;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Name;
import lotus.domino.Session;
import lotus.domino.View;

public class Helper implements Serializable {

	private static final long serialVersionUID = 1L;

	public ArrayList<DbNameset> getDbList(String serverName) {
		System.out.println(serverName);
		ArrayList<DbNameset> list = new ArrayList<DbNameset>();

		if (serverName == null) {
			list.add(new DbNameset("no server", "no server"));
			return list;
		}

		try {
			Session s = getCurrentSession();
			Database catalog = s.getDatabase(serverName, "catalog.nsf");
			View v = catalog.getView("ByTitle");
			Document doc = v.getFirstDocument();

			while (doc != null) {
				if (doc.getItemValueString("Pathname").indexOf(".ntf") == -1) {
					list.add(new DbNameset(doc.getItemValueString("Pathname").replace("\\", "/"),
							doc.getItemValueString("Title")));
				}

				doc = v.getNextDocument(doc);
			}

		} catch (Exception e) {
			list.add(new DbNameset("", "Could not retrieve the catalog from "
					+ serverName));
		}

		Collections.sort(list);
		return list;
	}

	public ArrayList<NabEntry> getNabList(String serverName) {
		ArrayList<NabEntry> list = new ArrayList<NabEntry>();

		try {

			Session s = getCurrentSession();
			Database nab = s.getDatabase(serverName, "names.nsf");
			View v = nab.getView("($VIMPeopleAndGroups)");
			Document doc = v.getFirstDocument();
			String name = "";
			Name nam;

			while (doc != null) {
				name = (doc.getItemValueString("Type").equals("Person")) ? (String) doc
						.getItemValue("Fullname").elementAt(0)
						: doc.getItemValueString("Listname");
				nam = s.createName(name);

				list.add(new NabEntry(nam.getAbbreviated(), nam.getCanonical(),
						doc.getItemValueString("Type")));
				doc = v.getNextDocument(doc);
			}

		} catch (Exception e) {
			e.printStackTrace();
			list.add(new NabEntry("Could not retrieve data from " + serverName,
					"", ""));
		}

		return list;
	}

	public static Session getCurrentSession() {
		FacesContext context = FacesContext.getCurrentInstance();
		return (Session) context.getApplication().getVariableResolver()
				.resolveVariable(context, "session");
	}

}
