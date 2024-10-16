package ms_312.CheckMeBackend.Users;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Class for a User of the application. Users own Retrievers which serve them Messages.
 */
@Entity
//@Table(name="USERS")
public class User extends RetrieverOwner{

    public enum UserType{
        @JsonEnumDefaultValue
        DEFAULT,
        ADMIN
    }


    @Schema(type = "string", example = "User101")
    private String name = super.getName();
    /**
     * The email address associated with the user's account
     */
    @Schema(type = "string", example = "example@gmail.com")
    private String email;

    /**
     * A cryptographic hash of this account's password.
     */
    @JsonIgnore
    private byte[] passwordHash;

    /**
     * Randomly generated salt applied to this user's password.
     */
    @JsonIgnore
    private byte[] salt;

    /**
     * JSON which represents any previously configured settings set by this user related to their account.
     */
    @Schema(type = "string", example = "{'ExampleSetting': 'on'}")
    private String profileSettings;

    private UserType userType;

    @ManyToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Group> groups;

    /**
     * Construct a new empty user with the given username and password hash.
     *
     * @param username A string storing the username to identify this account by.
     * @param email The email address to be associated with this User's account
     * @param passwordHash A cryptographic hash of this account's password.
     * @param salt         The salt used to hash this user's password
     */
    public User(String username, String email, byte[] passwordHash, byte[] salt) {
        super(username);
        this.groups = new ArrayList<>();
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.userType = UserType.DEFAULT;
    }


    public User(String username, String email, byte[] passwordHash, byte[] salt, String userType) {
        super(username);
        this.groups = new ArrayList<>();
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.userType = UserType.valueOf(userType);
    }



    /**
     * Default constructor used by the persistence API
     */
    private User(){
        super();
    }

    /**
     * @return A cryptographic hash of this account's password
     */
    public byte[] getPasswordHash() {
        return passwordHash;
    }

    /**
     * @return Any configured preferences/settings this user has set stored as a JSON  String.
     */
    public String getProfileSettings() {
        return profileSettings;
    }

    /**
     * Update the saved profile settings of a user to a new JSON String
     *
     * @param profileSettings The JSON String to save as the new profile settings.
     */
    public void setProfileSettings(String profileSettings) {
        this.profileSettings = profileSettings;
    }

    /**
     * @return The salt used for this user's password hash
     */
    public byte[] getSalt() {
        return salt;
    }

    /**
     * @return The email address associated with this account
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return An ArrayList containing all the {@link Group} objects for the groups this User is a member of.
     */
    public List<Group> getGroups(){
        return groups;
    }

    /**
     * Function to update the List of {@link Group}s used by the persistence API
     *
     * @param groups The {@link ArrayList} of {@link Group] objects represneitng groups this User is a member of
     */
    private void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    /**
     * Add a new Group that this User is a member of to the stored list of this User's groups
     *
     * @param group The {@link Group} object for the Group this User is joining
     */
    public void joinGroup(Group group){
        groups.add(group);
    }

    /**
     * Remove a Group that this User is a member of from the stored list of this User's groups
     *
     * @param group The {@link Group} object for the Group this User is leaving
     */
    public void removeGroup(Group group){
        groups.remove(group);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        if (!super.equals(o)) return false;

        return super.equals(o);
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }


}


