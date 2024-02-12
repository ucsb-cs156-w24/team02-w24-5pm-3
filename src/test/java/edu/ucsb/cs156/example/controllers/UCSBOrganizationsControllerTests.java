package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBOrganizations;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationsRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBOrganizationsController.class)
@Import(TestConfig.class)
public class UCSBOrganizationsControllerTests extends ControllerTestCase {

        @MockBean
        UCSBOrganizationsRepository ucsbOrganizationsRepository;

        @MockBean
        UserRepository userRepository;


        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsborganizations/all"))
                                .andExpect(status().is(403)); 
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsborganizations/all"))
                                .andExpect(status().is(200)); 
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                when(ucsbOrganizationsRepository.findById(eq("KASA"))).thenReturn(Optional.empty());

                MvcResult response = mockMvc.perform(get("/api/ucsborganizations?orgCode=KASA"))
                                .andExpect(status().isNotFound()).andReturn();


                verify(ucsbOrganizationsRepository, times(1)).findById(eq("KASA"));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("UCSBOrganizations with id KASA not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_ucsborganizations() throws Exception {


                UCSBOrganizations rowing = UCSBOrganizations.builder()
                                .orgCode("RC")
                                .orgTranslationShort("ROWING CLUB")
                                .orgTranslation("ROWING CLUB AT UCSB")
                                .inactive(false)
                                .build();

                UCSBOrganizations chess = UCSBOrganizations.builder()
                                .orgCode("CC")
                                .orgTranslationShort("CHESS CLUB")
                                .orgTranslation("CHESS CLUB AT UCSB")
                                .inactive(false)
                                .build();

                ArrayList<UCSBOrganizations> expectedOrganizations = new ArrayList<>();
                expectedOrganizations.addAll(Arrays.asList(rowing, chess));

                when(ucsbOrganizationsRepository.findAll()).thenReturn(expectedOrganizations);

                MvcResult response = mockMvc.perform(get("/api/ucsborganizations/all"))
                                .andExpect(status().isOk()).andReturn();


                verify(ucsbOrganizationsRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedOrganizations);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }


        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsborganizations/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsborganizations/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_organizations() throws Exception {

                UCSBOrganizations soccer = UCSBOrganizations.builder()
                                .orgCode("SC")
                                .orgTranslationShort("SOCCER")
                                .orgTranslation("SOCCER CLUB AT UCSB")
                                .inactive(true)
                                .build();

                when(ucsbOrganizationsRepository.save(eq(soccer))).thenReturn(soccer);

                MvcResult response = mockMvc.perform(
                                post("/api/ucsborganizations/post?orgCode=SC&orgTranslationShort=SOCCER&orgTranslation=SOCCER CLUB AT UCSB&inactive=true")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                verify(ucsbOrganizationsRepository, times(1)).save(soccer);
                String expectedJson = mapper.writeValueAsString(soccer);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }


                @Test
                public void logged_out_users_cannot_get_by_id() throws Exception {
                        mockMvc.perform(get("/api/ucsborganizations?orgCode=ZC"))
                                        .andExpect(status().is(403)); 
                }

                @WithMockUser(roles = { "USER" })
                @Test
                public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {


                        UCSBOrganizations lacrosse = UCSBOrganizations.builder()
                                .orgCode("LC")
                                .orgTranslationShort("LACROSSE")
                                .orgTranslation("LACROSSE CLUB AT UCSB")
                                .inactive(true)
                                .build();

                        when(ucsbOrganizationsRepository.findById(eq("LC"))).thenReturn(Optional.of(lacrosse));

                        MvcResult response = mockMvc.perform(get("/api/ucsborganizations?orgCode=LC"))
                                        .andExpect(status().isOk()).andReturn();


                        verify(ucsbOrganizationsRepository, times(1)).findById(eq("LC"));
                        String expectedJson = mapper.writeValueAsString(lacrosse);
                        String responseString = response.getResponse().getContentAsString();
                        assertEquals(expectedJson, responseString);
                }


                @WithMockUser(roles = { "ADMIN", "USER" })
                @Test
                public void admin_can_delete_a_date() throws Exception {

                        UCSBOrganizations volleyball = UCSBOrganizations.builder()
                                .orgCode("VC")
                                .orgTranslationShort("VOLLEYBALL")
                                .orgTranslation("VOLLEYBALL CLUB AT UCSB")
                                .inactive(true)
                                .build();

                        when(ucsbOrganizationsRepository.findById(eq("VC"))).thenReturn(Optional.of(volleyball));

                        MvcResult response = mockMvc.perform(
                                        delete("/api/ucsborganizations?orgCode=VC")
                                                        .with(csrf()))
                                        .andExpect(status().isOk()).andReturn();

                        verify(ucsbOrganizationsRepository, times(1)).findById("VC");
                        verify(ucsbOrganizationsRepository, times(1)).delete(any());

                        Map<String, Object> json = responseToJson(response);
                        assertEquals("UCSBOrganizations with id VC deleted", json.get("message"));
                }

                @WithMockUser(roles = { "ADMIN", "USER" })
                @Test
                public void admin_tries_to_delete_non_existant_commons_and_gets_right_error_message()
                                throws Exception {

                        when(ucsbOrganizationsRepository.findById(eq("CSA"))).thenReturn(Optional.empty());

                        MvcResult response = mockMvc.perform(
                                        delete("/api/ucsborganizations?orgCode=CSA")
                                                        .with(csrf()))
                                        .andExpect(status().isNotFound()).andReturn();

                        verify(ucsbOrganizationsRepository, times(1)).findById("CSA");
                        Map<String, Object> json = responseToJson(response);
                        assertEquals("UCSBOrganizations with id CSA not found", json.get("message"));
                }

}