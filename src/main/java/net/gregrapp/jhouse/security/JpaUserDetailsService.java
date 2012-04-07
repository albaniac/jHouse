/**
 * 
 */
package net.gregrapp.jhouse.security;

import java.util.ArrayList;
import java.util.List;

import net.gregrapp.jhouse.models.User;
import net.gregrapp.jhouse.repositories.UserRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Greg Rapp
 * 
 */
@Transactional(readOnly = true)
public class JpaUserDetailsService implements UserDetailsService
{

  /**
   * Wraps {@link String} roles to {@link SimpleGrantedAuthority} objects
   * 
   * @param roles
   *          {@link String} of roles
   * @return list of granted authorities
   */
  public static List<GrantedAuthority> getGrantedAuthorities(List<String> roles)
  {
    List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    for (String role : roles)
    {
      authorities.add(new SimpleGrantedAuthority(role));
    }
    return authorities;
  }

  private UserRepository userRepository;

  /*
   * (non-Javadoc)
   * 
   * @see org.springframework.security.core.userdetails.UserDetailsService#
   * loadUserByUsername(java.lang.String)
   */
  @Override
  public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException
  {

    if (userRepository == null)
    {
      throw new UsernameNotFoundException("");
    } else
    {
      User user = userRepository.findByUsername(username);
      boolean accountNonExpired = true;
      boolean credentialsNonExpired = true;

      org.springframework.security.core.userdetails.User userDetailsUser = new org.springframework.security.core.userdetails.User(
          user.getUsername(), user.getPassword(), user.isEnabled(),
          accountNonExpired, credentialsNonExpired, !user.isLocked(),
          getGrantedAuthorities(user.getRoleNames()));

      return userDetailsUser;
    }
  }
  
  public void setUserRepository(UserRepository userRepository)
  {
    this.userRepository = userRepository;
  }
}
