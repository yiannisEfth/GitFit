package com2027.killaz.kalorie.gitfit;

public class LeaderboardsUser {

    private String name;
    private int points;
    private int caloriesBurned;
    private int challengesCompleted;
    private int distanceTraveled;

    /**
     * User object used to initialise and display users in the leaderboards fragment list.
     * @param name of user
     * @param points of user
     * @param caloriesBurned of user
     * @param challengesCompleted of user
     * @param distanceTraveled of user
     */
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
