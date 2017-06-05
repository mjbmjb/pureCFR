import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class PureCfrPlayer implements IGame {

	public PureCfrPlayer() {
		// TODO Auto-generated constructor stub
	}
	
	public static int readMatchState()
	

	public static void main(String args[]) {
		// Print usage
		if (args.length != 4) {
			MyUtil.prl("Usage: pure_cfr_player <player_file> <server> <port>\n");
		}
		
		/* Initialize player module and get the abstract game */
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
				
			}
		} catch (IOException e) {
			// TODO: handle exception
		}
			
		
		
	}
	
}
