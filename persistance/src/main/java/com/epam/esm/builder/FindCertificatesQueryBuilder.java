package com.epam.esm.builder;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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

    public static final String SORT_BY_DATE_PARAMETER_KEY = "sortByDate";
    public static final String SORT_BY_NAME_PARAMETER_KEY = "sortByName";
    public static final String ASCENDING_ORDER_VALUE = "asc";
    public static final String DESCENDING_ORDER_VALUE = "desc";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String TAG_NAME_ATTRIBUTE_KEY = "tagName";
    public static final String COMMA = ",";
    public static final String PART_OF_DESCRIPTION_PARAMETER_KEY = "partOfDescription";
    public static final String PART_OF_NAME_PARAMETER_KEY = "partOfName";
    public static final String DESCRIPTION_ATTRIBUTE = "description";
    public static final String LIKE_PATTERN = "%%%s%%";

    /**
     * Build sql find all query that by passed parameters. argument map contains
     * keys that define parameter then appropriate sql query will be built.
     * If parameters map contains keys for order then sql will be build with order statement
     *
     * @param findParameters parameters map that define find certificate parameters
     * @return built sql find all certificates statement that defined by passed parameters map
     */
    public List<Specification<Certificate>> buildSpecifications(LinkedHashMap<String, String> findParameters) {
        List<Specification<Certificate>> findSpecifications = new ArrayList<>();
        nameLike(findParameters, findSpecifications);
        descriptionLike(findParameters, findSpecifications);
        hasTags(findParameters, findSpecifications);
        return findSpecifications;
    }

    public List<Sort.Order> buildOrders(LinkedHashMap<String, String> findParameters) {
        List<Sort.Order> sortOrders = new ArrayList<>();
        for (Map.Entry<String, String> entry : findParameters.entrySet()) {
            sortByName(entry, sortOrders);
            sortByDate(entry, sortOrders);
        }
        return sortOrders;
    }

    private void sortByDate(Map.Entry<String, String> entry, List<Sort.Order> sortOrders) {
        String key = entry.getKey();
        String value = entry.getValue();
        if (key.equals(SORT_BY_DATE_PARAMETER_KEY)) {
            if (value.equals(ASCENDING_ORDER_VALUE)) {
                Sort.Order sortByDateAsc = Sort.Order.asc("createDate");
                sortOrders.add(sortByDateAsc);
            } else if (value.equals(DESCENDING_ORDER_VALUE)) {
                Sort.Order sortByDateDesc = Sort.Order.desc("createDate");
                sortOrders.add(sortByDateDesc);
            }
        }
    }

    private void sortByName(Map.Entry<String, String> entry, List<Sort.Order> sortOrders) {
        String key = entry.getKey();
        String value = entry.getValue();
        if (key.equals(SORT_BY_NAME_PARAMETER_KEY)) {
            if (value.equals(ASCENDING_ORDER_VALUE)) {
                Sort.Order sortByNameAsc = Sort.Order.asc("name");
                sortOrders.add(sortByNameAsc);
            } else if (value.equals(DESCENDING_ORDER_VALUE)) {
                Sort.Order sortByNameDesc = Sort.Order.desc("name");
                sortOrders.add(sortByNameDesc);
            }
        }
    }

    private void hasTags(LinkedHashMap<String, String> findParameters, List<Specification<Certificate>> specifications) {
        String tagNames = findParameters.get(TAG_NAME_ATTRIBUTE_KEY);
        if (isNullOrEmptyParameter(tagNames)) {
            return;
        }
        String[] names = tagNames.split(COMMA);
        for (String name : names) {
            Specification<Certificate> tagNameEqualsSpecification = (root, query, criteriaBuilder) -> {
                Join<Certificate, Tag> join = root.join("tags", JoinType.LEFT);
                return criteriaBuilder.equal(join.get("name"), name);
            };
            specifications.add(tagNameEqualsSpecification);
        }
    }

    private void descriptionLike(LinkedHashMap<String, String> findParameters, List<Specification<Certificate>> specifications) {
        String partOfDescription = findParameters.get(PART_OF_DESCRIPTION_PARAMETER_KEY);
        if (isNullOrEmptyParameter(partOfDescription)) {
            return;
        }
        Specification<Certificate> partOfDescriptionSpecification = (root, query, builder) -> builder.like(root.get(DESCRIPTION_ATTRIBUTE), String.format(LIKE_PATTERN, partOfDescription));
        specifications.add(partOfDescriptionSpecification);
    }

    private void nameLike(LinkedHashMap<String, String> findParameters, List<Specification<Certificate>> specifications) {
        String partOfName = findParameters.get(PART_OF_NAME_PARAMETER_KEY);
        if (isNullOrEmptyParameter(partOfName)) {
            return;
        }
        Specification<Certificate> partOfNameSpecification = (root, query, builder) -> builder.like(root.get(NAME_ATTRIBUTE), String.format(LIKE_PATTERN, partOfName));
        specifications.add(partOfNameSpecification);
    }

    private boolean isNullOrEmptyParameter(String parameter) {
        return parameter == null || parameter.isEmpty();
    }
}
