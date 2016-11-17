package farkle;

import java.io.*;
import java.util.*;

public class FarklePlayer implements Runnable {
    Scanner in; // for reading input
    PrintStream out; // for printing output
    int numTurns;  // number of Farkle turns to play
    String line;  // for storing a line read from in
    int[] dice;  // keep track of the dice values

    public FarklePlayer(Scanner in, PrintStream out) {
        this.in = in;
        this.out = out;
        dice = new int[6];
        numTurns = 1000;
    }

    public void run() {
    	// This specifies how many turns to play.
    	// Change to 1000 for final version.
    	out.printf("%d\n", numTurns);
    	
        line = in.nextLine();
        int turn = 1;
        int turnScore = 0;
        int usedDiceCount = 0;
        double[][] rewardTable = initRtable();
        while (! line.equals("quit")) {
        	Scanner readline = new Scanner(line);
        	for (int i = 0; i < 6; i++) {
        		if (readline.hasNextInt()) {
        			dice[i] = readline.nextInt();     
        		} else {
        			readline.next();  // skip the X
        			dice[i] = -1;
        		}
        	}
        	readline.close();
        	
        	// Set aside 1s and 5s
        	// You will need to figure out how to set aside 3 of a kinds
        	String setAside = "";
        	ArrayList<Integer> usedDice = new ArrayList<Integer>();
        	setAside = findThreeOfAKind(dice, usedDice);
        	usedDiceCount += usedDice.size();
        	for (int i = 0; i < 6; i++) {
        		if (dice[i] == 1 || dice[i] == 5) {
        			// dice are numbered from 1 to 6 so avoid off by one errors
        			int d = i + 1;
        			if(!usedDice.contains(d)){		//check if dice was used by a 3 of a kind
        				setAside += d + " ";
        				usedDiceCount++;
        			}
        		}
        	}
        	out.print(setAside);
        	
        	// Now need to decide whether to bank or not.
        	// This program always decides to bank.
        	boolean banked = bankDecision(usedDiceCount, turnScore, rewardTable); //true;
        	
        	// Finish printing
        	if (banked){
        		out.print("bank");
        		usedDiceCount = 0;	//re-set the counter for used dice
        	}
        	out.print("\n");
        	
        	// Handle feedback including farkles
        	line = in.nextLine();
        	readline = new Scanner(line);
        	readline.next();  // skip 'score'
        	turnScore = readline.nextInt();  // this is the score so far for this turn
        	String status = readline.next();
        	readline.close();
        	
        	if (status.equals("banked")) {
        		turn++;
        		turnScore = 0;
        		usedDiceCount = 0;	//re-set the counter for used dice
        	} else if (status.equals("farkled")) {
        		turn++;
        		turnScore = 0;
        		usedDiceCount = 0;	//re-set the counter for used dice
        	} else if (status.equals("continue")) {
        		// nothing to do here
        	}
        	
        	// get the next dice values
        	line = in.nextLine();
        }

        out.print("quit\n");
        in.close();
        out.close();
    }
    
    
/*
 * Returns the dice numbers that are 3 of a kind in the dice array.
 * The method creates a 2D ArrayList to keep a count of the times each dice
 * is found. The count is incremented, and the index + 1 of the dice number
 * is saved. Finally, if the count is 3 or more, only the first 3 indexes are
 * returned. 
 * 
 * */    
    public String findThreeOfAKind(int[] dice, ArrayList<Integer> usedDice){
    	String diceNums = "";
    	ArrayList<ArrayList<Integer>> numCountTable = new ArrayList<ArrayList<Integer>>();
    	for(int i = 0; i <= 6; i++){		//Initialize table to 0's
    		numCountTable.add(new ArrayList<Integer>());
    		numCountTable.get(i).add(0);
    	}
    	for(int j = 0; j < dice.length; j++){
    		int dieNum = dice[j];
    		if(dieNum != -1){		// a "used" die is set to -1 in the array
    			numCountTable.get(dieNum).set(0, numCountTable.get(dieNum).get(0) + 1);		//increment count for dice number
        		numCountTable.get(dieNum).add(j+1);		//save index of die number in the dice array (from 1 to 6)
    		}
    	}
    	for(int k = 1; k < numCountTable.size(); k++){	//start at index 1
    		if(numCountTable.get(k).get(0) >= 3){		//check if dice num count >= 3
    			for(int l = 1; l < 4; l++){		//dice numbers start at index 1
    				diceNums += numCountTable.get(k).get(l) + " ";		//get only the 1st 3 indexes
    				usedDice.add(numCountTable.get(k).get(l));
    			}
    		}
    	}
    	return diceNums;
    }
    

    
/*
 * Decides whether to bank the score or not
 * 
 * */
    private boolean bankDecision(int usedDiceCount, int turnScore, double[][] rewardTable){
    	double expectedReward = 0.0;
    	int diceToRoll = 6 - usedDiceCount;
    	if(turnScore == 0){
    		return true;
    	}else{ 
    		double negRewards = -1 * rewardTable[diceToRoll][1] * turnScore;
    	//	expectedReward = negRewards + rewardTable[diceToRoll][0];
    	//	expectedReward = (rewardTable[diceToRoll][2] * negRewards) 		//[P(farkle) * NegRewards] + [P(score) * Pos Reward]
    	//			+ (rewardTable[diceToRoll][3] * rewardTable[diceToRoll][0]);
    		double utilityScore = 0.0, utilityFarkle = 0.0;
    		utilityScore = rewardTable[diceToRoll][3] * rewardTable[diceToRoll][0];
    		utilityFarkle = rewardTable[diceToRoll][2] * negRewards;
    		if(utilityScore > utilityFarkle){
    			return true;
    		}else{
    			return false;
    		}
    	//	if(expectedReward < turnScore) return true;
    	}
    //	return false;
    }
    
    
/*
 * Initializes the sum of positive rewards and farkle count table. 
 * The array's row index maps to the number of dice to be rolled,
 * and columns 0 thru 3 correspond to the sum of positive rewards,
 * farkle count, P(farkle) and P(scoring) respectively.
 * 
 * */    
    private double[][] initRtable(){
    	double[][] table = new double[7][4];
    	table[0][0] = 0.0;
    	table[0][1] = 0.0;
    	table[0][2] = 0.0;
    	table[0][3] = 0.0;
    	
    	table[1][0] = 150.0;
    	table[1][1] = 4.0;
    	table[1][2] = 0.6667;
    	table[1][3] = 0.3333;
    	
    	table[2][0] = 1800.0;
    	table[2][1] = 16.0;
    	table[2][2] = 0.4348;
    	table[2][3] = 0.5652;
    	
    	table[3][0] = 18750.0;
    	table[3][1] = 60.0;
    	table[3][2] = 0.2778;
    	table[3][3] = 0.7222;
    	
    	table[4][0] = 183150.0;
    	table[4][1] = 204.0;
    	table[4][2] = 0.1563;
    	table[4][3] = 0.8438;
    	
    	table[5][0] = 1675800.0;
    	table[5][1] = 600.0;
    	table[5][0] = 0.0769;
    	table[5][1] = 0.9231;
    	
    	table[6][0] = 14411250.0;
    	table[6][1] = 1440.0;
    	table[6][2] = 0.0231;
    	table[6][3] = 0.9769;
    	
    	return table;
    }
    
}
