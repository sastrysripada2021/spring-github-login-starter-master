package io.ssmtechbits.inbox.controllers;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.datastax.oss.driver.api.core.uuid.Uuids;

import io.ssmtechbits.inbox.emaillist.EmailListItemEntity;
import io.ssmtechbits.inbox.emaillist.EmailListItemRepository;
import io.ssmtechbits.inbox.folders.Folder;
import io.ssmtechbits.inbox.folders.FolderRepository;
// import io.ssmtechbits.inbox.folders.FolderService;

@Controller
public class InboxController {

@Autowired private FolderRepository folderRepository;
@Autowired private EmailListItemRepository emailListItemRepository;

// @Autowired
// private FolderService folderService;

    @GetMapping(value = "/")
    public String homePage (
    
    @RequestParam(required = false) String folder,
    @AuthenticationPrincipal OAuth2User principal,Model model){
        
        if (principal == null || !StringUtils.hasText(principal.getAttribute("login"))){
            return "index";
        }

        //Fetch Folders
        String userId = principal.getAttribute("login");
        
        List<Folder> userFolders = folderRepository.findAllById(userId);
        model.addAttribute("userFolders", userFolders);

        //List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
        List<Folder> defaultFolders = folderRepository.findAllById(userId);
        model.addAttribute("defaultFolders", defaultFolders);

        //Fetch messages

        if(!StringUtils.hasText(folder)){
            folder = "Inbox";
        }
        
        List<EmailListItemEntity> emailList = emailListItemRepository.findAllByKey_UserIdAndKey_Label(userId, folder);
        
        PrettyTime p = new PrettyTime();
        emailList.stream().forEach(emailItem -> {UUID timeUuid = emailItem.getKey().getTimeUUID();
        Date emailDateTime = new Date(Uuids.unixTimestamp(timeUuid));
        
        System.out.println("emailDateTime -- ::" + emailDateTime);
        emailItem.setAgoTimeString(p.format(emailDateTime));

        } );

       model.addAttribute("emailList", emailList);
       model.addAttribute("folderName", folder);
       model.addAttribute("userName", principal.getAttribute("name"));

        return "inbox-page";
    }
    
}
