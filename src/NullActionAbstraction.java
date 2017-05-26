
public class NullActionAbstraction extends ActionAbstraction{
	@Override
	public int getActions(Game game, GameState state, ActionType[] actions) {
		int numOfAction = 0;
		boolean error = false;
		
		
		char[] actionType = {'c', 'f', 'r'};
		int[] raiseSize = new int[2];
		for (int a = 0; a < MAX_ABSTRACT_ACTIONS; ++a) {
			//ֻ������ c r f
			if (actionType[a] == 'r' ) {
				//��ʱraise ��ʱ�򣬼��raise���Ƿ�����Ƿ񳬳�ʣ����룩
				if (!state.raiseIsValid(raiseSize)) {
					break;
				}
				actions[a] = new ActionType(actionType[a],RAISE_SIZE);
				numOfAction ++;
			}
			actions[a] = new ActionType(actionType[a],0);//TODO �ǲ���Ҫ��Ϊ����ֵ�� 
			numOfAction ++;	
		}
		return numOfAction;
	}
	
	public NullActionAbstraction() {
		// TODO Auto-generated constructor stub
	}

}
