package Timeouts;

import java.util.TimerTask;

import jaxb.ErrorType;
import networking.Connection;

public class SendMessageTimeout extends TimerTask {

	private Connection con;

	public SendMessageTimeout(Connection con) {
		this.con = con;
	}

	@Override
	public void run() {
		this.con.disconnect(ErrorType.TIMEOUT);
	}

}
