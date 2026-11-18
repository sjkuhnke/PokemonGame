"""Exp level-curve sim using real base yields/groups. Self-contained.
Usage: python exp_sim2.py [folder containing trainers.csv and pokemon.csv]   (default: this script's folder)"""
import os, re, sys, random, statistics as st

def total(g, n):
    if n <= 1: return 0
    if g == 0: return n**3
    if g == 4: return 4*n**3//5
    if g == 5: return 5*n**3//4
    if g == 3: return max(0, 6*n**3//5 - 15*n*n + 100*n - 140)
    if g == 1:
        if n < 50: return n**3*(100-n)//50
        if n < 68: return n**3*(150-n)//100
        if n < 98: return n**3*((1911-10*n)//3)//500
        return n**3*(160-n)//100
    if n < 15: return n**3*((n+1)//3+24)//50
    if n < 36: return n**3*(n+14)//50
    return n**3*(n//2+32)//50
def to_next(g, l): return total(g, l+1) - total(g, l)

def load_species(path):           # row number = species ID; [-2] = base exp yield, [-3] = exp group
    sp = {}
    with open(path, encoding='utf-8') as f:
        for i, line in enumerate(l for l in f if l.strip()):
            c = [x.strip() for x in line.rstrip('\n').split('|')]
            sp[i+1] = (int(c[-2]), int(c[-3]))
    return sp

def load_trainers(path):
    rows = []
    with open(path, encoding='utf-8') as f:
        next(f)
        for line in f:
            line = line.rstrip('\n')
            if '<' not in line: continue
            i = line.index('<'); head = line[:i-1].split('|')
            team = [(int(a), int(b)) for a, b in re.findall(r'<\^*(\d+)\|(\d+)\|', line[i:])]
            rows.append(dict(index=int(re.sub(r'\D', '', head[0])), name=head[1], money=int(head[2]), team=team))
    return sorted(rows, key=lambda r: r['index'])

folder = sys.argv[1] if len(sys.argv) > 1 else os.path.dirname(os.path.abspath(__file__))
SP = load_species(os.path.join(folder, 'pokemon.csv'))
ROWS = load_trainers(os.path.join(folder, 'trainers.csv'))
GYMS = [r for r in ROWS if r['money'] == 500]
GYM_IDX = [g['index'] for g in GYMS]; GYM_LV = [max(l for _, l in g['team']) for g in GYMS]
def cap_for(idx):
    for gi, lv in zip(GYM_IDX, GYM_LV):
        if idx <= gi: return lv
    return 100

def gain(b, L, Lp, s, cap, cfg):
    nuzlocke = cfg.get('nuzlocke', False)
    scale = 1.0
    if not nuzlocke:
        scale = ((2 * L + 10) / (L + Lp + 10)) ** 2.5
    x = (b * L / 5 / s * scale + 1) * 1.5
    x *= cfg.get('k', 1.0)
    gap = cap - Lp - cfg.get('margin', 2)
    if not nuzlocke and cfg.get('catchup') and gap > 0:
        x *= 1 + min(cfg['maxb'], gap * cfg['step'])
    return max(1, int(x))

def run(cfg, scn, seed):
    random.seed(seed)
    groups = [SP[random.randint(1, len(SP))][1] for _ in range(6)]
    lv, ex, out = [5]*6, [0]*6, []
    for r in ROWS:
        cap = cap_for(r['index'])
        for sid, L in r['team']:
            part = random.sample(range(6), 2) if scn == 'pair' else list(range(6))
            for i in part:
                if lv[i] >= 100: continue
                ex[i] += gain(SP[sid][0], L, lv[i], len(part), cap, cfg)
                while lv[i] < 100 and ex[i] >= to_next(groups[i], lv[i]):
                    ex[i] -= to_next(groups[i], lv[i]); lv[i] += 1
        if r['index'] in GYM_IDX: out.append(st.mean(lv) - max(l for _, l in r['team']))
    return out

def report(name, cfg):
    res = [run(cfg, 'pair', s) for s in range(30)]
    print(f"{name:36s}", ' '.join(f"{st.mean(c):+5.1f}" for c in zip(*res)))

if __name__ == '__main__':
    print('mean team level minus gym ace, gyms 1-8 (30 runs, 2 participants/foe, all trainers fought):')
    report('baseline (real yields)', {})
    report('global x1.25', {'k': 1.25})
    report('global x1.5', {'k': 1.5})
    report('catch-up m2 s.15 max2', {'catchup': 1, 'margin': 2, 'step': .15, 'maxb': 2.0})
    report('catch-up m2 s.10 max1', {'catchup': 1, 'margin': 2, 'step': .10, 'maxb': 1.0})
    report('catch-up m1 s.20 max2', {'catchup': 1, 'margin': 1, 'step': .20, 'maxb': 2.0})
    report('x1.2 + catch-up m2 s.10 max1', {'k': 1.2, 'catchup': 1, 'margin': 2, 'step': .10, 'maxb': 1.0})
    print('\nGym ace levels:', GYM_LV)
    report('Nuzlocke baseline (no scaling)', {'nuzlocke': True})
    report('Nuzlocke x1.25 (no scaling)', {'nuzlocke': True,'k': 1.25})