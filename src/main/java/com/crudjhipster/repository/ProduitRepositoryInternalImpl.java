package com.crudjhipster.repository;

import com.crudjhipster.domain.Produit;
import com.crudjhipster.domain.criteria.ProduitCriteria;
import com.crudjhipster.repository.rowmapper.ColumnConverter;
import com.crudjhipster.repository.rowmapper.ProduitRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the Produit entity.
 */
@SuppressWarnings("unused")
class ProduitRepositoryInternalImpl extends SimpleR2dbcRepository<Produit, Long> implements ProduitRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProduitRowMapper produitMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("produit", EntityManager.ENTITY_ALIAS);

    public ProduitRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProduitRowMapper produitMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Produit.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.produitMapper = produitMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Produit> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Produit> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProduitSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Produit.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Produit> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Produit> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Produit process(Row row, RowMetadata metadata) {
        Produit entity = produitMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Produit> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Produit> findByCriteria(ProduitCriteria produitCriteria, Pageable page) {
        return createQuery(page, buildConditions(produitCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(ProduitCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(ProduitCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getNom() != null) {
                builder.buildFilterConditionForField(criteria.getNom(), entityTable.column("nom"));
            }
            if (criteria.getPrix() != null) {
                builder.buildFilterConditionForField(criteria.getPrix(), entityTable.column("prix"));
            }
            if (criteria.getQuantite() != null) {
                builder.buildFilterConditionForField(criteria.getQuantite(), entityTable.column("quantite"));
            }
        }
        return builder.buildConditions();
    }
}
