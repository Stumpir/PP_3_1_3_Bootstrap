package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin")
    public String getUsers(Model model) {
        List<User> list = userService.listUsers();
        User auth =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("list", list);
        model.addAttribute("admin", auth);
        model.addAttribute("user", new User());
        return "users/show";
    }

    @GetMapping("/admin/{id}")
    public String adminGet(@PathVariable("id") Long id, Model model) {
        model.addAttribute("person", userService.getUser(id));
        return "users/get";
    }

    @GetMapping("/user")
    public String get(Model model) {
        User auth =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("person", userService.getUser(auth.getId()));
        return "users/get_user";
    }

    @GetMapping("/admin/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        return "users/new";
    }

    @PostMapping("/admin")
    public String create(@ModelAttribute("user") User user, @RequestParam("roles") String[] roles) {
        List<Role> list = new ArrayList<>();
        List<Role> rolesDB = userService.getRoles();
        for (String input : roles) {
            for (Role role : rolesDB) {
                if (role.getAuthority().equals(input)) {
                    list.add(role);
                }
            }
        }
        user.setRoles(list);
        userService.add(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", userService.getUser(id));
        return "users/edit";
    }

    @PatchMapping("/admin/{id}")
    public String update(@ModelAttribute("user") User user, @PathVariable("id") Long id, @RequestParam("password1") String passwordIn, @RequestParam("roles") String[] roles) {
        if (user.getPassword().equals("")) {
            user.setPassword(passwordIn);
        }
        List<Role> list = new ArrayList<>();
        List<Role> rolesDB = userService.getRoles();
        for (String input : roles) {
            for (Role role : rolesDB) {
                if (role.getAuthority().equals(input)) {
                    list.add(role);
                }
            }
        }
        user.setRoles(list);
        userService.update(id, user);
        return "redirect:/admin";
    }

    @DeleteMapping("/admin/{id}")
    public String delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}