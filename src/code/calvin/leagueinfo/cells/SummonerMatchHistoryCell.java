package code.calvin.leagueinfo.cells;

import code.calvin.leagueinfo.summoner.SummonerProfile;
import com.robrua.orianna.type.core.game.Game;
import javafx.scene.shape.SVGPath;

public class SummonerMatchHistoryCell {

    private SummonerProfile profile;
    private Game game;
    private boolean isExpanded = false;

    public SummonerMatchHistoryCell(SummonerProfile profile, Game game) {
        this.profile = profile;
        this.game = game;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    public SummonerProfile getProfile() {
        return profile;
    }

    public Game getGame() {
        return game;
    }
}
