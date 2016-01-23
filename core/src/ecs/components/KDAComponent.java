package ecs.components;

import ecs.Component;

/**
 * Created by Orion on 1/20/2016.
 */
public class KDAComponent extends Component {

    int kills, deaths, captures;

    public KDAComponent(){

    }

    public int getKills() {
        return kills;
    }

    public int incrementKills(){
        return ++kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int incrementDeaths(){
        return ++deaths;
    }

    public float getKD(){
        return (float)kills/(float)deaths;
    }

    public int getCaptures() {
        return captures;
    }

    public void setCaptures(int captures) {
        this.captures = captures;
    }

    public int incrementCaptures(){
        return ++captures;
    }
}
