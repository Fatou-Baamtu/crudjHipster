package com.crudjhipster.repository;

import com.crudjhipster.domain.Produit;
import com.crudjhipster.domain.criteria.ProduitCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Produit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProduitRepository extends ReactiveCrudRepository<Produit, Long>, ProduitRepositoryInternal {
    Flux<Produit> findAllBy(Pageable pageable);

    @Override
    <S extends Produit> Mono<S> save(S entity);

    @Override
    Flux<Produit> findAll();

    @Override
    Mono<Produit> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProduitRepositoryInternal {
    <S extends Produit> Mono<S> save(S entity);

    Flux<Produit> findAllBy(Pageable pageable);

    Flux<Produit> findAll();

    Mono<Produit> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Produit> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Produit> findByCriteria(ProduitCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(ProduitCriteria criteria);
}
