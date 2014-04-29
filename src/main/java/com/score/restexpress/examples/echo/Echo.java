package com.score.restexpress.examples.echo;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.restexpress.Request;
import org.restexpress.Response;
import org.restexpress.RestExpress;
import org.restexpress.pipeline.SimpleConsoleLogMessageObserver;
import org.restexpress.serialization.GsonSerializationProvider;

/**
 * Hello world!
 * 
 */
public class Echo {
	
	private static GsonSerializationProvider jsonSerialization = new GsonSerializationProvider();

	public static void main(String[] args) {
		RestExpress.setSerializationProvider( jsonSerialization );

		RestExpress server = new RestExpress();
		server.setName("Simple Echo Server");
		server.addMessageObserver(new SimpleConsoleLogMessageObserver() );
		
		server.uri("/echo", new EchoHandler () ).method( HttpMethod.GET );
		server.uri("/html", new HtmlHandler()).method( HttpMethod.GET ).noSerialization();
		
		server.bind(9900);
		server.awaitShutdown();
	}
	
	static class EchoHandler {
		// HTTP method - Get
		public Object read(Request request, Response response)
		{
			String message = null;
			String value = request.getHeader("echo");
			System.out.println("\t >> message : "+ value );

			if ( value != null ) {
				message = "received message is '" + value + "'.";
			} else {
				message = " try again....";
			}
			return new ResponseVO( message );
		}
	}
	
	static class HtmlHandler {
		public Object read(Request request, Response response)
		{
			response.setContentType("text/html");
			StringBuffer sb = new StringBuffer();
			sb.append("<!DOCTYPE html>\n");
			sb.append("<html>\n");
			sb.append("<head>\n");
			sb.append("<script src='http://code.jquery.com/jquery-git1.js'></script>\n");
			sb.append("<title>Testpage</title>\n");
			sb.append("<script>\n");
			sb.append("$(  function () {\n");
			sb.append("var messageBox = $( '#result');\n");
			sb.append("var ajaxCallCount = 0;\n");
			sb.append("var failCount = 0;\n");
			// event bind
			sb.append("$('#clickB').click( function ( event ){\n");
			// init value 
			sb.append("var count = parseInt( $('#countBox').val() );\n");
			sb.append("ajaxCallCount = 0;\n");
			sb.append("failCount = 0;\n");
			sb.append("var startTime = $.now();\n");
			sb.append("messageBox.text('Start process :  ' + count );\n");
			// loop start
			sb.append("for( var index = 0 ; index < count ; index++) { \n");
			sb.append("$.ajax( {\n");
			sb.append("type : 'GET',\n");
			sb.append("url : 'http://local.jquery.com:9900/echo?echo='+index+'&format=json'\n");
			// fail
			sb.append("} ).fail( function ( message ) {\n");
			sb.append(" failCount++;");
			// success
			sb.append("} ).done( function ( message ) {\n");
			sb.append("console.log('..............success');\n");
			sb.append("ajaxCallCount++;\n");
			sb.append("if ( ajaxCallCount + 1 > count ){ var endTime = $.now();\n");
			// result message
			sb.append("messageBox.text( messageBox.text() + ' ---- ResponseTime : ' + ( endTime - startTime ) + 'ms. - fail : ' + failCount ); }\n");
			sb.append("} );;\n");
			sb.append("}////loop end \n");

			sb.append("var endTime = $.now();\n");
			sb.append("});\n");
			sb.append("} );\n");
			sb.append("</script>\n");
			sb.append("</head>\n");
			// html
			sb.append("<body>\n");
			sb.append("<input name='count' id='countBox' value='1000' />\n");
			sb.append("<input type='button' id='clickB' value='click'/>\n");
			sb.append("<div id='result' >\n");
			sb.append("</div>\n");
			sb.append("</body>\n");
			sb.append("</html>\n");

			return sb.toString();
		}
	}
}
