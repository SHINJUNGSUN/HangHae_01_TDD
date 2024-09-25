package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(PointController.class)
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    @Test
    @DisplayName("포인트 조회 컨트롤러 테스트")
    public void pointTest() throws Exception {
        long id = 1L;

        mockMvc.perform(get("/point/{id}", id))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("포인트 충전/이용 내역 조회 컨트롤러 테스트")
    public void historyTest() throws Exception {
        long id = 1L;

        mockMvc.perform(get("/point/{id}/histories", id))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("포인트 충전 컨트롤러 테스트")
    public void chargeTest() throws Exception {
        long id = 1L;
        long amount = 1000L;

        mockMvc.perform(patch("/point/{id}/charge", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(amount)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("포인트 사용 컨트롤러 테스트")
    public void useTest() throws Exception {
        long id = 1L;
        long amount = 1000L;

        mockMvc.perform(patch("/point/{id}/use", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(amount)))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
