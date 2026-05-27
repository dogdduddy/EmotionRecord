// screens.jsx — All 10 emotion-check app screens
// Each screen is 360 x 800 (Android logical dp). Status bar / gesture pill
// included so the screen reads as a real phone surface.

// ─── Chrome bits ────────────────────────────────────────────────────
function StatusBar() {
  return (
    <div className="status-bar">
      <span className="num">9:30</span>
      <div className="right">
        <svg width="14" height="14" viewBox="0 0 16 16"><path d="M8 13.3 L.7 6 a10 10 0 0 1 14.6 0 z" fill="currentColor"/></svg>
        <svg width="14" height="14" viewBox="0 0 16 16"><path d="M14.7 14.7 V1.3 L1.3 14.7 z" fill="currentColor"/></svg>
        <svg width="20" height="14" viewBox="0 0 24 16">
          <rect x="1" y="3" width="20" height="10" rx="2.5" fill="none" stroke="currentColor" strokeWidth="1.4"/>
          <rect x="3" y="5" width="14" height="6" rx="1" fill="currentColor"/>
          <rect x="22" y="6" width="1.6" height="4" rx="0.6" fill="currentColor"/>
        </svg>
      </div>
    </div>
  );
}
function NavBar() {
  return <div className="nav-bar"><div className="pill"/></div>;
}

// Screen wrapper: 360 x 800 standard Android viewport
function Screen({ children, bg, label }) {
  return (
    <div className="app" data-screen-label={label} style={{
      width: 360, height: 800, display: 'flex', flexDirection: 'column',
      background: bg || 'var(--bg)', overflow: 'hidden',
      position: 'relative',
    }}>
      <StatusBar />
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minHeight: 0 }}>
        {children}
      </div>
      <NavBar />
    </div>
  );
}

// ─── 1. Record — Dial (before selection) ────────────────────────────
function RecordDialBefore() {
  return (
    <Screen label="01 Record — Dial (before)">
      <div className="topbar">
        <button className="icon-btn" aria-label="close">
          <svg width="22" height="22" viewBox="0 0 24 24"><path d="M6 6 L18 18 M18 6 L6 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/></svg>
        </button>
        <div className="title" />
      </div>
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'space-between', padding: '12px 24px 28px' }}>
        <div style={{ textAlign: 'center', marginTop: 4 }}>
          <p style={{ margin: 0, fontSize: 22, fontWeight: 700, color: 'var(--text-1)', letterSpacing: '-0.02em' }}>
            지금 기분은<br/>어떠세요?
          </p>
          <p style={{ margin: '10px 0 0', fontSize: 13, color: 'var(--text-2)' }}>
            다이얼을 돌려 감정을 골라보세요
          </p>
        </div>

        <div style={{ display: 'flex', justifyContent: 'center', margin: '20px 0' }}>
          <DialWidget selected={null} size={280} />
        </div>

        <button className="btn-primary" disabled>기록하기</button>
      </div>
    </Screen>
  );
}

// ─── 2. Record — Dial (after selection: 매우 좋음) ───────────────────
function RecordDialAfter() {
  const sel = 5;
  return (
    <Screen label="02 Record — Dial (selected)">
      <div className="topbar">
        <button className="icon-btn" aria-label="close">
          <svg width="22" height="22" viewBox="0 0 24 24"><path d="M6 6 L18 18 M18 6 L6 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/></svg>
        </button>
        <div className="title" />
      </div>
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'space-between', padding: '12px 24px 28px' }}>
        <div style={{ textAlign: 'center', marginTop: 4 }}>
          <p style={{ margin: 0, fontSize: 22, fontWeight: 700, color: 'var(--text-1)', letterSpacing: '-0.02em' }}>
            <span style={{ color: EMO[sel].color }}>{EMO[sel].name}</span>으로<br/>맞추셨어요
          </p>
          <p style={{ margin: '10px 0 0', fontSize: 13, color: 'var(--text-2)' }}>
            확실하면 아래 버튼을 눌러주세요
          </p>
        </div>

        <div style={{ display: 'flex', justifyContent: 'center', margin: '20px 0' }}>
          <DialWidget selected={sel} size={280} />
        </div>

        <button className="btn-primary">기록하기</button>
      </div>
    </Screen>
  );
}

// ─── 3. Record — Reel (after selection w/ motion trail) ─────────────
function RecordReel() {
  const sel = 4;
  return (
    <Screen label="03 Record — Reel">
      <div className="topbar">
        <button className="icon-btn" aria-label="close">
          <svg width="22" height="22" viewBox="0 0 24 24"><path d="M6 6 L18 18 M18 6 L6 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/></svg>
        </button>
        <div className="title" />
      </div>
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'space-between', padding: '12px 24px 28px' }}>
        <div style={{ textAlign: 'center', marginTop: 4 }}>
          <p style={{ margin: 0, fontSize: 22, fontWeight: 700, color: 'var(--text-1)', letterSpacing: '-0.02em' }}>
            위아래로 굴려서<br/>골라보세요
          </p>
        </div>

        <div style={{ display: 'flex', justifyContent: 'center', margin: '8px 0' }}>
          <ReelWidget center={sel} motion />
        </div>

        <div style={{ textAlign: 'center', fontSize: 13, color: 'var(--text-2)', marginBottom: 4 }}>
          <span style={{
            display: 'inline-block', padding: '4px 12px', borderRadius: 999,
            background: EMO[sel].tint, color: EMO[sel].ink, fontWeight: 600,
          }}>
            {sel}. {EMO[sel].name}
          </span>
        </div>

        <button className="btn-primary">기록하기</button>
      </div>
    </Screen>
  );
}

// ─── 4. Record — Gacha (before) ─────────────────────────────────────
function RecordGachaBefore() {
  return (
    <Screen label="04 Record — Gacha (before)">
      <div className="topbar">
        <button className="icon-btn" aria-label="close">
          <svg width="22" height="22" viewBox="0 0 24 24"><path d="M6 6 L18 18 M18 6 L6 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/></svg>
        </button>
        <div className="title" />
      </div>
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'space-between', padding: '12px 16px 28px' }}>
        <div style={{ textAlign: 'center', marginTop: 4 }}>
          <p style={{ margin: 0, fontSize: 22, fontWeight: 700, color: 'var(--text-1)', letterSpacing: '-0.02em' }}>
            오늘의 감정을<br/>뽑아볼까요?
          </p>
          <p style={{ margin: '10px 0 0', fontSize: 13, color: 'var(--text-2)' }}>
            레버를 당겨주세요
          </p>
        </div>

        <div style={{ display: 'flex', justifyContent: 'center' }}>
          <GachaMachine state="before" />
        </div>

        <button className="btn-primary" disabled>기록하기</button>
      </div>
    </Screen>
  );
}

// ─── 5. Record — Gacha (after, result revealed) ─────────────────────
function RecordGachaAfter() {
  const sel = 2;
  return (
    <Screen label="05 Record — Gacha (after)">
      <div className="topbar">
        <button className="icon-btn" aria-label="close">
          <svg width="22" height="22" viewBox="0 0 24 24"><path d="M6 6 L18 18 M18 6 L6 18" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/></svg>
        </button>
        <div className="title" />
      </div>
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'space-between', padding: '12px 16px 28px' }}>
        <div style={{ textAlign: 'center', marginTop: 4 }}>
          <p style={{ margin: 0, fontSize: 22, fontWeight: 700, color: 'var(--text-1)', letterSpacing: '-0.02em' }}>
            <span style={{ color: EMO[sel].color }}>{EMO[sel].name}</span>이<br/>나왔어요
          </p>
          <button className="btn-text" style={{ marginTop: 6, color: EMO[sel].color, fontWeight: 600 }}>
            ↻ 다시 뽑기
          </button>
        </div>

        <div style={{ display: 'flex', justifyContent: 'center', position: 'relative' }}>
          <GachaMachine state="after" resultLevel={sel} />
        </div>

        <button className="btn-primary">기록하기</button>
      </div>
    </Screen>
  );
}

// ─── 6. Home — Empty ────────────────────────────────────────────────
function HomeEmpty() {
  return (
    <Screen label="06 Home — Empty">
      <div className="topbar" style={{ padding: '0 8px 0 20px' }}>
        <div className="title" style={{ fontSize: 22, fontWeight: 700 }}>감정 체크</div>
        <GraphIconButton />
      </div>

      <div style={{ padding: '8px 20px 0', display: 'flex', flexDirection: 'column', gap: 28, flex: 1 }}>
        <BigRecordButton />

        <div>
          <p className="section-header">오늘의 기록</p>
          <EmptyState />
        </div>
      </div>
    </Screen>
  );
}

// Small icon-only graph navigation button (chart line icon)
function GraphIconButton() {
  return (
    <button aria-label="감정 그래프" style={{
      width: 44, height: 44, display: 'grid', placeItems: 'center',
      background: 'transparent', border: 0, borderRadius: '50%',
      color: 'var(--text-1)', cursor: 'pointer',
    }}>
      <svg width="22" height="22" viewBox="0 0 24 24" fill="currentColor">
        <rect x="4"  y="13" width="4" height="8"  rx="1.2" />
        <rect x="10" y="5"  width="4" height="16" rx="1.2" />
        <rect x="16" y="10" width="4" height="11" rx="1.2" />
      </svg>
    </button>
  );
}

function BigRecordButton({ pressed = false }) {
  return (
    <div style={{
      position: 'relative',
      borderRadius: 24,
      padding: '24px 22px',
      background: 'linear-gradient(135deg, #FFD0A6 0%, #FF9B7A 45%, #FF8A65 100%)',
      boxShadow: '0 12px 28px rgba(255, 138, 101, 0.32), 0 2px 6px rgba(224,120,86,0.18)',
      overflow: 'hidden',
      transform: pressed ? 'scale(0.985)' : 'none',
      transition: 'transform .15s',
    }}>
      {/* decorative pastel circles */}
      <div style={{
        position: 'absolute', right: -18, top: -22, width: 110, height: 110, borderRadius: '50%',
        background: 'rgba(255, 255, 255, 0.18)',
      }} />
      <div style={{
        position: 'absolute', right: 36, bottom: -20, width: 60, height: 60, borderRadius: '50%',
        background: 'rgba(255, 255, 255, 0.22)',
      }} />

      <div style={{ position: 'relative', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div>
          <p style={{
            margin: 0, fontSize: 22, fontWeight: 800,
            color: '#fff', letterSpacing: '-0.02em', textShadow: '0 1px 2px rgba(180,80,40,0.18)',
          }}>감정 기록하기</p>
          <div style={{
            marginTop: 12, display: 'inline-flex', gap: 4, padding: '6px 10px',
            background: 'rgba(255,255,255,0.28)', borderRadius: 999, alignItems: 'center',
          }}>
            <Face level={4} size={18} />
            <span style={{ color: '#fff', fontSize: 11, fontWeight: 600 }}>지금 30초만</span>
          </div>
        </div>
        <div style={{
          width: 52, height: 52, borderRadius: '50%',
          background: '#fff',
          display: 'grid', placeItems: 'center',
          boxShadow: '0 4px 10px rgba(0,0,0,0.12)',
        }}>
          <svg width="20" height="20" viewBox="0 0 24 24"><path d="M5 12 H19 M13 5 L20 12 L13 19" stroke="#FF8A65" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round" fill="none"/></svg>
        </div>
      </div>
    </div>
  );
}

function EmptyState() {
  return (
    <div style={{
      borderRadius: 20, padding: '32px 20px',
      background: 'var(--surface-soft)', border: '1px dashed var(--line-strong)',
      display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 12,
    }}>
      {/* friendly empty illustration */}
      <div style={{ position: 'relative', width: 88, height: 64 }}>
        <div style={{
          position: 'absolute', left: 14, bottom: 0,
          width: 60, height: 30, borderRadius: '0 0 30px 30px',
          background: '#FFE3D4', border: '1.5px solid #FFB28A',
        }} />
        <div style={{ position: 'absolute', left: 24, top: 6 }}>
          <Face level={3} size={40} />
        </div>
        <svg width="14" height="14" viewBox="0 0 14 14" style={{ position: 'absolute', left: -6, top: 14 }}>
          <path d="M7 1 L8 6 L13 7 L8 8 L7 13 L6 8 L1 7 L6 6 Z" fill="#FFB28A"/>
        </svg>
      </div>
      <p style={{ margin: 0, fontSize: 14, color: 'var(--text-2)', textAlign: 'center', lineHeight: 1.5 }}>
        아직 오늘의 기록이 없어요<br/>
        <span style={{ color: 'var(--text-1)', fontWeight: 600 }}>위 버튼을 눌러 시작해보세요</span>
      </p>
    </div>
  );
}

// ─── 7. Home — With records ─────────────────────────────────────────
const RECORDS_TODAY = [
  { level: 4, time: '오후 3:20', memo: '점심 먹고 산책 다녀옴. 햇살이 좋아서 기분이 풀렸다.', editable: true },
  { level: 3, time: '오후 1:10', memo: '회의가 길어졌다. 졸린데 커피 한 잔.', editable: false },
  { level: 2, time: '오전 9:45', memo: '출근길에 지하철을 놓침.', editable: false },
];

function HomeFilled() {
  return (
    <Screen label="07 Home — With records">
      <div className="topbar" style={{ padding: '0 8px 0 20px' }}>
        <div className="title" style={{ fontSize: 22, fontWeight: 700 }}>감정 체크</div>
        <GraphIconButton />
      </div>

      <div style={{ padding: '8px 20px 16px', display: 'flex', flexDirection: 'column', gap: 24, flex: 1, overflow: 'hidden' }}>
        <BigRecordButton />

        <div style={{ minHeight: 0 }}>
          <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 12 }}>
            <p className="section-header" style={{ margin: 0 }}>오늘의 기록</p>
            <span style={{ fontSize: 12, color: 'var(--text-3)' }} className="num">6월 13일 · 금요일</span>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            {RECORDS_TODAY.map((r, i) => <RecordCard key={i} {...r} />)}
          </div>
        </div>
      </div>
    </Screen>
  );
}

function RecordCard({ level, time, memo, editable, dimmed = false }) {
  return (
    <div className={`card${editable ? ' editable' : ''}`} style={{
      display: 'flex', gap: 12, padding: 14,
      opacity: dimmed ? 0.85 : 1,
    }}>
      <FaceChip level={level} size={44} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between' }}>
          <span style={{ fontSize: 15, fontWeight: 700, color: EMO[level].ink }}>{EMO[level].name}</span>
          <span className="num" style={{ fontSize: 12, color: 'var(--text-3)' }}>{time}</span>
        </div>
        <p style={{
          margin: '4px 0 0', fontSize: 13, lineHeight: 1.5, color: 'var(--text-2)',
          overflow: 'hidden', textOverflow: 'ellipsis',
          display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical',
        }}>{memo}</p>
      </div>
      {editable && (
        <button aria-label="메모 편집" style={{
          width: 24, height: 24, padding: 0, alignSelf: 'flex-start',
          background: 'transparent', border: 0, cursor: 'pointer',
          display: 'grid', placeItems: 'center',
        }}>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <path d="M4 20 L4 16 L16 4 L20 8 L8 20 Z" stroke="var(--primary-deep)" strokeWidth="1.8" strokeLinejoin="round"/>
            <path d="M14 6 L18 10" stroke="var(--primary-deep)" strokeWidth="1.8" strokeLinecap="round"/>
          </svg>
        </button>
      )}
    </div>
  );
}

// ─── 8. Home — Bottom sheet (memo edit) ─────────────────────────────
function HomeBottomSheet() {
  return (
    <div style={{ position: 'relative', width: 360, height: 800 }}>
      {/* underlay: dimmed home */}
      <div style={{ position: 'absolute', inset: 0, filter: 'blur(0.5px) brightness(0.92)' }}>
        <HomeFilled />
      </div>
      <div style={{ position: 'absolute', inset: 0, background: 'rgba(20, 12, 8, 0.45)' }} />

      <div data-screen-label="08 Home — Memo bottom sheet" style={{
        position: 'absolute', left: 0, right: 0, bottom: 0,
        background: 'var(--bg)', borderRadius: '24px 24px 0 0',
        padding: '14px 20px 28px',
        boxShadow: '0 -8px 24px rgba(0,0,0,0.16)',
        display: 'flex', flexDirection: 'column', gap: 14,
        fontFamily: '"Pretendard", -apple-system, system-ui, sans-serif',
        color: 'var(--text-1)',
      }}>
        {/* handle */}
        <div style={{ width: 40, height: 4, borderRadius: 2, background: 'rgba(62,44,35,0.2)', margin: '0 auto' }} />

        {/* header */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <FaceChip level={4} size={44} />
          <div style={{ flex: 1 }}>
            <p style={{ margin: 0, fontSize: 15, fontWeight: 700, color: EMO[4].ink }}>좋음</p>
            <p className="num" style={{ margin: '2px 0 0', fontSize: 12, color: 'var(--text-3)' }}>오후 3:20</p>
          </div>
          <button className="btn-text" style={{ color: 'var(--text-2)' }}>취소</button>
        </div>

        {/* textarea */}
        <div style={{
          background: 'var(--surface)', borderRadius: 14,
          border: '1.5px solid var(--primary)',
          padding: '14px 14px 12px',
          minHeight: 130,
          position: 'relative',
        }}>
          <p style={{ margin: 0, fontSize: 14, lineHeight: 1.55, color: 'var(--text-1)' }}>
            점심 먹고 산책 다녀옴. 햇살이 좋아서 기분이 풀렸다.<span style={{
              display: 'inline-block', width: 1.5, height: 16, background: 'var(--primary-deep)',
              verticalAlign: 'text-bottom', marginLeft: 1,
              animation: 'caret 1s infinite',
            }} />
          </p>
          <div style={{ position: 'absolute', right: 12, bottom: 8, fontSize: 11, color: 'var(--text-3)' }}>
            <span className="num">26</span> / 200
          </div>
        </div>
        <style>{`@keyframes caret { 50% { opacity: 0; } }`}</style>

        <button className="btn-primary" style={{ marginTop: 4 }}>저장</button>
      </div>
    </div>
  );
}

// ─── 9. Graph — normal (4 weeks, week 3 + Fri selected) ─────────────
function GraphNormal() {
  // Overall: 4 weeks averages
  const weeks = [
    { label: '1주', avg: 2.6 },
    { label: '2주', avg: 3.1 },
    { label: '3주', avg: 4.0 },
    { label: '4주', avg: 3.6 },
  ];
  const selectedWeek = 2; // index

  // Week 3 daily averages (Mon..Sun)
  const days = [
    { label: '월', avg: 3.5 },
    { label: '화', avg: 4.2 },
    { label: '수', avg: 3.0 },
    { label: '목', avg: 4.5 },
    { label: '금', avg: 4.0 },
    { label: '토', avg: 4.7 },
    { label: '일', avg: null }, // not yet recorded
  ];
  const selectedDay = 4; // Fri

  return (
    <Screen label="09 Graph — Normal">
      <div className="topbar" style={{ padding: '0 8px' }}>
        <button className="icon-btn" aria-label="back">
          <svg width="22" height="22" viewBox="0 0 24 24"><path d="M15 5 L8 12 L15 19" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round" fill="none"/></svg>
        </button>
        <div className="title">감정 그래프</div>
      </div>
      <div style={{ flex: 1, overflow: 'auto', padding: '4px 18px 20px', display: 'flex', flexDirection: 'column', gap: 18 }}>
        {/* Overall chart */}
        <div>
          <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 8 }}>
            <p className="section-header" style={{ margin: 0 }}>전체 흐름</p>
            <span style={{ fontSize: 11, color: 'var(--text-3)' }}>4주</span>
          </div>
          <div className="card" style={{ padding: '14px 12px' }}>
            <LineChart series={weeks.map(w => w.avg)} labels={weeks.map(w => w.label)} selected={selectedWeek} mode="weeks" />
          </div>
        </div>

        {/* Weekly chart */}
        <div>
          <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 8 }}>
            <p style={{ margin: 0, fontSize: 15, fontWeight: 700, color: 'var(--text-1)' }}>3주차</p>
            <span className="num" style={{ fontSize: 12, color: 'var(--text-2)' }}>6/9 – 6/15</span>
          </div>
          <div className="card" style={{ padding: '14px 12px' }}>
            <LineChart
              series={days.map(d => d.avg)}
              labels={days.map(d => d.label)}
              selected={selectedDay}
              mode="days"
            />
          </div>
        </div>

        {/* Day records */}
        <div>
          <p style={{ margin: '0 0 10px', fontSize: 15, fontWeight: 700, color: 'var(--text-1)' }}>6월 13일 금요일</p>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            <RecordCard level={4} time="오후 3:20" memo="점심 먹고 산책 다녀옴. 햇살이 좋아서 기분이 풀렸다." />
            <RecordCard level={3} time="오전 10:30" memo="회의가 길어졌다. 졸린데 커피 한 잔." />
          </div>
        </div>

        {/* Debug seed */}
        <div style={{ borderTop: '1px solid var(--line)', paddingTop: 14, marginTop: 4 }}>
          <button className="btn-ghost" style={{ width: '100%', height: 38, fontSize: 12, color: 'var(--text-3)' }}>
            데모 데이터 시딩 · DEBUG
          </button>
        </div>
      </div>
    </Screen>
  );
}

// ─── 10. Graph — Empty (just 1 week) ────────────────────────────────
function GraphEmpty() {
  return (
    <Screen label="10 Graph — Empty">
      <div className="topbar" style={{ padding: '0 8px' }}>
        <button className="icon-btn" aria-label="back">
          <svg width="22" height="22" viewBox="0 0 24 24"><path d="M15 5 L8 12 L15 19" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round" fill="none"/></svg>
        </button>
        <div className="title">감정 그래프</div>
      </div>
      <div style={{ flex: 1, padding: '4px 18px 20px', display: 'flex', flexDirection: 'column', gap: 18 }}>
        <div>
          <p className="section-header">전체 흐름</p>
          <div className="card" style={{
            padding: 18, display: 'flex', flexDirection: 'column', alignItems: 'center',
            justifyContent: 'center', minHeight: 200, gap: 12,
            background: 'var(--surface-soft)', border: '1px dashed var(--line-strong)', boxShadow: 'none',
          }}>
            {/* small placeholder line */}
            <svg width="220" height="80" viewBox="0 0 220 80">
              <defs>
                <linearGradient id="dimG" x1="0" x2="0" y1="0" y2="1">
                  <stop offset="0" stopColor="rgba(176,170,163,0.4)"/>
                  <stop offset="1" stopColor="rgba(176,170,163,0)"/>
                </linearGradient>
              </defs>
              <path d="M10 50 Q60 30 110 45 T210 35" fill="none" stroke="rgba(176,170,163,0.5)" strokeWidth="2" strokeDasharray="4 4"/>
              <path d="M10 50 Q60 30 110 45 T210 35 L210 80 L10 80 Z" fill="url(#dimG)"/>
              <circle cx="110" cy="45" r="4" fill="#B0AAA3"/>
            </svg>
            <p style={{ margin: 0, fontSize: 13, color: 'var(--text-2)', textAlign: 'center', lineHeight: 1.5 }}>
              최소 <b style={{ color: 'var(--text-1)' }}>1주 이상</b> 기록하면<br/>
              감정의 추이가 보여요
            </p>
          </div>
        </div>

        <div>
          <p style={{ margin: '0 0 10px', fontSize: 15, fontWeight: 700, color: 'var(--text-1)' }}>1주차</p>
          <div className="card" style={{ padding: '14px 12px' }}>
            <LineChart
              series={[3, 4, null, null, null, null, null]}
              labels={['월', '화', '수', '목', '금', '토', '일']}
              selected={1}
              mode="days"
              partial
            />
          </div>
        </div>

        <div>
          <p style={{ margin: '0 0 10px', fontSize: 15, fontWeight: 700, color: 'var(--text-1)' }}>6월 11일 화요일</p>
          <RecordCard level={4} time="오후 7:40" memo="기록이 처음이라 어색하지만, 시작해본다." />
        </div>

        <div style={{ borderTop: '1px solid var(--line)', paddingTop: 14, marginTop: 'auto' }}>
          <button className="btn-ghost" style={{ width: '100%', height: 38, fontSize: 12, color: 'var(--text-3)' }}>
            데모 데이터 시딩 · DEBUG
          </button>
        </div>
      </div>
    </Screen>
  );
}

// ─── Line chart shared ─────────────────────────────────────────────
function LineChart({ series, labels, selected, mode, partial }) {
  // canvas: 320 x 180, y axis 1..5, x axis n columns
  const W = 320, H = 170;
  const PAD_L = 38, PAD_R = 10, PAD_T = 14, PAD_B = 30;
  const innerW = W - PAD_L - PAD_R;
  const innerH = H - PAD_T - PAD_B;
  const n = series.length;
  const stepX = innerW / (n - 1);
  const yFor = (v) => PAD_T + (1 - (v - 1) / 4) * innerH;
  const xFor = (i) => PAD_L + i * stepX;

  // Path (catmull-rom smoothing) skipping nulls
  const pts = series.map((v, i) => v == null ? null : [xFor(i), yFor(v)]);
  const pathD = smoothPath(pts);
  const areaD = pathD ? `${pathD} L ${xFor(lastDefined(series))} ${H - PAD_B} L ${xFor(firstDefined(series))} ${H - PAD_B} Z` : '';

  return (
    <svg viewBox={`0 0 ${W} ${H}`} width="100%" style={{ display: 'block' }}>
      <defs>
        <linearGradient id="areaG" x1="0" x2="0" y1="0" y2="1">
          <stop offset="0" stopColor="rgba(255,138,101,0.32)"/>
          <stop offset="1" stopColor="rgba(255,138,101,0)"/>
        </linearGradient>
        <linearGradient id="lineG" x1="0" x2="1" y1="0" y2="0">
          <stop offset="0" stopColor="#FF9B7A"/>
          <stop offset="1" stopColor="#E07856"/>
        </linearGradient>
      </defs>

      {/* y axis face icons + gridlines */}
      {[1, 2, 3, 4, 5].map(level => {
        const y = yFor(level);
        return (
          <g key={level}>
            <line x1={PAD_L} x2={W - PAD_R} y1={y} y2={y} stroke="rgba(62,44,35,0.06)" />
            <foreignObject x="2" y={y - 11} width="30" height="22">
              <div xmlns="http://www.w3.org/1999/xhtml" style={{ display: 'grid', placeItems: 'center' }}>
                <Face level={level} size={18} />
              </div>
            </foreignObject>
          </g>
        );
      })}

      {/* selected column highlight band */}
      {selected != null && (
        <rect
          x={xFor(selected) - (stepX * 0.45)}
          y={PAD_T - 6}
          width={stepX * 0.9}
          height={innerH + 12}
          rx="10"
          fill="rgba(255,138,101,0.10)"
        />
      )}

      {/* area + line */}
      {areaD && <path d={areaD} fill="url(#areaG)" />}
      {pathD && <path d={pathD} fill="none" stroke="url(#lineG)" strokeWidth="2.6" strokeLinecap="round" strokeLinejoin="round" />}

      {/* points */}
      {series.map((v, i) => {
        if (v == null) return null;
        const isSel = i === selected;
        const cx = xFor(i), cy = yFor(v);
        return (
          <g key={i}>
            {isSel && (
              <circle cx={cx} cy={cy} r="10" fill="rgba(255,138,101,0.18)" />
            )}
            <circle cx={cx} cy={cy} r={isSel ? 6 : 4} fill="#fff" stroke={isSel ? '#E07856' : '#FF9B7A'} strokeWidth={isSel ? 2.4 : 2} />
            {isSel && (
              <g>
                <rect x={cx - 18} y={cy - 30} width="36" height="20" rx="6" fill="#3E2C23" />
                <text x={cx} y={cy - 16} textAnchor="middle" fontSize="11" fontWeight="700" fill="#fff" fontFamily="Inter">
                  {typeof v === 'number' ? v.toFixed(1) : v}
                </text>
              </g>
            )}
          </g>
        );
      })}

      {/* x axis labels */}
      {labels.map((l, i) => (
        <text key={i} x={xFor(i)} y={H - 8} textAnchor="middle"
              fontSize={i === selected ? 12 : 11}
              fontWeight={i === selected ? 800 : 500}
              fill={i === selected ? '#3E2C23' : 'rgba(125,107,95,0.85)'}
              fontFamily="Pretendard, sans-serif">
          {l}
        </text>
      ))}
    </svg>
  );
}

// Smooth catmull-rom path supporting nulls (breaks into sub-paths)
function smoothPath(pts) {
  const segs = [];
  let cur = [];
  pts.forEach(p => {
    if (p == null) { if (cur.length) { segs.push(cur); cur = []; } }
    else cur.push(p);
  });
  if (cur.length) segs.push(cur);
  return segs.map(seg => {
    if (seg.length === 0) return '';
    if (seg.length === 1) return `M ${seg[0][0]} ${seg[0][1]}`;
    let d = `M ${seg[0][0]} ${seg[0][1]}`;
    for (let i = 0; i < seg.length - 1; i++) {
      const p0 = seg[i - 1] || seg[i];
      const p1 = seg[i];
      const p2 = seg[i + 1];
      const p3 = seg[i + 2] || p2;
      const c1x = p1[0] + (p2[0] - p0[0]) / 6;
      const c1y = p1[1] + (p2[1] - p0[1]) / 6;
      const c2x = p2[0] - (p3[0] - p1[0]) / 6;
      const c2y = p2[1] - (p3[1] - p1[1]) / 6;
      d += ` C ${c1x} ${c1y}, ${c2x} ${c2y}, ${p2[0]} ${p2[1]}`;
    }
    return d;
  }).join(' ');
}
function firstDefined(arr) { return arr.findIndex(v => v != null); }
function lastDefined(arr) { for (let i = arr.length - 1; i >= 0; i--) if (arr[i] != null) return i; return -1; }

// ─── Design system sheet ────────────────────────────────────────────
function DesignSystemSheet() {
  const swatches = [
    { name: 'BG', hex: '#FBF7F2', label: 'Background' },
    { name: 'Surface', hex: '#FFFFFF', label: 'Card surface' },
    { name: 'Sub', hex: '#F5EFE7', label: 'Soft surface' },
    { name: 'Primary', hex: '#FF8A65', label: 'CTA · brand' },
    { name: 'Deep', hex: '#E07856', label: 'Pressed · ink' },
    { name: 'Text 1', hex: '#3E2C23', label: 'Title' },
    { name: 'Text 2', hex: '#7D6B5F', label: 'Body' },
    { name: 'Text 3', hex: '#B5A89C', label: 'Subtle' },
  ];
  return (
    <div className="app" data-screen-label="DS — System sheet" style={{
      width: 720, padding: 28, background: 'var(--bg)', borderRadius: 16,
    }}>
      <p style={{ margin: '0 0 18px', fontSize: 18, fontWeight: 700, color: 'var(--text-1)' }}>
        디자인 시스템 · 컬러 + 타이포
      </p>

      {/* Emotion scale */}
      <p className="section-header" style={{ marginTop: 8 }}>감정 스케일</p>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)', gap: 10 }}>
        {[1, 2, 3, 4, 5].map(n => (
          <div key={n} className="card" style={{ padding: 12, textAlign: 'center' }}>
            <FaceChip level={n} size={56} />
            <p style={{ margin: '8px 0 2px', fontSize: 12, fontWeight: 700, color: EMO[n].ink }}>{n}. {EMO[n].name}</p>
            <p className="num" style={{ margin: 0, fontSize: 10, color: 'var(--text-3)' }}>{EMO[n].color}</p>
          </div>
        ))}
      </div>

      {/* Surface / text */}
      <p className="section-header" style={{ marginTop: 22 }}>팔레트</p>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 10 }}>
        {swatches.map(s => (
          <div key={s.name} className="card" style={{ padding: 10 }}>
            <div style={{
              height: 48, borderRadius: 10, background: s.hex,
              border: '1px solid var(--line)',
            }} />
            <p style={{ margin: '8px 0 0', fontSize: 12, fontWeight: 700, color: 'var(--text-1)' }}>{s.name}</p>
            <p style={{ margin: 0, fontSize: 10, color: 'var(--text-3)' }}>{s.label}</p>
            <p className="num" style={{ margin: '4px 0 0', fontSize: 10, color: 'var(--text-2)' }}>{s.hex}</p>
          </div>
        ))}
      </div>

      {/* Typography */}
      <p className="section-header" style={{ marginTop: 22 }}>타이포그래피 · Pretendard</p>
      <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
        {[
          { sz: 26, w: 800, label: 'Display · 26 / 800', text: '오늘의 감정' },
          { sz: 22, w: 700, label: 'Title · 22 / 700', text: '감정 체크' },
          { sz: 17, w: 700, label: 'Section · 17 / 700', text: '오늘의 기록' },
          { sz: 14, w: 600, label: 'Subhead · 14 / 600', text: '오후 3시 20분 · 좋음' },
          { sz: 13, w: 400, label: 'Body · 13 / 400 · 1.5 leading', text: '점심 먹고 산책 다녀옴. 햇살이 좋아서 기분이 풀렸다.' },
          { sz: 11, w: 500, label: 'Caption · 11 / 500', text: '데모 데이터 시딩 · DEBUG' },
        ].map((r, i) => (
          <div key={i} style={{ display: 'flex', alignItems: 'baseline', gap: 14 }}>
            <span style={{ width: 220, fontSize: 11, color: 'var(--text-3)' }}>{r.label}</span>
            <span style={{ fontSize: r.sz, fontWeight: r.w, color: 'var(--text-1)', letterSpacing: '-0.01em' }}>{r.text}</span>
          </div>
        ))}
      </div>

      {/* Buttons */}
      <p className="section-header" style={{ marginTop: 22 }}>버튼</p>
      <div style={{ display: 'flex', gap: 10, alignItems: 'center', flexWrap: 'wrap' }}>
        <button className="btn-primary" style={{ width: 200 }}>Primary CTA</button>
        <button className="btn-primary" style={{ width: 160 }} disabled>비활성</button>
        <button className="btn-ghost">Secondary</button>
        <button className="btn-text">Text only</button>
      </div>

      {/* Cards */}
      <p className="section-header" style={{ marginTop: 22 }}>카드</p>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        <RecordCard level={4} time="오후 3:20" memo="햇살이 좋았다." editable />
        <RecordCard level={2} time="오전 9:45" memo="지하철을 놓쳤다." />
      </div>
    </div>
  );
}

Object.assign(window, {
  RecordDialBefore, RecordDialAfter, RecordReel,
  RecordGachaBefore, RecordGachaAfter,
  HomeEmpty, HomeFilled, HomeBottomSheet,
  GraphNormal, GraphEmpty, DesignSystemSheet,
});
