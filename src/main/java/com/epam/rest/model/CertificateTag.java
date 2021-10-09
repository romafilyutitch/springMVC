package com.epam.rest.model;

import java.util.Objects;

public class CertificateTag extends Entity {
    private Long certificateId;
    private Long tagId;

    public CertificateTag(Long id, Long certificateId, Long tagId) {
        super(id);
        this.certificateId = certificateId;
        this.tagId = tagId;
    }

    public CertificateTag(Long certificateId, Long tagId) {
        this(null, certificateId, tagId);
    }

    public CertificateTag() {
        super(null);
    }

    public Long getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(Long certificateId) {
        this.certificateId = certificateId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CertificateTag that = (CertificateTag) o;
        return Objects.equals(certificateId, that.certificateId) && Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), certificateId, tagId);
    }

    @Override
    public String toString() {
        return "CertificateTag{" +
                "certificateId=" + certificateId +
                ", tagId=" + tagId +
                '}';
    }
}
