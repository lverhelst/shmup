package Level;

import java.util.Random;

/**
 * Created by emery on 2016-01-23.
 */
public class Generator {
    public boolean[][] map;
    public int width, height;
    public float lowerRate = 0;
    public float upperRate = 0;
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

    public void printMap() {
        int width = map.length;
        int height = map[1].length;

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                System.out.print(map[x][y] ? "#" : " ");
            }
            System.out.println();
        }
    }

    public void smooth(int iterations) {
        for(int i = 0; i < iterations; ++i) {
            float maxCount = 81;
            int width = map.length;
            int height = map[0].length;
            int[][] countMap = new int[width][height];

            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    int walls = 0;

                    if(x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                        walls = 9;
                    } else {
                        walls += map[x][y] ? 1 : 0;
                        walls += map[x + 1][y + 1] ? 1 : 0;
                        walls += map[x + 1][y - 1] ? 1 : 0;
                        walls += map[x - 1][y + 1] ? 1 : 0;
                        walls += map[x - 1][y - 1] ? 1 : 0;
                        walls += map[x + 1][y] ? 1 : 0;
                        walls += map[x - 1][y] ? 1 : 0;
                        walls += map[x][y + 1] ? 1 : 0;
                        walls += map[x][y - 1] ? 1 : 0;
                    }

                    countMap[x][y] = walls;
                }
            }

            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    float count = 0;

                    if(x == 0 || y == 0 || x == width - 1 || y == height - 1) {
                        count = maxCount;
                    } else {
                        count += countMap[x][y];
                        count += countMap[x + 1][y + 1];
                        count += countMap[x + 1][y - 1];
                        count += countMap[x - 1][y + 1];
                        count += countMap[x - 1][y - 1];
                        count += countMap[x + 1][y];
                        count += countMap[x - 1][y];
                        count += countMap[x][y + 1];
                        count += countMap[x][y - 1];
                    }

                    float rate = count/maxCount;

                    if(rate > upperRate)
                        map[x][y] = true;

                    if(rate < lowerRate)
                        map[x][y] = false;
                }
            }
        }
    }

    public void fill() {
        int[][] search = new int[width][height];
        int maxPos = 0;
        int maxCount = 0;
        int place = 0;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
               if(!map[x][y]) {
                   if(search[x][y] == 0) {
                       int count = searchMap(search, x, y, ++place);
                       if(count > maxCount) {
                           maxCount = count;
                           maxPos = place;
                       }
                   }
               } else {
                   search[x][y] = -1;
               }
            }
        }

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if(search[x][y] != maxPos) {
                    map[x][y] = true;
                }
            }
        }
    }

    private int searchMap(int[][] search, int x, int y, int place) {
        search[x][y] = place;
        int count = 1;

        if(x + 1 < width && !map[x + 1][y] && search[x + 1][y] == 0) {
            count += searchMap(search, x + 1, y, place);
        }
        if(x - 1 >= 0 && !map[x - 1][y] && search[x - 1][y] == 0) {
            count += searchMap(search, x - 1, y, place);

        }
        if(y + 1 < height && !map[x][y+1] && search[x][y + 1] == 0) {
            count +=searchMap(search, x, y+1, place);

        }
        if(y - 1 >= 0 && !map[x][y-1] && search[x][y - 1] == 0) {
            count += searchMap(search, x, y-1, place);
        }

        return count;
    }
}