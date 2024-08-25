package de.praxidike.traindisplaymanager.models;

public class Station implements Comparable<Station>{
    String name;
    int evaNr;

    public Station(String name, int evaNr) {
        this.name = name;
        this.evaNr = evaNr;
    }

    public String getName() {
        return name;
    }

    public int getEvaNr() {
        return evaNr;
    }

    @Override
    public int compareTo(Station o) {
        return this.name.compareToIgnoreCase(o.getName());
    }

    @Override
    public String toString(){
        return this.name;
    }

}
