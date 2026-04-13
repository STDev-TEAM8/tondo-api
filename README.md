# tondo-api
STDev 8 Team : Server-side code repository

## Poc: 실행 방법
1. DB 실행
   docker compose up -d

2. 서버 실행
   ./gradlew bootRun

서버: http://localhost:8080

## API

- POST /api/v1/artworks — 이미지 생성 요청 (202 + taskId 반환)
- GET /api/v1/tasks/{taskId}/stream — SSE 진행 상태 스트리밍
- GET /api/v1/artworks/{taskId} — 완성 결과 조회

현재 stub 상태 (하드코딩 응답). Bedrock 연동 전.
