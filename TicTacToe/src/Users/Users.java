package Users;

public class Users {

    private int userId; 
    private String username;
    private String password;
    private int score; 
    private boolean status;

    public Users(String username, String password) {
        this.username = username;
        this.password = password;
        this.score = 0; // Default score
        this.status = false; // Default status
    }

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
