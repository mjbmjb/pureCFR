
public class InfoSetNode extends BettingNode {
	protected  final int solnIdx;
	protected  final int numChoices;
	protected  final int player;
	protected  final int round;
	protected  final BettingNode child;
		
	public InfoSetNode(int newSolnIdx,int newNumChoice,int  newPlayer, int newRound,
				BettingNode newChild) {
			super();
			this.solnIdx = newSolnIdx ;
		    this.numChoices = newNumChoice;
		    this.player = newPlayer;
		    this.round = newRound;
		    this.child = newChild;
		}

		public BettingNode getChild() {
			return child;
		}
		public int getSolnIdx(){
			return solnIdx;
		}
		public int getNumChoices(){
			return numChoices;
		}
		public int getPlayer(){
			return player;
		}
		public int getRound(){
			return round;
		}
		public int didPlayerFold(final int position){
			return 0;
		}
}


