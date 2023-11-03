package ms_312.CheckMeBackend.Controllers;


import ms_312.CheckMeBackend.Users.User;
import ms_312.CheckMeBackend.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

@RestController
public class AdminUserController {
    @Autowired
    private UserRepository userRepository;

    private boolean checkAdmin(String username) {
        User user = userRepository.findByName(username);
        return user.getUserType() == User.UserType.ADMIN;
    }

    public void deleteUser(String username, String userToDelete) {
        if (checkAdmin(username)) {
            userRepository.deleteByName(userToDelete);
        }
    }

}
