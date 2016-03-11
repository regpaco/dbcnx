package io.pivotal.dbcnx;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
* Sys Controller
*
* @author yjayaraman
*
*/
@Controller
public class SysController   {
	
	private String greeting = ",World";
	
	@RequestMapping("/")
	public String greet(Locale locale, Model model) {
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		model.addAttribute("greeting", greeting );
		
		return "home";
	}
	
	@RequestMapping("/test")
	public ModelAndView jndidbconnect() throws NamingException {
		System.out.println("In the method jndidbconnect");
		
		Map<String, String> results = new HashMap<String, String>();

		InitialContext ctx = null;
		Context envCtx = null;
		try {
			ctx = new InitialContext();
			envCtx = (Context) ctx.lookup("java:comp/env");
			
		} catch (NamingException e) {
			results.put("Severe Error", e.getMessage());
			return new ModelAndView("jndidbconnect", "results", results)
					.addObject("isSuccess", false);
		}
		Boolean isSuccess = Boolean.TRUE;
		NamingEnumeration<NameClassPair> e = envCtx.list("jdbc");
		NameClassPair p;
		System.out.println("22222222222" + e.hasMoreElements());
		while ( e.hasMoreElements()) {
			p = e.next();
			String testDataSourceMsg = testDataSource(DataSource.class.cast(envCtx.lookup("jdbc/" + p.getName())),
					getTestSql(p.getName()), getExpectedResult(p.getName()));
			results.put(p.getName(), testDataSourceMsg);
			if (testDataSourceMsg.startsWith("FAILURE")) {
				isSuccess = Boolean.FALSE;
			}
		}

		return new ModelAndView("jndidbconnect", "results", results)
			.addObject("isSuccess", isSuccess);
	}


	private String testDataSource(final DataSource ds, final String sql, final String expected) {
		if (ds == null || sql == null || expected == null) {
			System.out.println("FAILURE - Null test parameter(s) ds: " + ds + " sql: " + sql + " expected: " + expected);
			return "FAILURE - Null test parameter(s) ds: " + ds + " sql: " + sql + " expected: " + expected;
		}
	    Connection	conn = null;
	    java.sql.Statement stmt = null;
	    ResultSet rs = null;
	    try {
	    	conn = ds.getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(sql);
	    	if (rs.next()) {
   			if (rs.getString(1).equals(expected)) {
   				System.out.println("SUCCESS - Expected result of: " + expected + " returned by query: " + sql);
  					return "SUCCESS - Expected result of: " + expected + " returned by query: " + sql;
   			}
   			else {
   				System.out.println("FAILURE - Unexpected result of: " + expected + " returned by query: " + sql);
  					return "FAILURE - Unexpected result of: " + expected + " returned by query: " + sql;
   			}
	    	}
	    	else {
	    		System.out.println("FAILURE - No results returned by query: " + sql);
  				return "FAILURE - No results returned by query: " + sql;
	    	}
	    }
	    catch (SQLException ex) {
	    	System.out.println("FAILURE - SQLException: " + ex.getMessage() + " while executing query: " + sql);
			return "FAILURE - SQLException: " + ex.getMessage() + " while executing query: " + sql ;
	    }
	    finally {
	    	if (rs!=null) {
	    		try {
	    			rs.close();
	    		}
	    		catch(Exception ex) {
	    			System.out.println("Exception while closing ResultSet"+ ex.getMessage());
	    		}
	    	}
	    	if (stmt!=null) {
	    		try {
	    			stmt.close();
	    		}
	    		catch(Exception ex) {
	    			System.out.println("Exception while closing Statement"+ ex.getMessage());
	    		}
	    	}
	    	if (conn!=null) {
	    		try {
	    			conn.close();
	    		}
	    		catch(Exception ex) {
	    			System.out.println("Exception while closing Connection"+ ex.getMessage());
	    		}
	    	}
	    }
	}

	private String getTestSql(final String name) {
		return "SELECT 1";
	}

	private String getExpectedResult(final String name) {
		return "1";
	}

}

