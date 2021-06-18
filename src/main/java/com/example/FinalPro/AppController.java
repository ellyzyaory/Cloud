package com.example.FinalPro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class AppController {

    @Autowired
    private DocumentRepository documentrepo;

    @Autowired
    private UserRepository userrepo;

    @GetMapping("/")
    public String homePage(Model model) {
        List<Document> list = documentrepo.findAll();
        model.addAttribute("list", list);
        return "index";
    }

    // Upload file by using document and MultipartFile to store files in the web
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("document") MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        Document document = new Document();
        document.setName(fileName);
        document.setContent(multipartFile.getBytes());
        document.setSize(multipartFile.getSize());
        document.setUploadTime(new Date());

        documentrepo.save(document);

        return "redirect:/";
    }

    /* Download file by checking whether the document is present. If it's present
    it will get the result and download the file
    */
    @GetMapping("/download")
    public void downloadFile(@Param("id") Long id, HttpServletResponse response) throws Exception {
        Optional<Document> result = documentrepo.findById(id);
        if(!result.isPresent()) {
            throw new Exception("Document can't be find");
        }
        Document document = result.get();

        response.setContentType("application/");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + document.getName();

        response.setHeader(headerKey, headerValue);

        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(document.getContent());
        outputStream.close();
    }

    // GET mapping "/register" returns the registration_form
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());

        return "registration_form";
    }

    // POST mapping "/" returns index, save user, and encode the password
    @PostMapping("/")
    public String processRegistration(User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userrepo.save(user);

        return "index";
    }
}
