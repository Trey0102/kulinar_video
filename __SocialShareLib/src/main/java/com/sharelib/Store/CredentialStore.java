package com.sharelib.Store;

public interface CredentialStore {
    String[] read();
    void write(String[]response);
    void clearCredentials();
}
