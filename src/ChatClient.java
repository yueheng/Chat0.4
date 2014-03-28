import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient extends Frame{
	
	Socket s = null;
	DataOutputStream dos;
	private DataInputStream dis;
	
	TextArea ta = new TextArea();
	TextField tf = new TextField();
	
	public static void main(String[] args) {
		new ChatClient().launchFrame();
	}
	
	public void launchFrame() {
		setLocation(200, 200);
		setSize(400, 400);
		setVisible(true);
		add(ta, BorderLayout.NORTH);
		add(tf, BorderLayout.SOUTH);		
		pack();
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				disconnect();
				System.exit(0);
			}			
		});
		
		tf.addActionListener(new TextFieldListener());
		this.connect();
		new Thread(new Recieve()).start();
	}
	
	public void connect() {
		try {
			s = new Socket("127.0.0.1", 9999);
			dos = new DataOutputStream(s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
			System.out.println("Connected");
		} catch (UnknownHostException e) {
			System.out.println("UnknownHostException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException yueheng");
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			dos.close();
			dis.close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private class TextFieldListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String str = tf.getText().trim();
			tf.setText("");					
			
			try {				
				dos.writeUTF(str);
				dos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
		}
		
	}
	
	private class Recieve implements Runnable {
		public void run() {
			try {
				while(true) {
					String str = dis.readUTF();
					ta.setText(ta.getText() + str + '\n');
				}				
			} catch(SocketException e) {
				System.out.println("Exit...");
			}
			catch (IOException e) {
				e.printStackTrace();
			}	
			
		}
		
	}

}
