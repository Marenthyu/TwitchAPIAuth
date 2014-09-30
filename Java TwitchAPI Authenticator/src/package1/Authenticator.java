package package1;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;

import javax.swing.JOptionPane;

public class Authenticator {

	String cID, scopes="",authCode="";
	
	
	public Authenticator(String clientID, String scope) {
		cID=clientID;
		scopes=scope;
	}
	public Authenticator(String clientID) {
		cID=clientID;
		scopes="";
	}
	
	public void generateAccessToken() {
		JOptionPane.showMessageDialog(null, "A Webpage will open on your Default Browser. Please Accept. Make Sure on the Twitch Website, your Bot's Application Redirect URI is set to http://localhost:6789");
		try {
			if (!scopes.equals(""))
			Desktop.getDesktop().browse(new URI("https://api.twitch.tv/kraken/oauth2/authorize?response_type=code&client_id="+cID+"&redirect_uri=http://localhost:6789&scope="+scopes));
			else {Desktop.getDesktop().browse(new URI("https://api.twitch.tv/kraken/oauth2/authorize?response_type=code&client_id="+cID+"&redirect_uri=http://localhost:6789&scope=user_read+user_blocks_edit+user_blocks_read+user_follows_edit+channel_read+channel_editor+channel_commercial+channel_stream+channel_subscriptions+user_subscriptions+channel_check_subscription+chat_login"));}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			authCode=waitForToken();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void silentGeneration() {
		try {
			if (!scopes.equals(""))
			Desktop.getDesktop().browse(new URI("https://api.twitch.tv/kraken/oauth2/authorize?response_type=code&client_id="+cID+"&redirect_uri=http://localhost:6789&scope="+scopes));
			else {Desktop.getDesktop().browse(new URI("https://api.twitch.tv/kraken/oauth2/authorize?response_type=code&client_id="+cID+"&redirect_uri=http://localhost:6789&scope=user_read+user_blocks_edit+user_blocks_read+user_follows_edit+channel_read+channel_editor+channel_commercial+channel_stream+channel_subscriptions+user_subscriptions+channel_check_subscription+chat_login"));}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			authCode=waitForToken();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getAuthCode() {
		return authCode;
	}
	
	public String waitForToken() throws Exception {
		ServerSocket serverSock = new ServerSocket(6789);
		Socket sock = serverSock.accept();

		InputStream sis = sock.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(sis));
		String request = br.readLine(); // Now you get GET index.html HTTP/1.1`
		String[] requestParam = request.split(" ");
		String re = requestParam[1];
		
		System.out.println(re);
		String code = "";
		for (int i=7;i<re.indexOf('&');i++) code=code+re.charAt(i);
		System.out.println("Code: "+code);

		PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
		
		switch (code) {
		
		case "=access_denied" : {
			out.write("HTTP/1.0 200 OK\r\n");
	        out.write("Content-Type: text/html\r\n");
	        out.write("\r\n");
	        out.write("<TITLE>Not Successful</TITLE>");
	        out.write("<P>Authentification Denied</P>");
			out.flush();
			code="denied";
			break;
		}
		
		case "" : {
			out.write("HTTP/1.0 200 OK\r\n");
	        out.write("Content-Type: text/html\r\n");
	        out.write("\r\n");
	        out.write("<TITLE>Unsure</TITLE>");
	        out.write("<P>Already authenticated or unknown Error</P>");
	        out.flush();
	        code="unsure";
	        break;
		}
		
		default : {
			out.write("HTTP/1.0 200 OK\r\n");
	        out.write("Content-Type: text/html\r\n");
	        out.write("\r\n");
	        out.write("<TITLE>Success</TITLE>");
	        out.write("<P>Successfully authenticated</P>");
			out.flush();
		}
		
		}
		
//		if (!code.equals("=access_denied")) {
//		out.write("HTTP/1.0 200 OK\r\n");
//        out.write("Content-Type: text/html\r\n");
//        out.write("\r\n");
//        out.write("<TITLE>Success</TITLE>");
//        out.write("<P>Successfully authenticated</P>");
//		out.flush();} else {
//			out.write("HTTP/1.0 200 OK\r\n");
//	        out.write("Content-Type: text/html\r\n");
//	        out.write("\r\n");
//	        out.write("<TITLE>Not Successful</TITLE>");
//	        out.write("<P>Authentification Denied</P>");
//			out.flush();
//			code="";
//		}

		
		br.close();
		out.close();
		serverSock.close();
		return code;
	}
	
}
