package dimage.searchpic.service.auth;

public interface TokenInfo {
    String getSecretKey();
    long getValidTime();

    boolean isSameTokenType(String type);
    boolean isAccessToken();
    boolean isRefreshToken();
}
