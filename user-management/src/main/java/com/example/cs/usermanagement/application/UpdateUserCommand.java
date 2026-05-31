package com.example.cs.usermanagement.application;

import java.util.UUID;

public record UpdateUserCommand(UUID id, String name, String surname, String bankAccount) {}
