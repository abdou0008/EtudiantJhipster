package com.itsandra.etudiantetranger.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.itsandra.etudiantetranger.domain.Pays;
import com.itsandra.etudiantetranger.repository.rowmapper.PaysRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Pays entity.
 */
@SuppressWarnings("unused")
class PaysRepositoryInternalImpl extends SimpleR2dbcRepository<Pays, Long> implements PaysRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PaysRowMapper paysMapper;

    private static final Table entityTable = Table.aliased("pays", EntityManager.ENTITY_ALIAS);

    public PaysRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PaysRowMapper paysMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Pays.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.paysMapper = paysMapper;
    }

    @Override
    public Flux<Pays> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Pays> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Pays> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = PaysSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

        String select = entityManager.createSelect(selectFrom, Pays.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Pays> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Pays> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    private Pays process(Row row, RowMetadata metadata) {
        Pays entity = paysMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Pays> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
