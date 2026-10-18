# Rules

- **Discuss before every phase.** Go through the phase's "Discuss before starting" questions together. Write the answers into `docs/phases.md`, then start.
- **Recommend first.** Give one recommended option and say why, then mention the alternatives briefly.
- **Explain new tools.** Before using a new library or concept, explain in 2–3 simple sentences what it is and why we need it.
- **Simple English.** The user is B1–B2. Add Turkish for hard or technical words, e.g. *dependency (bağımlılık)*.
- **Small steps.** After each step, write a **step report in the chat text** (never only in the short next-step box). It always has these parts, like in the Phase 2 chat:
  1. **First line:** the last commit (hash) and which step is built now. Say if it builds, if the unit tests pass, and that it is **not tested in the game yet**.
  2. **What changed:** short bullets with the exact rules and numbers the player will notice (what happens, when it stops, messages, sneak, creative mode).
  3. **Files:** the changed files and what changed in each one.
  4. **How to test:** the `./gradlew runClient` command in its own code block, the game mode and the `/give` commands, then **numbered test steps**, each with what the user should see.
  5. **Next:** "When step N works, I'll commit it and we start step N+1: ...".
- **Discussions are written in the chat too.** All questions with the recommendation and the alternatives must be in the chat text, so the user can read them before answering.
- **Unclear answer → ask back.** If an answer could mean two things (e.g. "we try first": the recommendation or the alternative?), ask a short question before building. Don't guess.
- **Only the user says a test passed.** Answer buttons never say "I tested it" for the user. Mark an item "tested in-game" in `docs/items.md` only after the user really tested it.
- **Commit after every step.** When a step builds and the user's test is OK, make a small local git commit, e.g. `Phase 2 step 3: wet concrete curing`. Small commits make it easy to undo (geri almak) one mistake without losing a whole phase. Pushing to GitHub still happens only when the user asks.
- **Ask before big actions:** installing many packages, deleting or moving files, changing the architecture or data model, git push, anything that is hard to undo.
- **$0 budget.** No paid APIs or services. If something might cost money later, warn first.
- **The user is a student.** Suggest better ideas or alternative approaches when you see them, and teach while building.
- **Document every item.** When we add or change an item, tool or block, update `docs/items.md` **in the same step**. Record:
  - the names (EN + TR) and the ID
  - what it does and its exact rules
  - the recipe
  - the stats (durability, damage, speed, armor...)
  - repair and enchanting
  - tags
  - the look
  - the code files
  - the test status
- **No extra mods.** Only Fabric API (if agreed in Phase 0). Do not add other mods as dependencies.
