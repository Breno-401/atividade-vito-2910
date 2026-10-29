package br.edu.si.pedidoitemapi.repository;

import br.edu.si.pedidoitemapi.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}

