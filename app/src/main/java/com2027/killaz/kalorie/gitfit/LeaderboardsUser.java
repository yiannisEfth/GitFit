package com2027.killaz.kalorie.gitfit;

public class LeaderboardsUser {

    private String name;
    private int points;
    private int caloriesBurned;
    private int challengesCompleted;
    private int distanceTraveled;

    public LeaderboardsUser(String name, int points, int caloriesBurned, int challengesCompleted, int distanceTraveled) {
        this.name = name;
        this.points = points;
        this.caloriesBurned = caloriesBurned;
        this.challengesCompleted = challengesCompleted;
        this.distanceTraveled = distanceTraveled;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public int getChallengesCompleted() {
        return challengesCompleted;
    }

    public int getDistanceTraveled() {
        return distanceTraveled;
    }
}
