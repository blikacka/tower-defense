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
	}

}