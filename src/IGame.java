
interface IGame {
	public static int MAX_PLAYERS = 2;
	public static int MAX_ROUNDS = 1;
	public static int MAX_BOARD_CARDS=1;
	public static int MAX_HOLE_CARDS=1;
	public static int MAX_NUM_ACTIONS=10;
	public static int MAX_SUITS=1;
	public static int MAX_RANKS=3;
//	public static int MAX_LINE_LEN=READBUF_LEN;

	public static int NUM_ACTION_TYPES=3;
	public static int BIG_BLIND = 1;
	public static int SMALL_BLIND = 1;//TODO 大小盲注默认值
	public static int STACK_SIZE = 1000;
	public static int MAX_ABSTRACT_ACTIONS = 3;
	public static int[] MAX_REISE_NUM = {2,3,3,3};
	
	//mjb added
	public static int RAISE_SIZE = 20;

	// For acpc dealer
	public static int VERSION_MAJOR = 2;
	public static int VERSION_MINOR = 0;
	public static int VERSION_REVISION = 0;
}
