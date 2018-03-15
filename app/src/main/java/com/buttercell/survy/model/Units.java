package com.buttercell.survy.model;

/**
 * Created by amush on 13-Feb-18.
 */

public class Units {
    public String unitName,unitDescription;
    public int unitSem,unitYear;

    public Units(String unitName, String unitDescription, int unitSem, int unitYear) {
        this.unitName = unitName;
        this.unitDescription = unitDescription;
        this.unitSem = unitSem;
        this.unitYear = unitYear;
    }

    public Units() {
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitDescription() {
        return unitDescription;
    }

    public void setUnitDescription(String unitDescription) {
        this.unitDescription = unitDescription;
    }

    public int getUnitSem() {
        return unitSem;
    }

    public void setUnitSem(int unitSem) {
        this.unitSem = unitSem;
    }

    public int getUnitYear() {
        return unitYear;
    }

    public void setUnitYear(int unitYear) {
        this.unitYear = unitYear;
    }
}
