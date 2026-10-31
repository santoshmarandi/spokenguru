import zipfile
import re

with zipfile.ZipFile('Spoken_English_Guru_extraction_complete.zip', 'r') as z:
    content = z.read('completed/Lesson_39.md').decode('utf-8', errors='ignore')

quizzes = re.split(r'## Quiz\s+(\d+)', content)
print(f'Total quiz segments: {len(quizzes)}')
total_questions = 0
total_with_ans = 0

for i in range(1, len(quizzes), 2):
    quiz_num = int(quizzes[i])
    q_content = quizzes[i+1]
    
    # Extract answers
    ans_dict = {}
    ans_match = re.search(r'Answers?\s*\n(.*?)(?:```|\Z)', q_content, re.DOTALL | re.IGNORECASE)
    if ans_match:
        ans_text = ans_match.group(1).replace('\n', ' ')
        for item in re.finditer(r'(\d+)\s*([a-dA-D])', ans_text):
            ans_dict[int(item.group(1))] = item.group(2).lower()
            
    # Extract questions
    q_matches = re.finditer(r'Q\s*(\d+)\s*:(.*?)(?=(?:Q\s*\d+\s*:|Answers?|```|\Z))', q_content, re.DOTALL | re.IGNORECASE)
    q_list = list(q_matches)
    total_questions += len(q_list)
    for q in q_list:
        q_idx = int(q.group(1))
        if q_idx in ans_dict:
            total_with_ans += 1

print(f"Quizzes found: {(len(quizzes)-1)//2}")
print(f"Total MCQs parsed: {total_questions}")
print(f"Total MCQs with matching answer key: {total_with_ans}")
