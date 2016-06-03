/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.project;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

class Prom {

	String dstAddress = "192.168.1.101";
	int dstPort = 6789;

	public String getDstAddress() {
		return dstAddress;
	}

	public int getDstPort() {
		return dstPort;
	}

}

public class Network implements Runnable {

	String response = "";
	int pointX, pointY;
	String name;
	static int prevPointX = 0, prevPointY = 0;
	BufferedReader in;
	Prom prom;

	public Network() {
		prom = new Prom();
	}

	public String getName() {
		synchronized (this) {
			return this.name;
		}
	}

	public int getPrevPointX() {
		synchronized (this) {
			return this.prevPointX;
		}
	}

	public void setPrevPointX(int prevPointX) {
		synchronized (this) {
			this.prevPointX = prevPointX;
		}
	}

	public int getPrevPointY() {
		synchronized (this) {
			return this.prevPointY;
		}
	}

	public void setPrevPointY(int prevPointY) {
		synchronized (this) {
			this.prevPointY = prevPointY;
		}
	}

	public int getPointX() {
		synchronized (this) {
			return this.pointX;
		}
	}

	public int getPointY() {
		synchronized (this) {
			return this.pointY;
		}
	}

	@Override
	public void run() {
		while (true) {

			if (getPointX() != getPrevPointX() && getPointY() != getPrevPointY()) {
				Socket socket = null;
				try {
					socket = new Socket(prom.getDstAddress(), prom.getDstPort());

					DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());

					outToServer.writeBytes(getName() + "!" + String.valueOf(getPointX()) + '!' + String.valueOf(getPointY()) + '\n');

				} catch (UnknownHostException e) {
					e.printStackTrace();
					response = "UnknownHostException: " + e.toString();
				} catch (IOException e) {
					e.printStackTrace();
					response = "IOException: " + e.toString();
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				setPrevPointX(getPointX());
				setPrevPointY(getPointY());
			}
		}
//			return null;
	}

}

//class GetLine {
//
//	String name;
//	BufferedReader in;
//	Prom prom;
//
//	public GetLine() {
//		prom = new Prom();
//	}
//
//	public String conn(String name) {
////			try {
////				Thread.sleep(500);
////			} catch (InterruptedException ex) {
////				Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
////			}
//		int teamMateX = 0;
//		int teamMateY = 0;
//		try {
//			// Make connection and initialize streams
//			Socket socket = new Socket(prom.getDstAddress(), prom.getDstPort());
//			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
////			while (true) {
//			String line = in.readLine();
//
//			String response = line.substring(0) + "\n";
//			String[] parts = response.split("!");
//			if (!name.equals(parts[0])) {
//				Number num = null;
//				Number num2 = null;
//				try {
//					num = NumberFormat.getInstance().parse(parts[1]);
//					num2 = NumberFormat.getInstance().parse(parts[2]);
//				} catch (ParseException ex) {
//					Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
//				}
//				int dnt = num.intValue();
//				int dnt2 = num2.intValue();
//				if (teamMateX != dnt && teamMateY != dnt2) {
//					teamMateX = dnt;
//					teamMateY = dnt2;
//				}
//
////				}
////				Log.d("FROM SERVER", msg);
//			}
//			socket.close();
//		} catch (IOException ex) {
//			Logger.getLogger(GetLine.class.getName()).log(Level.SEVERE, null, ex);
//		}
//		return String.valueOf(teamMateX) + "/" + String.valueOf(teamMateY);
//	}
//}
