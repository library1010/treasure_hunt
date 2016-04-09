package com.example.treasurehunt;

/*
 * Class Score: handle the score of this game
 * 
 * @author group 8
 * 
 */
public class Score implements Comparable<Score> {

    public int scoreNum;
    /*
     * Properties
     */
    private String playerName;
    private int level;

    /*
     * Constructor
     *
     * @author 8A Tran Trong Viet
     *
     * @param name: player 's name, num: score, lev: level
     */
    public Score(String name, int num, int lev) {
        playerName = name;
        scoreNum = num;
        level = lev;
    }

    /*
     * Check this score against another
     *
     * @author 8A Tran Trong Viet
     *
     * @param sc: score
     */
    public int compareTo(Score sc) {
        // return 0 if equal
        // 1 if passed greater than this
        // -1 if this greater than passed
        return sc.scoreNum > scoreNum ? 1 : sc.scoreNum < scoreNum ? -1 : 0;
    }

    /*
     * Return score display text
     *
     * @author 8A Tran Trong Viet
     */
    public String getScoreText() {
        return playerName + " - " + scoreNum + " - " + level;
    }
}
