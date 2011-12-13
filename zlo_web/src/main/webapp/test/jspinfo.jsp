<%-- import attributes start here --%>
<%@ page import="java.lang.*" %>
<%@ page import="javax.servlet.*, javax.servlet.http.*" %>
<%@ page import="java.util.ArrayList, java.util.List, java.util.Date, java.util.Calendar, java.util.Vector, java.util.Properties, java.util.Enumeration" %>
<%@ page import="java.io.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.lang.reflect.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%
	String SN = n(request.getRequestURI()); // script name shortcut, including path
	String SCN = SN.substring(SN.lastIndexOf("/")+1); // just the jsp name (eg., Foo.jsp)
%>
<html>
<head>
<meta http-equiv="Content-Language" content="en-gb">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="shortcut icon" href="favicon.ico">
<META NAME="KEYWORDS" CONTENT="website hosting, uk web hosting,quality web hosting,affordable hosting,low cost web hosting,web hosting articles,website hosting tools">
<META NAME="DESCRIPTION" CONTENT="CJ Website Hosting - Quality, affordable web hosting. Offering high bandwidth and diskspace, packages start from £20 per year">
<META NAME="AUTHOR" CONTENT="CJ Website Hosting">
<META NAME="RATING" CONTENT="General">
<META name="robots" content="all">
<META name="revisit-after" content="7 days">
<link rel="stylesheet" type="text/css" media="screen, projection" href="styles/base.css">
<style>
body
{
	font-size:11px;
}
#container
{
	background-color:#F8F8F8;
	border:1px dashed #C0C0C0;
	padding:10px;
}
h2
{
	font-family: arial;
	font-size: 15px;
	color: #333333;
	margin-top:10px;
	margin-bottom:10px;
}
*html #container
{
	width:100%;
}
.e {
/* LEFT */
background-color: #999999; font-weight: bold; color: #FFFFFF;
width:280px;
text-align:right;
}
.v {
/* RIGHT */
background-color: #DDDDDD; color: #000000;
}
</style>
<title>JSP Information</title>
</head>
<body>
<div id="nav">
	<ul>
		<li><a href="http://www.cj-hosting.com/phpinfo.php">PHP Information</a></li>
		<li><a href="http://www.cj-hosting.com/cgi-bin/modules.pl">Perl Modules</a></li>
		<li id="on"><a href="http://www.cj-hosting.com/jspinfo.jsp">JSP Information</a></li>
	</ul>
</div>
<div id="wrapper">
<h1>JSP Information</h1>
<% String s=""; %>
<div id="container">
<%= javaInfo(request,response,999) %>
</div>
<p style="text-align:right;font-size:13px;"><a href="http://www.cj-hosting.com">CJ Website Hosting</a></p>
</div>
</body>
</html>
<%!

	String vBR = "<br>\n";

	public String javaInfo(HttpServletRequest request, HttpServletResponse response, int lev) {
		String out = "";
		String s = "";
		String t = "";

		out+=("<table border=\"0\" cellpadding=\"3\" width=\"100%\">");

		Properties props = System.getProperties();
        List<String> keys = new ArrayList<String>(props.size());
		for (Enumeration e = props.propertyNames(); e.hasMoreElements(); ) {
		  s = n(e.nextElement());
            keys.add(s);
		}

        Collections.sort(keys);

        for (String key : keys) {
		  if (!key.equals("")) {
			 t = n(System.getProperty(key));
			 t = (t.indexOf(";")>0 ? str_replace(java.io.File.pathSeparator,';'+vBR,t)+vBR : t);
			 t = str_replace(".jar:", ".jar:\n<br>" ,t);
			 t = str_replace(",$", ",\n<br>$", t);
			 out+=("<tr><td class='e'><b>"+ key +":</b></td><td class='v'>" + t +"</td></tr>");
		  }
        }

		out+=("</table>");
		out+=("<h2>Request Headers:</h2>");
		out+=("<table border=\"0\" cellpadding=\"3\" width=\"100%\">");
		for (Enumeration e = request.getHeaderNames(); e.hasMoreElements(); ) {
		  s = n(e.nextElement());
		  t = "";
		  if (!s.equals("")) {
			 t = n(request.getHeader(s));
			 t = (t.indexOf(";")>0 ? str_replace(java.io.File.pathSeparator,';'+vBR,t)+vBR : t);
			 out+=("<tr><td class='e'><b>"+s+":</b></td><td class='v'> " + t +"</td></tr>");
		  }
		}
		out+=("</table>");
		out+=("<h2>Request Headers:</h2>");
		out+=("<table border=\"0\" cellpadding=\"3\" width=\"100%\">");
		out+=("<tr><td class='e'><b>Method:</b></td><td class='v'> " + request.getMethod()+"</td></tr>");
		out+=("<tr><td class='e'><b>Request URI:</b></td><td class='v'> " + request.getRequestURI()+"</td></tr>");
		out+=("<tr><td class='e'><b>Protocol:</b></td><td class='v'> " + request.getProtocol()+"</td></tr>");
		out+=("<tr><td class='e'><b>Path Info:</b></td><td class='v'> " + request.getPathInfo()+"</td></tr>");
		out+=("<tr><td class='e'><b>Remote Address:</b></td><td class='v'> " + request.getRemoteAddr()+"</td></tr>");
		//out+=("getRequestedSessionId: "+n(request.getRequestedSessionId()) + vBR);
		out+=("</table>");

		return out;
	}

	public String n(Object obj) { // convert a possibly null into a "" instead
		String in = "";
		try {
			if (obj==null) {
				in = "";
			} else if (isInt(obj)) {
				in = ""+obj;
			} else if (isInteger(obj)) {
				in = obj.toString();
			} else if (hasMethod(obj,"toString()")) {
				in = obj.toString();
			} else if (obj!=null) { // else try to cast to String
				in = (String)obj;
			}
			in = ""+(in == null || in.equals("null") ? "" : in);
			return in;
		} catch (Exception e) {
			return handleException(e,"n("+obj.getClass().getName()+" \""+obj+"\")");
		}
	}


	public boolean isNull(Object obj) {
		return (obj==null);
	}

	public boolean isInt(Object obj) {
		return (obj!=null && obj.getClass().getName().indexOf("int")>=0);
	}

	public boolean isInteger(Object obj) {
		return (obj!=null && obj.getClass().getName().indexOf("Integer")>=0);
	}

	public boolean isString(Object obj) {
		return (obj!=null && obj.getClass().getName().indexOf("String")>=0);
	}

	public boolean isNullString(Object obj) {
		return (obj!=null && obj.getClass().getName().indexOf("String")>=0
			&& obj.toString().equals("")
		);
	}

	public boolean isNullOrNullString(Object obj) {
		return ( (obj==null || obj.toString().equals("")) &&
			obj.getClass().getName().indexOf("String")>=0
		);
	}

	// java.lang.reflection. cool.
	public List getMethods(Object obj) { // eg., out.write(listArrayList(getMethods(someObj))+"<p>");
		List list = new ArrayList();
		Method[] meth = obj.getClass().getMethods();
		for (int m=0;m<meth.length;m++) {
			list.add(meth[m].toString());
		}
		return list;
	}

	public boolean hasMethod(Object obj, String s) { // combine isInArrayList with getMethods...
		List list = new ArrayList();
		Method[] meth = obj.getClass().getMethods();
		for (int m=0;m<meth.length;m++) {
			if (meth[m].toString().indexOf(s)>=0) {
				return true;
			}
		}
		return false;
	}

	public String str_replace(String sep, String rep, String s) // analogue to preg_replace("pat","repl","src");
	{
		if (sep!=null && !sep.equals("") && sep.length()>0 && !sep.equals(rep)) {
			try
			{
				String outText = "";
				int pos = 0;
				while(s.length()>=1) {
					if (s.indexOf(sep)>-1){
						pos = s.indexOf(sep);
						outText += s.substring(0,pos)+rep;
						s = s.substring(pos+sep.length());
					} else {
						outText +=s;
						s="";
					}
				}
				return(outText);
			}
			catch (Exception e)
			{
				handleException(e,"str_replace()");
			}
			return("");
		} else {
			return s;
		}
	}

	public String str_replace_multi(String sep, String rep, String s) // analogue to preg_replace("pat","repl","src");, but where each char of sep is treated as a string to replace
	{
		if (sep!=null && !sep.equals("") && sep.length()>0 && !sep.equals(rep)) {
			try
			{
				String outText = "";
				int pos = 0;
				while(sep.length()>=1) {
					s = str_replace(sep.substring(0,1),rep,s);
					sep = sep.substring(1);
				}
				return s;
			}
			catch (Exception e)
			{
				handleException(e,"str_replace_multi()");
			}
			return("");
		} else {
			return s;
		}
	}

	public String handleException(Exception e, String funcname) {
	  String l = "";
	  try {
			l+=("<pre>");
			l+=("\n\t** ERROR in "+funcname+" **");
			l+=("\n\t1: "+e.toString());
			l+=("\n\t2: "+e.getMessage());
			l+=("\n\t3: "+e.getLocalizedMessage());
			l+=("\n\t** [ERROR in "+funcname+" ] **");
			l+=("</pre>");
			e.printStackTrace();
			return l;
	  } catch(Exception ex) {
		 ex.printStackTrace();
	  }
	  return l;
	 }


%>