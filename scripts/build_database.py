import os
import re
import sqlite3
import zipfile

ZIP_FILE = 'Spoken_English_Guru_extraction_complete.zip'
OUTPUT_DB = 'spoken_english.db'
ROOM_IDENTITY_HASH = '65c7883212c111f39b7abff06f0dee10'

def get_category_for_lesson(lesson_id):
    if lesson_id in [4, 5, 6, 31, 33, 34, 38]:
        return "Vocabulary & Word Forms"
    elif lesson_id in [1, 2, 3, 7, 8, 9, 17, 19, 21, 26, 27, 28, 29, 35]:
        return "Foundation & Concepts"
    elif lesson_id in [10, 11, 12, 13, 14, 15, 16, 20, 22, 23, 24, 25, 30, 32]:
        return "Grammar & Sentence Mechanics"
    elif lesson_id in [36, 37]:
        return "Applied Practice & Correction"
    elif lesson_id in [39, 40]:
        return "Quizzes & Mock Exams"
    return "General"

def create_database():
    if os.path.exists(OUTPUT_DB):
        os.remove(OUTPUT_DB)
    conn = sqlite3.connect(OUTPUT_DB)
    cursor = conn.cursor()

    # Exact DDL expected by Room in AppDatabase_Impl.java
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS `lessons` (
        `id` INTEGER NOT NULL, 
        `title` TEXT NOT NULL, 
        `category` TEXT NOT NULL, 
        `pdf_pages` TEXT, 
        `printed_pages` TEXT, 
        `summary` TEXT, 
        `total_items` INTEGER NOT NULL, 
        PRIMARY KEY(`id`)
    );
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS `sentence_practices` (
        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
        `lesson_id` INTEGER NOT NULL, 
        `type` TEXT NOT NULL, 
        `prompt_primary` TEXT NOT NULL, 
        `prompt_secondary` TEXT, 
        `english_answer` TEXT NOT NULL, 
        `notes` TEXT, 
        `is_favorite` INTEGER NOT NULL, 
        `mastery_score` INTEGER NOT NULL
    );
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS `mcq_questions` (
        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
        `lesson_id` INTEGER NOT NULL, 
        `quiz_num` INTEGER NOT NULL, 
        `question_num` INTEGER NOT NULL, 
        `prompt` TEXT NOT NULL, 
        `option_a` TEXT NOT NULL, 
        `option_b` TEXT NOT NULL, 
        `option_c` TEXT, 
        `option_d` TEXT, 
        `correct_option` TEXT NOT NULL, 
        `explanation` TEXT, 
        `is_bookmarked` INTEGER NOT NULL, 
        `times_wrong` INTEGER NOT NULL
    );
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS `vocab_items` (
        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
        `lesson_id` INTEGER NOT NULL, 
        `category` TEXT NOT NULL, 
        `word` TEXT NOT NULL, 
        `hindi_meaning` TEXT, 
        `romanized` TEXT, 
        `form2` TEXT, 
        `form3` TEXT, 
        `extra_info` TEXT, 
        `is_favorite` INTEGER NOT NULL
    );
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS `user_progress` (
        `key` TEXT NOT NULL, 
        `value_int` INTEGER NOT NULL, 
        `value_text` TEXT, 
        PRIMARY KEY(`key`)
    );
    """)

    # Room master table with compiled identity hash
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS room_master_table (
        id INTEGER PRIMARY KEY,
        identity_hash TEXT
    );
    """)
    cursor.execute("INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES (42, ?);", (ROOM_IDENTITY_HASH,))
    cursor.execute("PRAGMA user_version = 1;")

    conn.commit()
    return conn

def parse_lesson_index(z, conn):
    cursor = conn.cursor()
    content = z.read('lesson_index.md').decode('utf-8', errors='ignore')
    rows = re.findall(r'\|\s*(\d+)\s*\|\s*(.*?)\s*\|\s*(.*?)\s*\|\s*(.*?)\s*\|\s*(.*?)\s*\|', content)
    for row in rows:
        lid = int(row[0])
        title = row[1].strip()
        pdf_pages = row[2].strip()
        printed_pages = row[3].strip()
        cat = get_category_for_lesson(lid)
        cursor.execute(
            "INSERT INTO lessons (id, title, category, pdf_pages, printed_pages, summary, total_items) VALUES (?, ?, ?, ?, ?, ?, 0)",
            (lid, title, cat, pdf_pages, printed_pages, f"Complete practical guide and exercises for {title}")
        )
    conn.commit()
    print(f"Inserted {len(rows)} lessons from lesson_index.md")

def parse_sentences(z, conn):
    cursor = conn.cursor()
    total_sentences = 0

    for lid in range(1, 39):
        name = f"completed/Lesson_{lid:02d}.md"
        try:
            content = z.read(name).decode('utf-8', errors='ignore')
        except KeyError:
            continue

        lesson_count = 0

        # Pattern A: ### heading
        blocks = re.split(r'\n(?=###\s*\d+)', content)
        for b in blocks:
            if not b.startswith('###'):
                continue
            
            m_h = re.search(r'###\s*\d+\.?\s*(.*?)(?=\n|$)', b)
            heading_text = m_h.group(1).strip() if m_h else ""
            
            hindi = ""
            rom = ""
            ans = ""

            m_hp = re.search(r'[-*]*\s*\*\*Hindi(?:\s*prompt)?:\*\*\s*(.*?)(?=\n|$)', b)
            if m_hp:
                hindi = m_hp.group(1).strip()
            elif heading_text and any('\u0900' <= ch <= '\u097F' for ch in heading_text):
                hindi = heading_text

            m_rom = re.search(r'[-*]*\s*\*\*Romanized:\*\*\s*(.*?)(?=\n|$)', b)
            if m_rom:
                rom = m_rom.group(1).strip().strip('()')

            m_ans = re.search(r'[-*]*\s*\*\*(?:English answer|Answer|Printed answer):\*\*\s*(.*?)(?=\n|$)', b)
            if m_ans:
                ans = m_ans.group(1).strip()

            if hindi and ans and lid not in [4, 5, 6]:
                cursor.execute(
                    "INSERT INTO sentence_practices (lesson_id, type, prompt_primary, prompt_secondary, english_answer, notes, is_favorite, mastery_score) VALUES (?, ?, ?, ?, ?, ?, 0, 0)",
                    (lid, "TRANSLATION", hindi, rom, ans, "")
                )
                lesson_count += 1
                total_sentences += 1

        # Pattern B: Numbered lists
        numbered_blocks = re.split(r'\n(?=\d+\.\s*\*\*)', content)
        for nb in numbered_blocks:
            m_num = re.match(r'^\d+\.\s*', nb)
            if not m_num:
                continue

            hindi = ""
            rom = ""
            ans = ""
            item_type = "TRANSLATION"

            m_hp = re.search(r'\*\*Hindi(?:\s*prompt)?:\*\*\s*(.*?)(?=\n|$)', nb)
            if m_hp:
                hindi = m_hp.group(1).strip()

            m_rom = re.search(r'\*\*Romanized:\*\*\s*(.*?)(?=\n|$)', nb)
            if m_rom:
                rom = m_rom.group(1).strip().strip('()')

            m_ans = re.search(r'\*\*(?:English answer|English|Answer|Printed answer):\*\*\s*(.*?)(?=\n|$)', nb)
            if m_ans:
                ans = m_ans.group(1).strip()

            # Active to Passive
            m_act = re.search(r'\*\*Active sentence:\*\*\s*(.*?)(?=\n|$)', nb)
            m_pas = re.search(r'\*\*Passive voice answer:\*\*\s*(.*?)(?=\n|$)', nb)
            if m_act and m_pas:
                hindi = m_act.group(1).strip()
                ans = m_pas.group(1).strip()
                rom = "Change into Passive Voice"
                item_type = "ACTIVE_PASSIVE"

            # Direct to Indirect
            m_dir = re.search(r'\*\*Direct speech:\*\*\s*(.*?)(?=\n|$)', nb)
            m_ind = re.search(r'\*\*Indirect speech:\*\*\s*(.*?)(?=\n|$)', nb)
            if m_dir and m_ind:
                hindi = m_dir.group(1).strip()
                ans = m_ind.group(1).strip()
                rom = "Change into Indirect Speech"
                item_type = "DIRECT_INDIRECT"

            # Sentence Correction
            m_orig = re.search(r'\*\*Original:\*\*\s*(.*?)(?=\n|$)', nb)
            m_cor = re.search(r'\*\*Correct sentence.*:\*\*\s*(.*?)(?=\n|$)', nb)
            if m_orig and m_cor:
                hindi = m_orig.group(1).strip()
                ans = m_cor.group(1).strip()
                rom = "Correct the sentence"
                item_type = "CORRECTION"

            # Homonyms
            m_hom = re.match(r'^\d+\.\s*(.*?)(?=\n\s*\*\*Answer:\*\*)', nb, re.DOTALL)
            m_hom_ans = re.search(r'\*\*Answer:\*\*\s*(.*?)(?=\n|$)', nb)
            if m_hom and m_hom_ans and lid == 31:
                hindi = m_hom.group(1).strip().replace('\n', ' ')
                ans = m_hom_ans.group(1).strip()
                rom = "Choose the correct homonym"
                item_type = "HOMONYM"

            # Special Cases
            m_bold_hindi = re.match(r'^\d+\.\s*\*\*(.*?)\*\*', nb)
            if m_bold_hindi and not hindi:
                h_candidate = m_bold_hindi.group(1).strip()
                if any('\u0900' <= ch <= '\u097F' for ch in h_candidate):
                    hindi = h_candidate
                    m_rom2 = re.search(r'Romanized:\s*(.*?)(?=\n|$)', nb)
                    if m_rom2:
                        rom = m_rom2.group(1).strip()
                    m_ans2 = re.search(r'\*\*Answer:\*\*\s*(.*?)(?=\n|$)', nb)
                    if m_ans2:
                        ans = m_ans2.group(1).strip()

            if hindi and ans and lid not in [4, 5, 6, 33, 34, 38]:
                cursor.execute(
                    "INSERT INTO sentence_practices (lesson_id, type, prompt_primary, prompt_secondary, english_answer, notes, is_favorite, mastery_score) VALUES (?, ?, ?, ?, ?, ?, 0, 0)",
                    (lid, item_type, hindi, rom, ans, "")
                )
                lesson_count += 1
                total_sentences += 1

        if lesson_count > 0:
            cursor.execute("UPDATE lessons SET total_items = total_items + ? WHERE id = ?", (lesson_count, lid))

    conn.commit()
    print(f"Total sentence practice items inserted: {total_sentences}")

def parse_vocab(z, conn):
    cursor = conn.cursor()
    total_vocab = 0

    # 1. Lesson 04: Verbs
    content = z.read('completed/Lesson_04.md').decode('utf-8', errors='ignore')
    blocks = re.findall(r'###\s*\d+\.\s*(.*?)\n\s*\*\*Hindi:\*\*\s*(.*?)\n\s*\*\*Answer:\*\*\s*(.*?)(?=\n###|\n##|\Z)', content, re.DOTALL)
    for b in blocks:
        word = b[0].strip()
        hindi = b[1].strip()
        ans = b[2].strip()
        parts = [p.strip() for p in re.split(r'–|-', ans)]
        v2 = parts[1] if len(parts) > 1 else ""
        v3 = parts[2] if len(parts) > 2 else ""
        cursor.execute(
            "INSERT INTO vocab_items (lesson_id, category, word, hindi_meaning, romanized, form2, form3, extra_info, is_favorite) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)",
            (4, "VERB", word, hindi, "", v2, v3, ans)
        )
        total_vocab += 1
    cursor.execute("UPDATE lessons SET total_items = total_items + ? WHERE id = ?", (len(blocks), 4))

    # 2. Lesson 05: Adjectives
    content = z.read('completed/Lesson_05.md').decode('utf-8', errors='ignore')
    blocks = re.findall(r'###\s*\d+\.\s*(.*?)\n\s*\*\*Hindi:\*\*\s*(.*?)\n\s*\*\*Answer:\*\*\s*(.*?)(?=\n###|\n##|\Z)', content, re.DOTALL)
    for b in blocks:
        word = b[0].strip()
        hindi = b[1].strip()
        ans = b[2].strip()
        parts = [p.strip() for p in re.split(r'–|-', ans)]
        comp = parts[1] if len(parts) > 1 else ""
        sup = parts[2] if len(parts) > 2 else ""
        cursor.execute(
            "INSERT INTO vocab_items (lesson_id, category, word, hindi_meaning, romanized, form2, form3, extra_info, is_favorite) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)",
            (5, "ADJECTIVE", word, hindi, "", comp, sup, ans)
        )
        total_vocab += 1
    cursor.execute("UPDATE lessons SET total_items = total_items + ? WHERE id = ?", (len(blocks), 5))

    # 3. Lesson 06: Adverbs
    content = z.read('completed/Lesson_06.md').decode('utf-8', errors='ignore')
    blocks = re.findall(r'###\s*\d+\.\s*(.*?)\n\s*\*\*Answer:\*\*\s*(.*?)(?=\n###|\n##|\Z)', content, re.DOTALL)
    for b in blocks:
        word = b[0].strip()
        ans = b[1].strip()
        parts = [p.strip() for p in re.split(r'–|-', ans)]
        comp = parts[1] if len(parts) > 1 else ""
        sup = parts[2] if len(parts) > 2 else ""
        cursor.execute(
            "INSERT INTO vocab_items (lesson_id, category, word, hindi_meaning, romanized, form2, form3, extra_info, is_favorite) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)",
            (6, "ADVERB", word, "", "", comp, sup, ans)
        )
        total_vocab += 1
    cursor.execute("UPDATE lessons SET total_items = total_items + ? WHERE id = ?", (len(blocks), 6))

    # 4. Lesson 33: Antonyms
    content = z.read('completed/Lesson_33.md').decode('utf-8', errors='ignore')
    ant_matches = re.finditer(
        r'\d+\.\s*\*\*(.*?)\*\*\s*—\s*Hindi:\s*(.*?);\s*Romanized:\s*(.*?)\n\s*\*\*Opposite:\*\*\s*\*\*(.*?)\*\*\s*—\s*Hindi:\s*(.*?);\s*Romanized:\s*(.*?)(?=\n\d+\.|\n##|\Z)',
        content, re.DOTALL
    )
    ant_count = 0
    for m in ant_matches:
        word = m.group(1).strip()
        h1 = m.group(2).strip()
        r1 = m.group(3).strip().strip('*')
        opp = m.group(4).strip()
        h2 = m.group(5).strip()
        r2 = m.group(6).strip().strip('*')
        cursor.execute(
            "INSERT INTO vocab_items (lesson_id, category, word, hindi_meaning, romanized, form2, form3, extra_info, is_favorite) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)",
            (33, "ANTONYM", word, h1, r1, opp, h2, f"{opp} ({h2})")
        )
        ant_count += 1
        total_vocab += 1
    cursor.execute("UPDATE lessons SET total_items = total_items + ? WHERE id = ?", (ant_count, 33))

    # 5. Lesson 34: Synonyms
    content = z.read('completed/Lesson_34.md').decode('utf-8', errors='ignore')
    syn_matches = re.finditer(
        r'\d+\.\s*\*\*(.*?)\*\*\s*—\s*Hindi:\s*(.*?);\s*Romanized:\s*(.*?)\n\s*\*\*Synonym:\*\*\s*\*\*(.*?)\*\*(.*?)(?=\n\d+\.|\n##|\Z)',
        content, re.DOTALL
    )
    syn_count = 0
    for m in syn_matches:
        word = m.group(1).strip()
        h1 = m.group(2).strip()
        r1 = m.group(3).strip().strip('*')
        syn = m.group(4).strip()
        extra = m.group(5).strip()
        cursor.execute(
            "INSERT INTO vocab_items (lesson_id, category, word, hindi_meaning, romanized, form2, form3, extra_info, is_favorite) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)",
            (34, "SYNONYM", word, h1, r1, syn, "", extra)
        )
        syn_count += 1
        total_vocab += 1
    cursor.execute("UPDATE lessons SET total_items = total_items + ? WHERE id = ?", (syn_count, 34))

    conn.commit()
    print(f"Total vocabulary items inserted: {total_vocab}")

def parse_mcqs(z, conn):
    cursor = conn.cursor()
    content = z.read('completed/Lesson_39.md').decode('utf-8', errors='ignore')
    quizzes = re.split(r'##\s*Quiz\s+(\d+)', content)
    total_mcqs = 0

    for i in range(1, len(quizzes), 2):
        quiz_num = int(quizzes[i])
        q_content = quizzes[i+1]

        ans_dict = {}
        ans_match = re.search(r'Answers?\s*\n(.*?)(?:```|\Z)', q_content, re.DOTALL | re.IGNORECASE)
        if ans_match:
            ans_text = ans_match.group(1).replace('\n', ' ')
            for item in re.finditer(r'(\d+)\s*([a-dA-D])', ans_text):
                ans_dict[int(item.group(1))] = item.group(2).lower()

        q_matches = re.finditer(r'Q\s*(\d+)\s*:(.*?)(?=(?:Q\s*\d+\s*:|Answers?|```|\Z))', q_content, re.DOTALL | re.IGNORECASE)
        for q in q_matches:
            q_num = int(q.group(1))
            q_text = q.group(2).strip()
            correct_ans = ans_dict.get(q_num, 'a')

            parts = re.split(r'\n\s*([a-d])\)', '\n' + q_text)
            prompt = parts[0].strip()
            options = {'a': '', 'b': '', 'c': '', 'd': ''}
            for j in range(1, len(parts), 2):
                opt_letter = parts[j].lower()
                options[opt_letter] = parts[j+1].strip()

            if options['a'] and options['b']:
                cursor.execute(
                    """INSERT INTO mcq_questions 
                    (lesson_id, quiz_num, question_num, prompt, option_a, option_b, option_c, option_d, correct_option, explanation, is_bookmarked, times_wrong) 
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0)""",
                    (39, quiz_num, q_num, prompt, options['a'], options['b'], options['c'], options['d'], correct_ans, "")
                )
                total_mcqs += 1

    cursor.execute("UPDATE lessons SET total_items = ? WHERE id = 39", (total_mcqs,))
    conn.commit()
    print(f"Total MCQs inserted for Lesson 39: {total_mcqs}")

def main():
    with zipfile.ZipFile(ZIP_FILE, 'r') as z:
        conn = create_database()
        parse_lesson_index(z, conn)
        parse_sentences(z, conn)
        parse_vocab(z, conn)
        parse_mcqs(z, conn)

        cursor = conn.cursor()
        cursor.execute("SELECT COUNT(*) FROM lessons")
        print(f"Total lessons in DB: {cursor.fetchone()[0]}")
        cursor.execute("SELECT COUNT(*) FROM sentence_practices")
        print(f"Total sentence practices in DB: {cursor.fetchone()[0]}")
        cursor.execute("SELECT COUNT(*) FROM vocab_items")
        print(f"Total vocab items in DB: {cursor.fetchone()[0]}")
        cursor.execute("SELECT COUNT(*) FROM mcq_questions")
        print(f"Total MCQs in DB: {cursor.fetchone()[0]}")

        # Check room_master_table
        cursor.execute("SELECT * FROM room_master_table")
        print(f"room_master_table row: {cursor.fetchone()}")

        conn.close()
        print(f"\nSuccessfully generated {OUTPUT_DB} with exact Room schema match (Size: {os.path.getsize(OUTPUT_DB)/1024:.1f} KB)")

if __name__ == '__main__':
    main()
