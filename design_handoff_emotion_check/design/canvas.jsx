// canvas.jsx — Compose all screens into a DesignCanvas

function App() {
  const W = 360, H = 800;
  return (
    <DesignCanvas>
      <DCSection id="record" title="감정 체크 (Record)" subtitle="홈에서 진입 시 닫기 버튼. 위젯은 매 진입마다 랜덤 1개.">
        <DCArtboard id="dial-before" label="A · 다이얼 — 선택 전" width={W} height={H}>
          <RecordDialBefore />
        </DCArtboard>
        <DCArtboard id="dial-after" label="A · 다이얼 — 선택 후" width={W} height={H}>
          <RecordDialAfter />
        </DCArtboard>
        <DCArtboard id="reel" label="B · 릴 — 모션 잔상" width={W} height={H}>
          <RecordReel />
        </DCArtboard>
        <DCArtboard id="gacha-before" label="C · 뽑기 — 뽑기 전" width={W} height={H}>
          <RecordGachaBefore />
        </DCArtboard>
        <DCArtboard id="gacha-after" label="C · 뽑기 — 결과 공개" width={W} height={H}>
          <RecordGachaAfter />
        </DCArtboard>
      </DCSection>

      <DCSection id="home" title="홈 (Home)" subtitle="오늘 기록만 표시. 최신 카드만 수정 가능.">
        <DCArtboard id="home-empty" label="빈 상태" width={W} height={H}>
          <HomeEmpty />
        </DCArtboard>
        <DCArtboard id="home-filled" label="기록 있음" width={W} height={H}>
          <HomeFilled />
        </DCArtboard>
        <DCArtboard id="home-sheet" label="메모 편집 바텀시트" width={W} height={H}>
          <HomeBottomSheet />
        </DCArtboard>
      </DCSection>

      <DCSection id="graph" title="감정 그래프 (Graph)" subtitle="전체 → 주간 → 일별로 좁혀가며 탐색.">
        <DCArtboard id="graph-normal" label="정상 — 4주 · 3주차/금 선택" width={W} height={H}>
          <GraphNormal />
        </DCArtboard>
        <DCArtboard id="graph-empty" label="빈 상태 — 1주차만" width={W} height={H}>
          <GraphEmpty />
        </DCArtboard>
      </DCSection>

      <DCSection id="ds" title="디자인 시스템" subtitle="컬러 / 타이포 / 컴포넌트 레퍼런스">
        <DCArtboard id="ds-sheet" label="컬러 · 타이포 · 컴포넌트" width={720} height={920}>
          <DesignSystemSheet />
        </DCArtboard>
      </DCSection>

      <DCPostIt top={-60} left={20} width={260} rotate={-2}>
        <b>디자이너 코멘트</b><br/>
        — 얼굴: 둥근 형태 + 손그림 느낌 (눈/입만 변화)<br/>
        — 그라데이션: CTA + 그래프 영역에만<br/>
        — 곡선: catmull-rom 부드러운 라인<br/>
        — 톤: 따뜻한 크림, 코랄 강조
      </DCPostIt>
    </DesignCanvas>
  );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);
