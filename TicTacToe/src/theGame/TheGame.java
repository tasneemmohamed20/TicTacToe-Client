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
public class TheGame {
    
    private static int firstScore;
    private static String firstPos;
    
    private static int secondID;
    private static String secondName;
    private static int secondScore;
    private static String secondPos; 

    public static int getFirstScore() {
        return firstScore;
    }

    public static void setFirstScore(int aFirstScore) {
        firstScore = aFirstScore;
    }

    public static String getFirstPos() {
        return firstPos;
    }

    public static void setFirstPos(String aFirstPos) {
        firstPos = aFirstPos;
    }

    public static int getSecondID() {
        return secondID;
    }

    public static void setSecondID(int aSecondID) {
        secondID = aSecondID;
    }

    public static String getSecondName() {
        return secondName;
    }

    public static void setSecondName(String aSecondName) {
        secondName = aSecondName;
    }

    public static int getSecondScore() {
        return secondScore;
    }

    public static void setSecondScore(int aSecondScore) {
        secondScore = aSecondScore;
    }

    public static String getSecondPos() {
        return secondPos;
    }

    public static void setSecondPos(String aSecondPos) {
        secondPos = aSecondPos;
    }


}
