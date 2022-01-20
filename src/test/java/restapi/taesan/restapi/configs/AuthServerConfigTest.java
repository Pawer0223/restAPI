package restapi.taesan.restapi.configs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.JsonPath;
import restapi.taesan.restapi.accounts.Account;
import restapi.taesan.restapi.accounts.AccountRole;
import restapi.taesan.restapi.accounts.AccountService;
import restapi.taesan.restapi.common.BaseControllerTest;
import restapi.taesan.restapi.common.TestDescription;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception {
        String username = "taesan@email.com";
        String pw = "taesan";
        Account taesan = Account.builder()
                .email(username)
                .password(pw)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(taesan);
        String clientId = "myApp";
        String clientSecret = "pass";
        this.mockMvc.perform(post("/oauth/token")
                    .with(httpBasic(clientId, clientSecret))
                    .param("username", username)
                    .param("password", pw)
                    .param("grant_type", "password"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("access_token").exists())
        ;
    }

}