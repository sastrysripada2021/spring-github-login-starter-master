package io.ssmtechbits.inbox.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.ssmtechbits.inbox.email.EmailEntity;
import io.ssmtechbits.inbox.email.EmailRepository;
import io.ssmtechbits.inbox.folders.Folder;
import io.ssmtechbits.inbox.folders.FolderRepository;
import io.ssmtechbits.inbox.folders.FolderService;

@Controller
public class EmailViewController {

    @Autowired
    @Lazy
    private FolderRepository folderRepository;

    @Autowired
    @Lazy
    FolderService folderService;

    @Autowired
    @Lazy
    EmailRepository emailRepository;

    @GetMapping(value = "/emails/{id}")
    public String emailView(

            @PathVariable UUID id,

            @AuthenticationPrincipal OAuth2User principal, Model model) {

        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))) {
            return "index";
        }

        // Fetch Folders
        String userId = principal.getAttribute("login");
        System.out.println("userID is --- :: " + userId);

        List<Folder> userFolders = folderRepository.findAllById(userId);
        model.addAttribute("userFolders", userFolders);

        List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
        model.addAttribute("defaultFolders", defaultFolders);

        model.addAttribute("userName", principal.getAttribute("name"));


        Optional<EmailEntity> optionalEmail = emailRepository.findById(id);
        if (!optionalEmail.isPresent()) {
            return "inbox-page";
        }

       EmailEntity email =   optionalEmail.get();
       
       String toIds = String.join(",", email.getTo());

        model.addAttribute("email", email);
        
        model.addAttribute("toIds", toIds);
        return "email-page";

    }
}
