package package1;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;

import javax.swing.JOptionPane;

public class Authenticator {

	static String cID;
	static String scopes="";
	static String authCode="";
	
	
	public Authenticator(String clientID, String scope) {
		cID=clientID;
		scopes=scope;
	}
	public Authenticator(String clientID) {
		cID=clientID;
		scopes="";
	}
	
	public static void main(String args[]) {
		cID=JOptionPane.showInputDialog("Please enter ClientID!");
		scopes=JOptionPane.showInputDialog("Please input the scopes you need with a '+' between multiple scopes");
		generateAccessToken();
	}
	
	public static void generateAccessToken() {
		JOptionPane.showMessageDialog(null, "A Webpage will open on your Default Browser. Please Accept. Make Sure on the Twitch Website, your Bot's Application Redirect URI is set to http://localhost:6789");
		try {
			if (!scopes.equals(""))
			Desktop.getDesktop().browse(new URI("https://api.twitch.tv/kraken/oauth2/authorize?response_type=token&client_id="+cID+"&redirect_uri=http://localhost:6789&scope="+scopes));
			else {Desktop.getDesktop().browse(new URI("https://api.twitch.tv/kraken/oauth2/authorize?response_type=code&client_id="+cID+"&redirect_uri=http://localhost:6789&scope=user_read+user_blocks_edit+user_blocks_read+user_follows_edit+channel_read+channel_editor+channel_commercial+channel_stream+channel_subscriptions+user_subscriptions+channel_check_subscription+chat_login"));}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			waitForToken();
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
		waitForToken();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getAuthCode() {
		return authCode;
	}
	
	public static void waitForToken() throws Exception {
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
		
		out.write("HTTP/1.0 200 OK\r\n");
        out.write("Content-Type: text/html\r\n");
        out.write("\r\n");
        out.write("<TITLE>Success</TITLE>");
        out.write("<P>Please Read the URL in your browser. It will have a '#' and \"access_token=SOMETHING\" - This something is your Auth token. It might say that the access got denied - it means it got denied.</P>");
		out.flush();
		

		
		br.close();
		out.close();
		serverSock.close();
		
//		Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
//		StringSelection stringSelection = new StringSelection (code);
//		clpbrd.setContents (stringSelection, null);
		
		//JOptionPane.showMessageDialog(null, "Copied Oauth Token to your clipboard. You're done now :)");
		
//		return code;
	}
	
}
