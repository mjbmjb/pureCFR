
interface IGame {
	public static int MAX_PLAYERS = 2;
	public static int MAX_ROUNDS = 2;
	public static int MAX_BOARD_CARDS=1;
	public static int MAX_HOLE_CARDS=2;
	public static int MAX_NUM_ACTIONS=10;
	public static int MAX_SUITS=4;
	public static int MAX_RANKS=13;
//	public static int MAX_LINE_LEN=READBUF_LEN;

	public static int NUM_ACTION_TYPES=3;
	public static int BIG_BLIND = 20;
	public static int SMALL_BLIND = 10;//TODO 大小盲注默认值
	public static int STACK_SIZE = 1000;
	public static int MAX_ABSTRACT_ACTIONS = 3;
	public static int[] MAX_REISE_NUM = {2,3,3,3};
	
	//mjb added
	public static int RAISE_SIZE = 20;
	
}
