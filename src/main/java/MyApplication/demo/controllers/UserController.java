package MyApplication.demo.controllers;


import MyApplication.demo.models.Film;
import MyApplication.demo.models.FilmSale;
import MyApplication.demo.models.User;

import MyApplication.demo.repos.FilmSaleRepository;
import MyApplication.demo.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@SessionAttributes(value = "user")
@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilmSaleRepository filmSaleRepository;

    @GetMapping("/registration")
    public String registration(Model model){

        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@RequestParam String login, @RequestParam String password, @RequestParam String secretCode, Model model){

        Iterator<User> iteratorUser = userRepository.findAll().iterator();

        while (iteratorUser.hasNext())
        {
            User user = iteratorUser.next();
            if(user.getLogin().equals(login)){
                model.addAttribute("message", "Данный логин занят, попробуйте другой");
                return "registration";
            }
        }

        int status = 0;
        if(secretCode.equals("555")){ status = 1;}

        User user = new User(login, password, status);
        userRepository.save(user);
        model.addAttribute("message", "Регистрация прошла успешно. Пожалуйста, войдите под новым аккаунтом.");

        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String login, @RequestParam String password, Model model){

        Iterator<User> iteratorUser = userRepository.findAll().iterator();
        boolean isLogin = false;
        while (iteratorUser.hasNext())
        {
            User user = iteratorUser.next();
            if(user.isActive()){
                user.setActive(false);
                userRepository.save(user);
            }
            if(user.getLogin().equals(login) && user.getPassword().equals(password)){
                user.setActive(true);
                userRepository.save(user);
                model.addAttribute("user",user);
                isLogin = true;
            }
        }
        if(isLogin) return "home";
        else{
            model.addAttribute("message", "Такого пользователя не существует, попробуйте снова.");
            return "login";
        }

    }
    public void checkActiveUser(Model model) {
        Iterator<User> iteratorUser = userRepository.findAll().iterator();

        while (iteratorUser.hasNext()) {
            User user = iteratorUser.next();
            if (user.isActive()) {
                model.addAttribute("user", user);
                break;
            }
        }
    }
    public User checkActiveUser() {
        Iterator<User> iteratorUser = userRepository.findAll().iterator();

        while (iteratorUser.hasNext()) {
            User user = iteratorUser.next();
            if (user.isActive()) return user;

        }

        return null;
    }
    public void saveUser(User user){
        userRepository.save(user);
    }
    @GetMapping("/user")
    public String user(Model model){

        if(checkActiveUser() == null) return "login";

        if(checkActiveUser().getStatus() == 0){
            model.addAttribute("userStatus", "пользователь");
        }
        else{
            model.addAttribute("userStatus", "администратор");
        }
        model.addAttribute("user", checkActiveUser());
        model.addAttribute("countFilms", userCountFilms());

        return "user";
    }
    @PostMapping("/user")
    public String userDonate(Model model){

        User user = checkActiveUser();
        userRepository.findById(user.getId());
        user.setBalance(user.getBalance() + 10);
        userRepository.save(user);

        if(checkActiveUser().getStatus() == 0){
            model.addAttribute("userStatus", "пользователь");
        }
        else{
            model.addAttribute("userStatus", "администратор");
        }
        model.addAttribute("user", user);
        model.addAttribute("countFilms", userCountFilms());
        return "user";
    }
    @PostMapping("/relogin")
    public String relogin(Model model) {
        User user = checkActiveUser();
        user.setActive(false);
        userRepository.save(user);
        return "login";
    }

    public Integer userCountFilms(){
        User user = checkActiveUser();
        userRepository.findById(user.getId());


        Iterator<FilmSale> filmSaleIterator = filmSaleRepository.findAll().iterator();
        Integer countFilms = 0;
        while (filmSaleIterator.hasNext()) {
            FilmSale filmSale = filmSaleIterator.next();
            if (filmSale.getId_user() == user.getId()) countFilms++ ;
        }
        return countFilms;

    }

}
