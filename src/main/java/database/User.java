package database;


public class User {
    private int id;
    private String userName;
    private boolean parentalLock;

    public User(String userName, boolean parentalLock) {
        this.userName = userName;
        this.parentalLock = parentalLock;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getParentalLock() {
        return this.parentalLock;
    }

    public void setParentalLock(boolean parentalLock) {
        this.parentalLock = parentalLock;
    }

    public boolean login(String password) {
        return false;
    }
}
