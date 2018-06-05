package at.ac.univie.entertain.elterntrainingapp.Security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHandler {

    public static String securePassword(String password){
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        return hashed;
    }

    public static boolean checkPassword(String password, String hashedPassword){
        boolean valid = false;
        if (BCrypt.checkpw(password, hashedPassword)){
            valid = true;
        }
        return valid;
    }

}
