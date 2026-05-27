// faces.jsx — Hand-drawn feel emotion faces (5 levels)
// L1 매우 나쁨 → L5 매우 좋음. Soft round face with simple eyes + mouth.

const EMO = {
  1: { name: '매우 나쁨', color: '#7986CB', tint: '#E8EAF6', ink: '#3D4783' },
  2: { name: '나쁨',     color: '#9F8FCB', tint: '#EDE7F5', ink: '#574B85' },
  3: { name: '보통',     color: '#B0AAA3', tint: '#EFECE8', ink: '#5C544B' },
  4: { name: '좋음',     color: '#F2B66D', tint: '#FCEFDC', ink: '#8C5E1F' },
  5: { name: '매우 좋음', color: '#FF9B7A', tint: '#FFE7DC', ink: '#A14A2A' },
};

// One face. `level` 1..5, `size` px, `flat` = no fill (used inline w/ context).
function Face({ level = 3, size = 80, flat = false, accent = false }) {
  const e = EMO[level];
  const stroke = e.ink;
  const fill = flat ? 'transparent' : e.color;
  // Hand-drawn feel: very slight asymmetric eye positions
  return (
    <svg width={size} height={size} viewBox="0 0 100 100" style={{ display: 'block' }}>
      {/* face */}
      <circle cx="50" cy="50" r="44" fill={fill}
              stroke={flat ? stroke : 'rgba(0,0,0,0.06)'} strokeWidth={flat ? 2.6 : 1} />

      {/* per-level expression */}
      {level === 1 && (
        <g stroke={stroke} strokeWidth="3.4" strokeLinecap="round" fill="none">
          {/* sad slanted eyes */}
          <path d="M30 41 L41 47" />
          <path d="M70 41 L59 47" />
          {/* tear */}
          <path d="M37 53 q1 6 4 9" strokeWidth="2.6" />
          <circle cx="40.5" cy="64" r="2.6" fill={stroke} stroke="none" />
          {/* frown */}
          <path d="M36 74 q14 -10 28 0" />
        </g>
      )}

      {level === 2 && (
        <g stroke={stroke} strokeWidth="3.4" strokeLinecap="round" fill="none">
          {/* half-closed sad eyes */}
          <path d="M30 47 q5 -5 12 0" />
          <path d="M58 47 q5 -5 12 0" />
          {/* small frown */}
          <path d="M40 71 q10 -5 20 0" />
        </g>
      )}

      {level === 3 && (
        <g fill={stroke} stroke={stroke}>
          {/* dot eyes */}
          <circle cx="36" cy="46" r="3.4" />
          <circle cx="64.5" cy="46" r="3.4" />
          {/* flat mouth */}
          <path d="M38 68 L62 68" strokeWidth="3.4" strokeLinecap="round" fill="none" />
        </g>
      )}

      {level === 4 && (
        <g stroke={stroke} fill={stroke}>
          {/* round dot eyes */}
          <circle cx="36" cy="44" r="3.6" />
          <circle cx="64.5" cy="44" r="3.6" />
          {/* gentle smile */}
          <path d="M34 62 q16 14 32 0" strokeWidth="3.6" strokeLinecap="round" fill="none" />
        </g>
      )}

      {level === 5 && (
        <g stroke={stroke} strokeWidth="3.6" strokeLinecap="round" fill="none">
          {/* squinted happy eyes (crescents) */}
          <path d="M28 48 q8 -10 16 0" />
          <path d="M56 48 q8 -10 16 0" />
          {/* big smile */}
          <path d="M30 62 q20 22 40 0" />
          {/* cheek blush */}
          {accent && (
            <g fill="rgba(255,90,80,0.35)" stroke="none">
              <ellipse cx="25" cy="64" rx="5" ry="3" />
              <ellipse cx="75" cy="64" rx="5" ry="3" />
            </g>
          )}
        </g>
      )}
    </svg>
  );
}

// Compact face icon for list cards (no big colored disc — small disc, color halo).
function FaceChip({ level = 3, size = 40 }) {
  const e = EMO[level];
  return (
    <div style={{
      width: size, height: size, borderRadius: '50%',
      background: e.tint, display: 'grid', placeItems: 'center',
      border: `1px solid rgba(0,0,0,0.04)`,
      flex: '0 0 auto',
    }}>
      <Face level={level} size={size * 0.78} />
    </div>
  );
}

Object.assign(window, { Face, FaceChip, EMO });
