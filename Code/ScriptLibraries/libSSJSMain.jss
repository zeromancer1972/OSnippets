/*
this lib contains all server side script used by this app
*/

// globals
var appProfile:NotesDocument

// webdbname
function getWebDbName(){
	return @ReplaceSubstring(database.getFilePath(),"\\","/")
}



// get a cookie value server side
function getCookie(cookieName){
	var c = facesContext.getExternalContext().getRequestCookieMap().get(cookieName)
	return (c!=null)?c.getValue():""
}

// set a cookie value server side
// give the name, the value and the expires value in days
function setCookie(cookiename, cookieval, expires){
	response = facesContext.getExternalContext().getResponse(); 
	userCookie = new javax.servlet.http.Cookie(cookiename, cookieval);
	if(expires) userCookie.setMaxAge(expires*24*60*60*1000); 
	response.addCookie(userCookie); 
}


// search current view
function searchView(field){
	try {
		if(!database.isFTIndexed()){
			viewScope.globalexception = "Datenbank ist nicht volltextindiziert"
			return ""
		} else {
			viewScope.globalexception = ""
		}
	} catch(e) {
		viewScope.globalexception = "Datenbank ist nicht volltextindiziert"
		return ""
	}
	
	try{		
	var key = viewScope.searchview
	// for error tracking puposes only
	var col:NotesDocumentCollection = database.FTSearch(key)
	viewScope.globalexception = ""
	if(field!=null){
		return (key == null || key == "")? "":"[" + field + "] CONTAINS *" + key + "*"
	} else {
		return (key == null || key == "")? "":"*" + key + "*"
	}
	
} catch(e){
	viewScope.globalexception = "Suchfilter ungültig"
	return ""
}
}

// compute current page URL (xsp-name)
function getPageURL(){
	url = context.getUrl().toString()
	return @Subset(@Explode(url, "/"),-1)
}

// clear all scoped variables
function clearMap( map:Map ){
 // Get iterator for the keys
 var iterator = map.keySet().iterator();
 
 // Remove all items
 while( iterator.hasNext() ){
  map.remove( iterator.next() );
 }
}



function viewJSON(db, viewId){
	var exCon = facesContext.getExternalContext(); 
	var writer = facesContext.getResponseWriter();
	var response = exCon.getResponse();
	var db:NotesDatabase = session.getDatabase("", db)
	
	var v:NotesView

	response.setContentType("application/json; charset=ISO-8859-1");
	response.setHeader("Cache-Control", "no-cache");
	
	if(viewId == null){
		print("view not found: "+viewId)
		writer.write("{error:'view not declared'}");
	} else {
		v = db.getView(viewId)
		if(v==null){
			writer.write("{error:'view not found:'"+viewId+"}");
			writer.endDocument();
			facesContext.responseComplete();
			return true
		}
		
		var col:NotesViewEntryCollection = v.getAllEntries()
		if(col.getCount()==0){
			writer.write("{}");
			writer.endDocument();
			facesContext.responseComplete();
			return true
		}
		var ent:NotesViewEntry = col.getFirstEntry()
		
		var lineArray = new Array()
		while(ent!=null){
			var dataArray = new Array()
			try {
				for(x=0; x<ent.getColumnValues().size(); x++){
					dataArray.push('"col'+x + '":"' +ent.getColumnValues().elementAt(x)+'"')					
				}
			} catch(ex){
				writer.write("{error:'"+ex+"'}");
			}
			lineArray.push("{" + dataArray.join(", ") + "}")
			ent = col.getNextEntry(ent)
		}
		
		writer.write("["+lineArray.join(", ")+"]");
		// writer.write(lineArray.join(", "));
	}
	
	writer.endDocument();
	facesContext.responseComplete();

	// found on wissel.net
}

function getValue(vw, v, pos){		
	
	try {		
		initOffset(vw, v)
		// print(vw.getColumnCount()+ " - " + v.getColumnValues().length + " - " + v.getColumnValues().elementAt(0))
		var value = v.getColumnValues().elementAt(pos)
		var icon = '<img src="/icons/ecblank.gif" width=12"/>'
		
		if(vw.getColumn(pos+1).isIcon()){		
				
			if(isNumber(value.toString())){
				icon = (value!=null)?'<img src="/icons/vwicn'+@Right("000"+value.toString(),3)+'.gif" width="12"/>':'<img src="/icons/ecblank.gif" width="12"/>'		
				return icon
			}
			
			return ((value!=null)?value.toString():"")
		} else {
			
			// is this a date?
			if(typeof(v.getColumnValues().elementAt(pos))=="lotus.domino.local.DateTime"){
				var dt = session.createDateTime(v.getColumnValues().elementAt(pos).toString())
				return dt.getDateOnly()
			}
			// is this a @docchildren formula?
			if(v.getColumnValues().elementAt(pos).toString().indexOf("C3;")!=-1){
				return getDocChildren(v)
			}
			output = v.getColumnValues().elementAt(pos)
			return (output!=null)?output.toString():"&nbsp;"
		}	
	} catch(e){
		return ""
	}	
}

function initOffset(vw, v){
	try{
		if(sessionScope.get("offsetready")==null){
		
			if(v.isCategory() && (vw.getColumnCount() < v.getColumnValues().length)){
				sessionScope.put("offset", v.getColumnValues().length - vw.getColumnCount())
				sessionScope.put("offsetready", true)
				
			} else {
				sessionScope.put("offset", 0)
			}
		}
	} catch(e) {
	}	
}

function isNumber(n) {
  return !isNaN(parseFloat(n)) && isFinite(n);
}

function getTitle(vw, pos){
	var offset = 0
	try {	
		if(!vw.getColumn(pos+1).isCategory()){
			offset = sessionScope.get("offset")
			if(offset==null){
				offset = 0
			}
		}	
		// print(offset)
		if(vw.getColumn(pos+1).isHidden()){
			return "hidden"
		}
		return vw.getColumn(pos+1-offset).getTitle()
	} catch(e) {
		return "err"
	}
}

function getDocChildren(v){
	if(v.getChildCount()>0){
		return "!"
	} else {
		return "&nbsp;"
	}
}

function getViewTitle(){
	var viewname = sessionScope.get("currentView")
	var viewObj:NotesView = database.getView(viewname)
	try {
		return viewObj.getName()
	} catch(e){
		return "ohne Namen"
	}
}

function showColumn(vw, pos){	
	// print ("------------ column " + pos)
	try {
				
		if(vw.getColumn(pos+1).isHidden()){
			return false
		}
		return true
	} catch(e) {
		return false
	}
}

function getRSS(){
	var exCon = facesContext.getExternalContext(); 
	var writer = facesContext.getResponseWriter();
	var response = exCon.getResponse();
	
	var v:NotesView
	v = database.getView('snDate')
	var doc:NotesDocument
	
	var dbURL = facesContext.getExternalContext().getRequest().getRequestURL()
	dbURL = @LeftBack(dbURL, '/')

	response.setContentType("application/rss+xml; charset=UTF-8");
	response.setHeader("Cache-Control", "no-cache");
	
	writer.write('<?xml version="1.0" encoding="utf-8"?>');
	writer.write('<rss version="2.0">')
	writer.write('<channel>')
	writer.write('<title>'+database.getTitle()+'</title>')
	writer.write('<link>'+dbURL+'</link>')
	writer.write('<description>'+database.getTitle()+'</description>')
	writer.write('<language>en-us</language>')
	writer.write('<copyright>OSnippets</copyright>')
	writer.write('<pubDate>'+new java.util.Date().toString()+'</pubDate>')
	
	doc = v.getFirstDocument()
	
	var title = ""
	var content = ""
	var date = ""
	var url = ""
	
	while(doc!=null){
		
		if(doc.getItemValueString("Form").equals("snippet")){
			title = doc.getItemValueString("snSubject")
			content = doc.getMIMEEntity('snDesc').getContentAsText()
			date = @Replace(session.evaluate('@Text(@Modified;"Z2")', doc), ["[", "]"], ["", ""])
			url = dbURL+'/snippet.xsp?documentId='+doc.getUniversalID()
		} else {
			title = doc.getItemValueString("poSubject")
			content = @Left(doc.getMIMEEntity('poBody').getContentAsText(), 130)
			date = @Replace(session.evaluate('@Text(poDate;"Z2")', doc), ["[", "]"], ["", ""])
			url = dbURL+'/post.xsp?documentId='+doc.getUniversalID()
		}
		
		writer.write('<item>')
		writer.write('<title>'+title+'</title>')
		try {
			writer.write('<description><![CDATA['+content+']]></description>')
		} catch(e) {
			
		}
		writer.write('<link>'+url+'</link>')
		writer.write('<author>'+@Name('[CN]', doc.getItemValueString('snAuthor'))+'</author>')
		writer.write('<guid isPermaLink="false">'+doc.getUniversalID()+'</guid>')
		writer.write('<pubDate>'+date+'</pubDate>')
		writer.write('</item>')
		doc = v.getNextDocument(doc)
	}
	
	writer.write('</channel>')
	writer.write('</rss>')
	writer.endDocument();
	facesContext.responseComplete();
}

function getLocale(id){
	// uncomment this to dynamically compute the locale
	// this template is intended to be ini english only
	
	//var loc = context.getLocaleString()
	var loc = "en"
	loc = loc.substring(0, 2)+"_"
	try {
		return locales[loc+id]
	} catch(e){
		try {
			return locales["en_"+id]
		} catch(e){
			return "["+id+"]"
		}
		
		return "("+id+")"
	}
}