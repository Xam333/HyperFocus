package focusApp.models;

import javax.print.attribute.standard.RequestingUserName;

public class User {
    private int id;
    private String userName;
    private boolean parentalLock;
    private int totalFocusTime;

    public User(String userName, int totaFocusTime) {
        this.userName = userName;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public boolean getParentalLcok() {
        return parentalLock;
    }

    public void setParentalLock(boolean parentalLock) {
        this.parentalLock = parentalLock;
    }

    public int getTotalFocusTime() {
        return totalFocusTime;
    }

    public void setTotalFocusTime(int totalFocusTime) {
        this.totalFocusTime = totalFocusTime;
    }
}
