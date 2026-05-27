// widgets.jsx — 3 emotion input widgets: Dial / Reel / Gacha

// ─────────────────────────────────────────────────────────────────────
// A. Wheel — 5 emotion faces orbiting a large center preview.
//    User rotates the orbit; the face at the top under the indicator
//    fills the center as the current selection.
// ─────────────────────────────────────────────────────────────────────
function DialWidget({ selected, size = 300 }) {
  const angles = [0, 72, 144, 216, 288];          // top, going clockwise
  const rOuter = size / 2;
  const rRingCenter = rOuter - 44;                 // orbit radius
  const tokenR = 30;                               // base ring token radius
  const rot = selected ? -angles[selected - 1] : 0;
  const centerLevel = selected || null;

  return (
    <div style={{ position: 'relative', width: size, height: size, margin: '0 auto' }}>
      {/* Soft halo backdrop — no heavy texture */}
      <div style={{
        position: 'absolute', inset: -10, borderRadius: '50%',
        background:
          'radial-gradient(circle at 50% 45%, rgba(255,225,205,0.55) 0%, rgba(255,225,205,0.18) 45%, rgba(255,225,205,0) 70%)',
      }} />

      {/* Top indicator — small notch above the orbit */}
      <div style={{
        position: 'absolute', left: '50%', top: -14, transform: 'translateX(-50%)',
        display: 'flex', flexDirection: 'column', alignItems: 'center',
        zIndex: 4, pointerEvents: 'none',
      }}>
        <svg width="20" height="12" viewBox="0 0 20 12">
          <path d="M2 2 L10 10 L18 2" stroke="#E07856" strokeWidth="2.6"
                fill="none" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
      </div>

      {/* Dotted orbit guide — visual ring without heavy chrome */}
      <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`}
           style={{ position: 'absolute', inset: 0, pointerEvents: 'none' }}>
        <circle cx={rOuter} cy={rOuter} r={rRingCenter}
                fill="none" stroke="rgba(62,44,35,0.10)"
                strokeWidth="1" strokeDasharray="2 6" />
      </svg>

      {/* Rotating ring — counter-rotates each token to keep faces upright */}
      <div style={{
        position: 'absolute', inset: 0,
        transform: `rotate(${rot}deg)`,
        transition: 'transform 700ms cubic-bezier(.2,.9,.3,1.05)',
      }}>
        {angles.map((deg, i) => {
          const rad = (deg - 90) * Math.PI / 180;
          const cx = rOuter + Math.cos(rad) * rRingCenter;
          const cy = rOuter + Math.sin(rad) * rRingCenter;
          const isSel = selected === i + 1;
          const r = isSel ? tokenR + 6 : tokenR;
          const e = EMO[i + 1];
          return (
            <div key={i} style={{
              position: 'absolute',
              left: cx - r, top: cy - r,
              width: r * 2, height: r * 2,
              borderRadius: '50%',
              background: e.tint,
              boxShadow: isSel
                ? `0 0 0 3px #fff, 0 0 0 5px ${e.color}, 0 10px 22px ${e.color}55`
                : '0 2px 6px rgba(62,44,35,0.08), inset 0 0 0 1px rgba(0,0,0,0.04)',
              display: 'grid', placeItems: 'center',
              transform: `rotate(${-rot}deg)`,
              transition: 'all 600ms cubic-bezier(.2,.9,.3,1.05)',
              zIndex: isSel ? 3 : 2,
            }}>
              <Face level={i + 1} size={r * 1.55} />
            </div>
          );
        })}
      </div>

      {/* Center preview — large, swaps to match selection */}
      <div style={{
        position: 'absolute', left: '50%', top: '50%',
        width: 132, height: 132, marginLeft: -66, marginTop: -66,
        borderRadius: '50%',
        background: centerLevel ? EMO[centerLevel].tint : '#FFFBF6',
        boxShadow:
          '0 10px 30px rgba(62,44,35,0.10), ' +
          'inset 0 1px 0 rgba(255,255,255,0.9), ' +
          'inset 0 0 0 1px rgba(62,44,35,0.06)',
        display: 'grid', placeItems: 'center',
        transition: 'background 350ms ease',
        zIndex: 1,
      }}>
        {centerLevel ? (
          <CenterFaceMorph level={centerLevel} />
        ) : (
          <div style={{
            display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 6,
          }}>
            <span style={{
              fontSize: 13, fontWeight: 700, color: 'rgba(62,44,35,0.55)',
              letterSpacing: '-0.01em',
            }}>지금 기분</span>
            <span style={{
              fontSize: 28, fontWeight: 800, color: 'rgba(62,44,35,0.25)',
              fontFamily: 'Inter',
            }}>?</span>
          </div>
        )}
      </div>
    </div>
  );
}

// Subtle pop animation on level change for the center preview.
function CenterFaceMorph({ level }) {
  return (
    <div key={level} style={{
      display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 6,
      animation: 'faceMorph 380ms cubic-bezier(.2,.9,.3,1.4)',
    }}>
      <Face level={level} size={88} accent />
      <span style={{
        fontSize: 13, fontWeight: 700, color: EMO[level].ink, letterSpacing: '-0.01em',
      }}>{EMO[level].name}</span>
      <style>{`
        @keyframes faceMorph {
          0%   { transform: scale(0.85); opacity: 0; }
          60%  { transform: scale(1.05); opacity: 1; }
          100% { transform: scale(1); }
        }
      `}</style>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────
// B. Scroll Reel — slot-machine-like vertical scroll
//   Center face large, prev/next peek above/below. Motion trail uses
//   semi-translucent ghost copies + arrows.
// ─────────────────────────────────────────────────────────────────────
function ReelWidget({ center = 3, motion = true }) {
  const items = [center - 1, center, center + 1].filter(n => n >= 1 && n <= 5);
  // pad with empties if at edges
  const prev = center > 1 ? center - 1 : null;
  const next = center < 5 ? center + 1 : null;

  return (
    <div style={{ position: 'relative', width: 220, margin: '0 auto' }}>
      {/* center highlight band */}
      <div style={{
        position: 'absolute', left: -12, right: -12, top: 130, height: 160,
        borderRadius: 28,
        background: 'linear-gradient(180deg, rgba(255,138,101,0.06), rgba(255,138,101,0.12))',
        border: '1.5px solid rgba(255,138,101,0.35)',
      }} />

      <div style={{ position: 'relative', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 10 }}>
        {/* prev (small, dimmed) */}
        <div style={{
          height: 78, display: 'grid', placeItems: 'center', opacity: prev ? 0.35 : 0,
          transform: 'translateY(8px) scale(0.78)',
          filter: motion ? 'blur(1.2px)' : 'none',
        }}>
          {prev && <Face level={prev} size={70} />}
        </div>

        {/* center large */}
        <div style={{ position: 'relative', height: 160, display: 'grid', placeItems: 'center' }}>
          {motion && (
            // ghost trails behind center for motion blur feel
            <div style={{ position: 'absolute', inset: 0, display: 'grid', placeItems: 'center' }}>
              <div style={{ position: 'absolute', opacity: 0.18, transform: 'translateY(-22px) scale(0.92)' }}>
                <Face level={center} size={140} />
              </div>
              <div style={{ position: 'absolute', opacity: 0.18, transform: 'translateY(22px) scale(0.92)' }}>
                <Face level={center} size={140} />
              </div>
            </div>
          )}
          <Face level={center} size={140} accent />
        </div>

        {/* next (small, dimmed) */}
        <div style={{
          height: 78, display: 'grid', placeItems: 'center', opacity: next ? 0.35 : 0,
          transform: 'translateY(-8px) scale(0.78)',
          filter: motion ? 'blur(1.2px)' : 'none',
        }}>
          {next && <Face level={next} size={70} />}
        </div>
      </div>

      {/* up/down chevrons */}
      <ChevronCol direction="up"   top={62}  motion={motion} />
      <ChevronCol direction="down" bottom={62} motion={motion} />
    </div>
  );
}

function ChevronCol({ direction, top, bottom, motion }) {
  const isUp = direction === 'up';
  return (
    <div style={{
      position: 'absolute', left: '50%', transform: 'translateX(-50%)',
      top, bottom,
      display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2,
      pointerEvents: 'none',
    }}>
      {[0, 1, 2].map(i => (
        <svg key={i} width="20" height="10" viewBox="0 0 20 10"
             style={{
               opacity: motion ? (isUp ? 0.18 + i * 0.18 : 0.54 - i * 0.18) : 0.4,
               transform: isUp ? 'rotate(180deg)' : 'none',
             }}>
          <path d="M2 2 L10 8 L18 2" stroke="#E07856" strokeWidth="2.4" fill="none" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
      ))}
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────
// C. Gacha Machine
//   Before: capsule machine with lever, capsules inside.
//   After: capsule fallen out at base, opened with face revealed.
// ─────────────────────────────────────────────────────────────────────
function GachaMachine({ state = 'before', resultLevel = 4 }) {
  // body: 220 x 320 frame
  return (
    <div style={{ position: 'relative', width: 240, margin: '0 auto' }}>
      {/* Dome */}
      <svg width="240" height="380" viewBox="0 0 240 380" style={{ display: 'block' }}>
        <defs>
          <linearGradient id="domeG" x1="0" x2="0" y1="0" y2="1">
            <stop offset="0" stopColor="#FFF5EB" stopOpacity="0.95" />
            <stop offset="1" stopColor="#FFE3D4" stopOpacity="0.85" />
          </linearGradient>
          <linearGradient id="baseG" x1="0" x2="0" y1="0" y2="1">
            <stop offset="0" stopColor="#E07856" />
            <stop offset="1" stopColor="#B95536" />
          </linearGradient>
          <radialGradient id="domeShine" cx="0.3" cy="0.25" r="0.6">
            <stop offset="0" stopColor="#fff" stopOpacity="0.8" />
            <stop offset="1" stopColor="#fff" stopOpacity="0" />
          </radialGradient>
        </defs>

        {/* Glass dome */}
        <path d="M40 40 q80 -50 160 0 L200 200 L40 200 Z" fill="url(#domeG)"
              stroke="rgba(180,120,80,0.35)" strokeWidth="2" />

        {/* Capsules inside */}
        <g>
          {[
            { x: 70, y: 100, lvl: 1, r: -10 },
            { x: 120, y: 80, lvl: 5, r: 14 },
            { x: 170, y: 110, lvl: 3, r: -4 },
            { x: 95, y: 150, lvl: 2, r: 18 },
            { x: 150, y: 160, lvl: 4, r: -22 },
            { x: 60, y: 170, lvl: 4, r: 6 },
            { x: 185, y: 165, lvl: 1, r: 30 },
            { x: 130, y: 180, lvl: 5, r: -16 },
          ].map((c, i) => (
            <Capsule key={i} x={c.x} y={c.y} level={c.lvl} rotate={c.r} size={28} />
          ))}
        </g>

        {/* Shine */}
        <path d="M40 40 q80 -50 160 0 L200 200 L40 200 Z" fill="url(#domeShine)" />

        {/* Base */}
        <rect x="32" y="200" width="176" height="120" rx="8" fill="url(#baseG)" />
        <rect x="32" y="200" width="176" height="8" fill="rgba(0,0,0,0.18)" />

        {/* Coin slot */}
        <rect x="60" y="222" width="42" height="6" rx="2" fill="rgba(0,0,0,0.4)" />
        {/* Label plate */}
        <rect x="60" y="240" width="120" height="40" rx="6" fill="#FFF" opacity="0.92" />
        <text x="120" y="265" textAnchor="middle" fontFamily="Pretendard, sans-serif"
              fontWeight="700" fontSize="14" fill="#3E2C23">감정 뽑기</text>

        {/* Capsule chute */}
        <rect x="90" y="288" width="60" height="22" rx="4" fill="rgba(0,0,0,0.25)" />

        {/* Stand */}
        <rect x="48" y="320" width="144" height="14" rx="3" fill="#9A4628" />
        <rect x="60" y="334" width="120" height="10" rx="2" fill="rgba(0,0,0,0.25)" />
      </svg>

      {/* Lever — overlay so we can rotate it */}
      <div style={{
        position: 'absolute', right: 4, top: 222,
        width: 40, height: 60,
        transformOrigin: '8px 16px',
        transform: state === 'before' ? 'rotate(0deg)' : 'rotate(-65deg)',
        transition: 'transform 600ms cubic-bezier(.5,1.4,.5,1)',
      }}>
        <svg width="40" height="60" viewBox="0 0 40 60">
          <rect x="2" y="8" width="6" height="32" rx="3" fill="#5C3422" />
          <circle cx="5" cy="14" r="10" fill="#FFD97D"
                  stroke="#8C5E1F" strokeWidth="2" />
          <circle cx="3" cy="11" r="3" fill="#FFEDB5" />
        </svg>
      </div>

      {/* Dropped capsule */}
      {state === 'after' && (
        <div style={{
          position: 'absolute', left: '50%', bottom: -8, transform: 'translateX(-50%)',
        }}>
          <OpenedCapsule level={resultLevel} />
        </div>
      )}
    </div>
  );
}

function Capsule({ x, y, level, rotate = 0, size = 32 }) {
  const e = EMO[level];
  return (
    <g transform={`translate(${x} ${y}) rotate(${rotate})`}>
      <ellipse cx="0" cy="0" rx={size * 0.5} ry={size * 0.36} fill="#fff" stroke="rgba(0,0,0,0.1)" />
      <path d={`M${-size * 0.5} 0 A${size * 0.5} ${size * 0.36} 0 0 1 ${size * 0.5} 0 Z`} fill={e.color} />
      <circle cx={-size * 0.18} cy={-size * 0.18} r={size * 0.08} fill="rgba(255,255,255,0.6)" />
    </g>
  );
}

function OpenedCapsule({ level }) {
  const e = EMO[level];
  return (
    <div style={{ position: 'relative', width: 130, height: 90 }}>
      {/* top half (flipped open) */}
      <svg width="60" height="44" viewBox="0 0 60 44" style={{
        position: 'absolute', left: -4, top: -8, transform: 'rotate(-30deg)',
      }}>
        <path d="M2 42 A28 28 0 0 1 58 42 Z" fill={e.color} stroke="rgba(0,0,0,0.1)" />
      </svg>
      {/* bottom half holding face */}
      <div style={{
        position: 'absolute', left: 22, top: 8,
        width: 86, height: 50, borderRadius: '0 0 50px 50px',
        background: '#fff', boxShadow: '0 4px 10px rgba(0,0,0,0.12)',
        border: '1px solid rgba(0,0,0,0.06)',
      }} />
      {/* face popping up */}
      <div style={{
        position: 'absolute', left: 35, top: -14,
      }}>
        <Face level={level} size={60} accent />
      </div>
      {/* sparkle */}
      <svg width="20" height="20" style={{ position: 'absolute', right: 4, top: -10 }}>
        <path d="M10 2 L11.5 8.5 L18 10 L11.5 11.5 L10 18 L8.5 11.5 L2 10 L8.5 8.5 Z"
              fill="#FFD97D" stroke="#E07856" strokeWidth="0.8" />
      </svg>
    </div>
  );
}

Object.assign(window, { DialWidget, ReelWidget, GachaMachine });
