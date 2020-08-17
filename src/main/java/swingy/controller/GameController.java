package swingy.controller;

import swingy.model.Game;
import swingy.model.artifact.Armor;
import swingy.model.artifact.Artifact;
import swingy.model.artifact.Helm;
import swingy.model.artifact.Weapon;
import swingy.model.character.Hero;
import swingy.model.character.Villain;
import swingy.util.DataBase;
import swingy.util.Point;
import swingy.view.game.GameView;

import java.util.Random;

public class GameController {

    private GameView view;
    private Game game;
    private Point previousPosition;

    public GameController(GameView view) {
        this.view = view;
        game = Game.getInstance();
        previousPosition = new Point(0, 0);
    }

    public void onStart() {
        view.update(game);
    }

    public void onPrintMap() {
        view.printMap(game.getMap(), game.getHeroCoord());
        view.update(game);
    }

    public void onMove(String direction) {
        int x = game.getHeroCoord().getX();
        int y = game.getHeroCoord().getY();
        previousPosition.setX(x);
        previousPosition.setY(y);

        switch (direction.toUpperCase()) {
            case "NORTH":
                y--;
                break;
            case "EAST":
                x++;
                break;
            case "SOUTH":
                y++;
                break;
            case "WEST":
                x--;
                break;
        }

        if (x < 0 || y < 0 || x >= game.getMapSize() || y >= game.getMapSize()) {
            winGame();
            return;
        }

        game.getHeroCoord().setX(x);
        game.getHeroCoord().setY(y);

        if (game.getMap()[y][x]) {
            villainCollision();
        }

        if (game.getHero().getHitPoints() > 0)
            view.update(game);
    }

    private void winGame() {
        view.showMessage("You win! And got additional " + game.getMapSize() * 100 + "xp!");
        addExperience(game.getMapSize() * 100);
        updateDataBase();
        view.gameFinished();
    }

    private void updateDataBase() {
        Hero hero = game.getHero();
        DataBase.updateHero(hero);
    }

    private void villainCollision() {
        view.getVillainCollisionInput();
    }

    public void onRun() {
        if (new Random().nextBoolean()) {
            view.showMessage("You are lucky! And moved to previous position!");
            game.getHeroCoord().setX(previousPosition.getX());
            game.getHeroCoord().setY(previousPosition.getY());
        } else {
            view.showMessage("You have to fight");
            onFight();
        }
    }

    private void setArtifact(Artifact artifact) {
        if (artifact != null) {
            if (artifact instanceof Weapon) {
                if (game.getHero().getWeapon() == null || view.replaceArtifact("your weapon: " + game.getHero().getWeapon() + ", found: " + artifact)) {
                    game.getHero().equipWeapon((Weapon) artifact);
                    view.showMessage("You equipped new weapon: " + artifact);
                }
            } else if (artifact instanceof Helm) {
                if (game.getHero().getHelm() == null || view.replaceArtifact("your helmet: " + game.getHero().getHelm() + ", found: " + artifact)) {
                    game.getHero().equipHelm((Helm) artifact);
                    view.showMessage("You equipped new helm: " + artifact);
                }
            } else if (artifact instanceof Armor) {
                if (game.getHero().getArmor() == null || view.replaceArtifact("your armor: " + game.getHero().getArmor() + ", found: " + artifact)) {
                    game.getHero().equipArmor((Armor) artifact);
                    view.showMessage("You equipped new armor: " + artifact);
                }
            }
        }
    }

    public void onFight() {
        Villain villain = game.generateVillain();
        int xp = game.fightResult(villain);

        if (xp >= 0) {
            view.showMessage("You win, and got " + xp + "xp.");
            addExperience(xp);
            game.getMap()[game.getHeroCoord().getY()][game.getHeroCoord().getX()] = false;
            setArtifact(villain.getArtifact());
        } else {
            view.showMessage("Game over :(");
            view.gameFinished();
        }
    }

    private void addExperience(int addXP) {
        int level = game.getHero().getLevel();
        game.getHero().addExperience(addXP);
        if (level != game.getHero().getLevel())
            view.showMessage("Level UP!\nHP, attack and defense were increased!");
    }

    public void onSwitchButtonPressed() {
        view.switchView();
    }
}
