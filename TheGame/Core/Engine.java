package TheGame.Core;

import TheGame.TileEngine.TERenderer;
import TheGame.TileEngine.TETile;
import TheGame.TileEngine.Tileset;
import TheGame.Graph.*;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import edu.princeton.cs.introcs.StdDraw;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */

    private static final int HUD_HEIGHT = 2;
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30 + HUD_HEIGHT;

    private static final char[] MENU_REQUESTS = {'n', 'r', 'l', 's', 'q'};
    private static final char[] FEATURES = {'q', 'n', 'r', 'y', 'w', 'f', 'g', 'b', 'l', 's', 'd', 't'};

    private TETile[][] world;
    private Player player;
    private String theSeed; // e.g. "n######s"
    private String playerCommands;
    private HashMap<String, List<Integer>> tiles;
    private HashMap<Character, String> colors;
    private HashMap<Integer, Character> navigation;
    private Random r;
    private boolean fullScreen;
    private boolean doorExist;
    private boolean stringInput;

    public Engine() {
        theSeed = new String();
        playerCommands = new String();
        stringInput = true;
        fullScreen = false;
        doorExist = false;

        initialize();
    }

    private void initialize() {
        world = new TETile[WIDTH][HEIGHT];
        player = new Player();
        tiles = new HashMap<>();

        colors = new HashMap<>();
        colors.put('r', "red");
        colors.put('y', "yellow");
        colors.put('w', "white");
        colors.put('g', "green");
        colors.put('b', "blue");

        navigation = new HashMap<>();
        navigation.put(-1, 's');
        navigation.put(1, 'w');
        navigation.put(-HEIGHT, 'a');
        navigation.put(HEIGHT, 'd');
    }

    private class Player {

        TETile trace;
        TETile avatar;
        String color;
        int position;
        int health;
        int sight;
        int score;
        boolean lightning;

        private Player() {
            health = 256;
            position = 0;
            i();
        }

        private void i() {
            trace = Tileset.TRACE;
            avatar = Tileset.AVATAR;
            color = "white";
            sight = 2;
            lightning = false;
        }

        private void changeAppearance(String c) {
            color = c;

            if (color.equals("yellow")) {
                trace = Tileset.YELLOW_TRACE;
                avatar = Tileset.YELLOW_AVATAR;
            } else if (color.equals("red")) {
                trace = Tileset.RED_TRACE;
                avatar = Tileset.RED_AVATAR;
            } else if (color.equals("green")) {
                trace = Tileset.GREEN_TRACE;
                avatar = Tileset.GREEN_AVATAR;
            } else if (color.equals("blue")) {
                trace = Tileset.BLUE_TRACE;
                avatar = Tileset.BLUE_AVATAR;
            } else if (color.equals("white")) {
                trace = Tileset.TRACE;
                avatar = Tileset.AVATAR;
            } else if (c.equals("final")) {
                trace = Tileset.FLOOR;
                avatar = Tileset.FINAL_AVATAR;
            }
        }

        private void moveTo(int toP) {
            score++;
//            System.out.println(score);

            if (!tile(toP).description().equals("lightning")) {
                lightning = false;
            } else {
                playSound("TheGame/Clips/Flash.wav");
                lightning = true;
            }

            if (tiles.get("portal").contains(position)) {
                set(position, Tileset.PORTAL);
            } else {
                set(position, trace);
            }

            if (tiles.get("vision").contains(toP)) {
                remove(toP);
                System.out.println(tiles.get("vision"));
            }

            set(toP, avatar);
            position = toP;
        }

        private void collectVision() {
            int n = 3;
            incrementHealth(16);
            changeSight(1);

            if (sight % 5 == 2 && sight < 17) {
                addLightning();
            }

            if (sight > 2 + 5 * n) {
//                int size = colors.size();
//                int rand = r.nextInt(size);
//
//                for (char c : colors.keySet()) {
//                    if (rand == 0) {
//                        feature(c);
//                    }
//                    rand--;
//                }
            } else if (sight == 2 + 5 * n) {
                if (!doorExist) {
                    fullScreen = true;
                    changeAppearance("final");
                    set(player.position, player.avatar);
                    addDoor();
                }
            } else if (sight == 2 + 4 * n) {
                feature('r');
            } else if (sight == 2 + 3 * n) {
                feature('y');
            } else if (sight == 2 + 2 * n) {
                feature('g');
            } else if (sight == 2 + n) {
                feature('b');
            }
        }

        private void changeSight(int v) {
            sight += v;
        }

        private void incrementHealth(int n) {
            health += n;
        }

        private void decrementHealth(int n) {
            health -= n;
        }
    }



    private class PathTree {
        Node root, start;
        String path;

        HashMap<Integer, Node> map;

        private class Node {
            int value, depth, visited;
            Node parent;

            private Node(int v) {
                visited = 0;
                value = v;
                depth = Integer.MAX_VALUE;
                parent = null;
            }
        }

        private PathTree(int s, int d) {
            map = new HashMap<>();

            for (int i : tiles.get("floor")) {
                Node n = new Node(i);
                map.put(i, n);
            }

            for (int i : tiles.get("portal")) {
                Node n = new Node(i);
                map.put(i, n);
            }


//            find = false;
            path = "";
            root = map.get(d);
            root.depth = 0;

            start = map.get(s);

            buildFrom(root);

            path = pathToRoot(s);
        }

        private void buildFrom(Node n) {
            if (start == root || !map.containsValue(n)) {
                return;
            }

            int p = n.value;

            if (tiles.get("portal").contains(p)) {
                if (root.value != p) {
                    return;
                }
            }

            int[] navi = {1, -1, HEIGHT, -HEIGHT};

            heuristicSort(navi, p);

            for (int i = 0; i < 4; i++) {
                int pos = p + navi[i];

                if (!map.containsKey(pos)) {
                    continue;
                }

                Node nn = map.get(pos);
                if (nn.depth > n.depth && n.visited <= map.size() / 4) {
                    n.visited++;

                    if (tiles.get("portal").contains(nn.value) && start.value != nn.value) {
                        continue;
                    }

                    nn.parent = n;
                    nn.depth = n.depth + 1;

//                        set(nn.value, Tileset.CHASER);
                    buildFrom(nn);
                }
            }
        }

        private void heuristicSort(int[] navi, int p) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    // dest + something means start - sth.
                    if (distance(start.value + navi[j], p) < distance(start.value + navi[i], p)) {

                        int temp = navi[i];
                        navi[i] = navi[j];
                        navi[j] = temp;
                    }
                }
            }
        }

        private Node bestNeighbor(Node n) {
            Node best = n;

            for (int c : navigation.keySet()) {
                Node neighbor = map.get(n.value + c);

                if (map.containsValue(neighbor) && neighbor.depth < best.depth) {
                    best = neighbor;
                }
            }

            return best;
        }

//            private void relax(Node n) {
//                Node bestn = bestNeighbor(n);
//                if (bestn.value != n.value && bestn.depth < n.parent.depth) {
//                    n.parent = bestNeighbor(n);
//                }
//            }

        private String pathToRoot(int s) {
            Node n = map.get(s);

            while (n.parent != null) {
                n.parent = bestNeighbor(n);
                path += navigation.get(n.parent.value - n.value);
                n = n.parent;
            }

            return path;
        }

//            private void compressPath() {
//                for (int i = 0; i <= path.length() - 3; i++) {
//                    String toCheck = path.substring(i, i + 3);
//                    int value = toValue(toCheck);
//
//                    if (!navigation.keySet().contains(value)) {
//                        continue;
//                    }
//
//                    for (int n : navigation.keySet()) {
//                        if (n == value) {
//                            path = path.substring(0, i) + navigation.get(n) + path.substring(i + 3);
//                            i = -1;
//                        }
//                    }
//                }
//            }
    }


    private class Edge {
        int point1, point2;
        double dist;

        private Edge(int p1, int p2) {
            point1 = p1;
            point2 = p2;

            dist = distance(p1, p2);
        }
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */

    public void interactWithKeyboard() {
        stringInput = false;
        ter.initialize(WIDTH, HEIGHT);

        menu();

        if (theSeed.length() == 0) {
            return;
        }

        ter.renderFrame(worldInSight());

        while (true) {
            if (System.currentTimeMillis() % 100 < 10) {
                hud();
            }

            if (StdDraw.hasNextKeyTyped()) {
                char pressed = Character.toLowerCase(StdDraw.nextKeyTyped());

                if (pressed == ':') {
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char f = Character.toLowerCase(StdDraw.nextKeyTyped());
                            if (validate(f, "feature")) {
                                feature(f);

                                ter.renderFrame(worldInSight());
                                break;
                            }
                        }
                    }
                } else if (navigation.containsValue(pressed)) {
                    navigate(pressed);
                    ter.renderFrame(worldInSight());
                }
            }

            if (StdDraw.isMousePressed()) {
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();

                int dest = toOneD((int) x, (int) y);

                if (x > WIDTH / 4 - 2 && x < WIDTH / 4 + 2 && y > HEIGHT - 2.5 && y < HEIGHT - 0.5) {
                    feature('t');
                    ter.renderFrame(worldInSight());
                } else if (x > WIDTH * 3 / 4 - 2 && x < WIDTH * 3 / 4 + 2 && y > HEIGHT - 2.5 && y < HEIGHT - 0.5) {
                    feature('d');
                    ter.renderFrame(worldInSight());
                } else {
                    processCommands(aStarPath(player.position, dest), 16);
                }
//                    int timeOut = 2;
//                    while (player.position != toOneD(x, y)) {
//                        if (timeOut <= 0) {
//                            break;
//                        }
//                        processCommands(naivePath(x, y), 32);
//                        timeOut--;
//                    }
//                    if (player.position != dest) {
//                        PathTree pt = new PathTree(player.position, dest);
//                        processCommands(pt.path, 32);
//                    }
//                }
                StdDraw.pause(500);
            }
        }
    }


    private String aStarPath(int start, int end) {
        List<Integer> positions = tiles.get("floor");
        WeightedDirectedGraph wdg = new WeightedDirectedGraph(WIDTH * HEIGHT);

        for (int i : positions) {
            List<Integer> neighbors = validNeighbors(i);
            if (!neighbors.isEmpty()) {
                for (int vn : neighbors) {
                    wdg.addEdge(i, vn, 1);
                }
            }
        }

        if (tiles.get("portal").contains(end)
                || tile(end).description().equals("unlocked door")) {
            for (int vnp : validNeighbors(end)) {
                wdg.addEdge(vnp, end, 1);
            }
        }

        if (tiles.get("portal").contains(start)) {
            for (int vnp : validNeighbors(start)) {
                wdg.addEdge(start, vnp, 1);
            }
        }

        ShortestPathsSolver solver = new WeirdSolver<>(wdg, start, end, 3);
        if (solver.outcome() == SolverOutcome.SOLVED) {
            return toNavigation(solver.solution());
        } else {
            System.out.println("nope");
            return "";
        }
    }

    private String toNavigation(List<Integer> path) {
        String navi = "";

        int start = path.get(0);

        for (int i : path) {
            if (i == start) {
                continue;
            }

            navi += navigation.get(i - start);
            start = i;
        }

        return navi;
    }

    private List<Integer> validNeighbors(int p) {
        List<Integer> valid = new ArrayList<>();
        for (int navi : navigation.keySet()) {
            int neighbor = p + navi;
            if (tiles.get("floor").contains(neighbor)) {
                valid.add(neighbor);
            }
        }

        return valid;

    }


    /** naive path finding approach*/
    private String naivePath(int posX, int posY) {
        String path = "";
        int x = posX - getX(player.position);
        int y = posY - getY(player.position);

        for (int i = 0; i < Math.abs(x); i++) {
            if (x > 0) {
                path += 'd';
            } else {
                path += 'a';
            }
        }

        for (int i = 0; i < Math.abs(y); i++) {
            if (y > 0) {
                path += 'w';
            } else {
                path += 's';
            }
        }
        return path;
    }

    private void menu() {
        StdDraw.pause(1000);

        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.enableDoubleBuffering();

        menuFrame();

        while (true) {
            if (StdDraw.isMousePressed()) {
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();
                if (x > 36 && x < 44 && y < 12 && y > 11) {
                    handleMenu('n');
                    break;
                } else if (x > 37 && x < 43 && y < 10.5 && y > 9.5) {
                    handleMenu('r');
                    break;
                } else if (x > 36 && x < 44 && y < 9 && y > 8) {
                    handleMenu('l');
                    break;
                } else if (x > 38 && x < 42 && y < 8.5 && y > 7) {
                    handleMenu('s');
                    break;
                } else if (x > 38 && x < 42 && y < 6.5 && y > 5.5) {
                    handleMenu('q');
                    return;
                }
            } else if (StdDraw.hasNextKeyTyped()) {
                char pressed = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (validate(pressed, "menu")) {
                    handleMenu(pressed);
                    break;
                }
            }
        }

        // set back to TERender's font
        Font font = new Font("Monaco", Font.BOLD, 14);
        StdDraw.setFont(font);
        StdDraw.clear(Color.BLACK);
        StdDraw.show();
    }

    private void handleMenu(char command) {

        if (command == 'r') {
            replay();
        } else if (command == 'l') {
            stringInput = true;
            world = interactWithInputString(loadGame("l"));
            stringInput = false;
        } else if (command == 'q') {
            System.exit(0);
        } else if (command == 'n') {
            StdDraw.clear(Color.BLACK);
            StdDraw.setPenColor(Color.CYAN);
            StdDraw.setFont(new Font("Times New Roman", Font.BOLD, 25));
            StdDraw.text(WIDTH / 2, HEIGHT / 2, "Enter seed");
            StdDraw.show();

            String seed = "";

            while (true) {
                if (StdDraw.hasNextKeyTyped()) {
                    char s = Character.toLowerCase(StdDraw.nextKeyTyped());

                    if (s == 'r' && seed.length() == 0) {
                        Random randSeed = new Random();
                        theSeed = "n" + Math.abs(randSeed.nextLong()) + "s";
                        break;
                    }

                    if (s == 's' && seed.length() > 0) {
                        theSeed = "n" + seed + "s";
                        break;
                    }

                    if (s < '0' || s > '9') {
                        continue;
                    }
                    seed += s;


                    StdDraw.clear(Color.BLACK);
                    StdDraw.text(WIDTH / 2, HEIGHT / 2, "Enter seed");
                    StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, seed);
                    StdDraw.show();
                }
            }

            world = interactWithInputString(theSeed + playerCommands);

        } else if (command == 's') {
            settings();
        }
    }

    private void menuFrame() {

        StdDraw.setPenColor(Color.ORANGE);
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Times New Roman", Font.CENTER_BASELINE, 40));
        StdDraw.text(WIDTH / 2, HEIGHT * 2 / 3, "The Game");

        StdDraw.setFont(new Font("Times New Roman", Font.BOLD, 20));
        StdDraw.text(WIDTH / 2, HEIGHT / 3 + 1.5, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 3, "Replay (R)");
        StdDraw.text(WIDTH / 2, HEIGHT / 3 - 1.5, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 3 - 3, "Settings (S)");
        StdDraw.text(WIDTH / 2, HEIGHT / 3 - 4.5, "Quit (Q)");

        StdDraw.show();
    }

    /** read the saved file, split it into two parts
     * one to do the menu part and generate the world
     * one to navigate the character*/

    private void replay() {
        ter.initialize(WIDTH, HEIGHT);

        String input = loadGame("l");
        initialize(world);
        generateWorld(getSeed(input));
        processCommands(getCommands(input), 32);
    }

    private void settings() {

        StdDraw.setPenColor(Color.ORANGE);
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 20));
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Character Coloring (C)");
        StdDraw.show();

        while (true) {
            if (StdDraw.isMousePressed()) {
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();

                if (x > 37 && x < 43 && y < 15.7 && y > 14.3) {
                    coloring();
                    break;
                }
            } else if (StdDraw.hasNextKeyTyped()) {
                char command = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (command == 'c') {
                    coloring();
                    break;
                }
            }
        }
    }

    private void coloring() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.ORANGE);
        StdDraw.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 20));
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 1.5, "White (W)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Yellow (Y)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 1.5, "Red (R)");
        StdDraw.show();

        StdDraw.pause(1000);
        while (true) {
            if (StdDraw.isMousePressed()) {
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();

                if (x > 37 && x < 43 && y < 17.5 && y > 16) {
                    feature('w');
                    break;
                } else if (x > 37 && x < 43 && y < 15.7 && y > 14.3) {
                    feature('y');
                    break;
                } else if (x > 37 && x < 43 && y < 14 && y > 13) {
                    feature('r');
                    break;
                }
            } else if (StdDraw.hasNextKeyTyped()) {
                char color = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (colors.containsKey(color)) {
                    feature(color);
                    break;
                }
            }
        }

        menu();
    }

    private void hud() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();

        //
        if (x < WIDTH && y < HEIGHT && x >= 0 && y >= 0) {
            StdDraw.setPenColor((Color.BLACK));
            StdDraw.textLeft(1, HEIGHT - 1, "██████");
            StdDraw.show();

            StdDraw.setPenColor(Color.WHITE);
            StdDraw.textLeft(1, HEIGHT - 1, worldInSight()[x][y].description());
            StdDraw.show();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();

        StdDraw.setPenColor((Color.BLACK));
        StdDraw.textRight(WIDTH - 1, HEIGHT - 1, "██████");
        StdDraw.textRight(WIDTH - 5, HEIGHT - 1, "██████");
        StdDraw.show();

        StdDraw.setPenColor(Color.GREEN);
        StdDraw.textRight(WIDTH - 1, HEIGHT - 1, formatter.format(date));
        StdDraw.show();

        StdDraw.setPenColor((Color.BLACK));
        StdDraw.text(WIDTH / 2, HEIGHT - 1.5, "████████████████");
        StdDraw.show();

        StdDraw.setPenColor(Color.RED);
        StdDraw.text(WIDTH / 2, HEIGHT - 1.5, "Health remaining: " + player.health);
        StdDraw.show();

        StdDraw.setPenColor((Color.BLACK));
        StdDraw.text(WIDTH / 4, HEIGHT - 1.5, "███████");
        StdDraw.show();

        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text(WIDTH / 4, HEIGHT - 1.5, "(T)eleport");
        StdDraw.show();

        StdDraw.setPenColor((Color.BLACK));
        StdDraw.text(WIDTH * 3 / 4, HEIGHT - 1.5, "██████");
        StdDraw.show();

        StdDraw.setPenColor(Color.YELLOW);
        StdDraw.text(WIDTH * 3 / 4, HEIGHT - 1.5, "(D)amage");
        StdDraw.show();
    }

    private boolean validate(char command, String type) {
        if (type.equals("menu")) {
            for (char c : MENU_REQUESTS) {
                if (command == c) {
                    return true;
                }
            }
        } else if (type.equals("feature")) {
            for (char f : FEATURES) {
                if (command == f) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //

        /** When the user clicks New Game */

        char request = Character.toLowerCase(input.charAt(0));

        if (request == 'l') {
            input = loadGame(input);
        } else if (input.isEmpty() || request != 'n') {
            throw new IllegalArgumentException("input must start with letter 'n'");
        }

        generateWorld(getSeed(input));

        processCommands(getCommands(input), 0);

        return world;
    }

    /** return a world that is only within the sight of the player
     * the original world is not modified*/
    private TETile[][] worldInSight() {
        if (player.lightning || fullScreen) {
            return world;
        }

        int s = player.sight;

        TETile[][] worldInSight = new TETile[WIDTH][HEIGHT];
        initialize(worldInSight);

        int x = getX(player.position);
        int y = getY(player.position);

        int obscured = player.sight / 2;
        for (int i = x - s - obscured; i <= x + s + obscured; i++) {
            for (int j = y - s - obscured; j <= y + s + obscured; j++) {
                if (i >= WIDTH || j >= HEIGHT - HUD_HEIGHT || i < 0 || j < 0) {
                    continue;
                } else if (i >= x - s && i <= x + s && j >= y - s && j <= j + s) {
                    worldInSight[i][j] = world[i][j];
                } else if (world[i][j].description().equals("wall")){
                    worldInSight[i][j] = Tileset.FADED_WALL;
                }
            }
        }

        return worldInSight;
    }

    private void initialize(TETile[][] aWorld) {
//        tiles.clear();

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                aWorld[x][y] = Tileset.NOTHING;
            }
        }
    }

    /* Variables modified: world, r, portals,
    /** Approach:
     * 1: Put a random number of rectangular floors
     * 2: Connect them
     * 3: Add walls around the floors
     */
    private void generateWorld(long s) {
        r = new Random(s);
        theSeed = "n" + s + "s";
        generateWorld();
    }

    private void generateWorld() {
        System.out.println(theSeed);
        initialize(world);
        List<Integer> disjointRooms = new ArrayList<>();
        WeightedQuickUnionUF wqu = new WeightedQuickUnionUF(WIDTH * HEIGHT);
        putRooms(wqu, disjointRooms);
        addHallways(wqu, disjointRooms);

        addWalls();
        putPortals();
        putVisions();
        putVisions();
        putCharacter();
    }

    private void putRooms(WeightedQuickUnionUF wqu, List<Integer> rooms) {
        int n = RandomUtils.uniform(r, 20, 50); // number of rooms ~ [20, 37)
        for (int i = 0; i < n; i++) {
            int posX = RandomUtils.uniform(r, 1, WIDTH - 1); // pox_x ~ [1, WIDTH - 1)
            int posY = RandomUtils.uniform(r, 1, HEIGHT - HUD_HEIGHT);
            int w = RandomUtils.uniform(r, 2, 7); // room's width ~ [1, 7)
            int h = RandomUtils.uniform(r, 2, 7); // room's height ~ [1, 7)

            if (good(posX, posY, w, h)) {
                putRoom(wqu, rooms, posX, posY, w, h);
            } else {
                i--;
            }
        }
    }

    /** "good" is defined as every room is at least 1 tile from one another*/
    private boolean good(int posX, int posY, int w, int h) {
        if (posX + w >= WIDTH || posY + h >= HEIGHT - HUD_HEIGHT - 1) {
            return false;
        }

        for (int x = posX - 1; x < posX + w + 1; x++) {
            for (int y = posY - 1; y < posY + h + 1; y++) {
                if (world[x][y].description().equals("floor")) {
                    return false;
                }
            }
        }

        return true;
    }

    private void putRoom(WeightedQuickUnionUF wqu, List<Integer> rooms, int posX, int posY, int w, int h) {
        int i = r.nextInt(w * h);

        int root = toOneD(posX, posY);
        for (int x = posX; x < posX + w; x++) {
            for (int y = posY; y < posY + h; y++, i--) {
                int p = toOneD(x, y);
                wqu.union(root, p);
                set(p, Tileset.FLOOR);

                if (i == 0) {
                    // a random spot of the room; use to connect
                    rooms.add(p);
                }
            }
        }
    }

    /** The hallways satisfy
     * 1: only connect the nearest room
     * 2: every room is connected in the end*/
//    private void addHallways(WeightedQuickUnionUF wqu, List<Integer> rooms) {
//        List<Integer> checked = new ArrayList<>();
//        int root = rooms.get(0);
//        for (int i = 0; i < rooms.size() - 1; i++) {
//            double bestDist = Double.MAX_VALUE;
//            int best = root;
//
//            for (int r2 : rooms) {
//                double dist = distance(r2, root);
//                if (r2 != root && dist < bestDist
//                        && !checked.contains(r2)) {
//                    bestDist = dist;
//                    best = r2;
//                }
//            }
//
//            checked.add(root);
//            connect(wqu, root, best);
//            root = best;
//        }
//    }

    private void addHallways(WeightedQuickUnionUF wqu, List<Integer> rooms) {
        ArrayHeapMinPQ fringe = new ArrayHeapMinPQ();

        for (int i = 0; i < rooms.size() - 1; i++) {
            for (int j = i + 1; j < rooms.size(); j++) {
                Edge e = new Edge(rooms.get(i), rooms.get(j));
                fringe.add(e, e.dist);
            }
        }

        int edgesToAdd = rooms.size() - 1;
        while (edgesToAdd > 0 && fringe.size() > 0) {
            Edge e = (Edge) fringe.removeSmallest();
//            System.out.println(e.dist);
            int p1 = e.point1;
            int p2 = e.point2;

            if (!wqu.connected(p1, p2)) {
//                set(p1, Tileset.CHASER);d
                connect(wqu, p1, p2);
                edgesToAdd--;
            }
        }
    }


    // support 4 * 2 different shapes of 'L' hallway
    private void connect(WeightedQuickUnionUF wqu, int p1, int p2) {
        int i = getX(p1);
        int j = getY(p1);
        int x = getX(p2);
        int y = getY(p2);

        if (i <= x && j < y) {
            int dy = y - j;
            for (int n = 1; n <= dy; n++) {
                int p = toOneD(i, j + n);
                if (!wqu.connected(p1, p2)) {
                    set(p, Tileset.FLOOR);
                    wqu.union(p1, p);
                } else {
                    break;
                }
            }

            for (int n = 1; n <= x - i; n++) {
                int p = toOneD(i + n, j + dy);
                if (!wqu.connected(p1, p2)) {
                    set(p, Tileset.FLOOR);
                    wqu.union(p1, p);
                } else {
                    break;
                }
            }
        } else if (i < x && j >= y) {
            int dx = x - i;
            for (int n = 1; n <= dx; n++) {
                int p = toOneD(i + n, j);

                if (!wqu.connected(p1, p2)) {
                    set(p, Tileset.FLOOR);
                    wqu.union(p1, p);
                } else {
                    break;
                }
            }

            for (int n = 1; n <= j - y; n++) {
                int p = toOneD(i + dx, j - n);

                if (!wqu.connected(p1, p2)) {
                    set(p, Tileset.FLOOR);
                    wqu.union(p1, p);
                } else {
                    break;
                }
            }
        } else if (i >= x && j > y) {
            int dy = j - y;
            for (int n = 1; n <= dy; n++) {
                int p = toOneD(i, j - n);

                if (!wqu.connected(p1, p2)) {
                    set(p, Tileset.FLOOR);
                    wqu.union(p1, p);
                } else {
                    break;
                }
            }

            for (int n = 1; n <= i - x; n++) {
                int p = toOneD(i - n, j - dy);
                if (!wqu.connected(p1, p2)) {
                    set(p, Tileset.FLOOR);
                    wqu.union(p1, p);
                } else {
                    break;
                }
            }
        } else if (i > x && j <= y) {
            int dx = i - x;
            for (int n = 1; n <= dx; n++) {
                int p = toOneD(i - n, j);
                if (!wqu.connected(p1, p2)) {
                    set(p, Tileset.FLOOR);
                    wqu.union(p1, p);
                } else {
                    break;
                }
            }

            for (int n = 1; n <= y - j; n++) {
                int p = toOneD(i - dx, j + n);
                if (!wqu.connected(p1, p2)) {
                    set(p, Tileset.FLOOR);
                    wqu.union(p1, p);
                } else {
                    break;
                }
            }
        }

    }

    private double distance(int p1, int p2) {
        return Math.pow(p1 / HEIGHT - p2 / HEIGHT, 2)
                + Math.pow(p1 % HEIGHT - p2 % HEIGHT, 2);
    }



    /** check the 8 adjacent tiles for a floor
     * change the the tile that is "nothing" to "wall" */
    private void addWalls() {
        for (int floor : tiles.get("floor")) {
            addWall(floor);
        }
    }

    private void addWall(int f) {
        final int N = 3;

        TETile[] walls = new TETile[N];
        walls[0] = Tileset.PURPLE_WALL;
        walls[1] = Tileset.YELLOW_WALL;
        walls[2] = Tileset.WALL;

        List<Integer> nothing = getSurroundings(f, "nothing");
        for (int n : nothing) {
            set(n, walls[r.nextInt(N)]);
        }
    }

    /** given a position, return a list of positions
     * that surrounds it and matches the given type*/
    private List<Integer> getSurroundings(int p, String type) {
        List<Integer> matched = new ArrayList<>();
        int x = getX(p);
        int y = getY(p);

        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i == x && j == y) {
                    continue;
                }

                if (world[i][j].description().equals(type)) {
                    matched.add(toOneD(i, j));
                }
            }
        }

        return matched;
    }


    /** the portals satisfy
     * 1: not blocking any hallway
     * 2: far enough from other portals*/
    private void putPortals() {
        List<Integer> floors = tiles.get("floor");
        List<Integer> portals = new ArrayList<>();

        int n = RandomUtils.uniform(r, 3, 7);
        int iterationCount = 0;

        for (int i = 0; i < n; i++, iterationCount++) {
            if (iterationCount > 1000) {
                i++;
            }

            int f = floors.get(r.nextInt(floors.size()));
            if (getSurroundings(f, "floor").size() <= 6) {
                i--;
            } else if (!withinDist(portals, f, 100)) {
                remove(f);
                set(f, Tileset.PORTAL);
                portals.add(f);
            } else {
                i--;
                continue;
            }
        }

    }

    private void putCharacter() {
        List<Integer> floors = tiles.get("floor");

        int p = floors.get(r.nextInt(floors.size()));

        set(p, player.avatar);
        player.position = p;
    }

//    /** the chasers' location should be
//     * 1: at least 100 distance from the player
//     * 2: not on other items location*/
//    private void putChasers() {
//        int n = r.nextInt(3) + 2;
//        List<Integer> floors = tiles.get("floor");
//
//        for (int i = 0; i < n; i++) {
//            int f = floors.get(r.nextInt(floors.size()));
//            if (!withinDist(List.of(player.position), f, 100)
//                    && !tiles.get("portal").contains(f)
//                    && !tiles.get("vision").contains(f)) {
//                if (i != 0 && !withinDist(tiles.get("chaser"), f, 100)) {
//                    set(f, Tileset.CHASER);
//                } else if (i == 0){
//                    set(f, Tileset.CHASER);
//                } else {
//                    i--;
//                }
//            }
//        }
//    }

    private void addDoor() {
        List<Integer> walls = tiles.get("wall");

        int p = walls.get(r.nextInt(walls.size()));

        // make sure the door is reachable
        if (!validNeighbors(p).isEmpty()) {
            doorExist = true;
            set(p, Tileset.UNLOCKED_DOOR);
        } else {
            addDoor();
        }
    }

    private void putVisions() {

        int n = RandomUtils.uniform(r, 3, 6);

        int iterationCount = 0;
        for (int i = 0; i < n; i++, iterationCount++) {
            if (iterationCount > 100) {
                i++;
            }

            int p = r.nextInt(WIDTH * HEIGHT);
            if (tile(p).description().equals("floor")) {
                set(p, Tileset.VISION);
            } else {
                i--;
            }
        }
    }

    private void addLightning() {
        int toPut = r.nextInt(WIDTH * HEIGHT);
        if (tile(toPut).description().equals("floor")) {
            world[getX(toPut)][getY(toPut)] = Tileset.LIGHTNING;
        } else {
            addLightning();
        }
    }


    /** process the string after theSeed*/
    private void processCommands(String commands, int pauseMillis) {
//        System.out.println(commands);

        if (pauseMillis > 0) {
            hud();
            StdDraw.pause(pauseMillis);

            ter.renderFrame(worldInSight());

            if (player.lightning) {
                StdDraw.pause(3000);
            }
        }

        if (commands.length() == 0) {
            return;
        }

        if (commands.charAt(0) == ':'
                && commands.length() > 1) {
            feature(commands.charAt(1));
            if (pauseMillis > 0) {
                ter.renderFrame(worldInSight());
                if (commands.charAt(1) == 't') {
                    StdDraw.pause(500);
                }
            }
            processCommands(commands.substring(2), pauseMillis);
        } else {
            navigate(commands.charAt(0));
            processCommands(commands.substring(1), pauseMillis);
        }
    }

    /** handle "aswd" commands*/
    private void navigate(char command) {
        if (!navigation.containsValue(command)) {
            return;
        }

        int toP = player.position + toValue(String.valueOf(command));

        String dest = tile(toP).description();

        if (dest.equals("wall")) {
            return;
        }

        playerCommands += command;
        player.decrementHealth(1);

        if (dest.equals("unlocked door")) {
            nextWorld();
//            gameOver(true);
        } else if (player.health <= 0) {
            gameOver(false);
        } else if (dest.equals("vision")) {
            playSound("TheGame/Clips/Collect.wav");
            player.moveTo(toP);
            player.collectVision();
        } else if (dest.equals("portal")) {
            teleport(toP);
            playSound("TheGame/Clips/Teleport.wav");
        } else {
            player.moveTo(toP);
        }

        if (tiles.keySet().contains("vision")) {
            checkVisions();
        }
    }

    private void teleport(int origin) {
        List<Integer> portals = tiles.get("portal");
        int toP = portals.get(r.nextInt(portals.size()));

        if (origin != toP) {
            player.moveTo(toP);
        } else {
            teleport(origin);
        }
    }

    private void feature(char request) {
        if (request == 'q') {
            save();
            if (!stringInput) {
                System.exit(0);
            }
        } else if (request == 'n') {
            // a hidden option to start a new game while exploring
            Engine engine = new Engine();
            engine.interactWithKeyboard();
        } else if (colors.containsKey(request)) {
            player.changeAppearance(colors.get(request));
            set(player.position, player.avatar);
            playerCommands += ":" + request;
        } else if (request == 'l') { // self-killing lightning
            player.decrementHealth(8);
            if (player.sight > 1) {
                player.changeSight(-1);
            }
            player.lightning = true;
            playerCommands += ":l";
        } else if (request == 's') {
            save();
        } else if (request == 'f') {
            fullScreen = !fullScreen;
            playerCommands += ":f";
        } else if (request == 'd') {
            if (getX(player.position) == 1 || getX(player.position) == WIDTH - 1
                || getY(player.position) == 1 || getY(player.position) >= HEIGHT - 2 * HUD_HEIGHT - 1) {
                return;
            }

            List<Integer> walls = getSurroundings(player.position, "wall");
            if (walls.isEmpty()) {
                return;
            }

            playSound("TheGame/Clips/Damage.wav");
            for (int w : walls) {
                remove(w);
                set(w, Tileset.FLOOR);
                addWall(w);
            }

            player.decrementHealth(16);
            playerCommands += ":d";
        } else if (request == 't') {
            player.decrementHealth(10);
            List<Integer> floors = tiles.get("floor");

            while (true) {
                int toP = floors.get(r.nextInt(floors.size()));
                if (!withinDist(List.of(player.position), toP, 100)) {
                    player.moveTo(toP);
                    break;
                }
            }

            playSound("TheGame/Clips/Teleport.wav");
            playerCommands += ":t";
        }

    }

    private void checkVisions() {
        if (tiles.get("vision").size() < 3
                && player.sight < 17) {
            putVisions();
        }
    }

    private void nextWorld() {
        initialize(world);
        tiles.clear();
        player.i();
        generateWorld();
        fullScreen = false;
        player.incrementHealth(64);
        doorExist = false;
    }

    private void gameOver(boolean success) {
        if (stringInput) {
            return;
        }

        Font font = new Font("Monaco", Font.BOLD, 14);
        StdDraw.setFont(font);
        ter.renderFrame(world);

        StdDraw.text(WIDTH / 2, HEIGHT - 1.5, "██████████████████████");
        StdDraw.setFont(new Font("Times New Roman", Font.HANGING_BASELINE, 30));

        if (success) {
            StdDraw.setPenColor(Color.ORANGE);
            StdDraw.text(WIDTH / 2, HEIGHT - 1.5, "Congratulations!");
        } else {
            StdDraw.setPenColor(Color.RED);
            StdDraw.text(WIDTH / 2, HEIGHT - 1.5, "Score: " + player.score);
        }

        StdDraw.show();
        StdDraw.pause(5000);

        StdDraw.clear(Color.BLACK);

        save();

        Engine engine = new Engine();
        engine.interactWithKeyboard();
    }


    /**save the given string to "SavedGame.txt"
     * if the text already exists, overwrite it; else, create a new one and write it*/
    private void save() {

        try {
            File file = new File("TheGame/Core/SavedGame.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            if (playerCommands.contains(":q")) {
                playerCommands = playerCommands.substring(0, playerCommands.indexOf(":q"));
            }
            writer.write(theSeed + playerCommands);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* set the string's 'l' to be the content of "SavedGame.txt"*/
    private String loadGame(String string) {
        if (Character.toLowerCase(string.charAt(0)) != 'l') {
            return string;
        }


        try {
            File file = new File("TheGame/Core/SavedGame.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String saved = reader.readLine();

            return "n" + getSeed(saved)  + "s" + getCommands(saved) + string.substring(1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private void playSound(String path) {
        if (stringInput) {
            return;
        }

        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path).getAbsoluteFile());
            Clip c = AudioSystem.getClip();
            c.open(ais);
            c.loop(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void set(int p, TETile tile) {

        int x = getX(p);
        int y = getY(p);;

        world[x][y] = tile;

        String tileInfo = tile.description();

        // those info do not need to be recorded
        if (tileInfo.equals("trace") || tileInfo.equals("player")
                || tileInfo.equals("lightning")) {
            return;
        }

        if (tiles.containsKey(tileInfo)
                && !tiles.get(tileInfo).isEmpty()) {
            List<Integer> positions = tiles.get(tileInfo);
            if (!positions.contains(p)) {
                positions.add(p);
            }
        } else {
            List<Integer> first = new ArrayList<>();
            first.add(p);
            tiles.put(tileInfo, first);
        }
    }

    private void remove(int p) {
        List<Integer> list = tiles.get(tile(p).description());
        list.remove(list.indexOf(p));
    }


    /** if there exist any point in the given list of points
     * that is within the specified range (distance) of the root,
     * return true; false otherwise
     */
    private boolean withinDist(List<Integer> points, int root, double dist) {
        for (int p : points) {
            if (distance(p, root) < dist) {
                return true;
            }
        }

        return false;
    }

    /** get the "#####" part in "n######s" of a string*/
    private long getSeed(String string) {
        int posN = string.indexOf('n');
        int posS = string.indexOf('s');

        while (posS <= posN) {
            posS = string.indexOf('s', posS + 1);
        }

        return Long.parseLong(string.substring(posN + 1, posS));
    }

    /**get the commands part after the "n####s" part of a string*/
    private String getCommands(String string) {
        int posN = string.indexOf('n');
        int posS = string.indexOf('s');

        if (posN < posS) {
            return string.substring(posS + 1);
        }

        return getCommands(string.substring(posS + 1));
    }


    /** given a string of navigation, convert it into an integer value*/
    private int toValue(String nav) {
        if (nav.length() == 0) {
            return 0;
        }

        return charToInt(nav.charAt(0)) + toValue(nav.substring(1));
    }

    private int charToInt(char c) {
        for (int i : navigation.keySet()) {
            if (navigation.get(i) == c) {
                return i;
            }
        }

        return 0;
    }


    private TETile tile(int p) {
        return world[p / HEIGHT][p % HEIGHT];
    }

    private int toOneD(int i, int j) {
        return i * HEIGHT + j;
    }

    private int getX(int p) {
        return p / HEIGHT;
    }

    private int getY(int p) {
        return p % HEIGHT;
    }
}