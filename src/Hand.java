
public class Hand {
	/* The actual cards for this hand */	
	public int[] board_cards = new int[Game.MAX_BOARD_CARDS];
	public int[][] hole_cards = new int[Game.MAX_PLAYERS][Game.MAX_HOLE_CARDS];
	//Ͷ���Ǯ��
	public int[] showdown_value = new int[2];
	public Hand() {
		// TODO Auto-generated constructor stub
	}

}
