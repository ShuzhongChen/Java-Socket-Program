import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MasterBot {

	public static List<Socket> ConnectionArray = Collections.synchronizedList(new ArrayList<>());
	public static Map<Socket, LocalDate> registrationDate = new HashMap<Socket, LocalDate>();
	private static int portnumber;

	public static void main(String[] args) throws IOException {
		try {
			if (args.length == 0) {
				System.out.println("Please enter command");
				return;
			} else if (args[0].equals("-p")) {
				portnumber = Integer.parseInt(args[1]);
			} else {
				System.out.println("Please enter correct command. -p PortNumber");
				return;
			}

			System.out.println("Master started, waiting for slave...");
			MyThread X = new MyThread();
			X.start();
		} catch (Exception e) {
			System.exit(-1);
		}

		// get user input
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
		String msout = "";
		while (!msout.equals("exit")) {
			System.out.print("> ");
			msout = cin.readLine();
			String[] splited = msout.split("\\s+");

			if (splited[0].equals("list")) { // command: list
				// System.out.println("connection size: " + ConnectionArray.size());
				updateSlaveList();
				if (ConnectionArray.size() != 0) {
					System.out.println("SlaveHostName    IPAddress   SourcePortNumber   RegistrationDate");
				}
				for (int i = 0; i < ConnectionArray.size(); i++) {
					Socket tempsock = ConnectionArray.get(i);
					InetAddress ipaddr = tempsock.getInetAddress();
					String host = ipaddr.getHostName();
					String ip = ipaddr.getHostAddress();
					int port = tempsock.getPort();
					System.out.print(host + "        " + ip + "   " + port + "               ");
					LocalDate temp = registrationDate.get(tempsock);
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					System.out.println(dtf.format(temp));
				}
			} else if (splited[0].equals("rise-fake-url") || splited[0].equals("down-fake-url")) {
				updateSlaveList();
				for (int i = 0; i < ConnectionArray.size(); i++) {
					Socket tempsock = ConnectionArray.get(i);
					// send message to the slave
					PrintWriter out = new PrintWriter(tempsock.getOutputStream(), true);
					out.flush();
					out.println(msout);
					//System.out.println("debug_msout:" + msout);
				}
			}
		}

		System.exit(-1);
	}

	static class MyThread extends Thread {

		public void run() {
			try {
				ServerSocket master = new ServerSocket(portnumber);
				while (true) {
					Socket slave = master.accept();
                    System.out.println("A client connected.");
					ConnectionArray.add(slave);
					LocalDate date = LocalDate.now();
					registrationDate.put(slave, date);
				}
			} catch (Exception e) {
				System.exit(-1);
			}
			// master.close();
		}

	}
	
	public static void updateSlaveList() { // if slave is no longer connected, remove it from the list
		int size = ConnectionArray.size();
		for (int i = 0; i < size; i++) {
			try {
				ConnectionArray.get(i).getOutputStream().write(1);
			} catch (IOException e) {
				ConnectionArray.remove(i);
				size--;
				i--;
			}
		}
	}
}
