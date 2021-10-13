package com.epam.esm.builder;

public class FindCertificateBuilder {
    private static final String BASE_SQL = "select gift_certificate.*, tag.* from gift_certificate " +
            "left join certificate_tag on certificate_tag.certificate_id = gift_certificate.id " +
            "left join tag on certificate_tag.tag_id = tag.id ";
    private static final String BY_ID = "where gift_certificate.id = ?";
    private static final String BY_PART_OF_NAME = "where gift_certificate.name like ?";
    private static final String BY_TAG_NAME = "where tag.name = ?";
    private static final String BY_PART_OF_DESCRIPTION = "where gift_certificate.description like ?";
    private static final String AND = " and ";
    private static final String ORDER_BY = " order by ";
    private static final String NAME_ASC = "gift_certificate.name asc";
    private static final String NAME_DESC = "gift_certificate.name desc ";
    private static final String DATE_ASC = "gift_certificate.date asc";
    private static final String DATE_DESC = "gift_certificate.date desc";
    private static final String THEN_ORDER = ",";

    private String finalQuery;

    private static final FindCertificateBuilder builder = new FindCertificateBuilder();

    public static FindCertificateBuilder findAll() {
        builder.finalQuery = BASE_SQL;
        return builder;
    }

    public FindCertificateBuilder and() {
        finalQuery += AND;
        return this;
    }

    public FindCertificateBuilder whereTagName(String tagName) {
        if (tagName == null || tagName.isEmpty()) {
            return this;
        }
        finalQuery = finalQuery.equals(BASE_SQL) ? finalQuery + BY_TAG_NAME : finalQuery + AND + BY_TAG_NAME;
        return this;
    }

    public FindCertificateBuilder wherePartOfName(String partOfName) {
        if (partOfName == null || partOfName.isEmpty()) {
            return this;
        }
        finalQuery = finalQuery.equals(BASE_SQL) ? finalQuery + BY_PART_OF_NAME : finalQuery + AND + BY_PART_OF_NAME;
        return this;
    }

    public FindCertificateBuilder wherePartOfDescription(String partOfDescription) {
        if (partOfDescription == null || partOfDescription.isEmpty()) {
            return this;
        }
        finalQuery = finalQuery.equals(BASE_SQL) ? finalQuery + BY_PART_OF_DESCRIPTION : finalQuery + AND + BY_PART_OF_DESCRIPTION;
        return this;
    }

    public FindCertificateBuilder orderBy() {
        finalQuery += ORDER_BY;
        return this;
    }

    public FindCertificateBuilder thenOrder() {
        finalQuery += THEN_ORDER;
        return this;
    }

    public FindCertificateBuilder orderByName(String order) {
        if (order == null || order.isEmpty()) {
            return this;
        }
        if (order.equals("asc")) {
            finalQuery += NAME_ASC;
        } else if (order.equals("desc")) {
            finalQuery += NAME_DESC;
        }
        return this;
    }

    public FindCertificateBuilder orderByDate(String order) {
        if (order == null || order.isEmpty()) {
            return this;
        }
        if (order.equals("asc")) {
            finalQuery += DATE_ASC;
        } else if (order.equals("desc")) {
            finalQuery += DATE_DESC;
        }
        return this;
    }

    public String build() {
        return finalQuery;
    }
}
