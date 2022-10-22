package models;

import exceptions.TDEException;

public class ChessMatch {
    private Long id;
    private String winner;
    private Integer winnerRating;
    private Integer loserRating;
    private String winnerUsername;

    public ChessMatch(Long id, String winner, Integer winnerRating, Integer loserRating, String username) {
        this.id = id;
        this.winner = winner;
        this.winnerRating = winnerRating;
        this.loserRating = loserRating;
        this.winnerUsername = username;
    }

    public ChessMatch(String line) throws TDEException {
        String[] split = line.split(",");

        if (split.length != 5) throw new TDEException("Formato inválido");

        this.id = Long.parseLong(split[0]);
        this.winner = split[1];
        this.winnerRating = Integer.parseInt(split[2]);
        this.loserRating = Integer.parseInt(split[3]);
        this.winnerUsername = split[4].trim();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public Integer getWinnerRating() {
        return winnerRating;
    }

    public void setWinnerRating(Integer winnerRating) {
        this.winnerRating = winnerRating;
    }

    public Integer getLoserRating() {
        return loserRating;
    }

    public void setLoserRating(Integer loserRating) {
        this.loserRating = loserRating;
    }

    public String getWinnerUsername() {
        return winnerUsername;
    }

    public void setWinnerUsername(String winnerUsername) {
        this.winnerUsername = winnerUsername;
    }

    @Override
    public String toString() {
        return "ChessMatch{" +
                "id=" + id +
                ", winner='" + winner + '\'' +
                ", winnerRating=" + winnerRating +
                ", loserRating=" + loserRating +
                ", winnerUsername='" + winnerUsername + '\'' +
                '}';
    }
}
