package swingy.view.game;

import swingy.Main;
import swingy.controller.GameController;
import swingy.model.Game;
import swingy.util.Point;

import java.util.Scanner;

public class GameViewConsole implements GameView {

    private GameController controller;

    @Override
    public void start() {
        controller = new GameController(this);

        controller.onStart();
    }

    @Override
    public void update(Game game) {
        System.out.println("----------CHARACTER----------");
        System.out.println(game.getHero().toString() +
                "Position: " + "[" + game.getHeroCoord().getX() +
                "," + game.getHeroCoord().getY() + "]");
        System.out.println("-----------------------------");

        getUserInput();
    }

    private void getUserInput() {
        Scanner scanner = Main.getScanner();

        System.out.println("NORTH, EAST, SOUTH, WEST - to move to this direction");
        System.out.println("SWITCH - to switch to GUI view");
        System.out.println("Commands (NORTH, EAST, SOUTH, WEST, SWITCH):");
        while (scanner.hasNext()) {
            String input = scanner.nextLine();

            if ("map".equalsIgnoreCase(input)) {
                controller.onPrintMap();
                break;
            } else if ("north".equalsIgnoreCase(input) ||
                    "east".equalsIgnoreCase(input) ||
                    "south".equalsIgnoreCase(input) ||
                    "west".equalsIgnoreCase(input)) {
                controller.onMove(input);
                break;
            } else if ("switch".equalsIgnoreCase(input)) {
                controller.onSwitchButtonPressed();
                break;
            } else {
                System.out.println("Unknown command");
            }
        }
    }

    @Override
    public void printMap(boolean[][] map, Point heroCoord) {
        System.out.printf("MAP %dx%d", map.length, map.length);
        System.out.println();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (heroCoord.getX() == j && heroCoord.getY() == i)
                    System.out.print("H ");
                else if (map[i][j])
                    System.out.print("* ");
                else
                    System.out.print(". ");
            }
            System.out.println();
        }
    }

    @Override
    public void gameFinished() {
        System.out.println("See you!");
        Main.getFrame().dispose();
        Main.closeConnections();
    }

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void getVillainCollisionInput() {
        Scanner scanner = Main.getScanner();

        System.out.println();
        System.out.println("There is an enemy!");
        System.out.println("FIGHT - to fight with enemy");
        System.out.println("RUN - to run, 50% chance to move to the previous position");
        System.out.println("Commands (FIGHT, RUN):");
        while (scanner.hasNext()) {
            String input = scanner.nextLine();

            if ("fight".equalsIgnoreCase(input)) {
                controller.onFight();
                break;
            } else if ("run".equalsIgnoreCase(input)) {
                controller.onRun();
                break;
            } else {
                System.out.println("Unknown command");
            }
        }
    }

    @Override
    public boolean replaceArtifact(String replaceMessage) {
        Scanner scanner = Main.getScanner();

        System.out.println();
        System.out.println("Would you like to replace " + replaceMessage + "?");
        System.out.println("LEAVE - to leave your artifact");
        System.out.println("REPLACE - to replace by new artifact");
        System.out.println("Commands (LEAVE, REPLACE):");
        while (scanner.hasNext()) {
            String input = scanner.nextLine();

            if ("leave".equalsIgnoreCase(input)) {
                return false;
            } else if ("replace".equalsIgnoreCase(input)) {
                return true;
            } else {
                System.out.println("Unknown command");
            }
        }
        return false;
    }

    @Override
    public void switchView() {
        new GameViewGUI().start();
    }
}
