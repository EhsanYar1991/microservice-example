package com.yar.microservices.uaa.security;

/**
 * enum for Spring Security authorities.
 */

public enum SecurityAuthority {

    ADMIN("ROLE_ADMIN"),
    MANAGER("ROLE_MANAGER"),
    USER("ROLE_USER"),
    ANONYMOUS("ROLE_ANONYMOUS");

    private String name;

    SecurityAuthority(String name) {
        this.name = name;
    }


    public String getAuthority() {
        return name;
    }

    @Override
    public String toString() {
        return "CardType{" +
                ", name='" + name + '\'' +
                '}';
    }


}
