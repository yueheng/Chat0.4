import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
	
	ArrayList<Client> clients = new ArrayList<Client>();
	public static void main(String[] args) {
		new ChatServer(). start();
	}

	public void start() {
		boolean started = false;
		ServerSocket ss = null;

		try {
			ss = new ServerSocket(9999);
			started = true;
		} catch (BindException e) {
			System.out.println("Port is used...");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			while (started) {
				Socket s = ss.accept();
				Client c = new Client(s);
				clients.add(c);
				System.out.println(clients.size());
				new Thread(c).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private class Client implements Runnable {
		private Socket s = null;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean connected = false;

		public Client(Socket s) {
			this.s = s;
			connected = true;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void send(String str) {
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				clients.remove(this);
				System.out.println("A client quit. removed from list");
			}
		}

		public void run() {
			System.out.println("A client connected.");
			try {
				while (connected) {
					String str = dis.readUTF();
					System.out.println(str);
					for(int i = 0; i < clients.size(); i++) {
						Client c = clients.get(i);
						c.send(str);
					}
				}
			} catch (EOFException e) {
				System.out.println("Client Disconnect!");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (dis != null) dis.close();
					if (dos != null) dos.close();
					if (s != null) s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

}
