package org.lavender.common.constant;

public final class ApiPath {
    private ApiPath(){}

    public static final class AuthApi{
        private static final String BASE = "/auth";
        private AuthApi(){}
        public static final String LOGIN = BASE+"/login";
        public static final String LOGOUT = BASE+"/logout";
        public static final String REGISTER = BASE+"/register";
    }
    // 微服务
    public static final class UserApi{
        private static final String BASE = "/user";
        private UserApi(){}
        public static final String INFO = BASE + "/info";
    }
}
