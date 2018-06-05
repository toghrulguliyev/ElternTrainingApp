package at.ac.univie.entertain.elterntrainingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.UUID;

public class FamilyRule implements Parcelable {

    private String ruleName;
    private String rule;
    private String reason;
    private String forWho;
    private String id;
    private String familyId = "";
    private String autor;

    private ArrayList<String> usernames;

    public FamilyRule(String autor, String ruleName, String rule, String reason, String forWho) {
        this.autor = autor;
        this.ruleName = ruleName;
        this.reason = reason;
        this.forWho = forWho;
        this.id = UUID.randomUUID().toString();
        this.rule = rule;
    }

    public FamilyRule(String autor, String ruleName, String rule, String reason, String forWho, String id) {
        this.autor = autor;
        this.ruleName = ruleName;
        this.reason = reason;
        this.forWho = forWho;
        this.id = id;
        this.rule = rule;
    }

    public String getRuleName() {
        return this.ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getForWho() {
        return this.forWho;
    }

    public void setForWho(String forWho) {
        this.forWho = forWho;
    }

    public ArrayList<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(ArrayList<String> usernames) {
        this.usernames = usernames;
    }

    public void addUsername(String username) {
        this.usernames.add(username);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void generateId() {
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ruleName);
        dest.writeString(this.rule);
        dest.writeString(this.reason);
        dest.writeString(this.forWho);
        dest.writeString(this.id);
        dest.writeString(this.familyId);
        dest.writeString(this.autor);
        dest.writeStringList(this.usernames);
    }

    protected FamilyRule(Parcel in) {
        this.ruleName = in.readString();
        this.rule = in.readString();
        this.reason = in.readString();
        this.forWho = in.readString();
        this.id = in.readString();
        this.familyId = in.readString();
        this.autor = in.readString();
        this.usernames = in.createStringArrayList();
    }

    public static final Parcelable.Creator<FamilyRule> CREATOR = new Parcelable.Creator<FamilyRule>() {
        @Override
        public FamilyRule createFromParcel(Parcel source) {
            return new FamilyRule(source);
        }

        @Override
        public FamilyRule[] newArray(int size) {
            return new FamilyRule[size];
        }
    };
}