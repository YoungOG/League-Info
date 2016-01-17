package code.calvin.leagueinfo.cells;

import code.calvin.leagueinfo.controllers.UserMenuController;
import code.calvin.leagueinfo.summoner.SummonerProfile;
import code.calvin.leagueinfo.summoner.SummonerProfileManager;
import com.robrua.orianna.type.core.common.Side;
import com.robrua.orianna.type.core.currentgame.Participant;
import com.robrua.orianna.type.core.league.League;
import com.robrua.orianna.type.core.league.LeagueEntry;
import com.robrua.orianna.type.core.stats.PlayerStatsSummary;
import com.robrua.orianna.type.core.stats.PlayerStatsSummaryType;
import com.sun.glass.ui.EventLoop;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.input.TouchPoint;
import javafx.util.Callback;

import java.util.List;
import java.util.Map;
import java.util.Observable;

public class SummonerCurrentMatchCell {

    private UserMenuController controller = UserMenuController.getInstance();
    private TableCell<SummonerCurrentMatchCell, String> cell;
    private Participant participant;
    private StringProperty name = new SimpleStringProperty("Loading...");
    private StringProperty season = new SimpleStringProperty();
    private StringProperty champion = new SimpleStringProperty();
    private StringProperty totalWins = new SimpleStringProperty();
    private StringProperty rankedWins = new SimpleStringProperty();
    private StringProperty KDA = new SimpleStringProperty();
    private StringProperty tier = new SimpleStringProperty();
    private boolean isLoaded = false;
    private ProgressIndicator indicator = new ProgressIndicator();

    public SummonerCurrentMatchCell(Participant participant) {
        this.participant = participant;

        loadData();
    }

//    public SummonerCurrentMatchCell(Participant participant) {
//        this.participant = participant;
//        this.name.setValue("Summoner432");
//        this.season.setValue("Silver I (0 LP)");
//        this.champion.setValue("Ekko");
//        this.totalWins.setValue("" + 624);
//        this.rankedWins.setValue("" + 306);
//        this.KDA.setValue("" + 8.2 + "/" + 3.0 + "/" + 3.9);
//        this.tier = "Silver";
//        this.isLoaded = true;
//
//        indicator.setVisible(false);
//    }

    public void loadData() {
        System.out.println("Loading Data for: " + participant.getSummonerName());

        SummonerProfile summonerProfile = SummonerProfileManager.getInstance().getSummonerProfile(participant.getSummoner());

        new Thread(() -> {
            Map<PlayerStatsSummaryType, PlayerStatsSummary> stats = summonerProfile.getPlayerStatsSummary();
            List<League> leagues = summonerProfile.getLeagues();

            int totalWins = 0;
            double averageKills = 0.0;
            double averageDeaths = 0.0;
            double averageAssists = 0.0;
            League league = null;
            LeagueEntry entry = null;

            if (stats != null) {
                for (PlayerStatsSummary summary : stats.values()) {
                    totalWins += summary.getWins();

                    if (summary.getType().toString().toLowerCase().contains("ranked")) {
                        averageKills += summary.getAggregatedStats().getAverageKills();
                        averageDeaths += summary.getAggregatedStats().getAverageDeaths();
                        averageAssists += summary.getAggregatedStats().getAverageAssists();
                    }
                }
            }

            if (leagues != null) {
                for (League lea : leagues) {
                    for (LeagueEntry ent : lea.getEntries()) {
                        if (ent.getName().equalsIgnoreCase(participant.getSummonerName())) {
                            league = lea;
                            entry = ent;
                        }
                    }
                }
            }

            final int finalTotalWins = totalWins;
            final League finalLeague = league;
            final double finalAverageDeaths = averageDeaths;
            final double finalAverageKills = averageKills;
            final double finalAverageAssists = averageAssists;
            final LeagueEntry finalEntry = entry;

            new Thread(() -> {
                try {
                    synchronized (this) {
                        while (summonerProfile.isUpdating()) {
                            this.wait();
                        }

                        this.name.setValue(participant.getSummonerName());
                        this.champion.setValue(participant.getChampion().getName());
                        this.season.setValue(finalLeague != null ? finalLeague.getTier().toString().substring(0, 1).toUpperCase() + finalLeague.getTier().toString().toLowerCase().substring(1) + " " + finalEntry.getDivision() + " (LP " + finalEntry.getLeaguePoints() + ")" : "Unranked");
                        this.rankedWins.setValue(finalLeague != null ? "" + finalEntry.getWins() + "/" + finalEntry.getLosses() : "" + 0);
                        this.totalWins.setValue("" + finalTotalWins);
                        this.KDA.setValue(finalAverageKills + "/" + finalAverageDeaths + "/" + finalAverageAssists);
                        this.tier.setValue(finalLeague != null ? finalLeague.getTier().toString().substring(0, 1).toUpperCase() + finalLeague.getTier().toString().toLowerCase().substring(1) : "");
                        this.isLoaded = true;

                        System.out.println("Finished loading: " + participant.getSummonerName() + ", updating cell");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }).start();
    }
    public Participant getParticipant() {
        return participant;
    }

    public StringProperty getName() {
        return name;
    }

    public StringProperty getChampion() {
        return champion;
    }

    public StringProperty getSeason() {
        return season;
    }

    public StringProperty getTotalWins() {
        return totalWins;
    }

    public StringProperty getRankedWins() {
        return rankedWins;
    }

    public StringProperty getKDA() {
        return KDA;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public ProgressIndicator getIndicator() {
        return indicator;
    }

    public StringProperty getTier() {
        return tier;
    }

    public void setCell(TableCell<SummonerCurrentMatchCell, String> cell) {
        this.cell = cell;
    }

    public TableCell<SummonerCurrentMatchCell, String> getCell() {
        return cell;
    }

    //    public boolean compareTypes(PlayerStatsSummaryType playerStatsSummaryType, QueueType queueType) {
//        switch (playerStatsSummaryType) {
//            case PlayerStatsSummaryType.RAN:
//                break;
//
//        }
//    }
}
