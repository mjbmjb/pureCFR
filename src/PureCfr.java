
public class PureCfr implements IGame {
	Game game;
	
	public PureCfr() {
		game = new Game();
	}
	
	class PureCounter {
		long iterations = 0;
		long seconds = 0;
	}
	
	public void runIteration(Patameters params, PureCfrMachine pcm) {
		boolean doPause = false;
		boolean doQuit = false;
		
		MyUtil.prl("here we start");
		// Record the time we started
		long abStartTime = System.currentTimeMillis();
		
		PureCounter initPc = new PureCounter();
		
		if (params.loadDump) {
			// Load dump
			System.out.println("loading dump");
			if (pcm.loadDump(params.loadDumpPrefix)) {
				// Failed to load dump, exit
				System.out.print("Failed to load dump.exit(PureCfr.runIteration)");
				return;
			}
		}
		
		// Get the current time
		long startTime = System.currentTimeMillis();
		
		// Counter to keep track of the last time we printed a status update */
		PureCounter lastPc = new PureCounter();
		
		/* Keep track of the last time we dumped a checkpoint */
		
		// ʡ����dump���ʱ��Ĳ��裬ֱ��һ��ѵ��
		
		// ��ʼ�������ȼ�¼һ��dump time ��ÿ��dump�����
		long dumpStartTime = System.currentTimeMillis();
		
		//count loop
		int loopCounter = 0;
		while (!doQuit) {
			loopCounter ++;
			// Get the current time
			long curTime = System.currentTimeMillis();
			
			// compute work time 
			//����Ϊcurtime���ȼ����߳�֮�����޸�
			long workTime = curTime;
			
			// pcm iterations
			pcm.doIteration(game);
			++ initPc.iterations;
			
			// Is it time to quit?
			doQuit = ((curTime - abStartTime) > params.maxWalltimgSec);
			
			if (doQuit) {
				MyUtil.prl("Stop,loops " + loopCounter + " times");
			}
			
			// Get the number of iterations completed
			long iterationsComplete = initPc.iterations;
			
			/* Get the total amount of time we've been doing work */
			
			/* Is it time to print status? */
//			if (curTime - lastPc.seconds >= params.s)
			
			/* Is it time to checkpoint? */
			if ((workTime - dumpStartTime) > params.dumpFreTime) {
				// Yes, dump a checkpoint
				
				// Record time dump started
				dumpStartTime = System.currentTimeMillis();
				
				// Build the filename
				iterationsComplete = initPc.iterations;
				String fileName = "";
				String iterationStr = Long.toString(iterationsComplete);
				String workSecStr = Long.toString(workTime - dumpStartTime);
				fileName = params.outputPrefix + 
						   ".iter-" + 
						   iterationStr +
						   ".secs-" + 
					       workSecStr;
				
				pcm.writeDump(fileName);
				System.out.println("Checkpointing files with perfix:" + fileName);
				
				// How much time was spent dumping?
				long dumEndTime = System.currentTimeMillis();
				long dumpingSec = dumEndTime - dumpStartTime;
				
				//update the next dump
				dumpStartTime = System.currentTimeMillis();
			}
		}
	}
	
	public static void main(String args[]) {
		PureCfr pc = new PureCfr();
		
		Patameters params = new Patameters();
		
		PureCfrMachine pcm = new PureCfrMachine();
		
		pc.runIteration(params, pcm);
	}
	
}
