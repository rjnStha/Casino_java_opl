package edu.ramapo.rshresth.casino;

import java.util.Vector;
import java.util.Random;

public class Deck 
{
	//Vector of strings to store all the cards in Deck
	private Vector<String> cardDeck;

	//constructor
	//Generates 52 cards and shuffles
	public Deck(boolean isNewRound) {
		//new instance of deck in constructor
    	cardDeck = new Vector<String>();

    	//check if it is new round else return without creating new cards
		//required when loading a game
    	if(!isNewRound) return;

    	//assign values and suit to each of the 52 cards
    	for(int i = 0;i < 52;i++)
    	{
    		String temp;
    		
    		//first character as suit
    		if(i<13) temp = "S";
    		else if(i<26) temp = "H";
    		else if(i<39) temp = "C";
    		else temp = "D";
    		
    		//second character as value
    		int tempValue = (i % 13)+1;
    		if(tempValue == 1) temp += "A";
    		else if(tempValue < 10) temp += String.valueOf(tempValue);
    		else if(tempValue == 10) temp += 'X';
    		else if(tempValue == 11) temp += 'J';
    		else if(tempValue == 12) temp += 'Q';
    		else temp += 'K';

    		//push the temp into the deck
    		cardDeck.add(temp);
    	}
    	 
    	shuffle();                        
    }

	//prints all the cards in the deck
	public void printDeckCards() {
		for (String x:cardDeck)
		{
			System.out.print(x);
			System.out.print(" ");
		}
		System.out.println();
	}
	
	//shuffles the deck
	public void shuffle() {
        //creates instance of Random class 
		Random rand = new Random();
        
		//Start from the last element and swap one by one
	    //We don't need to run for the first element that's why i > 0 
		for (int i = cardDeck.size()-1; i > 0; i--) 
	    { 
	        // Generate a random integers in range 0 to i 
	        int j = rand.nextInt(i+1); 
	        
	        // Swap i with the element at random index j 
	        String temp = cardDeck.get(i);
	        cardDeck.set(i,cardDeck.get(j));
	        cardDeck.set(j,temp);
	    }
	}
	
	//removes the first card on deck and returns it
	public String getNewCard() {
		//store the first card of deck in temp
		String temp = cardDeck.get(0);
		//Erased the first card and return
		cardDeck.remove(0);
		return temp;
	}

    //stores the given card in the deck, used when loading game
    public void storeHand(String card) { cardDeck.add(card);}

	//returns the cardDeck vector
	public Vector<String> getDeck() { return cardDeck; }

}
