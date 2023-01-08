package com.chatup.chatup_server.service;

import com.chatup.chatup_server.domain.AppUser;
import com.chatup.chatup_server.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AppUserService implements UserDetailsService {
    private final AppUserRepository appUserRepository;
    private final int PAGESIZE;


    @Autowired
    public AppUserService(AppUserRepository appUserRepository, @Value("${app.feed.pageSize}") int pageSize) {
        this.appUserRepository = appUserRepository;
        this.PAGESIZE = pageSize;
    }

    @Override
    public AppUser loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findAppUserByUsername(username);
        if(user == null)
            throw new UsernameNotFoundException("User with username " + username + " doesn't exist.");
        
        return user;
    }

    public List<AppUser> fuzzySearchByUsername(String username, int page){
        return appUserRepository.fuzzyUserSearch(username, PageRequest.of(page, PAGESIZE));
    }
}