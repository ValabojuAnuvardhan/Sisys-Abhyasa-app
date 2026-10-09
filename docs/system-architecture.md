# ŚIṢYA ABHYĀSA — System Architecture Specification

## 1. Overview & Architectural Principles

Śiṣya Abhyāsa is designed as an offline-first, evidence-centric engineering readiness platform adhering to Clean MVVM Architecture on modern Android (Jetpack Compose).

### Fundamental Principles
1. **Learn it → Build it → Prove it → Show it → Opportunities**: Every concept learned directly connects to a real task in an active software project.
2. **Evidence Over Assertion**: Self-declared skills or passive course completion do not indicate competency. Competency requires verified evidence: code references, pull requests, automated checks, camera prototype evidence, or defended viva arguments.
3. **Socratic AI Tutoring**: AI companions (ŚiṣyaChat and AbhyāsBot) do not merely provide boilerplate answers; they probe reasoning, evaluate architectural trade-offs, and conduct viva defenses.

---

## 2. Layered Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                       Presentation Layer                    │
│  - Jetpack Compose Screens (Dashboard, Learn, Projects,    │
│    Workspace, Collaborate, Prove, Career, Viva, Chat)       │
│  - Recharts / D3-Inspired Custom Visualizations             │
│  - Material 3 Design System & Responsive Tokens             │
└──────────────────────────────┬──────────────────────────────┘
                               │ Observes UI State / Flows
┌──────────────────────────────▼──────────────────────────────┐
│                        ViewModel Layer                      │
│  - SisyaViewModel: Coordinates cross-phase state & Room DB │
│  - ChatViewModel: Manages conversation stream & AI prompt   │
└──────────────────────────────┬──────────────────────────────┘
                               │ Calls Repositories / Services
┌──────────────────────────────▼──────────────────────────────┐
│                    Data & Persistence Layer                 │
│  - ProjectRepository: Flow-driven local Room DB operations  │
│  - Room Database: ProjectDao, PhotoEvidenceDao              │
│  - MockDataSource: Curated catalog of roadmaps & problems   │
│  - GeminiApiClient: Google Gemini server-side AI integration│
└─────────────────────────────────────────────────────────────┘
```

---

## 3. The Six Product Pillars

### 1. LEARN
- Curated career roadmaps (Backend Systems, DevOps, Distributed Computing).
- Practice sandbox with difficulty tiers and algorithmic validation.
- Skill tracks mapping concepts to real engineering challenges.

### 2. BUILD
- Multi-project management stored in local Room database.
- Milestones with acceptance criteria and branch/PR tracking.
- Task workspace with checklist completion and camera evidence capture.
- AI System Architect generating component breakdowns, data models, and API boundaries.

### 3. COLLABORATE
- Squad member roster with role attributions.
- Incoming join requests with student pitches and one-click acceptance.
- Open-source community project catalog for multi-student contributions.

### 4. PROVE
- Immutable proof ledger connecting photo evidence to specific project tasks.
- Micro-Viva Defense: Interactive, multi-turn AI interrogation challenging engineering decisions.
- Exportable Proof Bundle with shareable verification tokens.

### 5. CAREER
- Role requirement comparison (e.g., Backend Systems Engineer).
- Automated gap analysis highlighting missing evidence.
- D3/Recharts-inspired visual metrics: Animated Donut Chart, Horizontal Bar Chart, Multi-Axis Radar Chart, and Velocity Sparkline.

### 6. DASHBOARD & PROFILE
- Unified loop tracker showing progression through Learn, Build, Collaborate, and Prove.
- Quick navigation chips across all platform submodules.
