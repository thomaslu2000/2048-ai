import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class utility {
	static float[] valueArray;
	static int[][] transLineArray;
    public static void initUtil(){
    	loadTransform();
    	loadValues();
    }
    
    public static void resetValues(){
    	valueArray=new float[28561];
    	transLineArray=new int[28561][4];
    	int size = 13;
		for(int a=0;a<size;a++) for(int b=0;b<size;b++) for(int c=0;c<size;c++) for(int d=0;d<size;d++){
			int[] line = {a,b,c,d};
			valueArray[intifyLine(line)]=utility.judgeLine(line,true);
			transLineArray[intifyLine(line)] = getMovedLine(line,true);
		}
		saveValues();
		saveTransform();
		System.out.println("done");
    }
    
	public static void saveTransform(){
		try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("transLineArray.ser"))) {
            os.writeObject(transLineArray);} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    public static void loadTransform(){
    	try (ObjectInputStream is = new ObjectInputStream(new FileInputStream("transLineArray.ser"))) {
            transLineArray = (int[][]) is.readObject();
            } 
    		catch (Exception e) {
				// TODO Auto-generated catch block
    			resetValues();
    			loadTransform();
				//e.printStackTrace();
			}
    }
    public static void saveValues(){
    	try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("valueArray.ser"))) {
            os.writeObject(valueArray);} 
    	catch (Exception e) {
				e.printStackTrace();
			}
    }
    public static void loadValues(){
    	try (ObjectInputStream is = new ObjectInputStream(new FileInputStream("valueArray.ser"))) {
    		valueArray = (float[]) is.readObject();} 
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    public static int[] getMovedLine(int[] numsInLine){return getMovedLine(numsInLine, false);}
    public static int[] getMovedLine(int[] numsInLine, boolean skipMemo){
    	int myName = intifyLine(numsInLine);
    	if(myName>=28561||skipMemo){
    		int[] movedLine=numsInLine.clone();
    		int stopHere=-1;
    		for(int i=2;i>=0;i--){
    			int loc = i;
    			boolean keepsGoing = true;
    			int myValue = movedLine[i];
    			if (myValue!=0) while(loc<3&&keepsGoing){
    				int nextValue = movedLine[loc+1];
    				keepsGoing = false;
    				if (nextValue==0){
    					movedLine[loc+1]=myValue;
    		    		movedLine[loc]=0;
    		    		loc+=1;
    		    		keepsGoing = true;
    		    	} else if(nextValue==myValue&&stopHere!=loc+1){
    		    		movedLine[loc+1]=nextValue+1;
    		    		movedLine[loc]=0;
    		    		stopHere=loc+1;
    		    	}
    			}
    		}
    		return movedLine;
    	}
    	return transLineArray[myName];
    }
    public static int[] massMove(int action, int[] tileArray){
    	int[][] myLineOrders = lineOrders[action];
    	for(int i=0;i<4;i++){
    		int[] line = new int[4];
    		for(int j=0;j<4;j++) line[j]=tileArray[myLineOrders[i][j]];
    		line = getMovedLine(line);
    		for(int j=0;j<4;j++) tileArray[myLineOrders[i][j]]=line[j];
    	}
    	return tileArray;
    }
    
    public static float judgeTiles(int[] t){
		float value=0;
		for(int i =0; i<13; i+=4){
			int[] line = new int[4];
			int[] otherLine = new int[4];
			for(int j=0;j<4;j++){
				line[j]=t[i+j];
				otherLine[j]=t[i/4+j*4];
			}
			value+=judgeLine(line);
			value+=judgeLine(otherLine);
		}
		int[] top = topThree(t);
		for(int i=0;i<3;i++) if (top[i]==t[i]) value*=4.5f-i/2f;
		if(top[0]>10) value*=5;
    	return value;
	}
    public static float judgeLine(int[] line){ return judgeLine(line, false);}
    public static float judgeLine(int[] line, boolean skipMemo){
    	int myInt = intifyLine(line);
    	float myScore;
    	if(myInt>=28561||skipMemo){
    		myScore=0f;
    		float[] powerLine = new float[4];
    		for(int i=0;i<4;i++) powerLine[i]=(float) Math.pow(4, line[i]);
    		boolean[] asDescending = {true,true};
    		boolean foundFirstNum = false;
    		float multiplier=1;//(float) Math.max(max(line),1);
    		int lastNum=-1;
    		int direction=0;
    		int max=-1;
    		for(int i=0;i<4;i++){
    			if(line[i]>max) max=line[i];
    			if(line[i]>0){
    				myScore+=powerLine[i];
    				//myScore+=powerLine[i]/(i+3f);
    				if(foundFirstNum){
    					int dif = line[i]-lastNum;
    					if(dif>0) asDescending[0]=false;
    					if(dif<0) asDescending[1]=false;
    					//if(Math.abs(dif)<2) myScore+=powerLine[i]/3f;
    					if(direction != 0 && direction != Integer.signum(dif)) multiplier*=0.95;//myScore-=powerLine[i]/2f;
    					direction = Integer.signum(dif);
    				} else {foundFirstNum=true;}
    				lastNum=line[i];
    			} else {
    				myScore+=550;
    			}
    		}
    		if(max==line[0]||max==line[3]) multiplier*=2f;
    		if (asDescending[0]||asDescending[1]) multiplier*=3f;
    		multiplier/=100f;
    		myScore*=multiplier;
    	} else myScore = valueArray[myInt];
    	return myScore;
    }
    
    
    
    public static int intifyLine(int[] nums){
		return 2197*nums[0]+ 169*nums[1]+ 13*nums[2]+nums[3];
	}
    public static int[][][] lineOrders = {//wrapper for entire thing
			{//wrapper for up
				{12,8,4,0},
				{13,9,5,1},
				{14,10,6,2},
				{15,11,7,3}
			},
			{//wrapper for right
				{0,1,2,3},
				{4,5,6,7},
				{8,9,10,11},
				{12,13,14,15}
			},
			{//wrapper for down
				{0,4,8,12},
				{1,5,9,13},
				{2,6,10,14},
				{3,7,11,15}
			},
			{//wrapper for left
				{3,2,1,0},
				{7,6,5,4},
				{11,10,9,8},
				{15,14,13,12}
			}
	};
	public static void printArray(int[] a){
		for(int i =0;i<a.length;i++) System.out.print(a[i]+" ");
		System.out.println();
	}
	public static String actionToString(int action){
		String[] a = {"up","right","down","left"};
		return a[action];
	}
	public static void printAction(int action){
		System.out.println((actionToString(action)));
	}
	public static int[] topThree(int[] a){
		int[] topThree = new int[3];
		for(int j=0;j<3;j++) topThree[j]=-1;
		for(int i=0;i<a.length;i++){
			int checked = a[i];
			for(int j=0;j<3;j++){
				if(checked>topThree[j]){
					int b = checked;
					checked = topThree[j];
					topThree[j]=b;
				}
			}
		}
		return topThree;
	}
	public static int getWorstMove(int[] t){
		float worst = judgeTiles(massMove(0,t.clone()));
		int worstAction = 0;
		for(int i=1;i<4;i++){
			float thisValue = judgeTiles(massMove(i,t.clone()));
			if(thisValue<worst){
				worst=thisValue;
				worstAction=i;
			}
		}
		return worstAction;
	}
	public static float valueOfNext(int[] t, int layer){ return valueOfNext(t,layer,false);}
	public static float valueOfNext(int[] t, int layer, boolean returnMove){
		if(layer==0) return judgeTiles(t);
		ArrayList<Integer> empties = new ArrayList<Integer>();
		float maxValue = -1;
		int maxAction = -1;
		int worstMove=0;
		if(layer==5 || layer == 4){
			worstMove = getWorstMove(t);
		}
		for(int i=0;i<4;i++){
			if((layer == 5 || layer == 4) && i == worstMove){ continue;}
			else {
				float currentValue = 0;
				int[] nextTiles = utility.massMove(i, t.clone());
				for(int j=0;j<16;j++) if(nextTiles[j]==0) empties.add(j);
				int numTestedSpots = Math.min(empties.size(), 4);
				if(numTestedSpots==0) currentValue = 0.9f*valueOfNext(nextTiles,layer-1);
				else{
					for(int j = 0; j<numTestedSpots; j++){
						int[] tPrime = nextTiles.clone();
						tPrime[empties.remove((int) (empties.size()*Math.random()))] = (Math.random()<0.9 ? 1 : 2);
						currentValue += valueOfNext(tPrime, layer-1);
					}
					if(nextTiles[0]==0){
						int[] tPrime = nextTiles.clone();
						tPrime[0] = 1;
						currentValue += valueOfNext(tPrime, layer-1);
						numTestedSpots++;
					}
					if(nextTiles[1]==0){
						int[] tPrime = nextTiles.clone();
						tPrime[1] = 1;
						currentValue += valueOfNext(tPrime, layer-1);
						numTestedSpots++;
					}
					currentValue /= numTestedSpots;
				}
				if(currentValue>maxValue){
					maxValue=currentValue;
					maxAction=i;
				}
			}
		}
		return returnMove? maxAction : maxValue;
	}
	public static int bestMove(int[] t){
		return (int) valueOfNext(t,5,true);
	}
}
