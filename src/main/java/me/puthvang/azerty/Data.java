package me.puthvang.azerty;

public class Data {

    private final Bot bot;
    private boolean usingEnvironmentalVariables;

    Data(Bot bot, boolean usingEnvironmentalVariables){
        this.bot = bot;
        this.usingEnvironmentalVariables = usingEnvironmentalVariables;
    }

    public Bot getBot() {
        return bot;
    }

    public void setVariables(boolean usingEnvironmentalVariables) {
        this.usingEnvironmentalVariables = usingEnvironmentalVariables;
    }

    public boolean isUsingEnvironmentalVariables() {
        return usingEnvironmentalVariables;
    }

    @Override
    public String toString() {
        return "Data{" +
                "bot=" + bot +
                ", usingEnvironmentalVariables=" + usingEnvironmentalVariables +
                '}';
    }
}
