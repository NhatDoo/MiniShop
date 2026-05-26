package com.demo.tmdt.service;

public record GoogleIdentity(
        String subject,
        String email,
        String name
) {}
