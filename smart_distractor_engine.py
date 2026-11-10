import re
import hashlib

# Core English confusable groups ordered by pedagogical trap value
CONFUSION_MAP = {
    # Auxiliaries & Modals (Agreement + Tense traps)
    "is": ["are", "am", "was", "has"],
    "are": ["is", "were", "am", "have"],
    "am": ["is", "are", "was"],
    "was": ["were", "is", "had", "did"],
    "were": ["was", "are", "had"],
    "do": ["does", "did", "are"],
    "does": ["do", "did", "is"],
    "did": ["do", "does", "was", "had"],
    "has": ["have", "had", "is"],
    "have": ["has", "had", "are"],
    "had": ["have", "has", "did", "was"],
    "will": ["would", "shall", "did", "can"],
    "would": ["will", "could", "should"],
    "shall": ["will", "should", "would"],
    "should": ["must", "would", "could"],
    "can": ["could", "may", "will"],
    "could": ["can", "would", "should"],
    "may": ["might", "can", "must"],
    "might": ["may", "could", "would"],
    "must": ["should", "might", "have"],
    "need": ["needs", "needed", "must"],

    # Contractions & Negations
    "don't": ["doesn't", "didn't", "won't"],
    "doesn't": ["don't", "didn't", "isn't"],
    "didn't": ["don't", "doesn't", "wasn't"],
    "isn't": ["aren't", "wasn't", "doesn't"],
    "aren't": ["isn't", "weren't", "don't"],
    "wasn't": ["weren't", "didn't", "isn't"],
    "weren't": ["wasn't", "didn't", "aren't"],
    "hasn't": ["haven't", "hadn't", "didn't"],
    "haven't": ["hasn't", "hadn't", "don't"],
    "hadn't": ["hasn't", "haven't", "didn't"],
    "won't": ["wouldn't", "didn't", "don't"],
    "wouldn't": ["won't", "couldn't", "shouldn't"],
    "can't": ["couldn't", "won't", "shouldn't"],
    "couldn't": ["can't", "wouldn't", "shouldn't"],
    "shouldn't": ["mustn't", "couldn't", "wouldn't"],
    "it's": ["its", "that's", "this"],
    "its": ["it's", "his", "their"],
    "that's": ["this", "those", "it's"],
    "who's": ["whose", "whom", "which"],
    "you're": ["your", "you", "they're"],
    "they're": ["their", "there", "these"],

    # Demonstratives & Determiners & Quantifiers
    "this": ["that", "these", "it"],
    "that": ["this", "those", "which"],
    "these": ["those", "this", "they"],
    "those": ["these", "that", "they"],
    "a": ["an", "the"],
    "an": ["a", "the"],
    "the": ["a", "an", "this"],
    "some": ["any", "many", "much"],
    "any": ["some", "no", "much"],
    "many": ["much", "more", "few"],
    "much": ["many", "more", "very"],
    "few": ["little", "less", "many"],
    "little": ["few", "less", "much"],
    "more": ["most", "much", "many"],
    "most": ["more", "many", "very"],
    "each": ["every", "all"],
    "every": ["each", "all"],
    "all": ["every", "each", "both"],
    "both": ["either", "neither", "all"],
    "either": ["neither", "both", "any"],
    "neither": ["either", "none", "both"],

    # Pronouns & Case / Gender / Possessive Traps
    "i": ["me", "my", "we"],
    "me": ["I", "my", "him"],
    "my": ["mine", "me", "your"],
    "mine": ["my", "me", "yours"],
    "he": ["him", "his", "she"],
    "him": ["he", "his", "her"],
    "his": ["him", "he", "her"],
    "she": ["her", "hers", "he"],
    "her": ["she", "hers", "him"],
    "hers": ["her", "she", "his"],
    "we": ["us", "our", "they"],
    "us": ["we", "our", "them"],
    "our": ["ours", "us", "their"],
    "ours": ["our", "us", "theirs"],
    "you": ["your", "yours"],
    "your": ["you", "yours", "their"],
    "yours": ["your", "you", "theirs"],
    "they": ["them", "their", "those"],
    "them": ["they", "their", "us"],
    "their": ["there", "they", "them"],
    "theirs": ["their", "there", "ours"],
    "myself": ["me", "himself", "yourself"],
    "himself": ["him", "herself", "his"],
    "herself": ["her", "himself", "she"],
    "yourself": ["you", "yourselves", "myself"],
    "themselves": ["them", "their", "himself"],

    # WH Words
    "who": ["whom", "whose", "which"],
    "whom": ["who", "whose", "what"],
    "whose": ["who", "whom", "which"],
    "what": ["which", "how", "who"],
    "which": ["what", "who", "that"],
    "where": ["there", "when", "here"],
    "when": ["while", "where", "then"],
    "why": ["what", "how", "because"],
    "how": ["what", "why", "who"],

    # Prepositions, Conjunctions, Adverbs & Connectors
    "in": ["into", "on", "at", "inside"],
    "into": ["in", "to", "onto"],
    "on": ["at", "in", "upon", "over"],
    "at": ["on", "in", "by", "to"],
    "to": ["from", "for", "at"],
    "from": ["by", "since", "to", "of"],
    "for": ["since", "from", "to", "of"],
    "since": ["for", "from", "because"],
    "by": ["with", "from", "for"],
    "with": ["by", "from", "without"],
    "of": ["from", "off", "by", "for"],
    "off": ["of", "from", "out"],
    "about": ["above", "around", "for"],
    "between": ["among", "around", "within"],
    "among": ["between", "in", "around"],
    "under": ["below", "over", "beneath"],
    "below": ["under", "beneath", "above"],
    "over": ["above", "on", "across"],
    "above": ["over", "on", "below"],
    "across": ["through", "over", "along"],
    "through": ["across", "by", "into"],
    "behind": ["beside", "beyond", "before"],
    "beside": ["besides", "behind", "near"],
    "besides": ["beside", "except", "also"],
    "before": ["ago", "after", "earlier"],
    "after": ["later", "before", "since"],
    "ago": ["before", "since", "back"],
    "until": ["unless", "till", "while"],
    "till": ["until", "unless", "for"],
    "unless": ["until", "if", "though"],
    "if": ["unless", "whether", "when"],
    "whether": ["if", "that", "either"],
    "because": ["so", "since", "although"],
    "although": ["despite", "though", "but"],
    "though": ["although", "despite", "however"],
    "than": ["then", "that", "from"],
    "then": ["than", "when", "now"],
    "there": ["their", "here", "where"],
    "here": ["there", "near", "where"],
    "too": ["very", "so", "to"],
    "very": ["much", "too", "so"],
    "so": ["very", "too", "such"],
    "already": ["yet", "still", "just"],
    "still": ["yet", "already", "always"],
    "yet": ["still", "already", "now"],
    "never": ["ever", "not", "always"],
    "always": ["often", "usually", "never"],
    "past": ["to", "after", "before"],
    "quarter": ["half", "past"],
    "half": ["quarter", "full"],
    "now": ["then", "today", "soon"],
    "today": ["yesterday", "tomorrow", "now"],
    "yesterday": ["tomorrow", "today", "before"],
    "tomorrow": ["yesterday", "today", "later"],
    "being": ["been", "be", "was"],
    "been": ["being", "be", "was"],
    "said": ["told", "asked", "says"],
    "told": ["said", "asked", "tells"],
    "say": ["tell", "said", "ask"],
    "tell": ["say", "told", "ask"],
    "asked": ["told", "said", "asks"],
}

# Rich irregular + common verb family groups (V1, V2, V3, V1s, Ving)
VERB_FAMILIES = [
    ("go", "went", "gone", "goes", "going"),
    ("come", "came", "come", "comes", "coming"),
    ("eat", "ate", "eaten", "eats", "eating"),
    ("drink", "drank", "drunk", "drinks", "drinking"),
    ("see", "saw", "seen", "sees", "seeing"),
    ("look", "looked", "looked", "looks", "looking"),
    ("watch", "watched", "watched", "watches", "watching"),
    ("say", "said", "said", "says", "saying"),
    ("tell", "told", "told", "tells", "telling"),
    ("speak", "spoke", "spoken", "speaks", "speaking"),
    ("talk", "talked", "talked", "talks", "talking"),
    ("ask", "asked", "asked", "asks", "asking"),
    ("take", "took", "taken", "takes", "taking"),
    ("give", "gave", "given", "gives", "giving"),
    ("make", "made", "made", "makes", "making"),
    ("get", "got", "gotten", "gets", "getting"),
    ("know", "knew", "known", "knows", "knowing"),
    ("think", "thought", "thought", "thinks", "thinking"),
    ("write", "wrote", "written", "writes", "writing"),
    ("read", "read", "read", "reads", "reading"),
    ("buy", "bought", "bought", "buys", "buying"),
    ("bring", "brought", "brought", "brings", "bringing"),
    ("teach", "taught", "taught", "teaches", "teaching"),
    ("learn", "learned", "learnt", "learns", "learning"),
    ("catch", "caught", "caught", "catches", "catching"),
    ("run", "ran", "run", "runs", "running"),
    ("walk", "walked", "walked", "walks", "walking"),
    ("sit", "sat", "sat", "sits", "sitting"),
    ("stand", "stood", "stood", "stands", "standing"),
    ("sleep", "slept", "slept", "sleeps", "sleeping"),
    ("wake", "woke", "woken", "wakes", "waking"),
    ("sing", "sang", "sung", "sings", "singing"),
    ("swim", "swam", "swum", "swims", "swimming"),
    ("break", "broke", "broken", "breaks", "breaking"),
    ("choose", "chose", "chosen", "chooses", "choosing"),
    ("forget", "forgot", "forgotten", "forgets", "forgetting"),
    ("keep", "kept", "kept", "keeps", "keeping"),
    ("leave", "left", "left", "leaves", "leaving"),
    ("lose", "lost", "lost", "loses", "losing"),
    ("meet", "met", "met", "meets", "meeting"),
    ("pay", "paid", "paid", "pays", "paying"),
    ("send", "sent", "sent", "sends", "sending"),
    ("spend", "spent", "spent", "spends", "spending"),
    ("win", "won", "won", "wins", "winning"),
    ("begin", "began", "begun", "begins", "beginning"),
    ("drive", "drove", "driven", "drives", "driving"),
    ("ride", "rode", "ridden", "rides", "riding"),
    ("fly", "flew", "flown", "flies", "flying"),
    ("grow", "grew", "grown", "grows", "growing"),
    ("hide", "hid", "hidden", "hides", "hiding"),
    ("wear", "wore", "worn", "wears", "wearing"),
    ("steal", "stole", "stolen", "steals", "stealing"),
    ("play", "played", "played", "plays", "playing"),
    ("work", "worked", "worked", "works", "working"),
    ("study", "studied", "studied", "studies", "studying"),
    ("try", "tried", "tried", "tries", "trying"),
    ("live", "lived", "lived", "lives", "living"),
    ("love", "loved", "loved", "loves", "loving"),
    ("like", "liked", "liked", "likes", "liking"),
    ("want", "wanted", "wanted", "wants", "wanting"),
    ("need", "needed", "needed", "needs", "needing"),
    ("help", "helped", "helped", "helps", "helping"),
    ("call", "called", "called", "calls", "calling"),
    ("wait", "waited", "waited", "waits", "waiting"),
    ("hear", "heard", "heard", "hears", "hearing"),
    ("listen", "listened", "listened", "listens", "listening"),
    ("feel", "felt", "felt", "feels", "feeling"),
    ("find", "found", "found", "finds", "finding"),
    ("hold", "held", "held", "holds", "holding"),
    ("fall", "fell", "fallen", "falls", "falling"),
    ("throw", "threw", "thrown", "throws", "throwing"),
    ("draw", "drew", "drawn", "draws", "drawing"),
    ("show", "showed", "shown", "shows", "showing"),
    ("open", "opened", "opened", "opens", "opening"),
    ("close", "closed", "closed", "closes", "closing"),
    ("start", "started", "started", "starts", "starting"),
    ("stop", "stopped", "stopped", "stops", "stopping"),
    ("move", "moved", "moved", "moves", "moving"),
    ("stay", "stayed", "stayed", "stays", "staying"),
    ("understand", "understood", "understood", "understands", "understanding"),
    ("remember", "remembered", "remembered", "remembers", "remembering"),
    ("believe", "believed", "believed", "believes", "believing"),
    ("sell", "sold", "sold", "sells", "selling"),
    ("build", "built", "built", "builds", "building"),
    ("burn", "burnt", "burned", "burns", "burning"),
    ("clean", "cleaned", "cleaned", "clean", "cleaning"),
    ("cook", "cooked", "cooked", "cooks", "cooking"),
    ("wash", "washed", "washed", "washes", "washing"),
    ("rain", "rained", "rained", "rains", "raining"),
    ("shine", "shone", "shone", "shines", "shining"),
    ("stand", "stood", "stood", "stands", "standing"),
    ("lie", "lay", "lain", "lies", "lying"),
    ("die", "died", "died", "dies", "dying"),
    ("cry", "cried", "cried", "cries", "crying"),
    ("laugh", "laughed", "laughed", "laughs", "laughing"),
    ("smile", "smiled", "smiled", "smiles", "smiling"),
    ("become", "became", "become", "becomes", "becoming"),
    ("feel", "felt", "felt", "feels", "feeling"),
    ("keep", "kept", "kept", "keeps", "keeping"),
    ("sleep", "slept", "slept", "sleeps", "sleeping"),
]

VERB_LOOKUP = {}
for fam in VERB_FAMILIES:
    unique_forms = []
    for f in fam:
        if f not in unique_forms:
            unique_forms.append(f)
    for idx, form in enumerate(fam):
        # Store siblings ordered by how confusing they are relative to form's slot:
        # e.g. if form is V1 (idx=0) -> prioritize V2 (idx=1), V1s (idx=3), Ving (idx=4), V3 (idx=2)
        if idx == 0:
            order = [fam[1], fam[3], fam[4], fam[2]]
        elif idx == 1:
            order = [fam[0], fam[2], fam[3], fam[4]]
        elif idx == 2:
            order = [fam[1], fam[0], fam[4], fam[3]]
        elif idx == 3:
            order = [fam[0], fam[1], fam[4], fam[2]]
        else:
            order = [fam[0], fam[1], fam[3], fam[2]]
        VERB_LOOKUP.setdefault(form, [])
        for cand in order:
            if cand != form and cand not in VERB_LOOKUP[form]:
                VERB_LOOKUP[form].append(cand)

SINGULAR_PLURAL_NOUNS = {
    "boy": "boys", "boys": "boy",
    "girl": "girls", "girls": "girl",
    "man": "men", "men": "man",
    "woman": "women", "women": "woman",
    "child": "children", "children": "child",
    "person": "people", "people": "person",
    "friend": "friends", "friends": "friend",
    "student": "students", "students": "student",
    "teacher": "teachers", "teachers": "teacher",
    "book": "books", "books": "book",
    "pen": "pens", "pens": "pen",
    "day": "days", "days": "day",
    "year": "years", "years": "year",
    "hour": "hours", "hours": "hour",
    "minute": "minutes", "minutes": "minute",
    "brother": "brothers", "brothers": "brother",
    "sister": "sisters", "sisters": "sister",
    "apple": "apples", "apples": "apple",
    "car": "cars", "cars": "car",
    "house": "houses", "houses": "house",
    "dog": "dogs", "dogs": "dog",
    "bird": "birds", "birds": "bird",
    "tree": "trees", "trees": "tree",
    "foot": "feet", "feet": "foot",
    "tooth": "teeth", "teeth": "tooth",
}

CONTRACTIONS = {
    "i'm": "i am", "you're": "you are", "we're": "we are", "they're": "they are",
    "he's": "he is", "she's": "she is", "it's": "it is", "that's": "that is",
    "what's": "what is", "who's": "who is", "isn't": "is not", "aren't": "are not",
    "wasn't": "was not", "weren't": "were not", "don't": "do not", "doesn't": "does not",
    "didn't": "did not", "haven't": "have not", "hasn't": "has not", "hadn't": "had not",
    "won't": "will not", "wouldn't": "would not", "can't": "cannot", "couldn't": "could not",
    "shouldn't": "should not", "mustn't": "must not", "let's": "let us",
}


def _clean_tok(tok):
    return re.sub(r"^[.,!?;:\"“”‘’()]+|[.,!?;:\"“”‘’()]+$", "", tok)


def _norm_key(word):
    w = _clean_tok(word).replace("’", "'").replace("‘", "'").lower()
    return re.sub(r"[^a-z0-9']", "", w)


def _norm_sentence(text):
    w = text.lower().replace("’", "'").replace("‘", "'")
    w = re.sub(r"[^a-z0-9' ]", "", w)
    w = re.sub(r"\s+", " ", w).strip()
    return " ".join(CONTRACTIONS.get(t, t) if isinstance(CONTRACTIONS.get(t), str) else t for t in w.split())


def _match_case(candidate, source_word, at_start=False):
    if candidate.lower() == "i":
        return "I"
    if at_start or (source_word != "I" and source_word[:1].isupper()) or (source_word == "I" and at_start):
        return candidate[:1].upper() + candidate[1:]
    return candidate


def _build_builtin_valid_words():
    words = set(SINGULAR_PLURAL_NOUNS.keys())
    for k, vals in CONFUSION_MAP.items():
        words.add(_norm_key(k))
        for v in vals:
            words.add(_norm_key(v))
    for fam in VERB_FAMILIES:
        for f in fam:
            words.add(f.lower())
    return words


BUILTIN_VALID_WORDS = _build_builtin_valid_words()


def _morph_siblings(word_lower, valid_words=None):
    """Dictionary-validated morphological sibling generator for verbs and nouns."""
    out = []
    if len(word_lower) < 3:
        return out
    vocab = valid_words if valid_words is not None else BUILTIN_VALID_WORDS

    if word_lower in SINGULAR_PLURAL_NOUNS:
        out.append(SINGULAR_PLURAL_NOUNS[word_lower])
    if word_lower.endswith("’s") or word_lower.endswith("'s"):
        stem = re.sub(r"[’']s$", "", word_lower)
        if stem:
            for c in (stem + "s", stem):
                if c in vocab:
                    out.append(c)
    elif word_lower.endswith("ied") and len(word_lower) > 4:
        stem = word_lower[:-3] + "y"
        for c in (stem, stem + "ing", word_lower[:-3] + "ies"):
            if c in vocab:
                out.append(c)
    elif word_lower.endswith("ed") and len(word_lower) > 4:
        stem = word_lower[:-2]
        stem_e = word_lower[:-1]  # e.g. danced -> dance, persecuted -> persecute
        candidates = [stem_e, stem_e[:-1] + "ing" if stem_e.endswith("e") else stem + "ing", stem, stem + "s", stem_e + "s"]
        for c in candidates:
            if c in vocab and c != word_lower and c not in out:
                out.append(c)
    elif word_lower.endswith("ing") and len(word_lower) > 5:
        stem = word_lower[:-3]
        candidates = [stem, stem + "e", stem + "ed", stem + "d", stem + "s", stem + "es"]
        for c in candidates:
            if c in vocab and c != word_lower and c not in out:
                out.append(c)
    elif word_lower.endswith("ies") and len(word_lower) > 4:
        stem = word_lower[:-3] + "y"
        for c in (stem, word_lower[:-3] + "ied"):
            if c in vocab and c not in out:
                out.append(c)
    elif word_lower.endswith("es") and len(word_lower) > 4:
        for c in (word_lower[:-2], word_lower[:-1], word_lower[:-1] + "d", word_lower[:-2] + "ing"):
            if c in vocab and c != word_lower and c not in out:
                out.append(c)
    elif word_lower.endswith("s") and not word_lower.endswith("ss") and len(word_lower) > 3:
        stem = word_lower[:-1]
        if stem in vocab and stem not in out:
            out.append(stem)
    else:
        # Try adding s / es / ed / ing if known real word
        for c in (word_lower + "s", word_lower + "es", word_lower + "ed", word_lower + "d", word_lower + "ing"):
            if c in vocab and c != word_lower and c not in out:
                out.append(c)
    return out


def generate_smart_distractors(primary, alts=None, lesson_vocab=None, lesson_num=0, hindi_text="", known_words=None):
    """
    Generate 4 high-confusion, grammatically plausible distractors for Sentence Builder:
    - Never repeats any word present in primary or alternateAnswers.
    - Matches first-word capitalization on at least one confusable so slot #1 can't be trivially guessed.
    - Prioritizes verb tense/form traps, auxiliary agreement, pronoun case, prepositions, and question-tag/passive traps.
    """
    if alts is None:
        alts = []
    if lesson_vocab is None:
        lesson_vocab = []
    valid_words = known_words if known_words is not None else BUILTIN_VALID_WORDS

    raw_tokens = [_clean_tok(w) for w in primary.split() if _clean_tok(w)]
    if not raw_tokens:
        return ["is", "are", "was", "do"]

    forbidden = set()
    all_valid_norm = {_norm_sentence(primary)}
    for w in raw_tokens:
        forbidden.add(_norm_key(w))
    for alt in alts:
        all_valid_norm.add(_norm_sentence(alt))
        for w in alt.split():
            k = _norm_key(w)
            if k:
                forbidden.add(k)

    # Also expand contraction parts into forbidden only for exact match safety when substituting
    def is_safe_cand(cand_str):
        k = _norm_key(cand_str)
        if not k or k in forbidden:
            return False
        # Verify substituting any single token in primary with cand_str doesn't form a valid alternate
        for idx in range(len(raw_tokens)):
            test_tokens = raw_tokens[:idx] + [cand_str] + raw_tokens[idx + 1:]
            if _norm_sentence(" ".join(test_tokens)) in all_valid_norm:
                return False
        return True

    # Deterministic per-question seed hash so distractors vary naturally while remaining reproducible
    q_hash = int(hashlib.md5(primary.encode("utf-8")).hexdigest(), 16)

    scored_candidates = []  # (score, token_idx, cand_surface)

    tokens_lower = [_clean_tok(w).replace("’", "'").lower() for w in raw_tokens]
    has_did = any(t in ("did", "didn't") for t in tokens_lower)
    has_perfect = any(t in ("has", "have", "had", "hasn't", "haven't", "hadn't") for t in tokens_lower)
    is_passive = ("by" in tokens_lower and any(t in ("is", "are", "was", "were", "be", "been", "being") for t in tokens_lower)) or lesson_num == 24
    is_indirect = lesson_num == 30 or any(t in ("said", "told", "asked") for t in tokens_lower)

    for idx, tok in enumerate(raw_tokens):
        low = tokens_lower[idx]
        norm = _norm_key(tok)

        # 1. Direct Confusion Map (Highest priority on Auxiliaries, Pronouns, Preps, Demonstratives, WH)
        direct_cands = CONFUSION_MAP.get(low) or CONFUSION_MAP.get(norm) or []
        for rank, cand in enumerate(direct_cands):
            base_score = 94 - rank * 9
            if norm == "i":
                # Keep "I" pronoun confusion secondary unless sentence is tiny so prepositions/verbs get picked first
                base_score -= 10
            if idx == 0 and tok[:1].isupper() and norm != "i":
                # Slot-0 Capitalization trap bonus only on rank 0
                surf = _match_case(cand, tok)
                if rank == 0:
                    base_score += 12
            else:
                surf = cand if cand == "I" else cand.lower()

            # Boost Passive/Indirect/Time traps contextually
            if is_passive and norm in ("is", "was", "are", "were", "by", "been"):
                base_score += 12
            if is_indirect and norm in ("said", "told", "that", "if", "was", "would"):
                base_score += 12
            if lesson_num == 32 and norm in ("is", "isn't", "are", "aren't", "do", "don't", "does", "doesn't", "did", "didn't"):
                base_score += 14
            if idx > 0 and tok[:1].isupper() and norm != "i":
                continue
            scored_candidates.append((base_score, idx, surf))

        # 2. Irregular / Common Verb Family Traps
        verb_cands = VERB_LOOKUP.get(low) or VERB_LOOKUP.get(norm) or []
        for rank, cand in enumerate(verb_cands):
            base_score = 96 - rank * 8
            if has_did and rank == 0:
                base_score += 16
            if has_perfect and rank == 0:
                base_score += 14
            if is_passive:
                base_score += 10
            surf = _match_case(cand, tok) if idx == 0 else cand
            scored_candidates.append((base_score, idx, surf))

        # 3. Morphological Siblings (plural nouns / -ed / -ing / 's)
        for rank, cand in enumerate(_morph_siblings(low, valid_words)):
            surf = _match_case(cand, tok) if idx == 0 else cand
            scored_candidates.append((86 - rank * 6, idx, surf))

    # Contextual extra traps if indirect/passive/modal hints present
    if is_indirect and "that" not in tokens_lower:
        scored_candidates.append((84, 99, "that"))
    if is_passive and "been" not in tokens_lower:
        scored_candidates.append((80, 99, "been"))

    # Pass 1: Pick at most ONE best trap per distinct source word position (maximum variety across sentence!)
    scored_candidates.sort(key=lambda item: (-item[0], (q_hash ^ hash(item[2])) & 0xFFFF))

    chosen = []
    chosen_keys = set()
    used_token_indices = set()

    for _, t_idx, cand in scored_candidates:
        if t_idx in used_token_indices:
            continue
        ck = _norm_key(cand)
        if ck in chosen_keys:
            continue
        if is_safe_cand(cand):
            chosen.append(cand)
            chosen_keys.add(ck)
            if t_idx < 99:
                used_token_indices.add(t_idx)
        if len(chosen) >= 4:
            break

    # Pass 2: For short 2-3 word sentences, allow 2nd-best traps from already-used tokens (e.g. "I didn't go")
    if len(chosen) < 4:
        for _, _, cand in scored_candidates:
            ck = _norm_key(cand)
            if ck in chosen_keys:
                continue
            if is_safe_cand(cand):
                chosen.append(cand)
                chosen_keys.add(ck)
            if len(chosen) >= 4:
                break

    # Backfill from clean lowercase non-proper words in lesson_vocab (shuffled deterministically)
    if len(chosen) < 4 and lesson_vocab:
        # Rotate lesson_vocab by q_hash so every question gets different context words
        offset = q_hash % len(lesson_vocab)
        rotated = lesson_vocab[offset:] + lesson_vocab[:offset]
        for cand in rotated:
            if not cand or not cand.islower() or not cand.isalpha():
                continue
            ck = _norm_key(cand)
            if ck not in chosen_keys and is_safe_cand(cand):
                chosen.append(cand)
                chosen_keys.add(ck)
            if len(chosen) >= 4:
                break

    # Final varied grammar fillers (rotated by q_hash instead of static list!)
    fallback_pool = [
        "are", "were", "does", "did", "has", "have", "had", "would",
        "from", "since", "for", "with", "by", "this", "these", "that",
        "not", "their", "there", "been", "to", "into"
    ]
    if len(chosen) < 4:
        offset = q_hash % len(fallback_pool)
        rotated_fb = fallback_pool[offset:] + fallback_pool[:offset]
        for cand in rotated_fb:
            ck = _norm_key(cand)
            if ck not in chosen_keys and is_safe_cand(cand):
                chosen.append(cand)
                chosen_keys.add(ck)
            if len(chosen) >= 4:
                break

    return chosen[:4]


# Targeted grammar swap rules for realistic SENTENCE_CORRECTION distractors
_SC_SWAPS = [
    ("is", "are"), ("are", "is"), ("am", "is"),
    ("was", "were"), ("were", "was"),
    ("has", "have"), ("have", "has"),
    ("doesn't", "don't"), ("don't", "doesn't"), ("didn't", "doesn't"),
    ("isn't", "aren't"), ("aren't", "isn't"),
    ("doesn’t", "don’t"), ("don’t", "doesn’t"), ("didn’t", "doesn’t"),
    ("does", "do"), ("did", "does"),
    ("in", "on"), ("on", "in"), ("at", "in"),
    ("from", "since"), ("since", "for"), ("for", "since"),
    ("by", "with"), ("with", "by"),
    ("into", "in"),
    ("much", "many"), ("many", "much"),
    ("this", "these"), ("these", "this"), ("that", "those"),
    ("their", "there"), ("there", "their"),
    ("than", "then"),
    ("go", "goes"), ("goes", "go"), ("went", "go"),
    ("come", "comes"), ("came", "come"),
    ("eat", "eats"), ("ate", "eat"),
    ("play", "plays"), ("played", "play"),
    ("know", "knows"), ("make", "makes"), ("take", "takes"),
    ("saw", "seen"), ("seen", "saw"),
    ("gave", "given"), ("written", "wrote"), ("spoken", "spoke"),
    ("him", "his"), ("his", "him"), ("her", "hers"),
    ("what", "which"), ("which", "what"),
    ("a", "the"), ("the", "a"),
]


def _apply_sc_swap(sentence, src, dst):
    """Replace whole word preserving trailing punctuation and initial capitalization."""
    # Avoid replacing infinitive marker 'to do' -> 'to does' or subject 'I' at start with 'me'
    pattern = re.compile(r'(?<!to\s)\b' + re.escape(src) + r'\b', re.IGNORECASE)
    m = pattern.search(sentence)
    if not m:
        return None
    orig = m.group(0)
    rep = _match_case(dst, orig, at_start=(m.start() == 0))
    return sentence[:m.start()] + rep + sentence[m.end():]


def generate_sentence_correction_options(correct_primary, incorrect_orig, known_words=None):
    """
    Generate 4 complete, clean, grammatically subtle options for SENTENCE_CORRECTION.
    Fixes truncated slices and awkward 'not' insertions from older builder.
    """
    opts = [correct_primary.strip()]
    seen_norm = {_norm_sentence(correct_primary)}

    inc_clean = incorrect_orig.strip()
    if inc_clean and _norm_sentence(inc_clean) not in seen_norm:
        opts.append(inc_clean)
        seen_norm.add(_norm_sentence(inc_clean))

    # Generate plausible grammar mutations on correct_primary
    for src, dst in _SC_SWAPS:
        if len(opts) >= 4:
            break
        mutated = _apply_sc_swap(correct_primary, src, dst)
        if mutated and _norm_sentence(mutated) not in seen_norm:
            opts.append(mutated)
            seen_norm.add(_norm_sentence(mutated))

    # Handle imperative "Let him dance." -> "Let him to dance."
    if len(opts) < 4 and correct_primary.lower().startswith("let "):
        m_let = re.match(r'^(Let\s+\S+)\s+([a-z]+)(.*)$', correct_primary)
        if m_let:
            let_err = f"{m_let.group(1)} to {m_let.group(2)}{m_let.group(3)}"
            if _norm_sentence(let_err) not in seen_norm:
                opts.append(let_err)
                seen_norm.add(_norm_sentence(let_err))

    # Secondary morph-based verb/noun swap if still < 4
    if len(opts) < 4:
        words = correct_primary.split()
        for i, tok in enumerate(words):
            low = _clean_tok(tok).lower()
            cands = VERB_LOOKUP.get(low) or CONFUSION_MAP.get(low) or _morph_siblings(low, known_words)
            for c in cands:
                rep = tok.replace(_clean_tok(tok), _match_case(c, _clean_tok(tok)))
                cand_sent = " ".join(words[:i] + [rep] + words[i + 1:])
                if _norm_sentence(cand_sent) not in seen_norm:
                    opts.append(cand_sent)
                    seen_norm.add(_norm_sentence(cand_sent))
                    break
            if len(opts) >= 4:
                break

    # Safe fallback mutations across either correct or original sentence
    safe_helpers = [("will", "would"), ("can", "could"), ("is", "was"), ("are", "were"), ("to ", "for ")]
    for h1, h2 in safe_helpers:
        if len(opts) >= 4:
            break
        mutated = _apply_sc_swap(inc_clean or correct_primary, h1.strip(), h2.strip())
        if mutated and _norm_sentence(mutated) not in seen_norm:
            opts.append(mutated)
            seen_norm.add(_norm_sentence(mutated))

    return opts[:4]
