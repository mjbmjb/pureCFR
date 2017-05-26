import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


class CardSetConstants{
    static final int HANDCLASS_SINGLE_CARD = 0;
    public static final int HANDCLASS_PAIR = 1287;
    public static final int HANDCLASS_TWO_PAIR = 5005;
    public static final int HANDCLASS_TRIPS = 8606;
    public static final int HANDCLASS_STRAIGHT = 9620;
    public static final int HANDCLASS_FLUSH = 9633;
    public static final int HANDCLASS_FULL_HOUSE = 10920;
    public static final int HANDCLASS_QUADS = 11934;
    public static final int HANDCLASS_STRAIGHT_FLUSH = 12103;       
}

public class CardSet extends CardSetConstants{
    public static int cards;
    public static int [] bySuit;
    
    public  int [] anyOtherVal;
    public  int [] oneSuitVal;
    public  int [] pairOtherVal;
    public  int [] pairsVal;
    public  int [] topBit;
    public  int [] quadsVal;
    public  int [] tripsOtherVal;
    public  int [] tripsVal;
    public  int [] twoPairOtherVal;
    public  int fullHouseOtherVal;
    
    public CardSet(){
        anyOtherVal = loadData("./data/anyOtherVal.txt", 8192);
    	oneSuitVal = loadData("./data/oneSuitVal.txt", 8192);
        pairOtherVal = loadData("./data/pairOtherVal.txt", 8192);  
        pairsVal = loadData("./data/pairsVal.txt", 13);
        topBit = loadData("./data/topBit.txt", 8192);
        quadsVal = loadData("./data/quadsVal.txt", 13);
        tripsOtherVal = loadData("./data/tripsOtherVal.txt", 8192);
        tripsVal = loadData("./data/tripsVal.txt", 13);
        twoPairOtherVal = loadData("./data/twoPairOtherVal.txt", 13);
        fullHouseOtherVal = HANDCLASS_FULL_HOUSE - HANDCLASS_TRIPS;
    }
    
    public static void main(String[] args) {
    	CardSet c = new CardSet();
    	int i = 0;
    	for (int a : c.oneSuitVal) {
    		System.out.println(a);
    		i++;
		}
    	System.out.println("number:" + i);
	}
       
    public static void emptyCardSet(){
    	cards = 0;
    }
    
    public static int [] loadData(String fileName, int size){
    	int [] array = new int[size];
    	File file = new File(fileName);  
        BufferedReader reader = null;  
        int number = 0;
        try {        
            reader = new BufferedReader(new FileReader(file));  
            String line = null;  
            while ((line = reader.readLine()) != null) {  
                String [] numbers = line.split(", ");
                for (String string : numbers) {
                	array[number++] = Integer.parseInt(string);
				}
            }  
            reader.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (reader != null) {  
                try {  
                    reader.close();  
                } catch (IOException e1) {  
                }  
            }  
        }
        return array;
    }
     
    public static void addCard(int suit, int rank){
    	cards |= 1 << ((suit << 4) + rank);
    }
    
    public static int rankCardSet(){
    	return 0;
    }
    
}