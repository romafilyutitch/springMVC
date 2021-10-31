package com.epam.esm.builder;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;


/**
 * Builder class is used to build fina all sql query to find certificate.
 * Determines sql query by passed parameters such as tag name,
 * part of name, part of description. Also can add certificate
 * order by name or data parameters. All parameters can be used in conjunction.
 * For example if passed parameters contains parameters to find certificate
 * by part of name and order by name and date, then all sql query to find all
 * certificates that contains passed parameter value as part of name
 * with order by name and then by name sql query will be built
 */
@Component
public class FindCertificatesSqlBuilder {
    private static final String BASE_SQL = "select gift_certificate.*, tag.* from gift_certificate " +
            "left join certificate_tag on certificate_tag.certificate_id = gift_certificate.id " +
            "left join tag on certificate_tag.tag_id = tag.id ";
    private static final String TAG_ID_IN = "certificate_tag.tag_id in ";
    private static final String BY_PART_OF_NAME = "gift_certificate.name like ?";
    private static final String BY_PART_OF_DESCRIPTION = "gift_certificate.description like ?";
    private static final String AND = " and ";
    private static final String ORDER_BY = " order by ";
    private static final String NAME_ASC = "gift_certificate.name asc";
    private static final String NAME_DESC = "gift_certificate.name desc ";
    private static final String DATE_ASC = "gift_certificate.create_date asc";
    private static final String DATE_DESC = "gift_certificate.create_date desc";
    private static final String COMMA = ",";
    private static final String WHERE = " where ";
    private static final String TAG_NAME_KEY = "tagName";
    private static final String PART_OF_NAME_KEY = "partOfName";
    private static final String PART_OF_DESCRIPTION_KEY = "partOfDescription";
    private static final String SORT_BY_NAME_KEY = "sortByName";
    private static final String SORT_BY_DATE_KEY = "sortByDate";
    private static final String ACCEDING_ORDER = "asc";
    private static final String DESCENDING_ORDER = "desc";
    private static final String PATTERN_FOR_LIKE_QUERY = "%%%s%%";
    private static final String GROUP_BY_CERTIFICATE_ID = " group by gift_certificate.id ";
    private static final String HAVING_CERTIFICATE_ID_COUNT = "having count(certificate_id) = ";

    private String finalQuery;

    /**
     * Build sql find all query that by passed parameters. argument map contains
     * keys that define parameter then appropriate sql query will be built.
     * If parameters map contains keys for order then sql will be build with order statement
     *
     * @param findParameters parameters map that define find certificate parameters
     * @return built sql find all certificates statement that defined by passed parameters map
     */
    public String buildSql(LinkedHashMap<String, String> findParameters) {
        String tagNames = findParameters.get(TAG_NAME_KEY);
        String partOfName = findParameters.get(PART_OF_NAME_KEY);
        String partOfDescription = findParameters.get(PART_OF_DESCRIPTION_KEY);
        Set<Map.Entry<String, String>> entries = findParameters.entrySet();
        FindCertificatesSqlBuilder builder = findAll()
                .whereTagName(tagNames)
                .wherePartOfName(partOfName)
                .wherePartOfDescription(partOfDescription)
                .groupByCertificateId()
                .havingIdCountEquals(tagNames);
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder = key.equals(SORT_BY_NAME_KEY) ? builder.orderByName(value) : builder;
            builder = key.equals(SORT_BY_DATE_KEY) ? builder.orderByDate(value) : builder;
        }
        return builder.build();
    }

    private FindCertificatesSqlBuilder havingIdCountEquals(String tagNames) {
        if (isNullOrEmptyParameter(tagNames)) {
            return this;
        }
        String[] names = tagNames.split(",");
        finalQuery += HAVING_CERTIFICATE_ID_COUNT;
        finalQuery += names.length;
        return this;
    }

    private FindCertificatesSqlBuilder groupByCertificateId() {
        finalQuery += GROUP_BY_CERTIFICATE_ID;
        return this;
    }

    private FindCertificatesSqlBuilder findAll() {
        finalQuery = BASE_SQL;
        return this;
    }

    /**
     * Reads passed find certificates parameters map and make
     * parameters values list. That need to pass sql values to Prepared statement
     * as PreparedStatement query values so PreparedStatement can execute query
     *
     * @param findParameters find all certificates parameters map
     * @return list of find parameters values
     */
    public List<String> getSqlValues(LinkedHashMap<String, String> findParameters) {
        String tagNames = findParameters.get(TAG_NAME_KEY);
        String partOfName = findParameters.get(PART_OF_NAME_KEY);
        String partOfDescription = findParameters.get(PART_OF_DESCRIPTION_KEY);
        if (partOfName != null) {
            partOfName = String.format(PATTERN_FOR_LIKE_QUERY, partOfName);
        }
        if (partOfDescription != null) {
            partOfDescription = String.format(PATTERN_FOR_LIKE_QUERY, partOfDescription);
        }
        List<String> values = new ArrayList<>();
        if (tagNames != null) {
            String[] names = tagNames.split(COMMA);
            values.addAll(Arrays.asList(names));
        }
        values.add(partOfName);
        values.add(partOfDescription);
        values.removeIf(Objects::isNull);
        return values;
    }

    private FindCertificatesSqlBuilder whereTagName(String tagNames) {
        if (isNullOrEmptyParameter(tagNames)) {
            return this;
        }
        String[] names = tagNames.split(COMMA);
        finalQuery = finalQuery.contains(WHERE) ? finalQuery + AND : finalQuery + WHERE;
        finalQuery += TAG_ID_IN;
        StringJoiner joiner = new StringJoiner(" or ", "(select tag.id from tag where ", ") ");
        for (String name : names) {
            joiner.add("tag.name = ?");
        }
        finalQuery += joiner;
        return this;
    }

    private FindCertificatesSqlBuilder wherePartOfName(String partOfName) {
        if (isNullOrEmptyParameter(partOfName)) {
            return this;
        }
        finalQuery = finalQuery.contains(WHERE) ? finalQuery + AND + BY_PART_OF_NAME : finalQuery + WHERE + BY_PART_OF_NAME;
        return this;
    }

    private FindCertificatesSqlBuilder wherePartOfDescription(String partOfDescription) {
        if (isNullOrEmptyParameter(partOfDescription)) {
            return this;
        }
        finalQuery = finalQuery.contains(WHERE) ? finalQuery + AND + BY_PART_OF_DESCRIPTION : finalQuery + WHERE + BY_PART_OF_DESCRIPTION;
        return this;
    }

    private FindCertificatesSqlBuilder orderByName(String order) {
        if (isNullOrEmptyParameter(order)) {
            return this;
        }
        if (order.equals(ACCEDING_ORDER)) {
            finalQuery = finalQuery.contains(ORDER_BY) ? finalQuery + COMMA : finalQuery + ORDER_BY;
            finalQuery += NAME_ASC;
        } else if (order.equals(DESCENDING_ORDER)) {
            finalQuery = finalQuery.contains(ORDER_BY) ? finalQuery + COMMA : finalQuery + ORDER_BY;
            finalQuery += NAME_DESC;
        }
        return this;
    }

    private FindCertificatesSqlBuilder orderByDate(String order) {
        if (isNullOrEmptyParameter(order)) {
            return this;
        }
        if (order.equals(ACCEDING_ORDER)) {
            finalQuery = finalQuery.contains(ORDER_BY) ? finalQuery + COMMA : finalQuery + ORDER_BY;
            finalQuery += DATE_ASC;
        } else if (order.equals(DESCENDING_ORDER)) {
            finalQuery = finalQuery.contains(ORDER_BY) ? finalQuery + COMMA : finalQuery + ORDER_BY;
            finalQuery += DATE_DESC;
        }
        return this;
    }

    private boolean isNullOrEmptyParameter(String parameter) {
        return parameter == null || parameter.isEmpty();
    }

    private String build() {
        return finalQuery;
    }

}
