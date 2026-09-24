#!/usr/bin/env python3
"""Regenerates the STATUS / FLINCH_ONLY sets in src/pokemon/SecondaryKinds.java from Pokemon.secondaryEffect.
Usage (from the source root):  python3 tools/gen_secondary_kinds.py src/pokemon/Pokemon.java
Prints the two EnumSet.of(...) bodies to paste over the existing ones."""
import re, sys
src = open(sys.argv[1], encoding='utf-8', newline='').read().replace('\r', '')
i = src.index('private void secondaryEffect(Pokemon foe, Move move')
j = src.index('{', i); d = 0; k = j
while True:
    c = src[k]
    d += (c == '{') - (c == '}')
    if d == 0: break
    k += 1
groups, labels, body, inbody = [], [], [], False
for l in src[j + 1:k].split('\n'):
    s = l.strip(); m = re.match(r'case (\w+):', s)
    if m and not (inbody and l.startswith('\t\t\t')):
        if inbody: groups.append((labels, '\n'.join(body))); labels, body, inbody = [], [], False
        labels.append(m.group(1))
    elif s.startswith('default:'):
        if inbody: groups.append((labels, '\n'.join(body))); labels, body, inbody = [], [], False
    elif labels:
        inbody |= bool(s); body.append(l)
if labels: groups.append((labels, '\n'.join(body)))
pat = re.compile(r'foe\.(paralyze|burn|freeze|frostbite|poison|toxic|sleep|confuse|flinch)\(')
status, flinch = [], []
for lab, b in groups:
    ks = set(pat.findall(b))
    if ks: (flinch if ks == {'flinch'} else status).extend(lab)
for name, ms in (('STATUS', status), ('FLINCH_ONLY', flinch)):
    print(name + ':'); print(', '.join('Move.' + m for m in sorted(set(ms)))); print()
