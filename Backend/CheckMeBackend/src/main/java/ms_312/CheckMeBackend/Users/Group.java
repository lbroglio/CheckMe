package ms_312.CheckMeBackend.Users;


import jakarta.persistence.*;
import ms_312.CheckMeBackend.Messages.DemoRetriever;
import ms_312.CheckMeBackend.Messages.MessageRetriever;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
public class Group extends RetrieverOwner{
    /**
     * Holds all the codes currently in use by groups to prevent two groups being assigned the same code.
     */
    private static final ArrayList<String> codesInUse = new ArrayList<>();


    /**
     * A list every User who is a members of this Group.
     * User's with  admin powers are still included in this list
     */
    @ManyToMany
    @JoinColumn(name = "user_id")
    private ArrayList<User> members;

    /**
     * A list of this Group's members  who have Admin privileges over the Group --  This list is in
     * addition to the member's list. All admins will also be listed as members as well
     */
    //@ManyToMany
    //@JoinColumn(name = "user_id")
    //private ArrayList<User> admins;

    /**
     * An 8 character code used to join this group
     */
    private String joinCode;

    public Group(String name, User creator){
        super(name);

        //Instantiate member ArrayLists
        members = new ArrayList<>();
        //admins  = new ArrayList<>();

        //Set the add the creator to members and admins
        members.add(creator);
        //admins.add(creator);

        //Generate a Join code
        StringBuilder codeBuild = null;
        //If this is true the program can exit the code creation loop
        boolean codeApproved = false;
        //Random
        Random rand = new Random();

        //Repeat until the code is approved (an unqiue code is generated)
        //The collision check is included just in case -- The space is large enough that a collision should never occur
        while (!codeApproved){
            codeBuild = new StringBuilder();
            //Repeat for 8 characters
            for (int i=0; i < 8; i++){
                // Generate a random integer with an ASCII value corresponding to A-Z
                int charCode = rand.nextInt(65,91);

                // Add the randomly generated char (Converted from the int) to the code string
                codeBuild.append((char) charCode);
            }
            // If the code is unqiue(The code isn't included in the list of in use codes) exit the loop
            if(!codesInUse.contains(String.valueOf(codeBuild))){
                codeApproved = true;
            }
        }

        // Set the JoinCode
        joinCode = String.valueOf(codeBuild);

        //Add the JoinCode to the list of code's in use
        codesInUse.add(joinCode);

    }

    /**
     * Default constructor for JPA
     */
    private Group(){}

    /**
     * This ensures that the static {@link #codesInUse} is rebuilt when Groups are loaded by the JPA
     */
    @PostLoad
    public void fillCodeList(){
        codesInUse.add(this.joinCode);
    }

    /**
     * @return The String representing the code for joining this group
     */
    public String getJoinCode(){
        return joinCode;
    }


    /**
     * @return An ArrayList of the {@link User} objects for the members of this group
     */
    public ArrayList<User> getMembers() {
        return members;
    }

    /**
     * Add a member to this group
     * @param newMember {@link User} object for the user to add to the group
     */
    public void addMember(User newMember) {
        this.members.add(newMember);
    }

    /**
     * @return An ArrayList of the {@link User} objects for the members of this group with admin powers
     */
    /*public ArrayList<User> getAdmins() {
        return admins;
    }
    */

    /**
     * Promote of member of this group to have Admin powers
     * @param newAdmin {@link User} object for the user to add to the group
     *
     * @throws IllegalArgumentException If the User to make an admin isn't a member of the group
     */
   /*
    public void addAdmin(User newAdmin) {
        //Check if the User to give admin powers is in the group
        if(members.contains(newAdmin)){
            // If the new Admin isn't a member throw an exception
            throw new IllegalArgumentException("New admins must be member's of the group");
        }

        this.admins.add(newAdmin);
    }
    */

}
