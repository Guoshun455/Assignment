package com.example.week11.data;

import com.example.week11.model.Match;
import com.example.week11.model.Player;
import com.example.week11.model.Team;
import java.util.List;
import java.util.function.Supplier;
public class DataProvider<T> {
    private Supplier<List<T>> dataSupplier;
    public DataProvider(Supplier<List<T>> dataSupplier) {
        this.dataSupplier = dataSupplier;
    }
    public List<T> getData() {
        return dataSupplier.get();
    }
    public static DataProvider<Team> createTeamProvider(){
        return new DataProvider<>(DataProvider::createSampleTeams);
    }
    public static DataProvider<Player> createPlayerProvider(){
        return new DataProvider<>(DataProvider::createSamplePlayers);
    }
    public static DataProvider<Match> createMatchProvider(){
        return new DataProvider<>(DataProvider::createSampleMatches);
    }
    public static List<Team> createSampleTeams() {
        return List.of(
                new Team("FC Barcelona", "Spain", "La Liga", "Camp Nou", 1899),
                new Team("Manchester United", "England", "Premier League", "Old Trafford", 1878),
                new Team("Bayern Munich", "Germany", "Bundesliga", "Allianz Arena", 1900),
                new Team("Juventus", "Italy", "Serie A", "Allianz Stadium", 1897),
                new Team("Paris Saint-Germain", "France", "Ligue 1", "Parc des Princes", 1970),
                new Team("Ajax Amsterdam", "Netherlands", "Eredivisie", "Johan Cruyff Arena", 1900),
                new Team("River Plate", "Argentina", "Primera División", "El Monumental", 1901),
                new Team("Flamengo", "Brazil", "Brasileirão", "Maracanã", 1895)
        );
    }
    public static List<Player> createSamplePlayers() {
        return List.of(
                new Player("Lionel Messi", 34, "Argentina", "Forward", "FC Barcelona", 10),
                new Player("Cristiano Ronaldo", 36, "Portugal", "Forward", "Juventus", 7),
                new Player("Robert Lewandowski", 32, "Poland", "Forward", "Bayern Munich", 9),
                new Player("Kevin De Bruyne", 29, "Belgium", "Midfielder", "Manchester City", 17),
                new Player("Virgil van Dijk", 30, "Netherlands", "Defender", "Liverpool", 4),
                new Player("Manuel Neuer", 35, "Germany", "Goalkeeper", "Bayern Munich", 1),
                new Player("Kylian Mbappé", 22, "France", "Forward", "Paris Saint-Germain", 7),
                new Player("Erling Haaland", 20, "Norway", "Forward", "Borussia Dortmund", 9),
                new Player("Bruno Fernandes", 26, "Portugal", "Midfielder", "Manchester United", 18),
                new Player("Joshua Kimmich", 26, "Germany", "Midfielder", "Bayern Munich", 6),
                new Player("Jan Oblak", 28, "Slovenia", "Goalkeeper", "Atletico Madrid", 13),
                new Player("Neymar Jr.", 29, "Brazil", "Forward", "Paris Saint-Germain", 10)
        );
    }
    public static List<Match> createSampleMatches() {
        return List.of(
                new Match("FC Barcelona", "Real Madrid", "2-1", "La Liga", "2023-04-10", "Camp Nou"),
                new Match("Manchester United", "Liverpool", "0-3", "Premier League", "2023-03-15", "Old Trafford"),
                new Match("Bayern Munich", "Borussia Dortmund", "4-2", "Bundesliga", "2023-04-01", "Allianz Arena"),
                new Match("Juventus", "AC Milan", "1-1", "Serie A", "2023-03-20", "Allianz Stadium"),
                new Match("Paris Saint-Germain", "Lyon", "3-0", "Ligue 1", "2023-04-05", "Parc des Princes"),
                new Match("FC Barcelona", "Bayern Munich", "0-3", "Champions League", "2023-02-28", "Camp Nou"),
                new Match("Manchester City", "Paris Saint-Germain", "2-1", "Champions League", "2023-03-08", "Etihad Stadium"),
                new Match("Liverpool", "Ajax Amsterdam", "1-0", "Champions League", "2023-03-01", "Anfield")
        );
    }
}
