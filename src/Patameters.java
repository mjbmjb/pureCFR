
public class Patameters {
	// Required parameters
	public String gameFile;
	public String outputPrefix;
	
	// Optional parameters
	public boolean loadDump;
	public String loadDumpPrefix;
	public int numThreads;
	public int maxWalltimgSec;
	public int statusFreqSecond;//frequency to print status
	
	
	//mjb added
	public long dumpFreTime = 6000;//0000; // 10min
	
	public Patameters() {
		// TODO Auto-generated constructor stub
	}

}
