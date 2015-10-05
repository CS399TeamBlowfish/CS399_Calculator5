package com.android.cs399_calculator5;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;


public class MainActivity extends AppCompatActivity {
    // IDs of all the numeric buttons
    private int[] numericButtons = {R.id.btnZero, R.id.btnOne, R.id.btnTwo, R.id.btnThree, R.id.btnFour, R.id.btnFive, R.id.btnSix, R.id.btnSeven, R.id.btnEight, R.id.btnNine};
    // IDs of all the operator buttons
    private int[] operatorButtons = {R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide};
    // TextView used to display the output
    private TextView txtScreen;
    // TextView used to display the precalculated results
    private TextView txtPreResults;
    // Represent whether the lastly pressed key is numeric or not
    private boolean lastNumeric;
    // Represent that current state is in error or not
    private boolean stateError;
    // If true, do not allow to add another DOT
    private boolean lastDot;
    //
    private boolean lastLeftParen;
    //
    private boolean lastParen;
    //
    private int leftParenCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find the TextView
        this.txtScreen = (TextView) findViewById(R.id.formula);
        // Find the TextView
        this.txtPreResults = (TextView) findViewById(R.id.result);
        // Find and set OnClickListener to numeric buttons
        setNumericOnClickListener();
        // Find and set OnClickListener to operator buttons, equal button and decimal point button
        setOperatorOnClickListener();
    }

    /**
     * Find and set OnClickListener to numeric buttons.
     */
    private void setNumericOnClickListener() {
        // Create a common OnClickListener
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Just append/set the text of clicked button
                Button button = (Button) v;
                if (stateError) {
                    // If current state is Error, replace the error message
                    txtScreen.setText(button.getText());
                    stateError = false;
                } else {
                    // If not, already there is a valid expression so append to it
                    txtScreen.append(button.getText());
                }
                // Set the flag
                lastNumeric = true;
                lastLeftParen = false;
            }
        };
        // Assign the listener to all the numeric buttons
        for (int id : numericButtons) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    /**
     * Find and set OnClickListener to operator buttons, equal button and decimal point button.
     */
    private void setOperatorOnClickListener() {
        // Create a common OnClickListener for operators
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the current state is Error do not append the operator
                // If the last input is number only, append the operator
                if (lastNumeric && !stateError) {
                    Button button = (Button) v;
                    txtScreen.append(button.getText());
                    lastNumeric = false;
                    lastLeftParen = false;
                    lastDot = false;    // Reset the DOT flag
                }
            }
        };
        // Assign the listener to all the operator buttons
        for (int id : operatorButtons) {
            findViewById(id).setOnClickListener(listener);
        }
        // Parenthesis Button
        // R.id.btnParen
        findViewById(R.id.btnParen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastNumeric && !stateError && leftParenCount>0 && !lastLeftParen ) {
                    txtScreen.append(")");
                    leftParenCount--;
                }else if (lastNumeric && !stateError && leftParenCount==0){
                    txtScreen.append("*(");
                    leftParenCount++;
                    lastLeftParen = true;
                }else if (lastLeftParen && !stateError){
                    txtScreen.append("(");
                    leftParenCount++;
                    lastLeftParen = true;
                }else if (!lastNumeric && !stateError) {
                    txtScreen.append("(");
                    lastLeftParen = true;
                    leftParenCount++;
                }
                lastNumeric = false;
                lastParen = true;
            }
        });
        // Decimal point
        findViewById(R.id.btnDecimal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastNumeric && !stateError && !lastDot) {
                    txtScreen.append(".");
                    lastNumeric = false;
                    lastLeftParen = false;
                    lastDot = true;
                }
            }
        });
        // Clear button
        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtScreen.setText("");  // Clear the screen
                txtPreResults.setText("");  // Clear the screen
                // Reset all the states and flags
                lastNumeric = false;
                lastLeftParen = false;
                leftParenCount = 0;
                stateError = false;
                lastDot = false;
            }
        });
        // Equal button
        findViewById(R.id.btnEqual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEqual();
            }
        });
    }

    /**
     * Logic to calculate the solution.
     */
    private void onEqual() {
        // If the current state is error, nothing to do.
        // If the last input is a number only, solution can be found.
        if ((lastNumeric || lastParen) && !stateError) {
            //add any need right parenthesis
            if (leftParenCount >0 ){

                String str;
                str = txtScreen.getText().toString();
                str=str.substring(str.length() - 1,str.length());

                //TODO this code does not work
               while(str == "("){
                   str = txtScreen.getText().toString();
                    txtScreen.setText(str.toCharArray(),1,str.length());
                    str = txtScreen.getText().toString();
                    leftParenCount--;
               }
                //-----------------------------
                while(leftParenCount >0) {
                    txtScreen.append(")");
                    leftParenCount--;
                }
            }

            // Read the expression
            String txt = txtScreen.getText().toString();
            // Create an Expression (A class from exp4j library)
            Expression expression = new ExpressionBuilder(txt).build();
            try {
                // Calculate the result and display
                double result = expression.evaluate();
                txtPreResults.setText(Double.toString(result));
                lastDot = true; // Result contains a dot
                lastParen = false;
                lastLeftParen = false;
                leftParenCount = 0;
            } catch (ArithmeticException ex) {
                // Display an error message
                txtPreResults.setText("Error");
                stateError = true;
                lastNumeric = false;
                lastParen = false;
                lastLeftParen = false;
                leftParenCount = 0;
            }
        }
    }
}