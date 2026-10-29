package br.edu.si.pedidoitemapi.repository;

import br.edu.si.pedidoitemapi.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}

