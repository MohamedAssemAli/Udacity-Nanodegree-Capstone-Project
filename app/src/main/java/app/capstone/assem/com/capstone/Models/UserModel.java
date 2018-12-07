package app.capstone.assem.com.capstone.Models;

public class UserModel {

    private String uid, email, username, password, img;

    public UserModel() {
    }

    public UserModel(String email, String username, String password, String img) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.img = img;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
