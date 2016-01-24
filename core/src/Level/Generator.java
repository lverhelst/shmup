package Level;

import java.util.Random;

/**
 * Created by emery on 2016-01-23.
 */
public class Generator {

    public String[][] generate(int w, int h) {
        String[][] map = new String[w][h];
        boolean[][] base = generateBase(w,h);

        for(int row = 0; row < h - 1; ++row) {
            for(int col = 0; col < w - 1; ++col) {
                String cell = base[row][col] ? "1" : "0";
                cell += base[row][col+1] ? "1" : "0";
                cell += base[row+1][col] ? "1" : "0";
                cell += base[row+1][col+1] ? "1" : "0";

                map[row][col] = cell;
            }
        }

        return map;
    }

    public boolean[][] generateBase(int w, int h) {
        boolean[][] map = new boolean[w][h];
        Random rand = new Random();

        for(int row = 0; row < h; ++row) {
            for(int col = 0; col < w; ++col) {
                //ensure edges are solid and others are random
                if(row == 0 || row == h - 1 || col == 0 || col == w - 1) {
                    map[row][col] = true;
                } else {
                    map[row][col] = rand.nextBoolean();
                }
            }
        }

        return map;
    }
}