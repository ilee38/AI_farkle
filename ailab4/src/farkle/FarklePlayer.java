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
        	for (int i = 0; i < 6; i++) {
        		if (dice[i] == 1 || dice[i] == 5) {
        			// dice are numbered from 1 to 6 so avoid off by one errors
        			int d = i + 1;
        			if(!usedDice.contains(d)){		//check if dice was used by a 3 of a kind
        				setAside += d + " ";
        			}
        		}
        	}
        	out.print(setAside);
        	
        	// Now need to decide whether to bank or not.
        	// This program always decides to bank.
        	boolean banked = true;
        	
        	// Finish printing
        	if (banked) out.print("bank");
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
        	} else if (status.equals("farkled")) {
        		turn++;
        		turnScore = 0;
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
 * is found. The count is incremented, and the index + 1 of the die number
 * is saved. Finally, if the count is 3 or more, only the first 3 indexes are
 * returned. 
 * */    
    public String findThreeOfAKind(int[] dice, ArrayList<Integer> usedDice){
    	String diceNums = "";
    	ArrayList<ArrayList<Integer>> numCountTable = new ArrayList<ArrayList<Integer>>();
    	for(int i = 0; i <= 6; i++){		//Initialize table to 0
    		numCountTable.add(new ArrayList<Integer>());
    		numCountTable.get(i).add(0);
    	}
    	for(int j = 0; j < dice.length; j++){
    		int dieNum = dice[j];
    		numCountTable.get(dieNum).set(0, numCountTable.get(dieNum).get(0) + 1);		//increment count for die number
    		numCountTable.get(dieNum).add(j+1);		//save index of die number in the dice array (from 1 to 6)
    	}
    	for(int k = 1; k < numCountTable.size(); k++){	//start at index 1
    		if(numCountTable.get(k).get(0) >= 3){		//check if die num count >= 3
    			for(int l = 1; l < 4; l++){		//dice numbers start at index 1
    				diceNums += numCountTable.get(k).get(l) + " ";		//get only the 1st 3 indexes
    				usedDice.add(numCountTable.get(k).get(l));
    			}
    		}
    	}
    	return diceNums;
    }
    
}
