package at.ac.univie.entertain.elterntrainingapp.model;


import java.util.ArrayList;

public class User {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private int accType; // 1:Kind; 2:Vater; 3:Mutter;
    private boolean enabled;
    private String email;
    private int birthday;
    private String gender;
    private String token;
    private String familyId;
    private ArrayList<String> relatives = new ArrayList<>();

    //FCM token
    private String fcmToken;


    public User(String firstName, String lastName, String username, String email, String password, int birthday, String gender, int accType){
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.accType = accType;
        this.enabled = true;
        this.email = email;
        //this.paarKey = "";
        this.birthday = birthday;
        this.gender = gender;
    }

    public String getFcmToken() {
        return this.fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

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

    public int getAccType() {
        return accType;
    }

    public void setAccType(int accType) {
        this.accType = accType;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getBirthday() {
        return birthday;
    }

    public void setBirthday(int birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ArrayList<String> getConnections() {
        return relatives;
    }

    public void setConnections(ArrayList<String> connections) {
        this.relatives = connections;
    }

    public void addConnectedUser(String user) {
        this.relatives.add(user);
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }
}
