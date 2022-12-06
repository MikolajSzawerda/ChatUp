package com.chatup.chatup_server.domain;

public record UserInfo(
        Long id,
        String username,
        String firstName,
        String lastName
) {
    public static UserInfo from(AppUser appUser) {
        return new UserInfo(
                appUser.getId(),
                appUser.getUsername(),
                appUser.getFirstName(),
                appUser.getLastName()
        );
    }
}
