# 멍글멍글 API 명세

## POST /api/analyze
- **설명**: 사용자가 입력한 텍스트를 받아 감정을 분석하고 위로 문장을 반환.
- **Request**:
    - Method: POST
    - URL: /api/analyze
    - Content-Type: application/json
    - Body:
      ```json
      {
        "text": "오늘 좀 힘들었어..."
      }
      

