package farkle;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.*;

public class Farkle implements Runnable {
    Scanner in; // for reading from the agent
    PrintStream out; // for printing info to the agent
    int seed;
    boolean askuser;

    public Farkle(Scanner in, PrintStream out) {
        this.in = in;
        this.out = out;
        // A different seed will be used to test your lab.
        seed = 13;
        askuser = false;
    }

    public void run() {
    	// first line is the number of turns to play
        int numTurns = 0;
        if (in.hasNextInt()) {
        	numTurns = in.nextInt();
        	if (! askuser) System.out.println("FarklePlayer " + numTurns);
        	in.nextLine(); // need to scan newline
     	} else {
     		out.printf("quit\n");
     		if (! askuser) System.out.println("Farkle quit");
     		return;
     	}
        
        double totalScore = 0;
        for (int turn = 1; turn <= numTurns; turn++) {
        	// print out turn if ! askuser && turn % linetest == 0
        	int linetest = 10;
            while (linetest < turn)
                linetest *= 10;
            linetest /= 10;
            boolean lineprint = ((! askuser) && turn % linetest == 0);
        	
            /* generate dice for each turn from a fixed seed */
        	Random random = new Random(seed + turn);
        	boolean[] setAside = new boolean[6];
        	int[] dice = new int[6];
        	boolean banked = false;
        	int turnScore = 0;
        	for (int throwNumber = 1; ! banked; throwNumber++) {
        		if (lineprint) System.out.print("Farkle ");
        		for (int i = 0; i < dice.length; i++) {
        			int r = 1 + random.nextInt(6);
        			if (setAside[i]) {
        				out.print("X ");
        				if (lineprint) System.out.print("X ");
        			} else {
        				dice[i] = r;
        				out.print(dice[i]  + " ");
        				if (lineprint) System.out.print(dice[i]  + " ");
        			}	
        		}
        		out.print('\n');
        		if (lineprint) System.out.println();
        		
        		String line = in.nextLine();
        		if (lineprint) System.out.println("FarklePlayer " + line);
        		ArrayList<Integer> setAsideList = new ArrayList<Integer>();
        		Scanner readline = new Scanner(line);
        		while (readline.hasNextInt()) {
        			int d = readline.nextInt();
        			if (d < 1 || d > 6) {
        				throw new RuntimeException("Can't set aside dice number " + d);
        			} else if (setAside[d-1]) {
        				throw new RuntimeException("Dice number " + d + " is already set aside");
        			}
        			setAsideList.add(d);
        			setAside[d-1] = true;
        		}
        		String command = "";
        		if (readline.hasNext()) {
        			command = readline.next();
        		}
        		
        		int partScore = scoreSetAside(dice, setAsideList);
        		turnScore += partScore;
        		if (partScore == 0) {
        			turnScore = 0;
        		}
        		if (turnScore == 0) {
        			banked = true;
        			out.printf("score 0 farkled, average score = %.2f after %d turns\n",
        					totalScore / turn, turn);
        			if (lineprint)
        				System.out.printf("Farkle score 0 farkled, average score = %.2f after %d turns\n",
            					totalScore / turn, turn);
        		} else if (command.equals("bank")) {
        			banked = true;
        			totalScore += turnScore;
        			out.printf("score %d banked, average score = %.2f after %d turns\n",
        					turnScore, totalScore / turn, turn);
        			if (lineprint)
        				System.out.printf("Farkle score %d banked, average score = %.2f after %d turns\n",
        					turnScore, totalScore / turn, turn);
        		} else {
        			banked = false;
        			out.printf("score %d continue\n", turnScore);
        			if (lineprint)
        				System.out.printf("Farkle score %d continue\n", turnScore);
        			boolean hotdice = true;
        			for (boolean b : setAside) {
        				if (! b) hotdice = false;
        			}
        			if (hotdice) {
        				Arrays.fill(setAside, false);
        			}
        		}	
        	} 
        }
        out.print("quit");
        out.print('\n');
        if (! askuser) System.out.println("Farkle quit");
        in.close();
        out.close();
    }
    
    // setAsideList has numbers from 1 to 6, so need to avoid off by one
    private int scoreSetAside(int[] dice, ArrayList<Integer> setAsideList) {
    	int partScore = 0;
    	int[] count = new int[7];
    	for (int d : setAsideList) {
    		count[dice[d-1]]++;
    	}
    	boolean badSetAside = false;
    	for (int i = 0; i < 7; i++) {
    		while (count[i] >= 3) {
    			partScore += (i == 1) ? 1000 : i * 100;
    			count[i] -= 3;
    		}
    		if (i == 1) partScore += count[i] * 100;
    		else if (i == 5) partScore += count[i] * 50;
    		else if (count[i] > 0) {
    			badSetAside = true;
    		}
    	}
    	
    	if (badSetAside && partScore > 0) {
    		throw new RuntimeException("Can't set aside dice that don't score");
    	}
    	
    	return partScore;
    }

    public static void main(String[] args) {
        Farkle farkle = new Farkle(new Scanner(System.in), System.out);
        farkle.askuser = true;
        farkle.run();
    }
}
