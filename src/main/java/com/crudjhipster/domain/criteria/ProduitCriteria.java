package com.crudjhipster.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.crudjhipster.domain.Produit} entity. This class is used
 * in {@link com.crudjhipster.web.rest.ProduitResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /produits?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProduitCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nom;

    private StringFilter prix;

    private IntegerFilter quantite;

    private Boolean distinct;

    public ProduitCriteria() {}

    public ProduitCriteria(ProduitCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.nom = other.optionalNom().map(StringFilter::copy).orElse(null);
        this.prix = other.optionalPrix().map(StringFilter::copy).orElse(null);
        this.quantite = other.optionalQuantite().map(IntegerFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProduitCriteria copy() {
        return new ProduitCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getNom() {
        return nom;
    }

    public Optional<StringFilter> optionalNom() {
        return Optional.ofNullable(nom);
    }

    public StringFilter nom() {
        if (nom == null) {
            setNom(new StringFilter());
        }
        return nom;
    }

    public void setNom(StringFilter nom) {
        this.nom = nom;
    }

    public StringFilter getPrix() {
        return prix;
    }

    public Optional<StringFilter> optionalPrix() {
        return Optional.ofNullable(prix);
    }

    public StringFilter prix() {
        if (prix == null) {
            setPrix(new StringFilter());
        }
        return prix;
    }

    public void setPrix(StringFilter prix) {
        this.prix = prix;
    }

    public IntegerFilter getQuantite() {
        return quantite;
    }

    public Optional<IntegerFilter> optionalQuantite() {
        return Optional.ofNullable(quantite);
    }

    public IntegerFilter quantite() {
        if (quantite == null) {
            setQuantite(new IntegerFilter());
        }
        return quantite;
    }

    public void setQuantite(IntegerFilter quantite) {
        this.quantite = quantite;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProduitCriteria that = (ProduitCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nom, that.nom) &&
            Objects.equals(prix, that.prix) &&
            Objects.equals(quantite, that.quantite) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, prix, quantite, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProduitCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNom().map(f -> "nom=" + f + ", ").orElse("") +
            optionalPrix().map(f -> "prix=" + f + ", ").orElse("") +
            optionalQuantite().map(f -> "quantite=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
