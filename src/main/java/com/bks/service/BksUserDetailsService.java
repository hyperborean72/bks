package com.bks.service;

import com.bks.domain.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class BksUserDetailsService implements UserDetailsService {

	private final ClientService clientService;

	/*
	согласно требованиям аутентификация может выполняться по email+password либо по phone+password.
	поэтому роль username будет играть либо mail либо phone клиента.  остановимся на mail
	*/
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

    	Client client = clientService.getClientByMail(s) ; //  != null согласно  getClientByMail
        return new User(s, client.getPassword(), new ArrayList<>());
    }
}