package forste.ourbikes.chat.interfaces;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;


public interface ISocketOperator {
	
	public String sendHttpRequest(String params);
	public boolean sendMessage(String message, String ip, int port);
	public int startListening(int port);
	public void stopListening();
	public void exit();
	public int getListeningPort();
	public String login(String username, String password);
	public void createSocket(InetAddress byAddress, int chatServerPort) throws IOException;
	public BufferedReader getInputReader();

}
