package Node;
import Para.IGame;


public class InfoSetNode extends BettingNode implements IGame{
	protected  int solnIdx;
	protected  int numChoices;
	protected  int player;
	protected  int round;
	protected  BettingNode child;
		
	public InfoSetNode(int newSolnIdx,
					   int newNumChoice,
					   int  newPlayer,
					   int newRound,
					   BettingNode newChild) {
			super();
			this.solnIdx = newSolnIdx ;
		    this.numChoices = newNumChoice;
		    this.player = newPlayer;
		    this.round = newRound;
		    this.child = newChild;
		}

		@Override
		public BettingNode getChild() {
			return child;
		}
		
		@Override
		public int getSolnIdx(){
			return solnIdx;
		}
		
		@Override
		public int getNumChoices(){
			return numChoices;
		}
		
		@Override
		public int getPlayer(){
			return player;
		}
		
		@Override
		public int getRound(){
			return round;
		}
		
		@Override
		public boolean didPlayerFold(final int position){
			return false;
		}
}


