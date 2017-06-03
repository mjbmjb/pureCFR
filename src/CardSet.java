import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;


class CardSetConstants{
    public static final int HANDCLASS_SINGLE_CARD = 0;
    public static final int HANDCLASS_PAIR = 1287;
    public static final int HANDCLASS_TWO_PAIR = 5005;
    public static final int HANDCLASS_TRIPS = 8606;
    public static final int HANDCLASS_STRAIGHT = 9620;
    public static final int HANDCLASS_FLUSH = 9633;
    public static final int HANDCLASS_FULL_HOUSE = 10920;
    public static final int HANDCLASS_QUADS = 11934;
    public static final int HANDCLASS_STRAIGHT_FLUSH = 12103;       
}

public class CardSet extends CardSetConstants implements IGame{
    public long cards;
    
    public int [] anySuitVal;
    public int [] oneSuitVal;
    public int [] pairOtherVal;
    public int [] pairsVal;
    public int [] topBit;
    public int [] quadsVal;
    public int [] tripsOtherVal;
    public int [] tripsVal;
    public int [] twoPairOtherVal;
    public int fullHouseOtherVal;
    
    public CardSet(){
        anySuitVal = loadData("./data/anySuitVal.txt", 8192);
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
    
    public static void main(String[] args) throws Exception {
    	;
	}
       
    public void emptyCardSet(){
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
     
    public void addCard(int suit, int rank){
    	cards |= 1 << ((suit << 4) + rank);
    }
    
    public int rankCardSet(){
    	int postponed, r;
    	int part0 = (int)(cards & 0xffffl);
    	int part1 = (int)((cards & 0xffff0000l) >> 16);
    	int part2 = (int)((cards & 0xffff00000000l) >> 32);
    	int part3 = (int)((cards & 0xffff000000000000l) >> 48);
    	postponed = oneSuitVal[part0];
    	if(postponed < oneSuitVal[part1]){
    		postponed = oneSuitVal[part1];
    	}
    	if(postponed < oneSuitVal[part2]){
    		postponed = oneSuitVal[part2];
    	}
    	if(postponed < oneSuitVal[part3]){
    		postponed = oneSuitVal[part3];
    	}
    	if(postponed >= HANDCLASS_STRAIGHT_FLUSH){
    		return postponed;
    	}
    	int s0, s1, s2, s3;
    	s0 = part0 | part1;
    	s1 = part0 & part1;
    	s2 = s1 | part2;
    	s1 |= s0 & part2;
    	s0 |= part2;
    	s3 = s2 & part3;
    	s2 |= s1 & part3;
    	s1 |= s0 & part3;
    	s0 |= part3;
    	
    	if(s3 != 0){
    		// quads
    		r = topBit[s3];
    		return quadsVal[r] + topBit[s0 ^ (1 << r)];
    	}
    	if(s2 != 0){
    		// trips or full house
    		r = topBit[s2];
    		s1 ^= (1 << r);
    		if(s1 != 0){
    			// full house
    			return tripsVal[r] + fullHouseOtherVal + topBit[s1];
    		}
    		if(postponed != 0){
    			// flush
    			return postponed;
    		}
    		postponed = anySuitVal[s0];
    		if(postponed >= HANDCLASS_STRAIGHT){
    			// straight
    			return postponed;
    		}
    		// trips
    		s0 ^= (1 << r);
    		return tripsVal[r] + tripsOtherVal[s0];
    	} else {
    		if(postponed != 0){
    			// flush
    			return postponed;
    		}
    		postponed = anySuitVal[s0];
    		if(postponed >= HANDCLASS_STRAIGHT){
    			// straight
    			return postponed;
    		}
    	}
    	if(s1 != 0){
    		// pair or two pair
    		r = topBit[s1];
    		s0 ^= (1 << r);
    		s1 ^= (1 << r);
    		if(s1 != 0){
    			// two pair
    			s0 ^= (1 << topBit[s1]);
    			return pairsVal[r] + twoPairOtherVal[topBit[s0]] + topBit[s0];
    		}
    		return pairsVal[r] + pairOtherVal[s0];
    	}
    	
    	return postponed;
    }
    

}
