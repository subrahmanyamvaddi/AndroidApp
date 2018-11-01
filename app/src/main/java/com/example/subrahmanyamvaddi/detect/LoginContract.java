package com.example.subrahmanyamvaddi.detect;

public final class LoginContract {

    private LoginContract(){ }

    public static class LoginEntry
    {
        public static final String TABLE_NAME = "Login_info";
        public static final String CONTACT_ID = "contact_id";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
    }

}
