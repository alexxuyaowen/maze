package TheGame.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {

    public static final TETile AVATAR = new TETile('@', Color.WHITE, Color.BLACK, "player");
    public static final TETile YELLOW_AVATAR = new TETile('@', Color.YELLOW, Color.BLACK, "player");
    public static final TETile RED_AVATAR = new TETile('@', Color.RED, Color.BLACK, "player");
    public static final TETile GREEN_AVATAR = new TETile('@', Color.GREEN, Color.BLACK, "player");
    public static final TETile BLUE_AVATAR = new TETile('@', Color.BLUE, Color.BLACK, "player");
    public static final TETile FINAL_AVATAR = new TETile('@', Color.CYAN, Color.BLACK, "player");

    public static final TETile CHASER = new TETile('☠', Color.RED, Color.BLACK, "chaser");

    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall");
    public static final TETile YELLOW_WALL =
            new TETile('#', new Color(170, 162, 92), Color.darkGray, "wall");
    public static final TETile PURPLE_WALL =
            new TETile('#', new Color(148, 76, 151), Color.darkGray, "wall");
    public static final TETile FADED_WALL =
            new TETile('#', Color.BLACK, new Color(41, 41, 41, 116), "wall");

    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black,
            "floor");

    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR =
            new TETile('⛋', Color.CYAN, new Color(247, 255, 190, 140),
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");

    public static final TETile TRACE =
            new TETile('@', new Color(255, 255, 255, 75), Color.BLACK, "trace");
    public static final TETile YELLOW_TRACE =
            new TETile('@', new Color(255, 255, 0, 75), Color.BLACK, "trace");
    public static final TETile RED_TRACE =
            new TETile('@', new Color(255, 0, 0, 75), Color.BLACK, "trace");
    public static final TETile GREEN_TRACE =
            new TETile('@', new Color(0, 255, 0, 75), Color.BLACK, "trace");
    public static final TETile BLUE_TRACE =
            new TETile('@', new Color(0, 0, 255, 75), Color.BLACK, "trace");

    public static final TETile PORTAL =
            new TETile('☯', Color.BLACK, new Color(255, 255, 255, 50), "portal");

    public static final TETile VISION =
            new TETile('☀', Color.ORANGE, Color.BLACK, "vision");

    public static final TETile LIGHTNING =
            new TETile('⚡', Color.RED, Color.BLACK, "lightning");



}


