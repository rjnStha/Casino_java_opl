package edu.ramapo.rshresth.casino;

import java.util.Vector;

public class Table 
{
	//vector to store the cards on the table board
	private static Vector<Vector<String>> board;
	
	//Constructor
	public Table() {
		//new instance of board in constructor
    	board = new Vector<Vector<String>>();
	}
		
	//stores the given card in a vector 
	//stores the vector with the card in the table board
	//stores the value of card,string as first element of the card
	public void storeCardsTable(String card) {
		String cardValue =card.substring(1);
		
		Vector<String> temp = new Vector<String>();
		temp.add(cardValue);
		temp.add(card);
		board.add(temp);
	}
	
	//stores the build as vector of strings in the table board
	//Overload
    /**
     * Rules --> the first element represents the value of the build
     *  		the second, owner of the build
     *  		the third, if the build is multi or single build
     *  		the fourth, if single build then start inputting card else
     *  		if multi then "[" then start inputting card on fifth
     *  		nth, for multi "]" to end a single build inside multi
	*/
    public void storeCardsTable(Vector<String> build) {	board.add(build);}

	//returns the table card of given index
	//deletes the card form the board
    //also handle build cards
    public Vector<String> getCard(int index){
        //temp vector to store the build
        Vector<String> temp = board.get(index);

        //check for loose or build card
        if(temp.size()>2){
            //build card
            //remove the brackets if multi build
            if(temp.get(2).equals("Multi")) {
                temp.remove("[");
                temp.remove("]");
            }

            //remove the first three elements since card info starts form fourth
            for(int i =0; i<3;i++) { temp.remove(0); }

        }
        else{

            //loose card has 2 elements value and card
            //removing the value
            temp.remove(0);

        }

        //Erased the indexed card form the board
        board.remove(index);
        return temp;
    }

	//returns copy of board
    public Vector<Vector<String>> getAllCards() {return board;}

	//print cards in hand / pile
	public void printTableCards() {
		for (Vector<String> x:board)
		{
			System.out.print(x);
			System.out.print(" ");
		}
		System.out.println();
	}

	//finds the card in the table
	//returns the index if the card found
	//if not found returns -1
	public int findCardTable(String card) {
		int count = 0;
		
		for(int i = 0;i< board.size(); i++)
		{
			System.out.println(board.get(i).get(1));
			if(board.get(i).get(1).equals(card)) break;
			count++;
		}
		
		if(count == board.size()) return -1;
		return count;
	}
	
}
