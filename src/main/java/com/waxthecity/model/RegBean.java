package com.waxthecity.model;

/**
 * Created by Balaji on 8/12/17.
 */
public class RegBean {
    String name;
    String acneMedicines;
    String acnePeriod;
    String bleachingAgent;
    String bleachingAgentPeriod;
    String alergy;
    boolean diabetic;
    boolean pregnant;
    String minor;
    String limitation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcneMedicines() {
        return acneMedicines;
    }

    public void setAcneMedicines(String acneMedicines) {
        this.acneMedicines = acneMedicines;
    }

    public String getAcnePeriod() {
        return acnePeriod;
    }

    public void setAcnePeriod(String acnePeriod) {
        this.acnePeriod = acnePeriod;
    }

    public String getBleachingAgent() {
        return bleachingAgent;
    }

    public void setBleachingAgent(String bleachingAgent) {
        this.bleachingAgent = bleachingAgent;
    }

    public String getBleachingAgentPeriod() {
        return bleachingAgentPeriod;
    }

    public void setBleachingAgentPeriod(String bleachingAgentPeriod) {
        this.bleachingAgentPeriod = bleachingAgentPeriod;
    }

    public String getAlergy() {
        return alergy;
    }

    public void setAlergy(String alergy) {
        this.alergy = alergy;
    }

    public boolean isDiabetic() {
        return diabetic;
    }

    public void setDiabetic(boolean diabetic) {
        this.diabetic = diabetic;
    }

    public boolean isPregnant() {
        return pregnant;
    }

    public void setPregnant(boolean pregnant) {
        this.pregnant = pregnant;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getLimitation() {
        return limitation;
    }

    public void setLimitation(String limitation) {
        this.limitation = limitation;
    }



    @Override
    public String toString() {
        return "RegBean{" +
                "name='" + name + '\'' +
                ", acneMedicines='" + acneMedicines + '\'' +
                ", acnePeriod='" + acnePeriod + '\'' +
                ", bleachingAgent='" + bleachingAgent + '\'' +
                ", bleachingAgentPeriod='" + bleachingAgentPeriod + '\'' +
                ", alergy='" + alergy + '\'' +
                ", diabetic=" + diabetic +
                ", pregnant=" + pregnant +
                ", minor='" + minor + '\'' +
                ", limitation='" + limitation + '\'' +
                '}';
    }
}
