package com.example.cs.registry.application;

import java.util.UUID;

public record RegisterCarCommand(UUID ownerId, String type, String registrationNumber) {}
