package edu.ramapo.rshresth.casino;

import java.util.Vector;

public class Player 
{
	//score of the player
	private int score;
	
	//vector to store the cards in the player's hand
	private Vector<String> hand;
	//vector to store the captured cards in pile
	private Vector<String> pile;
		
	//Constructor
	public Player() {
		hand = new Vector<String>();
		pile = new Vector<String>();
		score = 0; 
	}
		
	//Getters
	//gets the score
	public int getScore() { return this.score;}
	
	//Setters		
	//sets the score
	public void setScore(int score) {this.score = score;}

	//stores the given card in player hand
	public void storeHand(String card) { hand.add(card);}
		
	//stores the given card in player pile
	public void storePile(String card){
	    pile.add(card);
	}

    //calculates the score of player at the end of a round
	public void calculateScore(){

	    int score = 0;
	    int countSpade = 0;

	    //check for total number of cards in pile
        //if size >26 means player has most cards so add 3 points
	    if(pile.size()>26){ score += 3;}

	    for(int i= 0; i< pile.size();i++){

	        String card = pile.get(i);

            //check for spade cards and count the number of spades
	        if(card.charAt(0) == 'S'){ countSpade++; }
            //check for diamond 10 and if found increase 2 points
            if(card.equals("DX")){ score += 2;}
            //check for spade 2 and if found increase 1 point
            if(card.equals("S2")){ score++; }
            //check for aces and if fount increase 1 point
            if(card.charAt(1) == 'A'){score++; }
        }

        //if number of spade cards are more than 6
        // means player has more spade cards so add 1 point
        if(countSpade >6) { score ++;}

        //update the calculated score
        this.score += score;

    }
	
	//returns the hand card of given index
    //deletes the card form the board if flag true
    public String getHandCard(int index){
		//store the indexed card of deck in temp
		String temp = hand.get(index);

		//Erased the indexed card if flag true
		hand.remove(index);

		return temp;
	}

    //returns copy of hand
    public Vector<String> getAllHandCards() {return hand;}

    //returns copy of pile
	public Vector<String> getAllPileCards() {return pile;}

	//print cards in hand / pile
	//True for hand
	public void printHandOrPile(boolean handFlag){
		Vector<String> temp;
		//check if hand or pile
		if(handFlag) temp = hand;
		else temp = pile;
		
		for (String x:temp)
		{
			System.out.print(x);
			System.out.print(" ");
		}

		System.out.println();
	}

}
