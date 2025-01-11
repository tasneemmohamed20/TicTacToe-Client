/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theGame;

/**
 *
 * @author tasneem
 */
public enum XO {
    X('X'), 
    O('O'),
    B(' '); 

    private final char symbol;

    XO(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }
    
    public boolean isMarked(){
        if(this != B){ 
            return true;}
        else
            return false;
    }
    
    
}
