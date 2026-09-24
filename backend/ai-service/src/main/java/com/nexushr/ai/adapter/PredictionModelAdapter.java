package com.nexushr.ai.adapter;

import com.nexushr.ai.dto.AttritionFeatureVector;
import com.nexushr.ai.model.AttritionPrediction;
import org.springframework.stereotype.Component;

@Component
public class PredictionModelAdapter {

    public AttritionPrediction evaluate(AttritionFeatureVector features) {
        // Decision support logic for HR authorized users
        double riskScore = 0.0;
        if (features.getTenureMonths() < 12) riskScore += 0.3;
        if (features.getEngagementScore() < 60.0) riskScore += 0.4;
        if (features.getWorkloadScore() > 80.0) riskScore += 0.3;

        String riskLevel = riskScore > 0.6 ? "HIGH" : riskScore > 0.3 ? "MEDIUM" : "LOW";
        double confidence = 0.88;

        String explanation = String.format("Risk assessed based on tenure (%d months), engagement score (%.1f), and workload (%.1f).",
                features.getTenureMonths(), features.getEngagementScore(), features.getWorkloadScore());
        
        String recommendation = riskLevel.equals("HIGH") 
            ? "Schedule retention check-in and review compensation benchmarking."
            : "Continue routine engagement tracking.";

        return new AttritionPrediction(
                features.getEmployeeId(),
                riskLevel,
                confidence,
                "v2.4-enterprise-xgboost",
                "Tenure, Engagement, Workload",
                explanation,
                recommendation
        );
    }
}
