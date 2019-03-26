package com2027.killaz.kalorie.gitfit;

class LeaderboardsData {
    String leaderboardsName;
    String leaderboardsPoints;
    String leaderboardsRank;

    public LeaderboardsData(String leaderboardsName, String leaderboardsPoints, String leaderboardsRank) {
        this.leaderboardsName = leaderboardsName;
        this.leaderboardsPoints = leaderboardsPoints;
        this.leaderboardsRank = leaderboardsRank;
    }

    public String getLeaderboardsName() {
        return leaderboardsName;
    }

    public void setLeaderboardsName(String leaderboardsName) {
        this.leaderboardsName = leaderboardsName;
    }

    public String getLeaderboardsPoints() {
        return leaderboardsPoints;
    }

    public void setLeaderboardsPoints(String leaderboardsPoints) {
        this.leaderboardsPoints = leaderboardsPoints;
    }
}
