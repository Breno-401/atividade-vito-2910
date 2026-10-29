package br.edu.si.pedidoitemapi.repository;

import br.edu.si.pedidoitemapi.entity.Pedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    boolean existsByClienteId(Long clienteId);
    List<Pedido> findByClienteId(Long clienteId);
}

