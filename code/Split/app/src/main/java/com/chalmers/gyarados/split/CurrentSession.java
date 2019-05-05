package com.chalmers.gyarados.split;

import com.chalmers.gyarados.split.model.User;

public class CurrentSession {
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        CurrentSession.currentUser = currentUser;
    }
}
