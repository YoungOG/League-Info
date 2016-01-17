package code.calvin.leagueinfo.cells;

import com.robrua.orianna.type.core.summoner.Summoner;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SummonerSearchCell extends ListCell<SummonerSearchCell> {

    private Summoner summoner;

    public SummonerSearchCell(Summoner summoner) {
        this.summoner = summoner;
    }

    @Override
    public void updateItem(SummonerSearchCell cell, boolean empty) {
        super.updateItem(cell, empty);

        if (cell != null) {
            if (summoner == null) {

            } else {
                VBox vBox = new VBox(new Text(cell.getSummoner().getName()), new Text("Level " + cell.getSummoner().getLevel()));

                HBox hBox = new HBox(new ImageView(new Image(getClass().getResourceAsStream("../summoner_icons/ProfileIcon08.png"), 100, 100, true, true)), vBox);
                hBox.setSpacing(5);

                setGraphic(hBox);
            }
        }
    }

    public Summoner getSummoner() {
        return summoner;
    }
}
