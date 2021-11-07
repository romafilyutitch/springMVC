package com.epam.esm.builder;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.lang.UsesSunMisc;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Builder class is used to build find all sql query to find certificate.
 * Determines sql query by passed parameters such as tag name,
 * part of name, part of description. Also can add certificate
 * order by name or data parameters. All parameters can be used in conjunction.
 * For example if passed parameters contains parameters to find certificate
 * by part of name and order by name and date, then all sql query to find all
 * certificates that contains passed parameter value as part of name
 * with order by name and then by name sql query will be built
 */
@Component
public class FindCertificatesQueryBuilder {

    public static final String JOIN_ATTRIBUTE_NAME = "tags";
    public static final String SORT_BY_DATE_PARAMETER_KEY = "sortByDate";
    public static final String SORT_BY_NAME_PARAMETER_KEY = "sortByName";
    public static final String ASCENDING_ORDER_VALUE = "asc";
    public static final String DESCENDING_ORDER_VALUE = "desc";
    public static final String CREATE_DATE_ATTRIBUTE = "createDate";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String TAG_NAME_ATTRIBUTE_KEY = "tagName";
    public static final String TAG_NAME_ATTRIBUTE = "name";
    public static final String COMMA = ",";
    public static final String PART_OF_DESCRIPTION_PARAMETER_KEY = "partOfDescription";
    public static final String PART_OF_NAME_PARAMETER_KEY = "partOfName";
    public static final String DESCRIPTION_ATTRIBUTE = "description";
    public static final String LIKE_PATTERN = "%%%s%%";
    public static final String ID_ATTRIBUTE = "id";

    /**
     * Build sql find all query that by passed parameters. argument map contains
     * keys that define parameter then appropriate sql query will be built.
     * If parameters map contains keys for order then sql will be build with order statement
     *
     * @param findParameters parameters map that define find certificate parameters
     * @param criteriaBuilder builder to build HCQL query
     * @return built sql find all certificates statement that defined by passed parameters map
     */
    public Query<Certificate> buildSql(LinkedHashMap<String, String> findParameters, Session session) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Certificate> criteriaQuery = criteriaBuilder.createQuery(Certificate.class);
        Root<Certificate> root = criteriaQuery.from(Certificate.class);
        Join<Certificate, Tag> join = root.join(JOIN_ATTRIBUTE_NAME, JoinType.LEFT);
        List<Predicate> predicates = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        buildPartOfNameCriteria(findParameters, criteriaBuilder, root, predicates);
        buildPartOfDescriptionCriteria(findParameters, criteriaBuilder, root, predicates);
        buildTagNamesCriteria(findParameters, criteriaBuilder, join, predicates);
        Set<Map.Entry<String, String>> entries = findParameters.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            buildSortByNameOrder(criteriaBuilder, root, orders, key, value);
            buildSortByDateOrder(criteriaBuilder, root, orders, key, value);
        }
        criteriaQuery.select(root).where(predicates.toArray(new Predicate[]{})).groupBy(root.get(ID_ATTRIBUTE)).orderBy(orders);
        String offsetValue = findParameters.remove("offset");
        int offset = Integer.parseInt(offsetValue);
        String limitValue = findParameters.remove("limit");
        int limit = Integer.parseInt(limitValue);
        Query<Certificate> query = session.createQuery(criteriaQuery);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query;
    }

    private void buildSortByDateOrder(CriteriaBuilder criteriaBuilder, Root<Certificate> root, List<Order> orders, String key, String value) {
        if (key.equals(SORT_BY_DATE_PARAMETER_KEY)) {
            if (value.equals(ASCENDING_ORDER_VALUE)) {
                Order sortByDateAsc = criteriaBuilder.asc(root.get(CREATE_DATE_ATTRIBUTE));
                orders.add(sortByDateAsc);
            } else if (value.equals(DESCENDING_ORDER_VALUE)) {
                Order sortByDateDesc = criteriaBuilder.desc(root.get(CREATE_DATE_ATTRIBUTE));
                orders.add(sortByDateDesc);
            }
        }
    }

    private void buildSortByNameOrder(CriteriaBuilder criteriaBuilder, Root<Certificate> root, List<Order> orders, String key, String value) {
        if (key.equals(SORT_BY_NAME_PARAMETER_KEY)) {
            if (value.equals(ASCENDING_ORDER_VALUE)) {
                Order sortByNameAsc = criteriaBuilder.asc(root.get(NAME_ATTRIBUTE));
                orders.add(sortByNameAsc);
            } else if (value.equals(DESCENDING_ORDER_VALUE)) {
                Order sortByNameDesc = criteriaBuilder.desc(root.get(NAME_ATTRIBUTE));
                orders.add(sortByNameDesc);
            }
        }
    }

    private void buildTagNamesCriteria(LinkedHashMap<String, String> findParameters, CriteriaBuilder criteriaBuilder, Join<Certificate, Tag> join, List<Predicate> predicates) {
        String tagNames = findParameters.get(TAG_NAME_ATTRIBUTE_KEY);
        if (isNullOrEmptyParameter(tagNames)) {
            return;
        }
        String[] names = tagNames.split(COMMA);
        CriteriaBuilder.In<Object> inBuilder = criteriaBuilder.in(join.get(TAG_NAME_ATTRIBUTE));
        for (String name : names) {
            inBuilder.value(name);
        }
        predicates.add(inBuilder);
    }

    private void buildPartOfDescriptionCriteria(LinkedHashMap<String, String> findParameters, CriteriaBuilder criteriaBuilder, Root<Certificate> root, List<Predicate> predicates) {
        String partOfDescription = findParameters.get(PART_OF_DESCRIPTION_PARAMETER_KEY);
        if (isNullOrEmptyParameter(partOfDescription)) {
            return;
        }
        Predicate partOfDescriptionPredicate = criteriaBuilder.like(root.get(DESCRIPTION_ATTRIBUTE), String.format(LIKE_PATTERN, partOfDescription));
        predicates.add(partOfDescriptionPredicate);
    }

    private void buildPartOfNameCriteria(LinkedHashMap<String, String> findParameters, CriteriaBuilder criteriaBuilder, Root<Certificate> root, List<Predicate> predicates) {
        String partOfName = findParameters.get(PART_OF_NAME_PARAMETER_KEY);
        if (isNullOrEmptyParameter(partOfName)) {
            return;
        }
        Predicate partOfNamePredicate = criteriaBuilder.like(root.get(FindCertificatesQueryBuilder.NAME_ATTRIBUTE), String.format(LIKE_PATTERN, partOfName));
        predicates.add(partOfNamePredicate);
    }

    private boolean isNullOrEmptyParameter(String parameter) {
        return parameter == null || parameter.isEmpty();
    }
}
