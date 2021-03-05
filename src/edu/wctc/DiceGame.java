package edu.wctc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiceGame {
    private final List<Player> players = new ArrayList<>();
    private final List<Die> dice = new ArrayList<>();
    private final int maxRolls = 5;
    private Player currentPlayer;

    // If the number of players is less than 2, throws an IllegalArgumentException
    public DiceGame(int countPlayers, int countDice, int maxRolls) {
        if (countPlayers < 2) {
            throw new IllegalArgumentException();
        } else {
            for (int i = 1; i <= countPlayers; i++) {
                Player player = new Player();
                players.add(player);
            }
            for (int i = 1; i <= countDice; i++) {
                Die die = new Die(6);
                dice.add(die);
            }
        }
    }

    // Returns true if all dice are held, false otherwise.
    private boolean allDiceHeld() {
        return dice.stream().allMatch(Die::isBeingHeld);
    }


    public boolean autoHold(int faceValue) {
        // If there already is a die with the given face value that is held, just return true.
        if (isHoldingDie(faceValue)) {
            return true;
        } else {
            Stream<Die> filteredList = dice.stream().filter(die -> die.getFaceValue() == faceValue);
            Optional<Die> outcome = filteredList.findFirst();

            // If there is a die with the given face value that is unheld, hold it and return true.
            if (outcome.isPresent()) {
                if (!outcome.get().isBeingHeld()) {
                    outcome.get().holdDie();
                    return true;
                }
            }
            // If there is no die with the given face value, return false.
            return false;
        }
    }

    // Returns true if the current player has any rolls remaining and if not all dice are held.
    public boolean currentPlayerCanRoll() {
        if (currentPlayer.getRollsUsed() < 5 && !allDiceHeld()) {
            return true;
        } else {
            return false;
        }
    }

    public int getCurrentPlayerNumber() {
        return currentPlayer.getPlayerNumber();
    }

    public int getCurrentPlayerScore() {
        return currentPlayer.getScore();
    }

    public String getDiceResults() {
        return dice.stream().map(Die::toString).collect(Collectors.joining());
    }

    public String getFinalWinner() {
        players.sort(Comparator.comparingInt(Player::getWins));
        return players.get(players.size() - 1).toString();
    }

    public String getGameResults() {
        Stream<Player> outcome = players.stream().
                sorted(Comparator.comparingInt(Player::getScore).reversed());
        List<Player> playerList = outcome.collect(Collectors.toList());

        int count = 0;
        int winningScore = playerList.get(0).getScore();

        if (winningScore != 0) {
            for (Player player : playerList) {
                if (player.getScore() == winningScore) {
                    player.addWin();
                    count = count + 1;
                } else {
                    player.addLoss();
                }
            }
        }
        // Returns a string composed by concatenating each Player's toString.
        return playerList.stream().map(Player::toString).collect(Collectors.joining());
    }

    // Returns true if there is any held die with a matching face value, false otherwise.
    private boolean isHoldingDie(int faceValue) {
        Stream<Die> filteredList = dice.stream().filter(die -> die.getFaceValue() == faceValue).filter(Die::isBeingHeld);
        Optional<Die> outcome = filteredList.findFirst();

        if (outcome.isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    // 	If there are more players in the list after the current player, updates currentPlayer to be the next player and returns true. Otherwise, returns false.
    public boolean nextPlayer() {
        if (currentPlayer.getPlayerNumber() < players.size()) {
            currentPlayer = players.get(currentPlayer.getPlayerNumber());
            return true;
        } else return false;
    }

    public void playerHold(char dieNum) {
        Stream<Die> holdDice = dice.stream().filter(die -> die.getDieNum() == dieNum);
        Optional<Die> outcome = holdDice.findFirst();
        outcome.get().holdDie();
    }

    public void resetDice() {
        dice.forEach(Die::resetDie);
    }

    public void resetPlayers() {
        players.forEach(Player::resetPlayer);
    }

    public void rollDice() {
        currentPlayer.roll();
        dice.forEach(Die::rollDie);
    }

    // If there is currently a ship (6), captain (5), and crew (4) die held, adds the points for the remaining two dice (the cargo) to the current player's score.
    //If there is not a 6, 5, and 4 held, assigns no points.
    public void scoreCurrentPlayer() {
        List<Integer> heldDie = new ArrayList<>();
        List<Integer> scoringDie = new ArrayList<>();

        for (Die d : dice) {
            if (d.getFaceValue() == 6 && !heldDie.contains(6) || d.getFaceValue() == 5 && !heldDie.contains(5) || d.getFaceValue() == 4 && !heldDie.contains(4)) {
                heldDie.add(d.getFaceValue());
            } else {
                scoringDie.add(d.getFaceValue());
            }
        }
        int score = 0;

        if (heldDie.contains(6) & heldDie.contains(5) && heldDie.contains(4)) {
            for (Integer i : scoringDie) {
                score = score + i;
            }
        }
        currentPlayer.setScore(getCurrentPlayerScore() + score);
    }

    public void startNewGame() {
        currentPlayer = players.get(0);
        resetPlayers();
    }

}
