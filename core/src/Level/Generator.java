package Level;

import java.util.Random;

/**
 * Created by emery on 2016-01-23.
 */
public class Generator {
    public boolean[][] map;
    public int width, height;
    public int wallRate = 6;
    public int emptyRate = 3;
    public int density = 45;

    public Generator(int width, int height) {
        this.width = width;
        this.height = height;
        map = generateMap();
    }

    public void newMap(int width, int height) {
        this.width = width;
        this.height = height;
        map = generateMap();
    }

    private boolean[][] generateMap() {
        boolean[][] map = new boolean[width][height];
        Random rand = new Random();
        int width = map.length;
        int height = map[1].length;

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                //ensure edges are solid and others are random
                if(x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    map[x][y] = true;
                } else {
                    map[x][y] = rand.nextInt(100) < density;
                }
            }
        }

        return map;
    }

    public String[][] getMarchingMap() {
        String[][] marching = new String[width][height];

        for(int x = 0; x < width - 1; ++x) {
            for(int y = 0; y < height - 1; ++y) {
                String cell = map[x][y] ? "1" : "0";
                cell += map[x][y+1] ? "1" : "0";
                cell += map[x+1][y] ? "1" : "0";
                cell += map[x+1][y+1] ? "1" : "0";

                marching[x][y] = cell;
            }
        }

        return marching;
    }

    public void printMap(boolean[][] map) {
        int width = map.length;
        int height = map[1].length;

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                System.out.print(map[x][y] ? "x" : " ");
            }
            System.out.println();
        }
    }

    public void smooth(int iterations) {

        for(int i = 0; i < iterations; ++i) {
            boolean[][] newMap = new boolean[map.length][map[0].length];
            int width = map.length;
            int height = map[1].length;

            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    int walls = 0;

                    if(x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                        newMap[x][y] = true;
                    } else {
                        walls += map[x][y] ? 1 : 0;
                        walls += map[x + 1][y + 1] ? 1 : 0;
                        walls += map[x + 1][y - 1] ? 1 : 0;
                        walls += map[x - 1][y + 1] ? 1 : 0;
                        walls += map[x - 1][y - 1] ? 1 : 0;
                        walls += map[x + 1][y] ? 1 : 0;
                        walls += map[x - 1][y] ? 1 : 0;
                        walls += map[x][y - 1] ? 1 : 0;
                        walls += map[x][y + 1] ? 1 : 0;

                        if (map[x][y]) {
                            //already a wall, but if lots around empty make empty
                            newMap[x][y] = walls > wallRate;
                        } else {
                            //not wall but if surrounded by walls make wall
                            newMap[x][y] = walls > emptyRate;
                        }
                    }
                }
            }

            map = newMap;
        }
    }
}