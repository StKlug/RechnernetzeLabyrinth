package networking;

import jaxb.ErrorType;
import jaxb.MazeCom;
import jaxb.MazeComType;
import config.Settings;
import server.Player;

public class LoginThread extends Thread {

	private Connection con;
	private Player p;
	private MazeComMessageFactory mcmf;

	public LoginThread(Connection c, Player p) {
	
		this.p = p;
		this.con = c;
		this.mcmf = new MazeComMessageFactory();
	}

	public void run() {
		MazeCom loginMes = this.con.receiveMessage();
		int failCounter = 0;
		while (failCounter < Settings.LOGINTRIES) {
			// Test ob es sich um eine LoginNachricht handelt
			if (loginMes!= null && loginMes.getMcType() == MazeComType.LOGIN) {
				// sende Reply
				this.con.sendMessage(this.mcmf.createLoginReplyMessage(this.p
						.getID()),false);
				this.p.init(loginMes.getLoginMessage().getName());
				return;// verlassen des Threads
			}
			// Sende Fehler
			this.con.sendMessage(this.mcmf.createAcceptMessage(-1,
					ErrorType.AWAIT_LOGIN),true);
			failCounter++;
			// nach einem Fehler auf den nächsten Versuch warten
			loginMes = this.con.receiveMessage();
		}
		// Verlassen mit schwerem Fehlerfall
		this.con.disconnect(ErrorType.TOO_MANY_TRIES);
	}
}
