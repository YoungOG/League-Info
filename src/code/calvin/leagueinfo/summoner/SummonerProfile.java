package code.calvin.leagueinfo.summoner;

import code.calvin.leagueinfo.cells.SummonerCurrentMatchCell;
import code.calvin.leagueinfo.cells.SummonerMatchHistoryCell;
import code.calvin.leagueinfo.controllers.UserMenuController;
import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.core.common.Side;
import com.robrua.orianna.type.core.currentgame.CurrentGame;
import com.robrua.orianna.type.core.currentgame.Participant;
import com.robrua.orianna.type.core.game.Game;
import com.robrua.orianna.type.core.league.League;
import com.robrua.orianna.type.core.stats.PlayerStatsSummary;
import com.robrua.orianna.type.core.stats.PlayerStatsSummaryType;
import com.robrua.orianna.type.core.summoner.Summoner;
import com.robrua.orianna.type.exception.APIException;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SummonerProfile {

    private UserMenuController controller = UserMenuController.getInstance();
    private Summoner summoner;
    private Map<PlayerStatsSummaryType, PlayerStatsSummary> playerStatsSummary;
    private List<League> leagues;
    private CurrentGame currentGame;
    private List<Game> matchHistory;
    private boolean isSelected = false;
    private boolean updating = true;

    private Label l1;
    private Label l2;
    private Label l3;
    private Label l4;
    private Label l5;
    private Label l6;

    public SummonerProfile(Summoner summoner, boolean isSelected) {
        this.summoner = summoner;
        this.isSelected = isSelected;

        Runnable refreshRunnable = () -> {
            System.out.println("Refreshing: " + summoner.getName());
            update();
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(refreshRunnable, 0, 300, TimeUnit.SECONDS);

        l1 = new Label("Please wait\nLoading your game...");
        l1.setAlignment(Pos.CENTER);
        l1.setTextAlignment(TextAlignment.CENTER);

        l2 = new Label("Please wait\nLoading your game...");
        l2.setAlignment(Pos.CENTER);
        l2.setTextAlignment(TextAlignment.CENTER);

        l3 = new Label("Could not load game info\nThis summoner is not in a game");
        l3.setAlignment(Pos.CENTER);
        l3.setTextAlignment(TextAlignment.CENTER);

        l4 = new Label("Could not load game info\nThis summoner is not in a game");
        l4.setAlignment(Pos.CENTER);
        l4.setTextAlignment(TextAlignment.CENTER);

        l5 = new Label("Updating...");
        l5.setAlignment(Pos.CENTER);
        l5.setTextAlignment(TextAlignment.CENTER);
        l5.setGraphic(new ProgressIndicator());

        l6 = new Label("Updating...");
        l6.setAlignment(Pos.CENTER);
        l6.setTextAlignment(TextAlignment.CENTER);
        l6.setGraphic(new ProgressIndicator());

//        Label l5 = new Label("Please wait\nLoading match history");
//        l5.setAlignment(Pos.CENTER);
//        l5.setTextAlignment(TextAlignment.CENTER);
//
//        Label l6 = new Label("Could not load match history");
//        l6.setAlignment(Pos.CENTER);
//        l6.setTextAlignment(TextAlignment.CENTER);
    }

    public void update() {
        new Thread(() -> {
            try {
                playerStatsSummary = RiotAPI.getStats(summoner);
                System.out.println("Loaded Player Stats");

                leagues = RiotAPI.getLeaguesBySummoner(summoner);
                System.out.println("Loaded Leagues");

                if (isSelected) {
                    matchHistory = RiotAPI.getRecentGames(summoner);
                    System.out.println("Loaded Match History");
                }

                synchronized (this) {
                    updating = false;
                    this.notifyAll();
                }
            } catch (APIException ignored) {
                synchronized (this) {
                    System.out.println("Failed!");
                    updating = false;
                    this.notifyAll();
                }
            }
        }).start();
    }

    public void loadCurrentGame() {
        controller.getAlphaCurrentMatchCells().clear();
        controller.getBravoCurrentMatchCells().clear();
        controller.getAlphaTableView().setPlaceholder(new StackPane(new ProgressIndicator(), l1));
        controller.getBravoTableView().setPlaceholder(new StackPane(new ProgressIndicator(), l2));

        if (currentGame == null) {
            new Thread(() -> {
                try {
                    currentGame = RiotAPI.getCurrentGame(summoner);
                    Platform.runLater(this::setupCurrentGame);
                } catch (APIException ignored) {
                    ignored.printStackTrace();
                    controller.getAlphaCurrentMatchCells().clear();
                    controller.getBravoCurrentMatchCells().clear();
                    controller.getAlphaTableView().setPlaceholder(l3);
                    controller.getBravoTableView().setPlaceholder(l4);
                }
            }).start();
        } else {
            Platform.runLater(this::setupCurrentGame);
        }
    }

    public void setupCurrentGame() {
        if (currentGame == null) {
            controller.getAlphaCurrentMatchCells().clear();
            controller.getBravoCurrentMatchCells().clear();
            controller.getAlphaTableView().setPlaceholder(l3);
            controller.getBravoTableView().setPlaceholder(l4);
            return;
        }

        controller.getAlphaCurrentMatchCells().clear();
        controller.getBravoCurrentMatchCells().clear();


        for (Participant p : currentGame.getParticipants()) {
            if (!SummonerProfileManager.getInstance().hasSummonerProfile(p.getSummoner())) {
                SummonerProfileManager.getInstance().addSummoner(p.getSummoner(), false);
            }

            if (p.getTeam() == Side.BLUE) {
                controller.getAlphaCurrentMatchCells().add(new SummonerCurrentMatchCell(p));
            }

            if (p.getTeam() == Side.PURPLE) {
                controller.getBravoCurrentMatchCells().add(new SummonerCurrentMatchCell(p));
            }
        }

        controller.getAlphaTableView().setItems(UserMenuController.getInstance().getAlphaCurrentMatchCells());
        controller.getBravoTableView().setItems(UserMenuController.getInstance().getBravoCurrentMatchCells());
    }

    public void loadMatchHistory() {
        controller.getMatchHistoryCells().clear();

        if (matchHistory == null) {
            new Thread(() -> {
                try {
                    matchHistory = RiotAPI.getRecentGames(summoner);
                    setupMatchHistory();
                } catch (APIException ignored) {
                    ignored.printStackTrace();
                    controller.getMatchHistoryCells().clear();
                }
            }).start();
        }
    }

    public void setupMatchHistory() {
        if (matchHistory == null) {
            controller.getMatchHistoryCells().clear();
            return;
        }

        controller.getMatchHistoryCells().clear();

        System.out.println("" + matchHistory);

        for (Game game : matchHistory) {
            new Thread(() -> {
                try {
                    synchronized (this) {
                        while (updating) {
                            this.wait();
                        }

                        System.out.println("Game: " + game.getType());
                        SummonerMatchHistoryCell cell = new SummonerMatchHistoryCell(this, game);
                        controller.getMatchHistoryCells().add(cell);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        controller.getMatchHistoryListView().setItems(controller.getMatchHistoryCells());
    }

    public Summoner getSummoner() {
        return summoner;
    }

    public Map<PlayerStatsSummaryType, PlayerStatsSummary> getPlayerStatsSummary() {
        return playerStatsSummary;
    }

    public List<League> getLeagues() {
        return leagues;
    }

    public List<Game> getMatchHistory() {
        return matchHistory;
    }

    public CurrentGame getCurrentGame() {
        return currentGame;
    }

    public boolean isUpdating() {
        return updating;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
