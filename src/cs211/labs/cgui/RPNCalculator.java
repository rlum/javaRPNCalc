/*
 * CPSC 211: Lab 7
 */
package cs211.labs.cgui;

import java.util.Stack;

/**
 * A Reverse Polish Notation (RPN) calculator
 */
public class RPNCalculator {
    
    // Keep the operands on a stack
    private Stack<Double> operands;

    // Show the stack operator
    private static final String SHOWSTACK = ":"; 

    /**
     * Constructor
     */
    public RPNCalculator() {
        operands = new Stack<Double>();
    }
    
    /**
     * Given a command as a string, direct the calculator
     * to perform the desired operation
     * @param command An operator or a number
     * @throw Can throw an exception for an empty stack
     *    or a number conversion exception
     */
    public void performCommand(String command) {
        char operator;
        String trimCommand = command.trim();
        // decide which command to perform
        if (isOperator(trimCommand)) {
            operator = (trimCommand.toCharArray())[0];
            performBinaryOp(operator);
        }

        else {
            // Treat the command as a number
        	if (command!=null)
        		operands.push(new Double(command.trim()));
//            System.out.println(operands);
        }
    }
    
    /**
     * Return the current stack as a string
     * @return The operands on the stack as a string
     */
    public String getStack() {
        String returnValue = new String();
        Double operandsAsArray[] = new Double[operands.size()];
        operandsAsArray = operands.toArray(operandsAsArray);
        for ( int i = 0; i < operandsAsArray.length; i++ ) {
            returnValue += "\n" + operandsAsArray[i];
        }
        return returnValue;
    }
    
    public String getTopStack(){
    	if ( operands.size()>0){
    		return operands.lastElement().toString();
       	}else{
    		return null;
    	}
    }

    /**
     * Clear the calculator
     */
    public void clear() {
        operands.clear();
    }

    /**
     * Perform a binary operation
     * @pre nextOperation is a valid binary operand and size of operands stack
     *      and size of operands stack is at least 2
     *
     * @post operation has been applied to first two operands popped from stack
     *       and result has been pushed onto stack
     */
    private void performBinaryOp(char nextOperation) {

        double leftOperand, rightOperand;
        Double result = new Double(0);

        rightOperand = operands.pop().floatValue();
        leftOperand = operands.pop().floatValue();

        switch (nextOperation) {
            case '+':
                result = new Double(leftOperand + rightOperand);
                break;
            case '-':
                result = new Double(leftOperand - rightOperand);
                break;
            case '*':
                result = new Double(leftOperand * rightOperand);
                break;
            case '/':
                result = new Double(leftOperand / rightOperand);
                break;
        }
        operands.push(result);
    }

    /**
     * A helper function to determine if a command string is an operator
     */
    boolean isOperator(String nextChar) {
//    	System.out.println("IsOperator("+nextChar+")");
        return (nextChar.equals("+") || nextChar.equals("-")
                || nextChar.equals("*") || nextChar.equals("/")  );
    }

   

}
