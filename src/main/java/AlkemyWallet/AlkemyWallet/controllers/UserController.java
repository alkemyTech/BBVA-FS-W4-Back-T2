package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/home")
public class UserController {

    @Autowired
    private  UserService userService;


    @GetMapping("/users")
    public List<User> getUsers(){
        return userService.getAllUsers();
    }

    @DeleteMapping("/users/{id}")
    public void softDeleteUserById(@PathVariable Long id) {
        userService.softDeleteById(id);
    }

    }

