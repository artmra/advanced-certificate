package br.ufsc.labsec.emissoravancado.persistence.mysql.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {}
