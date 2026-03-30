/**
 * Seed script for seasonal events test data.
 *
 * Usage:
 *   node scripts/seed-seasonal-events.js              # seeds emulator (localhost:8080)
 *   node scripts/seed-seasonal-events.js --production  # seeds production Firestore (requires auth)
 */

const { initializeApp, cert } = require("firebase-admin/app");
const { getFirestore } = require("firebase-admin/firestore");

const isProduction = process.argv.includes("--production");

if (isProduction) {
  initializeApp({ projectId: "sudoku-plus-a70c9" });
} else {
  process.env.FIRESTORE_EMULATOR_HOST = "localhost:8080";
  initializeApp({ projectId: "sudoku-plus-a70c9" });
}

const db = getFirestore();
const COLLECTION = "seasonal_events";

function daysFromNow(days) {
  const d = new Date();
  d.setDate(d.getDate() + days);
  return d.toISOString().split("T")[0]; // YYYY-MM-DD
}

const events = [
  // Active event — currently running
  {
    id: "easter_2026",
    data: {
      title: "Easter Challenge",
      description:
        "Celebrate spring with egg-themed puzzles and exclusive Easter rewards!",
      eventType: "easter",
      startDate: daysFromNow(-3),
      endDate: daysFromNow(7),
      theme: {
        primaryColor: 0xff8bc34a,
        secondaryColor: 0xffffeb3b,
        backgroundColor: 0xfff1f8e9,
        accentColor: 0xff689f38,
      },
      challenges: [
        { day: 1, difficulty: "easy", xpMultiplier: 2.0 },
        { day: 2, difficulty: "moderate", xpMultiplier: 2.0 },
        { day: 3, difficulty: "hard", xpMultiplier: 2.0 },
        { day: 4, difficulty: "easy", xpMultiplier: 2.0 },
        { day: 5, difficulty: "moderate", xpMultiplier: 2.0 },
        { day: 6, difficulty: "hard", xpMultiplier: 2.0 },
        { day: 7, difficulty: "challenge", xpMultiplier: 2.5 },
        { day: 8, difficulty: "easy", xpMultiplier: 2.0 },
        { day: 9, difficulty: "moderate", xpMultiplier: 2.0 },
        { day: 10, difficulty: "challenge", xpMultiplier: 3.0 },
      ],
      rewards: [
        { type: "hints", amount: 3 },
        { type: "xp_boost", amount: 2 },
        { type: "event_badge", amount: 1 },
      ],
      badgeId: "event_easter",
    },
  },

  // Upcoming event — starts in the future
  {
    id: "summer_2026",
    data: {
      title: "Summer Sizzler",
      description:
        "Beat the heat with refreshing summer puzzles and cool rewards!",
      eventType: "summer",
      startDate: daysFromNow(14),
      endDate: daysFromNow(28),
      theme: {
        primaryColor: 0xffff9800,
        secondaryColor: 0xff03a9f4,
        backgroundColor: 0xfffff3e0,
        accentColor: 0xffe65100,
      },
      challenges: Array.from({ length: 14 }, (_, i) => ({
        day: i + 1,
        difficulty: ["easy", "moderate", "hard", "challenge"][i % 4],
        xpMultiplier: 2.0,
      })),
      rewards: [
        { type: "hints", amount: 5 },
        { type: "xp_boost", amount: 3 },
        { type: "event_badge", amount: 1 },
      ],
      badgeId: "event_summer",
    },
  },

  // Ended event — already finished
  {
    id: "new_year_2026",
    data: {
      title: "New Year Countdown",
      description:
        "Ring in the new year with special celebration puzzles!",
      eventType: "new_year",
      startDate: daysFromNow(-30),
      endDate: daysFromNow(-20),
      theme: {
        primaryColor: 0xff1565c0,
        secondaryColor: 0xffffd600,
        backgroundColor: 0xffe3f2fd,
        accentColor: 0xff0d47a1,
      },
      challenges: Array.from({ length: 10 }, (_, i) => ({
        day: i + 1,
        difficulty: ["easy", "moderate", "hard", "challenge"][i % 4],
        xpMultiplier: 2.0,
      })),
      rewards: [
        { type: "hints", amount: 3 },
        { type: "event_badge", amount: 1 },
      ],
      badgeId: "event_new_year",
    },
  },
];

async function seed() {
  console.log(
    `Seeding ${events.length} seasonal events to ${isProduction ? "PRODUCTION" : "emulator"}...`
  );

  for (const event of events) {
    await db.collection(COLLECTION).doc(event.id).set(event.data);
    console.log(`  ✓ ${event.id} (${event.data.title})`);
  }

  console.log("\nDone! Events seeded:");
  console.log(`  Active:   easter_2026 (${daysFromNow(-3)} → ${daysFromNow(7)})`);
  console.log(`  Upcoming: summer_2026 (${daysFromNow(14)} → ${daysFromNow(28)})`);
  console.log(`  Ended:    new_year_2026 (${daysFromNow(-30)} → ${daysFromNow(-20)})`);

  if (!isProduction) {
    console.log("\nEmulator UI: http://localhost:4000");
    console.log("Firestore:   http://localhost:4000/firestore");
  }
}

seed()
  .then(() => process.exit(0))
  .catch((e) => {
    console.error(e);
    process.exit(1);
  });
