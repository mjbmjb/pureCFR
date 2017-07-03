package Cfr;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import Abstraction.AbstractGame;
import MyUtil.MyUtil;
import Para.IGame;


public class PureCfrPlayer implements IGame {

	public PureCfrPlayer() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String args[]) {
		// Print usage
		if (args.length != 4) {
			MyUtil.prl("Usage: pure_cfr_player <player_file> <server> <port>\n");
		}
		
		/* Initialize player module and get the abstract game */
		File file = new File(args[0]);
		PlayerModule playerModule = new PlayerModule(file);
		AbstractGame ag = playerModule.ag;
		
		/* Connect to the dealer */
		int port;
		try {
			Socket s = new Socket(args[1], Integer.parseInt(args[2]));
			try {
				InputStream in = s.getInputStream();
				OutputStream out = s.getOutputStream();
				if (in == null || out == null) {
					MyUtil.prl("ERROR: could not get socket streams");
					return ;
				}
				//TODO
				
				PrintWriter pw = new PrintWriter(out);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				
				// Send version string to dealer
				pw.write("VERSION:" + VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_REVISION + "\n");
				pw.flush();
			
				// play the game 
				String serverStr = null;
				while ((serverStr = br.readLine()) != null) {
					if(serverStr.charAt(0) == '#' || serverStr.charAt(0) == ';') {
						continue;
					}
					
					// Read the incoming state
					GameState gs = new GameState();
					gs.readState(serverStr, ag.game);
					
					// Ignore game over message
					if (gs.finished) {
						continue;
					}
					
					// Ignore states that we are not acting in
					if (gs.currentPlayer(ag.game) != gs.viewingPlayer) {
						continue;
					}
					
					/* Start building the response to the server by adding a colon
				     * (guaranteed to fit because we read a new-line in fgets)
				     */
					StringBuilder serverSb = new StringBuilder(serverStr);
					serverSb.append(":");
					
					// Get action to the server
					ActionType action = playerModule.getAction(gs);
					
					// Send the action to the server
					gs.printAction(ag.game, action, serverSb);
					serverSb.append("\r\n");
					pw.write(serverSb.toString());
					pw.flush();
				}	
			}catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			MyUtil.prl("Socket error");
			e.printStackTrace();
		}
			
		
		
	}
	
}
