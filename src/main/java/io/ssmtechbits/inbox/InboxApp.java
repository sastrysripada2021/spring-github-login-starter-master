package io.ssmtechbits.inbox;

import java.nio.file.Path;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datastax.oss.driver.api.core.uuid.Uuids;

import io.ssmtechbits.inbox.email.EmailEntity;
import io.ssmtechbits.inbox.email.EmailRepository;
import io.ssmtechbits.inbox.emaillist.EmailListItemEntity;
import io.ssmtechbits.inbox.emaillist.EmailListItemKey;
import io.ssmtechbits.inbox.emaillist.EmailListItemRepository;
import io.ssmtechbits.inbox.folders.Folder;
import io.ssmtechbits.inbox.folders.FolderRepository;


@SpringBootApplication
@RestController
public class InboxApp {

	@Autowired
	@Lazy
	private FolderRepository folderRepository;

	 @Autowired
	 @Lazy
	 EmailListItemRepository emailListItemRepository;
	 
	 @Autowired
	 @Lazy
	 EmailRepository emailRepository;

	public static void main(String[] args) {
		SpringApplication.run(InboxApp.class, args);
	}

	@RequestMapping("/user")
	public String user(@AuthenticationPrincipal OAuth2User principal) {
		System.out.println(principal);
		return principal.getAttribute("name");
	}

    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

	@PostConstruct
	public void init (){

          folderRepository.save(  new Folder("sastrysripada2021", "Inbox", "blue"));
		  folderRepository.save(  new Folder("sastrysripada2021", "Sent", "Green"));
		  folderRepository.save(  new Folder("sastrysripada2021", "Important", "Yellow"));
        
		for (int i = 0; i < 10; i++) {

			EmailListItemKey key = new EmailListItemKey();
			key.setLabel("Inbox");
			key.setUserId("sastrysripada2021");
			key.setTimeUUID(Uuids.timeBased());

			EmailListItemEntity item = new EmailListItemEntity();
			item.setKey(key);
			item.setTo(Arrays.asList("Sastry Sripada", "abc", "def"));
			item.setSubject("Subject " + i);
			item.setUnRead(true);

		 	emailListItemRepository.save(item);

			EmailEntity email = new EmailEntity();
			email.setId(key.getTimeUUID());
			email.setFrom("sastrysripada2021");
			email.setSubject(item.getSubject());
			email.setBody("Body " + i);
			email.setTo(item.getTo());

			emailRepository.save(email);

		}
	}

}
