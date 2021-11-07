package com.epam.esm;

import com.epam.esm.config.PersistanceConfig;
import com.epam.esm.model.Certificate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = PersistanceConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = {"classpath:delete.sql", "classpath:data.sql"})
class CertificateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void showCertificates_shouldReturnFoundCertificatesOnFirstPage() throws Exception {
        mockMvc.perform(get("/certificates"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._embedded.certificateList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.certificateList[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.certificateList[0].name", is("free music listen certificate")))
                .andExpect(jsonPath("$._embedded.certificateList[0].description", is("spotify free music listening")))
                .andExpect(jsonPath("$._embedded.certificateList[0].price", is(200.50)))
                .andExpect(jsonPath("$._embedded.certificateList[0].duration", is(20)))
                .andExpect(jsonPath("$._embedded.certificateList[0].tags", hasSize(3)))
                .andExpect(jsonPath("$._embedded.certificateList[0].tags[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.certificateList[0].tags[0].name", is("spotify")))
                .andExpect(jsonPath("$._embedded.certificateList[0].tags[1].id", is(2)))
                .andExpect(jsonPath("$._embedded.certificateList[0].tags[1].name", is("music")))
                .andExpect(jsonPath("$._embedded.certificateList[0].tags[2].id", is(3)))
                .andExpect(jsonPath("$._embedded.certificateList[0].tags[2].name", is("art")))
                .andExpect(jsonPath("$._embedded.certificateList[0]._links.certificate.href", is("http://localhost/certificates/1")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/certificates?offset=0&limit=10")))
                .andExpect(jsonPath("$._links.next.href", is("http://localhost/certificates?offset=10&limit=10")))
                .andExpect(jsonPath("$._links.previous.href", is("http://localhost/certificates?offset=-10&limit=10")));
    }

    @Test
    public void showCertificatePage_shouldThrowExceptionIfOffsetIfGreaterThenTotalElements() throws Exception {
        mockMvc.perform(get("/certificates?offset=100"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.errorCode", is("40403")))
                .andExpect(jsonPath("$.message", is("Current offset 100 is out of bounds. Total elements amount is 1")));
    }

    @Test
    public void showCertificateById_shouldReturnHttpStatusCode404() throws Exception {
        mockMvc.perform(get("/certificates/{id}", 100L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.errorCode", is("40401")))
                .andExpect(jsonPath("$.message", is("Certificate with id 100 not found")));
    }

    @Test
    public void showCertificateById_shouldReturnFoundCertificate() throws Exception {
        mockMvc.perform(get("/certificates/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("free music listen certificate")))
                .andExpect(jsonPath("$.description", is("spotify free music listening")))
                .andExpect(jsonPath("$.price", is(200.50)))
                .andExpect(jsonPath("$.duration", is(20)))
                .andExpect(jsonPath("$.tags", hasSize(3)))
                .andExpect(jsonPath("$.tags[0].id", is(1)))
                .andExpect(jsonPath("$.tags[0].name", is("spotify")))
                .andExpect(jsonPath("$.tags[1].id", is(2)))
                .andExpect(jsonPath("$.tags[1].name", is("music")))
                .andExpect(jsonPath("$.tags[2].id", is(3)))
                .andExpect(jsonPath("$.tags[2].name", is("art")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/certificates/1")))
                .andExpect(jsonPath("$._links.tags.href", is("http://localhost/certificates/1/tags?offset=0&limit=10")))
                .andExpect(jsonPath("$._links.orders.href", is("http://localhost/certificates/1/orders?offset=0&limit=10")));
    }

    @Test
    public void saveCertificate_shouldReturnBadRequestForInvalidCertificate() throws Exception {
        Certificate invalidCertificate = new Certificate("", "", -10.0, -10);
        mockMvc.perform(post("/certificates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidCertificate)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.errorCode", is("40002")))
                .andExpect(jsonPath("$.message", is("Passed certificate is invalid. Name and description must be not empty. Price and duration must be positive")));
    }

    @Test
    public void saveCertificate_shouldAddNewCertificate() throws Exception {
        Certificate certificate = new Certificate("certificate", "test certificate", 10.0, 100);
        mockMvc.perform(post("/certificates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(certificate)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("certificate")))
                .andExpect(jsonPath("$.description", is("test certificate")))
                .andExpect(jsonPath("$.price", is(10.0)))
                .andExpect(jsonPath("$.duration", is(100)))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/certificates/2")));
    }

    @Test
    public void updateCertificate_shouldUpdateCertificateWithPassedId() throws Exception {
        Certificate certificateForUpdate = new Certificate("spotify", "spotify", 10.0, 10);
        mockMvc.perform(post("/certificates/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(certificateForUpdate)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("spotify")))
                .andExpect(jsonPath("$.description", is("spotify")))
                .andExpect(jsonPath("$.price", is(10.0)))
                .andExpect(jsonPath("$.duration", is(10)))
                .andExpect(jsonPath("$.tags", hasSize(3)))
                .andExpect(jsonPath("$.tags[0].id", is(1)))
                .andExpect(jsonPath("$.tags[0].name", is("spotify")))
                .andExpect(jsonPath("$.tags[1].id", is(2)))
                .andExpect(jsonPath("$.tags[1].name", is("music")))
                .andExpect(jsonPath("$.tags[2].id", is(3)))
                .andExpect(jsonPath("$.tags[2].name", is("art")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/certificates/1")))
                .andExpect(jsonPath("$._links.tags.href", is("http://localhost/certificates/1/tags?offset=0&limit=10")))
                .andExpect(jsonPath("$._links.orders.href", is("http://localhost/certificates/1/orders?offset=0&limit=10")));
    }

    @Test
    public void deleteCertificate_shouldDeleteCertificate() throws Exception {
        mockMvc.perform(delete("/certificates/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void showCertificateTags_shouldReturnCertificateTags() throws Exception {
        mockMvc.perform(get("/certificates/{id}/tags", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$._embedded.tagList", hasSize(3)))
                .andExpect(jsonPath("$._embedded.tagList[0].id", is(1)))
                .andExpect(jsonPath("$._embedded.tagList[0].name", is("spotify")))
                .andExpect(jsonPath("$._embedded.tagList[1].id", is(2)))
                .andExpect(jsonPath("$._embedded.tagList[1].name", is("music")))
                .andExpect(jsonPath("$._embedded.tagList[2].id", is(3)))
                .andExpect(jsonPath("$._embedded.tagList[2].name", is("art")))
                .andExpect(jsonPath("$._embedded.tagList[0]._links.tag.href", is("http://localhost/certificates/1/tags/1")))
                .andExpect(jsonPath("$._embedded.tagList[1]._links.tag.href", is("http://localhost/certificates/1/tags/2")))
                .andExpect(jsonPath("$._embedded.tagList[2]._links.tag.href", is("http://localhost/certificates/1/tags/3")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/certificates/1/tags?offset=0&limit=10")))
                .andExpect(jsonPath("$._links.next.href", is("http://localhost/certificates/1/tags?offset=10&limit=10")))
                .andExpect(jsonPath("$._links.previous.href", is("http://localhost/certificates/1/tags?offset=-10&limit=10")));
    }

    @Test
    public void showCertificateTag_shouldReturnCertificateTag() throws Exception {
        mockMvc.perform(get("/certificates/{id}/tags/{tagId}", 1L, 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("spotify")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/certificates/1/tags/1")));
    }

    @Test
    public void showCertificateTag_shouldReturn404WhenThereIsNotCertificateTag() throws Exception {
        mockMvc.perform(get("/certificates/{id}/tags/{tagId}", 1L, 100L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.errorCode", is("40401")))
                .andExpect(jsonPath("$.message", is("Tag with id 100 not found")));
    }

    @Test
    public void deleteCertificateTag_shouldDeleteCertificateIfThereIsCertificate() throws Exception {
        mockMvc.perform(delete("/certificates/{id}/tags/{tagId}", 1L, 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteCertificateTag_shouldReturn404ErrorCodeIfThereIsNoCertificateTag() throws Exception {
        mockMvc.perform(delete("/certificates/{id}/tags/{tagId}", 1L, 100L))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("40401")))
                .andExpect(jsonPath("$.message", is("Tag with id 100 not found")));
    }
}