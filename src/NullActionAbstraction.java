
public class NullActionAbstraction extends ActionAbstraction{
	@Override
	public int getActions(Game game, GameState state, ActionType[] actions) {
		int numOfAction = 0;
		boolean error = false;
		
		
		char[] actionType = {'c', 'f', 'r'};
		int[] raiseSize = new int[2];
//		for (int a = 0; a < MAX_ABSTRACT_ACTIONS; ++a) {
//			//ֻ������ c r f
//			ActionType newAction = new ActionType(actionType[a], 0);
//			if (actionType[a] == 'r' ) {
//				//��ʱraise ��ʱ�򣬼��raise���Ƿ�����Ƿ񳬳�ʣ����룩
//				if (!state.raiseIsValid(raiseSize)) {
//					break;
//				}
//				actions[a] = new ActionType(actionType[a],RAISE_SIZE);
//				numOfAction ++;
//			}
//			else if (state.isValidAction(newAction)) {
//				actions[a] = newAction;//TODO �ǲ���Ҫ��Ϊ����ֵ�� 
//				numOfAction ++;
//			}
//	
//		}
		for (char a : actionType) {
			//ֻ������ c r f
			ActionType newAction = new ActionType(a, 0);
			if (a == 'r' ) {
				//��ʱraise ��ʱ�򣬼��raise���Ƿ�����Ƿ񳬳�ʣ����룩
				if (!state.raiseIsValid(raiseSize)) {
					break;
				}
				actions[numOfAction ++] = new ActionType(a,RAISE_SIZE);
			}
			else if (state.isValidAction(game, newAction)) {
			actions[numOfAction ++] = newAction;//TODO �ǲ���Ҫ��Ϊ����ֵ�� 
			}
		}
		assert(actions[numOfAction - 1] != null);
		return numOfAction;
	}
	
	public NullActionAbstraction() {
		// TODO Auto-generated constructor stub
	}

}
