import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;


public class TestChild extends Test{

	public TestChild() {
		// TODO Auto-generated constructor stub
	}

//	public static void main(String[] args) {
////		double[] a = {1.1,2.2,3.3};
////		DataOutputStream din = null;
////		try {
////			File file = new File("test.dat");
////			if (!file.exists()) {
////				file.createNewFile();
////			}
////			din = new DataOutputStream(new FileOutputStream(file));
////			for(double iter : a) {
////				din.writeDouble(iter);
////			}
////			
////		} catch (Exception e) {
////			// TODO: handle exception
////		} finally {
////			try {
////				din.close();
////			} catch (Exception e2) {
////				// TODO: handle exception
////			}
////		}
////		
////		DataInputStream dout = null;
////		double[] b = new double[3];
////		try {
////			File file = new File("test.dat");
////			if (!file.exists()) {
////				file.createNewFile();
////			}
////			dout = new DataInputStream(new FileInputStream(file));
////			for(int c = 0; c < a.length; ++ c) {
////				 b[c] = dout.readDouble();
////			}
////			
////		} catch (Exception e) {
////			// TODO: handle exception
////		} finally {
////			try {
////				dout.close();
////			} catch (Exception e2) {
////				// TODO: handle exception
////			}
////		}
////		System.out.println(b);
//		
////		int[] c = {10,20};
////		copy(c);
////		int a = 1;
//		
//		
//		
//	}
	
	public static void copy(int[] a) {
		int[] b = {2,3,4,5};
		a = Arrays.copyOfRange(b, 0, 1);
	}
}
