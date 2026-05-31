package com.example.cs.usermanagement.application;

public record CreateUserCommand(String username, String name, String surname, String bankAccount) {}
