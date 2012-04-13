/**
 * 
 */
package net.gregrapp.jhouse.repositories;

import java.util.List;

import net.gregrapp.jhouse.models.TransportBean;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Greg Rapp
 *
 */
@Transactional
public interface TransportBeanRepository extends JpaRepository<TransportBean, Long>
{
  List<TransportBean> findByEnabled(boolean enabled);
}
