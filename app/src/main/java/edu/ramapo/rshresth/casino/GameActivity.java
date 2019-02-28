/*
 ************************************************************
 * Name:  Rojan Shrestha                                    *
 * Project:  Casino_Project3					            *
 * Class:  CMPS 366 01				                        *
 * Date:  Dec 14th, 2019			                        *
 ************************************************************
 */

package edu.ramapo.rshresth.casino;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.Vector;

public class GameActivity extends AppCompatActivity implements View.OnClickListener{

    //member Variables
    //made static to allow access to MoveCheck
    static Game CasinoGame;

    //Vector to store moves
    Vector<String> movesStorage;
    //Array to store string id of action buttons and button ids of table buttons and hand buttons
    public static final String actionButtonsName [] = {"actionTrail","actionCapture",
            "actionBuild","actionReset","turnMove","actionHelp","pileHuman","pileComputer"};

    //control step by step
    static boolean actionSelected;
    static boolean handCardSelected;

    //did this cause of java. out of Memory
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, WelcomeActivity.class));
    }

    //runs the function at the start of the Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //inspect bundle from previous activity to load either new game or load game
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //getting the info from prev Activity via bundle
        String gameStatus = bundle.getString("fileName");
        String humanTurnString = bundle.getString("humanTurn");

        //flags move steps set to false
        actionSelected = false;
        handCardSelected = false;

        movesStorage = new Vector<>();

        //On Click listener to save button separately cause the background is different than rest of the button
        findViewById(R.id.save).setOnClickListener(this);

        //Setting up on click listener to all the buttons not related to card dealing
        for(int i= 0; i<actionButtonsName.length ; i++) { restActionButton(actionButtonsName[i],true);}


        //load new game
        if (gameStatus.equals("newGame")) {

            CasinoGame = new Game(humanTurnString);

            //trial
            //CasinoGame = new Game("true");

            //get respective backgrounds of Table and Human buttons
            //Also adds onClickListener since both
            // startTableListener amd startHandListener are false
            resetScreen();

        }
        else {

            //new game initialization
            CasinoGame = new Game();

            //call the function with argument as filename
            if(!CasinoGame.loadGame(gameStatus)){

                //failed to load the game then go back to WelcomeActivity

                //new intent
                intent = new Intent(this, WelcomeActivity.class);
                //Sending data to Welcome activity
                intent.putExtra("LoadFailed...\n File" + gameStatus+" load fail", "loadSaveFile");
                startActivity(intent);

            }
            else {

                //load game success
                //get respective backgrounds of Table and Human buttons
                //Also adds onClickListener since both
                // startTableListener and startHandListener are false
                resetScreen();

                //make computer move
                //if(!CasinoGame.isHumanTurn()) {computerMoveDisplay();}
            }
            TextView displayInfo = findViewById(R.id.announcement1);
            displayInfo.setText("LoadComplete...\n File "+gameStatus+" load successful!!");

        }
    }

    //onClick function for all the buttons (Action, Hand Card and Table Card/s)
    @Override
    public void onClick(View view) {

        //get a local variable of the TextView to display info usually when buttons are pressed
        TextView displayInfo = findViewById(R.id.announcement1);

        //get a local variable of the selected button
        Button buttonSelected = (Button) view;

        //Check if its human turn
        //to restrict action from buttons
        //button selection restriction solely depends on this condition
        if(view.getId() == R.id.save ){

            //save button
            //just to reset any selection
            resetScreen();
            displayInfo.setText("Game Saving....");
            inputFileName();
            //displayInfo.

        } else if(view.getId() == R.id.pileHuman) {

            //pile button to display the pile
            resetScreen();
            displayPile(true);


        }  else if(view.getId() == R.id.pileComputer) {

            //pile button to display the pile
            resetScreen();
            displayPile(false);


        } else if(CasinoGame.isHumanTurn()) {

            //get the id of the black.img in drawable to use it as background when the action buttons are pressed
            int resIdBlackBackground = getResources().getIdentifier( "action_selected", "drawable", getPackageName());

            //check for the Move button
            if (view.getId() == R.id.turnMove) {

                //Check if action and hand are selected and (at least one of the cards in the table is selected or
                //the action button trial was pressed)
                if(actionSelected && handCardSelected &&
                        (movesStorage.get(0).equals("t")|| movesStorage.size() > 2)) {

                    /*****
                     * Entry point to the Game.java after the confirmation from user to make move
                     * *****/

                    String moveFeedback = CasinoGame.makeHumanMove(movesStorage);
                    displayInfo.setText(moveFeedback);

                    if(moveFeedback.charAt(0) =='s'){
                        //move successful

                        //change the turn after successful move
                        CasinoGame.changeTurn(false);

                    }

                    //resets the screen
                    resetScreen();

                    //was necessary cause the computer's turn was not being shown on textView
                    //probably cause of timer or since text
                    displayTurn();

                    //Makes Computer move
                    //computerMoveDisplay();

                } else {

                    //unsuccessful move

                    //display the error based on selection of action and hand cards
                    if(!actionSelected) {

                        //error selecting action button
                        displayInfo.setText("0. "+errorMessage(1));
                    }
                    else if(!handCardSelected){

                        //error selecting hand button
                        displayInfo.setText("0. "+errorMessage(2));
                    }
                    else {

                        //error selecting hand button
                        displayInfo.setText("0. "+errorMessage(3));
                    }
                }

            } else if(view.getId() == R.id.actionReset){

                //Reset Button

                //to reset the background of the selected cards
                resetScreen();

                displayInfo.setText("Selection Reset Successful !!!");


            } else if (view.getId() == R.id.actionHelp) {
                //Help action Button

                MoveCheck moveCheck = new MoveCheck("c");
                Vector<String> bestCaptureMove = moveCheck.getBestCaptureTotal();

                moveCheck = new MoveCheck("b");
                moveCheck.getBestBuildTotal();
                String bestBuildMove = moveCheck.getCurrentBuildMove();

                //trial to display
                displayInfo.setText("Help: Capture: "+bestCaptureMove+ " \n Build: "+ bestBuildMove);

                //suggestion: make help button a spinner
                //options include getting move suggestions or opening tutorial page

                //resets the selection
                resetScreen();


            } else if (!actionSelected && !handCardSelected) {

                //Checks for Action Buttons
                //Action being the first step of a move checks the flags action and hand
                //--> both false

                //checks for action Trail
                if (view.getId() == R.id.actionTrail) {

                    //set the background of the buttons with the respective cards
                    buttonSelected.setBackgroundResource(resIdBlackBackground);

                    displayInfo.setText("Trail");

                    //add code of trial to the move storage vector
                    movesStorage.add("t");
                    //flag true for selection of the action
                    actionSelected = true;

                } else if (view.getId() == R.id.actionCapture) {

                    //action Capture

                    //set the background of the buttons with the respective cards
                    buttonSelected.setBackgroundResource(resIdBlackBackground);

                    displayInfo.setText("Capture");

                    //add code of capture to the move storage vector
                    movesStorage.add("c");
                    //flag true for selection of the action
                    actionSelected = true;

                } else if (view.getId() == R.id.actionBuild) {

                    //action Build

                    //set the background of the buttons with the respective cards
                    buttonSelected.setBackgroundResource(resIdBlackBackground);

                    displayInfo.setText("Build");

                    //add code of build to the move storage vector
                    movesStorage.add("b");
                    //flag true for selection of the action
                    actionSelected = true;

                }else{

                    //Error --> Table or hand buttons selected
                    //It means no action buttons were pressed
                    //error for action button
                    displayInfo.setText("1. "+errorMessage(1));

                }

            } else if (actionSelected) {

                //only after one of the action buttons is pressed

                //get the name of the button to differentiate between table and human hand selection
                String buttonText = buttonSelected.getText().toString();

                //check if it card from hand is not selected and
                //if the button is hand
                if (!handCardSelected && buttonText.charAt(0) == 'h') {

                    //change the card background after selection
                    highlightCards(buttonText);
                    //Hand Card Selection
                    displayInfo.setText("HandHuman" + buttonText);

                    //add the index of the hand, the same index can be used to
                    // access the hand element at Game.java
                    movesStorage.add("" + buttonText.charAt(1));
                    //true the flag since hand card was successfully selected
                    handCardSelected = true;

                } else if (handCardSelected && buttonText.charAt(0) == 't' && !movesStorage.get(0).equals("t")) {

                    //check if hand card was selected, button selected is from table and
                    //if the action button is not trial (since trial does not need table cards)

                    //button highlight
                    highlightCards(buttonText);

                    //display the table position of the selected table card
                    displayInfo.setText("" + buttonText);

                    //remove the first char 't' and add the move list
                    buttonText = buttonText.substring(1);

                    //check for build
                    if (buttonSelected.getText().toString().charAt(0) == 't') {
                        //check for table button then check for build card
                        //display the info of the selected build card

                        Vector<String> selectedCard = CasinoGame.getTableCards().get(Integer.parseInt(buttonText));
                        if (selectedCard.size() > 2) {
                            //display the info of the selected table card
                            displayInfo.setText("t" +buttonText+" build: "+selectedCard);

                        }
                    }
                    movesStorage.add(buttonText);

                    //remove onClick listener to prevent clicking the same Table button more than once
                    //in one move session
                    buttonSelected.setOnClickListener(null);

                } else {

                    //error
                    //Check if hand card is the required selection
                    if (!handCardSelected) {
                        //error for hand card selection
                        displayInfo.setText("2. " + errorMessage(2));
                    } else {
                        //since actionSelected is true and hand already selected
                        //next button should be table buttons
                        displayInfo.setText("2. " + errorMessage(3));
                    }
                }

            } else{

                //Error
                displayInfo.setText("2. "+errorMessage(4));
            }

        } else{

            //Computer's turn

            if (view.getId() == R.id.turnMove) {

                //was necessary cause the computer's turn was not being shown on textView
                //probably cause of timer or since text
                displayTurn();

                displayInfo.setText("COMPUTER IS MAKING MOVE, PLEASE WAIT 3 SECONDS...");

                //make comp move
                computerMoveDisplay();

            }

        }
    }

    /* *********************************************************************
    Function Name: restActionButton
    Purpose: sets the default background of the action buttons
    Parameters: ButtonName, String name of the button
        onClickListenerSet, boolean flag to decide if to set on click listener the given button
    Return Value: void
    Local Variables: buttonTemp, Button the given button
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public void restActionButton(String ButtonName, boolean onClickListenerSet){

        Button buttonTemp = findViewById(getResources().getIdentifier(ButtonName,"id",getPackageName()));;

        //onClickListener
        if(onClickListenerSet){ buttonTemp.setOnClickListener(this);}

        int resId = getResources().getIdentifier( "action", "drawable", getPackageName());
        buttonTemp.setBackgroundResource(resId);
    }

    /* *********************************************************************
    Function Name: updateTableHands
    Purpose: updates the background of the table cards
    Parameters:
    Return Value: void
    Local Variables:
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public void updateTableHands(){

        //set respective background to the table cards
        Vector<Vector<String>> tableCards = CasinoGame.getTableCards();

        Button[] buttonTable = {findViewById(R.id.t0),
                findViewById(R.id.t1),
                findViewById(R.id.t2),
                findViewById(R.id.t3),
                findViewById(R.id.t4),
                findViewById(R.id.t5),
                findViewById(R.id.t6),
                findViewById(R.id.t7),
                findViewById(R.id.t8),
                findViewById(R.id.t9),
                findViewById(R.id.t10),
                findViewById(R.id.t11)};

        //index to access the element of the table vector
        int index = 0;

        //loop to create and set the background of the cards
        for(int j = 0; j< buttonTable.length ;j++) {

                //only set the background of the cards that are on table board
                if(index < tableCards.size()) {

                    //set action listener first time
                    buttonTable[j].setOnClickListener(this);
                    buttonTable[j].setText("t"+j);


                    if(tableCards.get(index).size() > 2) {

                        //CHECK IF THE TABLE HAS BUILD

                        //handle the background of the build cards
                        //convert the info to lowercase cause of naming convention of resource files
                        String imageName = "card_build_" + tableCards.get(index).get(0).toLowerCase();
                        int resId = getResources().getIdentifier("" + imageName, "drawable", getPackageName());
                        buttonTable[index++].setBackgroundResource(resId);

                        //need to make the background images

                    }
                    else {
                        //set the background of the buttons with the respective cards
                        String imageName = "card_" + tableCards.get(index).get(1).toLowerCase();
                        int resId = getResources().getIdentifier("" + imageName, "drawable", getPackageName());

                        //button[i].setText(imageName);
                        buttonTable[index++].setBackgroundResource(resId);
                    }

                } else {

                    //set action listener first time
                    buttonTable[j].setOnClickListener(null);
                    buttonTable[j].setText("t"+j);

                    int resId = getResources().getIdentifier("card_empty", "drawable", getPackageName());
                    buttonTable[j].setBackgroundResource(resId);


                }

        }
    }

    /* *********************************************************************
    Function Name: updateHumanHands
    Purpose: updates the background of the human hand
    Parameters:
    Return Value: void
    Local Variables:
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public void updateHumanHands(){

        Button[] buttonHand = {findViewById(R.id.h0),
                findViewById(R.id.h1),
                findViewById(R.id.h2),
                findViewById(R.id.h3)};

        //set respective background to the human cards
        Vector<String> humanHand = CasinoGame.getHandCards(true);

        for(int j = 0; j< buttonHand.length;j++) {

            if(j < humanHand.size()) {

                buttonHand[j].setOnClickListener(this);
                buttonHand[j].setText("h"+j);


                //set the background of the buttons with the respective cards
                String imageName = "card_" + humanHand.get(j).toLowerCase();
                int resId = getResources().getIdentifier("" + imageName, "drawable", getPackageName());
                buttonHand[j].setBackgroundResource(resId);

            } else {

                //set action listener null
                buttonHand[j].setOnClickListener(null);
                buttonHand[j].setText("h"+j);

                int resId = getResources().getIdentifier("card_empty", "drawable", getPackageName());
                buttonHand[j].setBackgroundResource(resId);

            }
        }
    }

    /* *********************************************************************
    Function Name: updateComputerHands
    Purpose: updates the background of the computer hand
    Parameters:
    Return Value: void
    Local Variables:
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public void updateComputerHands(){

        Button[] buttonHand = {findViewById(R.id.c0),
                findViewById(R.id.c1),
                findViewById(R.id.c2),
                findViewById(R.id.c3)};

        //set respective background to the human cards
        Vector<String> computerHand = CasinoGame.getHandCards(false);

        for(int j = 0; j< 4;j++) {

            if(j < computerHand.size()) {

                //set the background of the buttons with the respective cards
                String imageName = "card_" + computerHand.get(j).toLowerCase();
                int resId = getResources().getIdentifier("" + imageName, "drawable", getPackageName());
                buttonHand[j].setBackgroundResource(resId);

            } else {

                int resId = getResources().getIdentifier("card_empty", "drawable", getPackageName());
                buttonHand[j].setBackgroundResource(resId);

            }
        }
    }

    /* *********************************************************************
    Function Name: displayTurn
    Purpose: prints the turn in the TextView
    Parameters:
    Return Value: void
    Local Variables:
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public void displayTurn(){
        //Displaying the turn of the player
        Button displayTurn = findViewById(R.id.turnMove);
        if(CasinoGame.isHumanTurn()) {displayTurn.setText("Human's Move");}
        else{ displayTurn.setText("Computer's Move"); }
    }

    /* *********************************************************************
    Function Name: resetScreen
    Purpose: reset moves color and selection
    Parameters:
    Return Value: void
    Local Variables:
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public void resetScreen(){

        //updates the background of human hands and table
        updateHumanHands();
        updateTableHands();
        updateComputerHands();

        //reset the selection of the action buttons
        //action buttons --> first 3 elements of actionButtons[] --> trail, capture and build
        for(int i= 0; i<3 ; i++) { restActionButton(actionButtonsName[i],false);}
        movesStorage.clear();

        //display the turn of the player
        displayTurn();

        //flags reset
        actionSelected = false;
        handCardSelected = false;
    }

    /* *********************************************************************
    Function Name: highlightCards
    Purpose: change the background of the selected card, associated buttons from table and hand
    Parameters: buttonName, String the name of the button
    Return Value: void
    Local Variables:
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public void highlightCards(String buttonName) {

        //used substring instead of char.at cause table could have more than 2 characters as index
        int index = Integer.parseInt(buttonName.substring(1));

        //store selected button in the local variable
        Button buttonTemp = findViewById(getResources().getIdentifier(buttonName,"id",getPackageName()));;
        //filename of the image file in @drawable
        String imageName;

        //check if the buttons are from table
        if(buttonName.charAt(0) ==  't')
        {
            //table card

            //get the vector of cards from the Game.java
            Vector<Vector<String>> tableCards = CasinoGame.getTableCards();

            //table card with build vs normal card
            //since build card has more than 2 elements
            if(tableCards.get(index).size() > 2) { imageName = "card_build_" + tableCards.get(index).get(0).toLowerCase() + "_selected"; }
            else{ imageName = "card_" + tableCards.get(index).get(1).toLowerCase()+"_selected"; }

        } else {

            //human hand card

            //set respective background to the human cards
            Vector<String> humanHand = CasinoGame.getHandCards(true);
            imageName = "card_" + humanHand.get(index).toLowerCase()+"_selected";

        }

        int resId = getResources().getIdentifier("" + imageName, "drawable", getPackageName());
        buttonTemp.setBackgroundResource(resId);
    }

    /* *********************************************************************
    Function Name: moveAfterMessage
    Purpose: return the error message respective to the error code
    Parameters: i, int key for a given error prompt
    Return Value: String, error prompt for given index
    Local Variables: msg, String
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public String errorMessage(int i){
        String errorMsg = new String();
        //check the error code
        switch(i){
            case 1: errorMsg = "Please select action button first(Trial/Capture/Build) \nAND follow the correct selection steps: \nAction-> Hand-> Table(only in build and capture)-> Move";
            break;
            case 2 : errorMsg = "Please select a hand card \nAND follow the correct selection steps: \nAction-> Hand-> Table(only in build/capture)-> Move";
            break;
            case 3: errorMsg = "Please select card/s from the table \nAND follow the correct selection steps: \nAction-> Hand-> Table(only in build and capture)-> Move";
            break;
            case 4: errorMsg = "Please press move button to make the move \nOR Press Reset for new Selection \nOR Help for help options";
            break;
        }
        return errorMsg;
    }

    /* *********************************************************************
    Function Name: computerMoveDisplay
    Purpose: calls computerMove from Game.Java and displays the changes
    Parameters:
    Return Value: void
    Local Variables: moveMsg, String to store the status of move operation, success or failure
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public void computerMoveDisplay(){

        String moveMsg = CasinoGame.computerMove();

        if(moveMsg.charAt(0) == 's'){
            //successful move

            final String moveMsgTemp = moveMsg.substring(8);

            //delay the computer move helping user to see the moves step by step
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {

                            //UI - thread to make changes to the screen at the end of the run
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    //to display the changes made after successful computer move
                                    resetScreen();

                                    //display that the move was successful
                                    TextView displayInfo = findViewById(R.id.announcement1);

                                    displayInfo.setText("Computer moved: "+ moveMsgTemp);
                                }
                            });

                            //Change the turn to human after computer move completion
                            CasinoGame.changeTurn(true);
                        }
                    },2000
            );

        } else {

            //unsuccessful move

        }
    }

    /* *********************************************************************
    Function Name: inputFileName
    Purpose: called when save button pressed, dialog for user input file name
    Parameters:
    Return Value: void
    Local Variables:
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public void inputFileName(){

        //Dialog box
        final Dialog d = new Dialog(GameActivity.this);
        d.setTitle("Save Game");
        d.setContentView(R.layout.dialog_savegame);

        //EditText to input the filename, final since using inside onClickListener
        final EditText e1 = d.findViewById(R.id.userInputFile);

        //OK button
        Button b1 = d.findViewById(R.id.okSave);

        //since using the same button to save and load
        //changed the text value
        b1.setText("Save");

        final Intent intent = new Intent(this, WelcomeActivity.class);

        //on click listener to OK button
        b1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {

                //display failed or not
                TextView displayInfo = findViewById(R.id.announcement1);

                //Checks if save was succesful to and if true
                //ends the activity while passing the file name to Welcome Activity
                if(CasinoGame.saveGame(""+e1.getText())){
                    startActivity(intent);

                    //close the screen/end the activity
                    //Sending data to as intent
                    intent.putExtra("SaveComplete...\n Game saved as " + e1.getText() + ".txt","loadSaveFile");

                }
                else{

                    displayInfo.setText("SaveFailure," + e1.getText() + ".txt was not saved");

                }

                //close the dialog
                d.dismiss();

            }
        });

        d.show();

        ;
    }

    //dialog for user input file name
    //Ends Activity with file name and calls Game Activity
    //returns void and no parameter
    /* *********************************************************************
    Function Name: displayPile
    Purpose: called when pile button pressed, dialog to display player cards in pile
    Parameters:isHumanPile, boolean flag to decide the player --> true for human
    Return Value: void
    Local Variables:
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public void displayPile(boolean isHumanPile) {

        //Dialog
        final Dialog d = new Dialog(GameActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_pile);

        //check turn and set local variable to the pile of required player
        Vector<String> pile = CasinoGame.getPileCards(isHumanPile);

        TableLayout table = d.findViewById(R.id.pileDisplay);
        TableRow.LayoutParams trTable = new TableRow.LayoutParams();
        trTable.width = 750;
        int height = 160 * (pile.size() / 6 + 1)+40;
        trTable.height = height;

        TableRow row = new TableRow(this);

        for (int j = 0; j < pile.size(); j++) {

            if (j != 0 && j % 6 == 0) {
                //add the row to the table and create new table row
                table.addView(row);
                row = new TableRow(this);

            }

            TextView card = new TextView(this);
            //Layout to set the width and height of the card
            TableRow.LayoutParams tr = new TableRow.LayoutParams();
            tr.height = 150;
            tr.width = 100;
            tr.setMargins(20, 20, 0, 0);

            card.setLayoutParams(tr);

            String imageName = "card_" + pile.get(j).toLowerCase();
            int resId = getResources().getIdentifier("" + imageName, "drawable", getPackageName());
            card.setBackgroundResource(resId);

            row.addView(card);

        }
        table.addView(row);
        table.setLayoutParams(trTable);



        //OK button
        Button b1 = d.findViewById(R.id.ok);
        //on click listener to OK button
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //close the dialog
                d.dismiss();
            }
        });

        d.show();
    }

}
