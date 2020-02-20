package br.com.icompsoft.microservice.auth.service;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.icompsoft.microservice.auth.model.Authority;
import br.com.icompsoft.microservice.auth.model.Usuario;
import br.com.icompsoft.microservice.auth.repository.UsuarioRepository;

@Service
@Transactional
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	UsuarioRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
				.map(user -> new org.springframework.security.core.userdetails.User(user.getUsername(),
						user.getPassword(), getGrantedAuthorities(user)))
				.orElseThrow(() -> new UsernameNotFoundException("User " + username + " Not found"));
	}

	@Autowired
	public void setUserRepository(UsuarioRepository userRepository) {
		this.userRepository = userRepository;
	}

	private Collection<GrantedAuthority> getGrantedAuthorities(Usuario user) {
		Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		for (Authority authority : user.getAuthorities()) {
			GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getName());
			grantedAuthorities.add(grantedAuthority);
		}

		return grantedAuthorities;
	}
}