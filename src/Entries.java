import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

// A class for storing regret and avg strategies of variable type.
public class Entries {
	protected int numEntriesPerBucket;
	protected int totalNumEntries;
	protected double[] entries;//entries 是double类型的，考虑改为泛型
	protected boolean wasLoaded = false;
	
	public Entries(int numEntriesPerBucket, int totalNumEntries) {
		this.numEntriesPerBucket = numEntriesPerBucket;
		this.totalNumEntries = totalNumEntries;
	}
	/* Returns the sum of all pos_values in the returned pos_values array */	
	public double getPosValue(int bucket,
						   int solnIdx,
						   int numChoices,
						   double[] values) { //TODO posValue是否修改
		int baseIndex = getEntriesIndex(bucket, solnIdx);
		/* Entry 是double类型的 */
		double[] localEntries = Arrays.copyOfRange(this.entries, baseIndex, numChoices + baseIndex);
		double sumValue = 0.0;
		for(int c = 0; c < numChoices; ++c) {
			if(localEntries[c] > 0) {
				values[c] = localEntries[c];
				sumValue += localEntries[c];
			}
		}
		return sumValue;
	}

	/**
	 * 
	 * @param bucket
	 * @param solnIdx
	 * @param numChoices 可选择的chance node ?
	 * @param values
	 * @param retval
	 */
	public void updateRegret(int bucket,
						int solnIdx,
						int numChoices,
						int[] values,
						int retval) {
		int baseIndex = getEntriesIndex(bucket, solnIdx);
		for (int c = 0; c < numChoices; ++c) {
			int diff = values[c] - retval;
			double newRegret = this.entries[c + baseIndex] + diff;
			/* Only update regret if no overflow occurs （搞不懂...）*/
			if (((diff < 0) && (newRegret < this.entries[c + baseIndex])) || ((diff > 0) && (newRegret > this.entries[c + baseIndex])))
				this.entries[c + baseIndex] = newRegret;
		}
	}
	/**
	 * 
	 * @param bucket
	 * @param solnIdx
	 * @param choice 第几个action
	 * @return
	 */
	public boolean incrementEntry(int bucket, int solnIdx, int choice) {
		int baseIndex = getEntriesIndex(bucket, solnIdx);
		this.entries[baseIndex + choice] += 1;
		if (this.entries[baseIndex + choice] <= 0) {
			return true;
		}
		return false;
	}

	public int getEntriesIndex(int bucket, int solnIdx) {
		return (numEntriesPerBucket * bucket) + solnIdx;
	}
	
	public boolean load(File file) throws IOException {
		if (wasLoaded) {
			System.out.print("tried to load from file on top of loaded data at instantiation, which is not allowed\n");
			return true;
		}		 
		/* First, load the type and double-check that it matches */
		//二进制读取文件
		DataInputStream din = new DataInputStream(new FileInputStream(file));

		//用arraylist动态读取文件里的entries，回头转为double[]
		//ArrayList<Double> entriesList = new ArrayList<Double>();
		
		//index 为计数和索引
		int index = 0;
		//如果读到结尾
		while (din.available() > 0) {
			this.entries[index ++] = din.readDouble();
		}
		
		//没读完
		if (index + 1 != totalNumEntries) {
			System.out.println("error while loading ; only read" + (index + 1) + "of" + totalNumEntries + "entries");
			din.close();
			return true;
		}

		//close
		din.close();
		
		return false;
	}
	
	public void getValues(int bucket, int solnIdx, int numChoices,int[] values) {
		int baseIndex = getEntriesIndex(bucket, solnIdx);
		
	}
	
}
