package com2027.killaz.kalorie.gitfit;

class LeaderboardsData {
    String leaderboardsName;
    String leaderboardsScore;
    String leaderboardsRank;

    public LeaderboardsData(String leaderboardsName, String leaderboardsScore, String leaderboardsRank) {
        this.leaderboardsName = leaderboardsName;
        this.leaderboardsScore = leaderboardsScore;
        this.leaderboardsRank = leaderboardsRank;
    }

    public String getLeaderboardsName() {
        return leaderboardsName;
    }

    public void setLeaderboardsName(String leaderboardsName) {
        this.leaderboardsName = leaderboardsName;
    }

    public String getLeaderboardsScore() {
        return leaderboardsScore;
    }

    public void setLeaderboardsScore(String leaderboardsScore) {
        this.leaderboardsScore = leaderboardsScore;
    }

    public String getLeaderboardsRank() {
        return leaderboardsRank;
    }

    public void setLeaderboardsRank(String leaderboardsRank) {
        this.leaderboardsRank = leaderboardsRank;
    }
}
