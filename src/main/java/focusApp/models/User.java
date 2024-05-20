package focusApp.models;

import javax.print.attribute.standard.RequestingUserName;

public class User {
    private int id;
    private String userName;
    private boolean parentalLock;

    public User(String userName, boolean parentalLock) {
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

    public void setParentalLock(boolean parentalLock) {
        this.parentalLock = parentalLock;
    }

    public boolean getParentalLock() {
        return parentalLock;
    }
}
