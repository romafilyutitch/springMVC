package com.epam.esm.builder;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FindCertificateBuilder {
    private static final String BASE_SQL = "select gift_certificate.*, tag.* from gift_certificate " +
            "left join certificate_tag on certificate_tag.certificate_id = gift_certificate.id " +
            "left join tag on certificate_tag.tag_id = tag.id ";
    private static final String BY_ID = "where gift_certificate.id = ?";
    private static final String BY_TAG_NAME = "tag.name = ?";
    private static final String BY_PART_OF_NAME = "gift_certificate.name like ?";
    private static final String BY_PART_OF_DESCRIPTION = "gift_certificate.description like ?";
    private static final String AND = " and ";
    private static final String ORDER_BY = " order by ";
    private static final String NAME_ASC = "gift_certificate.name asc";
    private static final String NAME_DESC = "gift_certificate.name desc ";
    private static final String DATE_ASC = "gift_certificate.create_date asc";
    private static final String DATE_DESC = "gift_certificate.create_date desc";
    private static final String THEN_ORDER = ",";
    private static final String WHERE = " where ";

    private String finalQuery;

    public String buildSql(LinkedHashMap<String, String> findParameters) {
        String tagName = findParameters.get("tagName");
        String partOfName = findParameters.get("partOfName");
        String partOfDescription = findParameters.get("partOfDescription");
        Set<Map.Entry<String, String>> entries = findParameters.entrySet();
        FindCertificateBuilder builder = findAll()
                .whereTagName(tagName)
                .wherePartOfName(partOfName)
                .wherePartOfDescription(partOfDescription);
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder = key.equals("sortByName") ? builder.orderByName(value) : builder;
            builder = key.equals("sortByDate") ? builder.orderByDate(value) : builder;
        }
        return builder.build();
    }

    private FindCertificateBuilder findAll() {
        finalQuery = BASE_SQL;
        return this;
    }

    public List<String> getSqlValues(LinkedHashMap<String, String> findParameters) {
        String tagName = findParameters.get("tagName");
        String partOfName = findParameters.get("partOfName");
        String partOfDescription = findParameters.get("partOfDescription");
        List<String> values = new ArrayList<>();
        if (partOfName != null) {
            partOfName = String.format("%%%s%%", partOfName);
        }
        if (partOfDescription != null) {
            partOfDescription = String.format("%%%s%%", partOfDescription);
        }
        values.add(tagName);
        values.add(partOfName);
        values.add(partOfDescription);
        values.removeIf(Objects::isNull);
        return values;
    }

    private FindCertificateBuilder whereTagName(String tagName) {
        if (tagName == null || tagName.isEmpty()) {
            return this;
        }
        finalQuery = finalQuery.contains(WHERE) ? finalQuery + AND + BY_TAG_NAME : finalQuery + WHERE + BY_TAG_NAME ;
        return this;
    }

    private FindCertificateBuilder wherePartOfName(String partOfName) {
        if (partOfName == null || partOfName.isEmpty()) {
            return this;
        }
        finalQuery = finalQuery.contains(WHERE) ? finalQuery + AND + BY_PART_OF_NAME : finalQuery + WHERE + BY_PART_OF_NAME ;
        return this;
    }

    private FindCertificateBuilder wherePartOfDescription(String partOfDescription) {
        if (partOfDescription == null || partOfDescription.isEmpty()) {
            return this;
        }
        finalQuery = finalQuery.contains(WHERE) ? finalQuery + AND + BY_PART_OF_DESCRIPTION : finalQuery + WHERE + BY_PART_OF_DESCRIPTION;
        return this;
    }

    private FindCertificateBuilder orderByName(String order) {
        if (order == null || order.isEmpty()) {
            return this;
        }
        if (finalQuery.contains(ORDER_BY)) {
            finalQuery += THEN_ORDER;
        } else {
            finalQuery += ORDER_BY;
        }
        if (order.equals("asc")) {
            finalQuery += NAME_ASC;
        } else if (order.equals("desc")) {
            finalQuery += NAME_DESC;
        }
        return this;
    }

    private FindCertificateBuilder orderByDate(String order) {
        if (order == null || order.isEmpty()) {
            return this;
        }
        if (finalQuery.contains(ORDER_BY)) {
            finalQuery += THEN_ORDER;
        } else {
            finalQuery += ORDER_BY;
        }
        if (order.equals("asc")) {
            finalQuery += DATE_ASC;
        } else if (order.equals("desc")) {
            finalQuery += DATE_DESC;
        }
        return this;
    }

    private String build() {
        return finalQuery;
    }

    public static void main(String[] args) {
        FindCertificateBuilder findCertificateBuilder = new FindCertificateBuilder();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("sortByName", "asc");
        map.put("partOfName", "new");
        map.put("partOfDescription", "td");
        String sql = findCertificateBuilder.buildSql(map);
        List<String> sqlValues = findCertificateBuilder.getSqlValues(map);
        System.out.println(sql);
        System.out.println(sqlValues);
    }
}
