import zipfile
import re

with zipfile.ZipFile('Spoken_English_Guru_extraction_complete.zip', 'r') as z:
    for lesson_num in range(1, 39):
        name = f'completed/Lesson_{lesson_num:02d}.md'
        try:
            content = z.read(name).decode('utf-8', errors='ignore')
        except KeyError:
            continue
            
        sentences = []
        
        # Style 1: ### [num]. [Hindi] \n **Romanized:** ... \n **English answer:** ...
        matches_s1 = re.findall(
            r'###\s*\d+\.?\s*(.*?)\n\s*\*\*Romanized:\*\*\s*(.*?)\n\s*\*\*(?:English answer|Answer):\*\*\s*(.*?)(?=\n###|\n##|\Z)',
            content, re.DOTALL
        )
        for m in matches_s1:
            hindi = m[0].strip()
            rom = m[1].strip().strip('()')
            ans = m[2].strip()
            if hindi and ans:
                sentences.append(('STYLE1', hindi, rom, ans))
                
        # Style 2: - **Hindi:** ... \n - **Romanized:** ... \n - **Answer:** ...
        matches_s2 = re.findall(
            r'-\s*\*\*Hindi:\*\*\s*(.*?)\n\s*-\s*\*\*Romanized:\*\*\s*(.*?)\n\s*-\s*\*\*Answer:\*\*\s*(.*?)(?=\n###|\n##|\n-|\Z)',
            content, re.DOTALL
        )
        for m in matches_s2:
            hindi = m[0].strip()
            rom = m[1].strip().strip('()')
            ans = m[2].strip()
            if hindi and ans:
                sentences.append(('STYLE2', hindi, rom, ans))

        # Style 3: Active to Passive (Lesson 24)
        matches_s3 = re.findall(
            r'\d+\.\s*\*\*Active sentence:\*\*\s*(.*?)\n\s*\*\*Passive voice answer:\*\*\s*(.*?)(?=\n\d+\.|\n##|\Z)',
            content, re.DOTALL
        )
        for m in matches_s3:
            sentences.append(('ACTIVE_PASSIVE', m[0].strip(), '', m[1].strip()))

        # Style 4: Direct to Indirect (Lesson 30)
        matches_s4 = re.findall(
            r'\d+\.\s*\*\*Direct speech:\*\*\s*(.*?)\n\s*\*\*Indirect speech:\*\*\s*(.*?)(?=\n\d+\.|\n##|\Z)',
            content, re.DOTALL
        )
        for m in matches_s4:
            sentences.append(('DIRECT_INDIRECT', m[0].strip(), '', m[1].strip()))

        # Style 5: Sentence Correction (Lesson 37)
        matches_s5 = re.findall(
            r'\d+\.\s*\*\*Original:\*\*\s*(.*?)\n\s*\*\*Correct sentence.*:\*\*\s*(.*?)(?=\n\d+\.|\n##|\Z)',
            content, re.DOTALL
        )
        for m in matches_s5:
            sentences.append(('CORRECTION', m[0].strip(), '', m[1].strip()))

        print(f"Lesson {lesson_num:02d}: {len(sentences)} practice items")
