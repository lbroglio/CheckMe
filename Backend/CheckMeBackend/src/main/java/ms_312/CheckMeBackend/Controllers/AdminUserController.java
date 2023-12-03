package ms_312.CheckMeBackend.Controllers;


import ms_312.CheckMeBackend.Users.User;
import ms_312.CheckMeBackend.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashSet;

@RestController
public class AdminUserController {
    @Autowired
    private UserRepository userRepository;

    private boolean checkAdmin(String username) {
        User user = userRepository.findByName(username);
        return user.getUserType() == User.UserType.ADMIN;
    }

    @DeleteMapping("/delete/{userToDelete}")
    public ResponseEntity<String> deleteUser(@PathVariable String userToDelete, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if(userRepository.findByName(userToDelete) == null){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        String decodedAuth = new String(Base64.getDecoder().decode(authHeader));
        int authSplit = decodedAuth.lastIndexOf(':');
        String username = decodedAuth.substring(0, authSplit);

        if (checkAdmin(username)) {
            userRepository.deleteByName(userToDelete);
            return new ResponseEntity<>("User deleted", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("User is not an admin", HttpStatus.UNAUTHORIZED);
        }
    }


    @PutMapping("/promote/{userToPromote}")
    public ResponseEntity<String> promoteUser(@PathVariable String userToPromote, @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        User promoteUser = userRepository.findByName(userToPromote);
        if (promoteUser == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        String decodedAuth = new String(Base64.getDecoder().decode(authHeader));
        int authSplit = decodedAuth.lastIndexOf(':');
        String username = decodedAuth.substring(0, authSplit);

        if (checkAdmin(username)) {
            if(promoteUser.getUserType() == User.UserType.ADMIN){
                return new ResponseEntity<>("User is already an admin", HttpStatus.BAD_REQUEST);
            }
            promoteUser.setUserType(User.UserType.ADMIN);
            userRepository.save(promoteUser);
            return new ResponseEntity<>("User promoted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User is not an admin", HttpStatus.UNAUTHORIZED);
        }

    }

}
