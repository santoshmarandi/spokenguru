#!/usr/bin/env python3
import zipfile
import re
import json
import os

ZIP_PATH = 'Spoken_English_Guru_extraction_complete.zip'
ASSET_OUT_PATH = 'app/src/main/assets/lessons_seed.json'

def clean_text(text):
    if not text:
        return ""
    return re.sub(r'\s+', ' ', text).strip()

def clean_word(w):
    return re.sub(r"[^\w']", "", w).lower()

def extract_alts(raw_ans):
    if " / " in raw_ans:
        parts = [p.strip() for p in raw_ans.split(" / ") if p.strip()]
        return parts[0], parts[1:]
    return raw_ans.strip(), []

def generate_distractors(primary, alts, lesson_vocab):
    words_in_ans = set(clean_word(w) for w in primary.split() if clean_word(w))
    for a in alts:
        words_in_ans.update(clean_word(w) for w in a.split() if clean_word(w))
    
    distractors = []
    for cand in lesson_vocab:
        c_clean = clean_word(cand)
        if c_clean and c_clean not in words_in_ans and cand not in distractors:
            distractors.append(cand)
        if len(distractors) >= 4:
            break
            
    common_cands = [
        "is", "are", "was", "were", "do", "does", "did", "have", "has", "had",
        "will", "would", "can", "could", "should", "the", "a", "an", "this", "that",
        "these", "those", "in", "on", "at", "to", "for", "with", "not", "he", "she", "they"
    ]
    for cand in common_cands:
        if cand not in words_in_ans and cand not in distractors:
            distractors.append(cand)
        if len(distractors) >= 4:
            break
            
    return distractors[:4]

def get_lesson_hint_and_rule(lesson_num, lesson_title, hindi, ans):
    ans_lower = ans.lower()
    hindi_clean = hindi.strip()
    
    # Lesson 2: What is the time?
    if lesson_num == 2:
        if "past" in ans_lower:
            if "quarter past" in ans_lower:
                return (
                    "सवा (Quarter Past) का नियम",
                    "सवा (15 मिनट बीतने) के लिए 'quarter past' का प्रयोग होता है।\nसंरचना: quarter past + [Hour] (जैसे: quarter past 6 = सवा 6 बजे)।",
                    "सवा (15 मिनट बीतने) के लिए 'quarter past' का प्रयोग करें।"
                )
            elif "half past" in ans_lower:
                return (
                    "साढ़े (Half Past) का नियम",
                    "साढ़े (30 मिनट बीतने) के लिए 'half past' का प्रयोग होता है।\nसंरचना: half past + [Hour] (जैसे: half past 2 = ढाई बजे)।",
                    "साढ़े (30 मिनट बीतने) के लिए 'half past' का प्रयोग करें।"
                )
            else:
                return (
                    "बीत चुके मिनट (Past) का नियम",
                    "बजकर मिनट बीतने के लिए 'past' का प्रयोग होता है।\nसंरचना: [Minute] + past + [Hour] (जैसे: 5 past 7 = 7 बजकर 5 मिनट)।\n5 के गुणज (5, 10, 20) में 'minutes' शब्द नहीं लगाते।",
                    "बीत चुके मिनटों के लिए संरचना: [Minutes] + past + [Hour] का प्रयोग करें।"
                )
        elif "to" in ans_lower:
            if "quarter to" in ans_lower:
                return (
                    "पौने (Quarter To) का नियम",
                    "पौने (15 मिनट बाकी) के लिए 'quarter to' का प्रयोग होता है।\nसंरचना: quarter to + [Next Hour] (जैसे: quarter to 8 = पौने 8 बजे)।",
                    "पौने (15 मिनट बाकी) के लिए 'quarter to' का प्रयोग करें।"
                )
            else:
                return (
                    "बजने में बाकी मिनट (To) का नियम",
                    "बजने में बाकी मिनटों के लिए 'to' का प्रयोग होता है।\nसंरचना: [Remaining Minutes] + to + [Next Hour] (जैसे: 10 to 8 = 8 बजने में 10 मिनट)।",
                    "बजने में बाकी मिनटों के लिए संरचना: [Minutes] + to + [Hour] का प्रयोग करें।"
                )
        else:
            return (
                "समय बताने का सामान्य नियम",
                "अंग्रेजी में समय बताते समय पहले मिनट और फिर घंटा बोला जाता है (Minute + past/to + Hour)।\nजैसे: It's 10 past 2 (2 बजकर 10 मिनट)।",
                "समय बताने के नियम: बीत चुके मिनटों के लिए 'past' और बाकी मिनटों के लिए 'to' का प्रयोग करें।"
            )

    # Lesson 3: WH Family Words
    if lesson_num == 3:
        wh_word = "WH Word"
        for w in ["what", "where", "when", "why", "who", "which", "whose", "whom", "how"]:
            if w in ans_lower.split():
                wh_word = w.capitalize()
                break
        return (
            f"WH Family ({wh_word}) का प्रश्नवाचक नियम",
            f"WH Family प्रश्नवाचक संरचना:\n[WH Word] + [Helping Verb (is/are/do/does/did)] + [Subject] + [Main Verb]?\nजैसे: What is your name? / Where do you live?",
            "प्रश्नवाचक वाक्य में WH Word के तुरंत बाद Helping Verb (do/does/did/is/are) लगाएं।"
        )

    # Lesson 4: Three Forms of Verbs
    if lesson_num == 4:
        return (
            "क्रिया के तीन रूप (Forms of Verbs)",
            "क्रिया के तीन मुख्य रूप होते हैं:\nV1 = Base/Present form (वर्तमान काल)\nV2 = Simple Past form (भूतकाल)\nV3 = Past Participle (Perfect Tenses & Passive Voice में प्रयुक्त)।",
            "क्रिया के V1 (Present), V2 (Past) और V3 (Past Participle) रूपों को ध्यान से देखें।"
        )

    # Lesson 5: Three Degrees of Adjectives
    if lesson_num == 5:
        return (
            "विशेषण की अवस्थाएं (Degrees of Adjectives)",
            "Adjectives की 3 अवस्थाएं होती हैं:\n1. Positive: सामान्य गुण (Big, Small)\n2. Comparative: दो में तुलना (Bigger, Smaller / More...)\n3. Superlative: सर्वश्रेष्ठ गुण, इसके पहले 'the' लगता है (the Biggest, the Most...)।",
            "तुलना के लिए -er / more और सर्वश्रेष्ठ के लिए the + -est / most का प्रयोग करें।"
        )

    # Lesson 6 & 7: Degrees of Adverbs & Placement
    if lesson_num in [6, 7]:
        return (
            "क्रिया-विशेषण व स्थान (Adverb Placement)",
            "Adverbs क्रिया की विशेषता बताते हैं।\nनियम: Adverbs of Manner (deeply, quickly) आमतौर पर वाक्य या Object के अंत में आते हैं, जबकि Frequency Adverbs (often, always, never) मुख्य क्रिया से ठीक पहले आते हैं।",
            "Adverb का सही स्थान ध्यान रखें: Often/Always मुख्य क्रिया से पहले और Deeply/Well अंत में आते हैं।"
        )

    # Lesson 8: Simple Sentences
    if lesson_num == 8:
        return (
            "सरल वाक्य (No Action by Subject)",
            "जब कर्ता (Subject) कोई कार्य नहीं कर रहा हो, केवल उसकी स्थिति, गुण या अधिकार की बात हो:\n• Present: Subject + is/am/are/has/have + Object\n• Past: Subject + was/were/had + Object.",
            "सरल वाक्यों में कोई Action Verb नहीं होती, केवल अवस्था या अधिकार (is/are/has/have) बताया जाता है।"
        )

    # Lesson 9: Use of THERE
    if lesson_num == 9:
        return (
            "Use of THERE (अस्तित्व सूचक वाक्य)",
            "जब किसी स्थान पर किसी व्यक्ति या वस्तु की उपस्थिति या अस्तित्व बताया जाए, तो 'There' का प्रयोग Dummy Subject के रूप में होता है:\nसंरचना: There + is/are/was/were + [Noun] + [Place] (जैसे: There is a book on the table)।",
            "किसी चीज़ के कहीं होने या न होने की बात बताने के लिए वाक्य की शुरुआत 'There' से करें।"
        )

    # Lesson 10: All Tenses in English
    if lesson_num == 10:
        if "ing" in ans_lower:
            return (
                "Continuous Tense (प्रगतिशील काल)",
                "Continuous Tense में कार्य चल रहा होता है:\nसंरचना: [Subject] + is/am/are/was/were + [Verb + ing].\nजैसे: Rohit is going to school / He was playing.",
                "रहा है / रहा था के लिए Helping verb + Verb की 'ing' form का प्रयोग करें।"
            )
        elif any(w in ans_lower for w in ["has", "have", "had"]) and any(w in ans_lower for w in ["been", "done", "gone", "come", "seen", "given"]):
            return (
                "Perfect Tense (पूर्ण काल)",
                "Perfect Tense में कार्य समाप्त हो चुका होता है:\nसंरचना: [Subject] + has/have/had + [V3 (3rd form of Verb)].\nजैसे: I have finished / He had gone.",
                "चुका है / चुका था के लिए has/have/had के साथ हमेशा क्रिया की 3rd Form (V3) लगाएं।"
            )
        elif "did" in ans_lower or any(w.endswith("ed") for w in ans_lower.split()):
            return (
                "Past Indefinite Tense (भूतकाल)",
                "Past Indefinite Tense:\nसकारात्मक वाक्य में Verb की 2nd form (V2) आती है। नकारात्मक और प्रश्नवाचक वाक्यों में 'did + V1 (1st form)' का प्रयोग होता है।\nजैसे: I went / I didn't go.",
                "Past Indefinite में सकारात्मक में V2 और 'did' के साथ हमेशा V1 (1st form) का प्रयोग करें।"
            )
        elif "will" in ans_lower:
            return (
                "Future Tense (भविष्य काल)",
                "Future Indefinite Tense:\nभविष्य के कार्य के लिए 'will + V1' का प्रयोग होता है।\nसंरचना: [Subject] + will + [V1] (जैसे: I will help you)।",
                "गा / गे / गी के लिए 'will + V1' का प्रयोग करें।"
            )
        else:
            return (
                "Present Indefinite Tense (सामान्य वर्तमान)",
                "Present Indefinite Tense:\nदैनिक आदतों और तथ्यों के लिए V1 आता है। He/She/Singular के साथ V1+s/es और नकारात्मक/प्रश्नवाचक में 'do/does + V1' आता है।\nजैसे: Where do you go? / He speaks well.",
                "Present Indefinite में Singular Subject के साथ s/es और प्रश्नवाचक में do/does का ध्यान रखें।"
            )

    # Lessons 11-14: Modal Verbs (Can, Could, Should, Would)
    if lesson_num == 11:
        return (
            "Modal Verbs (Can, May, Must, Should)",
            "Modal Verbs के सामान्य नियम:\nModal verb (Can, Could, Should, Must) के तुरंत बाद हमेशा क्रिया का पहला रूप (V1) आता है।\nजैसे: I must go / He can swim.",
            "Modal Verb के बाद हमेशा मुख्य क्रिया की पहली अवस्था (V1) लगाएं।"
        )
    if lesson_num == 12:
        return (
            "Use of SHOULD / SHOULD HAVE",
            "Should = चाहिए (सलाह या कर्तव्य) -> Should + V1.\nShould have + V3 = चाहिए था (पछतावा या बीती सलाह) -> जैसे: You should have gone there (तुम्हें वहाँ जाना चाहिए था)।",
            "वर्तमान में सलाह के लिए 'should + V1' और बीते समय के लिए 'should have + V3' लगाएं।"
        )
    if lesson_num == 13:
        return (
            "Use of CAN / COULD",
            "Can = सकता है (वर्तमान क्षमता/अनुमति) -> Can + V1.\nCould = सका / सकता था (भूतकाल क्षमता) -> Could + V1.\nCould have + V3 = कर सकता था (पर नहीं किया)।",
            "क्षमता या योग्यता के लिए Can/Could + V1 और बीते समय में संभावना के लिए Could have + V3 लगाएं।"
        )
    if lesson_num == 14:
        return (
            "Use of WOULD",
            "Would का प्रयोग:\n1. अतीत में भविष्य की बात (He said he would come)\n2. अतीत की आदत (He would play daily)\n3. विनम्र निवेदन (Would you please help me?).",
            "अतीत में कहे गए भविष्य के वाक्यों में 'will' के स्थान पर 'would' का प्रयोग होता है।"
        )

    # Lesson 15: Conditionals
    if lesson_num == 15:
        return (
            "शर्त वाले वाक्य (Conditional Sentences)",
            "Conditional Sentences:\n• Type 1: If + Present Simple, will + V1 (संभावित भविष्य)\n• Type 2: If + Past Simple, would + V1 (काल्पनिक वर्तमान)\n• Type 3: If + Past Perfect, would have + V3 (असंभव भूतकाल)।\nनियम: 'If' वाले उपवाक्य में 'will' का प्रयोग कभी नहीं होता।",
            "शर्त (If) वाले भाग में कभी 'will' न लगाएं, दूसरे भाग में will/would का सही तालमेल रखें।"
        )

    # Lesson 16: Auxiliary / Helping Verbs
    if lesson_num == 16:
        return (
            "Helping Verbs (सहायक क्रियाएं)",
            "Subject-Verb Agreement:\n• Singular (He/She/It/Name): is, was, has, does\n• Plural (We/They/Plural): are, were, have, do\n• I: am, was, have, do\n• You: are, were, have, do.",
            "Subject के एकवचन (Singular) या बहुवचन (Plural) होने के अनुसार सही Helping Verb चुनें।"
        )

    # Lesson 17: Use of IT
    if lesson_num == 17:
        return (
            "Use of IT (समय, मौसम व प्राकृतिक घटनाएं)",
            "It का प्रयोग Dummy Subject के रूप में समय, मौसम, दिन, तारीख, दूरी और निर्जीव वस्तुओं के लिए किया जाता है:\nजैसे: It is 2 o'clock / It is raining / It is hot today.",
            "समय, मौसम, दिन या तापमान बताने वाले वाक्यों को 'It' से शुरू करें।"
        )

    # Lesson 18 & 19: Prepositions
    if lesson_num in [18, 19]:
        return (
            "Prepositions (सम्बन्धबोधक अव्यय)",
            "Prepositions के प्रमुख नियम:\n• From: किसी स्थान, समय या स्रोत से (From Delhi / From Monday)\n• In: किसी बंद या बड़े स्थान में • At: निश्चित बिंदु या छोटे स्थान पर\n• Since: निश्चित समय (Point of time) • For: समयावधि (Duration of time)\n• Into: गति के साथ अंदर प्रवेश (He jumped into the river)।",
            "दिशा, स्थान और समय के संदर्भ का ध्यान रखकर सही Preposition (from/to/in/at) चुनें।"
        )

    # Lesson 20: Conjunctions
    if lesson_num == 20:
        return (
            "Conjunctions (समुच्चयबोधक शब्द)",
            "Conjunctions दो उपवाक्यों को जोड़ते हैं:\n• As soon as = जैसे ही • The moment = जिस पल\n• Neither...nor = न यह...न वह • Either...or = या तो...या\n• Whether...or = चाहे...या • Although = यद्यपि / हालांकि।",
            "दो वाक्यों को जोड़ते समय सही Conjunction (as soon as, neither..nor) का प्रयोग करें।"
        )

    # Lesson 21 & 22: LET & Imperatives
    if lesson_num == 21:
        return (
            "Use of LET (अनुमति देना / करने देना)",
            "Let का अर्थ 'काम करने देना' है:\nसंरचना: [Subject] + let/lets/letting + [Object Pronoun (me/him/her/them)] + [V1 (1st form)].\nजैसे: He lets me play (वह मुझे खेलने देता है)।",
            "Let के बाद हमेशा Object Form (me, him, her, us) और Verb की 1st Form लगाएं।"
        )
    if lesson_num == 22:
        return (
            "आदेशात्मक वाक्य (Imperative Sentences)",
            "आदेश, अनुरोध या सलाह वाले वाक्य सीधे क्रिया की 1st form से शुरू होते हैं:\n• सकारात्मक: V1 + Object (जैसे: Come here / Speak English)\n• नकारात्मक: Don't + V1 + Object (जैसे: Don't go there).",
            "आदेश या अनुरोध में Subject (You) छिपा रहता है, वाक्य V1 या Don't + V1 से शुरू होता है।"
        )

    # Lesson 23: Causative Verbs
    if lesson_num == 23:
        return (
            "Causative Verbs (Make & Get)",
            "जब कर्ता खुद काम न करके किसी अन्य से करवाता है:\n• Make: दबाव या अधिकार से काम करवाना -> [Sub] + make + [Person] + [V1]\n• Get: मनाकर या साधन से काम करवाना -> [Sub] + get + [Thing] + [V3].",
            "किसी से काम करवाने में Make के बाद V1 और Get के बाद वस्तु + V3 का प्रयोग करें।"
        )

    # Lesson 24 & 25: Passive Voice
    if lesson_num in [24, 25]:
        return (
            "Passive Voice (कर्मवाच्य का नियम)",
            "Passive Voice में कर्म (Object) पर ज़ोर होता है:\n1. Object वाक्य के प्रारंभ में आता है।\n2. Tense के अनुसार Helping Verb लगती है।\n3. हमेशा Verb की 3rd Form (V3) का प्रयोग होता है!\n4. कर्ता से पहले 'by' लगता है (Object + H.V. + V3 + by Subject)।",
            "Passive Voice में हमेशा Verb की 3rd form (V3) का प्रयोग करें और Object को आगे रखें।"
        )

    # Lesson 26 & 27: Have, Having / Be, Being, Been
    if lesson_num in [26, 27]:
        return (
            "Use of Having / Being / Been",
            "Having + V3 = कोई कार्य करने के बाद (e.g. Having eaten food = खाना खाने के बाद)।\nBeing = किसी अवस्था में होना या Passive Continuous (is being done = किया जा रहा है)।\nBeen = Perfect Tense में प्रयुक्त (has been done = किया जा चुका है)।",
            "Having के साथ V3 और Being के साथ अवस्था या Passive Continuous का ध्यान रखें।"
        )

    # Lesson 28 & 29: WH + Ever / Ever & Never
    if lesson_num in [28, 29]:
        return (
            "WH + Ever / Ever & Never का नियम",
            "Whatever = जो कुछ भी • Whoever = जो कोई भी • Whenever = जब कभी भी • Wherever = जहाँ कहीं भी।\nEver = कभी (प्रश्नों में), Never = कभी नहीं (इसके साथ 'not' नहीं लगता)।",
            "Whatever/Whoever का प्रयोग संदर्भ के अनुसार करें। Never के साथ 'not' न लगाएं।"
        )

    # Lesson 30: Direct to Indirect Speech
    if lesson_num == 30:
        return (
            "Direct to Indirect Speech (अप्रत्यक्ष कथन)",
            "Indirect Speech के नियम:\n1. Inverted commas हटाकर 'that' लगाएं।\n2. 'Said to' को 'told' में बदलें।\n3. Reporting Verb Past में हो तो Tense में परिवर्तन होता है (Present Simple -> Past Simple)।\n4. Pronoun का सही परिवर्तन करें।",
            "Inverted Commas हटाकर 'that' लगाएं और सही Past Tense व Pronouns का प्रयोग करें।"
        )

    # Lesson 31 & 32: Homonyms & Question Tags
    if lesson_num == 31:
        return (
            "Homonyms (समोच्चरित भिन्नार्थक शब्द)",
            "समान उच्चारण वाले लेकिन भिन्न अर्थ और वर्तनी (spelling) वाले शब्द। वाक्य के संदर्भ और अर्थ को समझकर सही शब्द चुनें।",
            "शब्दों के अर्थ और संदर्भ को समझकर सटीक वर्तनी वाला शब्द चुनें।"
        )
    if lesson_num == 32:
        return (
            "Question Tags (पुछल्ले प्रश्न)",
            "Question Tag का नियम:\n• सकारात्मक वाक्य (Positive Statement) -> नकारात्मक Tag (Negative Tag) [He is good, isn't he?]\n• नकारात्मक वाक्य (Negative Statement) -> सकारात्मक Tag (Positive Tag) [He isn't good, is he?].",
            "सकारात्मक वाक्य के साथ 'isn't/aren't' और नकारात्मक वाक्य के साथ 'is/are' लगाएं।"
        )

    # Lessons 33 & 34: Antonyms & Synonyms
    if lesson_num in [33, 34]:
        return (
            "शब्दावली (Antonyms & Synonyms)",
            "Antonyms = विलोम शब्द (विपरीत अर्थ वाले शब्द)\nSynonyms = पर्यायवाची (समान अर्थ वाले शब्द)।\nशब्द के सही व्याकरणिक रूप (Noun, Verb, Adjective) का ध्यान रखें।",
            "दिए गए शब्द के सटीक अर्थ को समझकर उसका सही विलोम या पर्यायवाची चुनें।"
        )

    # Lesson 35 & 36: Special Cases & Mixed Practice
    if lesson_num in [35, 36]:
        return (
            "विशेष बोलचाल व मिश्रित अभ्यास",
            "दैनिक बोलचाल में प्रयुक्त विशिष्ट मुहावरेदार संरचनाएं।\nवाक्य के भाव, समय (Tense) और उचित Helping verb का तालमेल सही रखें।",
            "वाक्य के भाव को समझें और सही Tense व Helping Verb का प्रयोग करें।"
        )

    # Lesson 37: Sentence Correction
    if lesson_num == 37:
        return (
            "वाक्य शुद्धि (Sentence Correction Rules)",
            "सामान्य गलतियों से बचें:\n1. 'Did' के साथ हमेशा V1 आता है, V2 नहीं (Did he go? - सही, Did he went? - गलत)।\n2. Singular Subject के साथ Singular Verb लगती है (He doesn't know - सही, He don't know - गलत)।\n3. Double negative का प्रयोग न करें।",
            "Subject-Verb Agreement और 'did' के साथ V1 के नियम की जाँच करें।"
        )

    # Lesson 38: Singular to Plural
    if lesson_num == 38:
        return (
            "एकवचन से बहुवचन (Singular to Plural)",
            "Plural बनाने के नियम:\n• सामान्यतः: -s लगाएं (Book -> Books)\n• अंत में s, ss, sh, ch, x, o हो तो: -es लगाएं (Box -> Boxes, Mango -> Mangoes)\n• Consonant + y हो तो: y हटाकर -ies लगाएं (City -> Cities)\n• f / fe हो तो: -ves लगाएं (Knife -> Knives)\n• Irregular: Man -> Men, Tooth -> Teeth.",
            "अंतिम अक्षरों के नियम के अनुसार सही प्रत्यय (-s, -es, -ies, -ves) का प्रयोग करें।"
        )

    # Lessons 39 & 40: MCQs & Test Papers
    return (
        f"Lesson {lesson_num} अभ्यास नियम",
        f"व्याकरण नियम ({lesson_title}):\nवाक्य के Tense, Subject-Verb Agreement और उपयुक्त शब्द चयन के नियमों के अनुसार वाक्य का निर्माण करें।",
        "Tense, Helping Verb और सही शब्द संरचना का ध्यान रखें।"
    )

def main():
    if not os.path.exists(ZIP_PATH):
        raise FileNotFoundError(f"Zip file not found: {ZIP_PATH}")

    # Load existing Lesson 1 enriched questions to preserve rich glosses & rules
    existing_lesson1_qs = []
    if os.path.exists(ASSET_OUT_PATH):
        try:
            with open(ASSET_OUT_PATH, 'r', encoding='utf-8') as f:
                prev_data = json.load(f)
                existing_lesson1_qs = prev_data.get('lesson1Questions') or [q for q in prev_data.get('questions', []) if q.get('lessonNumber') == 1]
                print(f"Loaded {len(existing_lesson1_qs)} existing enriched Lesson 1 questions.")
        except Exception as e:
            print(f"Notice: Could not load previous seed data: {e}")

    with zipfile.ZipFile(ZIP_PATH) as z:
        index_md = z.read('lesson_index.md').decode('utf-8', errors='ignore')
        lesson_meta = {}
        for line in index_md.splitlines():
            m = re.match(r'\|\s*(\d+)\s*\|\s*([^|]+)\s*\|\s*([^|]+)\s*\|\s*([^|]+)\s*\|\s*([^|]+)\s*\|', line)
            if m:
                num = int(m.group(1))
                title = m.group(2).strip().replace('“', '"').replace('”', '"')
                pages = m.group(4).strip()
                p_start = int(pages.split('-')[0]) if '-' in pages else 1
                lesson_meta[num] = {"title": title, "page": p_start}

        all_lessons_data = []
        all_questions = []

        for lesson_num in range(1, 41):
            meta = lesson_meta.get(lesson_num, {"title": f"Lesson {lesson_num}", "page": 1})
            lesson_title = meta["title"]
            page_num = meta["page"]

            # If Lesson 1 and we have enriched questions, use them
            if lesson_num == 1 and existing_lesson1_qs:
                all_questions.extend(existing_lesson1_qs)
                all_lessons_data.append({
                    "lessonNumber": 1,
                    "title": lesson_title,
                    "pageNumber": page_num,
                    "totalQuestions": len(existing_lesson1_qs),
                    "isAvailable": True
                })
                print(f"Lesson 01 ({lesson_title[:30]}): Reused {len(existing_lesson1_qs)} enriched questions.")
                continue

            md_name = f'completed/Lesson_{lesson_num:02d}.md'
            try:
                md_content = z.read(md_name).decode('utf-8', errors='ignore')
            except KeyError:
                print(f"Warning: {md_name} not found in zip, skipping.")
                continue

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
                        if re.search(r'\*\*Hindi:\*\*', l):
                            hindi = re.sub(r'.*?\*\*Hindi:\*\*\s*', '', l).strip()
                        elif re.search(r'\*\*Romanized:\*\*', l):
                            roman = re.sub(r'.*?\*\*Romanized:\*\*\s*', '', l).strip()
                        elif re.search(r'\*\*Answer:\*\*', l):
                            ans = re.sub(r'.*?\*\*Answer:\*\*\s*', '', l).strip()
                    if ans:
                        raw_items.append((q_num, hindi, roman, ans))

            elif 4 <= lesson_num <= 17:
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
                        if re.search(r'\*\*Hindi:\*\*', l):
                            hindi = re.sub(r'.*?\*\*Hindi:\*\*\s*', '', l).strip()
                        elif re.search(r'\*\*Romanized:\*\*', l):
                            roman = re.sub(r'.*?\*\*Romanized:\*\*\s*', '', l).strip()
                        elif re.search(r'\*\*(?:English\s+answer|Answer|Printed\s+answer):\*\*', l):
                            ans = re.sub(r'.*?\*\*(?:English\s+answer|Answer|Printed\s+answer):\*\*\s*', '', l).strip()
                    if not hindi and header_text:
                        hindi = header_text
                    if ans:
                        raw_items.append((q_num, hindi, roman, ans))

            elif 18 <= lesson_num <= 37:
                pattern = re.compile(r'^\s*(\d+)\.\s+(.*?)(?=\n\s*\d+\.|\n##|\Z)', re.DOTALL | re.MULTILINE)
                for m in pattern.finditer(md_content):
                    q_num = int(m.group(1))
                    block = m.group(2)
                    lines = [l.strip() for l in block.splitlines() if l.strip()]
                    hindi, roman, ans = '', '', ''
                    
                    if lesson_num == 31: # Homonyms
                        first_line = lines[0] if lines else ''
                        hindi = first_line
                        for l in lines:
                            if re.search(r'\*\*Answer:\*\*', l):
                                ans = re.sub(r'.*?\*\*Answer:\*\*\s*', '', l).strip()
                    elif lesson_num == 32: # Question tags
                        for l in lines:
                            if re.search(r'\*\*Hindi:\*\*', l):
                                hindi = re.sub(r'.*?\*\*Hindi:\*\*\s*', '', l).strip()
                            elif re.search(r'\*\*Romanized:\*\*', l):
                                roman = re.sub(r'.*?\*\*Romanized:\*\*\s*', '', l).strip()
                            elif re.search(r'\*\*English:\*\*', l):
                                ans = re.sub(r'.*?\*\*English:\*\*\s*', '', l).strip()
                    elif lesson_num in [35, 36]:
                        first_line = lines[0] if lines else ''
                        hindi = re.sub(r'^\*\*(.*?)\*\*$', r'\1', first_line).strip()
                        for l in lines[1:]:
                            if re.search(r'Romanized:\s*', l):
                                roman = re.sub(r'.*?Romanized:\s*', '', l).strip()
                            elif re.search(r'\*\*Answer:\*\*', l):
                                ans = re.sub(r'.*?\*\*Answer:\*\*\s*', '', l).strip()
                    else:
                        for l in lines:
                            if re.search(r'\*\*Hindi(?:\s+prompt)?:\*\*', l):
                                hindi = re.sub(r'.*?\*\*Hindi(?:\s+prompt)?:\*\*\s*', '', l).strip()
                            elif re.search(r'\*\*Romanized:\*\*', l):
                                roman = re.sub(r'.*?\*\*Romanized:\*\*\s*', '', l).strip()
                            elif re.search(r'\*\*(?:English\s+answer|Answer|Printed\s+answer|Passive voice answer|Indirect speech|Opposite):\*\*', l):
                                ans = re.sub(r'.*?\*\*(?:English\s+answer|Answer|Printed\s+answer|Passive voice answer|Indirect speech|Opposite):\*\*\s*', '', l).strip()
                            elif re.search(r'\*\*Active sentence:\*\*', l):
                                hindi = re.sub(r'.*?\*\*Active sentence:\*\*\s*', '', l).strip()
                            elif re.search(r'\*\*Direct speech:\*\*', l):
                                hindi = re.sub(r'.*?\*\*Direct speech:\*\*\s*', '', l).strip()
                            elif re.search(r'\*\*Original:\*\*', l):
                                hindi = re.sub(r'.*?\*\*Original:\*\*\s*', '', l).strip()
                            elif re.search(r'\*\*Correct sentence(?:\s+\(printed key\))?:\*\*', l):
                                ans = re.sub(r'.*?\*\*Correct sentence(?:\s+\(printed key\))?:\*\*\s*', '', l).strip()
                    
                    if ans:
                        raw_items.append((q_num, hindi, roman, ans))

            elif lesson_num == 38: # Singular to Plural
                ans_sec = md_content.split('## Answers')
                if len(ans_sec) > 1:
                    ans_lines = ans_sec[1].splitlines()
                    for l in ans_lines:
                        m_ans = re.match(r'^\s*(\d+)\.\s+`?([^`=\n]+)=\s*([^`\n]+)`?', l)
                        if m_ans:
                            q_num = int(m_ans.group(1))
                            formula = m_ans.group(2).strip()
                            plural = m_ans.group(3).strip()
                            base_word = formula.split('+')[0].strip()
                            raw_items.append((q_num, f"Singular: {base_word}", f"Plural rule: {formula}", plural))

            elif lesson_num == 39: # 1500+ MCQs
                quiz_blocks = md_content.split('## Quiz ')
                global_q = 1
                for qb in quiz_blocks[1:]:
                    parts = qb.split('Answers')
                    if len(parts) < 2: continue
                    q_text = parts[0]
                    ans_text = parts[1]
                    ans_map = {}
                    for ak in re.findall(r'(\d+)\s*([a-d])', ans_text):
                        ans_map[int(ak[0])] = ak[1].lower()
                    
                    q_splits = re.split(r'Q\s*(\d+):', q_text)
                    for i in range(1, len(q_splits), 2):
                        local_q_num = int(q_splits[i])
                        body = q_splits[i+1]
                        opt_pattern = re.compile(r'([a-d])\)\s*(.*?)(?=\n[a-d]\)|\Z)', re.DOTALL)
                        options = {}
                        for om in opt_pattern.finditer(body):
                            options[om.group(1).lower()] = clean_text(om.group(2))
                        
                        first_opt = re.search(r'\n[a-d]\)', body)
                        prompt = body[:first_opt.start()].strip() if first_opt else body.strip()
                        prompt = clean_text(prompt)
                        
                        correct_opt = ans_map.get(local_q_num)
                        if correct_opt and correct_opt in options:
                            correct_ans = options[correct_opt]
                            choice_distractors = [v for k, v in options.items() if k != correct_opt and v]
                            raw_items.append((global_q, prompt, f"MCQ Option ({correct_opt.upper()})", correct_ans, choice_distractors))
                            global_q += 1

            elif lesson_num == 40: # Test papers
                test_blocks = md_content.split('## Test – ')
                global_tq = 1
                for tb in test_blocks[1:]:
                    parts = tb.split('Answers')
                    if len(parts) < 2: continue
                    q_part = parts[0]
                    ans_part = parts[1]
                    
                    ans_dict = {}
                    for am in re.finditer(r'^\s*(\d+)\.\s+(.*?)(?=\n\s*\d+\.|\Z)', ans_part, re.DOTALL | re.MULTILINE):
                        ans_dict[int(am.group(1))] = clean_text(am.group(2))
                    
                    for qm in re.finditer(r'^\s*(\d+)\.\s+(.*?)(?=\n\s*\d+\.|\Z|\n\s*Q\d+:)', q_part, re.DOTALL | re.MULTILINE):
                        t_num = int(qm.group(1))
                        raw_prompt = clean_text(qm.group(2))
                        if t_num in ans_dict:
                            ans = ans_dict[t_num]
                            m_hr = re.match(r'^(.*?)\s*\((.*?)\)$', raw_prompt)
                            if m_hr:
                                hindi = m_hr.group(1).strip()
                                roman = m_hr.group(2).strip()
                            else:
                                hindi = raw_prompt
                                roman = ""
                            raw_items.append((global_tq, hindi, roman, ans))
                            global_tq += 1

            # Vocabulary for distractors
            lesson_vocab = []
            for item in raw_items:
                ans_text = item[3]
                for w in ans_text.split():
                    cw = clean_word(w)
                    if len(cw) >= 3 and cw not in lesson_vocab:
                        lesson_vocab.append(cw)

            lesson_questions = []
            tag_name = lesson_title.split('—')[-1].split('–')[-1].strip()[:24]

            for item in raw_items:
                hindi = item[1]
                roman = item[2]
                raw_ans = item[3]
                predefined_distractors = item[4] if len(item) > 4 and isinstance(item[4], list) else None

                primary, alts = extract_alts(raw_ans)
                distractors = predefined_distractors if predefined_distractors else generate_distractors(primary, alts, lesson_vocab)

                hindi = clean_text(hindi)
                roman = clean_text(roman)
                primary = clean_text(primary)

                if not primary: continue

                rule_title, grammar_rule, hint_text = get_lesson_hint_and_rule(lesson_num, lesson_title, hindi, primary)

                q_obj = {
                    "questionNumber": len(lesson_questions) + 1,
                    "lessonNumber": lesson_num,
                    "hindiText": hindi if hindi else primary,
                    "romanText": roman,
                    "primaryAnswer": primary,
                    "alternateAnswers": alts,
                    "distractors": distractors,
                    "hint": hint_text,
                    "ruleTitle": rule_title,
                    "grammarRule": grammar_rule,
                    "tagKeyword": tag_name,
                    "categoryTag": f"Lesson {lesson_num}",
                    "wordGlosses": {}
                }
                lesson_questions.append(q_obj)

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
            "version": 4,
            "lessons": all_lessons_data,
            "questions": all_questions,
            "lesson1Questions": [q for q in all_questions if q.get("lessonNumber") == 1]
        }

        os.makedirs(os.path.dirname(ASSET_OUT_PATH), exist_ok=True)
        with open(ASSET_OUT_PATH, 'w', encoding='utf-8') as f:
            json.dump(final_data, f, ensure_ascii=False, indent=2)

        file_size_mb = os.path.getsize(ASSET_OUT_PATH) / (1024 * 1024)
        print(f"\nSUCCESS: Generated {ASSET_OUT_PATH}")
        print(f"Total Lessons: {len(all_lessons_data)} (all active)")
        print(f"Total Questions: {len(all_questions)}")
        print(f"Asset File Size: {file_size_mb:.2f} MB")

if __name__ == '__main__':
    main()
