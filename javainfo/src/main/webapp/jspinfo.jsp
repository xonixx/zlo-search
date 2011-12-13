<%-- import attributes start here --%>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="javax.servlet.http.HttpServletResponse, java.lang.reflect.Method" %>
<%@ page import="java.util.*" %>
<%
    //	String SN = n(request.getRequestURI()); // script name shortcut, including path
//	String SCN = SN.substring(SN.lastIndexOf("/")+1); // just the jsp name (eg., Foo.jsp)
%>
<html>
<head>
    <style type="text/css">
        body {
            font-size: 11px;
        }

        #container {
            background-color: #F8F8F8;
            border: 1px dashed #C0C0C0;
            padding: 10px;
        }

        h2 {
            font-family: arial;
            font-size: 15px;
            color: #333333;
            margin-top: 10px;
            margin-bottom: 10px;
        }

        *html #container {
            width: 100%;
        }

        .e {
            /* LEFT */
            background-color: #999999;
            font-weight: bold;
            color: #FFFFFF;
            width: 280px;
            text-align: right;
        }

        .v {
            /* RIGHT */
            background-color: #DDDDDD;
            color: #000000;
        }
    </style>
    <title>JSP Information</title>
</head>
<body>

<div id="wrapper">
    <h1>JSP Information</h1>

    <div id="container">
        <%= javaInfo(request, response, 999) %>
    </div>
</div>
</body>
</html>
<%!
    String vBR = "<br>\n";

    public String javaInfo(HttpServletRequest request, HttpServletResponse response, int lev) {
        StringBuilder out = new StringBuilder();
        String s;
        String t;

        out.append("<table border=\"0\" cellpadding=\"3\" width=\"100%\">");

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
                t = (t.indexOf(";") > 0 ? str_replace(java.io.File.pathSeparator, ';' + vBR, t) + vBR : t);
                t = str_replace(".jar:", ".jar:\n<br>", t);
                t = str_replace(",$", ",\n<br>$", t);
                out.append("<tr><td class='e'><b>").append(key).append(":</b></td><td class='v'>").append(t).append("</td></tr>");
            }
        }

        out.append("</table>");
        out.append("<h2>Request Headers:</h2>");
        out.append("<table border=\"0\" cellpadding=\"3\" width=\"100%\">");
        for (Enumeration e = request.getHeaderNames(); e.hasMoreElements(); ) {
            s = n(e.nextElement());
            if (!s.equals("")) {
                t = n(request.getHeader(s));
                t = (t.indexOf(";") > 0 ? str_replace(java.io.File.pathSeparator, ';' + vBR, t) + vBR : t);
                out.append("<tr><td class='e'><b>").append(s).append(":</b></td><td class='v'> ").append(t).append("</td></tr>");
            }
        }
        out.append("</table>");
        out.append("<h2>Request Headers:</h2>");
        out.append("<table border=\"0\" cellpadding=\"3\" width=\"100%\">");
        out.append("<tr><td class='e'><b>Method:</b></td><td class='v'> ").append(request.getMethod()).append("</td></tr>");
        out.append("<tr><td class='e'><b>Request URI:</b></td><td class='v'> ").append(request.getRequestURI()).append("</td></tr>");
        out.append("<tr><td class='e'><b>Protocol:</b></td><td class='v'> ").append(request.getProtocol()).append("</td></tr>");
        out.append("<tr><td class='e'><b>Path Info:</b></td><td class='v'> ").append(request.getPathInfo()).append("</td></tr>");
        out.append("<tr><td class='e'><b>Remote Address:</b></td><td class='v'> ").append(request.getRemoteAddr()).append("</td></tr>");
        //out+=("getRequestedSessionId: "+n(request.getRequestedSessionId()) + vBR);
        out.append("</table>");

        // Memory
        reportMemory(out);

        return out.toString();
    }
    private void reportMemory(StringBuilder out) {
        Runtime runtime = Runtime.getRuntime();

        float mb = 1024 * 1024f;

        Map<String, Float> memory = new LinkedHashMap<String, Float>();
        final float free = runtime.freeMemory() / mb;
        final float total = runtime.totalMemory() / mb;
        final float max = runtime.maxMemory() / mb;
//        memory.put("Free", free);
//        memory.put("Total", total);
//        memory.put("Max", max);

        out.append("<h2>Memory:</h2>");
        out.append("<table border=\"0\" cellpadding=\"3\" width=\"100%\">");
        out.append("<tr><td class='e'><b>Free:</b></td><td class='v'> ").append(free).append(" M</td></tr>");
        out.append("<tr><td class='e'><b>Total:</b></td><td class='v'> ").append(total).append(" M</td></tr>");
        out.append("<tr><td class='e'><b>Max:</b></td><td class='v'> ").append(max).append(" M</td></tr>");
        //out+=("getRequestedSessionId: "+n(request.getRequestedSessionId()) + vBR);
        out.append("</table>");
    }

    public String n(Object obj) { // convert a possibly null into a "" instead
        String in = "";
        try {
            if (obj == null) {
                in = "";
            } else if (isInt(obj)) {
                in = "" + obj;
            } else if (isInteger(obj)) {
                in = obj.toString();
            } else if (hasMethod(obj, "toString()")) {
                in = obj.toString();
            } else { // else try to cast to String
                in = (String) obj;
            }
            in = "" + (in == null || in.equals("null") ? "" : in);
            return in;
        } catch (Exception e) {
            return handleException(e, "n(" + obj.getClass().getName() + " \"" + obj + "\")");
        }
    }


    public boolean isNull(Object obj) {
        return (obj == null);
    }

    public boolean isInt(Object obj) {
        return (obj != null && obj.getClass().getName().contains("int"));
    }

    public boolean isInteger(Object obj) {
        return (obj != null && obj.getClass().getName().contains("Integer"));
    }

    public boolean isString(Object obj) {
        return (obj != null && obj.getClass().getName().contains("String"));
    }

    public boolean isNullString(Object obj) {
        return (obj != null && obj.getClass().getName().contains("String")
                && obj.toString().equals("")
        );
    }

    public boolean isNullOrNullString(Object obj) {
        return ((obj == null || obj.toString().equals("")) &&
                (obj != null && obj.getClass().getName().contains("String"))
        );
    }

    // java.lang.reflection. cool.
    public List<String> getMethods(Object obj) { // eg., out.write(listArrayList(getMethods(someObj))+"<p>");
        List<String> list = new ArrayList<String>();
        Method[] meth = obj.getClass().getMethods();
        for (Method aMeth : meth) {
            list.add(aMeth.toString());
        }
        return list;
    }

    public boolean hasMethod(Object obj, String s) { // combine isInArrayList with getMethods...
        Method[] meth = obj.getClass().getMethods();
        for (Method aMeth : meth) {
            if (aMeth.toString().contains(s)) {
                return true;
            }
        }
        return false;
    }

    //TODO wtf
    public String str_replace(String sep, String rep, String s) // analogue to preg_replace("pat","repl","src");
    {
        if (sep != null && !sep.equals("") && sep.length() > 0 && !sep.equals(rep)) {
            try {
                String outText = "";
                int pos = 0;
                while (s.length() >= 1) {
                    if (s.contains(sep)) {
                        pos = s.indexOf(sep);
                        outText += s.substring(0, pos) + rep;
                        s = s.substring(pos + sep.length());
                    } else {
                        outText += s;
                        s = "";
                    }
                }
                return (outText);
            } catch (Exception e) {
                handleException(e, "str_replace()");
            }
            return ("");
        } else {
            return s;
        }
    }

/*
    public String str_replace_multi(String sep, String rep, String s) // analogue to preg_replace("pat","repl","src");, but where each char of sep is treated as a string to replace
    {
        if (sep != null && !sep.equals("") && sep.length() > 0 && !sep.equals(rep)) {
            try {
                String outText = "";
                int pos = 0;
                while (sep.length() >= 1) {
                    s = str_replace(sep.substring(0, 1), rep, s);
                    sep = sep.substring(1);
                }
                return s;
            } catch (Exception e) {
                handleException(e, "str_replace_multi()");
            }
            return ("");
        } else {
            return s;
        }
    }
*/

    public String handleException(Exception e, String funcname) {
        String l = "";
        try {
            l += ("<pre>");
            l += ("\n\t** ERROR in " + funcname + " **");
            l += ("\n\t1: " + e.toString());
            l += ("\n\t2: " + e.getMessage());
            l += ("\n\t3: " + e.getLocalizedMessage());
            l += ("\n\t** [ERROR in " + funcname + " ] **");
            l += ("</pre>");
            e.printStackTrace();
            return l;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return l;
    }


%>