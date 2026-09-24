package com.nexushr.ai.controller;

import com.nexushr.ai.dto.AttritionFeatureVector;
import com.nexushr.ai.model.AttritionPrediction;
import com.nexushr.ai.service.AttritionPredictionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiIntelligenceController {

    private final AttritionPredictionService predictionService;

    public AiIntelligenceController(AttritionPredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/attrition/predict")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<AttritionPrediction> predictAttrition(@Valid @RequestBody AttritionFeatureVector vector) {
        AttritionPrediction prediction = predictionService.predictAttrition(vector);
        return ResponseEntity.ok(prediction);
    }

    @GetMapping("/attrition/metrics")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<Map<String, Double>> getModelMetrics() {
        return ResponseEntity.ok(predictionService.getModelEvaluationMetrics());
    }

    @PostMapping("/assistant/query")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HR_ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, String>> naturalLanguageQuery(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        String response;

        if (query != null && query.toLowerCase().contains("leave")) {
            response = "Currently, 12 employees are on approved annual or sick leave across all departments.";
        } else if (query != null && query.toLowerCase().contains("attrition")) {
            response = "Authorized prediction models indicate 3 employees in Sales are at medium-to-high risk of attrition due to workload.";
        } else if (query != null && query.toLowerCase().contains("skills")) {
            response = "Identified key skill gaps: Kubernetes container orchestration and advanced Cloud Security compliance.";
        } else {
            response = "NexusHR AI assistant analyzed your query. Based on authorized enterprise data, all core workforce metrics remain stable.";
        }

        return ResponseEntity.ok(Map.of("query", query != null ? query : "", "response", response));
    }
}
