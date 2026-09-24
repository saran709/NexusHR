# NexusHR AI Workforce Intelligence Documentation

## 1. Overview & Provider Abstraction
The AI Workforce Intelligence module uses **Spring AI** with a provider abstraction layer supporting OpenAI, Hugging Face, and local models.

## 2. Core Capabilities
1. **Attrition Prediction**: Evaluates tenure, attendance trends, overtime, leave utilization, performance scores, salary bands, and feedback sentiment to output risk scores and confidence metrics.
2. **Skill Gap Analysis**: Compares employee skills against job-role requirements and recommends targeted training paths and certifications.
3. **Engagement Scoring**: Computes organizational engagement factors with detailed explanations.
4. **Natural-Language HR Assistant**: Enables authorized HR personnel to query workforce data securely.

## 3. Ethical Safeguards & Compliance
- **No Automated Employment Decisions**: AI outputs serve strictly as decision-support information for authorized HR personnel. Every insight carries a prominent disclaimer.
- **Strict Authorization**: The AI assistant obeys user RBAC roles and never exposes unauthorized employee PII.
- **PII Scrubbing**: Server-side filters scrub sensitive credentials and PII before dispatching payloads to external AI providers.
