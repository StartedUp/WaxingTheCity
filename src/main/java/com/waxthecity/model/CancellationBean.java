package com.waxthecity.model;

/**
 * Created by Balaji on 19/3/18.
 */
public class CancellationBean {
    private String firstName;
    private String lastName;
    private String imaageData;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImaageData() {
        return imaageData;
    }

    public void setImaageData(String imaageData) {
        this.imaageData = imaageData;
    }

    @Override
    public String toString() {
        return "CancellationBean{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", imaageData='" + imaageData + '\'' +
                '}';
    }
}
