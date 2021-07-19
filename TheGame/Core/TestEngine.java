package TheGame.Core;

import TheGame.TileEngine.TERenderer;
import TheGame.TileEngine.TETile;

import java.util.Random;

public class TestEngine {
    public static void main(String[] args) {
        for (int i = 0; i < 10000; i++) {
            Random rand = new Random();

            String seed = "n" + Math.abs(rand.nextLong()) + "s";
            Engine engine = new Engine();
            TETile[][] tiles = engine.interactWithInputString(seed);
        }

    }
}


