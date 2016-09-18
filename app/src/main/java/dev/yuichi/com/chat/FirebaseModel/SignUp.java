package dev.yuichi.com.chat.FirebaseModel;

/**
 * Created by yuichi on 9/14/16.
 */
public class SignUp {
    private String name;
    private String email;
    //private String userid;

    public SignUp(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return this.name;
    }
    public String getEmail() {return this.email; }
}
