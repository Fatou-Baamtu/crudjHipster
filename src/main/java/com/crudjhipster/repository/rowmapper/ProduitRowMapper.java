package com.crudjhipster.repository.rowmapper;

import com.crudjhipster.domain.Produit;
import io.r2dbc.spi.Row;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Produit}, with proper type conversions.
 */
@Service
public class ProduitRowMapper implements BiFunction<Row, String, Produit> {

    private final ColumnConverter converter;

    public ProduitRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Produit} stored in the database.
     */
    @Override
    public Produit apply(Row row, String prefix) {
        Produit entity = new Produit();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNom(converter.fromRow(row, prefix + "_nom", String.class));
        entity.setPrix(converter.fromRow(row, prefix + "_prix", BigDecimal.class));
        entity.setQuantite(converter.fromRow(row, prefix + "_quantite", Integer.class));
        return entity;
    }
}
