package com.example.bullhorn;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listActors(Model model) {
        model.addAttribute("messages", messageRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String newActor(Model model) {
        model.addAttribute("message", new Message());
        return "form";
    }

    @PostMapping("/process")
    public String processActor(@Valid Message message, BindingResult result,
                               @RequestParam("file") MultipartFile file) {
        if (result.hasErrors()) {
            return  "form";
        }
        if (file.isEmpty()) {
            return "redirect:/form";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            message.setHeadshot(uploadResult.get("url").toString());
            messageRepository.save(message);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/form";
        }
        return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String showCourse(@PathVariable("id") long id, Model model) {
        model.addAttribute("message", messageRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateCourse(@PathVariable("id") long id, Model model) {
        model.addAttribute("message", messageRepository.findById(id).get());
        return "form";
    }

    @RequestMapping("/delete/{id}")
    public String delCourse(@PathVariable("id") long id) {
        messageRepository.deleteById(id);
        return "redirect:/";
    }
}
