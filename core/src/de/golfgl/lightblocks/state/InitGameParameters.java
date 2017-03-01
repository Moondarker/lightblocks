package de.golfgl.lightblocks.state;

import de.golfgl.lightblocks.model.GameModel;

/**
 * This class defines new games.
 *
 * Created by Benjamin Schulte on 21.02.2017.
 */

public class InitGameParameters {

    private int inputKey;
    private int beginningLevel;
    private Class<? extends GameModel> gameModelClass;

    public int getInputKey() {
        return inputKey;
    }

    public void setInputKey(int inputKey) {
        this.inputKey = inputKey;
    }

    public int getBeginningLevel() {
        return beginningLevel;
    }

    public void setBeginningLevel(int beginningLevel) {
        this.beginningLevel = beginningLevel;
    }

    public Class<? extends GameModel> getGameModelClass() {
        return gameModelClass;
    }

    public void setGameModelClass(Class<? extends GameModel> gameModelClass) {
        this.gameModelClass = gameModelClass;
    }
}
