package com.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewColumn;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;
import com.ibm.commons.util.io.json.JsonJavaObject;
import com.ibm.xsp.actions.SetValueAction;

@SuppressWarnings("all")
public class REST {
	String viewName = null;

	public REST() {
	}

	public String invoke(HttpServletRequest request,
			HttpServletResponse response, String viewName) {
		this.viewName = viewName;
		return doPost(request, response);
	}

	public String doGet(HttpServletRequest request, HttpServletResponse response) {
		return doPost(request, response);
	}

	public String doPost(HttpServletRequest request,
			HttpServletResponse response) {
		String strValue = "";
		String strType = "";
		Database database = null;
		View view = null;
		ViewNavigator nav = null;
		int count = 0;
		int totalCount = 0;
		int start = 1;
		int limit = 100;
		try {
			// Get the paging parameters
			strType = request.getParameter("type");
			try {
				start = Integer.parseInt(request.getParameter("start"));
				limit = Integer.parseInt(request.getParameter("limit"));
			} catch (NumberFormatException nu) {
				
			}
			// Set the response headers
			response.setContentType("application/json");
			response.setHeader("Cache-Control", "no-cache");
			database = getCurrentSession().getCurrentDatabase();
			view = database.getView(this.viewName);
			if (view != null) {
				view.setAutoUpdate(false);
				// get the total number of entries in the view
				totalCount = view.getEntryCount();
				nav = view.createViewNav();
				nav.setEntryOptions(ViewNavigator.VN_ENTRYOPT_NOCOUNTDATA);
				// No need to buffer any more entries than we actually need
				nav.setBufferMaxEntries(limit);
				// try to skip <start> entries and return the # of entries
				// actually skipped
				int skippedEntries = nav.skip(start);
				if (skippedEntries == start) {
					// Get the View Column Names ( or titles )
					Map<Integer, String> columnNameMap = new HashMap<Integer, String>();
					for (ViewColumn col : (List<ViewColumn>) view.getColumns()) {
						if (col.getColumnValuesIndex() < 65535) {
							// We can return the Column Names or Column Items
							// columnNameMap.put(col.getColumnValuesIndex(),
							// col.getTitle());
							columnNameMap.put(col.getColumnValuesIndex(), col
									.getItemName());
						}
					}
					// This list of employee information will be turned into
					// JSON.
					List emplData = new ArrayList();
					// read the current entry after the skip operation
					ViewEntry entry = nav.getCurrent();
					while (entry != null && count <= (limit - 1)) {
						if (!entry.isCategory()) {
							count++;
							// Get a list of the column values
							List<Object> columnValues = entry.getColumnValues();
							// Create a map of column:value pairs
							HashMap<String, String> entryMap = new HashMap<String, String>();
							entryMap.put("@UNID", entry.getUniversalID());
							entryMap.put("@position", entry.getPosition('.'));
							for (Integer index : columnNameMap.keySet())
								entryMap.put(columnNameMap.get(index),
										columnValues.get(index).toString());
							// Add the entry map to the data variable that was
							// passed in
							emplData.add(entryMap);
						}
						ViewEntry tmpentry = nav.getNext(entry);
						entry.recycle();
						entry = tmpentry;
					} // while
					// Create a JSON object to wrap the employee Json array and
					// provide the root element items
					JsonJavaObject returnJSON = new JsonJavaObject();
					// set success to true
					returnJSON.put("success", true);
					// set the total number of Items
					returnJSON.put("total", totalCount);
					// set the data element to the employee JSON list.
					returnJSON.put("data", emplData);
					// Return a JSON string generated from our JsonJavaObject
					strValue = JsonGenerator.toJson(JsonJavaFactory.instanceEx,
							returnJSON);
				}
				view.recycle();
				nav.recycle();
			}
			database = null;
		} catch (NotesException e) {
			strValue = "{\"success\":false,\"message\":\"" + e.getMessage()
					+ "\"}";
			e.printStackTrace();
		} catch (Exception e) {
			strValue = "{\"success\":false,\"message\":\"" + e.getMessage()
					+ "\"}";
			e.printStackTrace();
		}
		return strValue;
	}

	public static Session getCurrentSession() {
		FacesContext context = FacesContext.getCurrentInstance();
		return (Session) context.getApplication().getVariableResolver()
				.resolveVariable(context, "session");
	}
}