package code.calvin.leagueinfo.controllers;

import code.calvin.leagueinfo.cells.SummonerCurrentMatchCell;
import code.calvin.leagueinfo.cells.SummonerMatchHistoryCell;
import code.calvin.leagueinfo.cells.SummonerSearchCell;
import code.calvin.leagueinfo.summoner.SummonerProfile;
import code.calvin.leagueinfo.summoner.SummonerProfileManager;
import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.core.stats.PlayerStatsSummary;
import com.robrua.orianna.type.core.stats.PlayerStatsSummaryType;
import com.robrua.orianna.type.core.summoner.Summoner;
import com.robrua.orianna.type.exception.APIException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import javafx.util.Duration;

import java.lang.reflect.Field;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class UserMenuController implements Initializable {

    private static UserMenuController instance;

    @FXML
    private TextField summonerSearchBar;
    @FXML
    private ListView<SummonerSearchCell> summonerList;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab currentMatchTab;
    @FXML
    private Tab overviewTab;
    @FXML
    private Tab matchHistoryTab;
    @FXML
    private VBox currentMatchVBox;

    //Alpha Table View and Columns
    @FXML
    private TableView<SummonerCurrentMatchCell> alphaTableView;
    @FXML
    private TableColumn<SummonerCurrentMatchCell, String> alphaNameColumn;
    @FXML
    private TableColumn<SummonerCurrentMatchCell, String> alphaChampionColumn;
    @FXML
    private TableColumn<SummonerCurrentMatchCell, String> alphaSeasonColumn;
    @FXML
    private TableColumn<SummonerCurrentMatchCell, String> alphaWinsColumn;
    @FXML
    private TableColumn<SummonerCurrentMatchCell, String> alphaRankedWinsColumn;
    @FXML
    private TableColumn<SummonerCurrentMatchCell, String> alphaKDAColumn;

    //Bravo Table View and Columns
    @FXML
    private TableView<SummonerCurrentMatchCell> bravoTableView;
    @FXML
    private TableColumn<SummonerCurrentMatchCell, String> bravoNameColumn;
    @FXML
    private TableColumn<SummonerCurrentMatchCell, String> bravoChampionColumn;
    @FXML
    private TableColumn<SummonerCurrentMatchCell, String> bravoSeasonColumn;
    @FXML
    private TableColumn<SummonerCurrentMatchCell, String> bravoWinsColumn;
    @FXML
    private TableColumn<SummonerCurrentMatchCell, String> bravoRankedWinsColumn;
    @FXML
    private TableColumn<SummonerCurrentMatchCell, String> bravoKDAColumn;

    //Match History ListView
    @FXML
    private ListView<SummonerMatchHistoryCell> matchHistoryListView;

    //Stats
    @FXML
    private Label public_total_wins;
    @FXML
    private Label public_total_kills;
    @FXML
    private Label public_gold_earned;
    @FXML
    private Label public_minions_killed;
    @FXML
    private Label public_total_assists;
    @FXML
    private Label public_turrets_downed;

    @FXML
    private Label ranked_penta_kills;
    @FXML
    private Label ranked_double_kills;
    @FXML
    private Label ranked_gold_earned;
    @FXML
    private Label ranked_quadra_kills;
    @FXML
    private Label ranked_total_kills;
    @FXML
    private Label ranked_turrets_downed;
    @FXML
    private Label ranked_triple_kills;
    @FXML
    private Label ranked_total_assists;
    @FXML
    private Label ranked_killing_sprees;

    private ObservableList<SummonerCurrentMatchCell> alphaCurrentMatchCells = FXCollections.observableArrayList();
    private ObservableList<SummonerCurrentMatchCell> bravoCurrentMatchCells = FXCollections.observableArrayList();
    private ObservableList<SummonerMatchHistoryCell> matchHistoryCells = FXCollections.observableArrayList();

    public UserMenuController() {
        instance = this;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainTabPane.managedProperty().bind(mainTabPane.visibleProperty());
        mainTabPane.setVisible(false);

        initializeCurrentGameModules();

        System.out.println("View Loaded!");
    }

    @FXML
    public void handleSearchButton(ActionEvent event) throws Exception {
        mainTabPane.getSelectionModel().clearSelection();

        if (mainTabPane.isVisible()) {
            mainTabPane.setVisible(false);
        }

        if (summonerSearchBar.getText().length() > 0) {
            String summonerName = summonerSearchBar.getText();
            SummonerProfile summonerProfile = null;

            if (!SummonerProfileManager.getInstance().hasSummonerProfile(summonerName)) {
                try {
                    Summoner result = RiotAPI.getSummonerByName(summonerName);
                    SummonerProfileManager.getInstance().addSummoner(result, true);
                    summonerProfile = SummonerProfileManager.getInstance().getSummonerProfile(summonerName);
                } catch (APIException e) {
                    e.printStackTrace();
                }
            } else {
                summonerProfile = SummonerProfileManager.getInstance().getSummonerProfile(summonerName);
            }

            if (summonerProfile == null) {
                return;
            }

            summonerList.getItems().clear();
            summonerList.getItems().add(new SummonerSearchCell(summonerProfile.getSummoner()));
            summonerList.setCellFactory(new Callback<ListView<SummonerSearchCell>, ListCell<SummonerSearchCell>>() {
                @Override
                public ListCell<SummonerSearchCell> call(ListView<SummonerSearchCell> param) {
                    return new ListCell<SummonerSearchCell>() {

                        @Override
                        public void updateItem(SummonerSearchCell cell, boolean empty) {
                            super.updateItem(cell, empty);

                            if (cell != null) {
                                SummonerProfile profile = SummonerProfileManager.getInstance().getSummonerProfile(cell.getSummoner());

                                VBox vBox = new VBox(new Text(profile.getSummoner().getName()), new Text("Level " + profile.getSummoner().getLevel()));

                                HBox hBox = new HBox(new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("summoner_icons/ProfileIcon08.jpg"), 38, 38, true, true)), vBox);
                                hBox.setSpacing(5);

                                setGraphic(hBox);
                            }
                        }
                    };
                }
            });

            summonerList.getSelectionModel().selectedItemProperty().addListener((observable, oldCell, newCell) -> {
                if (!mainTabPane.isVisible()) {
                    mainTabPane.setVisible(true);
                }

                if (newCell != null) {
                    SummonerProfile profile = SummonerProfileManager.getInstance().getSummonerProfile(newCell.getSummoner());

                    mainTabPane.getSelectionModel().select(overviewTab);
                    mainTabPane.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
                        if (newTab != null) {
                            if (newTab.equals(currentMatchTab)) {
                                profile.loadCurrentGame();
                            } else if (newTab.equals(matchHistoryTab)) {
                                profile.loadMatchHistory();
                            }
                        }
                    });

                    Map<PlayerStatsSummaryType, PlayerStatsSummary> stats = profile.getPlayerStatsSummary();

                    int p_total_wins = 0,
                            p_total_kills = 0,
                            p_gold_earned = 0,
                            p_minions_killed = 0,
                            p_total_assists = 0,
                            p_turrets_downed = 0;

                    int r_penta_kills = 0,
                            r_double_kills = 0,
                            r_gold_earned = 0,
                            r_quadra_kills = 0,
                            r_total_kills = 0,
                            r_turrets_downed = 0,
                            r_triple_kills = 0,
                            r_total_assists = 0,
                            r_killing_sprees = 0;

                    for (PlayerStatsSummary summary : stats.values()) {
                        //Public Stats
                        if (summary.getType() == PlayerStatsSummaryType.Unranked || summary.getType() == PlayerStatsSummaryType.Unranked3x3 || summary.getType() == PlayerStatsSummaryType.OdinUnranked || summary.getType() == PlayerStatsSummaryType.AramUnranked5x5) {
                            p_total_wins += summary.getWins();
                            p_total_kills += summary.getAggregatedStats().getTotalKills();
                            p_gold_earned += summary.getAggregatedStats().getTotalGoldEarned();
                            p_minions_killed += summary.getAggregatedStats().getTotalMinionKills();
                            p_total_assists += summary.getAggregatedStats().getTotalAssists();
                            p_turrets_downed += summary.getAggregatedStats().getTotalTurretsKilled();
                        }

                        //Ranked Stats
                        if (summary.getType() == PlayerStatsSummaryType.RankedSolo5x5 || summary.getType() == PlayerStatsSummaryType.RankedTeam5x5) {
                            r_penta_kills += summary.getAggregatedStats().getTotalPentaKills();
                            r_double_kills += summary.getAggregatedStats().getTotalDoubleKills();
                            r_gold_earned += summary.getAggregatedStats().getTotalGoldEarned();
                            r_quadra_kills += summary.getAggregatedStats().getTotalQuadraKills();
                            r_total_kills += summary.getAggregatedStats().getTotalKills();
                            r_turrets_downed += summary.getAggregatedStats().getTotalTurretsKilled();
                            r_triple_kills += summary.getAggregatedStats().getTotalTripleKills();
                            r_total_assists += summary.getAggregatedStats().getTotalAssists();
                            r_killing_sprees += summary.getAggregatedStats().getKillingSprees();
                        }
                    }

                    public_total_wins.setText("" + NumberFormat.getInstance().format(p_total_wins));
                    public_total_kills.setText("" + NumberFormat.getInstance().format(p_total_kills));
                    public_gold_earned.setText("" + NumberFormat.getInstance().format(p_gold_earned));
                    public_minions_killed.setText("" + NumberFormat.getInstance().format(p_minions_killed));
                    public_total_assists.setText("" + NumberFormat.getInstance().format(p_total_assists));
                    public_turrets_downed.setText("" + NumberFormat.getInstance().format(p_turrets_downed));

                    ranked_penta_kills.setText("" + NumberFormat.getInstance().format(r_penta_kills));
                    ranked_double_kills.setText("" + NumberFormat.getInstance().format(r_double_kills));
                    ranked_gold_earned.setText("" + NumberFormat.getInstance().format(r_gold_earned));
                    ranked_quadra_kills.setText("" + NumberFormat.getInstance().format(r_quadra_kills));
                    ranked_total_kills.setText("" + NumberFormat.getInstance().format(r_total_kills));
                    ranked_turrets_downed.setText("" + NumberFormat.getInstance().format(r_turrets_downed));
                    ranked_triple_kills.setText("" + NumberFormat.getInstance().format(r_triple_kills));
                    ranked_total_assists.setText("" + NumberFormat.getInstance().format(r_total_assists));
                    ranked_killing_sprees.setText("" + NumberFormat.getInstance().format(r_killing_sprees));
                } else {
                    mainTabPane.getSelectionModel().clearSelection();
                    mainTabPane.setVisible(false);
                }
            });
        }
    }

    public void initializeCurrentGameModules() {
        alphaTableView.setStyle("-fx-table-cell-border-color: transparent;");

        matchHistoryListView.setCellFactory(cell -> new ListCell<SummonerMatchHistoryCell>() {
            @Override
            protected void updateItem(SummonerMatchHistoryCell item, boolean empty) {
                super.updateItem(item, empty);
                final VBox vbox = new VBox();

                if (item != null) {
                    HBox h1 = new HBox();

                    Label mode = new Label("NORMAL 5V5");
                    mode.setAlignment(Pos.CENTER);
                    mode.setMaxWidth(Double.MAX_VALUE);
                    mode.setTextAlignment(TextAlignment.CENTER);

                    Label when = new Label("1 HOUR AGO");
                    when.setAlignment(Pos.CENTER);
                    when.setMaxWidth(Double.MAX_VALUE);
                    when.setTextAlignment(TextAlignment.CENTER);

                    Label duration = new Label("32 MINUTES");
                    duration.setAlignment(Pos.CENTER);
                    duration.setMaxWidth(Double.MAX_VALUE);
                    duration.setTextAlignment(TextAlignment.CENTER);

                    Label result = new Label("WIN");
                    result.setAlignment(Pos.CENTER);
                    result.setMaxWidth(Double.MAX_VALUE);
                    result.setTextAlignment(TextAlignment.CENTER);

                    h1.getChildren().addAll(mode, when, duration, result);
                    h1.setHgrow(mode, Priority.ALWAYS);
                    h1.setHgrow(when, Priority.ALWAYS);
                    h1.setHgrow(duration, Priority.ALWAYS);
                    h1.setHgrow(result, Priority.ALWAYS);

                    HBox h2 = new HBox();

                    ImageView spell1 = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("summoner_spells/SummonerFlash.png"), 40.5, 40.5, true, true));
                    spell1.setPreserveRatio(true);
                    Tooltip tt1 = new Tooltip("Flash");
                    hackTooltip(tt1);
                    tt1.setAutoFix(true);
                    tt1.setGraphic(new ImageView(spell1.getImage()));
                    Tooltip.install(spell1, tt1);

                    ImageView spell2 = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("summoner_spells/SummonerIgnite.png"), 40.5, 40.5, true, true));
                    spell2.setPreserveRatio(true);
                    Tooltip tt2 = new Tooltip("Ignite");
                    hackTooltip(tt2);
                    tt2.setAutoFix(true);
                    tt2.setGraphic(new ImageView(spell2.getImage()));
                    Tooltip.install(spell2, tt2);

                    VBox spellBox = new VBox(spell1, spell2);
                    spellBox.setAlignment(Pos.CENTER_LEFT);
                    spellBox.setSpacing(4.5);

                    ImageView champIcon = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("champion_squares/Ekko.png"), 85, 85, true, true));

                    h2.getChildren().addAll(champIcon, spellBox);
                    h2.setSpacing(4.5);

                    HBox h3 = new HBox();

                    Hyperlink link = new Hyperlink("Toggle Details");
                    link.setAlignment(Pos.CENTER);
                    link.setTextAlignment(TextAlignment.CENTER);

                    TextFlow flow = new TextFlow(link);
                    flow.setTextAlignment(TextAlignment.CENTER);

                    h3.getChildren().addAll(flow);

                    vbox.getChildren().addAll(h1, h2, h3);
                    vbox.setPadding(new Insets(8, 8, 8, 8));

                    setGraphic(vbox);
                }
            }
        });

        //Alpha Team Table
        {
            //Name
            alphaNameColumn.setCellValueFactory(cellData -> cellData.getValue().getName());
            alphaNameColumn.setCellFactory(cell -> new TableCell<SummonerCurrentMatchCell, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setAlignment(Pos.CENTER_LEFT);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        if (getTableRow().getItem() != null) {
                            if (!((SummonerCurrentMatchCell) getTableRow().getItem()).isLoaded()) {
                                Label l = new Label("Updating...");
                                l.setAlignment(Pos.CENTER);
                                l.setTextAlignment(TextAlignment.CENTER);
                                l.setGraphic(new ProgressIndicator());

                                setGraphic(l);
                                setText(null);
                            } else {
                                setText(null);

                                VBox vBox = new VBox(new Text(item), new Text("Level " + ((SummonerCurrentMatchCell) getTableRow().getItem()).getParticipant().getSummoner().getLevel()));
                                vBox.setAlignment(Pos.CENTER_LEFT);

                                HBox hBox = new HBox(new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("summoner_icons/ProfileIcon08.jpg"), 38, 38, true, true)), vBox);
                                hBox.setSpacing(5);
                                hBox.setAlignment(Pos.CENTER_LEFT);

                                setGraphic(hBox);
                            }
                        } else {
                            Label l = new Label("Failed to load");
                            l.setTextFill(Paint.valueOf("Red"));
                            l.setAlignment(Pos.CENTER);
                            l.setTextAlignment(TextAlignment.CENTER);

                            setGraphic(l);
                            setText(null);
                        }
                    }
                }
            });

            //Champion
            alphaChampionColumn.setCellValueFactory(cellData -> cellData.getValue().getChampion());
            alphaChampionColumn.setCellFactory(column -> new TableCell<SummonerCurrentMatchCell, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setAlignment(Pos.CENTER_LEFT);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        if (getTableRow().getItem() != null) {
                            if (!((SummonerCurrentMatchCell) getTableRow().getItem()).isLoaded()) {
                                SummonerCurrentMatchCell mCell = ((SummonerCurrentMatchCell) getTableRow().getItem());

                                setText(item);
                                setGraphic(mCell.getIndicator());
                            } else {
                                ImageView spell1 = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("summoner_spells/Summoner" + ((SummonerCurrentMatchCell) getTableRow().getItem()).getParticipant().getSummonerSpell1().getName() + ".png"), 22, 22, true, true));
                                spell1.setPreserveRatio(true);
                                Tooltip tt1 = new Tooltip("" + ((SummonerCurrentMatchCell) getTableRow().getItem()).getParticipant().getSummonerSpell1().getName());
                                hackTooltip(tt1);
                                tt1.setAutoFix(true);
                                tt1.setGraphic(new ImageView(spell1.getImage()));
                                Tooltip.install(spell1, tt1);

                                ImageView spell2 = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("summoner_spells/Summoner" + ((SummonerCurrentMatchCell) getTableRow().getItem()).getParticipant().getSummonerSpell2().getName() + ".png"), 22, 22, true, true));
                                spell2.setPreserveRatio(true);
                                Tooltip tt2 = new Tooltip("" + ((SummonerCurrentMatchCell) getTableRow().getItem()).getParticipant().getSummonerSpell2().getName());
                                hackTooltip(tt2);
                                tt2.setAutoFix(true);
                                tt2.setGraphic(new ImageView(spell2.getImage()));
                                Tooltip.install(spell2, tt2);

                                VBox vBox = new VBox(spell1, spell2);
                                vBox.setAlignment(Pos.CENTER_LEFT);
                                vBox.setSpacing(2.5);

                                ImageView square = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("champion_squares/" + ((SummonerCurrentMatchCell) getTableRow().getItem()).getParticipant().getChampion().getName() + ".png"), 42, 42, true, true));

                                HBox hBox = new HBox(square, vBox, new Text(item));
                                hBox.setSpacing(3);
                                hBox.setAlignment(Pos.CENTER_LEFT);

                                setGraphic(hBox);
                            }
                        }
                    }
                }
            });

            //Season
            alphaSeasonColumn.setCellValueFactory(cellData -> cellData.getValue().getSeason());
            alphaSeasonColumn.setCellFactory(column -> new TableCell<SummonerCurrentMatchCell, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setAlignment(Pos.CENTER);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        if (getTableRow().getItem() != null) {
                            if (!((SummonerCurrentMatchCell) getTableRow().getItem()).isLoaded()) {
                                SummonerCurrentMatchCell mCell = ((SummonerCurrentMatchCell) getTableRow().getItem());

                                setText(item);
                                setGraphic(mCell.getIndicator());
                            } else {
                                VBox vBox = new VBox(new Text(item));
                                vBox.setAlignment(Pos.CENTER_LEFT);

                                HBox hBox;

                                if (!((SummonerCurrentMatchCell) getTableRow().getItem()).getSeason().toString().contains("Unranked")) {
                                    ImageView badge = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("rank_badges/" + ((SummonerCurrentMatchCell) getTableRow().getItem()).getTier().get() + ".png"), 29, 29, true, true));
                                    hBox = new HBox(badge, vBox);
                                } else {
                                    hBox = new HBox(vBox);
                                }

                                hBox.setSpacing(5);
                                hBox.setAlignment(Pos.CENTER_LEFT);

                                setGraphic(hBox);
                            }
                        }
                    }
                }
            });

            //Wins
            alphaWinsColumn.setCellValueFactory(cellData -> cellData.getValue().getTotalWins());
            alphaWinsColumn.setCellFactory(column -> new TableCell<SummonerCurrentMatchCell, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setAlignment(Pos.CENTER);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        if (getTableRow().getItem() != null) {
                            if (!((SummonerCurrentMatchCell) getTableRow().getItem()).isLoaded()) {
                                SummonerCurrentMatchCell mCell = ((SummonerCurrentMatchCell) getTableRow().getItem());

                                setText(item);
                                setGraphic(mCell.getIndicator());
                            } else {
                                setText(item);
                                setGraphic(null);
                                setTooltip(new Tooltip("Summoner Level\nLevel " + ((SummonerCurrentMatchCell) getTableRow().getItem()).getParticipant().getSummoner().getLevel()));
                            }
                        }
                    }
                }
            });

            //Ranked Wins
            alphaRankedWinsColumn.setCellValueFactory(cellData -> cellData.getValue().getRankedWins());
            alphaRankedWinsColumn.setCellFactory(column -> new TableCell<SummonerCurrentMatchCell, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setAlignment(Pos.CENTER);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        if (getTableRow().getItem() != null) {
                            if (!((SummonerCurrentMatchCell) getTableRow().getItem()).isLoaded()) {
                                SummonerCurrentMatchCell mCell = ((SummonerCurrentMatchCell) getTableRow().getItem());

                                setText(item);
                                setGraphic(mCell.getIndicator());
                            } else {
                                setText(item);
                                setGraphic(null);
                            }
                        }
                    }
                }
            });

            //KDA
            alphaKDAColumn.setCellValueFactory(cellData -> cellData.getValue().getKDA());
            alphaKDAColumn.setCellFactory(column -> new TableCell<SummonerCurrentMatchCell, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setAlignment(Pos.CENTER);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        if (getTableRow().getItem() != null) {
                            if (!((SummonerCurrentMatchCell) getTableRow().getItem()).isLoaded()) {
                                SummonerCurrentMatchCell mCell = ((SummonerCurrentMatchCell) getTableRow().getItem());

                                setText(item);
                                setGraphic(mCell.getIndicator());
                            } else {
                                setText(item);
                                setGraphic(null);
                            }
                        }
                    }
                }
            });
        }

        //Bravo Team Table
        {
            //Name
            bravoNameColumn.setCellValueFactory(cellData -> cellData.getValue().getName());
            bravoNameColumn.setCellFactory(cell -> new TableCell<SummonerCurrentMatchCell, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setAlignment(Pos.CENTER_LEFT);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        if (getTableRow().getItem() != null) {
                            if (!((SummonerCurrentMatchCell) getTableRow().getItem()).isLoaded()) {
                                Label l = new Label("Updating...");
                                l.setAlignment(Pos.CENTER);
                                l.setTextAlignment(TextAlignment.CENTER);
                                l.setGraphic(new ProgressIndicator());

                                setGraphic(l);
                                setText(null);
                            } else {
                                setText(null);

                                VBox vBox = new VBox(new Text(item), new Text("Level " + ((SummonerCurrentMatchCell) getTableRow().getItem()).getParticipant().getSummoner().getLevel()));
                                vBox.setAlignment(Pos.CENTER_LEFT);

                                HBox hBox = new HBox(new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("summoner_icons/ProfileIcon08.jpg"), 38, 38, true, true)), vBox);
                                hBox.setSpacing(5);
                                hBox.setAlignment(Pos.CENTER_LEFT);

                                setGraphic(hBox);
                            }
                        } else {
                            Label l = new Label("Failed to load");
                            l.setTextFill(Paint.valueOf("Red"));
                            l.setAlignment(Pos.CENTER);
                            l.setTextAlignment(TextAlignment.CENTER);

                            setGraphic(l);
                            setText(null);
                        }
                    }
                }
            });

            //Champion
            bravoChampionColumn.setCellValueFactory(cellData -> cellData.getValue().getChampion());
            bravoChampionColumn.setCellFactory(column -> new TableCell<SummonerCurrentMatchCell, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setAlignment(Pos.CENTER_LEFT);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        if (getTableRow().getItem() != null) {
                            if (!((SummonerCurrentMatchCell) getTableRow().getItem()).isLoaded()) {
                                SummonerCurrentMatchCell mCell = ((SummonerCurrentMatchCell) getTableRow().getItem());

                                setText(item);
                                setGraphic(mCell.getIndicator());
                            } else {
                                ImageView spell1 = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("summoner_spells/Summoner" + ((SummonerCurrentMatchCell) getTableRow().getItem()).getParticipant().getSummonerSpell1().getName() + ".png"), 19, 19, true, true));
                                spell1.setPreserveRatio(true);
                                Tooltip tt1 = new Tooltip("" + ((SummonerCurrentMatchCell) getTableRow().getItem()).getParticipant().getSummonerSpell1().getName());
                                hackTooltip(tt1);
                                tt1.setAutoFix(true);
                                tt1.setGraphic(new ImageView(spell1.getImage()));
                                Tooltip.install(spell1, tt1);

                                ImageView spell2 = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("summoner_spells/Summoner" + ((SummonerCurrentMatchCell) getTableRow().getItem()).getParticipant().getSummonerSpell2().getName() + ".png"), 19, 19, true, true));
                                spell2.setPreserveRatio(true);
                                Tooltip tt2 = new Tooltip("" + ((SummonerCurrentMatchCell) getTableRow().getItem()).getParticipant().getSummonerSpell2().getName());
                                hackTooltip(tt2);
                                tt2.setAutoFix(true);
                                tt2.setGraphic(new ImageView(spell2.getImage()));
                                Tooltip.install(spell2, tt2);

                                VBox vBox = new VBox(spell1, spell2);
                                vBox.setAlignment(Pos.CENTER_LEFT);
                                vBox.setSpacing(2.5);

                                ImageView square = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("champion_squares/" + ((SummonerCurrentMatchCell) getTableRow().getItem()).getParticipant().getChampion().getName() + ".png"), 36, 36, true, true));

                                HBox hBox = new HBox(square, vBox, new Text(item));
                                hBox.setSpacing(3);
                                hBox.setAlignment(Pos.CENTER_LEFT);

                                setGraphic(hBox);
                            }
                        }
                    }
                }
            });

            //Season
            bravoSeasonColumn.setCellValueFactory(cellData -> cellData.getValue().getSeason());
            bravoSeasonColumn.setCellFactory(column -> new TableCell<SummonerCurrentMatchCell, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setAlignment(Pos.CENTER);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        if (getTableRow().getItem() != null) {
                            if (!((SummonerCurrentMatchCell) getTableRow().getItem()).isLoaded()) {
                                SummonerCurrentMatchCell mCell = ((SummonerCurrentMatchCell) getTableRow().getItem());

                                setText(item);
                                setGraphic(mCell.getIndicator());
                            } else {
                                VBox vBox = new VBox(new Text(item));
                                vBox.setAlignment(Pos.CENTER_LEFT);

                                HBox hBox;

                                if (!((SummonerCurrentMatchCell) getTableRow().getItem()).getSeason().toString().contains("Unranked")) {
                                    ImageView badge = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("rank_badges/" + ((SummonerCurrentMatchCell) getTableRow().getItem()).getTier().get() + ".png"), 29, 29, true, true));
                                    hBox = new HBox(badge, vBox);
                                } else {
                                    hBox = new HBox(vBox);
                                }

                                hBox.setSpacing(5);
                                hBox.setAlignment(Pos.CENTER_LEFT);

                                setGraphic(hBox);
                            }
                        }
                    }
                }
            });

            //Wins
            bravoWinsColumn.setCellValueFactory(cellData -> cellData.getValue().getTotalWins());
            bravoWinsColumn.setCellFactory(column -> new TableCell<SummonerCurrentMatchCell, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setAlignment(Pos.CENTER);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        if (getTableRow().getItem() != null) {
                            if (!((SummonerCurrentMatchCell) getTableRow().getItem()).isLoaded()) {
                                SummonerCurrentMatchCell mCell = ((SummonerCurrentMatchCell) getTableRow().getItem());

                                setText(item);
                                setGraphic(mCell.getIndicator());
                            } else {
                                setText(item);
                                setGraphic(null);
                                setTooltip(new Tooltip("Summoner Level\nLevel " + ((SummonerCurrentMatchCell) getTableRow().getItem()).getParticipant().getSummoner().getLevel()));
                            }
                        }
                    }
                }
            });

            //Ranked Wins
            bravoRankedWinsColumn.setCellValueFactory(cellData -> cellData.getValue().getRankedWins());
            bravoRankedWinsColumn.setCellFactory(column -> new TableCell<SummonerCurrentMatchCell, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setAlignment(Pos.CENTER);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        if (getTableRow().getItem() != null) {
                            if (!((SummonerCurrentMatchCell) getTableRow().getItem()).isLoaded()) {
                                SummonerCurrentMatchCell mCell = ((SummonerCurrentMatchCell) getTableRow().getItem());

                                setText(item);
                                setGraphic(mCell.getIndicator());
                            } else {
                                setText(item);
                                setGraphic(null);
                            }
                        }
                    }
                }
            });

            //KDA
            bravoKDAColumn.setCellValueFactory(cellData -> cellData.getValue().getKDA());
            bravoKDAColumn.setCellFactory(column -> new TableCell<SummonerCurrentMatchCell, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setAlignment(Pos.CENTER);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        if (getTableRow().getItem() != null) {
                            if (!((SummonerCurrentMatchCell) getTableRow().getItem()).isLoaded()) {
                                SummonerCurrentMatchCell mCell = ((SummonerCurrentMatchCell) getTableRow().getItem());

                                setText(item);
                                setGraphic(mCell.getIndicator());
                            } else {
                                setText(item);
                                setGraphic(null);
                            }
                        }
                    }
                }
            });
        }
    }

    public <T> void refresh(final TableView<T> table, final ObservableList<T> tableList) {
        table.getItems().clear();
        table.layout();
        table.setItems(tableList);
    }

    public static void hackTooltip(Tooltip tooltip) {
        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(5)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SVGPath createArrowPath(int height, boolean up) {
        SVGPath svg = new SVGPath();
        int width = height / 4;

        if (up)
            svg.setContent("M" + width + " 0 L" + (width * 2) + " " + width + " L0 " + width + " Z");
        else
            svg.setContent("M0 0 L" + (width * 2) + " 0 L" + width + " " + width + " Z");

        return svg;
    }

//    public WritableImage getRoundedImage(ImageView imageView) {
//        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
//        clip.setArcWidth(20);
//        clip.setArcHeight(20);
//
//        imageView.setClip(clip);
//
//        SnapshotParameters param = new SnapshotParameters();
//        param.setFill(Color.TRANSPARENT);
//
//        WritableImage image = imageView.snapshot(param, null);
//
//        imageView.setClip(null);
//        imageView
//    }

    public static UserMenuController getInstance() {
        return instance;
    }

    public TableView<SummonerCurrentMatchCell> getAlphaTableView() {
        return alphaTableView;
    }

    public ObservableList<SummonerCurrentMatchCell> getAlphaCurrentMatchCells() {
        return alphaCurrentMatchCells;
    }

    public TableView<SummonerCurrentMatchCell> getBravoTableView() {
        return bravoTableView;
    }

    public ObservableList<SummonerCurrentMatchCell> getBravoCurrentMatchCells() {
        return bravoCurrentMatchCells;
    }

    public ListView<SummonerMatchHistoryCell> getMatchHistoryListView() {
        return matchHistoryListView;
    }

    public ObservableList<SummonerMatchHistoryCell> getMatchHistoryCells() {
        return matchHistoryCells;
    }
}