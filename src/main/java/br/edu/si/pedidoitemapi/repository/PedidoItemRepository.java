package br.edu.si.pedidoitemapi.repository;

import br.edu.si.pedidoitemapi.entity.PedidoItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {
    boolean existsByItemId(Long itemId);
    List<PedidoItem> findByPedidoIdOrderByIdAsc(Long pedidoId);
    Optional<PedidoItem> findByIdAndPedidoId(Long id, Long pedidoId);
}

