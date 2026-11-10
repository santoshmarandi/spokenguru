#!/usr/bin/env python3
import zipfile
import re
import json
import os
import random

from generate_complete_curriculum import (
    get_lesson_hint_and_rule,
    clean_text,
    clean_word,
    extract_alts
)
from smart_distractor_engine import (
    BUILTIN_VALID_WORDS,
    generate_smart_distractors,
    generate_sentence_correction_options,
    _morph_siblings
)

ZIP_PATH = 'Spoken_English_Guru_extraction_complete.zip'
ASSET_OUT_PATH = 'app/src/main/assets/lessons_seed.json'

COMMON_PREPOSITIONS = [
    "in", "on", "at", "to", "from", "for", "since", "by", "with", "into", "onto",
    "under", "over", "between", "among", "through", "across", "behind", "about",
    "against", "before", "after", "during", "towards", "without"
]

COMMON_MODALS = [
    "can", "could", "should", "would", "may", "might", "must", "ought to"
]

COMMON_CONJUNCTIONS = [
    "because", "although", "though", "unless", "until", "and", "but", "so", "or",
    "if", "while", "as", "since", "whether", "neither", "either"
]

def make_mcq_options(correct, all_candidates, count=4):
    options = [correct]
    seen = {correct.strip().lower()}
    for c in all_candidates:
        cl = c.strip().lower()
        if cl and cl not in seen and len(options) < count:
            options.append(c.strip())
            seen.add(cl)
    while len(options) < count:
        filler = "None of the above"
        if filler.lower() not in seen:
            options.append(filler)
            seen.add(filler.lower())
        else:
            options.append(f"Option {len(options)+1}")
    random.shuffle(options)
    return options

def main():
    print(f"Reading extraction zip: {ZIP_PATH}...")
    random.seed(42)

    with zipfile.ZipFile(ZIP_PATH, 'r') as z:
        lesson_meta = {}
        known_english_words = set(BUILTIN_VALID_WORDS)
        with open('app/src/main/assets/lessons_seed.json') as f:
            prev_data = json.load(f)
            for l in prev_data["lessons"]:
                lesson_meta[l["lessonNumber"]] = {
                    "title": l["title"],
                    "page": l["pageNumber"]
                }
            for pq in prev_data.get("questions", []):
                for w in (pq.get("primaryAnswer", "") + " " + " ".join(pq.get("alternateAnswers") or [])).split():
                    cw = clean_word(w)
                    if cw and cw.isalpha():
                        known_english_words.add(cw)

        all_lessons_data = []
        all_questions = []

        for lesson_num in range(1, 41):
            meta = lesson_meta.get(lesson_num, {"title": f"Lesson {lesson_num}", "page": 1})
            lesson_title = meta["title"]
            page_num = meta["page"]
            md_name = f'completed/Lesson_{lesson_num:02d}.md'

            try:
                md_content = z.read(md_name).decode('utf-8', errors='ignore')
            except KeyError:
                print(f"Warning: {md_name} not found in zip, skipping.")
                continue

            lesson_questions = []
            tag_name = lesson_title.split('—')[-1].split('–')[-1].strip()[:24]

            # =========================================================================
            # CLUSTER 1: VOCABULARY & PAIR MATCHING (Lessons 4, 5, 6, 31, 33, 34, 38)
            # =========================================================================
            if lesson_num == 4: # Three Forms of Verbs
                verb_pairs = []
                for s in md_content.split('### ')[1:]:
                    lines = [l.strip() for l in s.splitlines() if l.strip()]
                    if not lines: continue
                    m_num = re.match(r'^(\d+)\.\s*(.*)', lines[0])
                    if not m_num: continue
                    base = m_num.group(2).strip()
                    hindi = ""
                    ans = ""
                    for l in lines[1:]:
                        if '**Hindi:**' in l:
                            hindi = re.sub(r'.*?\*\*Hindi:\*\*\s*', '', l).strip()
                        elif '**Answer:**' in l:
                            ans = re.sub(r'.*?\*\*Answer:\*\*\s*', '', l).strip()
                    if base and ans:
                        parts = [p.strip() for p in re.split(r'[–-]', ans) if p.strip()]
                        v2 = parts[1] if len(parts) > 1 else ans
                        v3 = parts[2] if len(parts) > 2 else v2
                        verb_pairs.append({
                            "base": f"{base} ({hindi})" if hindi else base,
                            "base_pure": base,
                            "hindi": hindi,
                            "v2": v2,
                            "v3": v3
                        })

                # Generate pure WORD_MATCH (chunks of 4)
                q_idx = 1
                for i in range(0, len(verb_pairs), 4):
                    chunk = verb_pairs[i:i+4]
                    if len(chunk) >= 2:
                        match_map = {item["base"]: item["v2"] for item in chunk}
                        r_title, r_rule, r_hint = get_lesson_hint_and_rule(lesson_num, lesson_title, "Match verb forms", "verb forms")
                        lesson_questions.append({
                            "questionNumber": q_idx,
                            "lessonNumber": lesson_num,
                            "questionType": "WORD_MATCH",
                            "hindiText": "क्रिया के सही रूपों का मिलान करें (Match V1 with V2):",
                            "romanText": "Verb Forms Matching",
                            "primaryAnswer": ", ".join(f"{k} ↔ {v}" for k, v in match_map.items()),
                            "matchPairs": match_map,
                            "options": list(match_map.values()),
                            "hint": r_hint,
                            "ruleTitle": r_title,
                            "grammarRule": r_rule,
                            "tagKeyword": "Verb Conjugation",
                            "categoryTag": "Word Match"
                        })
                        q_idx += 1

            elif lesson_num in [5, 6]: # Adjectives & Adverbs Degrees
                deg_pairs = []
                for s in md_content.split('### ')[1:]:
                    lines = [l.strip() for l in s.splitlines() if l.strip()]
                    if not lines: continue
                    m_num = re.match(r'^(\d+)\.\s*(.*)', lines[0])
                    base = m_num.group(2).strip() if m_num else lines[0].strip()
                    hindi = ""
                    ans = ""
                    for l in lines[1:]:
                        if '**Hindi:**' in l:
                            hindi = re.sub(r'.*?\*\*Hindi:\*\*\s*', '', l).strip()
                        elif '**Answer:**' in l:
                            ans = re.sub(r'.*?\*\*Answer:\*\*\s*', '', l).strip()
                    if base and ans:
                        parts = [p.strip() for p in re.split(r'[–-]', ans) if p.strip()]
                        comp = parts[1].split('(')[0].strip() if len(parts) > 1 else ""
                        sup = parts[2].split('(')[0].strip() if len(parts) > 2 else ""
                        if comp:
                            deg_pairs.append({
                                "base": f"{base} ({hindi})" if hindi else base,
                                "base_pure": base,
                                "comp": comp,
                                "sup": sup
                            })

                q_idx = 1
                for i in range(0, len(deg_pairs), 4):
                    chunk = deg_pairs[i:i+4]
                    if len(chunk) >= 2:
                        match_map = {item["base"]: item["comp"] for item in chunk}
                        r_title, r_rule, r_hint = get_lesson_hint_and_rule(lesson_num, lesson_title, "Degrees", "comparative")
                        lesson_questions.append({
                            "questionNumber": q_idx,
                            "lessonNumber": lesson_num,
                            "questionType": "WORD_MATCH",
                            "hindiText": "Positive और Comparative Degrees का मिलान करें:",
                            "romanText": "Match Degrees of Adjective/Adverb",
                            "primaryAnswer": ", ".join(f"{k} ↔ {v}" for k, v in match_map.items()),
                            "matchPairs": match_map,
                            "options": list(match_map.values()),
                            "hint": r_hint,
                            "ruleTitle": r_title,
                            "grammarRule": r_rule,
                            "tagKeyword": "Degrees Match",
                            "categoryTag": "Word Match"
                        })
                        q_idx += 1

            elif lesson_num == 31: # Homonyms
                # Homonyms are fill in the blank with confusable homonyms + related forms
                pattern = re.compile(r'^\s*(\d+)\.\s+(.*?)(?=\n\s*\d+\.|\n##|\Z)', re.DOTALL | re.MULTILINE)
                homonym_entries = []
                homonym_word_pool = []
                for m in pattern.finditer(md_content):
                    block = m.group(2)
                    lines = [l.strip() for l in block.splitlines() if l.strip()]
                    first_line = lines[0] if lines else ''
                    ans = ''
                    for l in lines:
                        if re.search(r'\*\*Answer:\*\*', l):
                            ans = re.sub(r'.*?\*\*Answer:\*\*\s*', '', l).strip()
                    if ans and first_line:
                        m_br = re.search(r'\(([^)]+)\)', first_line)
                        choices = [c.strip() for c in m_br.group(1).split(',') if c.strip()] if m_br else [ans]
                        homonym_entries.append((first_line, ans, choices))
                        for c in choices:
                            if c not in homonym_word_pool:
                                homonym_word_pool.append(c)

                q_idx = 1
                for first_line, ans, choices in homonym_entries:
                    blank_sent = re.sub(r'…+|\.{3,}|\([^)]+\)', '____', first_line).strip()
                    blank_sent = re.sub(r'____\s*____', '____', blank_sent)
                    extra_cands = list(choices)
                    for c in choices:
                        for sib in _morph_siblings(c.lower(), known_english_words):
                            if sib not in extra_cands:
                                extra_cands.append(sib)
                    # Add phonetically/alphabetically near words from Lesson 31 homonym pool
                    near_pool = sorted(
                        [w for w in homonym_word_pool if w.lower() != ans.lower() and w not in extra_cands],
                        key=lambda w: (0 if w[:2].lower() == ans[:2].lower() else 1, abs(len(w) - len(ans)), w)
                    )
                    opts = make_mcq_options(ans, extra_cands + near_pool[:6], 4)
                    r_title, r_rule, r_hint = get_lesson_hint_and_rule(lesson_num, lesson_title, first_line, ans)
                    lesson_questions.append({
                        "questionNumber": q_idx,
                        "lessonNumber": lesson_num,
                        "questionType": "FILL_BLANK",
                        "hindiText": f"खाली स्थान में उपयुक्त समध्वनि शब्द (Homonym) भरें:\n{blank_sent}",
                        "romanText": "Choose appropriate homonym",
                        "primaryAnswer": ans,
                        "alternateAnswers": [blank_sent.replace('____', ans)],
                        "options": opts,
                        "distractors": [o for o in opts if o != ans],
                        "hint": r_hint,
                        "ruleTitle": r_title,
                        "grammarRule": r_rule,
                        "tagKeyword": "Homonyms",
                        "categoryTag": "Fill in the Blank"
                    })
                    q_idx += 1

            elif lesson_num in [33, 34]: # Antonyms & Synonyms
                word_pairs = []
                if lesson_num == 33: # Antonyms
                    for m in re.finditer(r"\d+\.\s+\*\*([^*]+)\*\*\s*—\s*Hindi:\s*([^;]+);[^\n]*\n\s*\*\*Opposite:\*\*\s*\*\*([^*]+)\*\*", md_content):
                        base_en = m.group(1).strip()
                        base_hi = m.group(2).strip()
                        opp_en = m.group(3).strip()
                        word_pairs.append({
                            "word": f"{base_en} ({base_hi})",
                            "pure": base_en,
                            "target": opp_en,
                            "type": "Opposite / Antonym"
                        })
                else: # Synonyms
                    for m in re.finditer(r"\d+\.\s+\*\*([^*]+)\*\*\s*(?:\(Hindi:\s*([^)]+)\))?\s*\n\s*\*\*Answer:\*\*\s*([^\n]+)", md_content):
                        base = m.group(1).strip()
                        hi = m.group(2).strip() if m.group(2) else ""
                        syn = m.group(3).split(',')[0].strip()
                        word_pairs.append({
                            "word": f"{base} ({hi})" if hi else base,
                            "pure": base,
                            "target": syn,
                            "type": "Synonym"
                        })

                q_idx = 1
                for i in range(0, len(word_pairs), 4):
                    chunk = word_pairs[i:i+4]
                    if len(chunk) >= 2:
                        match_map = {item["word"]: item["target"] for item in chunk}
                        r_title, r_rule, r_hint = get_lesson_hint_and_rule(lesson_num, lesson_title, "Pairs", "matching")
                        label = "विलोम शब्द (Antonyms)" if lesson_num == 33 else "समानार्थी शब्द (Synonyms)"
                        lesson_questions.append({
                            "questionNumber": q_idx,
                            "lessonNumber": lesson_num,
                            "questionType": "WORD_MATCH",
                            "hindiText": f"सही {label} का मिलान करें (Match the pairs):",
                            "romanText": f"Match {label}",
                            "primaryAnswer": ", ".join(f"{k} ↔ {v}" for k, v in match_map.items()),
                            "matchPairs": match_map,
                            "options": list(match_map.values()),
                            "hint": r_hint,
                            "ruleTitle": r_title,
                            "grammarRule": r_rule,
                            "tagKeyword": "Word Match",
                            "categoryTag": "Pair Matching"
                        })
                        q_idx += 1

            elif lesson_num == 38: # Singular to Plural
                pl_pairs = []
                ans_sec = md_content.split('## Answers')
                if len(ans_sec) > 1:
                    for l in ans_sec[1].splitlines():
                        m_ans = re.match(r'^\s*\d+\.\s+`?([^`=\n]+)=\s*([^`\n]+)`?', l)
                        if m_ans:
                            formula = m_ans.group(1).strip()
                            plural = m_ans.group(2).strip()
                            base = formula.split('+')[0].strip()
                            pl_pairs.append({
                                "base": base,
                                "plural": plural,
                                "rule": formula
                            })

                q_idx = 1
                for i in range(0, len(pl_pairs), 4):
                    chunk = pl_pairs[i:i+4]
                    if len(chunk) >= 2:
                        match_map = {item["base"]: item["plural"] for item in chunk}
                        r_title, r_rule, r_hint = get_lesson_hint_and_rule(lesson_num, lesson_title, "Plural", "plurals")
                        lesson_questions.append({
                            "questionNumber": q_idx,
                            "lessonNumber": lesson_num,
                            "questionType": "WORD_MATCH",
                            "hindiText": "Singular और Plural शब्दों का सही मिलान करें:",
                            "romanText": "Match Singular to Plural",
                            "primaryAnswer": ", ".join(f"{k} ↔ {v}" for k, v in match_map.items()),
                            "matchPairs": match_map,
                            "options": list(match_map.values()),
                            "hint": r_hint,
                            "ruleTitle": r_title,
                            "grammarRule": r_rule,
                            "tagKeyword": "Singular Plural",
                            "categoryTag": "Word Match"
                        })
                        q_idx += 1

            # =========================================================================
            # CLUSTER 5: SENTENCE CORRECTION (Lesson 37)
            # =========================================================================
            elif lesson_num == 37:
                pattern = re.compile(r'^\s*(\d+)\.\s+(.*?)(?=\n\s*\d+\.|\n##|\Z)', re.DOTALL | re.MULTILINE)
                q_idx = 1
                for m in pattern.finditer(md_content):
                    block = m.group(2)
                    lines = [l.strip() for l in block.splitlines() if l.strip()]
                    incorrect = ""
                    correct = ""
                    for l in lines:
                        if re.search(r'\*\*Original:\*\*', l):
                            incorrect = re.sub(r'.*?\*\*Original:\*\*\s*', '', l).strip()
                        elif re.search(r'\*\*Correct sentence(?:\s+\(printed key\))?:\*\*', l):
                            correct = re.sub(r'.*?\*\*Correct sentence(?:\s+\(printed key\))?:\*\*\s*', '', l).strip()
                    if not incorrect and lines:
                        incorrect = lines[0]
                    if not correct:
                        for l in lines:
                            if '**Answer:**' in l or 'Answer:' in l:
                                correct = re.sub(r'.*?Answer:\s*', '', l).strip()
                    if incorrect and correct and incorrect != correct:
                        primary_corr, alt_corrs = extract_alts(clean_text(correct))
                        inc_clean = clean_text(incorrect)
                        r_title, r_rule, r_hint = get_lesson_hint_and_rule(lesson_num, lesson_title, inc_clean, primary_corr)
                        opts = generate_sentence_correction_options(primary_corr, inc_clean, known_words=known_english_words)
                        random.shuffle(opts)

                        lesson_questions.append({
                            "questionNumber": q_idx,
                            "lessonNumber": lesson_num,
                            "questionType": "SENTENCE_CORRECTION",
                            "hindiText": f"अशुद्ध वाक्य को पहचानें और सही विकल्प चुनें:\n\"{inc_clean}\"",
                            "romanText": "Spot the error and select the correct sentence",
                            "primaryAnswer": primary_corr,
                            "alternateAnswers": alt_corrs,
                            "options": opts,
                            "hint": r_hint,
                            "ruleTitle": r_title,
                            "grammarRule": r_rule,
                            "tagKeyword": "Sentence Correction",
                            "categoryTag": "Error Spotting"
                        })
                        q_idx += 1

            # =========================================================================
            # CLUSTER 6: 1500+ MCQS & TEST PAPERS (Lessons 39, 40)
            # =========================================================================
            elif lesson_num == 39:
                quiz_blocks = md_content.split('## Quiz ')
                q_idx = 1
                for qb in quiz_blocks[1:]:
                    parts = qb.split('Answers')
                    if len(parts) < 2: continue
                    q_text, ans_text = parts[0], parts[1]
                    ans_map = {int(ak[0]): ak[1].lower() for ak in re.findall(r'(\d+)\s*([a-d])', ans_text)}
                    q_splits = re.split(r'Q\s*(\d+):', q_text)
                    for i in range(1, len(q_splits), 2):
                        local_q_num = int(q_splits[i])
                        body = q_splits[i+1]
                        opt_pattern = re.compile(r'([a-d])\)\s*(.*?)(?=\n[a-d]\)|\Z)', re.DOTALL)
                        options = {om.group(1).lower(): clean_text(om.group(2)) for om in opt_pattern.finditer(body)}
                        first_opt = re.search(r'\n[a-d]\)', body)
                        prompt = body[:first_opt.start()].strip() if first_opt else body.strip()
                        prompt = clean_text(prompt)
                        correct_opt = ans_map.get(local_q_num)
                        if correct_opt and correct_opt in options:
                            correct_ans = options[correct_opt]
                            choice_list = list(options.values())
                            r_title, r_rule, r_hint = get_lesson_hint_and_rule(lesson_num, lesson_title, prompt, correct_ans)
                            lesson_questions.append({
                                "questionNumber": q_idx,
                                "lessonNumber": lesson_num,
                                "questionType": "MCQ",
                                "hindiText": prompt,
                                "romanText": f"Rapid MCQ Q{q_idx}",
                                "primaryAnswer": correct_ans,
                                "options": choice_list,
                                "hint": r_hint,
                                "ruleTitle": r_title,
                                "grammarRule": r_rule,
                                "tagKeyword": "1500+ MCQ",
                                "categoryTag": "Multiple Choice"
                            })
                            q_idx += 1

            elif lesson_num == 40: # Test papers
                test_blocks = md_content.split('## Test – ')
                raw_test_items = []
                for tb in test_blocks[1:]:
                    parts = tb.split('Answers')
                    if len(parts) < 2: continue
                    q_part, ans_part = parts[0], parts[1]
                    ans_dict = {int(am.group(1)): clean_text(am.group(2)) for am in re.finditer(r'^\s*(\d+)\.\s+(.*?)(?=\n\s*\d+\.|\Z)', ans_part, re.DOTALL | re.MULTILINE)}
                    for qm in re.finditer(r'^\s*(\d+)\.\s+(.*?)(?=\n\s*\d+\.|\Z|\n\s*Q\d+:)', q_part, re.DOTALL | re.MULTILINE):
                        t_num = int(qm.group(1))
                        raw_prompt = clean_text(qm.group(2))
                        if t_num in ans_dict:
                            ans = ans_dict[t_num]
                            m_hr = re.match(r'^(.*?)\s*\((.*?)\)$', raw_prompt)
                            hindi = m_hr.group(1).strip() if m_hr else raw_prompt
                            roman = m_hr.group(2).strip() if m_hr else ""
                            primary, alts = extract_alts(ans)
                            if primary:
                                raw_test_items.append((hindi, roman, primary, alts))

                l40_vocab = []
                for _, _, primary, _ in raw_test_items:
                    for w in primary.split():
                        cw = clean_word(w)
                        if len(cw) >= 3 and cw not in l40_vocab:
                            l40_vocab.append(cw)

                q_idx = 1
                for hindi, roman, primary, alts in raw_test_items:
                    r_title, r_rule, r_hint = get_lesson_hint_and_rule(lesson_num, lesson_title, hindi, primary)
                    distractors = generate_smart_distractors(primary, alts, l40_vocab, lesson_num=lesson_num, hindi_text=hindi, known_words=known_english_words)
                    lesson_questions.append({
                        "questionNumber": q_idx,
                        "lessonNumber": lesson_num,
                        "questionType": "SENTENCE_BUILDER",
                        "hindiText": hindi,
                        "romanText": roman,
                        "primaryAnswer": primary,
                        "alternateAnswers": alts,
                        "distractors": distractors,
                        "options": [],
                        "hint": r_hint,
                        "ruleTitle": r_title,
                        "grammarRule": r_rule,
                        "tagKeyword": "Test Paper",
                        "categoryTag": "Sentence Builder"
                    })
                    q_idx += 1

            # =========================================================================
            # ALL OTHER LESSONS: CLOZE / FILL_BLANK, TRANSFORMATION, OR SENTENCE BUILDER
            # =========================================================================
            else:
                # Parse raw translation items
                raw_items = []
                if lesson_num in [1, 2, 3]:
                    sections = md_content.split('### ')
                    for s in sections[1:]:
                        lines = [l.strip() for l in s.splitlines() if l.strip()]
                        if not lines: continue
                        m_num = re.match(r'^(\d+)', lines[0])
                        if not m_num: continue
                        q_num = int(m_num.group(1))
                        hindi, roman, ans = '', '', ''
                        for l in lines[1:]:
                            if '**Hindi:**' in l:
                                hindi = re.sub(r'.*?\*\*Hindi:\*\*\s*', '', l).strip()
                            elif '**Romanized:**' in l:
                                roman = re.sub(r'.*?\*\*Romanized:\*\*\s*', '', l).strip()
                            elif '**Answer:**' in l:
                                ans = re.sub(r'.*?\*\*Answer:\*\*\s*', '', l).strip()
                        if ans:
                            raw_items.append((q_num, hindi, roman, ans))

                elif 7 <= lesson_num <= 17:
                    sections = md_content.split('### ')
                    for s in sections[1:]:
                        lines = [l.strip() for l in s.splitlines() if l.strip()]
                        if not lines: continue
                        m_num = re.match(r'^(\d+)\.\s*(.*)', lines[0])
                        if not m_num: continue
                        q_num = int(m_num.group(1))
                        header_text = m_num.group(2).strip()
                        hindi, roman, ans = '', '', ''
                        for l in lines[1:]:
                            if '**Hindi:**' in l:
                                hindi = re.sub(r'.*?\*\*Hindi:\*\*\s*', '', l).strip()
                            elif '**Romanized:**' in l:
                                roman = re.sub(r'.*?\*\*Romanized:\*\*\s*', '', l).strip()
                            elif re.search(r'\*\*(?:English\s+answer|Answer|Printed\s+answer):\*\*', l):
                                ans = re.sub(r'.*?\*\*(?:English\s+answer|Answer|Printed\s+answer):\*\*\s*', '', l).strip()
                        if not hindi and header_text:
                            hindi = header_text
                        if ans:
                            raw_items.append((q_num, hindi, roman, ans))

                elif 18 <= lesson_num <= 36:
                    pattern = re.compile(r'^\s*(\d+)\.\s+(.*?)(?=\n\s*\d+\.|\n##|\Z)', re.DOTALL | re.MULTILINE)
                    for m in pattern.finditer(md_content):
                        q_num = int(m.group(1))
                        block = m.group(2)
                        lines = [l.strip() for l in block.splitlines() if l.strip()]
                        hindi, roman, ans = '', '', ''
                        if lesson_num == 32: # Question tags
                            for l in lines:
                                if '**Hindi:**' in l:
                                    hindi = re.sub(r'.*?\*\*Hindi:\*\*\s*', '', l).strip()
                                elif '**Romanized:**' in l:
                                    roman = re.sub(r'.*?\*\*Romanized:\*\*\s*', '', l).strip()
                                elif '**English:**' in l:
                                    ans = re.sub(r'.*?\*\*English:\*\*\s*', '', l).strip()
                        elif lesson_num in [35, 36]:
                            first_line = lines[0] if lines else ''
                            hindi = re.sub(r'^\*\*(.*?)\*\*$', r'\1', first_line).strip()
                            for l in lines[1:]:
                                if 'Romanized:' in l:
                                    roman = re.sub(r'.*?Romanized:\s*', '', l).strip()
                                elif '**Answer:**' in l:
                                    ans = re.sub(r'.*?\*\*Answer:\*\*\s*', '', l).strip()
                        else:
                            for l in lines:
                                if re.search(r'\*\*Hindi(?:\s+prompt)?:\*\*', l):
                                    hindi = re.sub(r'.*?\*\*Hindi(?:\s+prompt)?:\*\*\s*', '', l).strip()
                                elif re.search(r'\*\*Romanized:\*\*', l):
                                    roman = re.sub(r'.*?\*\*Romanized:\*\*\s*', '', l).strip()
                                elif re.search(r'\*\*(?:English\s+answer|Answer|Printed\s+answer|Passive voice answer|Indirect speech):\*\*', l):
                                    ans = re.sub(r'.*?\*\*(?:English\s+answer|Answer|Printed\s+answer|Passive voice answer|Indirect speech):\*\*\s*', '', l).strip()
                                elif re.search(r'\*\*Active sentence:\*\*', l):
                                    hindi = f"Change to Passive: {re.sub(r'.*?\*\*Active sentence:\*\*\s*', '', l).strip()}"
                                elif re.search(r'\*\*Direct speech:\*\*', l):
                                    hindi = f"Change to Indirect: {re.sub(r'.*?\*\*Direct speech:\*\*\s*', '', l).strip()}"
                        if ans:
                            raw_items.append((q_num, hindi, roman, ans))

                # Vocabulary for distractors
                lesson_vocab = []
                for item in raw_items:
                    for w in item[3].split():
                        cw = clean_word(w)
                        if len(cw) >= 3 and cw not in lesson_vocab:
                            lesson_vocab.append(cw)

                q_idx = 1
                for item in raw_items:
                    hindi = clean_text(item[1])
                    roman = clean_text(item[2])
                    raw_ans = clean_text(item[3])
                    primary, alts = extract_alts(raw_ans)
                    if not primary: continue

                    rule_title, grammar_rule, hint_text = get_lesson_hint_and_rule(lesson_num, lesson_title, hindi, primary)
                    # All sentence exercises are SENTENCE_BUILDER with smart distractors
                    distractors = generate_smart_distractors(primary, alts, lesson_vocab, lesson_num=lesson_num, hindi_text=hindi, known_words=known_english_words)
                    lesson_questions.append({
                        "questionNumber": q_idx,
                        "lessonNumber": lesson_num,
                        "questionType": "SENTENCE_BUILDER",
                        "hindiText": hindi if hindi else primary,
                        "romanText": roman,
                        "primaryAnswer": primary,
                        "alternateAnswers": alts,
                        "distractors": distractors,
                        "options": [],
                        "hint": hint_text,
                        "ruleTitle": rule_title,
                        "grammarRule": grammar_rule,
                        "tagKeyword": tag_name,
                        "categoryTag": "Sentence Builder"
                    })
                    q_idx += 1

            all_questions.extend(lesson_questions)
            all_lessons_data.append({
                "lessonNumber": lesson_num,
                "title": lesson_title,
                "pageNumber": page_num,
                "totalQuestions": len(lesson_questions),
                "isAvailable": len(lesson_questions) > 0
            })
            print(f"Lesson {lesson_num:02d} ({lesson_title[:30]}): {len(lesson_questions)} questions generated.")

        final_data = {
            "version": 6,
            "lessons": all_lessons_data,
            "questions": all_questions,
            "lesson1Questions": [q for q in all_questions if q.get("lessonNumber") == 1]
        }

        with open(ASSET_OUT_PATH, 'w', encoding='utf-8') as f:
            json.dump(final_data, f, ensure_ascii=False, indent=2)

        file_size_mb = os.path.getsize(ASSET_OUT_PATH) / (1024 * 1024)
        print(f"\n==========================================")
        print(f"SUCCESS: Generated Multi-Format {ASSET_OUT_PATH}")
        print(f"Total Lessons: {len(all_lessons_data)} (all 40 active)")
        print(f"Total Questions: {len(all_questions)}")
        print(f"Asset File Size: {file_size_mb:.2f} MB")

        # Breakdown by Question Type
        counts = {}
        for q in all_questions:
            qt = q.get("questionType", "UNKNOWN")
            counts[qt] = counts.get(qt, 0) + 1
        print("Question Format Breakdown:")
        for k, v in sorted(counts.items()):
            print(f"  - {k}: {v} questions")

if __name__ == '__main__':
    main()
