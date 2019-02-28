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
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.io.File;
import java.util.Date;
import java.util.Random;

public class WelcomeActivity extends AppCompatActivity {

    //the local variable
    Button buttonTemp;

    //required when loading file
    //introduced when adding delete option
    boolean isLoad;
    TextView display;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //inspect bundle from previous activity to load either new game or load game
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        //display the text
        if(intent.hasExtra("loadSaveFile")){
            //getting the info from prev Activity via bundle
            String loadSaveStatus = bundle.getString("loadSaveFile");
            display.setText(loadSaveStatus);
        }

        //onclick listener to newGame button
        buttonTemp = findViewById(R.id.newGame);
        buttonTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //what the button does on click
                inputCoinValue();
            }
        });

        //onclick listener to load game button
        buttonTemp = findViewById(R.id.loadGame);
        buttonTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get File name and end Activity
                inputFileName();
            }
        });

        //onclick listener to tutorial button
        buttonTemp = findViewById(R.id.tutorial);
        buttonTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tutorial();
            }
        });

        //display info
        display = findViewById(R.id.displayWelcome);

    }

    /* *********************************************************************
    Function Name: inputCoinValue
    Purpose: gets user selection head or tail, compares it to random generated value between
    Parameters:
    Return Value: void
    Local Variables:
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public void inputCoinValue(){
        //Dialog
        final Dialog d = new Dialog(WelcomeActivity.this);
        d.setTitle("Choose a side");
        d.setContentView(R.layout.cointoss_playerturn);

        //Random generate 1 and 2
        //1 --> head and  2 --> tail
        Random rand = new Random();
        final int turn = rand.nextInt(2) + 1;

        //textView
        final TextView t1 = d.findViewById(R.id.statusPrint);

        //buttons tail and head
        final Button tail = d.findViewById(R.id.coinTail);
        final Button head = d.findViewById(R.id.coinHead);

        //on click listener to head button
        head.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                //restrict tail button selection
                tail.setClickable(false);

                //ends activity passing the file name
                if(turn == 1){
                    t1.setText("Congrats it is head. Human's turn.");
                    tail.setBackgroundResource(R.drawable.black);
                    endActivity("newGame","true");
                }
                else {
                    t1.setText("Sorry it is tail. Computer's turn.");
                    head.setBackgroundResource(R.drawable.black);
                    endActivity("newGame","false");
                }
            }
        });

        //on click listener to tail button
        tail.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                //restrict right button selection
                head.setClickable(false);

                //ends activity passing the file name
                if(turn == 2){
                    t1.setText("Congrats it is tail. Human's turn.");
                    head.setBackgroundResource(R.drawable.black);
                    endActivity("newGame","true");
                }
                else {
                    t1.setText("Sorry it is head. Computer's turn.");
                    tail.setBackgroundResource(R.drawable.black);
                    endActivity("newGame","false");
                }
            }
        });


        d.show();
    }

    /* *********************************************************************
    Function Name: inputFileName
    Purpose: called when load button pressed, dialog for user input file name
    Parameters:
    Return Value: void
    Local Variables:
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public void inputFileName(){

        //set flag to true as default
        isLoad = true;

        //local variable for Dialog
        final Dialog d = new Dialog(WelcomeActivity.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_loadgame);

        //local variable for delete and load button layout
        final LinearLayout deleteLoadButton = d.findViewById(R.id.deleteLoadButton);

        //save button
        buttonTemp = new Button(this);
        createDeleteLoadButton("Load");
        deleteLoadButton.addView(buttonTemp);

        //delete button
        buttonTemp = new Button(this);
        createDeleteLoadButton("Delete");
        deleteLoadButton.addView(buttonTemp);

        //get the list of files from the document
        final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        File f = new File(path);
        File files[] = f.listFiles();

        //table layout
        TableLayout table = d.findViewById(R.id.loadGameList);

        //get the number of files
        int count = 0;

        //loop accessing all files in file[]
        for (final File file : files) {

            //horizontal linear layout to store button and text view
            LinearLayout llayout = new LinearLayout(this);
            llayout.setOrientation(LinearLayout.HORIZONTAL);

            //edit the button and get filename
            final String fileName = path+"/"+file.getName();
            buttonTemp = new Button(this);
            buttonTemp.setText(file.getName());
            buttonTemp.setTextSize(23);
            buttonTemp.setLayoutParams(new LinearLayout.LayoutParams(300, 50));

            //get the date modified in string
            String dateModified = new Date(file.lastModified()).toString();

            //display the date modified
            TextView dateText = new TextView(this);
            dateText.setText(""+dateModified);
            dateText.setTextSize(23);

            //Button
            final Button b1 = buttonTemp;
            //listener ending the activity while passing the filename
            b1.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v) {

                    if(isLoad) {

                        //ends activity passing the file name
                        endActivity(fileName, "");
                        display.setText(file.getName()+" loading...");
                        //close the dialog
                        d.dismiss();

                    } else{

                        //delete the file
                        file.delete();
                        display.setText(file.getName()+" delete success!!!");
                        //close the dialog
                        d.dismiss();
                    }
                }
            });


            //adding view
            llayout.addView(b1);
            llayout.addView(dateText);

            //add view
            table.addView(llayout);

            //increment table height multiple
            count++;
        }

        //Set alignments and margin of table layout
        TableRow.LayoutParams trTable = new TableRow.LayoutParams();
        trTable.height = 60*count;
        table.setLayoutParams(trTable);

        d.show();
    }

    //called by inputFileName()
    //create delete and load button and required edit
    public void createDeleteLoadButton(final String type){

        buttonTemp.setText(type);
        buttonTemp.setTextSize(18);
        buttonTemp.setLayoutParams(new LinearLayout.LayoutParams(80, 40));
        buttonTemp.setTextSize(18);
        buttonTemp.setBackgroundResource(R.drawable.action);
        buttonTemp.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {

                //Check condition for Load and Delete Button
                if(!isLoad & type.equals("Load")){ isLoad = true; }
                else if(isLoad & type.equals("Delete")){ isLoad = false; }

            }
        });
    }

    /* *********************************************************************
    Function Name: endActivity
    Purpose: Ends activity and passes Extra to Game Activity,Extra includes information for fileName and human turn & new game or load game
    Parameters: gameName, String fileName or new game
            HumanTurn, String player turn based on coin toss
    Return Value: void
    Local Variables:
    Algorithm:
    Assistance Received: none
    ********************************************************************* */
    public void endActivity(String gameName, String HumanTurn){
        final Intent intent = new Intent(this, GameActivity.class);
        //Sending data to result activity
        intent.putExtra("fileName",gameName);
        intent.putExtra("humanTurn",HumanTurn);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        }, 1500);

    }

    //Ends activity and opens Tutorial Activity
    public void tutorial(){
        final Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }



}
