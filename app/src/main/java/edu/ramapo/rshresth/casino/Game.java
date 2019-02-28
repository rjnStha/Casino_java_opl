/*
 ************************************************************
 * Name:  Rojan Shrestha                                    *
 * Project:  Casino_Project3					            *
 * Class:  CMPS 366 01				                        *
 * Date:  Dec 14th, 2019			                        *
 ************************************************************
 */

package edu.ramapo.rshresth.casino;

import android.os.Environment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Vector;

public class Game
{
	//Game with member variables table,deck and player
	private Deck gameDeck;
	private Human human;
	private Computer computer;
	private Table gameTable;

	//round of the tournament
    private int round;
	//boolean to decide human or computer
	private static boolean humanTurn;

	private boolean lastCaptureIsHuman;
    //Constructor
	//String info --> gives true or false when newGame
	//called for new game
	public Game(String info) {

	    //Initialize at the start of the game
	    gameDeck = new Deck(true);
		human =  new Human();
		computer = new Computer();
		gameTable = new Table();
        round = 0;

        // deals 3 set of 4 cards
        newDealCards(true);

        //setting the turn of the player
        if(info.equals("true")) humanTurn = true;
        else humanTurn = false;

        //Print in console
        System.out.println("Starting the Game:");
        printAllCards();

    }

	//overloading Constructor function
    //called when loading game
    public Game() {

        //Initialize at the start of the game
        gameDeck = new Deck(false);
        human = new Human();
        computer = new Computer();
        gameTable = new Table();
        round = 0;

    }

    //getters

    /* *********************************************************************
    Function Name: newDealCards
    Purpose: gets 4 cards from deck to player hands and table
    Parameters: dealTable, boolean to decide whether to deal cards in table
    Return Value: void
    Local Variables:
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public void newDealCards(boolean dealTable){

        //Distribute card from the deck to table and hands
        if(dealTable) {for(int i =0; i<4; i++) { gameTable.storeCardsTable(gameDeck.getNewCard()); } }
        for(int i =0; i<4; i++) {computer.storeHand(gameDeck.getNewCard());}
        for(int i =0; i<4; i++) {human.storeHand(gameDeck.getNewCard());}

    }

    //returns the vector of table cards
    //does not deletes the card form the board as cardErase flag false
    public Vector<Vector<String>> getTableCards() { return gameTable.getAllCards(); }

    //returns the vector of hand cards depending upon the parameter isHuman, true for human
    //does not deletes the card form the board as cardErase flag false
    public Vector<String> getHandCards(boolean isHuman){
        //check turn for human
        if(isHuman) return human.getAllHandCards();
        return computer.getAllHandCards();
    }

    //returns the vector of pile cards depending upon the parameter isHuman, true for human
    public Vector<String> getPileCards(boolean isHuman){
        //check turn for human
        if(isHuman) return human.getAllPileCards();
        return computer.getAllPileCards();
    }

    //returns true if human Turn
    public boolean isHumanTurn(){ return humanTurn; }

    //changes the turn of the player
    public void changeTurn(boolean changeToHuman){ humanTurn = changeToHuman; }

    /* *********************************************************************
    Function Name: trail
    Purpose: carries out trail action for human or computer with given hand position
    Parameters: handPosition, integer that stores the hand position to trail
    Return Value: String, info about the move
    Local Variables:
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public String trail(int handPosition) {

        //Check human or computer
        //get the card from hand and store in the table board
        if (humanTurn) { gameTable.storeCardsTable(human.getHandCard(handPosition)); }
        else { gameTable.storeCardsTable(computer.getHandCard(handPosition));}

        return "success: Trailed hand"+handPosition;

    }

    /* *********************************************************************
    Function Name: capture
    Purpose: carries out capture action for human or computer with given hand position and table card position
    Parameters:
        handPosition, integer that stores the hand card position to trail
        tableCardPosition, Vector storing the selected table card position
    Return Value: String, info about the move
    Local Variables: currentPlayer, Player to decide human or computer and use the variable to carry capture action
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    private String capture(int handPosition, Vector<String> tableCardPosition) {

	    //since Capture involves deleting elements from table
        //arrange the tableCardPosition in descending order
        //prevents removing less indexed element before higher ones
        Collections.sort(tableCardPosition, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) { return  Integer.parseInt(o2) - Integer.parseInt(o1); }
        });

	    //get the current player
	    Player currentPlayer ;
	    if(humanTurn) {currentPlayer = human;}
	    else {currentPlayer = computer;}

        //store the hand card and table cards into human pile
        currentPlayer.storePile(currentPlayer.getHandCard(handPosition));

        //storing selected table cards into human pile
        for(int i = 0; i<tableCardPosition.size(); i++){

            int currentTableCardPos = Integer.parseInt(tableCardPosition.get(i));

            //check for build cards
            if(gameTable.getAllCards().get(currentTableCardPos).size() > 2){

                //local variable to store the vector with buildInfo
                Vector<String> buildCards = gameTable.getCard(currentTableCardPos);
                //get each card from the build
                for(int j = 0; j<buildCards.size(); j++){
                    String temp = buildCards.get(j);
                    if(!temp.equals("[") && !temp.equals("]")){currentPlayer.storePile(temp);}
                }

            } else{

                //loose card
                currentPlayer.storePile(gameTable.getCard(currentTableCardPos).get(0));
            }

        }
        return "success: Captured hand"+handPosition+ " and table"+ tableCardPosition;
    }

    /* *********************************************************************
    Function Name: makeHumanMove
    Purpose: checks for valid move and carries out the move for human
    Parameters: moveInfo, Vector storing the type of move, hand card position and table card position
        t2 --> for trial hand positon 2
        b2356 --> for build hand position 2 and table position 3,5 and 6
        c2321 --> for capture hand position 2 and table position 3,2 and 1
    Return Value: String, info aobut the succesful and unsuccesful move
    Local Variables: msg, String to store the move sucess info
        moveCheck, MoveCheck for move validation
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public String makeHumanMove(Vector<String> moveInfo){

	    String msg = "";

	    //Decode the information moveInfo

        //move check before making move
        //creating MoveCheck object with given info
        MoveCheck moveCheck = new MoveCheck(moveInfo);

        //get the type of move and the position of the hand
        String moveType = moveCheck.getMoveType();
        int handPosition= moveCheck.getHandPosition();
        Vector<String> tablePosition = moveCheck.getMoveTableInfo();

        //Check the moveType and call the required function to make move
        if(moveType.equals("t")) {

            //Trail
            msg = trail(handPosition);

        } else if(moveType.equals("c")) {

            //Capture

            //check for invalid move and return if invalid
            if(!moveCheck.moveCheckCapture()) { return moveAfterMessage(0);}


            //carry out the move after successful validation
            msg = capture(handPosition, moveInfo);

            //Setting the last capturer to the given turn
            lastCaptureIsHuman = humanTurn;

        } else {

            //Build

            //Check for invalid move
            //returns if move invalid
            if (!moveCheck.moveCheckBuild()) { return moveAfterMessage(1); }

            //removing the hand card from hand
            human.getHandCard(handPosition);

            //since Build involves deleting elements from table
            //arrange the tableCardPosition in descending order
            //prevents removing less indexed element before higher ones
            Collections.sort(tablePosition, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) { return  Integer.parseInt(o2) - Integer.parseInt(o1); }
            });

            //removes the user give positioned table card from the table
            for(int i = 0; i< tablePosition.size();i++) { gameTable.getCard(Integer.parseInt(tablePosition.get(i))); }

            //gets the string value of build after the user given moves are validated
            //calls the storeBuildTable to store the build into the table
            storeBuildTable(moveCheck.getCurrentBuildMove());

            msg = "success: Build hand "+handPosition+"table";

        }

        //new sets of cards if both hands empty
        //new deck or round
        return msg +"\n"+ afterMoveOperation();
    }

    /* *********************************************************************
    Function Name: computerMove
    Purpose: checks for valid move and carries out the move for computer
    Parameters:
    Return Value: String, info aobut the succesful and unsuccesful move
    Local Variables: msg, String to store the move sucess info
        moveCheck, MoveCheck for move validation
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public String computerMove() {

        String msgMove = "";

        //move check before making move
        //creating MoveCheck object with given info
        MoveCheck moveCheck = new MoveCheck("c");
        //Check for empty list --> no moves for given hand card
        Vector<String> bestCaptureMove = moveCheck.getBestCaptureTotal();
        int handPosition;
        if(!bestCaptureMove.isEmpty()){

            //getting the last info of besCaptureMove since it is the hand position
            handPosition = Integer.parseInt(bestCaptureMove.get(bestCaptureMove.size()-1));
            bestCaptureMove.remove(bestCaptureMove.size()-1);
            //capture
            msgMove = capture(handPosition, bestCaptureMove);
            //Setting the last capturer
            lastCaptureIsHuman = humanTurn;
        }
        else{

            //attempt build
            moveCheck = new MoveCheck("b");
            moveCheck.getBestBuildTotal();
            String buildInfo =moveCheck.getCurrentBuildMove();
            if(!buildInfo.equals("")) {

                //removing the hand card from hand
                computer.getHandCard(moveCheck.getHandPosition());

                Vector<String> tablePositionInfo = moveCheck.getMoveTableInfo();
                Collections.sort(tablePositionInfo, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) { return  Integer.parseInt(o2) - Integer.parseInt(o1); }
                });

                //removes the user give positioned table card from the table
                for(int i = 0; i< tablePositionInfo.size();i++) { gameTable.getCard(Integer.parseInt(tablePositionInfo.get(i))); }

                //store the build in table
                storeBuildTable(buildInfo);
                msgMove = "success: Build - " + buildInfo;

            }
            else{
                //trail
                msgMove = trail(0);
            }
        }

        //alternate turn
        humanTurn = true;

        //return successful trail and the position
        return msgMove +"\n"+afterMoveOperation();

    }

    /* *********************************************************************
    Function Name: afterMoveOperation
    Purpose: checks for status of the game,  new hand or new round or end of tournament. Carries appropriate actions
        depending upon the game status like dealing new cards, calculating score...
    Parameters:
    Return Value: String, info about status of the game, new hand or new round or end of tournament
    Local Variables: gameStatus, info about status of the game, new hand or new round or end of tournament
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public String afterMoveOperation(){

	    printAllCards();

	    //variable to store different routes of game
	    String gameStatus = "";

	    //check for empty deck which equals end of the round
	    if(gameDeck.getDeck().isEmpty() && human.getAllHandCards().isEmpty() && computer.getAllHandCards().isEmpty()) {

	        // if still cards in the table then
            // capture the cards at the end of the round by the player to last capture
	        Vector<Vector<String>> tableCards = gameTable.getAllCards();
	        if(!tableCards.isEmpty()){

	            System.out.print("not empty");
	            //Calculate the last capturer
	            Player playerCurrent;
                if (lastCaptureIsHuman) { playerCurrent = human; }
                else { playerCurrent = computer; }

                //storing the table cards into the player's pile
                for (int i = 0; i < tableCards.size(); i++) {
                    //get the table card and store in the player's hand
                    String card =  tableCards.get(i).get(1);
                    playerCurrent.storePile(card);
                }

                //removing the cards from the table
                for (int i = 0; i < tableCards.size(); i++) { gameTable.getCard(i); }
            }

            //Calculate score for computer and human
            human.calculateScore();
            computer.calculateScore();

            //Tournament ends
            if(human.getScore() >= 21 || computer.getScore() >= 21){
                gameStatus = "GameEnd Winner-";

                //Calculate the winner
                int scoreDifference = human.getScore() - computer.getScore();
                if(scoreDifference > 0) { gameStatus+="Human"; }
                else if(scoreDifference < 0) { gameStatus+="Computer"; }
                else { gameStatus+="Draw"; }

            }
            else{
                //new deck for new round
                gameDeck = new Deck(true);
                newDealCards(false);
                //set the turn to the last capturer
                humanTurn = lastCaptureIsHuman;
                //Calculate the score
                human.setScore(human.getScore()+calculateScore(true));
                computer.setScore(computer.getScore()+calculateScore(false));

                gameStatus = "NewRound Human Score: "+human.getScore()+" Computer Score: " + computer.getScore() ;
                gameStatus += "\n Human Pile: "+ human.getAllPileCards()+"\n Computer Pile: "+ computer.getAllPileCards();

            }

        } else if(!gameDeck.getDeck().isEmpty() && human.getAllHandCards().isEmpty() && computer.getAllHandCards().isEmpty()) {
	        //deck not empty but human and computer hands are both empty
            //deal new card
            newDealCards(false);
            gameStatus = "NewHand";
        }

        return gameStatus;

    }

    /* *********************************************************************
    Function Name: calculateScore
    Purpose: calculates the score
    Parameters: isHuman, boolean to decide the player
    Return Value: int, value of score of given player
    Local Variables: pile, Vector to store the given player's pile
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public int calculateScore(boolean isHuman){
	    int score = 0;
	    Vector<String> pile = getPileCards(isHuman);
	    int countSpade = 0;

	    if(pile.size()>26) score+=4;

	    for(int i = 0;i<pile.size();i++){
	        String card = pile.get(i);
	        score += scoreCalculator(card);
	        if(card.charAt(0) =='S') countSpade++;
	    }

	    //count the spade
	    if(countSpade>6) score++;

	    return score;
    }

    /* *********************************************************************
    Function Name: scoreCalculator
    Purpose: calculates the score value for a given card. Checks for Aces and diamond 10 and spade 2
    Parameters: card, String the card info ex: DX --> diamond 10
    Return Value: int, score value of given card
    Local Variables: score, int
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    private int scoreCalculator(String card){
        int score = 0;
        if( card.equals("CA") || card.equals("DA") || card.equals("SA") || card.equals("HA") || card.equals("S2")){ score = 1;}
        else if(card.equals("DX")){ score = 2;}
        return score;
    }

    /* *********************************************************************
    Function Name: loadGame
    Purpose: loads game of given filename
    Parameters: fileName, String name of the file to load
    Return Value: boolean, check whether load was success
    Local Variables: many..and not so important
    Algorithm:
        1. Get a line form the file and parse the line
        2. Check for different labels and carry out required actions to store the info
    Assistance Received: none
    ********************************************************************* */
    public boolean loadGame(String fileName) {

	    //Empty string
        String label = "";
        String data = "";
        boolean isHuman = false;

        try {

            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;

            while ((line = reader.readLine()) != null) {


                //Scanner object to use delimiter
                Scanner scan = new Scanner(line);
                // initialize the string delimiter
                scan.useDelimiter(": ");
                //loop to get string values divided by the delimiter

                if (scan.hasNext()) label = scan.next();
                if (scan.hasNext()) data = scan.next();

                // closing the scanner stream
                scan.close();

                //Check the label
                if (label.equals("Round")) {
                    //Round

                    //set the round
                    round = Integer.parseInt(data);
                    //System.out.println("parse_Round:"+Integer.parseInt(data));

                } else if (label.equals("Computer")) {

                    //set the flag false
                    isHuman = false;

                } else if (label.equals("Human")) {

                    //set the flag true
                    isHuman = true;

                } else if (label.equals("   Score")) {

                    //Score

                    //check human/computer and set the score of the players
                    if (isHuman) {

                        human.setScore(Integer.parseInt(data));
                        //System.out.println("parse_human_Score:"+human.getScore());;
                    } else {

                        computer.setScore(Integer.parseInt(data));
                        //System.out.println("parse_comp_Score:"+computer.getScore());;

                    }

                } else if (label.equals("   Hand")) {

                    //Hand

                    //Scanner object to use delimiter and get individual cards
                    scan = new Scanner(data);
                    // initialize the string delimiter
                    scan.useDelimiter(" ");

                    //loop to get string values divided by the delimiter
                    while (scan.hasNext()) {
                        String card = scan.next();

                        //Check hand of human or computer
                        if (isHuman) {

                            human.storeHand(card);
                            //System.out.println("parse_human_Hand:"+human.getAllHandCards());


                        } else {

                            computer.storeHand(card);
                            //System.out.println("parse_comp_Hand:"+computer.getAllHandCards());

                        }
                    }

                    // closing the scanner stream
                    scan.close();


                } else if (label.equals("   Pile")) {

                    //Pile

                    //Scanner object to use delimiter and get individual cards
                    scan = new Scanner(data);
                    // initialize the string delimiter
                    scan.useDelimiter(" ");

                    //loop to get string values divided by the delimiter
                    while (scan.hasNext()) {
                        String card = scan.next();

                        if (isHuman) {

                            human.storePile(card);
                            //System.out.println("parse_human_pile:"+human.getAllPileCards());

                        } else {

                            computer.storePile(card);
                            //System.out.println("parse_comp_pile:"+computer.getAllPileCards());

                        }
                    }

                    // closing the scanner stream
                    scan.close();

                } else if (label.equals("Table")) {

                    //Table
                    //Ignore build cards in this section and handles in "Build Owner"


                    //Scanner object to use delimiter and get individual cards
                    scan = new Scanner(data);
                    // initialize the string delimiter
                    scan.useDelimiter(" ");

                    //loop to get string values divided by the delimiter
                    //loop until there are more elements in the scan
                    while (scan.hasNext()) {


                        String card = scan.next();

                        //check conditions to find the build in the table
                        //finding using brackets [ , ]
                        //Ignore the build in "Table" instead parsing in "Build Owner"

                        //System.out.println(card);

                        if (card.equals("[")) {

                            //loop until card equals the string "]"
                            do {
                                card = scan.next();
                                //System.out.println(card);

                            } while (scan.hasNext() && !card.equals("]"));

                        } else if (card.charAt(0) == '[') {

                            //loop until char at 2 in card equals the string "]"
                            do {
                                card = scan.next();
                                //System.out.println(card);

                            } while (scan.hasNext() && card.charAt(2) != ']');
                        } else {
                            //regular card string

                            //store the card in table
                            gameTable.storeCardsTable(card);
                            //System.out.println("parse_table:"+gameTable.getAllCards());

                        }
                    }

                    // closing the scanner stream
                    scan.close();

                } else if (label.equals("Deck")) {

                    //Deck

                    //Scanner object to use delimiter and get individual cards
                    scan = new Scanner(data);
                    // initialize the string delimiter
                    scan.useDelimiter(" ");


                    //loop to get string values divided by the delimiter
                    while (scan.hasNext()) {

                        String card = scan.next();

                        gameDeck.storeHand(card);
                        //gameDeck.printDeckCards();

                    }

                    // closing the scanner stream
                    scan.close();

                } else if (label.equals("Next Player")) {

                    //Player turn

                    //Check if the next turn is computer or human
                    //set the turn of the player
                    if (data.equals("Computer")) {
                        humanTurn = false;
                    } else {
                        humanTurn = true;
                    }

                } else if (label.equals("Build Owner")) {
                    //build ownership
                    // Parsing build
                    // since ignore build cards in "Table" section

                    //call store build function
                    if(!storeBuildTable(data)) return false;
                }

                //resetting the values to avoid repeating data
                data = "";
                label = "";
            }

            reader.close();
            //printing in the console
            printAllCards();
            return true;

        } catch (Exception ex) {

            //Unable to open file
            //Error reading file
            //Displaying the turn of the player
            return false;

        }



    }

    /* *********************************************************************
    Function Name: saveGame
    Purpose: saves game of given filename
    Parameters: fileName, String name of the file to load
    Return Value: boolean, check whether save was success
    Local Variables: many..and not so important
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public boolean saveGame(String fileName){

	    //if the user input empty filename
	    if(fileName.isEmpty()) return false;

        File file = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOCUMENTS)+"/"+fileName+".txt");
        FileOutputStream fos;

        try {

            //attach the file to fileOutputStream
            fos = new FileOutputStream(file);

            //check whether the file exists, not then create
            if (!file.exists()) { file.createNewFile(); }

            //variable to store a line of info
            String contentLine = "";


            //Storing Round

            contentLine += "Round: "+ round + "\n\n";

            //Storing Computer info

            contentLine += "Computer: \n"+ "   Score: "+computer.getScore()+"\n   Hand: ";
            //get cards from hand vector
            Vector<String> handOrPile = computer.getAllHandCards();
            for(int i = 0; i<handOrPile.size();i++){ contentLine += handOrPile.get(i)+" "; }
            //get cards from pile vector
            contentLine +="\n   Pile: ";
            handOrPile = computer.getAllPileCards();
            for(int i = 0; i<handOrPile.size();i++){ contentLine += handOrPile.get(i)+" "; }
            //new line
            contentLine +="\n\n";

            //Storing Human info

            contentLine += "Human: \n"+ "   Score: "+human.getScore()+"\n   Hand: ";
            //get cards from hand vector
            handOrPile = human.getAllHandCards();
            for(int i = 0; i<handOrPile.size();i++){ contentLine += handOrPile.get(i)+" "; }
            //get cards from pile vector
            contentLine +="\n   Pile: ";
            handOrPile = human.getAllPileCards();
            for(int i = 0; i<handOrPile.size();i++){ contentLine += handOrPile.get(i)+" "; }
            //new line
            contentLine +="\n\n";

            //Storing Table

            Vector<Vector<String>> tableCards = gameTable.getAllCards();
            contentLine += "Table: ";

            //Store the buildOwner while accessing table
            Vector<String> buildOwner = new Vector<>();

            for(int i = 0; i < tableCards.size(); i++){

                //accessing elements of the table board

                //check the table element size for build
                //>2 means its a build
                if(tableCards.get(i).size() == 2){ contentLine += tableCards.get(i).get(1)+" ";}
                else {

                    //build Owner
                    buildOwner.add("Build Owner: ");
                    //to store each build seperately
                    String buildInfo = "";

                    //build
                    //check if multi or single build, stored as 3rd element of the table
                    if(tableCards.get(i).get(2).equals("Single")) {
                        buildInfo +="[";

                        //Single build so simply store the cards
                        for(int j = 3; j < tableCards.get(i).size();j++){

                            //adding the cards without any space in between
                            buildInfo += tableCards.get(i).get(j);

                            //Check if it's the last element of the build
                            //since no need to add " " to the last element
                            if(j != tableCards.get(i).size()-1) buildInfo += " ";

                        }
                    }
                    else{
                        //multi build
                        buildInfo +="[ ";

                        //loop to get the cards form the given element of the table
                        //card's info starts from index 3
                        for(int j = 3; j < tableCards.get(i).size();j++) {

                            //find if it's single or multi card
                            if(tableCards.get(i).get(j).equals("[") ){
                                //multi card build

                                //adding start
                                buildInfo += "[";

                                //increasing index to get new card
                                j++;

                                while (!tableCards.get(i).get(j).equals("]")){

                                    buildInfo += tableCards.get(i).get(j);
                                    j++;
                                    if(!tableCards.get(i).get(j).equals("]")) buildInfo += " ";
                                }

                                //adding end
                                buildInfo += "] ";
                            }
                            else {

                                //single card
                                //no bs just write the card inside [ ]
                                buildInfo += "["+tableCards.get(i).get(j)+"] ";
                            }
                        }
                    }

                    //end of the build
                    buildInfo += "] ";

                    //storing the info to the contentLine
                    contentLine += buildInfo;

                    //adding build info to buildOwner
                    int start = buildInfo.indexOf('[');
                    buildOwner.add(buildInfo.substring(start));
                    buildOwner.add(tableCards.get(i).get(1)+"\n\n");

                }
            }

            System.out.println(buildOwner);

            //next line
            contentLine += "\n\n";

            //adding the build owner info from vector to the content
            for(int i = 0; i<buildOwner.size();i++){ contentLine += buildOwner.get(i); }

            //addding the Deck
            contentLine += "Deck: ";
            Vector<String> deck = gameDeck.getDeck();
            for(int i = 0; i<deck.size();i++){ contentLine += deck.get(i)+" "; }

            //next line
            contentLine += "\n\n";

            //adding the next Player
            contentLine += "Next Player: ";
            if(humanTurn){ contentLine += "Human"; }
            else{ contentLine += "Computer"; }

            fos.write(contentLine.getBytes());
            fos.flush();
            fos.close();

            return true;

        } catch (IOException e) {

            // handle exception
            System.out.println("ERROR");
            return false;

        }
    }

    /* *********************************************************************
    Function Name: printAllCards
    Purpose: Prints all card info Deck,Table,hand and pile of both comp and human into console
    Parameters:
    Return Value: void
    Local Variables:
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public void printAllCards(){
        System.out.println();
        gameDeck.printDeckCards();
        System.out.println("Computer Score: "+computer.getScore());
        computer.printHandOrPile(true);
        computer.printHandOrPile(false);

        //Human
        System.out.println("Human Score: "+human.getScore());
        human.printHandOrPile(true);
        human.printHandOrPile(false);

        //Table and turn
        System.out.print("Table: ");
        gameTable.printTableCards();
        gameDeck.printDeckCards();
        System.out.println("human: "+humanTurn);
    }
    /* *********************************************************************
    Function Name: cardStringToValue
    Purpose: converts card value in character to numeric value
    Parameters: key, char the character value of card DX --> X
    Return Value: int, integer value of the card
    Local Variables:
    Algorithm: Check for X, A, J, Q and assign necessary values
    Assistance Received: none
    ********************************************************************* */
    public int cardStringToValue(char key) {
        switch(key)
        {
            case 'A': return 1;
            case 'K': return 13;
            case 'Q': return 12;
            case 'J': return 11;
            case 'X': return 10;
            default: return key-'0';
        }
    }

    /* *********************************************************************
    Function Name: storeBuildTable
    Purpose: stores one build multi or single into the table for human and computer
    Parameters: data, String the info of build in format --> [ [DX] [H9 SA] ] Human
    Return Value: boolean, true for successful store of build
    Local Variables: scan, Scanner to use delimiter and get individual cards
    Algorithm: Parse the string data using delimiter "[" and "]"
    Assistance Received: none
    ********************************************************************* */
    public boolean storeBuildTable(String data){
        //Scanner object to use delimiter and get individual cards
        Scanner scan = new Scanner(data);
        //initialize the string delimiter
        scan.useDelimiter(" ");

        //to store all the data in the build
        Vector<String> buildCards = new Vector<>();

        //to store the value of the build
        int buildValue = 0;
        //required to differentiate multi and single build and while calculating the build value
        int start;
        int end;

        String card = scan.next();

        if (card.equals("[")) {

            //multi build

            buildCards.add("Multi");
            //get new card to compare
            card = scan.next();

            //6 types of card info
            // [ , [S8 , S8, S8], [S8] , ]
            //but ony [S8] and [S8 starts the single build

            //loop until the end of the build
            while (!card.equals("]")) {

                //type [S8
                if (card.length() == 3 && card.charAt(0) == '[') {

                    //adding [ and S8 from [S8 to vector as separate elements
                    buildCards.add("[");
                    buildCards.add("" + card.charAt(1) + card.charAt(2));

                    //get new card
                    card = scan.next();

                    //loop until the end of the single build of type [S8 .. S8]
                    //end means data with value S8]
                    while (card.length() != 3) {

                        //if not the end card S8]
                        //then the value must be individual card S8
                        buildCards.add(card);
                        //get new card to compare
                        card = scan.next();
                    }

                    //adding ] and S8 from S8] to vector as separate elements
                    buildCards.add("" + card.charAt(0) + card.charAt(1));
                    buildCards.add("]");



                } else {

                    //type [S8]

                    //Simply adding the card info ignoring [ and ]
                    buildCards.add("" + card.charAt(1) + card.charAt(2));

                }

                //get new single build or "]" as end of the multi build
                card = scan.next();

            }


            //calculate the build value start and end index
            start = buildCards.indexOf("[")+1;
            end = buildCards.indexOf("]");



        } else if (card.length() == 3 && card.charAt(0) == '[') {

            //single build
            buildCards.add("Single");

            //type [S8
            //adding S8 from [S8 to vector
            buildCards.add("" + card.charAt(1) + card.charAt(2));

            //get new card
            card = scan.next();

            //loop until the end of the single build of type [S8 .. S8]
            //end means data with value S8]
            while (card.length() != 3) {

                //if not the end card S8]
                //then the value must be individual card S8
                buildCards.add(card);
                //get new card to compare
                card = scan.next();
            }

            //adding S8 from S8] to vector and ending the single build
            buildCards.add("" + card.charAt(0) + card.charAt(1));

            //calculate the build value start and end index
            start = 1;
            end = buildCards.size();

        } else {
            //error in the file
            return false;
        }

        //calculating the build value
        //fails to calculate the buildValue for case
        // [0,Human, Multi, H6,C6, S6] since no [ and ]
        for(int i = start;i < end;i++){
            buildValue += cardStringToValue(buildCards.get(i).charAt(1));
        }

        //for case: [0,Human, Multi, H6,C6, S6]
        if(buildValue ==0) { buildValue = cardStringToValue(buildCards.get(1).charAt(1));}

        //get the owner of the build human or computer
        card = scan.next();

        //temporary local string vector to store all info of a build
        Vector<String> buildTotal = new Vector<>();

        //value of the build
        buildTotal.add("" + buildValue);
        //owner: human or computer
        buildTotal.add(card);

        //store the each card info in build into temp vector
        for (int i = 0; i < buildCards.size(); i++) { buildTotal.add(buildCards.elementAt(i)); }

        //Store in the game table
        gameTable.storeCardsTable(buildTotal);

        return true;
    }

    /* *********************************************************************
    Function Name: moveAfterMessage
    Purpose: returns the string prompt depending upon index
    Parameters: index, int key for a given String prompt
    Return Value: String, string prompt for given index
    Local Variables: msg, String
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public String moveAfterMessage(int index){

	    String msg =  "" ;
	    switch (index){
            case 0:
                msg = "Invalid Capture Move: Please select table card whose value is equal to hand card \n or select set of cards whose sum is equal to hand card";
                break;
            case 1:
                msg ="Invalid Build Move: Please select cards following Build making rules";
                break;

        }
	    return msg;
    }
}
