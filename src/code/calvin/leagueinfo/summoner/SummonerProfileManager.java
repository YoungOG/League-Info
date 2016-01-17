package code.calvin.leagueinfo.summoner;

import com.robrua.orianna.type.core.summoner.Summoner;

import java.util.ArrayList;

public class SummonerProfileManager {

    private static SummonerProfileManager instance;
    private ArrayList<SummonerProfile> summonerProfiles = new ArrayList<>();

    public SummonerProfileManager() {
        instance = this;
    }

    public void addSummoner(Summoner summoner, boolean isSelected) {
        if (!hasSummonerProfile(summoner)) {
            SummonerProfile profile = new SummonerProfile(summoner, isSelected);

            if (!summonerProfiles.contains(profile)) {
                summonerProfiles.add(profile);
            }
        }
    }

    public SummonerProfile getSummonerProfile(Summoner summoner) {
        for (SummonerProfile profiles : summonerProfiles) {
            if (profiles.getSummoner().getID() == summoner.getID()) {
                return profiles;
            }
        }

        return null;
    }

    public SummonerProfile getSummonerProfile(String summoner) {
        for (SummonerProfile profiles : summonerProfiles) {
            if (profiles.getSummoner().getName().equalsIgnoreCase(summoner)) {
                return profiles;
            }
        }

        return null;
    }

    public boolean hasSummonerProfile(Summoner summoner) {
        for (SummonerProfile profiles : summonerProfiles) {
            if (profiles.getSummoner().getID() == summoner.getID()) {
                return true;
            }
        }

        return false;
    }

    public boolean hasSummonerProfile(String summoner) {
        for (SummonerProfile profiles : summonerProfiles) {
            if (profiles.getSummoner().getName().equalsIgnoreCase(summoner)) {
                return true;
            }
        }

        return false;
    }

    public static SummonerProfileManager getInstance() {
        return instance;
    }
}
