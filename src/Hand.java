
public class Hand implements IGame{
	/* The actual cards for this hand */	
	public int[] board_cards = new int[MAX_BOARD_CARDS];
	public int[][] hole_cards = new int[MAX_PLAYERS][MAX_HOLE_CARDS];
	//投入的钱数
	public int[] showdownValue = new int[2];
	public Hand() {
		// TODO Auto-generated constructor stub
	}

}
