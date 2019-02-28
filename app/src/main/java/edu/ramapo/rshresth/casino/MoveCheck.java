/*
 ************************************************************
 * Name:  Rojan Shrestha                                    *
 * Project:  Casino_Project3					            *
 * Class:  CMPS 366 01				                        *
 * Date:  Dec 14th, 2019			                        *
 ************************************************************
 */

package edu.ramapo.rshresth.casino;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import static edu.ramapo.rshresth.casino.GameActivity.CasinoGame;

//MoveCheck main purpose is to make list of possible moves
//for given moveType and hand Position
//required when validating move, making computer move and human move suggestion
public class MoveCheck {

    //stores move info
    private Vector<String> moveTableInfo;
    private String moveType;
    private int handPosition;
    private String currentBuildMove;
    private boolean isHuman;
    private int valueHandPosition;

    //initializes variables and calls
    MoveCheck(Vector<String> movesStorage){

        this.moveType = movesStorage.get(0);
        this.handPosition = Integer.parseInt(movesStorage.get(1));

        //removing the action and handCard info
        movesStorage.remove(0);
        movesStorage.remove(0);

        //check if table section of the information is empty
        //if empty means strictly capture or build action
        if(!movesStorage.isEmpty()){
            this.moveTableInfo = movesStorage;
        }

        //set the flag for human or computer's turn
        isHuman = CasinoGame.isHumanTurn();

    }

    //Constructor overload
    //required for computer move and human help
    MoveCheck(String moveType){
        this.moveType = moveType;
        currentBuildMove = "";

        //set the flag for human or computer's turn
        isHuman = CasinoGame.isHumanTurn();
    }

    /* *********************************************************************
    Function Name: moveCheckCapture
    Purpose: checks if the given capture move is valid
    Parameters:
    Return Value: boolean, true if move valid
    Local Variables: handCard, String info of selected hand position
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public boolean moveCheckCapture(){

        //hand Card and its numeric value
        String handCard = CasinoGame.getHandCards(isHuman).get(handPosition);
        int handCardValue = CasinoGame.cardStringToValue(handCard.charAt(1));
        return captureBuildSumChecker(handCardValue);
    }

    /* *********************************************************************
    Function Name: moveCheckBuild
    Purpose: checks if the given build move is valid
    Parameters:
    Return Value: boolean, true if move valid
    Local Variables: selectedHandCard, String info of selected hand position
    Algorithm:
        1. Get each human hand except selected hand card and checks for build value
        2. Calls required functions for given build value to for move validation
    Assistance Received: none
    ********************************************************************* */
    public boolean moveCheckBuild(){

        //Selected hand card value
        String selectedHandCard = CasinoGame.getHandCards(isHuman).get(handPosition);
        int selectedHandCardValue = CasinoGame.cardStringToValue(selectedHandCard.charAt(1));

        //get the vector of hand cards
        Vector<String> handCards = CasinoGame.getHandCards(isHuman);

        //flag to find if build valued card in hand
        boolean buildValueFound = false;
        int buildValue = 0;
        //loop through all the hand cards to find the build valued hand card
        for(int i = 0; i<handCards.size();i++ ){

            //check for selected hand card and ignore if found
            if(i == handPosition){ continue; }

            //build value equals the value of given Hand Card
            buildValue = CasinoGame.cardStringToValue((CasinoGame.getHandCards(isHuman).get(i)).charAt(1));

            //check validity for given hand card
            if(buildValue >= selectedHandCardValue && captureBuildSumChecker(buildValue)) {
                //found a hand card whose value is equal to build Value
                buildValueFound = true;
                break;
            }
        }

        //if build move valid create string to make move
        if(buildValueFound) { generateStringBuildMove(buildValue); }

        return buildValueFound;
    }

    //for trail
    public void moveCheckTrail(){ }

    //check for valid set of cards whose sum is equal to the the valueCompare
    /* *********************************************************************
    Function Name: captureBuildSumChecker
    Purpose: Checks if sum of given move is divisible by the value to compare
        if divisible calls recursiveSumChecker for further validation of the move
    Parameters: valueCompare, integer the value of the card to compare
    Return Value: boolean, true if the move is valid
    Local Variables: valueSetTable, Vector to store valid moves for given value Compare
    Algorithm:
        1. get the first element the highest value in the set
        2. for(size of list)
            3. Add the given value with next elements in the list and check the sum for =< valueCompare
            4. If equal then remove both the elements
            5. If less than get another value from the list and add to the sum, check for condition
            6. If greater than get next value
        7. Repeat 0,1,2,3,4 until no element in the list
    Assistance Received: none
    ********************************************************************* */
    private boolean captureBuildSumChecker(int valueCompare) {

        Vector<Vector<String>> tableCards = CasinoGame.getTableCards();

        //loop to get a vector of table card values, each being less than the valueCompare
        Vector<Integer> valueSetTable = new Vector<>();
        for(int i = 0; i<moveTableInfo.size();i++) {

            //check for build or loose card
            //get the value of the card
            int tableValue;
            int index = Integer.parseInt(moveTableInfo.get(i));
            Vector<String> currentTableCard = tableCards.get(index);
            if (currentTableCard.size() > 2) {

                //Check for Multi build for action build and return false since multi build cannot be further build into
                if(moveType == "b" && currentTableCard.get(2).equals("Multi")) return false;
                tableValue = Integer.parseInt(currentTableCard.get(0));

            } else { tableValue = CasinoGame.cardStringToValue(currentTableCard .get(0).charAt(0)); }

            //the given table value can never be greater than the valueCompare
            //only add values less than valueCompare
            if(tableValue > valueCompare) { return false; }
            else if(tableValue < valueCompare){ valueSetTable.add(tableValue); }
            //else don't add to the list, equal
        }

        //check if the action is build
        //add the selected hand to the list
        if(moveType == "b") {
            //Selected hand card value
            String selectedHandCard = CasinoGame.getHandCards(isHuman).get(handPosition);
            valueSetTable.add(CasinoGame.cardStringToValue(selectedHandCard.charAt(1)));
        }

        //sort the table values list in descending order with respect to their values
        Collections.sort(valueSetTable, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) { return  o2 - o1;} });

        //******************************************/
        //Algorithm to check for valid set of cards whose sum is equal to the the valueCompare
        //list with elements in descending,
        // 0. get the first element the highest value in the set
        //      for(size of list)
        // 1.   Add the given value with next elements in the list and check the sum for =< valueCompare
        // 2.   If equal then remove both the elements
        // 3.   If less than get another value from the list and add to the sum, check for condition
        // 4.   If greater than get next value
        // 5. repeat 0,1,2,3,4 until no element in the list
        //******************************************/
        Iterator<Integer> itr = valueSetTable.iterator();
        while(itr.hasNext()){
            itr = valueSetTable.iterator();
            int currentHighValue = itr.next();
            itr.remove();

            Vector<Integer> tempValueSetTable = new Vector<>(valueSetTable);

            Vector<Integer> returnedRemSum = new Vector<>();
            //check for each element and return false when no other elements to add found

            //stops when finds the remaining sum values
            while(!tempValueSetTable.isEmpty()){
                returnedRemSum = recursiveSumChecker(currentHighValue,valueCompare,tempValueSetTable);
                if(returnedRemSum.size() != 0){
                    //break since the vector is empty
                    //no set sum values found
                    break;
                }

                System.out.println("captureBuildSumChecker: "+returnedRemSum+" "+tempValueSetTable);

                tempValueSetTable.remove(0);
            }

            //check if the vector is empty which means the given value
            //does not have a pair value in the list
            //invalid move
            if(returnedRemSum.size() == 0) { return false;}
            else {
                //valid values
                //find their poistion and remove from the list
                for(int i =0; i<returnedRemSum.size();i++){
                    //get the position of the equal values in the list
                    //and remove when found
                    int position = valueSetTable.indexOf(returnedRemSum.get(i));
                    valueSetTable.remove(position);
                }
            }

        }

        return true;
    }

    /* *********************************************************************
    Function Name: recursiveSumChecker
    Purpose: utility function for captureBuildSumChecker that recursively checks for valid sets of card for given move
    Parameters:sum ,int to store the sum value --> 0 when capture and selected handValue when build
            valueCompare, int to store the value to compare
            valueSetTable, Vector<Integer> to store the values of selected table card
    Return Value: boolean, true if move valid
    Local Variables: tempValidValue, Vector to store the pair of valid card values
    Algorithm:
        1. For a given given card in table add its value to the sum
        2. Check if the sum value is equal, less or more than the valueCompare
        3. if equal add the value to the vector and return
        4. if less than remove the first element(the current value) and get new value and call recursiveSumChecker with
            new valueSetTable
        5. if more than return empty vector as a sign of invalid move

    Assistance Received: none
    ********************************************************************* */
    private Vector<Integer> recursiveSumChecker(int sum , int valueCompare, Vector<Integer> valueSetTable){

        System.out.println("recursiveSumChecker: "+ sum +" "+ valueCompare +" "+valueSetTable);
        //empty vector
        Vector<Integer> tempValidValue = new Vector<>();

        //recursion when the sum is less the valueCompare
        //itr = valueSetTableNew.iterator();
        int currentLowerValue = valueSetTable.get(0);
        sum += currentLowerValue;

        //only delete when found equal
        //and return true
        if(sum == valueCompare){
            valueSetTable.remove(0);
            //returning the value that lead to the required sum
            Vector<Integer> temp = new Vector<>();
            temp.add(currentLowerValue);
            return temp;
        }
        else if(sum < valueCompare){

            //remove the first element from the temp valueSetTable
            Vector<Integer> tempValueSetTable = new Vector<>(valueSetTable);
            tempValueSetTable.remove(0);

            //loop until the list is empty
            while(!tempValueSetTable.isEmpty()){
                tempValidValue = new Vector<Integer>(recursiveSumChecker(sum,valueCompare,tempValueSetTable));

                //Check for non empty vector
                //end of the recursion
                if(!tempValidValue.isEmpty()){
                    tempValidValue.add(currentLowerValue);
                    return tempValidValue;
                }
                tempValueSetTable.remove(0);
            }

            //returns empty list
            return tempValueSetTable;
        }
        else{
            //sum > valueCompare
            return tempValidValue;
        }
    }

    /* *********************************************************************
    Function Name: generateStringBuildMove
    Purpose: generates string info of build in format --> [ [DX] [H9 SA] ] Human
    Parameters: buildValue, int build Value based on which the build info is generated
    Return Value: void
    Local Variables: buildCards, Vector of Vector where each element, vector stores the card and its value
        the variable is a list of all the buildable cards
    Algorithm:
        1. Get each element and find the pair whose sum would equal to the build
        2. When found add the pair to Build info String
    Assistance Received: none
    ********************************************************************* */
    private void generateStringBuildMove(int buildValue) {

        Vector<Vector<String>> tableCards = CasinoGame.getTableCards();

        //Vector of vector for build
        //Vector of String for both card and its value
        Vector<Vector<String>> buildCards = new Vector<>();

        //store selected hand card and it's value
        Vector<String> tempCard = new Vector<>();
        tempCard.add(CasinoGame.getHandCards(isHuman).get(handPosition).substring(1));
        tempCard.add(CasinoGame.getHandCards(isHuman).get(handPosition));
        buildCards.add(tempCard);

        //store all the selected table cards
        for(int i = 0; i<moveTableInfo.size();i++){ buildCards.add(tableCards.get(Integer.parseInt(moveTableInfo.get(i)))); }

        //sorts the cards in ascending order based on the card value
        //starts building from highest value therefore highest values are removed first
        // in order to prevent removing lower indexed elements before higher ones
        Collections.sort(buildCards,new Comparator<Vector<String>>(){
            // Used for sorting in ascending order of card value
            public int compare(Vector<String> cardA, Vector<String> cardB) {

                //setting the value of card A and B depending upon whether
                //the card is loose card or a build card
                int valueCardA, valueCardB;
                if(cardA.size()>2){ valueCardA = Integer.parseInt(cardA.get(0));}
                else{ valueCardA = CasinoGame.cardStringToValue(cardA.get(0).charAt(0)); }
                if(cardB.size()>2){ valueCardB = Integer.parseInt(cardB.get(0));}
                else{ valueCardB = CasinoGame.cardStringToValue(cardB.get(0).charAt(0)); }

                return valueCardA - valueCardB;
            }
        });

        String contentBuildValueSame = "";
        String contentNewBuild = "";

        while(!buildCards.isEmpty()){

            //get the last card
            tempCard = buildCards.get(buildCards.size() - 1);

            int bigCardValue;
            //check for build card
            if(tempCard.size() > 2){
                //card with build
                bigCardValue = Integer.parseInt(tempCard.get(0));

            } else {
                //loose card
                bigCardValue = CasinoGame.cardStringToValue(tempCard.get(0).charAt(0));
            }

            //loop for the current big valued card
            //adding the same value card to the content
            if(bigCardValue == buildValue) {

                //check for build
                if(tempCard.size()>2){
                    //start of the build
                    contentBuildValueSame += "[";
                    for(int i = 3; i< tempCard.size();i++){
                        //get each card from the build
                        contentBuildValueSame += tempCard.get(i);
                        if(i != tempCard.size() - 1) contentBuildValueSame += " ";
                    }
                    //end of the build
                    contentBuildValueSame += "] ";

                } else {
                    //adding single card
                    contentBuildValueSame += "["+tempCard.get(1)+"] " ;
                }

                //removing the card from the list
                buildCards.remove(buildCards.size() - 1);

            } else{

                //start of build
                contentNewBuild += "[";
                //check for build and add to the content
                if(tempCard.size()>2){
                    //card with build
                    for(int i = 3; i< tempCard.size();i++){
                        contentNewBuild += tempCard.get(i)+" ";
                    }

                } else{
                    contentNewBuild += tempCard.get(1)+" ";
                }

                //second element from the last
                int index = buildCards.size() - 2;
                Vector<String> tempCard2 = new Vector<>();

                //flag for building more than 2 cards
                boolean buildNextLevel = false;
                int sum = bigCardValue;
                while(sum != buildValue){

                    //get the value of the card
                    tempCard2 = buildCards.get(index);
                    int smallCardValue;

                    if(tempCard2.size() > 2){
                        //card with build
                        smallCardValue = Integer.parseInt(tempCard2.get(0));

                    } else {
                        //loose card
                        smallCardValue = CasinoGame.cardStringToValue(tempCard2.get(0).charAt(0));
                    }

                    //adding the values
                    sum += smallCardValue;

                    if(sum > buildValue){
                        //get next element from the back
                        index--;
                        //check for possible error
                        //3, 4 + 5 --> 9 + 3 --> 12 >10 --> sum resets to zero
                        if(!buildNextLevel) { sum = bigCardValue; }
                        else { sum -= smallCardValue;}

                    } else if(sum < buildValue){

                        //check for build and add to the content
                        if(tempCard2.size()>2){
                            //card with build
                            for(int i = 3; i< tempCard2.size();i++){
                                contentNewBuild += tempCard2.get(i)+" ";
                            }

                        } else{
                            contentNewBuild += tempCard2.get(1)+" ";
                        }

                        buildCards.remove(index);
                        buildNextLevel = true;
                    }
                }

                //remove high card and the last low card
                buildCards.remove(buildCards.size() - 1);
                buildCards.remove(index);

                //end of build
                //check for build and add to the content
                if(tempCard2.size()>2){
                    //card with build
                    for(int i = 3; i< tempCard2.size();i++){
                        contentNewBuild += tempCard2.get(i);
                        if(i != tempCard.size() - 1) contentBuildValueSame += " ";
                    }

                } else{
                    contentNewBuild += tempCard2.get(1);
                }

                contentNewBuild +="] ";

            }
        }

        //end of the build as adding the owner of the build
        int indexBracket = contentNewBuild.indexOf("[");
        indexBracket = contentNewBuild.indexOf("[",indexBracket+1);

        if(indexBracket != -1 || !contentBuildValueSame.isEmpty()) {
            //multi build
            contentNewBuild = "[ " + contentNewBuild + contentBuildValueSame + "] ";
        }

        if(CasinoGame.isHumanTurn()) contentNewBuild += "Human";
        else contentNewBuild+= "Computer" ;

        System.out.println("currentBuildMove: "+contentNewBuild);
        currentBuildMove = contentNewBuild;
    }

    //getter for current build move
    public String getCurrentBuildMove(){ return currentBuildMove; }

    //getter for moveTable info
    public Vector<String> getMoveTableInfo(){ return moveTableInfo; }

    //getter for moveType
    public String getMoveType(){ return moveType; }

    //getter for hand position
    public int getHandPosition(){ return handPosition; }

    /* *********************************************************************
    Function Name: listValidCaptureHand
    Purpose: makes a list of all possible Capture moves for a given hand card
    Parameters:
    Return Value: Vector of Vector where each element is the valid move, info of the position of valid cards
    Local Variables: filteredTablePosition, Vector to store valid moves after checking the sum % valueCompare == 0
    Algorithm: Calls recursiveSumChecker() after getting all the possible moves for given hand selection
    Assistance Received: none
    ********************************************************************* */
    private Vector<Vector<String>> listValidCaptureHand(){

        //hand Card and its numeric value
        int handCardTotalValue = -1;

        if(moveType.equals("c")){
            //capture
            String handCard = CasinoGame.getHandCards(isHuman).get(handPosition);
            handCardTotalValue = CasinoGame.cardStringToValue(handCard.charAt(1));

        }
        else if(moveType.equals("b")){
            //build
            //handCardTotalValue should be the valueHandPosition
            String handCard = CasinoGame.getHandCards(isHuman).get(valueHandPosition);
            handCardTotalValue = CasinoGame.cardStringToValue(handCard.charAt(1));
        }


        //table Card
        Vector<Vector<String>> tableCards = CasinoGame.getTableCards();
        Vector<String> filteredTablePosition = new Vector<>();

        //get the list of table cards whose value is equal or less than hand value
        for(int i = 0; i < tableCards.size();i++) {

            //check for build or loose card
            //get the value of the card
            int value;
            if (tableCards.get(i).size() > 2) { value = Integer.parseInt(tableCards.get(i).get(0)); }
            else { value = CasinoGame.cardStringToValue(tableCards.get(i).get(0).charAt(0)); }

            //check if the values are equal or less and add them to a list
            if (value <= handCardTotalValue) { filteredTablePosition.add(""+i); }

        }

        //get all the possible move
        tableCards =  new Vector<>(getPowerSet(filteredTablePosition));
        System.out.println("listCaptureOption "+tableCards);

        Vector<Vector<String>> validCaptureTableCards = new Vector<>();

        //loop to check for valid moves
        for(int i = 0; i < tableCards.size();i++) {
            //set current table info
            this.moveTableInfo = tableCards.get(i);
            if (captureBuildSumChecker(handCardTotalValue)) { validCaptureTableCards.add(tableCards.get(i)); }

        }
        System.out.println("listCaptureOption.valid" + validCaptureTableCards);

        return validCaptureTableCards;
    }

    /* *********************************************************************
    Function Name: getPowerSet
    Purpose: returns power set of all possible combination of table cards for a given hand card
    Parameters: tablePosition, Vector storing the position of selected table cards
    Return Value: Vector of Vector where each element is possible move
    Local Variables:
    Algorithm:
        1. Get the size of power set powet_set_size = pow(2, set_size)
        2. Loop for counter from 0 to pow_set_size
            (a) Loop for i = 0 to set_size
                (i) If ith bit in counter is set ith element from set for this subset

    Assistance Received: none
    ********************************************************************* */
    private Vector<Vector<String>> getPowerSet(Vector<String> tablePosition) {

        Vector<Vector<String>> tableCards = CasinoGame.getTableCards();
        int handCardTotalValue = -1;
        int buildSelectedHandValue = 0;

        if(moveType.equals("c")){
            //capture
            String handCard = CasinoGame.getHandCards(isHuman).get(handPosition);
            handCardTotalValue = CasinoGame.cardStringToValue(handCard.charAt(1));

        }
        else if(moveType.equals("b")){
            //build
            //handCardTotalValue should be the valueHandPosition
            String handCard = CasinoGame.getHandCards(isHuman).get(valueHandPosition);
            handCardTotalValue = CasinoGame.cardStringToValue(handCard.charAt(1));
            handCard = CasinoGame.getHandCards(isHuman).get(handPosition);
            buildSelectedHandValue = CasinoGame.cardStringToValue(handCard.charAt(1));
        }



        Vector<Vector<String>> tablePositionList = new Vector<>();
        int set_size = tablePosition.size();

        //set_size of power set of a set with set_size n is (2^n -1)
        long pow_set_size = (long)Math.pow(2, set_size);

        //Run from counter 000..0 to 111..1
        for(int counter = 0; counter < pow_set_size; counter++)
        {
            Vector<String> tablePositionSet = new Vector<>();
            int sum = 0;
            for(int j = 0; j < set_size; j++)
            {
                //Check if jth bit in the counter is set
                //If set then add jth element from set
                if((counter & (1 << j)) > 0){
                    Vector<String> tempCard = tableCards.get(Integer.parseInt(tablePosition.get(j)));
                    String tableValueString = tempCard.get(0);
                    int tableValue;
                    if(tableValueString.length() == 1) { tableValue = CasinoGame.cardStringToValue(tableValueString.charAt(0)); }
                    else{ tableValue = Integer.parseInt(tableValueString); }

                    sum += tableValue;
                    tablePositionSet.add(tablePosition.get(j));
                }
            }

            //if build also add the value of selected card to the set of table
            if(moveType.equals("b")){sum += buildSelectedHandValue; }

            //only add if sum multiple of handValue
            if(sum % handCardTotalValue == 0) tablePositionList.add(tablePositionSet);
        }
        //removing the first element, the empty set
        if(moveType.equals("c")) tablePositionList.remove(0);
        System.out.print("K cha-->"+tablePositionList);
        return tablePositionList;
    }

    /* *********************************************************************
    Function Name: getBestCaptureHand
    Purpose: calls listValidCaptureHand and checks for each move in the list for highest score or highest number of cards
        for a given hand card
    Parameters:
    Return Value: Vector storing the best capture move
    Local Variables: listValidCaptures, Vector of Vector list of valid moves from listValidCaptures()
    Algorithm:
        1. Get each valid moves and calculate the score and number of cards
        2. Return the move with max score, if same score decide using the number of cards
    Assistance Received: none
    ********************************************************************* */
    private Vector<String> getBestCaptureHand(){
        //get the list of Capture valid moves
        Vector<Vector<String>> listValidCaptures = listValidCaptureHand();

        //if list empty return empty list
        if(listValidCaptures.isEmpty()) return new Vector<>();

        Vector<Vector<String>> tableCards = CasinoGame.getTableCards();

        //get each element from the valid list to calculate score
        //check for empty list since possible to not have a valid
        Vector<String> bestScoreMove = new Vector<>();

        Vector<Integer> listScore = new Vector<>();
        int highScore = 0;

        if(!listValidCaptures.isEmpty()) {

            //getting each valid move from the list
            for(int i = 0; i< listValidCaptures.size();i++){
                bestScoreMove= listValidCaptures.get(i);
                int score = 0;

                System.out.println("getBestCaptureHand-->validMove: "+bestScoreMove);


                //getting each position from single valid move
                for(int j =0 ; j <bestScoreMove.size();j++){

                    //access each table card
                    Vector<String> tableCard = tableCards.get(Integer.parseInt(bestScoreMove.get(j)));

                    System.out.println("getBestCaptureHand--> Card: "+tableCard);

                    //get their values
                    int CardValue;
                    //check for build in the table to access each card and calculate the score
                    if(tableCard.size()>2) { for(int k =0 ; k <tableCard.size();k++) { score += scoreCalculator(tableCard.get(1)); } }
                    else { score += scoreCalculator(tableCard.get(1));}

                }
                //store the score in a vector and also find the highscore
                listScore.add(score);
                if(score>highScore) highScore = score;

            }
        }

        int index = 0;
        int maxNumCards = 0;


        //Check if there are more than one move with the high score
        //if found then find with highest number of cards or highest spade
        Vector<Integer> indexListHighScore = new Vector<>();
        for(int i = 0; i< listScore.size();i++){ if(listScore.get(i).equals(highScore)){ indexListHighScore.add(i); } }
        if(indexListHighScore.size() > 1) {
            //more moves with same high score
            for (int i = 0; i < indexListHighScore.size(); i++) {
                int currentCardNum = listValidCaptures.get(indexListHighScore.get(i)).size();
                if(currentCardNum > maxNumCards) {
                    maxNumCards = currentCardNum;
                    index = indexListHighScore.get(i);
                }
            }
        }
        else{
            index = listScore.indexOf(highScore);
        }

        //get the best move with the index and add score
        Vector<String> bestMoveFinal = listValidCaptures.get(index);
        bestMoveFinal.add(""+highScore);
        System.out.println("getBestCaptureHand--> highScore: "+ highScore + "index: " + index);

        return bestMoveFinal;
    }

    //Check the card for aces and DX and S2 and calculates the score
    private int scoreCalculator(String card){
        int score = 0;
        if( card.equals("CA") || card.equals("DA") || card.equals("SA") || card.equals("HA") || card.equals("S2")){ score = 1;}
        else if(card.equals("DX")){ score = 2;}
        return score;
    }

    /* *********************************************************************
    Function Name: getBestCaptureTotal
    Purpose: gets the best capture move for all the hand position
    Parameters:
    Return Value: Vector storing the best capture move for all hand position
    Local Variables: bestMoveTotalFinal, Vector storing the best move for all hand position
    Algorithm:
        1. Call getBestCaptureHand() for given hand card
        2. Store the moves
        3. Check for the best move from the list based on their score
    Assistance Received: none
    ********************************************************************* */
    public Vector<String> getBestCaptureTotal(){

        Vector<String> handCard = CasinoGame.getHandCards(isHuman);

        Vector<String> bestMoveTotalFinal = new Vector<>();

        //max score and size given hand card
        int[] maxNumScore = {0,0};

        for(int i = 0 ; i < handCard.size(); i++){

            //get the best capture move for given hand
            this.handPosition = i;
            Vector<String> tempBestMoveTotalFinal = getBestCaptureHand();

            //ignore the empty lists
            if(getBestCaptureHand().isEmpty()) continue;

            System.out.println("getBestCaptureTotal-->"+tempBestMoveTotalFinal);

            int score = Integer.parseInt(tempBestMoveTotalFinal.get(tempBestMoveTotalFinal.size() - 1));
            tempBestMoveTotalFinal.remove(tempBestMoveTotalFinal.size() - 1);

            int numCards = 0;

            Vector<Vector<String>> tableCards = CasinoGame.getTableCards();
            //size should be the total number of cards1
            for(int j = 0; j<tempBestMoveTotalFinal.size();j++){
                Vector<String> card = tableCards.get(Integer.parseInt(tempBestMoveTotalFinal.get(j)));
                System.out.println("kina1__"+card);
                //check for build and count all the cards
                if(card.size()>2) {
                    for(int k = 3; k < card.size();k++){
                        if(card.get(k)!="[" && card.get(k)!="]") numCards ++;
                        System.out.println(numCards);
                    }
                }
                else{ numCards++;}
            }
            System.out.println("kina__"+tempBestMoveTotalFinal);


            if(score > maxNumScore[0]){
                maxNumScore[0] = score;
                maxNumScore[1] = numCards;
                tempBestMoveTotalFinal.add(""+i);
                bestMoveTotalFinal = tempBestMoveTotalFinal;

            } else if(score == maxNumScore[0] && numCards > maxNumScore[1]){
                maxNumScore[1] = numCards;
                tempBestMoveTotalFinal.add(""+i);
                bestMoveTotalFinal = tempBestMoveTotalFinal;
            }
            System.out.println("getBestCaptureTotal-->"+bestMoveTotalFinal);

        }


        return bestMoveTotalFinal;
    }

     /* *********************************************************************
    Function Name: getBestBuildTotal
    Purpose: gets the best build move for all the hand position and call generateStringBuildMove()
        to stores the move as String in currentBuildMove
    Parameters:
    Return Value: void
    Local Variables:
    Algorithm:
        1. Call getBestCaptureHand() for given hand card
        2. Store the moves
        3. Check for the best move from the list based on their score
    Assistance Received: none
    ********************************************************************* */
    public void getBestBuildTotal(){

        //check for size of hand if less than 2 no way to make a build
        if(CasinoGame.getHandCards(isHuman).size() < 2 ) return;

        Vector<String> hand = new Vector<>(CasinoGame.getHandCards(isHuman));
        Vector<Vector<String>> handValueDescend = new Vector<>();

        //adding the values and index of each hand card
        for(int i =0 ; i<hand.size();i++ ){
            Vector<String> temp = new Vector<>();
            temp.add(""+CasinoGame.cardStringToValue(hand.get(i).charAt(1)));
            temp.add(""+i);
            handValueDescend.add(temp);
        }

        //Sort descending order
        Collections.sort(handValueDescend, new Comparator<Vector<String>>() {
            @Override
            public int compare(Vector<String> o1, Vector<String> o2) { return Integer.parseInt(o1.get(0)) - Integer.parseInt(o2.get(0)); } });

        Vector<String> bestMoveTable = new Vector<>();
        int bestMoveSelectedHand = 0;
        int bestMoveBuildHand = 0;
        int score = 0;

        while(handValueDescend.size() != 1 ){

            //set the hand position to current
            this.handPosition = Integer.parseInt(handValueDescend.get(0).get(1));
            handValueDescend.remove(0);

            for(int j =0; j < handValueDescend.size();j++){
                this.valueHandPosition = Integer.parseInt(handValueDescend.get(j).get(1));
                Vector<String> temp = getBestCaptureHand();
                if(!temp.isEmpty() && score <= Integer.parseInt(temp.get(temp.size()-1))){
                    bestMoveTable = temp;
                    temp.remove( temp.size()-1);
                    bestMoveSelectedHand = handPosition;
                    bestMoveBuildHand = CasinoGame.cardStringToValue(hand.get(valueHandPosition).charAt(1));
                }
            }
        }

        System.out.println("SB"+bestMoveBuildHand+bestMoveTable+bestMoveSelectedHand);

        if(!bestMoveTable.isEmpty()) {
            handPosition = bestMoveSelectedHand;
            moveTableInfo = bestMoveTable;
            generateStringBuildMove(bestMoveBuildHand);
            System.out.println("Starting Build-->"+currentBuildMove);
        }

    }

    //makes a list of all possible Build moves for a given hand card
    public void listBuildOption(){

    }


}
