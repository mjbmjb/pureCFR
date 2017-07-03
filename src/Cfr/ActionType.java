package Cfr;
public class ActionType implements Cloneable {
	private char name;
	private int size;
	
	public ActionType(char nam, int siz) {
		name = nam;
		size = siz;
	}
	
	public char getType() {
		return name;
	}
	
	public int getSize() {
		return size;
	}

	public void setType(char c) {
		// TODO Auto-generated method stub
		name=c;
	}

	public void setSize(int i) {
		// TODO Auto-generated method stub
		size=i;
	}
	
	public Object clone(){
		Object object=null;
		try{
			object=(ActionType)super.clone();
		}
		catch (CloneNotSupportedException e) {
		}
		return object;
	}
}
