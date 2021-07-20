package TheGame.Core;

import TheGame.TileEngine.TERenderer;
import TheGame.TileEngine.TETile;

public class Main {
    public static void main(String[] args) {
        if (args.length > 1) {
            System.out.println("Can only have one argument - the input string");
            System.exit(0);
        } else if (args.length == 1) {
            Engine engine = new Engine();
            TETile[][] tiles = engine.interactWithInputString(args[0]);
            TERenderer ter = new TERenderer();
            ter.initialize(Engine.WIDTH, Engine.HEIGHT);

            engine.ter.renderFrame(tiles);
            // System.out.println(TETile.toString(tiles));
        } else {
            Engine engine = new Engine();
            engine.interactWithKeyboard();
        }
    }
}


