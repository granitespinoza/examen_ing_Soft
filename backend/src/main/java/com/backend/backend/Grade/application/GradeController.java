package com.backend.backend.Grade.application;

import com.backend.backend.Grade.domain.GradeService;
import com.backend.backend.Grade.dto.CalculateGradeRequestDTO;
import com.backend.backend.Grade.dto.GradeResultDTO;
import com.backend.backend.common.dto.ResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestionar el cálculo de notas finales.
 * Implementa todos los RF: RF01-RF05
 */
@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {
    
    private final GradeService gradeService;
    
    /**
     * RF04: Solicitar cálculo de nota final.
     * Endpoint principal que calcula la nota final considerando:
     * - RF01: Evaluaciones (nota + peso %)
     * - RF02: Asistencia mínima
     * - RF03: Acuerdo de docentes para puntos extra
     * - RF05: Retorna detalle completo del cálculo
     * 
     * @param request DTO con los datos del estudiante
     * @return Resultado del cálculo con todos los detalles
     */
    @PostMapping("/calculate")
    public ResponseEntity<ResponseDTO<GradeResultDTO>> calculateFinalGrade(
            @Valid @RequestBody CalculateGradeRequestDTO request) {
        try {
            GradeResultDTO result = gradeService.calculateFinalGrade(request);
            return ResponseEntity.ok(ResponseDTO.success("Cálculo realizado exitosamente", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDTO.error(e.getMessage()));
        }
    }
}

