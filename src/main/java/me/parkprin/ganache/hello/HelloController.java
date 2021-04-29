package me.parkprin.ganache.hello;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class HelloController {

    @GetMapping
    public String sayHello(@RequestParam(value = "name", required = false, defaultValue = "파라미터 입력") String name, Model model){
        model.addAttribute("name", name);
        return "marketboro";
    }
}
