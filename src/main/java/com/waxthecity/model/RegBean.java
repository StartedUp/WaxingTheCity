package com.waxthecity.model;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Balaji on 8/12/17.
 */
public class RegBean {
    //@NotEmpty
    private String name;
    //@NotEmpty
    private String acneMedicines;
    //@NotEmpty
    private String acnePeriod;
    //@NotEmpty
    private String bleachingAgent;
    //@NotEmpty
    private String bleachingAgentPeriod;
    //@NotEmpty
    private String illness;
    //@NotEmpty
    private String alergy;
    //@NotEmpty
    private boolean diabetic;
    //@NotEmpty
    private boolean pregnant;
    //@NotEmpty
    private String limitation;

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

    public String getIllness() {
        return illness;
    }

    public void setIllness(String illness) {
        this.illness = illness;
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

    public String getLimitation() {
        return limitation;
    }

    public void setLimitation(String limitation) {
        this.limitation=limitation;
    }

    @Override
    public String toString() {
        return "RegBean{" +
                "name='" + name + '\'' +
                ", acneMedicines='" + acneMedicines + '\'' +
                ", acnePeriod='" + acnePeriod + '\'' +
                ", bleachingAgent='" + bleachingAgent + '\'' +
                ", bleachingAgentPeriod='" + bleachingAgentPeriod + '\'' +
                ", illness='" + illness + '\'' +
                ", alergy='" + alergy + '\'' +
                ", diabetic=" + diabetic +
                ", pregnant=" + pregnant +
                ", limitation='" + limitation + '\'' +
                '}';
    }
}