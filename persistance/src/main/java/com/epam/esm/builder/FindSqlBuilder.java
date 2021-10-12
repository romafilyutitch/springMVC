package com.epam.esm.builder;

public class FindSqlBuilder {
    private static final String BASE_SQL = "select gift_certificate.*, tag.* from gift_certificate " +
            "left join certificate_tag on certificate_tag.certificate_id = gift_certificate.id " +
            "left join tag on certificate_tag.tag_id = tag.id ";
    private static final String BY_ID = "where gift_certificate.id = ?";
    private static final String BY_NAME = "where gift_certificate.name = ?";
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

    private static final FindSqlBuilder builder = new FindSqlBuilder();

    public static FindSqlBuilder findAll() {
        builder.finalQuery = BASE_SQL;
        return builder;
    }

    public FindSqlBuilder and() {
        finalQuery += AND;
        return this;
    }

    public FindSqlBuilder whereId() {
        finalQuery += BY_ID;
        return this;
    }

    public FindSqlBuilder wherePartOfName() {
        finalQuery += BY_PART_OF_NAME;
        return this;
    }

    public FindSqlBuilder wherePartOfDescription() {
        finalQuery += BY_PART_OF_DESCRIPTION;
        return this;
    }

    public FindSqlBuilder orderBy() {
        finalQuery = finalQuery + ORDER_BY;
        return this;
    }

    public FindSqlBuilder thenOrder() {
        finalQuery += THEN_ORDER;
        return this;
    }

    public FindSqlBuilder nameAcceding() {
        finalQuery = finalQuery + NAME_ASC;
        return this;
    }

    public FindSqlBuilder nameDescending() {
        finalQuery = finalQuery + NAME_DESC;
        return this;
    }

    public FindSqlBuilder dateAcceding() {
        finalQuery = finalQuery + DATE_ASC;
        return this;
    }

    public FindSqlBuilder dateDescending() {
        finalQuery = finalQuery + DATE_DESC;
        return this;
    }

    public String build() {
        return finalQuery;
    }

    public static void main(String[] args) {
        FindSqlBuilder builder = FindSqlBuilder.findAll()
                .wherePartOfName()
                .and()
                .wherePartOfDescription()
                .orderBy()
                .nameAcceding()
                .thenOrder()
                .dateAcceding();
        System.out.println(builder.build());
    }

}
