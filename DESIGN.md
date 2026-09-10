# System Design: OpsCopilot

## Architecture Overview
OpsCopilot follows a **Modular Monolith** pattern. The application is divided clearly into the following components:
- Controllers for REST endpoints
- Services for core business logic
- Repositories for database interaction
- AI module for orchestrating Google Gemini tool calls
- Next.js frontend for an interactive conversational UI.

## Why a Modular Monolith?
For a project of this scale and for an MVP, a microservices architecture (e.g., separating Order, Payment, Delivery into different apps) introduces unnecessary complexity (network latency, distributed transactions, complex deployment). A modular monolith perfectly addresses the domain separation without the infrastructure overhead.

## Request Flow
1. **User Request**: The user submits a query through the Next.js UI.
2. **API Controller**: `CopilotController` receives the query.
3. **Orchestration Loop**: `CopilotService` begins a loop. It sends the prompt + tool declarations to Gemini.
4. **Tool Selection**: Gemini evaluates the prompt and decides to call a specific tool (e.g., `get_payment`).
5. **Tool Execution**: `CopilotService` dynamically calls the registered Java tool. The tool calls the relevant business `Service` which fetches data from the `Repository`.
6. **Result to LLM**: The factual result is passed back to Gemini.
7. **Iterative Checking**: Gemini decides if more information is needed or if the final response can be generated.
8. **Final Response**: Once sufficient data is gathered, Gemini synthesizes the final textual response.

## Database Design
- `orders`: Contains order metadata and links to `customers`.
- `payments`: Linked to orders, tracks transaction status.
- `deliveries`: Linked to orders, tracks fulfillment.
- `order_events`: Timeline tracking for operational history.

## Error Handling
Exceptions like `ResourceNotFoundException` and `GeminiException` are globally caught by `@RestControllerAdvice`, ensuring that standard JSON error objects are returned instead of stack traces.

## Security
The AI never executes arbitrary SQL or shell commands. It can only trigger the predefined Java tools (which act as read-only endpoints). The Gemini API key remains isolated in the backend server.
