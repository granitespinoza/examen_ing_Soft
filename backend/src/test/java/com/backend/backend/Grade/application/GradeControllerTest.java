package com.backend.backend.Grade.application;

import com.backend.backend.Grade.domain.GradeService;
import com.backend.backend.Grade.dto.CalculateGradeRequestDTO;
import com.backend.backend.Grade.dto.EvaluationDTO;
import com.backend.backend.Grade.dto.GradeResultDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GradeController.class)
class GradeControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private GradeService gradeService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void shouldCalculateFinalGradeSuccessfully() throws Exception {
        CalculateGradeRequestDTO request = CalculateGradeRequestDTO.builder()
                .examsStudents(Arrays.asList(
                    EvaluationDTO.builder().grade(4.0).weight(50.0).build(),
                    EvaluationDTO.builder().grade(3.5).weight(50.0).build()
                ))
                .hasReachedMinimumClasses(true)
                .allYearsTeachers(false)
                .build();
        
        GradeResultDTO result = GradeResultDTO.builder()
                .weightedAverage(3.75)
                .attendancePenalty(0.0)
                .gradeAfterAttendance(3.75)
                .extraPoints(0.0)
                .finalGrade(3.75)
                .calculationTimeMs(10)
                .message("Promedio ponderado: 3.75. Asistencia mínima cumplida: sin penalización. Nota final: 3.75")
                .build();
        
        when(gradeService.calculateFinalGrade(any(CalculateGradeRequestDTO.class)))
                .thenReturn(result);
        
        mockMvc.perform(post("/api/grades/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.finalGrade").value(3.75))
                .andExpect(jsonPath("$.data.weightedAverage").value(3.75));
    }
    
    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        CalculateGradeRequestDTO request = CalculateGradeRequestDTO.builder()
                .examsStudents(null)
                .hasReachedMinimumClasses(true)
                .allYearsTeachers(false)
                .build();
        
        // Cuando hay un error de validación, el GlobalExceptionHandler devuelve ErrorResponse
        mockMvc.perform(post("/api/grades/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors").exists());
    }
}

