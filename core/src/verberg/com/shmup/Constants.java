package verberg.com.shmup;

/**
 * Created by Orion on 11/29/2015.
 */
public class Constants {

    static final int PPM = 5;


    //When setting bits:
    //Category Bits - I AM A...
    //Mask Bits - I collide with...

    /**
     *  COLLISION MATRIX
     *
     *  ENTITY          CATEGORY               MASK
     *  WALL            WALL                   EVERYTHING (0)
     *  CARBODY         CAR                    WALL|TIRE|CAR|POWERUP|BULLET (Everything @ 11/29)
     *  TIRE            TIRE                   WALL|TIRE|CAR|BULLET  //Don't need to collide w/ powerup since if we only collide with body then we're conveniently at root of car
     *  POWERUP         POWERUP                CAR
     *  BULLET          BULLET                 WALL|TIRE|CAR|BULLET  //Don't collide w/ powerups
     */

    //0000 0000 0000 0001
    public static final int WALL_BIT = 1;
    //0000 0000 0000 0010
    public static final int CAR_BIT = 2;
    //0000 0000 0000 0100
    public static final int TIRE_BIT = 4;
    //0000 0000 0000 1000
    public static final int POWERUP_BIT = 8;
    //0000 0000 0001 0000
    public static final int BULLET_BIT = 16;
    //0000 0000 0010 0000
    public static final int GROUND_BIT = 32;
    //0000 0000 0100 0000
    public static final int XY_BIT = 64;

    public static final int CAR_MASK = WALL_BIT | CAR_BIT | TIRE_BIT | POWERUP_BIT | BULLET_BIT | GROUND_BIT;   //63
    public static final int TIRE_MASK = WALL_BIT | CAR_BIT | TIRE_BIT | BULLET_BIT | GROUND_BIT;                //55
    public static final int BULLET_MASK = WALL_BIT | CAR_BIT | TIRE_BIT | BULLET_BIT;
    public static final int POWERUP_MASK = CAR_BIT;                                                             //02
    public static final int GROUND_MASK = CAR_BIT | TIRE_BIT;


}
